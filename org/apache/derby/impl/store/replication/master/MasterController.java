// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.replication.master;

import java.net.SocketTimeoutException;
import org.apache.derby.impl.store.replication.buffer.LogBufferFullException;
import org.apache.derby.iapi.services.property.PropertyUtil;
import org.apache.derby.impl.store.replication.net.ReplicationMessage;
import java.io.IOException;
import org.apache.derby.iapi.services.monitor.Monitor;
import java.net.UnknownHostException;
import org.apache.derby.iapi.error.StandardException;
import java.util.Properties;
import org.apache.derby.impl.store.replication.net.SlaveAddress;
import org.apache.derby.impl.store.replication.ReplicationLogger;
import org.apache.derby.impl.store.replication.net.ReplicationMessageTransmit;
import org.apache.derby.impl.store.replication.buffer.ReplicationLogBuffer;
import org.apache.derby.iapi.store.raw.log.LogFactory;
import org.apache.derby.iapi.store.raw.data.DataFactory;
import org.apache.derby.iapi.store.raw.RawStoreFactory;
import org.apache.derby.iapi.services.monitor.ModuleSupportable;
import org.apache.derby.iapi.services.monitor.ModuleControl;
import org.apache.derby.iapi.store.replication.master.MasterFactory;

public class MasterController implements MasterFactory, ModuleControl, ModuleSupportable
{
    private static final int DEFAULT_LOG_BUFFER_SIZE = 32768;
    private static final int LOG_BUFFER_SIZE_MIN = 8192;
    private static final int LOG_BUFFER_SIZE_MAX = 1048576;
    private RawStoreFactory rawStoreFactory;
    private DataFactory dataFactory;
    private LogFactory logFactory;
    private ReplicationLogBuffer logBuffer;
    private AsynchronousLogShipper logShipper;
    private ReplicationMessageTransmit transmitter;
    private ReplicationLogger repLogger;
    private String replicationMode;
    private SlaveAddress slaveAddr;
    private String dbname;
    private int logBufferSize;
    private boolean active;
    private static final int SLAVE_CONNECTION_ATTEMPT_TIMEOUT = 5000;
    
    public MasterController() {
        this.logBufferSize = 0;
        this.active = false;
    }
    
    public void boot(final boolean b, final Properties properties) throws StandardException {
        this.replicationMode = properties.getProperty("derby.__rt.replication.master.mode");
    }
    
    public boolean canSupport(final Properties properties) {
        final String property = properties.getProperty("derby.__rt.replication.master.mode");
        return property != null && property.equals("derby.__rt.asynch");
    }
    
    public void stop() {
        try {
            this.stopMaster();
        }
        catch (StandardException ex) {
            this.repLogger.logError("R008", ex);
        }
    }
    
    public void startMaster(final RawStoreFactory rawStoreFactory, final DataFactory dataFactory, final LogFactory logFactory, final String s, final int n, final String dbname) throws StandardException {
        if (this.active) {
            throw StandardException.newException("XRE22.C", dbname);
        }
        try {
            this.slaveAddr = new SlaveAddress(s, n);
        }
        catch (UnknownHostException ex) {
            throw StandardException.newException("XRE04.C.1", ex, dbname, this.getHostName(), String.valueOf(this.getPortNumber()));
        }
        this.dbname = dbname;
        this.rawStoreFactory = rawStoreFactory;
        this.dataFactory = dataFactory;
        this.logFactory = logFactory;
        this.repLogger = new ReplicationLogger(dbname);
        this.getMasterProperties();
        this.logBuffer = new ReplicationLogBuffer(this.logBufferSize, this);
        try {
            this.logFactory.startReplicationMasterRole(this);
            this.rawStoreFactory.unfreeze();
            this.setupConnection();
            if (this.replicationMode.equals("derby.__rt.asynch")) {
                (this.logShipper = new AsynchronousLogShipper(this.logBuffer, this.transmitter, this, this.repLogger)).setDaemon(true);
                this.logShipper.start();
            }
        }
        catch (StandardException ex2) {
            this.repLogger.logError("R005", ex2);
            this.logFactory.stopReplicationMasterRole();
            this.teardownNetwork();
            throw ex2;
        }
        this.active = true;
        Monitor.logTextMessage("R007", dbname);
    }
    
    public void stopMaster() throws StandardException {
        if (!this.active) {
            throw StandardException.newException("XRE07");
        }
        this.active = false;
        this.logFactory.stopReplicationMasterRole();
        try {
            this.logShipper.flushBuffer();
        }
        catch (IOException ex) {
            this.repLogger.logError("R009", ex);
        }
        catch (StandardException ex2) {
            this.repLogger.logError("R009", ex2);
        }
        finally {
            this.teardownNetwork();
        }
        Monitor.logTextMessage("R008", this.dbname);
    }
    
