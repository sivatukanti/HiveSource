// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.lazybinary.fast;

import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hive.serde2.io.TimestampWritable;
import org.apache.hadoop.hive.serde2.io.HiveIntervalDayTimeWritable;
import org.apache.hadoop.hive.serde2.io.HiveIntervalYearMonthWritable;
import org.apache.hadoop.hive.serde2.io.DateWritable;
import org.apache.hadoop.hive.serde2.io.HiveVarcharWritable;
import org.apache.hadoop.hive.serde2.typeinfo.VarcharTypeInfo;
import org.apache.hadoop.hive.serde2.io.HiveCharWritable;
import org.apache.hadoop.hive.serde2.typeinfo.CharTypeInfo;
import java.io.EOFException;
import java.io.IOException;
import org.apache.hadoop.hive.serde2.objectinspector.PrimitiveObjectInspector;
import org.apache.hadoop.hive.serde2.io.HiveDecimalWritable;
import org.apache.hadoop.hive.serde2.lazybinary.LazyBinaryUtils;
import org.apache.hadoop.hive.common.type.HiveDecimal;
import org.apache.hadoop.hive.serde2.typeinfo.DecimalTypeInfo;
import org.apache.hadoop.hive.serde2.typeinfo.PrimitiveTypeInfo;
import org.apache.commons.logging.Log;
import org.apache.hadoop.hive.serde2.fast.DeserializeRead;

public class LazyBinaryDeserializeRead implements DeserializeRead
{
    public static final Log LOG;
    private PrimitiveTypeInfo[] primitiveTypeInfos;
    private byte[] bytes;
    private int start;
    private int offset;
    private int end;
    private int fieldCount;
    private int fieldIndex;
    private byte nullByte;
    private DecimalTypeInfo saveDecimalTypeInfo;
    private HiveDecimal saveDecimal;
    private LazyBinaryUtils.VInt tempVInt;
    private LazyBinaryUtils.VLong tempVLong;
    private HiveDecimalWritable tempHiveDecimalWritable;
    private boolean readBeyondConfiguredFieldsWarned;
    private boolean readBeyondBufferRangeWarned;
    private boolean bufferRangeHasExtraDataWarned;
    
    public LazyBinaryDeserializeRead(final PrimitiveTypeInfo[] primitiveTypeInfos) {
        this.primitiveTypeInfos = primitiveTypeInfos;
        this.fieldCount = primitiveTypeInfos.length;
        this.tempVInt = new LazyBinaryUtils.VInt();
        this.tempVLong = new LazyBinaryUtils.VLong();
        this.readBeyondConfiguredFieldsWarned = false;
        this.readBeyondBufferRangeWarned = false;
        this.bufferRangeHasExtraDataWarned = false;
    }
    
    private LazyBinaryDeserializeRead() {
    }
    
    @Override
    public PrimitiveTypeInfo[] primitiveTypeInfos() {
        return this.primitiveTypeInfos;
    }
    
    @Override
    public void set(final byte[] bytes, final int offset, final int length) {
        this.bytes = bytes;
        this.offset = offset;
        this.start = offset;
        this.end = offset + length;
        this.fieldIndex = 0;
    }
    
    @Override
    public boolean readCheckNull() throws IOException {
        if (this.fieldIndex >= this.fieldCount) {
            if (!this.readBeyondConfiguredFieldsWarned) {
                LazyBinaryDeserializeRead.LOG.info("Reading beyond configured fields! Configured " + this.fieldCount + " fields but " + " reading more (NULLs returned).  Ignoring similar problems.");
                this.readBeyondConfiguredFieldsWarned = true;
            }
            return true;
        }
        if (this.fieldIndex == 0) {
            if (this.offset >= this.end) {
                this.warnBeyondEof();
            }
            this.nullByte = this.bytes[this.offset++];
        }
        if ((this.nullByte & 1 << this.fieldIndex % 8) != 0x0) {
            if (this.offset >= this.end) {
                this.warnBeyondEof();
            }
            return this.primitiveTypeInfos[this.fieldIndex].getPrimitiveCategory() == PrimitiveObjectInspector.PrimitiveCategory.DECIMAL && this.earlyReadHiveDecimal();
        }
        ++this.fieldIndex;
        if (this.fieldIndex < this.fieldCount && this.fieldIndex % 8 == 0) {
            if (this.offset >= this.end) {
                this.warnBeyondEof();
            }
            this.nullByte = this.bytes[this.offset++];
        }
        return true;
    }
    
