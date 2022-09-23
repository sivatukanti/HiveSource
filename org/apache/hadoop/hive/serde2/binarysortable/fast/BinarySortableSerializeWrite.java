// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.binarysortable.fast;

import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hive.common.type.HiveDecimal;
import org.apache.hive.common.util.DateUtils;
import org.apache.hadoop.hive.common.type.HiveIntervalDayTime;
import org.apache.hadoop.hive.common.type.HiveIntervalYearMonth;
import java.sql.Timestamp;
import org.apache.hadoop.hive.serde2.io.DateWritable;
import java.sql.Date;
import org.apache.hadoop.hive.common.type.HiveVarchar;
import org.apache.hadoop.hive.common.type.HiveChar;
import java.io.IOException;
import org.apache.hadoop.hive.serde2.binarysortable.BinarySortableSerDe;
import java.util.Arrays;
import org.apache.hadoop.hive.serde2.io.TimestampWritable;
import org.apache.hadoop.hive.serde2.ByteStream;
import org.apache.commons.logging.Log;
import org.apache.hadoop.hive.serde2.fast.SerializeWrite;

public class BinarySortableSerializeWrite implements SerializeWrite
{
    public static final Log LOG;
    private ByteStream.Output output;
    private boolean[] columnSortOrderIsDesc;
    private int index;
    private int fieldCount;
    private TimestampWritable tempTimestampWritable;
    
    public BinarySortableSerializeWrite(final boolean[] columnSortOrderIsDesc) {
        this();
        this.fieldCount = columnSortOrderIsDesc.length;
        this.columnSortOrderIsDesc = columnSortOrderIsDesc;
    }
    
    public BinarySortableSerializeWrite(final int fieldCount) {
        this();
        this.fieldCount = fieldCount;
        Arrays.fill(this.columnSortOrderIsDesc = new boolean[fieldCount], false);
    }
    
    private BinarySortableSerializeWrite() {
        this.tempTimestampWritable = new TimestampWritable();
    }
    
    @Override
    public void set(final ByteStream.Output output) {
        (this.output = output).reset();
        this.index = -1;
    }
    
    @Override
    public void setAppend(final ByteStream.Output output) {
        this.output = output;
        this.index = -1;
    }
    
    @Override
    public void reset() {
        this.output.reset();
        this.index = -1;
    }
    
    @Override
    public void writeNull() throws IOException {
        BinarySortableSerDe.writeByte(this.output, (byte)0, this.columnSortOrderIsDesc[++this.index]);
    }
    
    @Override
    public void writeBoolean(final boolean v) throws IOException {
        final boolean invert = this.columnSortOrderIsDesc[++this.index];
        BinarySortableSerDe.writeByte(this.output, (byte)1, invert);
        BinarySortableSerDe.writeByte(this.output, (byte)(v ? 2 : 1), invert);
    }
    
    @Override
    public void writeByte(final byte v) throws IOException {
        final boolean invert = this.columnSortOrderIsDesc[++this.index];
        BinarySortableSerDe.writeByte(this.output, (byte)1, invert);
        BinarySortableSerDe.writeByte(this.output, (byte)(v ^ 0x80), invert);
    }
    
    @Override
    public void writeShort(final short v) throws IOException {
        final boolean invert = this.columnSortOrderIsDesc[++this.index];
        BinarySortableSerDe.writeByte(this.output, (byte)1, invert);
        BinarySortableSerDe.serializeShort(this.output, v, invert);
    }
    
    @Override
    public void writeInt(final int v) throws IOException {
        final boolean invert = this.columnSortOrderIsDesc[++this.index];
        BinarySortableSerDe.writeByte(this.output, (byte)1, invert);
        BinarySortableSerDe.serializeInt(this.output, v, invert);
    }
    
    @Override
    public void writeLong(final long v) throws IOException {
        final boolean invert = this.columnSortOrderIsDesc[++this.index];
        BinarySortableSerDe.writeByte(this.output, (byte)1, invert);
        BinarySortableSerDe.serializeLong(this.output, v, invert);
    }
    
    @Override
    public void writeFloat(final float vf) throws IOException {
        final boolean invert = this.columnSortOrderIsDesc[++this.index];
        BinarySortableSerDe.writeByte(this.output, (byte)1, invert);
        BinarySortableSerDe.serializeFloat(this.output, vf, invert);
    }
    
    @Override
    public void writeDouble(final double vd) throws IOException {
        final boolean invert = this.columnSortOrderIsDesc[++this.index];
        BinarySortableSerDe.writeByte(this.output, (byte)1, invert);
        BinarySortableSerDe.serializeDouble(this.output, vd, invert);
    }
    
    @Override
    public void writeString(final byte[] v) throws IOException {
        final boolean invert = this.columnSortOrderIsDesc[++this.index];
        BinarySortableSerDe.writeByte(this.output, (byte)1, invert);
        BinarySortableSerDe.serializeBytes(this.output, v, 0, v.length, invert);
    }
    
