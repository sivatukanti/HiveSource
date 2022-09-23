// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.db;

import org.apache.derby.impl.store.replication.ReplicationLogger;
import org.apache.derby.jdbc.InternalDriver;
import org.apache.derby.iapi.util.InterruptStatus;
import java.sql.SQLException;
import org.apache.derby.iapi.error.PublicAPI;
import org.apache.derby.iapi.services.context.ContextService;
import org.apache.derby.iapi.jdbc.AuthenticationService;
import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;
import org.apache.derby.iapi.services.context.ContextManager;
import org.apache.derby.iapi.services.monitor.Monitor;
import java.util.Properties;
import org.apache.derby.iapi.store.replication.slave.SlaveFactory;
import org.apache.derby.iapi.error.StandardException;

public class SlaveDatabase extends BasicDatabase
{
    private volatile boolean inReplicationSlaveMode;
    private volatile boolean shutdownInitiated;
    private volatile boolean inBoot;
    private volatile StandardException bootException;
    private String dbname;
    private volatile SlaveFactory slaveFac;
    
    public boolean canSupport(final Properties properties) {
        boolean desiredCreateType = Monitor.isDesiredCreateType(properties, this.getEngineType());
        if (desiredCreateType) {
            final String property = properties.getProperty("replication.slave.mode");
            if (property == null || !property.equals("slavemode")) {
                desiredCreateType = false;
            }
        }
        return desiredCreateType;
    }
    
    public void boot(final boolean b, final Properties properties) throws StandardException {
        this.inReplicationSlaveMode = true;
        this.inBoot = true;
        this.shutdownInitiated = false;
        this.dbname = properties.getProperty("replication.slave.dbname");
        final Thread thread = new Thread(new SlaveDatabaseBootThread(b, properties), "derby.slave.boot-" + this.dbname);
        thread.setDaemon(true);
        thread.start();
        this.verifySuccessfulBoot();
        this.inBoot = false;
        this.active = true;
    }
    
    public void stop() {
        if (this.inReplicationSlaveMode && this.slaveFac != null) {
            try {
                this.slaveFac.stopSlave(true);
            }
            catch (StandardException ex) {}
            finally {
                this.slaveFac = null;
            }
        }
        super.stop();
    }
    
    public boolean isInSlaveMode() {
        return this.inReplicationSlaveMode;
    }
    
    public LanguageConnectionContext setupConnection(final ContextManager contextManager, final String s, final String s2, final String s3) throws StandardException {
        if (this.inReplicationSlaveMode) {
            throw StandardException.newException("08004.C.7", s3);
        }
        return super.setupConnection(contextManager, s, s2, s3);
    }
    
    public AuthenticationService getAuthenticationService() throws StandardException {
        if (this.inReplicationSlaveMode) {
            throw StandardException.newException("08004.C.7", this.dbname);
        }
        return super.getAuthenticationService();
    }
    
    public void verifyShutdownSlave() throws StandardException {
        if (!this.shutdownInitiated) {
            throw StandardException.newException("XRE43");
        }
        this.pushDbContext(ContextService.getFactory().getCurrentContextManager());
    }
    
    public void stopReplicationSlave() throws SQLException {
        if (this.shutdownInitiated) {
            return;
        }
        if (!this.inReplicationSlaveMode) {
            throw PublicAPI.wrapStandardException(StandardException.newException("XRE40"));
        }
        try {
            this.slaveFac.stopSlave(false);
        }
        catch (StandardException ex) {
            throw PublicAPI.wrapStandardException(ex);
        }
        this.slaveFac = null;
    }
    
    public void failover(final String s) throws StandardException {
        if (this.inReplicationSlaveMode) {
            this.slaveFac.failover();
            while (this.inReplicationSlaveMode) {
                try {
                    Thread.sleep(500L);
                }
                catch (InterruptedException ex) {
                    InterruptStatus.setInterrupted();
                }
            }
        }
        else {
            super.failover(s);
        }
    }
    
    private void verifySuccessfulBoot() throws StandardException {
        while (!this.isSlaveFactorySet() || !this.slaveFac.isStarted()) {
            if (this.bootException != null) {
                throw this.bootException;
            }
            try {
                Thread.sleep(500L);
            }
            catch (InterruptedException ex) {
                InterruptStatus.setInterrupted();
            }
        }
        if (this.bootException != null) {
            throw this.bootException;
        }
    }
    
    private boolean isSlaveFactorySet() {
        if (this.slaveFac != null) {
            return true;
        }
        try {
            this.slaveFac = (SlaveFactory)Monitor.findServiceModule(this, "org.apache.derby.iapi.store.replication.slave.SlaveFactory");
            return true;
        }
        catch (StandardException ex) {
            return false;
        }
    }
    
    private void handleShutdown(final StandardException bootException) {
        if (this.inBoot) {
            this.bootException = bootException;
            return;
        }
        try {
            this.shutdownInitiated = true;
            final String string = "jdbc:derby:" + this.dbname + ";" + "internal_stopslave" + "=true";
            final InternalDriver activeDriver = InternalDriver.activeDriver();
            if (activeDriver != null) {
                activeDriver.connect(string, null, 0);
            }
        }
        catch (Exception ex) {}
    }
    
    private void bootBasicDatabase(final boolean b, final Properties properties) throws StandardException {
        super.boot(b, properties);
    }
    
    private class SlaveDatabaseBootThread implements Runnable
    {
        private boolean create;
        private Properties params;
        
        public SlaveDatabaseBootThread(final boolean create, final Properties params) {
            this.create = create;
            this.params = params;
        }
        
        public void run() {
            try {
                final ContextManager contextManager = ContextService.getFactory().newContextManager();
                ContextService.getFactory().setCurrentContextManager(contextManager);
                SlaveDatabase.this.bootBasicDatabase(this.create, this.params);
                SlaveDatabase.this.inReplicationSlaveMode = false;
                if (contextManager != null) {
                    ContextService.getFactory().resetCurrentContextManager(contextManager);
                }
            }
            catch (Exception ex) {
                new ReplicationLogger(SlaveDatabase.this.dbname).logError("R005", ex);
                if (ex instanceof StandardException) {
                    SlaveDatabase.this.handleShutdown((StandardException)ex);
                }
            }
        }
    }
}
