// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.raw.log;

import org.apache.derby.iapi.services.i18n.MessageService;
import org.apache.derby.iapi.store.raw.xact.TransactionFactory;
import org.apache.derby.iapi.store.raw.Undoable;
import org.apache.derby.iapi.store.raw.RePreparable;
import org.apache.derby.iapi.store.access.DatabaseInstant;
import org.apache.derby.iapi.store.raw.xact.TransactionId;
import org.apache.derby.iapi.store.raw.Compensation;
import org.apache.derby.iapi.util.ByteArray;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.services.io.LimitObjectInput;
import org.apache.derby.iapi.store.raw.Transaction;
import org.apache.derby.iapi.store.raw.log.LogInstant;
import org.apache.derby.iapi.store.raw.Loggable;
import org.apache.derby.iapi.store.raw.xact.RawTransaction;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.derby.iapi.services.io.ArrayInputStream;
import org.apache.derby.iapi.services.io.FormatIdOutputStream;
import org.apache.derby.iapi.services.io.DynamicByteArrayOutputStream;
import org.apache.derby.iapi.store.raw.log.Logger;

public class FileLogger implements Logger
{
    private LogRecord logRecord;
    protected byte[] encryptionBuffer;
    private DynamicByteArrayOutputStream logOutputBuffer;
    private FormatIdOutputStream logicalOut;
    private ArrayInputStream logIn;
    private LogToFile logFactory;
    
    public FileLogger(final LogToFile logFactory) {
        this.logFactory = logFactory;
        this.logOutputBuffer = new DynamicByteArrayOutputStream(1024);
        this.logicalOut = new FormatIdOutputStream(this.logOutputBuffer);
        this.logIn = new ArrayInputStream();
        this.logRecord = new LogRecord();
    }
    
    public void close() throws IOException {
        if (this.logOutputBuffer != null) {
            this.logOutputBuffer.close();
            this.logOutputBuffer = null;
        }
        this.logIn = null;
        this.logFactory = null;
        this.logicalOut = null;
        this.logRecord = null;
    }
    
    public synchronized LogInstant logAndDo(final RawTransaction rawTransaction, final Loggable loggable) throws StandardException {
        boolean b = false;
        try {
            this.logOutputBuffer.reset();
            this.logRecord.setValue(rawTransaction.getId(), loggable);
            b = true;
            this.logicalOut.writeObject(this.logRecord);
            b = false;
            int offset = 0;
            final ByteArray preparedLog = loggable.getPreparedLog();
            byte[] array;
            int length;
            if (preparedLog != null) {
                array = preparedLog.getArray();
                length = preparedLog.getLength();
                offset = preparedLog.getOffset();
                this.logIn.setData(array);
                this.logIn.setPosition(offset);
                this.logIn.setLimit(length);
            }
            else {
                array = null;
                length = 0;
            }
            this.logicalOut.writeInt(length);
            final int n = this.logOutputBuffer.getPosition() + length;
            LogInstant logInstant = null;
            int n2 = 0;
            try {
                if (this.logFactory.databaseEncrypted()) {
                    n2 = n;
                    if (n2 % this.logFactory.getEncryptionBlockSize() != 0) {
                        n2 = n2 + this.logFactory.getEncryptionBlockSize() - n2 % this.logFactory.getEncryptionBlockSize();
                    }
                    if (this.encryptionBuffer == null || this.encryptionBuffer.length < n2) {
                        this.encryptionBuffer = new byte[n2];
                    }
                    System.arraycopy(this.logOutputBuffer.getByteArray(), 0, this.encryptionBuffer, 0, n - length);
                    if (length > 0) {
                        System.arraycopy(array, offset, this.encryptionBuffer, n - length, length);
                    }
                    this.logFactory.encrypt(this.encryptionBuffer, 0, n2, this.encryptionBuffer, 0);
                }
                if ((loggable.group() & 0x3) != 0x0) {
                    synchronized (this.logFactory) {
                        long n3;
                        if (this.logFactory.databaseEncrypted()) {
                            n3 = this.logFactory.appendLogRecord(this.encryptionBuffer, 0, n2, null, -1, 0);
                        }
                        else {
                            n3 = this.logFactory.appendLogRecord(this.logOutputBuffer.getByteArray(), 0, n, array, offset, length);
                        }
                        logInstant = new LogCounter(n3);
                        loggable.doMe(rawTransaction, logInstant, this.logIn);
                    }
                }
                else {
                    long n4;
                    if (this.logFactory.databaseEncrypted()) {
                        n4 = this.logFactory.appendLogRecord(this.encryptionBuffer, 0, n2, null, -1, 0);
                    }
                    else {
                        n4 = this.logFactory.appendLogRecord(this.logOutputBuffer.getByteArray(), 0, n, array, offset, length);
                    }
                    logInstant = new LogCounter(n4);
                    loggable.doMe(rawTransaction, logInstant, this.logIn);
                }
            }
            catch (StandardException ex) {
                throw this.logFactory.markCorrupt(StandardException.newException("XSLA1.D", ex, loggable));
            }
            catch (IOException ex2) {
                throw this.logFactory.markCorrupt(StandardException.newException("XSLA1.D", ex2, loggable));
            }
            finally {
                this.logIn.clearLimit();
            }
            return logInstant;
        }
        catch (IOException ex3) {
            if (b) {
                throw StandardException.newException("XSLB1.S", ex3, loggable);
            }
            throw StandardException.newException("XSLB2.S", ex3, loggable);
        }
    }
    
