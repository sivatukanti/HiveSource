// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.net;

import org.slf4j.LoggerFactory;
import java.util.Arrays;
import java.util.List;
import com.google.common.net.InetAddresses;
import java.util.Vector;
import java.util.Iterator;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.net.SocketException;
import java.util.Enumeration;
import java.net.NetworkInterface;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import com.sun.istack.Nullable;
import java.net.InetAddress;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.LimitedPrivate({ "HDFS", "MapReduce" })
@InterfaceStability.Unstable
public class DNS
{
    private static final Logger LOG;
    private static final String cachedHostname;
    private static final String cachedHostAddress;
    private static final String LOCALHOST = "localhost";
    
    public static String reverseDns(final InetAddress hostIp, @Nullable final String ns) throws NamingException {
        final String[] parts = hostIp.getHostAddress().split("\\.");
        final String reverseIP = parts[3] + "." + parts[2] + "." + parts[1] + "." + parts[0] + ".in-addr.arpa";
        final DirContext ictx = new InitialDirContext();
        Attributes attribute;
        try {
            attribute = ictx.getAttributes("dns://" + ((ns == null) ? "" : ns) + "/" + reverseIP, new String[] { "PTR" });
        }
        finally {
            ictx.close();
        }
        String hostname = attribute.get("PTR").get().toString();
        final int hostnameLength = hostname.length();
        if (hostname.charAt(hostnameLength - 1) == '.') {
            hostname = hostname.substring(0, hostnameLength - 1);
        }
        return hostname;
    }
    
    private static NetworkInterface getSubinterface(final String strInterface) throws SocketException {
        final Enumeration<NetworkInterface> nifs = NetworkInterface.getNetworkInterfaces();
        while (nifs.hasMoreElements()) {
            final Enumeration<NetworkInterface> subNifs = nifs.nextElement().getSubInterfaces();
            while (subNifs.hasMoreElements()) {
                final NetworkInterface nif = subNifs.nextElement();
                if (nif.getName().equals(strInterface)) {
                    return nif;
                }
            }
        }
        return null;
    }
    
    private static LinkedHashSet<InetAddress> getSubinterfaceInetAddrs(final NetworkInterface nif) {
        final LinkedHashSet<InetAddress> addrs = new LinkedHashSet<InetAddress>();
        final Enumeration<NetworkInterface> subNifs = nif.getSubInterfaces();
        while (subNifs.hasMoreElements()) {
            final NetworkInterface subNif = subNifs.nextElement();
            addrs.addAll((Collection<?>)Collections.list(subNif.getInetAddresses()));
        }
        return addrs;
    }
    
    public static String[] getIPs(final String strInterface) throws UnknownHostException {
        return getIPs(strInterface, true);
    }
    
    public static String[] getIPs(final String strInterface, final boolean returnSubinterfaces) throws UnknownHostException {
        if ("default".equals(strInterface)) {
            return new String[] { DNS.cachedHostAddress };
        }
        NetworkInterface netIf;
        try {
            netIf = NetworkInterface.getByName(strInterface);
            if (netIf == null) {
                netIf = getSubinterface(strInterface);
            }
        }
        catch (SocketException e) {
            DNS.LOG.warn("I/O error finding interface {}", strInterface, e);
            return new String[] { DNS.cachedHostAddress };
        }
        if (netIf == null) {
            throw new UnknownHostException("No such interface " + strInterface);
        }
        final LinkedHashSet<InetAddress> allAddrs = new LinkedHashSet<InetAddress>();
        allAddrs.addAll((Collection<?>)Collections.list(netIf.getInetAddresses()));
        if (!returnSubinterfaces) {
            allAddrs.removeAll(getSubinterfaceInetAddrs(netIf));
        }
        final String[] ips = new String[allAddrs.size()];
        int i = 0;
        for (final InetAddress addr : allAddrs) {
            ips[i++] = addr.getHostAddress();
        }
        return ips;
    }
    
    public static String getDefaultIP(final String strInterface) throws UnknownHostException {
        final String[] ips = getIPs(strInterface);
        return ips[0];
    }
    