    @Override
    public void extraFieldsCheck() {
        if (this.offset < this.end && !this.bufferRangeHasExtraDataWarned) {
            final int length = this.end - this.start;
            final int remaining = this.end - this.offset;
            LazyBinaryDeserializeRead.LOG.info("Not all fields were read in the buffer range! Buffer range " + this.start + " for length " + length + " but " + remaining + " bytes remain. " + "(total buffer length " + this.bytes.length + ")" + "  Ignoring similar problems.");
            this.bufferRangeHasExtraDataWarned = true;
        }
    }
    
    @Override
    public boolean readBeyondConfiguredFieldsWarned() {
        return this.readBeyondConfiguredFieldsWarned;
    }
    
    @Override
    public boolean readBeyondBufferRangeWarned() {
        return this.readBeyondBufferRangeWarned;
    }
    
    @Override
    public boolean bufferRangeHasExtraDataWarned() {
        return this.bufferRangeHasExtraDataWarned;
    }
    
    private void warnBeyondEof() throws EOFException {
        if (!this.readBeyondBufferRangeWarned) {
            final int length = this.end - this.start;
            LazyBinaryDeserializeRead.LOG.info("Reading beyond buffer range! Buffer range " + this.start + " for length " + length + " but reading more... " + "(total buffer length " + this.bytes.length + ")" + "  Ignoring similar problems.");
            this.readBeyondBufferRangeWarned = true;
        }
    }
    
    @Override
    public boolean readBoolean() throws IOException {
        final byte result = this.bytes[this.offset++];
        ++this.fieldIndex;
        if (this.fieldIndex < this.fieldCount && this.fieldIndex % 8 == 0) {
            if (this.offset >= this.end) {
                this.warnBeyondEof();
            }
            this.nullByte = this.bytes[this.offset++];
        }
        return result != 0;
    }
    
    @Override
    public byte readByte() throws IOException {
        final byte result = this.bytes[this.offset++];
        ++this.fieldIndex;
        if (this.fieldIndex < this.fieldCount) {
            if (this.offset >= this.end) {
                this.warnBeyondEof();
            }
            if (this.fieldIndex % 8 == 0) {
                this.nullByte = this.bytes[this.offset++];
            }
        }
        return result;
    }
    
    @Override
    public short readShort() throws IOException {
        if (this.offset + 2 > this.end) {
            this.warnBeyondEof();
        }
        final short result = LazyBinaryUtils.byteArrayToShort(this.bytes, this.offset);
        this.offset += 2;
        ++this.fieldIndex;
        if (this.fieldIndex < this.fieldCount && this.fieldIndex % 8 == 0) {
            if (this.offset >= this.end) {
                this.warnBeyondEof();
            }
            this.nullByte = this.bytes[this.offset++];
        }
        return result;
    }
    
    @Override
    public int readInt() throws IOException {
        LazyBinaryUtils.readVInt(this.bytes, this.offset, this.tempVInt);
        this.offset += this.tempVInt.length;
        if (this.offset > this.end) {
            this.warnBeyondEof();
        }
        ++this.fieldIndex;
        if (this.fieldIndex < this.fieldCount && this.fieldIndex % 8 == 0) {
            if (this.offset >= this.end) {
                this.warnBeyondEof();
            }
            this.nullByte = this.bytes[this.offset++];
        }
        return this.tempVInt.value;
    }
    