    public LogInstant logAndUndo(final RawTransaction rawTransaction, final Compensation compensation, final LogInstant logInstant, final LimitObjectInput limitObjectInput) throws StandardException {
        boolean b = false;
        try {
            this.logOutputBuffer.reset();
            this.logRecord.setValue(rawTransaction.getId(), compensation);
            b = true;
            this.logicalOut.writeObject(this.logRecord);
            b = false;
            this.logicalOut.writeLong(((LogCounter)logInstant).getValueAsLong());
            final int position = this.logOutputBuffer.getPosition();
            long n2;
            if (this.logFactory.databaseEncrypted()) {
                int n = position;
                if (n % this.logFactory.getEncryptionBlockSize() != 0) {
                    n = n + this.logFactory.getEncryptionBlockSize() - n % this.logFactory.getEncryptionBlockSize();
                }
                if (this.encryptionBuffer == null || this.encryptionBuffer.length < n) {
                    this.encryptionBuffer = new byte[n];
                }
                System.arraycopy(this.logOutputBuffer.getByteArray(), 0, this.encryptionBuffer, 0, position);
                this.logFactory.encrypt(this.encryptionBuffer, 0, n, this.encryptionBuffer, 0);
                n2 = this.logFactory.appendLogRecord(this.encryptionBuffer, 0, n, null, 0, 0);
            }
            else {
                n2 = this.logFactory.appendLogRecord(this.logOutputBuffer.getByteArray(), 0, position, null, 0, 0);
            }
            final LogCounter logCounter = new LogCounter(n2);
            try {
                compensation.doMe(rawTransaction, logCounter, limitObjectInput);
            }
            catch (StandardException ex) {
                throw this.logFactory.markCorrupt(StandardException.newException("XSLA1.D", ex, compensation));
            }
            catch (IOException ex2) {
                throw this.logFactory.markCorrupt(StandardException.newException("XSLA1.D", ex2, compensation));
            }
            return logCounter;
        }
        catch (IOException ex3) {
            if (b) {
                throw StandardException.newException("XSLB1.S", ex3, compensation);
            }
            throw StandardException.newException("XSLB2.S", ex3, compensation);
        }
    }
    
    public void flush(final LogInstant logInstant) throws StandardException {
        this.logFactory.flush(logInstant);
    }
    
    public void flushAll() throws StandardException {
        this.logFactory.flushAll();
    }
    
