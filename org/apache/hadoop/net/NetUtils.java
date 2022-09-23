// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.net;

import java.util.HashMap;
import org.slf4j.LoggerFactory;
import java.net.ServerSocket;
import java.util.Enumeration;
import org.apache.commons.net.util.SubnetUtils;
import java.lang.reflect.Constructor;
import java.io.EOFException;
import java.net.NoRouteToHostException;
import java.net.BindException;
import java.net.SocketException;
import java.net.NetworkInterface;
import java.util.Collection;
import java.nio.channels.SocketChannel;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import com.google.common.base.Preconditions;
import java.net.SocketAddress;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.net.Socket;
import org.apache.hadoop.ipc.Server;
import java.util.Iterator;
import java.util.Set;
import java.util.ArrayList;
import java.util.List;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.net.InetAddress;
import org.apache.hadoop.security.SecurityUtil;
import java.net.URI;
import java.net.InetSocketAddress;
import org.apache.hadoop.util.ReflectionUtils;
import javax.net.SocketFactory;
import org.apache.hadoop.conf.Configuration;
import java.util.regex.Pattern;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.LimitedPrivate({ "HDFS", "MapReduce" })
@InterfaceStability.Unstable
public class NetUtils
{
    private static final Logger LOG;
    private static Map<String, String> hostToResolved;
    private static final String FOR_MORE_DETAILS_SEE = " For more details see:  ";
    public static final String UNKNOWN_HOST = "(unknown)";
    public static final String HADOOP_WIKI = "http://wiki.apache.org/hadoop/";
    private static final ConcurrentHashMap<String, String> canonicalizedHostCache;
    private static final Pattern ipPortPattern;
    
    public static SocketFactory getSocketFactory(final Configuration conf, final Class<?> clazz) {
        SocketFactory factory = null;
        final String propValue = conf.get("hadoop.rpc.socket.factory.class." + clazz.getSimpleName());
        if (propValue != null && propValue.length() > 0) {
            factory = getSocketFactoryFromProperty(conf, propValue);
        }
        if (factory == null) {
            factory = getDefaultSocketFactory(conf);
        }
        return factory;
    }
    
    public static SocketFactory getDefaultSocketFactory(final Configuration conf) {
        final String propValue = conf.get("hadoop.rpc.socket.factory.class.default", "org.apache.hadoop.net.StandardSocketFactory");
        if (propValue == null || propValue.length() == 0) {
            return SocketFactory.getDefault();
        }
        return getSocketFactoryFromProperty(conf, propValue);
    }
    
    public static SocketFactory getSocketFactoryFromProperty(final Configuration conf, final String propValue) {
        try {
            final Class<?> theClass = conf.getClassByName(propValue);
            return ReflectionUtils.newInstance(theClass, conf);
        }
        catch (ClassNotFoundException cnfe) {
            throw new RuntimeException("Socket Factory class not found: " + cnfe);
        }
    }
    
    public static InetSocketAddress createSocketAddr(final String target) {
        return createSocketAddr(target, -1);
    }
    
    public static InetSocketAddress createSocketAddr(final String target, final int defaultPort) {
        return createSocketAddr(target, defaultPort, null);
    }
    