    @Override
    public long readLong() throws IOException {
        LazyBinaryUtils.readVLong(this.bytes, this.offset, this.tempVLong);
        this.offset += this.tempVLong.length;
        if (this.offset > this.end) {
            this.warnBeyondEof();
        }
        ++this.fieldIndex;
        if (this.fieldIndex < this.fieldCount && this.fieldIndex % 8 == 0) {
            if (this.offset >= this.end) {
                this.warnBeyondEof();
            }
            this.nullByte = this.bytes[this.offset++];
        }
        return this.tempVLong.value;
    }
    
    @Override
    public float readFloat() throws IOException {
        if (this.offset + 4 > this.end) {
            this.warnBeyondEof();
        }
        final float result = Float.intBitsToFloat(LazyBinaryUtils.byteArrayToInt(this.bytes, this.offset));
        this.offset += 4;
        ++this.fieldIndex;
        if (this.fieldIndex < this.fieldCount && this.fieldIndex % 8 == 0) {
            if (this.offset >= this.end) {
                this.warnBeyondEof();
            }
            this.nullByte = this.bytes[this.offset++];
        }
        return result;
    }
    
    @Override
    public double readDouble() throws IOException {
        if (this.offset + 8 > this.end) {
            this.warnBeyondEof();
        }
        final double result = Double.longBitsToDouble(LazyBinaryUtils.byteArrayToLong(this.bytes, this.offset));
        this.offset += 8;
        ++this.fieldIndex;
        if (this.fieldIndex < this.fieldCount && this.fieldIndex % 8 == 0) {
            if (this.offset >= this.end) {
                this.warnBeyondEof();
            }
            this.nullByte = this.bytes[this.offset++];
        }
        return result;
    }
    
    @Override
    public ReadStringResults createReadStringResults() {
        return new LazyBinaryReadStringResults();
    }
    
    @Override
    public void readString(final ReadStringResults readStringResults) throws IOException {
        LazyBinaryUtils.readVInt(this.bytes, this.offset, this.tempVInt);
        this.offset += this.tempVInt.length;
        if (this.offset > this.end) {
            this.warnBeyondEof();
        }
        final int saveStart = this.offset;
        final int length = this.tempVInt.value;
        this.offset += length;
        if (this.offset > this.end) {
            this.warnBeyondEof();
        }
        ++this.fieldIndex;
        if (this.fieldIndex < this.fieldCount && this.fieldIndex % 8 == 0) {
            if (this.offset >= this.end) {
                this.warnBeyondEof();
            }
            this.nullByte = this.bytes[this.offset++];
        }
        readStringResults.bytes = this.bytes;
        readStringResults.start = saveStart;
        readStringResults.length = length;
    }
    
    @Override
    public ReadHiveCharResults createReadHiveCharResults() {
        return new LazyBinaryReadHiveCharResults();
    }
    
    @Override
    public void readHiveChar(final ReadHiveCharResults readHiveCharResults) throws IOException {
        final LazyBinaryReadHiveCharResults lazyBinaryReadHiveCharResults = (LazyBinaryReadHiveCharResults)readHiveCharResults;
        if (!lazyBinaryReadHiveCharResults.isInit()) {
            lazyBinaryReadHiveCharResults.init((CharTypeInfo)this.primitiveTypeInfos[this.fieldIndex]);
        }
        if (lazyBinaryReadHiveCharResults.readStringResults == null) {
            lazyBinaryReadHiveCharResults.readStringResults = new LazyBinaryReadStringResults();
        }
        final LazyBinaryReadStringResults readStringResults = lazyBinaryReadHiveCharResults.readStringResults;
        this.readString(readStringResults);
        final HiveCharWritable hiveCharWritable = lazyBinaryReadHiveCharResults.getHiveCharWritable();
        hiveCharWritable.getTextValue().set(readStringResults.bytes, readStringResults.start, readStringResults.length);
        hiveCharWritable.enforceMaxLength(lazyBinaryReadHiveCharResults.getMaxLength());
        readHiveCharResults.bytes = hiveCharWritable.getTextValue().getBytes();
        readHiveCharResults.start = 0;
        readHiveCharResults.length = hiveCharWritable.getTextValue().getLength();
    }
    
    @Override
    public ReadHiveVarcharResults createReadHiveVarcharResults() {
        return new LazyBinaryReadHiveVarcharResults();
    }
    
