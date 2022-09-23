// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.management.jmx;

import org.datanucleus.util.StringUtils;
import org.datanucleus.util.NucleusLogger;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.NucleusContext;

public class ManagementManager
{
    private final NucleusContext nucleusContext;
    private boolean closed;
    private ManagementServer mgmtServer;
    private String domainName;
    private String instanceName;
    
    public ManagementManager(final NucleusContext ctxt) {
        this.closed = false;
        this.nucleusContext = ctxt;
        this.domainName = ctxt.getPersistenceConfiguration().getStringProperty("datanucleus.PersistenceUnitName");
        if (this.domainName == null) {
            this.domainName = "datanucleus";
        }
        this.instanceName = "datanucleus-" + NucleusContext.random.nextInt();
        this.startManagementServer();
    }
    
    public String getInstanceName() {
        return this.instanceName;
    }
    
    public String getDomainName() {
        return this.domainName;
    }
    
    public void registerMBean(final Object mbean, final String name) {
        this.mgmtServer.registerMBean(mbean, name);
    }
    
    public void deregisterMBean(final String name) {
        this.mgmtServer.unregisterMBean(name);
    }
    
    public boolean isOpen() {
        return !this.closed;
    }
    
    public synchronized void close() {
        this.assertNotClosed();
        this.stopManagementServer();
        this.closed = true;
    }
    
    private void assertNotClosed() {
        if (this.closed) {
            throw new NucleusException("Management instance is closed and cannot be used. You must adquire a new context").setFatal();
        }
    }
    
    private void startManagementServer() {
        if (this.mgmtServer == null) {
            final String jmxType = this.nucleusContext.getPersistenceConfiguration().getStringProperty("datanucleus.jmxType");
            try {
                this.mgmtServer = (ManagementServer)this.nucleusContext.getPluginManager().createExecutableExtension("org.datanucleus.management_server", "name", jmxType, "class", null, null);
                if (this.mgmtServer != null) {
                    NucleusLogger.GENERAL.info("Starting Management Server");
                    this.mgmtServer.start();
                }
            }
            catch (Exception e) {
                this.mgmtServer = null;
                NucleusLogger.GENERAL.error("Error instantiating or connecting to Management Server : " + StringUtils.getStringFromStackTrace(e));
            }
        }
    }
    
    private void stopManagementServer() {
        if (this.mgmtServer != null) {
            NucleusLogger.GENERAL.info("Stopping Management Server");
            this.mgmtServer.stop();
        }
    }
}
