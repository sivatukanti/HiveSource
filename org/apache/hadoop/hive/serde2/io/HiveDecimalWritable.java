// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.io;

import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hive.serde2.ByteStream;
import org.apache.hadoop.hive.common.type.Decimal128;
import java.io.DataOutput;
import java.io.IOException;
import org.apache.hadoop.io.WritableUtils;
import java.io.DataInput;
import org.apache.hadoop.hive.serde2.typeinfo.HiveDecimalUtils;
import java.math.BigInteger;
import org.apache.hadoop.hive.common.type.HiveDecimal;
import org.apache.hadoop.hive.serde2.lazybinary.LazyBinaryUtils;
import org.apache.commons.logging.Log;
import org.apache.hadoop.io.WritableComparable;

public class HiveDecimalWritable implements WritableComparable<HiveDecimalWritable>
{
    private static final Log LOG;
    private byte[] internalStorage;
    private int scale;
    private final LazyBinaryUtils.VInt vInt;
    
    public HiveDecimalWritable() {
        this.internalStorage = new byte[0];
        this.vInt = new LazyBinaryUtils.VInt();
    }
    
    public HiveDecimalWritable(final byte[] bytes, final int scale) {
        this.internalStorage = new byte[0];
        this.vInt = new LazyBinaryUtils.VInt();
        this.set(bytes, scale);
    }
    
    public HiveDecimalWritable(final HiveDecimalWritable writable) {
        this.internalStorage = new byte[0];
        this.vInt = new LazyBinaryUtils.VInt();
        this.set(writable.getHiveDecimal());
    }
    
    public HiveDecimalWritable(final HiveDecimal value) {
        this.internalStorage = new byte[0];
        this.vInt = new LazyBinaryUtils.VInt();
        this.set(value);
    }
    
    public void set(final HiveDecimal value) {
        this.set(value.unscaledValue().toByteArray(), value.scale());
    }
    
    public void set(final HiveDecimal value, final int maxPrecision, final int maxScale) {
        this.set(HiveDecimal.enforcePrecisionScale(value, maxPrecision, maxScale));
    }
    
    public void set(final HiveDecimalWritable writable) {
        this.set(writable.getHiveDecimal());
    }
    
    public void set(final byte[] bytes, final int scale) {
        this.internalStorage = bytes;
        this.scale = scale;
    }
    
    public void setFromBytes(final byte[] bytes, int offset, final int length) {
        LazyBinaryUtils.readVInt(bytes, offset, this.vInt);
        this.scale = this.vInt.value;
        offset += this.vInt.length;
        LazyBinaryUtils.readVInt(bytes, offset, this.vInt);
        offset += this.vInt.length;
        if (this.internalStorage.length != this.vInt.value) {
            this.internalStorage = new byte[this.vInt.value];
        }
        System.arraycopy(bytes, offset, this.internalStorage, 0, this.vInt.value);
    }
    
    public HiveDecimal getHiveDecimal() {
        return HiveDecimal.create(new BigInteger(this.internalStorage), this.scale);
    }
    
    public HiveDecimal getHiveDecimal(final int maxPrecision, final int maxScale) {
        return HiveDecimalUtils.enforcePrecisionScale(HiveDecimal.create(new BigInteger(this.internalStorage), this.scale), maxPrecision, maxScale);
    }
    
    @Override
    public void readFields(final DataInput in) throws IOException {
        this.scale = WritableUtils.readVInt(in);
        final int byteArrayLen = WritableUtils.readVInt(in);
        if (this.internalStorage.length != byteArrayLen) {
            this.internalStorage = new byte[byteArrayLen];
        }
        in.readFully(this.internalStorage);
    }
    
    @Override
    public void write(final DataOutput out) throws IOException {
        WritableUtils.writeVInt(out, this.scale);
        WritableUtils.writeVInt(out, this.internalStorage.length);
        out.write(this.internalStorage);
    }
    
    @Override
    public int compareTo(final HiveDecimalWritable that) {
        return this.getHiveDecimal().compareTo(that.getHiveDecimal());
    }
    
    public static void writeToByteStream(final Decimal128 dec, final ByteStream.Output byteStream) {
        final HiveDecimal hd = HiveDecimal.create(dec.toBigDecimal());
        LazyBinaryUtils.writeVInt(byteStream, hd.scale());
        final byte[] bytes = hd.unscaledValue().toByteArray();
        LazyBinaryUtils.writeVInt(byteStream, bytes.length);
        byteStream.write(bytes, 0, bytes.length);
    }
    
    public void writeToByteStream(final ByteStream.RandomAccessOutput byteStream) {
        LazyBinaryUtils.writeVInt(byteStream, this.scale);
        LazyBinaryUtils.writeVInt(byteStream, this.internalStorage.length);
        byteStream.write(this.internalStorage, 0, this.internalStorage.length);
    }
    
    @Override
    public String toString() {
        return this.getHiveDecimal().toString();
    }
    
    @Override
    public boolean equals(final Object other) {
        if (other == null || !(other instanceof HiveDecimalWritable)) {
            return false;
        }
        final HiveDecimalWritable bdw = (HiveDecimalWritable)other;
        return this.getHiveDecimal().compareTo(bdw.getHiveDecimal()) == 0;
    }
    
    @Override
    public int hashCode() {
        return this.getHiveDecimal().hashCode();
    }
    
    public byte[] getInternalStorage() {
        return this.internalStorage;
    }
    
    public int getScale() {
        return this.scale;
    }
    
    static {
        LOG = LogFactory.getLog(HiveDecimalWritable.class);
    }
}