    public static InetSocketAddress createSocketAddr(String target, final int defaultPort, final String configName) {
        String helpText = "";
        if (configName != null) {
            helpText = " (configuration property '" + configName + "')";
        }
        if (target == null) {
            throw new IllegalArgumentException("Target address cannot be null." + helpText);
        }
        target = target.trim();
        final boolean hasScheme = target.contains("://");
        URI uri = null;
        try {
            uri = (hasScheme ? URI.create(target) : URI.create("dummyscheme://" + target));
        }
        catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Does not contain a valid host:port authority: " + target + helpText);
        }
        final String host = uri.getHost();
        int port = uri.getPort();
        if (port == -1) {
            port = defaultPort;
        }
        final String path = uri.getPath();
        if (host == null || port < 0 || (!hasScheme && path != null && !path.isEmpty())) {
            throw new IllegalArgumentException("Does not contain a valid host:port authority: " + target + helpText);
        }
        return createSocketAddrForHost(host, port);
    }
    
    public static InetSocketAddress createSocketAddrForHost(final String host, final int port) {
        final String staticHost = getStaticResolution(host);
        final String resolveHost = (staticHost != null) ? staticHost : host;
        InetSocketAddress addr;
        try {
            InetAddress iaddr = SecurityUtil.getByName(resolveHost);
            if (staticHost != null) {
                iaddr = InetAddress.getByAddress(host, iaddr.getAddress());
            }
            addr = new InetSocketAddress(iaddr, port);
        }
        catch (UnknownHostException e) {
            addr = InetSocketAddress.createUnresolved(host, port);
        }
        return addr;
    }
    
    public static URI getCanonicalUri(URI uri, final int defaultPort) {
        final String host = uri.getHost();
        if (host == null) {
            return uri;
        }
        final String fqHost = canonicalizeHost(host);
        final int port = uri.getPort();
        if (host.equals(fqHost) && port != -1) {
            return uri;
        }
        try {
            uri = new URI(uri.getScheme(), uri.getUserInfo(), fqHost, (port == -1) ? defaultPort : port, uri.getPath(), uri.getQuery(), uri.getFragment());
        }
        catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
        return uri;
    }
    
    private static String canonicalizeHost(final String host) {
        String fqHost = NetUtils.canonicalizedHostCache.get(host);
        if (fqHost == null) {
            try {
                fqHost = SecurityUtil.getByName(host).getHostName();
                NetUtils.canonicalizedHostCache.putIfAbsent(host, fqHost);
            }
            catch (UnknownHostException e) {
                fqHost = host;
            }
        }
        return fqHost;
    }
    
    public static void addStaticResolution(final String host, final String resolvedName) {
        synchronized (NetUtils.hostToResolved) {
            NetUtils.hostToResolved.put(host, resolvedName);
        }
    }
    
    public static String getStaticResolution(final String host) {
        synchronized (NetUtils.hostToResolved) {
            return NetUtils.hostToResolved.get(host);
        }
    }
    
    public static List<String[]> getAllStaticResolutions() {
        synchronized (NetUtils.hostToResolved) {
            final Set<Map.Entry<String, String>> entries = NetUtils.hostToResolved.entrySet();
            if (entries.size() == 0) {
                return null;
            }
            final List<String[]> l = new ArrayList<String[]>(entries.size());
            for (final Map.Entry<String, String> e : entries) {
                l.add(new String[] { e.getKey(), e.getValue() });
            }
            return l;
        }
    }
    
    public static InetSocketAddress getConnectAddress(final Server server) {
        return getConnectAddress(server.getListenerAddress());
    }
    
    public static InetSocketAddress getConnectAddress(InetSocketAddress addr) {
        if (!addr.isUnresolved() && addr.getAddress().isAnyLocalAddress()) {
            try {
                addr = new InetSocketAddress(InetAddress.getLocalHost(), addr.getPort());
            }
            catch (UnknownHostException uhe) {
                addr = createSocketAddrForHost("127.0.0.1", addr.getPort());
            }
        }
        return addr;
    }
    
    public static SocketInputWrapper getInputStream(final Socket socket) throws IOException {
        return getInputStream(socket, socket.getSoTimeout());
    }
    
    public static SocketInputWrapper getInputStream(final Socket socket, final long timeout) throws IOException {
        final InputStream stm = (socket.getChannel() == null) ? socket.getInputStream() : new SocketInputStream(socket);
        final SocketInputWrapper w = new SocketInputWrapper(socket, stm);
        w.setTimeout(timeout);
        return w;
    }
    
    public static OutputStream getOutputStream(final Socket socket) throws IOException {
        return getOutputStream(socket, 0L);
    }
    
    public static OutputStream getOutputStream(final Socket socket, final long timeout) throws IOException {
        return (socket.getChannel() == null) ? socket.getOutputStream() : new SocketOutputStream(socket, timeout);
    }
    
    public static void connect(final Socket socket, final SocketAddress address, final int timeout) throws IOException {
        connect(socket, address, null, timeout);
    }
    
    public static void connect(final Socket socket, final SocketAddress endpoint, final SocketAddress localAddr, final int timeout) throws IOException {
        if (socket == null || endpoint == null || timeout < 0) {
            throw new IllegalArgumentException("Illegal argument for connect()");
        }
        final SocketChannel ch = socket.getChannel();
        if (localAddr != null) {
            final Class localClass = localAddr.getClass();
            final Class remoteClass = endpoint.getClass();
            Preconditions.checkArgument(localClass.equals(remoteClass), "Local address %s must be of same family as remote address %s.", localAddr, endpoint);
            socket.bind(localAddr);
        }
        try {
            if (ch == null) {
                socket.connect(endpoint, timeout);
            }
            else {
                SocketIOWithTimeout.connect(ch, endpoint, timeout);
            }
        }
        catch (SocketTimeoutException ste) {
            throw new ConnectTimeoutException(ste.getMessage());
        }
        if (socket.getLocalPort() == socket.getPort() && socket.getLocalAddress().equals(socket.getInetAddress())) {
            NetUtils.LOG.info("Detected a loopback TCP socket, disconnecting it");
            socket.close();
            throw new ConnectException("Localhost targeted connection resulted in a loopback. No daemon is listening on the target port.");
        }
    }
    
    public static String normalizeHostName(final String name) {
        try {
            return InetAddress.getByName(name).getHostAddress();
        }
        catch (UnknownHostException e) {
            return name;
        }
    }
    
    public static List<String> normalizeHostNames(final Collection<String> names) {
        final List<String> hostNames = new ArrayList<String>(names.size());
        for (final String name : names) {
            hostNames.add(normalizeHostName(name));
        }
        return hostNames;
    }
    
    public static void verifyHostnames(final String[] names) throws UnknownHostException {
        for (final String name : names) {
            if (name == null) {
                throw new UnknownHostException("null hostname found");
            }
            URI uri = null;
            try {
                uri = new URI(name);
                if (uri.getHost() == null) {
                    uri = new URI("http://" + name);
                }
            }
            catch (URISyntaxException e) {
                uri = null;
            }
            if (uri == null || uri.getHost() == null) {
                throw new UnknownHostException(name + " is not a valid Inet address");
            }
        }
    }
    
    public static String getHostNameOfIP(final String ipPort) {
        if (null == ipPort || !NetUtils.ipPortPattern.matcher(ipPort).matches()) {
            return null;
        }
        try {
            final int colonIdx = ipPort.indexOf(58);
            final String ip = (-1 == colonIdx) ? ipPort : ipPort.substring(0, ipPort.indexOf(58));
            return InetAddress.getByName(ip).getHostName();
        }
        catch (UnknownHostException e) {
            return null;
        }
    }
    
    public static String getLocalHostname() {
        try {
            return InetAddress.getLocalHost().getHostName();
        }
        catch (UnknownHostException uhe) {
            return "" + uhe;
        }
    }
    
    public static String getHostname() {
        try {
            return "" + InetAddress.getLocalHost();
        }
        catch (UnknownHostException uhe) {
            return "" + uhe;
        }
    }
    
    public static String getHostPortString(final InetSocketAddress addr) {
        return addr.getHostName() + ":" + addr.getPort();
    }
    
    public static InetAddress getLocalInetAddress(final String host) throws SocketException {
        if (host == null) {
            return null;
        }
        InetAddress addr = null;
        try {
            addr = SecurityUtil.getByName(host);
            if (NetworkInterface.getByInetAddress(addr) == null) {
                addr = null;
            }
        }
        catch (UnknownHostException ex) {}
        return addr;
    }
    
    public static boolean isLocalAddress(final InetAddress addr) {
        boolean local = addr.isAnyLocalAddress() || addr.isLoopbackAddress();
        if (!local) {
            try {
                local = (NetworkInterface.getByInetAddress(addr) != null);
            }
            catch (SocketException e) {
                local = false;
            }
        }
        return local;
    }
    
    public static IOException wrapException(final String destHost, final int destPort, final String localHost, final int localPort, final IOException exception) {
        try {
            if (exception instanceof BindException) {
                return wrapWithMessage(exception, "Problem binding to [" + localHost + ":" + localPort + "] " + exception + ";" + see("BindException"));
            }
            if (exception instanceof ConnectException) {
                if ((destHost != null && (destHost.equals("0.0.0.0") || destHost.equals("0:0:0:0:0:0:0:0") || destHost.equals("::"))) || destPort == 0) {
                    return wrapWithMessage(exception, "Your endpoint configuration is wrong;" + see("UnsetHostnameOrPort"));
                }
                return wrapWithMessage(exception, "Call From " + localHost + " to " + destHost + ":" + destPort + " failed on connection exception: " + exception + ";" + see("ConnectionRefused"));
            }
            else {
                if (exception instanceof UnknownHostException) {
                    return wrapWithMessage(exception, "Invalid host name: " + getHostDetailsAsString(destHost, destPort, localHost) + exception + ";" + see("UnknownHost"));
                }
                if (exception instanceof SocketTimeoutException) {
                    return wrapWithMessage(exception, "Call From " + localHost + " to " + destHost + ":" + destPort + " failed on socket timeout exception: " + exception + ";" + see("SocketTimeout"));
                }
                if (exception instanceof NoRouteToHostException) {
                    return wrapWithMessage(exception, "No Route to Host from  " + localHost + " to " + destHost + ":" + destPort + " failed on socket timeout exception: " + exception + ";" + see("NoRouteToHost"));
                }
                if (exception instanceof EOFException) {
                    return wrapWithMessage(exception, "End of File Exception between " + getHostDetailsAsString(destHost, destPort, localHost) + ": " + exception + ";" + see("EOFException"));
                }
                if (exception instanceof SocketException) {
                    return wrapWithMessage(exception, "Call From " + localHost + " to " + destHost + ":" + destPort + " failed on socket exception: " + exception + ";" + see("SocketException"));
                }
                return wrapWithMessage(exception, "DestHost:destPort " + destHost + ":" + destPort + " , LocalHost:localPort " + localHost + ":" + localPort + ". Failed on local exception: " + exception);
            }
        }
        catch (IOException ex) {
            return (IOException)new IOException("Failed on local exception: " + exception + "; Host Details : " + getHostDetailsAsString(destHost, destPort, localHost)).initCause(exception);
        }
    }
    
    private static String see(final String entry) {
        return " For more details see:  http://wiki.apache.org/hadoop/" + entry;
    }
    
    private static <T extends IOException> T wrapWithMessage(final T exception, final String msg) throws T, IOException {
        final Class<? extends Throwable> clazz = exception.getClass();
        try {
            final Constructor<? extends Throwable> ctor = clazz.getConstructor(String.class);
            final Throwable t = (Throwable)ctor.newInstance(msg);
            return (T)t.initCause(exception);
        }
        catch (Throwable e) {
            NetUtils.LOG.warn("Unable to wrap exception of type {}: it has no (String) constructor", clazz, e);
            throw exception;
        }
    }
    
    private static String getHostDetailsAsString(final String destHost, final int destPort, final String localHost) {
        final StringBuilder hostDetails = new StringBuilder(27);
        hostDetails.append("local host is: ").append(quoteHost(localHost)).append("; ");
        hostDetails.append("destination host is: ").append(quoteHost(destHost)).append(":").append(destPort).append("; ");
        return hostDetails.toString();
    }
    
    private static String quoteHost(final String hostname) {
        return (hostname != null) ? ("\"" + hostname + "\"") : "(unknown)";
    }
    
    public static boolean isValidSubnet(final String subnet) {
        try {
            new SubnetUtils(subnet);
            return true;
        }
        catch (IllegalArgumentException iae) {
            return false;
        }
    }
    
    private static void addMatchingAddrs(final NetworkInterface nif, final SubnetUtils.SubnetInfo subnetInfo, final List<InetAddress> addrs) {
        final Enumeration<InetAddress> ifAddrs = nif.getInetAddresses();
        while (ifAddrs.hasMoreElements()) {
            final InetAddress ifAddr = ifAddrs.nextElement();
            if (subnetInfo.isInRange(ifAddr.getHostAddress())) {
                addrs.add(ifAddr);
            }
        }
    }
    
    public static List<InetAddress> getIPs(final String subnet, final boolean returnSubinterfaces) {
        final List<InetAddress> addrs = new ArrayList<InetAddress>();
        final SubnetUtils.SubnetInfo subnetInfo = new SubnetUtils(subnet).getInfo();
        Enumeration<NetworkInterface> nifs;
        try {
            nifs = NetworkInterface.getNetworkInterfaces();
        }
        catch (SocketException e) {
            NetUtils.LOG.error("Unable to get host interfaces", e);
            return addrs;
        }
        while (nifs.hasMoreElements()) {
            final NetworkInterface nif = nifs.nextElement();
            addMatchingAddrs(nif, subnetInfo, addrs);
            if (!returnSubinterfaces) {
                continue;
            }
            final Enumeration<NetworkInterface> subNifs = nif.getSubInterfaces();
            while (subNifs.hasMoreElements()) {
                addMatchingAddrs(subNifs.nextElement(), subnetInfo, addrs);
            }
        }
        return addrs;
    }
    
    public static int getFreeSocketPort() {
        int port = 0;
        try {
            final ServerSocket s = new ServerSocket(0);
            port = s.getLocalPort();
            s.close();
            return port;
        }
        catch (IOException ex) {
            return port;
        }
    }
    
    public static InetAddress bindToLocalAddress(final InetAddress localAddr, final boolean bindWildCardAddress) {
        if (!bindWildCardAddress) {
            return localAddr;
        }
        return null;
    }
    
    static {
        LOG = LoggerFactory.getLogger(NetUtils.class);
        NetUtils.hostToResolved = new HashMap<String, String>();
        canonicalizedHostCache = new ConcurrentHashMap<String, String>();
        ipPortPattern = Pattern.compile("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}(:\\d+)?");
    }
}