    @Override
    public void readHiveVarchar(final ReadHiveVarcharResults readHiveVarcharResults) throws IOException {
        final LazyBinaryReadHiveVarcharResults lazyBinaryReadHiveVarcharResults = (LazyBinaryReadHiveVarcharResults)readHiveVarcharResults;
        if (!lazyBinaryReadHiveVarcharResults.isInit()) {
            lazyBinaryReadHiveVarcharResults.init((VarcharTypeInfo)this.primitiveTypeInfos[this.fieldIndex]);
        }
        if (lazyBinaryReadHiveVarcharResults.readStringResults == null) {
            lazyBinaryReadHiveVarcharResults.readStringResults = new LazyBinaryReadStringResults();
        }
        final LazyBinaryReadStringResults readStringResults = lazyBinaryReadHiveVarcharResults.readStringResults;
        this.readString(readStringResults);
        final HiveVarcharWritable hiveVarcharWritable = lazyBinaryReadHiveVarcharResults.getHiveVarcharWritable();
        hiveVarcharWritable.getTextValue().set(readStringResults.bytes, readStringResults.start, readStringResults.length);
        hiveVarcharWritable.enforceMaxLength(lazyBinaryReadHiveVarcharResults.getMaxLength());
        readHiveVarcharResults.bytes = hiveVarcharWritable.getTextValue().getBytes();
        readHiveVarcharResults.start = 0;
        readHiveVarcharResults.length = hiveVarcharWritable.getTextValue().getLength();
    }
    
    @Override
    public ReadBinaryResults createReadBinaryResults() {
        return new LazyBinaryReadBinaryResults();
    }
    
    @Override
    public void readBinary(final ReadBinaryResults readBinaryResults) throws IOException {
        final LazyBinaryReadBinaryResults lazyBinaryReadBinaryResults = (LazyBinaryReadBinaryResults)readBinaryResults;
        if (lazyBinaryReadBinaryResults.readStringResults == null) {
            lazyBinaryReadBinaryResults.readStringResults = new LazyBinaryReadStringResults();
        }
        final LazyBinaryReadStringResults readStringResults = lazyBinaryReadBinaryResults.readStringResults;
        this.readString(readStringResults);
        readBinaryResults.bytes = readStringResults.bytes;
        readBinaryResults.start = readStringResults.start;
        readBinaryResults.length = readStringResults.length;
    }
    
    @Override
    public ReadDateResults createReadDateResults() {
        return new LazyBinaryReadDateResults();
    }
    
    @Override
    public void readDate(final ReadDateResults readDateResults) throws IOException {
        final LazyBinaryReadDateResults lazyBinaryReadDateResults = (LazyBinaryReadDateResults)readDateResults;
        LazyBinaryUtils.readVInt(this.bytes, this.offset, this.tempVInt);
        this.offset += this.tempVInt.length;
        if (this.offset > this.end) {
            this.warnBeyondEof();
        }
        ++this.fieldIndex;
        if (this.fieldIndex < this.fieldCount && this.fieldIndex % 8 == 0) {
            if (this.offset >= this.end) {
                this.warnBeyondEof();
            }
            this.nullByte = this.bytes[this.offset++];
        }
        final DateWritable dateWritable = lazyBinaryReadDateResults.getDateWritable();
        dateWritable.set(this.tempVInt.value);
    }
    
    @Override
    public ReadIntervalYearMonthResults createReadIntervalYearMonthResults() {
        return new LazyBinaryReadIntervalYearMonthResults();
    }
    