    public void startFailover() throws StandardException {
        if (!this.active) {
            throw StandardException.newException("XRE07");
        }
        ReplicationMessage sendMessageWaitForReply = null;
        this.active = false;
        this.rawStoreFactory.freeze();
        try {
            this.logShipper.flushBuffer();
            sendMessageWaitForReply = this.transmitter.sendMessageWaitForReply(new ReplicationMessage(21, null));
        }
        catch (IOException ex) {
            this.handleFailoverFailure(ex);
        }
        catch (StandardException ex2) {
            this.handleFailoverFailure(ex2);
        }
        if (sendMessageWaitForReply == null) {
            this.handleFailoverFailure(null);
        }
        else {
            if (sendMessageWaitForReply.getType() == 11) {
                this.teardownNetwork();
                this.rawStoreFactory.unfreeze();
                throw StandardException.newException("XRE20.D", this.dbname);
            }
            this.handleFailoverFailure(null);
        }
    }
    
    private void getMasterProperties() {
        this.logBufferSize = PropertyUtil.getSystemInt("derby.replication.logBufferSize", 32768);
        if (this.logBufferSize < 8192) {
            this.logBufferSize = 8192;
        }
        else if (this.logBufferSize > 1048576) {
            this.logBufferSize = 1048576;
        }
    }
    
    private void handleFailoverFailure(final Throwable t) throws StandardException {
        this.teardownNetwork();
        this.rawStoreFactory.unfreeze();
        if (t != null) {
            throw StandardException.newException("XRE21.C", t, this.dbname);
        }
        throw StandardException.newException("XRE21.C", this.dbname);
    }
    
    public void appendLog(final long n, final byte[] array, final int n2, final int n3) {
        try {
            this.logBuffer.appendLog(n, array, n2, n3);
        }
        catch (LogBufferFullException ex4) {
            try {
                this.logShipper.forceFlush();
                this.logBuffer.appendLog(n, array, n2, n3);
            }
            catch (LogBufferFullException ex) {
                this.printStackAndStopMaster(ex);
            }
            catch (IOException ex2) {
                this.printStackAndStopMaster(ex2);
            }
            catch (StandardException ex3) {
                this.printStackAndStopMaster(ex3);
            }
        }
    }
    
    public void flushedTo(final long n) {
        this.logShipper.flushedInstance(n);
    }
    
    private void setupConnection() throws StandardException {
        try {
            if (this.transmitter != null) {
                this.transmitter.tearDown();
            }
            this.transmitter = new ReplicationMessageTransmit(this.slaveAddr);
            if (this.logShipper != null && this.logShipper.getHighestShippedInstant() != -1L) {
                this.transmitter.initConnection(5000, this.logShipper.getHighestShippedInstant());
            }
            else {
                this.transmitter.initConnection(5000, this.logFactory.getFirstUnflushedInstantAsLong());
            }
        }
        catch (SocketTimeoutException ex4) {
            throw StandardException.newException("XRE06", this.dbname);
        }
        catch (IOException ex) {
            throw StandardException.newException("XRE04.C.1", ex, this.dbname, this.getHostName(), String.valueOf(this.getPortNumber()));
        }
        catch (StandardException ex2) {
            throw ex2;
        }
        catch (Exception ex3) {
            throw StandardException.newException("XRE04.C.1", ex3, this.dbname, this.getHostName(), String.valueOf(this.getPortNumber()));
        }
    }
    
    ReplicationMessageTransmit handleExceptions(final Exception ex) {
        Label_0127: {
            if (ex instanceof IOException) {
                this.repLogger.logError("R009", ex);
                Monitor.logTextMessage("R010", this.dbname);
                while (this.active) {
                    try {
                        this.transmitter = new ReplicationMessageTransmit(this.slaveAddr);
                        if (this.logShipper != null && this.logShipper.getHighestShippedInstant() != -1L) {
                            this.transmitter.initConnection(5000, this.logShipper.getHighestShippedInstant());
                        }
                        else {
                            this.transmitter.initConnection(5000, this.logFactory.getFirstUnflushedInstantAsLong());
                        }
                        break;
                    }
                    catch (SocketTimeoutException ex3) {
                        continue;
                    }
                    catch (IOException ex4) {
                        continue;
                    }
                    catch (Exception ex2) {
                        this.printStackAndStopMaster(ex2);
                        return null;
                    }
                    break Label_0127;
                }
                return this.transmitter;
            }
        }
        if (ex instanceof StandardException) {
            this.printStackAndStopMaster(ex);
            return null;
        }
        return this.transmitter;
    }
    
    private void printStackAndStopMaster(final Exception ex) {
        this.repLogger.logError("R009", ex);
        try {
            this.stopMaster();
        }
        catch (StandardException ex2) {
            this.repLogger.logError("R008", ex2);
        }
    }
    
    public void workToDo() {
        this.logShipper.workToDo();
    }
    
    private void teardownNetwork() {
        if (this.logShipper != null) {
            this.logShipper.stopLogShipment();
        }
        if (this.transmitter != null) {
            try {
                this.transmitter.sendMessage(new ReplicationMessage(20, null));
            }
            catch (IOException ex) {}
            try {
                this.transmitter.tearDown();
            }
            catch (IOException ex2) {}
        }
    }
    
    String getDbName() {
        return this.dbname;
    }
    
    private String getHostName() {
        return this.slaveAddr.getHostAddress().getHostName();
    }
    
    private int getPortNumber() {
        return this.slaveAddr.getPortNumber();
    }
}