    @Override
    public void writeString(final byte[] v, final int start, final int length) throws IOException {
        final boolean invert = this.columnSortOrderIsDesc[++this.index];
        BinarySortableSerDe.writeByte(this.output, (byte)1, invert);
        BinarySortableSerDe.serializeBytes(this.output, v, start, length, invert);
    }
    
    @Override
    public void writeHiveChar(final HiveChar hiveChar) throws IOException {
        final String string = hiveChar.getStrippedValue();
        final byte[] bytes = string.getBytes();
        this.writeString(bytes);
    }
    
    @Override
    public void writeHiveVarchar(final HiveVarchar hiveVarchar) throws IOException {
        final String string = hiveVarchar.getValue();
        final byte[] bytes = string.getBytes();
        this.writeString(bytes);
    }
    
    @Override
    public void writeBinary(final byte[] v) throws IOException {
        final boolean invert = this.columnSortOrderIsDesc[++this.index];
        BinarySortableSerDe.writeByte(this.output, (byte)1, invert);
        BinarySortableSerDe.serializeBytes(this.output, v, 0, v.length, invert);
    }
    
    @Override
    public void writeBinary(final byte[] v, final int start, final int length) {
        final boolean invert = this.columnSortOrderIsDesc[++this.index];
        BinarySortableSerDe.writeByte(this.output, (byte)1, invert);
        BinarySortableSerDe.serializeBytes(this.output, v, start, length, invert);
    }
    
    @Override
    public void writeDate(final Date date) throws IOException {
        final boolean invert = this.columnSortOrderIsDesc[++this.index];
        BinarySortableSerDe.writeByte(this.output, (byte)1, invert);
        BinarySortableSerDe.serializeInt(this.output, DateWritable.dateToDays(date), invert);
    }
    
    @Override
    public void writeDate(final int dateAsDays) throws IOException {
        final boolean invert = this.columnSortOrderIsDesc[++this.index];
        BinarySortableSerDe.writeByte(this.output, (byte)1, invert);
        BinarySortableSerDe.serializeInt(this.output, dateAsDays, invert);
    }
    
    @Override
    public void writeTimestamp(final Timestamp vt) throws IOException {
        final boolean invert = this.columnSortOrderIsDesc[++this.index];
        BinarySortableSerDe.writeByte(this.output, (byte)1, invert);
        this.tempTimestampWritable.set(vt);
        BinarySortableSerDe.serializeTimestampWritable(this.output, this.tempTimestampWritable, invert);
    }
    
    @Override
    public void writeHiveIntervalYearMonth(final HiveIntervalYearMonth viyt) throws IOException {
        final boolean invert = this.columnSortOrderIsDesc[++this.index];
        BinarySortableSerDe.writeByte(this.output, (byte)1, invert);
        BinarySortableSerDe.serializeHiveIntervalYearMonth(this.output, viyt, invert);
    }
    
    @Override
    public void writeHiveIntervalYearMonth(final int totalMonths) throws IOException {
        final boolean invert = this.columnSortOrderIsDesc[++this.index];
        BinarySortableSerDe.writeByte(this.output, (byte)1, invert);
        BinarySortableSerDe.serializeInt(this.output, totalMonths, invert);
    }
    
    @Override
    public void writeHiveIntervalDayTime(final HiveIntervalDayTime vidt) throws IOException {
        final boolean invert = this.columnSortOrderIsDesc[++this.index];
        BinarySortableSerDe.writeByte(this.output, (byte)1, invert);
        BinarySortableSerDe.serializeHiveIntervalDayTime(this.output, vidt, invert);
    }
    
    @Override
    public void writeHiveIntervalDayTime(final long totalNanos) throws IOException {
        final boolean invert = this.columnSortOrderIsDesc[++this.index];
        BinarySortableSerDe.writeByte(this.output, (byte)1, invert);
        final long totalSecs = DateUtils.getIntervalDayTimeTotalSecondsFromTotalNanos(totalNanos);
        final int nanos = DateUtils.getIntervalDayTimeNanosFromTotalNanos(totalNanos);
        BinarySortableSerDe.serializeLong(this.output, totalSecs, invert);
        BinarySortableSerDe.serializeInt(this.output, nanos, invert);
    }
    
    @Override
    public void writeHiveDecimal(final HiveDecimal dec) throws IOException {
        final boolean invert = this.columnSortOrderIsDesc[++this.index];
        BinarySortableSerDe.writeByte(this.output, (byte)1, invert);
        BinarySortableSerDe.serializeHiveDecimal(this.output, dec, invert);
    }
    
    static {
        LOG = LogFactory.getLog(BinarySortableSerializeWrite.class.getName());
    }
}