    public void reprepare(final RawTransaction rawTransaction, final TransactionId transactionId, final LogInstant logInstant, final LogInstant logInstant2) throws StandardException {
        int n = 0;
        int n2 = 0;
        RePreparable rePreparable = null;
        ArrayInputStream arrayInputStream = null;
        try {
            StreamLogScan streamLogScan;
            if (logInstant2 == null) {
                streamLogScan = (StreamLogScan)this.logFactory.openBackwardsScan(logInstant);
            }
            else {
                if (logInstant2.lessThan(logInstant)) {
                    return;
                }
                streamLogScan = (StreamLogScan)this.logFactory.openBackwardsScan(((LogCounter)logInstant2).getValueAsLong(), logInstant);
            }
            arrayInputStream = new ArrayInputStream(new byte[4096]);
            LogRecord nextRecord;
            while ((nextRecord = streamLogScan.getNextRecord(arrayInputStream, transactionId, 0)) != null) {
                ++n2;
                if (nextRecord.isCLR()) {
                    ++n;
                    nextRecord.skipLoggable();
                    streamLogScan.resetPosition(new LogCounter(arrayInputStream.readLong()));
                }
                else {
                    if (!nextRecord.requiresPrepareLocks()) {
                        continue;
                    }
                    rePreparable = nextRecord.getRePreparable();
                    if (rePreparable == null) {
                        continue;
                    }
                    rePreparable.reclaimPrepareLocks(rawTransaction, rawTransaction.newLockingPolicy(1, 4, true));
                }
            }
        }
        catch (ClassNotFoundException ex) {
            throw this.logFactory.markCorrupt(StandardException.newException("XSLA3.D", ex));
        }
        catch (IOException ex2) {
            throw this.logFactory.markCorrupt(StandardException.newException("XSLA5.D", ex2));
        }
        catch (StandardException ex3) {
            throw this.logFactory.markCorrupt(StandardException.newException("XSLA8.D", ex3, transactionId, rePreparable, null));
        }
        finally {
            if (arrayInputStream != null) {
                try {
                    arrayInputStream.close();
                }
                catch (IOException ex4) {
                    throw this.logFactory.markCorrupt(StandardException.newException("XSLA5.D", ex4, transactionId));
                }
            }
        }
    }
    
    public void undo(final RawTransaction rawTransaction, final TransactionId transactionId, final LogInstant logInstant, final LogInstant logInstant2) throws StandardException {
        int n = 0;
        int n2 = 0;
        int n3 = 0;
        Compensation generateUndo = null;
        Undoable undoable = null;
        ArrayInputStream arrayInputStream = null;
        try {
            StreamLogScan streamLogScan;
            if (logInstant2 == null) {
                streamLogScan = (StreamLogScan)this.logFactory.openBackwardsScan(logInstant);
            }
            else {
                if (logInstant2.lessThan(logInstant)) {
                    return;
                }
                streamLogScan = (StreamLogScan)this.logFactory.openBackwardsScan(((LogCounter)logInstant2).getValueAsLong(), logInstant);
            }
            arrayInputStream = new ArrayInputStream(new byte[4096]);
            LogRecord nextRecord;
            while ((nextRecord = streamLogScan.getNextRecord(arrayInputStream, transactionId, 0)) != null) {
                ++n3;
                if (nextRecord.isCLR()) {
                    ++n2;
                    nextRecord.skipLoggable();
                    streamLogScan.resetPosition(new LogCounter(arrayInputStream.readLong()));
                }
                else {
                    undoable = nextRecord.getUndoable();
                    if (undoable == null) {
                        continue;
                    }
                    final int int1 = arrayInputStream.readInt();
                    final int position = arrayInputStream.getPosition();
                    arrayInputStream.setLimit(int1);
                    generateUndo = undoable.generateUndo(rawTransaction, arrayInputStream);
                    ++n;
                    if (generateUndo == null) {
                        continue;
                    }
                    arrayInputStream.setLimit(position, int1);
                    rawTransaction.logAndUndo(generateUndo, new LogCounter(streamLogScan.getInstant()), arrayInputStream);
                    generateUndo.releaseResource(rawTransaction);
                    generateUndo = null;
                }
            }
        }
        catch (ClassNotFoundException ex) {
            throw this.logFactory.markCorrupt(StandardException.newException("XSLA3.D", ex));
        }
        catch (IOException ex2) {
            throw this.logFactory.markCorrupt(StandardException.newException("XSLA5.D", ex2));
        }
        catch (StandardException ex3) {
            throw this.logFactory.markCorrupt(StandardException.newException("XSLA8.D", ex3, transactionId, undoable, generateUndo));
        }
        finally {
            if (generateUndo != null) {
                generateUndo.releaseResource(rawTransaction);
            }
            if (arrayInputStream != null) {
                try {
                    arrayInputStream.close();
                }
                catch (IOException ex4) {
                    throw this.logFactory.markCorrupt(StandardException.newException("XSLA5.D", ex4, transactionId));
                }
            }
        }
    }
    
