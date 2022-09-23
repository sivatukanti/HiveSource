// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.raw.log;

import org.apache.derby.iapi.services.i18n.MessageService;
import org.apache.derby.iapi.store.raw.log.LogInstant;
import org.apache.derby.iapi.store.raw.xact.TransactionId;
import org.apache.derby.iapi.services.io.ArrayInputStream;
import java.io.IOException;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.io.StorageRandomAccessFile;

public class FlushedScan implements StreamLogScan
{
    private StorageRandomAccessFile scan;
    LogToFile logFactory;
    boolean open;
    long currentLogFileNumber;
    long currentLogFileFirstUnflushedPosition;
    long currentInstant;
    long firstUnflushed;
    long firstUnflushedFileNumber;
    long firstUnflushedFilePosition;
    static final int LOG_REC_LEN_BYTE_LENGTH = 4;
    int nextRecordLength;
    boolean readNextRecordLength;
    
    public FlushedScan(final LogToFile logFactory, final long n) throws StandardException {
        this.firstUnflushed = -1L;
        try {
            this.currentLogFileNumber = LogCounter.getLogFileNumber(n);
            this.logFactory = logFactory;
            this.scan = logFactory.getLogFileAtPosition(n);
            this.setFirstUnflushed();
            this.open = true;
            this.currentInstant = 0L;
        }
        catch (IOException ex) {
            throw logFactory.markCorrupt(StandardException.newException("XSLA2.D", ex));
        }
    }
    
    public LogRecord getNextRecord(final ArrayInputStream arrayInputStream, final TransactionId obj, final int n) throws StandardException {
        try {
            int n2 = LogRecord.formatOverhead() + LogRecord.maxGroupStoredSize();
            if (obj != null) {
                n2 += LogRecord.maxTransactionIdStoredSize(obj);
            }
            while (this.open && this.positionToNextRecord()) {
                int n3 = 1;
                int n4 = -1;
                this.currentInstant = this.scan.readLong();
                byte[] data = arrayInputStream.getData();
                if (data.length < this.nextRecordLength) {
                    data = new byte[this.nextRecordLength];
                    arrayInputStream.setData(data);
                }
                if (this.logFactory.databaseEncrypted()) {
                    this.scan.readFully(data, 0, this.nextRecordLength);
                    arrayInputStream.setLimit(0, this.logFactory.decrypt(data, 0, this.nextRecordLength, data, 0));
                }
                else if (n == 0 && obj == null) {
                    this.scan.readFully(data, 0, this.nextRecordLength);
                    arrayInputStream.setLimit(0, this.nextRecordLength);
                }
                else {
                    n4 = ((this.nextRecordLength > n2) ? n2 : this.nextRecordLength);
                    this.scan.readFully(data, 0, n4);
                    arrayInputStream.setLimit(0, n4);
                }
                final LogRecord logRecord = (LogRecord)arrayInputStream.readObject();
                if (n != 0 || obj != null) {
                    if (n != 0 && (n & logRecord.group()) == 0x0) {
                        n3 = 0;
                    }
                    if (n3 != 0 && obj != null && !logRecord.getTransactionId().equals(obj)) {
                        n3 = 0;
                    }
                    if (n3 != 0 && !this.logFactory.databaseEncrypted() && n4 < this.nextRecordLength) {
                        final int position = arrayInputStream.getPosition();
                        this.scan.readFully(data, n4, this.nextRecordLength - n4);
                        arrayInputStream.setLimit(0, this.nextRecordLength);
                        arrayInputStream.setPosition(position);
                    }
                }
                if (n3 != 0 || this.logFactory.databaseEncrypted()) {
                    this.scan.readInt();
                }
                else {
                    this.scan.seek(LogCounter.getLogFilePosition(this.currentInstant) + this.nextRecordLength + 16L);
                }
                if (n3 != 0) {
                    return logRecord;
                }
            }
            return null;
        }
        catch (ClassNotFoundException ex) {
            throw this.logFactory.markCorrupt(StandardException.newException("XSLA3.D", ex));
        }
        catch (IOException ex2) {
            throw this.logFactory.markCorrupt(StandardException.newException("XSLA2.D", ex2));
        }
    }
    
    public void resetPosition(final LogInstant logInstant) throws IOException {
    }
    
    public long getLogRecordEnd() {
        return 0L;
    }
    
    public boolean isLogEndFuzzy() {
        return false;
    }
    
    public long getInstant() {
        return this.currentInstant;
    }
    
    public LogInstant getLogInstant() {
        if (this.currentInstant == 0L) {
            return null;
        }
        return new LogCounter(this.currentInstant);
    }
    
    public void close() {
        if (this.scan != null) {
            try {
                this.scan.close();
            }
            catch (IOException ex) {}
            this.scan = null;
        }
        this.currentInstant = 0L;
        this.open = false;
    }
    
    private void setFirstUnflushed() throws StandardException, IOException {
        this.firstUnflushed = ((LogCounter)this.logFactory.getFirstUnflushedInstant()).getValueAsLong();
        this.firstUnflushedFileNumber = LogCounter.getLogFileNumber(this.firstUnflushed);
        this.firstUnflushedFilePosition = LogCounter.getLogFilePosition(this.firstUnflushed);
        this.setCurrentLogFileFirstUnflushedPosition();
    }
    
    private void setCurrentLogFileFirstUnflushedPosition() throws IOException {
        if (this.currentLogFileNumber == this.firstUnflushedFileNumber) {
            this.currentLogFileFirstUnflushedPosition = this.firstUnflushedFilePosition;
        }
        else {
            if (this.currentLogFileNumber >= this.firstUnflushedFileNumber) {
                throw new IOException(MessageService.getTextMessage("L014"));
            }
            this.currentLogFileFirstUnflushedPosition = this.scan.length();
        }
    }
    
    private void switchLogFile() throws StandardException {
        try {
            this.readNextRecordLength = false;
            this.scan.close();
            this.scan = null;
            final LogToFile logFactory = this.logFactory;
            final long currentLogFileNumber = this.currentLogFileNumber + 1L;
            this.currentLogFileNumber = currentLogFileNumber;
            this.scan = logFactory.getLogFileAtBeginning(currentLogFileNumber);
            this.setCurrentLogFileFirstUnflushedPosition();
        }
        catch (IOException ex) {
            throw this.logFactory.markCorrupt(StandardException.newException("XSLA2.D", ex));
        }
    }
    
    private boolean currentLogFileHasUnflushedRecord() throws IOException {
        long filePointer = this.scan.getFilePointer();
        if (!this.readNextRecordLength) {
            if (filePointer + 4L > this.currentLogFileFirstUnflushedPosition) {
                return false;
            }
            this.nextRecordLength = this.scan.readInt();
            filePointer += 4L;
            this.readNextRecordLength = true;
        }
        if (this.nextRecordLength == 0) {
            return false;
        }
        if (filePointer + (this.nextRecordLength + 4) > this.currentLogFileFirstUnflushedPosition) {
            return false;
        }
        this.readNextRecordLength = false;
        return true;
    }
    
    private boolean positionToNextRecord() throws StandardException, IOException {
        if (this.currentLogFileHasUnflushedRecord()) {
            return true;
        }
        this.setFirstUnflushed();
        if (this.currentLogFileHasUnflushedRecord()) {
            return true;
        }
        while (this.currentLogFileNumber < this.firstUnflushedFileNumber) {
            this.switchLogFile();
            if (this.currentLogFileHasUnflushedRecord()) {
                return true;
            }
        }
        this.currentInstant = 0L;
        return false;
    }
}
