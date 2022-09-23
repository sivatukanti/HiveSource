// 
// Decompiled by Procyon v0.5.36
// 

package parquet.example.data.simple;

import parquet.io.api.RecordConsumer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import parquet.Preconditions;
import parquet.io.api.Binary;

public class NanoTime extends Primitive
{
    private final int julianDay;
    private final long timeOfDayNanos;
    
    public static NanoTime fromBinary(final Binary bytes) {
        Preconditions.checkArgument(bytes.length() == 12, "Must be 12 bytes");
        final ByteBuffer buf = bytes.toByteBuffer();
        buf.order(ByteOrder.LITTLE_ENDIAN);
        final long timeOfDayNanos = buf.getLong();
        final int julianDay = buf.getInt();
        return new NanoTime(julianDay, timeOfDayNanos);
    }
    
    public static NanoTime fromInt96(final Int96Value int96) {
        final ByteBuffer buf = int96.getInt96().toByteBuffer();
        return new NanoTime(buf.getInt(), buf.getLong());
    }
    
    public NanoTime(final int julianDay, final long timeOfDayNanos) {
        this.julianDay = julianDay;
        this.timeOfDayNanos = timeOfDayNanos;
    }
    
    public int getJulianDay() {
        return this.julianDay;
    }
    
    public long getTimeOfDayNanos() {
        return this.timeOfDayNanos;
    }
    
    public Binary toBinary() {
        final ByteBuffer buf = ByteBuffer.allocate(12);
        buf.order(ByteOrder.LITTLE_ENDIAN);
        buf.putLong(this.timeOfDayNanos);
        buf.putInt(this.julianDay);
        buf.flip();
        return Binary.fromByteBuffer(buf);
    }
    
    public Int96Value toInt96() {
        return new Int96Value(this.toBinary());
    }
    
    @Override
    public void writeValue(final RecordConsumer recordConsumer) {
        recordConsumer.addBinary(this.toBinary());
    }
    
    @Override
    public String toString() {
        return "NanoTime{julianDay=" + this.julianDay + ", timeOfDayNanos=" + this.timeOfDayNanos + "}";
    }
}