    protected long redo(final RawTransaction rawTransaction, final TransactionFactory transactionFactory, final StreamLogScan streamLogScan, final long n, final long n2) throws IOException, StandardException, ClassNotFoundException {
        int n3 = 0;
        int n4 = 0;
        int n5 = 0;
        int n6 = 0;
        int n7 = 0;
        this.logIn.setData(this.logOutputBuffer.getByteArray());
        StreamLogScan streamLogScan2 = null;
        Loggable loggable = null;
        long logRecordEnd = 0L;
        try {
            LogRecord nextRecord;
            while ((nextRecord = streamLogScan.getNextRecord(this.logIn, null, 0)) != null) {
                ++n3;
                long long1 = 0L;
                final long instant = streamLogScan.getInstant();
                logRecordEnd = streamLogScan.getLogRecordEnd();
                if (n != 0L && instant < n && !nextRecord.isFirst() && !nextRecord.isComplete() && !nextRecord.isPrepare()) {
                    continue;
                }
                final TransactionId transactionId = nextRecord.getTransactionId();
                if (!transactionFactory.findTransaction(transactionId, rawTransaction)) {
                    if (n != 0L && instant < n && (nextRecord.isPrepare() || nextRecord.isComplete())) {
                        ++n7;
                        continue;
                    }
                    if (n2 == 0L && !nextRecord.isFirst()) {
                        throw StandardException.newException("XSLAO.D", MessageService.getTextMessage("L012", transactionId));
                    }
                    ++n6;
                    rawTransaction.setTransactionId(nextRecord.getLoggable(), transactionId);
                }
                else {
                    if (n2 == 0L && nextRecord.isFirst()) {
                        throw StandardException.newException("XSLAO.D", MessageService.getTextMessage("L013", transactionId));
                    }
                    if (nextRecord.isFirst()) {
                        ++n6;
                        continue;
                    }
                }
                loggable = nextRecord.getLoggable();
                if (loggable.needsRedo(rawTransaction)) {
                    ++n4;
                    if (nextRecord.isCLR()) {
                        ++n5;
                        if (long1 == 0L) {
                            long1 = this.logIn.readLong();
                        }
                        if (streamLogScan2 == null) {
                            streamLogScan2 = (StreamLogScan)this.logFactory.openForwardsScan(long1, null);
                        }
                        else {
                            streamLogScan2.resetPosition(new LogCounter(long1));
                        }
                        this.logIn.clearLimit();
                        ((Compensation)loggable).setUndoOp(streamLogScan2.getNextRecord(this.logIn, null, 0).getUndoable());
                    }
                    this.logIn.setLimit(this.logIn.readInt());
                    loggable.doMe(rawTransaction, new LogCounter(instant), this.logIn);
                    loggable.releaseResource(rawTransaction);
                    loggable = null;
                }
                if (!nextRecord.isComplete()) {
                    continue;
                }
                ++n7;
                rawTransaction.commit();
            }
            final long logRecordEnd2 = streamLogScan.getLogRecordEnd();
            if (logRecordEnd2 != 0L && LogCounter.getLogFileNumber(logRecordEnd) < LogCounter.getLogFileNumber(logRecordEnd2)) {
                logRecordEnd = logRecordEnd2;
            }
        }
        catch (StandardException ex) {
            throw StandardException.newException("XSLA7.D", ex, loggable);
        }
        finally {
            streamLogScan.close();
            if (streamLogScan2 != null) {
                streamLogScan2.close();
            }
            if (loggable != null) {
                loggable.releaseResource(rawTransaction);
            }
        }
        return logRecordEnd;
    }
    
    protected Loggable readLogRecord(final StreamLogScan streamLogScan, final int n) throws IOException, StandardException, ClassNotFoundException {
        Loggable loggable = null;
        final LogRecord nextRecord = streamLogScan.getNextRecord(new ArrayInputStream(new byte[n]), null, 0);
        if (nextRecord != null) {
            loggable = nextRecord.getLoggable();
        }
        return loggable;
    }
}
