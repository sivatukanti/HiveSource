// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.raw.log;

import java.io.SyncFailedException;
import org.apache.derby.iapi.util.InterruptStatus;
import java.io.IOException;
import org.apache.derby.iapi.error.StandardException;
import java.io.OutputStream;
import org.apache.derby.iapi.store.raw.Loggable;
import org.apache.derby.iapi.store.raw.xact.TransactionId;
import org.apache.derby.iapi.services.io.FormatIdOutputStream;
import org.apache.derby.iapi.services.io.ArrayOutputStream;
import org.apache.derby.iapi.store.replication.master.MasterFactory;
import org.apache.derby.io.StorageRandomAccessFile;
import java.util.LinkedList;

public class LogAccessFile
{
    private static final int LOG_RECORD_FIXED_OVERHEAD_SIZE = 16;
    private static final int LOG_RECORD_HEADER_SIZE = 12;
    private static final int LOG_RECORD_TRAILER_SIZE = 4;
    private static final int LOG_NUMBER_LOG_BUFFERS = 3;
    private LinkedList freeBuffers;
    private LinkedList dirtyBuffers;
    private LogAccessFileBuffer currentBuffer;
    private boolean flushInProgress;
    private final StorageRandomAccessFile log;
    private final Object logFileSemaphore;
    static int mon_numWritesToLog;
    static int mon_numBytesToLog;
    MasterFactory masterFac;
    boolean inReplicationMasterMode;
    boolean inReplicationSlaveMode;
    private ArrayOutputStream logOutputBuffer;
    private FormatIdOutputStream logicalOut;
    private long checksumInstant;
    private int checksumLength;
    private int checksumLogRecordSize;
    private boolean writeChecksum;
    private ChecksumOperation checksumLogOperation;
    private LogRecord checksumLogRecord;
    private LogToFile logFactory;
    private boolean databaseEncrypted;
    
    public LogAccessFile(final LogToFile logFactory, final StorageRandomAccessFile storageRandomAccessFile, final int n) {
        this.flushInProgress = false;
        this.inReplicationMasterMode = false;
        this.inReplicationSlaveMode = false;
        this.checksumInstant = -1L;
        this.databaseEncrypted = false;
        logFactory.checkForReplication(this);
        this.log = storageRandomAccessFile;
        this.logFileSemaphore = storageRandomAccessFile;
        this.logFactory = logFactory;
        this.freeBuffers = new LinkedList();
        this.dirtyBuffers = new LinkedList();
        for (int i = 0; i < 3; ++i) {
            this.freeBuffers.addLast(new LogAccessFileBuffer(n));
        }
        this.currentBuffer = this.freeBuffers.removeFirst();
        this.writeChecksum = logFactory.checkVersion(10, 1);
        if (this.inReplicationSlaveMode) {
            this.writeChecksum = false;
        }
        if (this.writeChecksum) {
            (this.checksumLogOperation = new ChecksumOperation()).init();
            (this.checksumLogRecord = new LogRecord()).setValue(null, this.checksumLogOperation);
            final LogRecord checksumLogRecord = this.checksumLogRecord;
            this.checksumLength = LogRecord.getStoredSize(this.checksumLogOperation.group(), null) + this.checksumLogOperation.getStoredSize();
            if (logFactory.databaseEncrypted()) {
                this.checksumLength = logFactory.getEncryptedDataLength(this.checksumLength);
                this.databaseEncrypted = true;
            }
            this.checksumLogRecordSize = this.checksumLength + 16;
            this.logOutputBuffer = new ArrayOutputStream();
            this.logicalOut = new FormatIdOutputStream(this.logOutputBuffer);
        }
        else {
            this.checksumLogRecordSize = 0;
        }
        this.currentBuffer.init(this.checksumLogRecordSize);
    }
    
    public void writeLogRecord(final int n, final long greatest_instant, final byte[] array, final int n2, final byte[] array2, final int n3, final int n4) throws StandardException, IOException {
        final int n5 = n + 16;
        if (n5 <= this.currentBuffer.bytes_free) {
            this.currentBuffer.position = this.appendLogRecordToBuffer(this.currentBuffer.buffer, this.currentBuffer.position, n, greatest_instant, array, n2, array2, n3, n4);
            final LogAccessFileBuffer currentBuffer = this.currentBuffer;
            currentBuffer.bytes_free -= n5;
            this.currentBuffer.greatest_instant = greatest_instant;
        }
        else {
            final int n6 = this.checksumLogRecordSize + n5;
            final byte[] array3 = new byte[n6];
            this.appendLogRecordToBuffer(array3, this.checksumLogRecordSize, n, greatest_instant, array, n2, array2, n3, n4);
            if (this.writeChecksum) {
                this.checksumLogOperation.reset();
                this.checksumLogOperation.update(array3, this.checksumLogRecordSize, n5);
                this.writeChecksumLogRecord(array3);
            }
            this.flushLogAccessFile();
            this.writeToLog(array3, 0, n6, greatest_instant);
        }
    }
    