    @Override
    public void readIntervalYearMonth(final ReadIntervalYearMonthResults readIntervalYearMonthResults) throws IOException {
        final LazyBinaryReadIntervalYearMonthResults lazyBinaryReadIntervalYearMonthResults = (LazyBinaryReadIntervalYearMonthResults)readIntervalYearMonthResults;
        LazyBinaryUtils.readVInt(this.bytes, this.offset, this.tempVInt);
        this.offset += this.tempVInt.length;
        if (this.offset > this.end) {
            this.warnBeyondEof();
        }
        ++this.fieldIndex;
        if (this.fieldIndex < this.fieldCount && this.fieldIndex % 8 == 0) {
            if (this.offset >= this.end) {
                this.warnBeyondEof();
            }
            this.nullByte = this.bytes[this.offset++];
        }
        final HiveIntervalYearMonthWritable hiveIntervalYearMonthWritable = lazyBinaryReadIntervalYearMonthResults.getHiveIntervalYearMonthWritable();
        hiveIntervalYearMonthWritable.set(this.tempVInt.value);
    }
    
    @Override
    public ReadIntervalDayTimeResults createReadIntervalDayTimeResults() {
        return new LazyBinaryReadIntervalDayTimeResults();
    }
    
    @Override
    public void readIntervalDayTime(final ReadIntervalDayTimeResults readIntervalDayTimeResults) throws IOException {
        final LazyBinaryReadIntervalDayTimeResults lazyBinaryReadIntervalDayTimeResults = (LazyBinaryReadIntervalDayTimeResults)readIntervalDayTimeResults;
        LazyBinaryUtils.readVLong(this.bytes, this.offset, this.tempVLong);
        this.offset += this.tempVLong.length;
        if (this.offset >= this.end) {
            this.warnBeyondEof();
        }
        LazyBinaryUtils.readVInt(this.bytes, this.offset, this.tempVInt);
        this.offset += this.tempVInt.length;
        if (this.offset > this.end) {
            this.warnBeyondEof();
        }
        ++this.fieldIndex;
        if (this.fieldIndex < this.fieldCount && this.fieldIndex % 8 == 0) {
            if (this.offset >= this.end) {
                this.warnBeyondEof();
            }
            this.nullByte = this.bytes[this.offset++];
        }
        final HiveIntervalDayTimeWritable hiveIntervalDayTimeWritable = lazyBinaryReadIntervalDayTimeResults.getHiveIntervalDayTimeWritable();
        hiveIntervalDayTimeWritable.set(this.tempVLong.value, this.tempVInt.value);
    }
    
    @Override
    public ReadTimestampResults createReadTimestampResults() {
        return new LazyBinaryReadTimestampResults();
    }
    
    @Override
    public void readTimestamp(final ReadTimestampResults readTimestampResults) throws IOException {
        final LazyBinaryReadTimestampResults lazyBinaryReadTimestampResults = (LazyBinaryReadTimestampResults)readTimestampResults;
        final int length = TimestampWritable.getTotalLength(this.bytes, this.offset);
        final int saveStart = this.offset;
        this.offset += length;
        if (this.offset > this.end) {
            this.warnBeyondEof();
        }
        ++this.fieldIndex;
        if (this.fieldIndex < this.fieldCount && this.fieldIndex % 8 == 0) {
            if (this.offset >= this.end) {
                this.warnBeyondEof();
            }
            this.nullByte = this.bytes[this.offset++];
        }
        final TimestampWritable timestampWritable = lazyBinaryReadTimestampResults.getTimestampWritable();
        timestampWritable.set(this.bytes, saveStart);
    }
    
    @Override
    public ReadDecimalResults createReadDecimalResults() {
        return new LazyBinaryReadDecimalResults();
    }
    
    @Override
    public void readHiveDecimal(final ReadDecimalResults readDecimalResults) throws IOException {
        final LazyBinaryReadDecimalResults lazyBinaryReadDecimalResults = (LazyBinaryReadDecimalResults)readDecimalResults;
        if (!lazyBinaryReadDecimalResults.isInit()) {
            lazyBinaryReadDecimalResults.init(this.saveDecimalTypeInfo);
        }
        lazyBinaryReadDecimalResults.hiveDecimal = this.saveDecimal;
        this.saveDecimal = null;
        this.saveDecimalTypeInfo = null;
    }
    
