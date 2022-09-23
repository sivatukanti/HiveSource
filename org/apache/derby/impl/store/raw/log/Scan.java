// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.raw.log;

import org.apache.derby.iapi.store.raw.xact.TransactionId;
import org.apache.derby.iapi.services.io.ArrayInputStream;
import org.apache.derby.iapi.error.StandardException;
import java.io.IOException;
import org.apache.derby.iapi.store.raw.log.LogInstant;
import org.apache.derby.io.StorageRandomAccessFile;

public class Scan implements StreamLogScan
{
    public static final byte FORWARD = 1;
    public static final byte BACKWARD = 2;
    public static final byte BACKWARD_FROM_LOG_END = 4;
    private StorageRandomAccessFile scan;
    private LogToFile logFactory;
    private long currentLogFileNumber;
    private long currentLogFileLength;
    private long knownGoodLogEnd;
    private long currentInstant;
    private long stopAt;
    private byte scanDirection;
    private boolean fuzzyLogEnd;
    
    public Scan(final LogToFile logFactory, final long n, final LogInstant logInstant, final byte b) throws IOException, StandardException {
        this.fuzzyLogEnd = false;
        this.logFactory = logFactory;
        this.currentLogFileNumber = LogCounter.getLogFileNumber(n);
        this.currentLogFileLength = -1L;
        this.knownGoodLogEnd = 0L;
        this.currentInstant = 0L;
        if (logInstant != null) {
            this.stopAt = ((LogCounter)logInstant).getValueAsLong();
        }
        else {
            this.stopAt = 0L;
        }
        switch (b) {
            case 1: {
                this.scan = logFactory.getLogFileAtPosition(n);
                this.scanDirection = 1;
                this.currentLogFileLength = this.scan.length();
                break;
            }
            case 2: {
                (this.scan = logFactory.getLogFileAtPosition(n)).seek(this.scan.getFilePointer() + this.scan.readInt() + 16L - 4L);
                this.scanDirection = 2;
                break;
            }
            case 4: {
                this.scan = logFactory.getLogFileAtPosition(n);
                this.scanDirection = 2;
                break;
            }
        }
    }
    
    public LogRecord getNextRecord(final ArrayInputStream arrayInputStream, final TransactionId transactionId, final int n) throws StandardException {
        if (this.scan == null) {
            return null;
        }
        LogRecord logRecord = null;
        try {
            if (this.scanDirection == 2) {
                logRecord = this.getNextRecordBackward(arrayInputStream, transactionId, n);
            }
            else if (this.scanDirection == 1) {
                logRecord = this.getNextRecordForward(arrayInputStream, transactionId, n);
            }
            return logRecord;
        }
        catch (IOException ex) {
            throw this.logFactory.markCorrupt(StandardException.newException("XSLA3.D", ex));
        }
        catch (ClassNotFoundException ex2) {
            throw this.logFactory.markCorrupt(StandardException.newException("XSLA3.D", ex2));
        }
        finally {
            if (logRecord == null) {
                this.close();
            }
        }
    }
    