    private int appendLogRecordToBuffer(final byte[] array, int n, final int n2, final long n3, final byte[] array2, final int n4, final byte[] array3, final int n5, final int n6) {
        n = this.writeInt(n2, array, n);
        n = this.writeLong(n3, array, n);
        final int n7 = n2 - n6;
        System.arraycopy(array2, n4, array, n, n7);
        n += n7;
        if (n6 != 0) {
            System.arraycopy(array3, n5, array, n, n6);
            n += n6;
        }
        n = this.writeInt(n2, array, n);
        return n;
    }
    
    private final int writeInt(final int n, final byte[] array, int n2) {
        array[n2++] = (byte)(n >>> 24 & 0xFF);
        array[n2++] = (byte)(n >>> 16 & 0xFF);
        array[n2++] = (byte)(n >>> 8 & 0xFF);
        array[n2++] = (byte)(n & 0xFF);
        return n2;
    }
    
    private final int writeLong(final long n, final byte[] array, int n2) {
        array[n2++] = (byte)((int)(n >>> 56) & 0xFF);
        array[n2++] = (byte)((int)(n >>> 48) & 0xFF);
        array[n2++] = (byte)((int)(n >>> 40) & 0xFF);
        array[n2++] = (byte)((int)(n >>> 32) & 0xFF);
        array[n2++] = (byte)((int)(n >>> 24) & 0xFF);
        array[n2++] = (byte)((int)(n >>> 16) & 0xFF);
        array[n2++] = (byte)((int)(n >>> 8) & 0xFF);
        array[n2++] = (byte)((int)n & 0xFF);
        return n2;
    }
    
    public void writeInt(final int n) {
        this.currentBuffer.position = this.writeInt(n, this.currentBuffer.buffer, this.currentBuffer.position);
        final LogAccessFileBuffer currentBuffer = this.currentBuffer;
        currentBuffer.bytes_free -= 4;
    }
    
    public void writeLong(final long n) {
        this.currentBuffer.position = this.writeLong(n, this.currentBuffer.buffer, this.currentBuffer.position);
        final LogAccessFileBuffer currentBuffer = this.currentBuffer;
        currentBuffer.bytes_free -= 8;
    }
    
    public void write(final int n) {
        this.currentBuffer.buffer[this.currentBuffer.position++] = (byte)n;
        final LogAccessFileBuffer currentBuffer = this.currentBuffer;
        --currentBuffer.bytes_free;
    }
    
    public void write(final byte[] array, final int n, final int n2) {
        System.arraycopy(array, n, this.currentBuffer.buffer, this.currentBuffer.position, n2);
        final LogAccessFileBuffer currentBuffer = this.currentBuffer;
        currentBuffer.bytes_free -= n2;
        final LogAccessFileBuffer currentBuffer2 = this.currentBuffer;
        currentBuffer2.position += n2;
    }
    
    protected void flushDirtyBuffers() throws IOException {
        LogAccessFileBuffer e = null;
        int i = 0;
        try {
            int size;
            synchronized (this) {
                while (this.flushInProgress) {
                    try {
                        this.wait();
                    }
                    catch (InterruptedException ex) {
                        InterruptStatus.setInterrupted();
                    }
                }
                size = this.dirtyBuffers.size();
                if (size > 0) {
                    e = this.dirtyBuffers.removeFirst();
                }
                this.flushInProgress = true;
            }
            while (i < size) {
                if (e.position != 0) {
                    this.writeToLog(e.buffer, 0, e.position, e.greatest_instant);
                }
                ++i;
                synchronized (this) {
                    this.freeBuffers.addLast(e);
                    if (i < size) {
                        e = this.dirtyBuffers.removeFirst();
                    }
                    else {
                        final int size2 = this.dirtyBuffers.size();
                        if (size2 <= 0 || i > 3) {
                            continue;
                        }
                        size += size2;
                        e = this.dirtyBuffers.removeFirst();
                    }
                }
            }
        }
        finally {
            synchronized (this) {
                this.flushInProgress = false;
                this.notifyAll();
            }
        }
    }
    
