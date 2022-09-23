// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.server;

import org.slf4j.LoggerFactory;
import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.LoginException;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.Configuration;
import javax.management.JMException;
import org.apache.zookeeper.jmx.ZKMBeanInfo;
import org.apache.zookeeper.jmx.MBeanRegistry;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.HashSet;
import java.util.Map;
import org.apache.zookeeper.Login;
import org.apache.zookeeper.server.auth.SaslServerCallbackHandler;
import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentMap;
import org.slf4j.Logger;

public abstract class ServerCnxnFactory
{
    public static final String ZOOKEEPER_SERVER_CNXN_FACTORY = "zookeeper.serverCnxnFactory";
    private static final Logger LOG;
    protected final ConcurrentMap<Long, ServerCnxn> sessionMap;
    static final ByteBuffer closeConn;
    protected SaslServerCallbackHandler saslServerCallbackHandler;
    public Login login;
    protected ZooKeeperServer zkServer;
    private final Map<ServerCnxn, ConnectionBean> connectionBeans;
    protected final HashSet<ServerCnxn> cnxns;
    
    public ServerCnxnFactory() {
        this.sessionMap = new ConcurrentHashMap<Long, ServerCnxn>();
        this.connectionBeans = new ConcurrentHashMap<ServerCnxn, ConnectionBean>();
        this.cnxns = new HashSet<ServerCnxn>();
    }
    
    public abstract int getLocalPort();
    
    public abstract Iterable<ServerCnxn> getConnections();
    
    public int getNumAliveConnections() {
        synchronized (this.cnxns) {
            return this.cnxns.size();
        }
    }
    
    ZooKeeperServer getZooKeeperServer() {
        return this.zkServer;
    }
    
    public abstract void closeSession(final long p0);
    
    public abstract void configure(final InetSocketAddress p0, final int p1) throws IOException;
    
    public abstract int getMaxClientCnxnsPerHost();
    
    public abstract void setMaxClientCnxnsPerHost(final int p0);
    
    public abstract void startup(final ZooKeeperServer p0) throws IOException, InterruptedException;
    
    public abstract void join() throws InterruptedException;
    
    public abstract void shutdown();
    
    public abstract void start();
    
    public final void setZooKeeperServer(final ZooKeeperServer zk) {
        this.zkServer = zk;
        if (zk != null) {
            zk.setServerCnxnFactory(this);
        }
    }
    
    public abstract void closeAll();
    
    public static ServerCnxnFactory createFactory() throws IOException {
        String serverCnxnFactoryName = System.getProperty("zookeeper.serverCnxnFactory");
        if (serverCnxnFactoryName == null) {
            serverCnxnFactoryName = NIOServerCnxnFactory.class.getName();
        }
        try {
            final ServerCnxnFactory serverCnxnFactory = (ServerCnxnFactory)Class.forName(serverCnxnFactoryName).getDeclaredConstructor((Class<?>[])new Class[0]).newInstance(new Object[0]);
            ServerCnxnFactory.LOG.info("Using {} as server connection factory", serverCnxnFactoryName);
            return serverCnxnFactory;
        }
        catch (Exception e) {
            final IOException ioe = new IOException("Couldn't instantiate " + serverCnxnFactoryName);
            ioe.initCause(e);
            throw ioe;
        }
    }
    
    public static ServerCnxnFactory createFactory(final int clientPort, final int maxClientCnxns) throws IOException {
        return createFactory(new InetSocketAddress(clientPort), maxClientCnxns);
    }
    
    public static ServerCnxnFactory createFactory(final InetSocketAddress addr, final int maxClientCnxns) throws IOException {
        final ServerCnxnFactory factory = createFactory();
        factory.configure(addr, maxClientCnxns);
        return factory;
    }
    
    public abstract InetSocketAddress getLocalAddress();
    
    public void unregisterConnection(final ServerCnxn serverCnxn) {
        final ConnectionBean jmxConnectionBean = this.connectionBeans.remove(serverCnxn);
        if (jmxConnectionBean != null) {
            MBeanRegistry.getInstance().unregister(jmxConnectionBean);
        }
    }
    
    public void registerConnection(final ServerCnxn serverCnxn) {
        if (this.zkServer != null) {
            final ConnectionBean jmxConnectionBean = new ConnectionBean(serverCnxn, this.zkServer);
            try {
                MBeanRegistry.getInstance().register(jmxConnectionBean, this.zkServer.jmxServerBean);
                this.connectionBeans.put(serverCnxn, jmxConnectionBean);
            }
            catch (JMException e) {
                ServerCnxnFactory.LOG.warn("Could not register connection", e);
            }
        }
    }
    
    public void addSession(final long sessionId, final ServerCnxn cnxn) {
        this.sessionMap.put(sessionId, cnxn);
    }
    
    protected void configureSaslLogin() throws IOException {
        final String serverSection = System.getProperty("zookeeper.sasl.serverconfig", "Server");
        AppConfigurationEntry[] entries = null;
        SecurityException securityException = null;
        try {
            entries = Configuration.getConfiguration().getAppConfigurationEntry(serverSection);
        }
        catch (SecurityException e) {
            securityException = e;
        }
        if (entries != null) {
            try {
                this.saslServerCallbackHandler = new SaslServerCallbackHandler(Configuration.getConfiguration());
                (this.login = new Login(serverSection, this.saslServerCallbackHandler)).startThreadIfNeeded();
            }
            catch (LoginException e2) {
                throw new IOException("Could not configure server because SASL configuration did not allow the  ZooKeeper server to authenticate itself properly: " + e2);
            }
            return;
        }
        final String jaasFile = System.getProperty("java.security.auth.login.config");
        final String loginContextName = System.getProperty("zookeeper.sasl.serverconfig");
        if (securityException != null && (loginContextName != null || jaasFile != null)) {
            String errorMessage = "No JAAS configuration section named '" + serverSection + "' was found";
            if (jaasFile != null) {
                errorMessage = errorMessage + "in '" + jaasFile + "'.";
            }
            if (loginContextName != null) {
                errorMessage += " But zookeeper.sasl.serverconfig was set.";
            }
            ServerCnxnFactory.LOG.error(errorMessage);
            throw new IOException(errorMessage);
        }
    }
    
    static {
        LOG = LoggerFactory.getLogger(ServerCnxnFactory.class);
        closeConn = ByteBuffer.allocate(0);
    }
    
    public interface PacketProcessor
    {
        void processPacket(final ByteBuffer p0, final ServerCnxn p1);
    }
}