    private LogRecord getNextRecordBackward(final ArrayInputStream arrayInputStream, final TransactionId obj, final int n) throws StandardException, IOException, ClassNotFoundException {
        int n2 = LogRecord.formatOverhead() + LogRecord.maxGroupStoredSize();
        if (obj != null) {
            n2 += LogRecord.maxTransactionIdStoredSize(obj);
        }
        long n3 = this.scan.getFilePointer();
        int i;
        LogRecord logRecord;
        do {
            i = 1;
            logRecord = null;
            int n4 = -1;
            if (n3 == 24L) {
                if (this.stopAt != 0L && LogCounter.getLogFileNumber(this.stopAt) == this.currentLogFileNumber) {
                    return null;
                }
                this.scan.seek(16L);
                final long long1 = this.scan.readLong();
                this.scan.close();
                this.currentLogFileNumber = LogCounter.getLogFileNumber(long1);
                this.scan = this.logFactory.getLogFileAtPosition(long1);
                n3 = this.scan.getFilePointer();
                if (n3 == 24L) {
                    continue;
                }
            }
            this.scan.seek(n3 - 4L);
            final int int1 = this.scan.readInt();
            final long n5 = n3 - int1 - 16L;
            this.scan.seek(n5 + 4L);
            this.currentInstant = this.scan.readLong();
            if (this.currentInstant < this.stopAt && this.stopAt != 0L) {
                this.currentInstant = 0L;
                return null;
            }
            byte[] data = arrayInputStream.getData();
            if (data.length < int1) {
                data = new byte[int1];
                arrayInputStream.setData(data);
            }
            if (this.logFactory.databaseEncrypted()) {
                this.scan.readFully(data, 0, int1);
                this.logFactory.decrypt(data, 0, int1, data, 0);
                arrayInputStream.setLimit(0, int1);
            }
            else if (n == 0 && obj == null) {
                this.scan.readFully(data, 0, int1);
                arrayInputStream.setLimit(0, int1);
            }
            else {
                n4 = ((int1 > n2) ? n2 : int1);
                this.scan.readFully(data, 0, n4);
                arrayInputStream.setLimit(0, n4);
            }
            logRecord = (LogRecord)arrayInputStream.readObject();
            if (logRecord.isChecksum()) {
                i = 0;
            }
            else if (n != 0 || obj != null) {
                if (logRecord.isChecksum()) {
                    i = 0;
                }
                if (i != 0 && n != 0 && (n & logRecord.group()) == 0x0) {
                    i = 0;
                }
                if (i != 0 && obj != null && !logRecord.getTransactionId().equals(obj)) {
                    i = 0;
                }
                if (i != 0 && !this.logFactory.databaseEncrypted() && n4 < int1) {
                    final int position = arrayInputStream.getPosition();
                    this.scan.readFully(data, n4, int1 - n4);
                    arrayInputStream.setLimit(0, int1);
                    arrayInputStream.setPosition(position);
                }
            }
            n3 = n5;
            this.scan.seek(n3);
        } while (i == 0);
        return logRecord;
    }
    
