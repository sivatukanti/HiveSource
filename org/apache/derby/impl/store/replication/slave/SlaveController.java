// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.replication.slave;

import org.apache.derby.impl.store.raw.log.LogCounter;
import java.io.EOFException;
import org.apache.derby.impl.store.replication.net.ReplicationMessage;
import java.io.IOException;
import java.net.SocketTimeoutException;
import org.apache.derby.iapi.services.monitor.Monitor;
import org.apache.derby.iapi.store.raw.log.LogFactory;
import java.net.UnknownHostException;
import org.apache.derby.iapi.error.StandardException;
import java.util.Properties;
import org.apache.derby.impl.store.replication.net.SlaveAddress;
import org.apache.derby.impl.store.replication.ReplicationLogger;
import org.apache.derby.impl.store.replication.net.ReplicationMessageReceive;
import org.apache.derby.impl.store.raw.log.LogToFile;
import org.apache.derby.iapi.store.raw.RawStoreFactory;
import org.apache.derby.iapi.services.monitor.ModuleSupportable;
import org.apache.derby.iapi.services.monitor.ModuleControl;
import org.apache.derby.iapi.store.replication.slave.SlaveFactory;

public class SlaveController implements SlaveFactory, ModuleControl, ModuleSupportable
{
    private static final int DEFAULT_SOCKET_TIMEOUT = 1000;
    private RawStoreFactory rawStoreFactory;
    private LogToFile logToFile;
    private ReplicationMessageReceive receiver;
    private ReplicationLogger repLogger;
    private SlaveAddress slaveAddr;
    private String dbname;
    private volatile long highestLogInstant;
    private volatile boolean inReplicationSlaveMode;
    private volatile boolean startupSuccessful;
    private ReplicationLogScan logScan;
    private SlaveLogReceiverThread logReceiverThread;
    
    public SlaveController() {
        this.highestLogInstant = -1L;
        this.inReplicationSlaveMode = true;
        this.startupSuccessful = false;
    }
    
    public void boot(final boolean b, final Properties properties) throws StandardException {
        final String property = properties.getProperty("slavePort");
        try {
            int intValue = -1;
            if (property != null) {
                intValue = new Integer(property);
            }
            this.slaveAddr = new SlaveAddress(properties.getProperty("slaveHost"), intValue);
        }
        catch (UnknownHostException ex) {
            throw StandardException.newException("XRE04.C.1", ex, this.dbname, this.getHostName(), String.valueOf(this.getPortNumber()));
        }
        this.dbname = properties.getProperty("replication.slave.dbname");
        this.repLogger = new ReplicationLogger(this.dbname);
    }
    
    public void stop() {
        if (this.inReplicationSlaveMode) {
            try {
                this.stopSlave(true);
            }
            catch (StandardException ex) {}
        }
    }
    
    public boolean canSupport(final Properties properties) {
        final String property = properties.getProperty("replication.slave.mode");
        return property != null && property.equals("slavemode");
    }
    
    public void startSlave(final RawStoreFactory rawStoreFactory, final LogFactory logFactory) throws StandardException {
        this.rawStoreFactory = rawStoreFactory;
        try {
            this.logToFile = (LogToFile)logFactory;
        }
        catch (ClassCastException ex) {
            throw StandardException.newException("XRE00");
        }
        this.logToFile.initializeReplicationSlaveRole();
        this.receiver = new ReplicationMessageReceive(this.slaveAddr, this.dbname);
        while (!this.setupConnection()) {
            if (!this.inReplicationSlaveMode) {
                return;
            }
        }
        this.logScan = new ReplicationLogScan();
        this.startLogReceiverThread();
        this.startupSuccessful = true;
        Monitor.logTextMessage("R003", this.dbname);
    }
    
    private void stopSlave() throws StandardException {
        this.inReplicationSlaveMode = false;
        this.teardownNetwork();
        this.logToFile.stopReplicationSlaveRole();
        Monitor.logTextMessage("R004", this.dbname);
    }
    
    public void stopSlave(final boolean b) throws StandardException {
        if (!b && this.isConnectedToMaster()) {
            throw StandardException.newException("XRE41.C");
        }
        this.stopSlave();
    }
    
    public void failover() throws StandardException {
        if (this.isConnectedToMaster()) {
            throw StandardException.newException("XRE41.C");
        }
        this.doFailover();
        this.teardownNetwork();
    }
    
    private void doFailover() {
        this.inReplicationSlaveMode = false;
        this.logToFile.failoverSlave();
        Monitor.logTextMessage("R020", this.dbname);
    }
    