    public static String[] getHosts(final String strInterface, @Nullable final String nameserver, final boolean tryfallbackResolution) throws UnknownHostException {
        final List<String> hosts = new Vector<String>();
        final List<InetAddress> addresses = getIPsAsInetAddressList(strInterface, true);
        for (final InetAddress address : addresses) {
            try {
                hosts.add(reverseDns(address, nameserver));
            }
            catch (NamingException ex) {}
        }
        if (hosts.isEmpty() && tryfallbackResolution) {
            for (final InetAddress address : addresses) {
                final String canonicalHostName = address.getCanonicalHostName();
                if (!InetAddresses.isInetAddress(canonicalHostName)) {
                    hosts.add(canonicalHostName);
                }
            }
        }
        if (hosts.isEmpty()) {
            DNS.LOG.warn("Unable to determine hostname for interface {}", strInterface);
            hosts.add(DNS.cachedHostname);
        }
        return hosts.toArray(new String[hosts.size()]);
    }
    
    private static String resolveLocalHostname() {
        String localhost;
        try {
            localhost = InetAddress.getLocalHost().getCanonicalHostName();
        }
        catch (UnknownHostException e) {
            DNS.LOG.warn("Unable to determine local hostname -falling back to '{}'", "localhost", e);
            localhost = "localhost";
        }
        return localhost;
    }
    
    private static String resolveLocalHostIPAddress() {
        String address;
        try {
            address = InetAddress.getLocalHost().getHostAddress();
        }
        catch (UnknownHostException e) {
            DNS.LOG.warn("Unable to determine address of the host -falling back to '{}' address", "localhost", e);
            try {
                address = InetAddress.getByName("localhost").getHostAddress();
            }
            catch (UnknownHostException noLocalHostAddressException) {
                DNS.LOG.error("Unable to determine local loopback address of '{}' -this system's network configuration is unsupported", "localhost", e);
                address = null;
            }
        }
        return address;
    }
    
    public static String[] getHosts(final String strInterface) throws UnknownHostException {
        return getHosts(strInterface, null, false);
    }
    
    public static String getDefaultHost(@Nullable final String strInterface, @Nullable String nameserver, final boolean tryfallbackResolution) throws UnknownHostException {
        if (strInterface == null || "default".equals(strInterface)) {
            return DNS.cachedHostname;
        }
        if (nameserver != null && "default".equals(nameserver)) {
            nameserver = null;
        }
        final String[] hosts = getHosts(strInterface, nameserver, tryfallbackResolution);
        return hosts[0];
    }
    
    public static String getDefaultHost(@Nullable final String strInterface) throws UnknownHostException {
        return getDefaultHost(strInterface, null, false);
    }
    
    public static String getDefaultHost(@Nullable final String strInterface, @Nullable final String nameserver) throws UnknownHostException {
        return getDefaultHost(strInterface, nameserver, false);
    }
    
    public static List<InetAddress> getIPsAsInetAddressList(final String strInterface, final boolean returnSubinterfaces) throws UnknownHostException {
        if ("default".equals(strInterface)) {
            return Arrays.asList(InetAddress.getByName(DNS.cachedHostAddress));
        }
        NetworkInterface netIf;
        try {
            netIf = NetworkInterface.getByName(strInterface);
            if (netIf == null) {
                netIf = getSubinterface(strInterface);
            }
        }
        catch (SocketException e) {
            DNS.LOG.warn("I/O error finding interface {}: {}", strInterface, e.getMessage());
            return Arrays.asList(InetAddress.getByName(DNS.cachedHostAddress));
        }
        if (netIf == null) {
            throw new UnknownHostException("No such interface " + strInterface);
        }
        final LinkedHashSet<InetAddress> allAddrs = new LinkedHashSet<InetAddress>();
        allAddrs.addAll((Collection<?>)Collections.list(netIf.getInetAddresses()));
        if (!returnSubinterfaces) {
            allAddrs.removeAll(getSubinterfaceInetAddrs(netIf));
        }
        return new Vector<InetAddress>(allAddrs);
    }
    
    static {
        LOG = LoggerFactory.getLogger(DNS.class);
        cachedHostname = resolveLocalHostname();
        cachedHostAddress = resolveLocalHostIPAddress();
    }
}
