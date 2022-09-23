// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.raw.log;

import java.io.DataOutput;
import java.io.ObjectOutput;
import java.io.IOException;
import java.io.DataInput;
import org.apache.derby.iapi.services.io.CompressedNumber;
import java.io.ObjectInput;
import org.apache.derby.iapi.store.access.DatabaseInstant;
import org.apache.derby.iapi.store.raw.log.LogInstant;

public class LogCounter implements LogInstant
{
    public static final long INVALID_LOG_INSTANT = 0L;
    public static final long DERBY_10_0_MAX_LOGFILE_NUMBER = 4194303L;
    public static final long MAX_LOGFILE_NUMBER = 2147483647L;
    private static final long FILE_NUMBER_SHIFT = 32L;
    public static final long MAX_LOGFILE_SIZE = 268435455L;
    private static final long FILE_POSITION_MASK = 2147483647L;
    private long fileNumber;
    private long filePosition;
    
    public LogCounter(final long n) {
        this.fileNumber = getLogFileNumber(n);
        this.filePosition = getLogFilePosition(n);
    }
    
    public LogCounter(final long fileNumber, final long filePosition) {
        this.fileNumber = fileNumber;
        this.filePosition = filePosition;
    }
    
    public LogCounter() {
    }
    
    public static final long makeLogInstantAsLong(final long n, final long n2) {
        return n << 32 | n2;
    }
    
    public static final long getLogFilePosition(final long n) {
        return n & 0x7FFFFFFFL;
    }
    
    public static final long getLogFileNumber(final long n) {
        return n >>> 32;
    }
    
    public boolean lessThan(final DatabaseInstant databaseInstant) {
        final LogCounter logCounter = (LogCounter)databaseInstant;
        return (this.fileNumber == logCounter.fileNumber) ? (this.filePosition < logCounter.filePosition) : (this.fileNumber < logCounter.fileNumber);
    }
    
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LogCounter)) {
            return false;
        }
        final LogCounter logCounter = (LogCounter)o;
        return this.fileNumber == logCounter.fileNumber && this.filePosition == logCounter.filePosition;
    }
    
    public DatabaseInstant next() {
        return new LogCounter(makeLogInstantAsLong(this.fileNumber, this.filePosition) + 1L);
    }
    
    public DatabaseInstant prior() {
        return new LogCounter(makeLogInstantAsLong(this.fileNumber, this.filePosition) - 1L);
    }
    
    public int hashCode() {
        return (int)(this.filePosition ^ this.fileNumber);
    }
    
    public String toString() {
        return "(" + this.fileNumber + "," + this.filePosition + ")";
    }
    
    public static String toDebugString(final long n) {
        return null;
    }
    
    public long getValueAsLong() {
        return makeLogInstantAsLong(this.fileNumber, this.filePosition);
    }
    
    public long getLogFilePosition() {
        return this.filePosition;
    }
    
    public long getLogFileNumber() {
        return this.fileNumber;
    }
    
    public void readExternal(final ObjectInput objectInput) throws IOException, ClassNotFoundException {
        this.fileNumber = CompressedNumber.readLong(objectInput);
        this.filePosition = CompressedNumber.readLong(objectInput);
    }
    
    public void writeExternal(final ObjectOutput objectOutput) throws IOException {
        CompressedNumber.writeLong(objectOutput, this.fileNumber);
        CompressedNumber.writeLong(objectOutput, this.filePosition);
    }
    
    public int getTypeFormatId() {
        return 130;
    }
}