    private LogRecord getNextRecordForward(final ArrayInputStream arrayInputStream, final TransactionId obj, final int n) throws StandardException, IOException, ClassNotFoundException {
        long n2 = this.scan.getFilePointer();
        int n3 = LogRecord.formatOverhead() + LogRecord.maxGroupStoredSize();
        if (obj != null) {
            n3 += LogRecord.maxTransactionIdStoredSize(obj);
        }
        int i;
        LogRecord logRecord;
        do {
            i = 1;
            int n4 = -1;
            if (n2 + 4L > this.currentLogFileLength) {
                if (n2 != this.currentLogFileLength) {
                    this.fuzzyLogEnd = true;
                }
                return null;
            }
            int n5;
            for (n5 = this.scan.readInt(); n5 == 0 || n2 + n5 + 16L > this.currentLogFileLength; n5 = this.scan.readInt()) {
                if (n5 != 0) {
                    this.fuzzyLogEnd = true;
                    this.scan.close();
                    this.scan = null;
                    return null;
                }
                if (this.stopAt != 0L && LogCounter.getLogFileNumber(this.stopAt) == this.currentLogFileNumber) {
                    return null;
                }
                this.scan.close();
                final LogToFile logFactory = this.logFactory;
                final long currentLogFileNumber = this.currentLogFileNumber + 1L;
                this.currentLogFileNumber = currentLogFileNumber;
                this.scan = logFactory.getLogFileAtBeginning(currentLogFileNumber);
                if (this.scan == null) {
                    return null;
                }
                n2 = this.scan.getFilePointer();
                this.scan.seek(16L);
                if (this.scan.readLong() != this.knownGoodLogEnd) {
                    return null;
                }
                this.scan.seek(n2);
                this.knownGoodLogEnd = LogCounter.makeLogInstantAsLong(this.currentLogFileNumber, n2);
                this.currentLogFileLength = this.scan.length();
                if (n2 + 4L >= this.currentLogFileLength) {
                    return null;
                }
            }
            this.currentInstant = this.scan.readLong();
            if (this.currentInstant < this.knownGoodLogEnd) {
                this.fuzzyLogEnd = true;
                return null;
            }
            if (this.stopAt != 0L && this.currentInstant > this.stopAt) {
                this.currentInstant = 0L;
                return null;
            }
            byte[] data = arrayInputStream.getData();
            if (data.length < n5) {
                data = new byte[n5];
                arrayInputStream.setData(data);
            }
            if (this.logFactory.databaseEncrypted()) {
                this.scan.readFully(data, 0, n5);
                arrayInputStream.setLimit(0, this.logFactory.decrypt(data, 0, n5, data, 0));
            }
            else if (n == 0 && obj == null) {
                this.scan.readFully(data, 0, n5);
                arrayInputStream.setLimit(0, n5);
            }
            else {
                n4 = ((n5 > n3) ? n3 : n5);
                this.scan.readFully(data, 0, n4);
                arrayInputStream.setLimit(0, n4);
            }
            logRecord = (LogRecord)arrayInputStream.readObject();
            if (n != 0 || obj != null) {
                if (n != 0 && (n & logRecord.group()) == 0x0) {
                    i = 0;
                }
                if (i != 0 && obj != null && !logRecord.getTransactionId().equals(obj)) {
                    i = 0;
                }
                if (i != 0 && !this.logFactory.databaseEncrypted() && n4 < n5) {
                    final int position = arrayInputStream.getPosition();
                    this.scan.readFully(data, n4, n5 - n4);
                    arrayInputStream.setLimit(0, n5);
                    arrayInputStream.setPosition(position);
                }
            }
            if (i == 0) {
                this.scan.seek(n2 - 4L);
            }
            final int int1 = this.scan.readInt();
            if (int1 != n5 && int1 < n5 && int1 < n5) {
                this.fuzzyLogEnd = true;
                return null;
            }
            n2 += n5 + 16;
            this.knownGoodLogEnd = LogCounter.makeLogInstantAsLong(this.currentLogFileNumber, n2);
            this.scan.seek(n2);
            if (!logRecord.isChecksum()) {
                continue;
            }
            i = 0;
            final ChecksumOperation checksumOperation = (ChecksumOperation)logRecord.getLoggable();
            final int dataLength = checksumOperation.getDataLength();
            if (data.length < dataLength) {
                data = new byte[dataLength];
                arrayInputStream.setData(data);
                arrayInputStream.setLimit(0, dataLength);
            }
            boolean b = false;
            if (n2 + dataLength <= this.currentLogFileLength) {
                this.scan.readFully(data, 0, dataLength);
                if (checksumOperation.isChecksumValid(data, 0, dataLength)) {
                    b = true;
                }
            }
            if (!b) {
                this.fuzzyLogEnd = true;
                this.scan.close();
                this.scan = null;
                return null;
            }
            this.scan.seek(n2);
        } while (i == 0);
        return logRecord;
    }
    
    public void resetPosition(final LogInstant logInstant) throws IOException, StandardException {
        final long valueAsLong = ((LogCounter)logInstant).getValueAsLong();
        if (valueAsLong == 0L || (this.stopAt != 0L && this.scanDirection == 1 && valueAsLong > this.stopAt) || (this.scanDirection == 1 && valueAsLong < this.stopAt)) {
            this.close();
            throw StandardException.newException("XSLB8.S", logInstant, new LogCounter(this.stopAt));
        }
        final long logFileNumber = ((LogCounter)logInstant).getLogFileNumber();
        if (logFileNumber != this.currentLogFileNumber) {
            this.scan.close();
            this.scan = this.logFactory.getLogFileAtPosition(valueAsLong);
            this.currentLogFileNumber = logFileNumber;
            if (this.scanDirection == 1) {
                this.currentLogFileLength = this.scan.length();
            }
        }
        else {
            this.scan.seek(((LogCounter)logInstant).getLogFilePosition());
            this.currentLogFileLength = this.scan.length();
        }
        this.currentInstant = valueAsLong;
        this.knownGoodLogEnd = this.currentInstant;
    }
    
    public long getInstant() {
        return this.currentInstant;
    }
    
    public long getLogRecordEnd() {
        return this.knownGoodLogEnd;
    }
    
    public boolean isLogEndFuzzy() {
        return this.fuzzyLogEnd;
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
        this.logFactory = null;
        this.currentLogFileNumber = -1L;
        this.currentLogFileLength = -1L;
        this.currentInstant = 0L;
        this.stopAt = 0L;
        this.scanDirection = 0;
    }
}
