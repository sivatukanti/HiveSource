// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.io;

import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hive.serde2.lazybinary.LazyBinaryUtils;
import org.apache.hadoop.hive.serde2.ByteStream;
import java.io.DataOutput;
import java.io.IOException;
import org.apache.hadoop.io.WritableUtils;
import java.io.DataInput;
import org.apache.hadoop.hive.common.type.HiveIntervalYearMonth;
import org.apache.commons.logging.Log;
import org.apache.hadoop.io.WritableComparable;

public class HiveIntervalYearMonthWritable implements WritableComparable<HiveIntervalYearMonthWritable>
{
    private static final Log LOG;
    protected HiveIntervalYearMonth intervalValue;
    
    public HiveIntervalYearMonthWritable() {
        this.intervalValue = new HiveIntervalYearMonth();
    }
    
    public HiveIntervalYearMonthWritable(final HiveIntervalYearMonth hiveInterval) {
        (this.intervalValue = new HiveIntervalYearMonth()).set(hiveInterval);
    }
    
    public HiveIntervalYearMonthWritable(final HiveIntervalYearMonthWritable hiveIntervalWritable) {
        (this.intervalValue = new HiveIntervalYearMonth()).set(hiveIntervalWritable.intervalValue);
    }
    
    public void set(final int years, final int months) {
        this.intervalValue.set(years, months);
    }
    
    public void set(final HiveIntervalYearMonth hiveInterval) {
        this.intervalValue.set(hiveInterval);
    }
    
    public void set(final HiveIntervalYearMonthWritable hiveIntervalWritable) {
        this.intervalValue.set(hiveIntervalWritable.intervalValue);
    }
    
    public void set(final int totalMonths) {
        this.intervalValue.set(totalMonths);
    }
    
    public HiveIntervalYearMonth getHiveIntervalYearMonth() {
        return new HiveIntervalYearMonth(this.intervalValue);
    }
    
    @Override
    public void readFields(final DataInput in) throws IOException {
        this.set(WritableUtils.readVInt(in));
    }
    
    @Override
    public void write(final DataOutput out) throws IOException {
        WritableUtils.writeVInt(out, this.intervalValue.getTotalMonths());
    }
    
    public void writeToByteStream(final ByteStream.RandomAccessOutput byteStream) {
        LazyBinaryUtils.writeVInt(byteStream, this.intervalValue.getTotalMonths());
    }
    
    public void setFromBytes(final byte[] bytes, final int offset, final int length, final LazyBinaryUtils.VInt vInt) {
        LazyBinaryUtils.readVInt(bytes, offset, vInt);
        assert length == vInt.length;
        this.set(vInt.value);
    }
    
    @Override
    public int compareTo(final HiveIntervalYearMonthWritable other) {
        return this.intervalValue.compareTo(other.intervalValue);
    }
    
    @Override
    public boolean equals(final Object obj) {
        return this == obj || (obj instanceof HiveIntervalYearMonthWritable && 0 == this.compareTo((HiveIntervalYearMonthWritable)obj));
    }
    
    @Override
    public int hashCode() {
        return this.intervalValue.hashCode();
    }
    
    @Override
    public String toString() {
        return this.intervalValue.toString();
    }
    
    static {
        LOG = LogFactory.getLog(HiveIntervalYearMonthWritable.class);
    }
}
