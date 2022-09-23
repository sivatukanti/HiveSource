// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.replication.master;

import org.apache.derby.iapi.services.property.PropertyUtil;
import java.util.NoSuchElementException;
import org.apache.derby.iapi.error.StandardException;
import java.io.IOException;
import org.apache.derby.iapi.util.InterruptStatus;
import org.apache.derby.impl.store.replication.ReplicationLogger;
import org.apache.derby.impl.store.replication.net.ReplicationMessage;
import org.apache.derby.impl.store.replication.net.ReplicationMessageTransmit;
import org.apache.derby.impl.store.replication.buffer.ReplicationLogBuffer;

public class AsynchronousLogShipper extends Thread implements LogShipper
{
    private final ReplicationLogBuffer logBuffer;
    private ReplicationMessageTransmit transmitter;
    private long shippingInterval;
    private long minShippingInterval;
    private long maxShippingInterval;
    private long lastShippingTime;
    private volatile boolean stopShipping;
    private MasterController masterController;
    private Object objLSTSync;
    private Object forceFlushSemaphore;
    public static final int DEFAULT_FORCEFLUSH_TIMEOUT = 5000;
    private ReplicationMessage failedChunk;
    private long failedChunkHighestInstant;
    private long highestShippedInstant;
    private static final int FI_LOW = 10;
    private static final int FI_HIGH = 80;
    private static final long MIN = 100L;
    private static final long MAX = 5000L;
    private final ReplicationLogger repLogger;
    
    public AsynchronousLogShipper(final ReplicationLogBuffer logBuffer, final ReplicationMessageTransmit transmitter, final MasterController masterController, final ReplicationLogger repLogger) {
        super("derby.master.logger-" + masterController.getDbName());
        this.stopShipping = false;
        this.masterController = null;
        this.objLSTSync = new Object();
        this.forceFlushSemaphore = new Object();
        this.failedChunk = null;
        this.failedChunkHighestInstant = -1L;
        this.highestShippedInstant = -1L;
        this.logBuffer = logBuffer;
        this.transmitter = transmitter;
        this.masterController = masterController;
        this.stopShipping = false;
        this.repLogger = repLogger;
        this.getLogShipperProperties();
        this.shippingInterval = this.minShippingInterval;
        this.lastShippingTime = System.currentTimeMillis();
    }
    
    public void run() {
        while (!this.stopShipping) {
            try {
                synchronized (this.forceFlushSemaphore) {
                    this.shipALogChunk();
                    this.forceFlushSemaphore.notify();
                }
                this.shippingInterval = this.calculateSIfromFI();
                if (this.shippingInterval == -1L) {
                    continue;
                }
                synchronized (this.objLSTSync) {
                    this.objLSTSync.wait(this.shippingInterval);
                }
            }
            catch (InterruptedException ex3) {
                InterruptStatus.setInterrupted();
            }
            catch (IOException ex) {
                this.transmitter = this.masterController.handleExceptions(ex);
                if (this.transmitter != null) {
                    continue;
                }
                continue;
            }
            catch (StandardException ex2) {
                this.masterController.handleExceptions(ex2);
            }
        }
    }
    
    private synchronized boolean shipALogChunk() throws IOException, StandardException {
        ReplicationMessage failedChunk = null;
        try {
            if (this.failedChunk != null) {
                this.transmitter.sendMessage(this.failedChunk);
                this.highestShippedInstant = this.failedChunkHighestInstant;
                this.failedChunk = null;
            }
            if (this.logBuffer.next()) {
                failedChunk = new ReplicationMessage(10, this.logBuffer.getData());
                this.transmitter.sendMessage(failedChunk);
                this.highestShippedInstant = this.logBuffer.getLastInstant();
                this.lastShippingTime = System.currentTimeMillis();
                return true;
            }
        }
        catch (NoSuchElementException ex) {
            this.masterController.handleExceptions(StandardException.newException("XRE03", ex));
        }
        catch (IOException ex2) {
            if (failedChunk != null) {
                this.failedChunk = failedChunk;
                this.failedChunkHighestInstant = this.logBuffer.getLastInstant();
            }
            throw ex2;
        }
        return false;
    }
    
    public void flushBuffer() throws IOException, StandardException {
        while (this.shipALogChunk()) {}
    }
    
    public void forceFlush() throws IOException, StandardException {
        if (this.stopShipping) {
            return;
        }
        synchronized (this.forceFlushSemaphore) {
            synchronized (this.objLSTSync) {
                this.objLSTSync.notify();
            }
            try {
                this.forceFlushSemaphore.wait(5000L);
            }
            catch (InterruptedException ex) {
                InterruptStatus.setInterrupted();
            }
        }
    }
    
    public long getHighestShippedInstant() {
        return this.highestShippedInstant;
    }
    
    public void flushedInstance(final long n) {
    }
    
    public void stopLogShipment() {
        this.stopShipping = true;
    }
    
    public void workToDo() {
        if (this.logBuffer.getFillInformation() >= 80 || System.currentTimeMillis() - this.lastShippingTime > this.minShippingInterval) {
            synchronized (this.objLSTSync) {
                this.objLSTSync.notify();
            }
        }
    }
    
    private long calculateSIfromFI() {
        final int fillInformation = this.logBuffer.getFillInformation();
        long n;
        if (fillInformation >= 80) {
            n = -1L;
        }
        else if (fillInformation > 10 && fillInformation < 80) {
            n = this.minShippingInterval;
        }
        else {
            n = this.maxShippingInterval;
        }
        return n;
    }
    
    private void getLogShipperProperties() {
        this.minShippingInterval = PropertyUtil.getSystemInt("derby.replication.minLogShippingInterval", 100);
        this.maxShippingInterval = PropertyUtil.getSystemInt("derby.replication.maxLogShippingInterval", 5000);
        final int n = 10;
        if (this.minShippingInterval > this.maxShippingInterval / n) {
            this.minShippingInterval = this.maxShippingInterval / n;
        }
    }
}