    public void flushLogAccessFile() throws IOException, StandardException {
        this.switchLogBuffer();
        this.flushDirtyBuffers();
    }
    
    public void switchLogBuffer() throws IOException, StandardException {
        synchronized (this) {
            if (this.currentBuffer.position == this.checksumLogRecordSize) {
                return;
            }
            if (this.writeChecksum) {
                this.checksumLogOperation.reset();
                this.checksumLogOperation.update(this.currentBuffer.buffer, this.checksumLogRecordSize, this.currentBuffer.position - this.checksumLogRecordSize);
                this.writeChecksumLogRecord(this.currentBuffer.buffer);
            }
            this.dirtyBuffers.addLast(this.currentBuffer);
            if (this.freeBuffers.size() == 0) {
                this.flushDirtyBuffers();
            }
            (this.currentBuffer = this.freeBuffers.removeFirst()).init(this.checksumLogRecordSize);
        }
    }
    
    public void syncLogAccessFile() throws IOException, StandardException {
        int n = 0;
        while (true) {
            try {
                synchronized (this) {
                    this.log.sync();
                }
            }
            catch (SyncFailedException ex) {
                ++n;
                try {
                    Thread.sleep(200L);
                }
                catch (InterruptedException ex2) {
                    InterruptStatus.setInterrupted();
                }
                if (n > 20) {
                    throw StandardException.newException("XSLA4.D", ex);
                }
                continue;
            }
            break;
        }
    }
    
    public void corrupt() throws IOException {
        synchronized (this.logFileSemaphore) {
            if (this.log != null) {
                this.log.close();
            }
        }
    }
    
    public void close() throws IOException, StandardException {
        this.flushLogAccessFile();
        synchronized (this.logFileSemaphore) {
            if (this.log != null) {
                this.log.close();
            }
        }
    }
    
    protected void setReplicationMasterRole(final MasterFactory masterFac) {
        this.masterFac = masterFac;
        this.inReplicationMasterMode = true;
    }
    
    protected void stopReplicationMasterRole() {
        this.inReplicationMasterMode = false;
        this.masterFac = null;
    }
    
    protected void setReplicationSlaveRole() {
        this.inReplicationSlaveMode = true;
    }
    
    private void writeToLog(final byte[] array, final int n, final int n2, final long n3) throws IOException {
        synchronized (this.logFileSemaphore) {
            if (this.log != null) {
                int n4 = 0;
                while (true) {
                    try {
                        this.log.write(array, n, n2);
                        if (this.inReplicationMasterMode) {
                            this.masterFac.appendLog(n3, array, n, n2);
                        }
                    }
                    catch (IOException ex) {
                        if (n4 >= 5) {
                            throw ex;
                        }
                        ++n4;
                        continue;
                    }
                    break;
                }
            }
        }
    }
    
    protected long reserveSpaceForChecksum(final int n, final long n2, final long n3) throws StandardException, IOException {
        final int n4 = n + 16;
        boolean b = false;
        if (this.currentBuffer.position == this.checksumLogRecordSize) {
            b = this.writeChecksum;
        }
        else if (n4 > this.currentBuffer.bytes_free) {
            this.switchLogBuffer();
            b = this.writeChecksum;
        }
        if (b) {
            this.checksumInstant = LogCounter.makeLogInstantAsLong(n2, n3);
            return this.checksumLogRecordSize;
        }
        return 0L;
    }
    
    private void writeChecksumLogRecord(final byte[] data) throws IOException, StandardException {
        final int writeLong = this.writeLong(this.checksumInstant, data, this.writeInt(this.checksumLength, data, 0));
        this.logOutputBuffer.setData(data);
        this.logOutputBuffer.setPosition(writeLong);
        this.logicalOut.writeObject(this.checksumLogRecord);
        if (this.databaseEncrypted) {
            this.logFactory.encrypt(data, 12, this.checksumLength, data, 12);
        }
        this.writeInt(this.checksumLength, data, 12 + this.checksumLength);
    }
    
    public int getChecksumLogRecordSize() {
        return this.checksumLogRecordSize;
    }
    
    protected void writeEndMarker(final int n) throws IOException, StandardException {
        this.flushLogAccessFile();
        final byte[] buffer = this.currentBuffer.buffer;
        this.writeToLog(buffer, 0, this.writeInt(n, buffer, 0), -1L);
    }
}
