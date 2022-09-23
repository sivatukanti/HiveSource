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
import org.apache.hadoop.hive.common.type.HiveIntervalDayTime;
import org.apache.commons.logging.Log;
import org.apache.hadoop.io.WritableComparable;

public class HiveIntervalDayTimeWritable implements WritableComparable<HiveIntervalDayTimeWritable>
{
    private static final Log LOG;
    protected HiveIntervalDayTime intervalValue;
    
    public HiveIntervalDayTimeWritable() {
        this.intervalValue = new HiveIntervalDayTime();
    }
    
    public HiveIntervalDayTimeWritable(final HiveIntervalDayTime value) {
        (this.intervalValue = new HiveIntervalDayTime()).set(value);
    }
    
    public HiveIntervalDayTimeWritable(final HiveIntervalDayTimeWritable writable) {
        (this.intervalValue = new HiveIntervalDayTime()).set(writable.intervalValue);
    }
    
    public void set(final int days, final int hours, final int minutes, final int seconds, final int nanos) {
        this.intervalValue.set(days, hours, minutes, seconds, nanos);
    }
    
    public void set(final HiveIntervalDayTime value) {
        this.intervalValue.set(value);
    }
    
    public void set(final HiveIntervalDayTimeWritable writable) {
        this.intervalValue.set(writable.intervalValue);
    }
    
    public void set(final long totalSeconds, final int nanos) {
        this.intervalValue.set(totalSeconds, nanos);
    }
    
    public HiveIntervalDayTime getHiveIntervalDayTime() {
        return new HiveIntervalDayTime(this.intervalValue);
    }
    
    @Override
    public void readFields(final DataInput in) throws IOException {
        this.set(WritableUtils.readVLong(in), WritableUtils.readVInt(in));
    }
    
    @Override
    public void write(final DataOutput out) throws IOException {
        WritableUtils.writeVLong(out, this.intervalValue.getTotalSeconds());
        WritableUtils.writeVInt(out, this.intervalValue.getNanos());
    }
    
    public void writeToByteStream(final ByteStream.RandomAccessOutput byteStream) {
        LazyBinaryUtils.writeVLong(byteStream, this.intervalValue.getTotalSeconds());
        LazyBinaryUtils.writeVInt(byteStream, this.intervalValue.getNanos());
    }
    
    public void setFromBytes(final byte[] bytes, final int offset, final int length, final LazyBinaryUtils.VInt vInt, final LazyBinaryUtils.VLong vLong) {
        LazyBinaryUtils.readVLong(bytes, offset, vLong);
        LazyBinaryUtils.readVInt(bytes, offset + vLong.length, vInt);
        assert length == vInt.length + vLong.length;
        this.set(vLong.value, vInt.value);
    }
    
    @Override
    public int compareTo(final HiveIntervalDayTimeWritable other) {
        return this.intervalValue.compareTo(other.intervalValue);
    }
    
    @Override
    public boolean equals(final Object obj) {
        return this == obj || (obj instanceof HiveIntervalDayTimeWritable && 0 == this.compareTo((HiveIntervalDayTimeWritable)obj));
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
        LOG = LogFactory.getLog(HiveIntervalDayTimeWritable.class);
    }
}
