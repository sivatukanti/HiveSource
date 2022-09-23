// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.binarysortable.fast;

import org.apache.hadoop.io.Text;
import org.apache.commons.logging.LogFactory;
import java.math.BigInteger;
import org.apache.hadoop.hive.serde2.io.HiveIntervalDayTimeWritable;
import org.apache.hadoop.hive.serde2.io.HiveIntervalYearMonthWritable;
import org.apache.hadoop.hive.serde2.io.TimestampWritable;
import org.apache.hadoop.hive.serde2.io.DateWritable;
import org.apache.hadoop.hive.serde2.io.HiveVarcharWritable;
import org.apache.hadoop.hive.serde2.typeinfo.VarcharTypeInfo;
import org.apache.hadoop.hive.serde2.io.HiveCharWritable;
import org.apache.hadoop.hive.serde2.typeinfo.CharTypeInfo;
import org.apache.hadoop.hive.serde2.binarysortable.BinarySortableSerDe;
import java.io.IOException;
import org.apache.hadoop.hive.serde2.objectinspector.PrimitiveObjectInspector;
import java.util.Arrays;
import org.apache.hadoop.hive.serde2.binarysortable.InputByteBuffer;
import org.apache.hadoop.hive.serde2.io.HiveDecimalWritable;
import org.apache.hadoop.hive.common.type.HiveDecimal;
import org.apache.hadoop.hive.serde2.typeinfo.DecimalTypeInfo;
import org.apache.hadoop.hive.serde2.typeinfo.PrimitiveTypeInfo;
import org.apache.commons.logging.Log;
import org.apache.hadoop.hive.serde2.fast.DeserializeRead;

public class BinarySortableDeserializeRead implements DeserializeRead
{
    public static final Log LOG;
    private PrimitiveTypeInfo[] primitiveTypeInfos;
    private boolean[] columnSortOrderIsDesc;
    private int fieldIndex;
    private int fieldCount;
    private int start;
    private DecimalTypeInfo saveDecimalTypeInfo;
    private HiveDecimal saveDecimal;
    private byte[] tempDecimalBuffer;
    private HiveDecimalWritable tempHiveDecimalWritable;
    private boolean readBeyondConfiguredFieldsWarned;
    private boolean readBeyondBufferRangeWarned;
    private boolean bufferRangeHasExtraDataWarned;
    private InputByteBuffer inputByteBuffer;
    
    public BinarySortableDeserializeRead(final PrimitiveTypeInfo[] primitiveTypeInfos) {
        this(primitiveTypeInfos, null);
    }
    
    public BinarySortableDeserializeRead(final PrimitiveTypeInfo[] primitiveTypeInfos, final boolean[] columnSortOrderIsDesc) {
        this.inputByteBuffer = new InputByteBuffer();
        this.primitiveTypeInfos = primitiveTypeInfos;
        this.fieldCount = primitiveTypeInfos.length;
        if (columnSortOrderIsDesc != null) {
            this.columnSortOrderIsDesc = columnSortOrderIsDesc;
        }
        else {
            Arrays.fill(this.columnSortOrderIsDesc = new boolean[primitiveTypeInfos.length], false);
        }
        this.inputByteBuffer = new InputByteBuffer();
        this.readBeyondConfiguredFieldsWarned = false;
        this.readBeyondBufferRangeWarned = false;
        this.bufferRangeHasExtraDataWarned = false;
    }
    
    private BinarySortableDeserializeRead() {
        this.inputByteBuffer = new InputByteBuffer();
    }
    
    @Override
    public PrimitiveTypeInfo[] primitiveTypeInfos() {
        return this.primitiveTypeInfos;
    }
    
    @Override
    public void set(final byte[] bytes, final int offset, final int length) {
        this.fieldIndex = -1;
        this.inputByteBuffer.reset(bytes, offset, offset + length);
        this.start = offset;
    }
    