    public boolean isStarted() {
        return this.startupSuccessful;
    }
    
    private boolean setupConnection() throws StandardException {
        try {
            if (this.highestLogInstant != -1L) {
                this.receiver.initConnection(1000, this.highestLogInstant, this.dbname);
            }
            else {
                this.receiver.initConnection(1000, this.logToFile.getFirstUnflushedInstantAsLong(), this.dbname);
            }
            return true;
        }
        catch (StandardException ex) {
            throw ex;
        }
        catch (SocketTimeoutException ex3) {
            return false;
        }
        catch (Exception ex2) {
            throw StandardException.newException("XRE04.C.1", ex2, this.dbname, this.getHostName(), String.valueOf(this.getPortNumber()));
        }
    }
    
    private void handleDisconnect(final Exception ex) {
        if (!this.inReplicationSlaveMode) {
            return;
        }
        this.repLogger.logError("R006", ex);
        try {
            while (!this.setupConnection()) {
                if (!this.inReplicationSlaveMode) {
                    return;
                }
            }
            this.startLogReceiverThread();
        }
        catch (StandardException ex2) {
            this.handleFatalException(ex2);
        }
    }
    
    private boolean isConnectedToMaster() {
        return this.receiver != null && this.receiver.isConnectedToMaster();
    }
    
    private void startLogReceiverThread() {
        (this.logReceiverThread = new SlaveLogReceiverThread()).setDaemon(true);
        this.logReceiverThread.start();
    }
    
    private void handleFatalException(final Exception ex) {
        if (!this.inReplicationSlaveMode) {
            return;
        }
        this.repLogger.logError("R005", ex);
        try {
            this.stopSlave();
        }
        catch (StandardException ex2) {
            this.repLogger.logError("R005", ex2);
        }
    }
    
    private void teardownNetwork() {
        try {
            if (this.receiver != null) {
                this.receiver.tearDown();
                this.receiver = null;
            }
        }
        catch (IOException ex) {
            this.repLogger.logError(null, ex);
        }
    }
    
    private String getHostName() {
        return this.slaveAddr.getHostAddress().getHostName();
    }
    
    private int getPortNumber() {
        return this.slaveAddr.getPortNumber();
    }
    
    private class SlaveLogReceiverThread extends Thread
    {
        SlaveLogReceiverThread() {
            super("derby.slave.logger-" + SlaveController.this.dbname);
        }
        
        public void run() {
            try {
                while (SlaveController.this.inReplicationSlaveMode) {
                    final ReplicationMessage message = SlaveController.this.receiver.readMessage();
                    switch (message.getType()) {
                        case 10: {
                            this.handleLogChunk((byte[])message.getMessage());
                            continue;
                        }
                        case 21: {
                            SlaveController.this.doFailover();
                            SlaveController.this.receiver.sendMessage(new ReplicationMessage(11, "failover succeeded"));
                            SlaveController.this.teardownNetwork();
                            continue;
                        }
                        case 20: {
                            SlaveController.this.stopSlave();
                            continue;
                        }
                        default: {
                            System.out.println("Not handling non-log messages yet - got a type " + message.getType());
                            continue;
                        }
                    }
                }
            }
            catch (EOFException ex) {
                SlaveController.this.handleDisconnect(ex);
            }
            catch (StandardException ex2) {
                SlaveController.this.handleFatalException(ex2);
            }
            catch (Exception ex3) {
                SlaveController.this.handleFatalException(StandardException.newException("XRE03", ex3));
            }
        }
        
        private void handleLogChunk(final byte[] array) throws StandardException {
            SlaveController.this.logScan.init(array);
            while (SlaveController.this.logScan.next()) {
                if (SlaveController.this.logScan.isLogFileSwitch()) {
                    SlaveController.this.logToFile.switchLogFile();
                }
                else {
                    final long appendLogRecord = SlaveController.this.logToFile.appendLogRecord(SlaveController.this.logScan.getData(), 0, SlaveController.this.logScan.getDataLength(), null, 0, 0);
                    if (SlaveController.this.logScan.getInstant() != appendLogRecord) {
                        throw StandardException.newException("XRE05.C", SlaveController.this.dbname, new Long(LogCounter.getLogFileNumber(SlaveController.this.logScan.getInstant())), new Long(LogCounter.getLogFilePosition(SlaveController.this.logScan.getInstant())), new Long(LogCounter.getLogFileNumber(appendLogRecord)), new Long(LogCounter.getLogFilePosition(appendLogRecord)));
                    }
                    SlaveController.this.highestLogInstant = appendLogRecord;
                }
            }
        }
    }
}