    private boolean earlyReadHiveDecimal() throws EOFException {
        LazyBinaryUtils.readVInt(this.bytes, this.offset, this.tempVInt);
        final int saveStart = this.offset;
        this.offset += this.tempVInt.length;
        if (this.offset >= this.end) {
            this.warnBeyondEof();
        }
        LazyBinaryUtils.readVInt(this.bytes, this.offset, this.tempVInt);
        this.offset += this.tempVInt.length;
        if (this.offset >= this.end) {
            this.warnBeyondEof();
        }
        this.offset += this.tempVInt.value;
        if (this.offset > this.end) {
            this.warnBeyondEof();
        }
        final int length = this.offset - saveStart;
        if (this.tempHiveDecimalWritable == null) {
            this.tempHiveDecimalWritable = new HiveDecimalWritable();
        }
        this.tempHiveDecimalWritable.setFromBytes(this.bytes, saveStart, length);
        this.saveDecimalTypeInfo = (DecimalTypeInfo)this.primitiveTypeInfos[this.fieldIndex];
        final int precision = this.saveDecimalTypeInfo.getPrecision();
        final int scale = this.saveDecimalTypeInfo.getScale();
        this.saveDecimal = this.tempHiveDecimalWritable.getHiveDecimal(precision, scale);
        ++this.fieldIndex;
        if (this.fieldIndex < this.fieldCount && this.fieldIndex % 8 == 0) {
            if (this.offset >= this.end) {
                this.warnBeyondEof();
            }
            this.nullByte = this.bytes[this.offset++];
        }
        return this.saveDecimal == null;
    }
    
    static {
        LOG = LogFactory.getLog(LazyBinaryDeserializeRead.class.getName());
    }
    
    private class LazyBinaryReadStringResults extends ReadStringResults
    {
        public LazyBinaryReadStringResults() {
        }
    }
    
    private static class LazyBinaryReadHiveCharResults extends ReadHiveCharResults
    {
        public LazyBinaryReadStringResults readStringResults;
        
        public LazyBinaryReadHiveCharResults() {
        }
        
        public HiveCharWritable getHiveCharWritable() {
            return this.hiveCharWritable;
        }
    }
    
    private static class LazyBinaryReadHiveVarcharResults extends ReadHiveVarcharResults
    {
        public LazyBinaryReadStringResults readStringResults;
        
        public LazyBinaryReadHiveVarcharResults() {
        }
        
        public HiveVarcharWritable getHiveVarcharWritable() {
            return this.hiveVarcharWritable;
        }
    }
    
    private class LazyBinaryReadBinaryResults extends ReadBinaryResults
    {
        public LazyBinaryReadStringResults readStringResults;
        
        public LazyBinaryReadBinaryResults() {
        }
    }
    
    private static class LazyBinaryReadDateResults extends ReadDateResults
    {
        public LazyBinaryReadDateResults() {
        }
        
        public DateWritable getDateWritable() {
            return this.dateWritable;
        }
    }
    
    private static class LazyBinaryReadIntervalYearMonthResults extends ReadIntervalYearMonthResults
    {
        public LazyBinaryReadIntervalYearMonthResults() {
        }
        
        public HiveIntervalYearMonthWritable getHiveIntervalYearMonthWritable() {
            return this.hiveIntervalYearMonthWritable;
        }
    }
    
    private static class LazyBinaryReadIntervalDayTimeResults extends ReadIntervalDayTimeResults
    {
        public LazyBinaryReadIntervalDayTimeResults() {
        }
        
        public HiveIntervalDayTimeWritable getHiveIntervalDayTimeWritable() {
            return this.hiveIntervalDayTimeWritable;
        }
    }
    
    private static class LazyBinaryReadTimestampResults extends ReadTimestampResults
    {
        public LazyBinaryReadTimestampResults() {
        }
        
        public TimestampWritable getTimestampWritable() {
            return this.timestampWritable;
        }
    }
    
    private static class LazyBinaryReadDecimalResults extends ReadDecimalResults
    {
        public HiveDecimal hiveDecimal;
        
        @Override
        public void init(final DecimalTypeInfo decimalTypeInfo) {
            super.init(decimalTypeInfo);
        }
        
        @Override
        public HiveDecimal getHiveDecimal() {
            return this.hiveDecimal;
        }
    }
}
