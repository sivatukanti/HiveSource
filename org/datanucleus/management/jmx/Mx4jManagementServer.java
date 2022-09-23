// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.management.jmx;

import javax.management.ObjectName;
import java.rmi.NoSuchObjectException;
import java.io.IOException;
import org.datanucleus.exceptions.NucleusException;
import java.util.Map;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;
import java.util.Hashtable;
import java.net.InetAddress;
import javax.management.MBeanServerFactory;
import org.datanucleus.util.NucleusLogger;
import mx4j.tools.naming.NamingService;
import javax.management.remote.JMXConnectorServer;
import javax.management.MBeanServer;

public class Mx4jManagementServer implements ManagementServer
{
    MBeanServer server;
    JMXConnectorServer jmxServer;
    NamingService naming;
    
    @Override
    public void start() {
        if (NucleusLogger.GENERAL.isDebugEnabled()) {
            NucleusLogger.GENERAL.debug("Starting ManagementServer");
        }
        final int port = 1199;
        try {
            (this.naming = new NamingService(port)).start();
            this.server = MBeanServerFactory.createMBeanServer();
            final String hostName = InetAddress.getLocalHost().getHostName();
            final Hashtable env = new Hashtable();
            env.put("java.naming.factory.initial", "com.sun.jndi.rmi.registry.RegistryContextFactory");
            env.put("java.naming.provider.url", "rmi://" + hostName + ":" + port);
            final JMXServiceURL address = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://" + hostName + ":" + port + "/datanucleus");
            (this.jmxServer = JMXConnectorServerFactory.newJMXConnectorServer(address, env, this.server)).start();
            if (NucleusLogger.GENERAL.isDebugEnabled()) {
                NucleusLogger.GENERAL.debug("MBeanServer listening at " + this.jmxServer.getAddress().toString());
            }
        }
        catch (Exception e) {
            throw new NucleusException(e.getMessage(), e);
        }
    }
    
    @Override
    public void stop() {
        if (NucleusLogger.GENERAL.isDebugEnabled()) {
            NucleusLogger.GENERAL.debug("Stopping ManagementServer");
        }
        if (this.jmxServer != null) {
            try {
                this.jmxServer.stop();
            }
            catch (IOException e) {
                NucleusLogger.GENERAL.error(e);
            }
        }
        if (this.naming != null) {
            try {
                this.naming.stop();
            }
            catch (NoSuchObjectException e2) {
                NucleusLogger.GENERAL.error(e2);
            }
        }
        this.jmxServer = null;
        this.naming = null;
        this.server = null;
    }
    
    @Override
    public void registerMBean(final Object mbean, final String name) {
        try {
            final ObjectName objName = new ObjectName(name);
            this.server.registerMBean(mbean, objName);
        }
        catch (Exception e) {
            throw new NucleusException(e.getMessage(), e);
        }
    }
    
    @Override
    public void unregisterMBean(final String name) {
        try {
            final ObjectName objName = new ObjectName(name);
            this.server.unregisterMBean(objName);
        }
        catch (Exception e) {
            throw new NucleusException(e.getMessage(), e);
        }
    }
    
    public Object getMBeanServer() {
        return this.server;
    }
}