    @Override
    public boolean readCheckNull() throws IOException {
        ++this.fieldIndex;
        if (this.fieldIndex >= this.fieldCount) {
            if (!this.readBeyondConfiguredFieldsWarned) {
                BinarySortableDeserializeRead.LOG.info("Reading beyond configured fields! Configured " + this.fieldCount + " fields but " + " reading more (NULLs returned).  Ignoring similar problems.");
                this.readBeyondConfiguredFieldsWarned = true;
            }
            return true;
        }
        if (this.inputByteBuffer.isEof()) {
            if (!this.readBeyondBufferRangeWarned) {
                final int length = this.inputByteBuffer.tell() - this.start;
                BinarySortableDeserializeRead.LOG.info("Reading beyond buffer range! Buffer range " + this.start + " for length " + length + " but reading more... " + "(total buffer length " + this.inputByteBuffer.getData().length + ")" + "  Ignoring similar problems.");
                this.readBeyondBufferRangeWarned = true;
            }
            return true;
        }
        final byte isNull = this.inputByteBuffer.read(this.columnSortOrderIsDesc[this.fieldIndex]);
        return isNull == 0 || (this.primitiveTypeInfos[this.fieldIndex].getPrimitiveCategory() == PrimitiveObjectInspector.PrimitiveCategory.DECIMAL && this.earlyReadHiveDecimal());
    }
    
