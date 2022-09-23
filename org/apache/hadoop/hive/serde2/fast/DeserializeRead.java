// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.fast;

import org.apache.hadoop.hive.common.type.HiveDecimal;
import org.apache.hadoop.hive.serde2.typeinfo.DecimalTypeInfo;
import org.apache.hadoop.hive.common.type.HiveIntervalDayTime;
import org.apache.hadoop.hive.serde2.io.HiveIntervalDayTimeWritable;
import org.apache.hadoop.hive.common.type.HiveIntervalYearMonth;
import org.apache.hadoop.hive.serde2.io.HiveIntervalYearMonthWritable;
import java.sql.Timestamp;
import org.apache.hadoop.hive.serde2.io.TimestampWritable;
import java.sql.Date;
import org.apache.hadoop.hive.serde2.io.DateWritable;
import org.apache.hadoop.hive.common.type.HiveVarchar;
import org.apache.hadoop.hive.serde2.io.HiveVarcharWritable;
import org.apache.hadoop.hive.serde2.typeinfo.VarcharTypeInfo;
import org.apache.hadoop.hive.common.type.HiveChar;
import org.apache.hadoop.hive.serde2.io.HiveCharWritable;
import org.apache.hadoop.hive.serde2.typeinfo.CharTypeInfo;
import java.io.IOException;
import org.apache.hadoop.hive.serde2.typeinfo.PrimitiveTypeInfo;

public interface DeserializeRead
{
    PrimitiveTypeInfo[] primitiveTypeInfos();
    
    void set(final byte[] p0, final int p1, final int p2);
    
    boolean readCheckNull() throws IOException;
    
    void extraFieldsCheck();
    
    boolean readBeyondConfiguredFieldsWarned();
    
    boolean readBeyondBufferRangeWarned();
    
    boolean bufferRangeHasExtraDataWarned();
    
    boolean readBoolean() throws IOException;
    
    byte readByte() throws IOException;
    
    short readShort() throws IOException;
    
    int readInt() throws IOException;
    
    long readLong() throws IOException;
    
    float readFloat() throws IOException;
    
    double readDouble() throws IOException;
    
    ReadStringResults createReadStringResults();
    
    void readString(final ReadStringResults p0) throws IOException;
    
    ReadHiveCharResults createReadHiveCharResults();
    
    void readHiveChar(final ReadHiveCharResults p0) throws IOException;
    
    ReadHiveVarcharResults createReadHiveVarcharResults();
    
    void readHiveVarchar(final ReadHiveVarcharResults p0) throws IOException;
    
    ReadBinaryResults createReadBinaryResults();
    
    void readBinary(final ReadBinaryResults p0) throws IOException;
    
    ReadDateResults createReadDateResults();
    
    void readDate(final ReadDateResults p0) throws IOException;
    
    ReadTimestampResults createReadTimestampResults();
    
    void readTimestamp(final ReadTimestampResults p0) throws IOException;
    
    ReadIntervalYearMonthResults createReadIntervalYearMonthResults();
    
    void readIntervalYearMonth(final ReadIntervalYearMonthResults p0) throws IOException;
    
    ReadIntervalDayTimeResults createReadIntervalDayTimeResults();
    
    void readIntervalDayTime(final ReadIntervalDayTimeResults p0) throws IOException;
    
    ReadDecimalResults createReadDecimalResults();
    
    void readHiveDecimal(final ReadDecimalResults p0) throws IOException;
    
    public abstract static class ReadBytesResults
    {
        public byte[] bytes;
        public int start;
        public int length;
        
        public ReadBytesResults() {
            this.bytes = null;
            this.start = 0;
            this.length = 0;
        }
    }
    
    public abstract static class ReadStringResults extends ReadBytesResults
    {
    }
    
    public abstract static class ReadHiveCharResults extends ReadBytesResults
    {
        private CharTypeInfo charTypeInfo;
        private int maxLength;
        protected HiveCharWritable hiveCharWritable;
        
        public void init(final CharTypeInfo charTypeInfo) {
            this.charTypeInfo = charTypeInfo;
            this.maxLength = charTypeInfo.getLength();
            this.hiveCharWritable = new HiveCharWritable();
        }
        
        public boolean isInit() {
            return this.charTypeInfo != null;
        }
        
        public int getMaxLength() {
            return this.maxLength;
        }
        
        public HiveChar getHiveChar() {
            return this.hiveCharWritable.getHiveChar();
        }
    }
    
    public abstract static class ReadHiveVarcharResults extends ReadBytesResults
    {
        private VarcharTypeInfo varcharTypeInfo;
        private int maxLength;
        protected HiveVarcharWritable hiveVarcharWritable;
        
        public void init(final VarcharTypeInfo varcharTypeInfo) {
            this.varcharTypeInfo = varcharTypeInfo;
            this.maxLength = varcharTypeInfo.getLength();
            this.hiveVarcharWritable = new HiveVarcharWritable();
        }
        
        public boolean isInit() {
            return this.varcharTypeInfo != null;
        }
        
        public int getMaxLength() {
            return this.maxLength;
        }
        
        public HiveVarchar getHiveVarchar() {
            return this.hiveVarcharWritable.getHiveVarchar();
        }
    }
    
    public abstract static class ReadBinaryResults extends ReadBytesResults
    {
    }
    
    public abstract static class ReadDateResults
    {
        protected DateWritable dateWritable;
        
        public ReadDateResults() {
            this.dateWritable = new DateWritable();
        }
        
        public Date getDate() {
            return this.dateWritable.get();
        }
        
        public int getDays() {
            return this.dateWritable.getDays();
        }
    }
    
    public abstract static class ReadTimestampResults
    {
        protected TimestampWritable timestampWritable;
        
        public ReadTimestampResults() {
            this.timestampWritable = new TimestampWritable();
        }
        
        public Timestamp getTimestamp() {
            return this.timestampWritable.getTimestamp();
        }
    }
    
    public abstract static class ReadIntervalYearMonthResults
    {
        protected HiveIntervalYearMonthWritable hiveIntervalYearMonthWritable;
        
        public ReadIntervalYearMonthResults() {
            this.hiveIntervalYearMonthWritable = new HiveIntervalYearMonthWritable();
        }
        
        public HiveIntervalYearMonth getHiveIntervalYearMonth() {
            return this.hiveIntervalYearMonthWritable.getHiveIntervalYearMonth();
        }
    }
    
    public abstract static class ReadIntervalDayTimeResults
    {
        protected HiveIntervalDayTimeWritable hiveIntervalDayTimeWritable;
        
        public ReadIntervalDayTimeResults() {
            this.hiveIntervalDayTimeWritable = new HiveIntervalDayTimeWritable();
        }
        
        public HiveIntervalDayTime getHiveIntervalDayTime() {
            return this.hiveIntervalDayTimeWritable.getHiveIntervalDayTime();
        }
    }
    
    public abstract static class ReadDecimalResults
    {
        protected DecimalTypeInfo decimalTypeInfo;
        
        public void init(final DecimalTypeInfo decimalTypeInfo) {
            this.decimalTypeInfo = decimalTypeInfo;
        }
        
        public boolean isInit() {
            return this.decimalTypeInfo != null;
        }
        
        public abstract HiveDecimal getHiveDecimal();
    }
}
