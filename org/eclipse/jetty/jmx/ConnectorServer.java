// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.jmx;

import org.eclipse.jetty.util.log.Log;
import java.rmi.Remote;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.LocateRegistry;
import java.net.ServerSocket;
import java.net.InetAddress;
import org.eclipse.jetty.util.thread.ShutdownThread;
import org.eclipse.jetty.util.component.LifeCycle;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.remote.JMXConnectorServerFactory;
import java.lang.management.ManagementFactory;
import java.util.Map;
import javax.management.remote.JMXServiceURL;
import java.rmi.registry.Registry;
import javax.management.remote.JMXConnectorServer;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.util.component.AbstractLifeCycle;

public class ConnectorServer extends AbstractLifeCycle
{
    private static final Logger LOG;
    JMXConnectorServer _connectorServer;
    Registry _registry;
    
    public ConnectorServer(final JMXServiceURL serviceURL, final String name) throws Exception {
        this(serviceURL, null, name);
    }
    
    public ConnectorServer(JMXServiceURL svcUrl, final Map<String, ?> environment, final String name) throws Exception {
        String urlPath = svcUrl.getURLPath();
        final int idx = urlPath.indexOf("rmi://");
        if (idx > 0) {
            final String hostPort = urlPath.substring(idx + 6, urlPath.indexOf(47, idx + 6));
            final String regHostPort = this.startRegistry(hostPort);
            if (regHostPort != null) {
                urlPath = urlPath.replace(hostPort, regHostPort);
                svcUrl = new JMXServiceURL(svcUrl.getProtocol(), svcUrl.getHost(), svcUrl.getPort(), urlPath);
            }
        }
        final MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();
        mbeanServer.registerMBean(this._connectorServer = JMXConnectorServerFactory.newJMXConnectorServer(svcUrl, environment, mbeanServer), new ObjectName(name));
    }
    
    public void doStart() throws Exception {
        this._connectorServer.start();
        ShutdownThread.register(0, this);
        ConnectorServer.LOG.info("JMX Remote URL: {}", this._connectorServer.getAddress().toString());
    }
    
    public void doStop() throws Exception {
        ShutdownThread.deregister(this);
        this._connectorServer.stop();
        this.stopRegistry();
    }
    
    private String startRegistry(final String hostPath) throws Exception {
        int rmiPort = 1099;
        String rmiHost = hostPath;
        final int idx = hostPath.indexOf(58);
        if (idx > 0) {
            rmiPort = Integer.parseInt(hostPath.substring(idx + 1));
            rmiHost = hostPath.substring(0, idx);
        }
        final InetAddress hostAddress = InetAddress.getByName(rmiHost);
        if (hostAddress.isLoopbackAddress()) {
            if (rmiPort == 0) {
                final ServerSocket socket = new ServerSocket(0);
                rmiPort = socket.getLocalPort();
                socket.close();
            }
            else {
                try {
                    LocateRegistry.getRegistry(rmiPort).list();
                    return null;
                }
                catch (Exception ex) {
                    ConnectorServer.LOG.ignore(ex);
                }
            }
            this._registry = LocateRegistry.createRegistry(rmiPort);
            Thread.sleep(1000L);
            rmiHost = InetAddress.getLocalHost().getCanonicalHostName();
            return rmiHost + ':' + Integer.toString(rmiPort);
        }
        return null;
    }
    
    private void stopRegistry() {
        if (this._registry != null) {
            try {
                UnicastRemoteObject.unexportObject(this._registry, true);
            }
            catch (Exception ex) {
                ConnectorServer.LOG.ignore(ex);
            }
        }
    }
    
    static {
        LOG = Log.getLogger(ConnectorServer.class);
    }
}