    @Override
    public void extraFieldsCheck() {
        if (!this.inputByteBuffer.isEof() && !this.bufferRangeHasExtraDataWarned) {
            final int length = this.inputByteBuffer.getEnd() - this.start;
            final int remaining = this.inputByteBuffer.getEnd() - this.inputByteBuffer.tell();
            BinarySortableDeserializeRead.LOG.info("Not all fields were read in the buffer range! Buffer range " + this.start + " for length " + length + " but " + remaining + " bytes remain. " + "(total buffer length " + this.inputByteBuffer.getData().length + ")" + "  Ignoring similar problems.");
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
    
    @Override
    public boolean readBoolean() throws IOException {
        final byte b = this.inputByteBuffer.read(this.columnSortOrderIsDesc[this.fieldIndex]);
        return b == 2;
    }
    
    @Override
    public byte readByte() throws IOException {
        return (byte)(this.inputByteBuffer.read(this.columnSortOrderIsDesc[this.fieldIndex]) ^ 0x80);
    }
    
    @Override
    public short readShort() throws IOException {
        final boolean invert = this.columnSortOrderIsDesc[this.fieldIndex];
        int v = this.inputByteBuffer.read(invert) ^ 0x80;
        v = (v << 8) + (this.inputByteBuffer.read(invert) & 0xFF);
        return (short)v;
    }
    
    @Override
    public int readInt() throws IOException {
        final boolean invert = this.columnSortOrderIsDesc[this.fieldIndex];
        int v = this.inputByteBuffer.read(invert) ^ 0x80;
        for (int i = 0; i < 3; ++i) {
            v = (v << 8) + (this.inputByteBuffer.read(invert) & 0xFF);
        }
        return v;
    }
    
    @Override
    public long readLong() throws IOException {
        final boolean invert = this.columnSortOrderIsDesc[this.fieldIndex];
        long v = this.inputByteBuffer.read(invert) ^ 0x80;
        for (int i = 0; i < 7; ++i) {
            v = (v << 8) + (this.inputByteBuffer.read(invert) & 0xFF);
        }
        return v;
    }
    
    @Override
    public float readFloat() throws IOException {
        final boolean invert = this.columnSortOrderIsDesc[this.fieldIndex];
        int v = 0;
        for (int i = 0; i < 4; ++i) {
            v = (v << 8) + (this.inputByteBuffer.read(invert) & 0xFF);
        }
        if ((v & Integer.MIN_VALUE) == 0x0) {
            v ^= -1;
        }
        else {
            v ^= Integer.MIN_VALUE;
        }
        return Float.intBitsToFloat(v);
    }
    
    @Override
    public double readDouble() throws IOException {
        final boolean invert = this.columnSortOrderIsDesc[this.fieldIndex];
        long v = 0L;
        for (int i = 0; i < 8; ++i) {
            v = (v << 8) + (this.inputByteBuffer.read(invert) & 0xFF);
        }
        if ((v & Long.MIN_VALUE) == 0x0L) {
            v ^= -1L;
        }
        else {
            v ^= Long.MIN_VALUE;
        }
        return Double.longBitsToDouble(v);
    }
    
    @Override
    public ReadStringResults createReadStringResults() {
        return new BinarySortableReadStringResults();
    }
    
    @Override
    public void readString(final ReadStringResults readStringResults) throws IOException {
        final BinarySortableReadStringResults binarySortableReadStringResults = (BinarySortableReadStringResults)readStringResults;
        BinarySortableSerDe.deserializeText(this.inputByteBuffer, this.columnSortOrderIsDesc[this.fieldIndex], binarySortableReadStringResults.text);
        readStringResults.bytes = binarySortableReadStringResults.text.getBytes();
        readStringResults.start = 0;
        readStringResults.length = binarySortableReadStringResults.text.getLength();
    }
    
    @Override
    public ReadHiveCharResults createReadHiveCharResults() {
        return new BinarySortableReadHiveCharResults();
    }
    
    @Override
    public void readHiveChar(final ReadHiveCharResults readHiveCharResults) throws IOException {
        final BinarySortableReadHiveCharResults binarySortableReadHiveCharResults = (BinarySortableReadHiveCharResults)readHiveCharResults;
        if (!binarySortableReadHiveCharResults.isInit()) {
            binarySortableReadHiveCharResults.init((CharTypeInfo)this.primitiveTypeInfos[this.fieldIndex]);
        }
        final HiveCharWritable hiveCharWritable = binarySortableReadHiveCharResults.getHiveCharWritable();
        BinarySortableSerDe.deserializeText(this.inputByteBuffer, this.columnSortOrderIsDesc[this.fieldIndex], hiveCharWritable.getTextValue());
        hiveCharWritable.enforceMaxLength(binarySortableReadHiveCharResults.getMaxLength());
        readHiveCharResults.bytes = hiveCharWritable.getTextValue().getBytes();
        readHiveCharResults.start = 0;
        readHiveCharResults.length = hiveCharWritable.getTextValue().getLength();
    }
    
    @Override
    public ReadHiveVarcharResults createReadHiveVarcharResults() {
        return new BinarySortableReadHiveVarcharResults();
    }
    
    @Override
    public void readHiveVarchar(final ReadHiveVarcharResults readHiveVarcharResults) throws IOException {
        final BinarySortableReadHiveVarcharResults binarySortableReadHiveVarcharResults = (BinarySortableReadHiveVarcharResults)readHiveVarcharResults;
        if (!binarySortableReadHiveVarcharResults.isInit()) {
            binarySortableReadHiveVarcharResults.init((VarcharTypeInfo)this.primitiveTypeInfos[this.fieldIndex]);
        }
        final HiveVarcharWritable hiveVarcharWritable = binarySortableReadHiveVarcharResults.getHiveVarcharWritable();
        BinarySortableSerDe.deserializeText(this.inputByteBuffer, this.columnSortOrderIsDesc[this.fieldIndex], hiveVarcharWritable.getTextValue());
        hiveVarcharWritable.enforceMaxLength(binarySortableReadHiveVarcharResults.getMaxLength());
        readHiveVarcharResults.bytes = hiveVarcharWritable.getTextValue().getBytes();
        readHiveVarcharResults.start = 0;
        readHiveVarcharResults.length = hiveVarcharWritable.getTextValue().getLength();
    }
    
    @Override
    public ReadBinaryResults createReadBinaryResults() {
        return new BinarySortableReadBinaryResults();
    }
    
    @Override
    public void readBinary(final ReadBinaryResults readBinaryResults) throws IOException {
        final BinarySortableReadBinaryResults binarySortableReadBinaryResults = (BinarySortableReadBinaryResults)readBinaryResults;
        BinarySortableSerDe.deserializeText(this.inputByteBuffer, this.columnSortOrderIsDesc[this.fieldIndex], binarySortableReadBinaryResults.text);
        readBinaryResults.bytes = binarySortableReadBinaryResults.text.getBytes();
        readBinaryResults.start = 0;
        readBinaryResults.length = binarySortableReadBinaryResults.text.getLength();
    }
    
    @Override
    public ReadDateResults createReadDateResults() {
        return new BinarySortableReadDateResults();
    }
    
    @Override
    public void readDate(final ReadDateResults readDateResults) throws IOException {
        final BinarySortableReadDateResults binarySortableReadDateResults = (BinarySortableReadDateResults)readDateResults;
        final boolean invert = this.columnSortOrderIsDesc[this.fieldIndex];
        int v = this.inputByteBuffer.read(invert) ^ 0x80;
        for (int i = 0; i < 3; ++i) {
            v = (v << 8) + (this.inputByteBuffer.read(invert) & 0xFF);
        }
        final DateWritable dateWritable = binarySortableReadDateResults.getDateWritable();
        dateWritable.set(v);
    }
    
    @Override
    public ReadTimestampResults createReadTimestampResults() {
        return new BinarySortableReadTimestampResults();
    }
    
    @Override
    public void readTimestamp(final ReadTimestampResults readTimestampResults) throws IOException {
        final BinarySortableReadTimestampResults binarySortableReadTimestampResults = (BinarySortableReadTimestampResults)readTimestampResults;
        final boolean invert = this.columnSortOrderIsDesc[this.fieldIndex];
        final byte[] timestampBytes = binarySortableReadTimestampResults.timestampBytes;
        for (int i = 0; i < timestampBytes.length; ++i) {
            timestampBytes[i] = this.inputByteBuffer.read(invert);
        }
        final TimestampWritable timestampWritable = binarySortableReadTimestampResults.getTimestampWritable();
        timestampWritable.setBinarySortable(timestampBytes, 0);
    }
    
    @Override
    public ReadIntervalYearMonthResults createReadIntervalYearMonthResults() {
        return new BinarySortableReadIntervalYearMonthResults();
    }
    
    @Override
    public void readIntervalYearMonth(final ReadIntervalYearMonthResults readIntervalYearMonthResults) throws IOException {
        final BinarySortableReadIntervalYearMonthResults binarySortableReadIntervalYearMonthResults = (BinarySortableReadIntervalYearMonthResults)readIntervalYearMonthResults;
        final boolean invert = this.columnSortOrderIsDesc[this.fieldIndex];
        int v = this.inputByteBuffer.read(invert) ^ 0x80;
        for (int i = 0; i < 3; ++i) {
            v = (v << 8) + (this.inputByteBuffer.read(invert) & 0xFF);
        }
        final HiveIntervalYearMonthWritable hiveIntervalYearMonthWritable = binarySortableReadIntervalYearMonthResults.getHiveIntervalYearMonthWritable();
        hiveIntervalYearMonthWritable.set(v);
    }
    
    @Override
    public ReadIntervalDayTimeResults createReadIntervalDayTimeResults() {
        return new BinarySortableReadIntervalDayTimeResults();
    }
    
    @Override
    public void readIntervalDayTime(final ReadIntervalDayTimeResults readIntervalDayTimeResults) throws IOException {
        final BinarySortableReadIntervalDayTimeResults binarySortableReadIntervalDayTimeResults = (BinarySortableReadIntervalDayTimeResults)readIntervalDayTimeResults;
        final boolean invert = this.columnSortOrderIsDesc[this.fieldIndex];
        long totalSecs = this.inputByteBuffer.read(invert) ^ 0x80;
        for (int i = 0; i < 7; ++i) {
            totalSecs = (totalSecs << 8) + (this.inputByteBuffer.read(invert) & 0xFF);
        }
        int nanos = this.inputByteBuffer.read(invert) ^ 0x80;
        for (int j = 0; j < 3; ++j) {
            nanos = (nanos << 8) + (this.inputByteBuffer.read(invert) & 0xFF);
        }
        final HiveIntervalDayTimeWritable hiveIntervalDayTimeWritable = binarySortableReadIntervalDayTimeResults.getHiveIntervalDayTimeWritable();
        hiveIntervalDayTimeWritable.set(totalSecs, nanos);
    }
    
    @Override
    public ReadDecimalResults createReadDecimalResults() {
        return new BinarySortableReadDecimalResults();
    }
    
    @Override
    public void readHiveDecimal(final ReadDecimalResults readDecimalResults) throws IOException {
        final BinarySortableReadDecimalResults binarySortableReadDecimalResults = (BinarySortableReadDecimalResults)readDecimalResults;
        if (!binarySortableReadDecimalResults.isInit()) {
            binarySortableReadDecimalResults.init(this.saveDecimalTypeInfo);
        }
        binarySortableReadDecimalResults.hiveDecimal = this.saveDecimal;
        this.saveDecimal = null;
        this.saveDecimalTypeInfo = null;
    }
    
    private boolean earlyReadHiveDecimal() throws IOException {
        final boolean invert = this.columnSortOrderIsDesc[this.fieldIndex];
        int b = this.inputByteBuffer.read(invert) - 1;
        assert b == 0;
        final boolean positive = b != -1;
        int factor = this.inputByteBuffer.read(invert) ^ 0x80;
        for (int i = 0; i < 3; ++i) {
            factor = (factor << 8) + (this.inputByteBuffer.read(invert) & 0xFF);
        }
        if (!positive) {
            factor = -factor;
        }
        final int start = this.inputByteBuffer.tell();
        int length = 0;
        while (true) {
            b = this.inputByteBuffer.read(positive ? invert : (!invert));
            assert b != 1;
            if (b == 0) {
                if (this.tempDecimalBuffer == null || this.tempDecimalBuffer.length < length) {
                    this.tempDecimalBuffer = new byte[length];
                }
                this.inputByteBuffer.seek(start);
                for (int j = 0; j < length; ++j) {
                    this.tempDecimalBuffer[j] = this.inputByteBuffer.read(positive ? invert : (!invert));
                }
                this.inputByteBuffer.read(positive ? invert : (!invert));
                final String digits = new String(this.tempDecimalBuffer, 0, length, BinarySortableSerDe.decimalCharSet);
                final BigInteger bi = new BigInteger(digits);
                HiveDecimal bd = HiveDecimal.create(bi).scaleByPowerOfTen(factor - length);
                if (!positive) {
                    bd = bd.negate();
                }
                if (this.tempHiveDecimalWritable == null) {
                    this.tempHiveDecimalWritable = new HiveDecimalWritable();
                }
                this.tempHiveDecimalWritable.set(bd);
                this.saveDecimalTypeInfo = (DecimalTypeInfo)this.primitiveTypeInfos[this.fieldIndex];
                final int precision = this.saveDecimalTypeInfo.getPrecision();
                final int scale = this.saveDecimalTypeInfo.getScale();
                this.saveDecimal = this.tempHiveDecimalWritable.getHiveDecimal(precision, scale);
                return this.saveDecimal == null;
            }
            ++length;
        }
    }
    
    static {
        LOG = LogFactory.getLog(BinarySortableDeserializeRead.class.getName());
    }
    
    private static class BinarySortableReadStringResults extends ReadStringResults
    {
        private Text text;
        
        public BinarySortableReadStringResults() {
            this.text = new Text();
        }
    }
    
    private static class BinarySortableReadHiveCharResults extends ReadHiveCharResults
    {
        public BinarySortableReadHiveCharResults() {
        }
        
        public HiveCharWritable getHiveCharWritable() {
            return this.hiveCharWritable;
        }
    }
    
    private static class BinarySortableReadHiveVarcharResults extends ReadHiveVarcharResults
    {
        public BinarySortableReadHiveVarcharResults() {
        }
        
        public HiveVarcharWritable getHiveVarcharWritable() {
            return this.hiveVarcharWritable;
        }
    }
    
    private static class BinarySortableReadBinaryResults extends ReadBinaryResults
    {
        private Text text;
        
        public BinarySortableReadBinaryResults() {
            this.text = new Text();
        }
    }
    
    private static class BinarySortableReadDateResults extends ReadDateResults
    {
        public BinarySortableReadDateResults() {
        }
        
        public DateWritable getDateWritable() {
            return this.dateWritable;
        }
    }
    
    private static class BinarySortableReadTimestampResults extends ReadTimestampResults
    {
        private byte[] timestampBytes;
        
        public BinarySortableReadTimestampResults() {
            this.timestampBytes = new byte[11];
        }
        
        public TimestampWritable getTimestampWritable() {
            return this.timestampWritable;
        }
    }
    
    private static class BinarySortableReadIntervalYearMonthResults extends ReadIntervalYearMonthResults
    {
        public BinarySortableReadIntervalYearMonthResults() {
        }
        
        public HiveIntervalYearMonthWritable getHiveIntervalYearMonthWritable() {
            return this.hiveIntervalYearMonthWritable;
        }
    }
    
    private static class BinarySortableReadIntervalDayTimeResults extends ReadIntervalDayTimeResults
    {
        public BinarySortableReadIntervalDayTimeResults() {
        }
        
        public HiveIntervalDayTimeWritable getHiveIntervalDayTimeWritable() {
            return this.hiveIntervalDayTimeWritable;
        }
    }
    
    private static class BinarySortableReadDecimalResults extends ReadDecimalResults
    {
        public HiveDecimal hiveDecimal;
        
        public BinarySortableReadDecimalResults() {
        }
        
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
