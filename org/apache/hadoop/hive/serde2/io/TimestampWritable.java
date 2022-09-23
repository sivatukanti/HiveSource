// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.io;

import java.text.SimpleDateFormat;
import org.apache.hadoop.hive.common.type.HiveDecimal;
import java.util.Date;
import java.io.DataOutput;
import java.io.OutputStream;
import java.io.IOException;
import java.io.DataInput;
import org.apache.hadoop.hive.serde2.lazybinary.LazyBinaryUtils;
import org.apache.hadoop.io.WritableUtils;
import org.apache.hadoop.hive.serde2.ByteStream;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.math.BigDecimal;
import org.apache.hadoop.io.WritableComparable;

public class TimestampWritable implements WritableComparable<TimestampWritable>
{
    public static final byte[] nullBytes;
    private static final int DECIMAL_OR_SECOND_VINT_FLAG = Integer.MIN_VALUE;
    private static final int LOWEST_31_BITS_OF_SEC_MASK = Integer.MAX_VALUE;
    private static final long SEVEN_BYTE_LONG_SIGN_FLIP = -36028797018963968L;
    private static final BigDecimal BILLION_BIG_DECIMAL;
    public static final int MAX_BYTES = 13;
    public static final int BINARY_SORTABLE_LENGTH = 11;
    private static final ThreadLocal<DateFormat> threadLocalDateFormat;
    private Timestamp timestamp;
    private boolean bytesEmpty;
    private boolean timestampEmpty;
    private byte[] currentBytes;
    private final byte[] internalBytes;
    private byte[] externalBytes;
    private int offset;
    
    public TimestampWritable() {
        this.timestamp = new Timestamp(0L);
        this.internalBytes = new byte[13];
        this.bytesEmpty = false;
        this.currentBytes = this.internalBytes;
        this.offset = 0;
        this.clearTimestamp();
    }
    
    public TimestampWritable(final byte[] bytes, final int offset) {
        this.timestamp = new Timestamp(0L);
        this.internalBytes = new byte[13];
        this.set(bytes, offset);
    }
    
    public TimestampWritable(final TimestampWritable t) {
        this(t.getBytes(), 0);
    }
    
    public TimestampWritable(final Timestamp t) {
        this.timestamp = new Timestamp(0L);
        this.internalBytes = new byte[13];
        this.set(t);
    }
    
    public void set(final byte[] bytes, final int offset) {
        this.externalBytes = bytes;
        this.offset = offset;
        this.bytesEmpty = false;
        this.currentBytes = this.externalBytes;
        this.clearTimestamp();
    }
    
    public void setTime(final long time) {
        this.timestamp.setTime(time);
        this.bytesEmpty = true;
        this.timestampEmpty = false;
    }
    
    public void set(final Timestamp t) {
        if (t == null) {
            this.timestamp.setTime(0L);
            this.timestamp.setNanos(0);
            return;
        }
        this.timestamp = t;
        this.bytesEmpty = true;
        this.timestampEmpty = false;
    }
    
    public void set(final TimestampWritable t) {
        if (t.bytesEmpty) {
            this.set(t.getTimestamp());
            return;
        }
        if (t.currentBytes == t.externalBytes) {
            this.set(t.currentBytes, t.offset);
        }
        else {
            this.set(t.currentBytes, 0);
        }
    }
    
    private void clearTimestamp() {
        this.timestampEmpty = true;
    }
    
    public void writeToByteStream(final ByteStream.RandomAccessOutput byteStream) {
        this.checkBytes();
        byteStream.write(this.currentBytes, this.offset, this.getTotalLength());
    }
    
    public long getSeconds() {
        if (!this.timestampEmpty) {
            return millisToSeconds(this.timestamp.getTime());
        }
        if (!this.bytesEmpty) {
            return getSeconds(this.currentBytes, this.offset);
        }
        throw new IllegalStateException("Both timestamp and bytes are empty");
    }
    
    public int getNanos() {
        if (!this.timestampEmpty) {
            return this.timestamp.getNanos();
        }
        if (!this.bytesEmpty) {
            return this.hasDecimalOrSecondVInt() ? getNanos(this.currentBytes, this.offset + 4) : 0;
        }
        throw new IllegalStateException("Both timestamp and bytes are empty");
    }
    
    int getTotalLength() {
        this.checkBytes();
        return getTotalLength(this.currentBytes, this.offset);
    }
    
    public static int getTotalLength(final byte[] bytes, final int offset) {
        int len = 4;
        if (hasDecimalOrSecondVInt(bytes[offset])) {
            final int firstVIntLen = WritableUtils.decodeVIntSize(bytes[offset + 4]);
            len += firstVIntLen;
            if (hasSecondVInt(bytes[offset + 4])) {
                len += WritableUtils.decodeVIntSize(bytes[offset + 4 + firstVIntLen]);
            }
        }
        return len;
    }
    
    public Timestamp getTimestamp() {
        if (this.timestampEmpty) {
            this.populateTimestamp();
        }
        return this.timestamp;
    }
    
    public byte[] getBytes() {
        this.checkBytes();
        final int len = this.getTotalLength();
        final byte[] b = new byte[len];
        System.arraycopy(this.currentBytes, this.offset, b, 0, len);
        return b;
    }
    
    public byte[] getBinarySortable() {
        final byte[] b = new byte[11];
        final int nanos = this.getNanos();
        final long seconds = this.getSeconds() ^ 0xFF80000000000000L;
        sevenByteLongToBytes(seconds, b, 0);
        intToBytes(nanos, b, 7);
        return b;
    }
    
    public void setBinarySortable(final byte[] bytes, final int binSortOffset) {
        final long seconds = readSevenByteLong(bytes, binSortOffset) ^ 0xFF80000000000000L;
        final int nanos = bytesToInt(bytes, binSortOffset + 7);
        int firstInt = (int)seconds;
        final boolean hasSecondVInt = seconds < 0L || seconds > 2147483647L;
        if (nanos != 0 || hasSecondVInt) {
            firstInt |= Integer.MIN_VALUE;
        }
        else {
            firstInt &= Integer.MAX_VALUE;
        }
        intToBytes(firstInt, this.internalBytes, 0);
        setNanosBytes(nanos, this.internalBytes, 4, hasSecondVInt);
        if (hasSecondVInt) {
            LazyBinaryUtils.writeVLongToByteArray(this.internalBytes, 4 + WritableUtils.decodeVIntSize(this.internalBytes[4]), seconds >> 31);
        }
        this.currentBytes = this.internalBytes;
        this.offset = 0;
    }
    
    private void checkBytes() {
        if (this.bytesEmpty) {
            convertTimestampToBytes(this.timestamp, this.internalBytes, 0);
            this.offset = 0;
            this.currentBytes = this.internalBytes;
            this.bytesEmpty = false;
        }
    }
    
    public double getDouble() {
        double seconds;
        double nanos;
        if (this.bytesEmpty) {
            seconds = (double)millisToSeconds(this.timestamp.getTime());
            nanos = this.timestamp.getNanos();
        }
        else {
            seconds = (double)this.getSeconds();
            nanos = this.getNanos();
        }
        return seconds + nanos / 1.0E9;
    }
    
    @Override
    public void readFields(final DataInput in) throws IOException {
        in.readFully(this.internalBytes, 0, 4);
        if (hasDecimalOrSecondVInt(this.internalBytes[0])) {
            in.readFully(this.internalBytes, 4, 1);
            final int len = (byte)WritableUtils.decodeVIntSize(this.internalBytes[4]);
            if (len > 1) {
                in.readFully(this.internalBytes, 5, len - 1);
            }
            final long vlong = LazyBinaryUtils.readVLongFromByteArray(this.internalBytes, 4);
            if (vlong < -1000000000L || vlong > 999999999L) {
                throw new IOException("Invalid first vint value (encoded nanoseconds) of a TimestampWritable: " + vlong + ", expected to be between -1000000000 and 999999999.");
            }
            if (vlong < 0L) {
                in.readFully(this.internalBytes, 4 + len, 1);
                final int secondVIntLen = (byte)WritableUtils.decodeVIntSize(this.internalBytes[4 + len]);
                if (secondVIntLen > 1) {
                    in.readFully(this.internalBytes, 5 + len, secondVIntLen - 1);
                }
            }
        }
        this.currentBytes = this.internalBytes;
        this.offset = 0;
    }
    
    public void write(final OutputStream out) throws IOException {
        this.checkBytes();
        out.write(this.currentBytes, this.offset, this.getTotalLength());
    }
    
    @Override
    public void write(final DataOutput out) throws IOException {
        this.write((OutputStream)out);
    }
    
    @Override
    public int compareTo(final TimestampWritable t) {
        this.checkBytes();
        final long s1 = this.getSeconds();
        final long s2 = t.getSeconds();
        if (s1 != s2) {
            return (s1 < s2) ? -1 : 1;
        }
        final int n1 = this.getNanos();
        final int n2 = t.getNanos();
        if (n1 == n2) {
            return 0;
        }
        return n1 - n2;
    }
    
    @Override
    public boolean equals(final Object o) {
        return this.compareTo((TimestampWritable)o) == 0;
    }
    
    @Override
    public String toString() {
        if (this.timestampEmpty) {
            this.populateTimestamp();
        }
        final String timestampString = this.timestamp.toString();
        if (timestampString.length() <= 19) {
            return TimestampWritable.threadLocalDateFormat.get().format(this.timestamp);
        }
        if (timestampString.length() == 21 && timestampString.substring(19).compareTo(".0") == 0) {
            return TimestampWritable.threadLocalDateFormat.get().format(this.timestamp);
        }
        return TimestampWritable.threadLocalDateFormat.get().format(this.timestamp) + timestampString.substring(19);
    }
    
    @Override
    public int hashCode() {
        long seconds = this.getSeconds();
        seconds <<= 30;
        seconds |= this.getNanos();
        return (int)(seconds >>> 32 ^ seconds);
    }
    
    private void populateTimestamp() {
        final long seconds = this.getSeconds();
        final int nanos = this.getNanos();
        this.timestamp.setTime(seconds * 1000L);
        this.timestamp.setNanos(nanos);
    }
    
    public static long getSeconds(final byte[] bytes, final int offset) {
        final int lowest31BitsOfSecondsAndFlag = bytesToInt(bytes, offset);
        if (lowest31BitsOfSecondsAndFlag >= 0 || !hasSecondVInt(bytes[offset + 4])) {
            return lowest31BitsOfSecondsAndFlag & Integer.MAX_VALUE;
        }
        return (long)(lowest31BitsOfSecondsAndFlag & Integer.MAX_VALUE) | LazyBinaryUtils.readVLongFromByteArray(bytes, offset + 4 + WritableUtils.decodeVIntSize(bytes[offset + 4])) << 31;
    }
    
    public static int getNanos(final byte[] bytes, final int offset) {
        final LazyBinaryUtils.VInt vInt = LazyBinaryUtils.threadLocalVInt.get();
        LazyBinaryUtils.readVInt(bytes, offset, vInt);
        int val = vInt.value;
        if (val < 0) {
            val = -val - 1;
        }
        final int len = (int)Math.floor(Math.log10(val)) + 1;
        int tmp = 0;
        while (val != 0) {
            tmp *= 10;
            tmp += val % 10;
            val /= 10;
        }
        val = tmp;
        if (len < 9) {
            val *= (int)Math.pow(10.0, 9 - len);
        }
        return val;
    }
    
    public static void convertTimestampToBytes(final Timestamp t, final byte[] b, final int offset) {
        final long millis = t.getTime();
        final int nanos = t.getNanos();
        final long seconds = millisToSeconds(millis);
        final boolean hasSecondVInt = seconds < 0L || seconds > 2147483647L;
        final boolean hasDecimal = setNanosBytes(nanos, b, offset + 4, hasSecondVInt);
        int firstInt = (int)seconds;
        if (hasDecimal || hasSecondVInt) {
            firstInt |= Integer.MIN_VALUE;
        }
        else {
            firstInt &= Integer.MAX_VALUE;
        }
        intToBytes(firstInt, b, offset);
        if (hasSecondVInt) {
            LazyBinaryUtils.writeVLongToByteArray(b, offset + 4 + WritableUtils.decodeVIntSize(b[offset + 4]), seconds >> 31);
        }
    }
    
    private static boolean setNanosBytes(int nanos, final byte[] b, final int offset, final boolean hasSecondVInt) {
        int decimal = 0;
        if (nanos != 0) {
            for (int counter = 0; counter < 9; ++counter) {
                decimal *= 10;
                decimal += nanos % 10;
                nanos /= 10;
            }
        }
        if (hasSecondVInt || decimal != 0) {
            LazyBinaryUtils.writeVLongToByteArray(b, offset, hasSecondVInt ? ((long)(-decimal - 1)) : ((long)decimal));
        }
        return decimal != 0;
    }
    
    public static Timestamp decimalToTimestamp(final HiveDecimal d) {
        final BigDecimal nanoInstant = d.bigDecimalValue().multiply(TimestampWritable.BILLION_BIG_DECIMAL);
        int nanos = nanoInstant.remainder(TimestampWritable.BILLION_BIG_DECIMAL).intValue();
        if (nanos < 0) {
            nanos += 1000000000;
        }
        final long seconds = nanoInstant.subtract(new BigDecimal(nanos)).divide(TimestampWritable.BILLION_BIG_DECIMAL).longValue();
        final Timestamp t = new Timestamp(seconds * 1000L);
        t.setNanos(nanos);
        return t;
    }
    
    public static Timestamp longToTimestamp(final long time, final boolean intToTimestampInSeconds) {
        return new Timestamp(intToTimestampInSeconds ? (time * 1000L) : time);
    }
    
    public static Timestamp doubleToTimestamp(final double f) {
        final long seconds = (long)f;
        BigDecimal bd = new BigDecimal(String.valueOf(f));
        bd = bd.subtract(new BigDecimal(seconds)).multiply(new BigDecimal(1000000000));
        int nanos = bd.intValue();
        long millis = seconds * 1000L;
        if (nanos < 0) {
            millis -= 1000L;
            nanos += 1000000000;
        }
        final Timestamp t = new Timestamp(millis);
        t.setNanos(nanos);
        return t;
    }
    
    public static void setTimestamp(final Timestamp t, final byte[] bytes, final int offset) {
        final boolean hasDecimalOrSecondVInt = hasDecimalOrSecondVInt(bytes[offset]);
        long seconds = getSeconds(bytes, offset);
        int nanos = 0;
        if (hasDecimalOrSecondVInt) {
            nanos = getNanos(bytes, offset + 4);
            if (hasSecondVInt(bytes[offset + 4])) {
                seconds += LazyBinaryUtils.readVLongFromByteArray(bytes, offset + 4 + WritableUtils.decodeVIntSize(bytes[offset + 4]));
            }
        }
        t.setTime(seconds * 1000L);
        if (nanos != 0) {
            t.setNanos(nanos);
        }
    }
    
    public static Timestamp createTimestamp(final byte[] bytes, final int offset) {
        final Timestamp t = new Timestamp(0L);
        setTimestamp(t, bytes, offset);
        return t;
    }
    
    private static boolean hasDecimalOrSecondVInt(final byte b) {
        return b >> 7 != 0;
    }
    
    private static boolean hasSecondVInt(final byte b) {
        return WritableUtils.isNegativeVInt(b);
    }
    
    private final boolean hasDecimalOrSecondVInt() {
        return hasDecimalOrSecondVInt(this.currentBytes[this.offset]);
    }
    
    public final boolean hasDecimal() {
        return this.hasDecimalOrSecondVInt() || this.currentBytes[this.offset + 4] != -1;
    }
    
    private static void intToBytes(final int value, final byte[] dest, final int offset) {
        dest[offset] = (byte)(value >> 24 & 0xFF);
        dest[offset + 1] = (byte)(value >> 16 & 0xFF);
        dest[offset + 2] = (byte)(value >> 8 & 0xFF);
        dest[offset + 3] = (byte)(value & 0xFF);
    }
    
    static void sevenByteLongToBytes(final long value, final byte[] dest, final int offset) {
        dest[offset] = (byte)(value >> 48 & 0xFFL);
        dest[offset + 1] = (byte)(value >> 40 & 0xFFL);
        dest[offset + 2] = (byte)(value >> 32 & 0xFFL);
        dest[offset + 3] = (byte)(value >> 24 & 0xFFL);
        dest[offset + 4] = (byte)(value >> 16 & 0xFFL);
        dest[offset + 5] = (byte)(value >> 8 & 0xFFL);
        dest[offset + 6] = (byte)(value & 0xFFL);
    }
    
    private static int bytesToInt(final byte[] bytes, final int offset) {
        return (0xFF & bytes[offset]) << 24 | (0xFF & bytes[offset + 1]) << 16 | (0xFF & bytes[offset + 2]) << 8 | (0xFF & bytes[offset + 3]);
    }
    
    static long readSevenByteLong(final byte[] bytes, final int offset) {
        return ((0xFFL & (long)bytes[offset]) << 56 | (0xFFL & (long)bytes[offset + 1]) << 48 | (0xFFL & (long)bytes[offset + 2]) << 40 | (0xFFL & (long)bytes[offset + 3]) << 32 | (0xFFL & (long)bytes[offset + 4]) << 24 | (0xFFL & (long)bytes[offset + 5]) << 16 | (0xFFL & (long)bytes[offset + 6]) << 8) >> 8;
    }
    
    static long millisToSeconds(final long millis) {
        if (millis >= 0L) {
            return millis / 1000L;
        }
        return (millis - 999L) / 1000L;
    }
    
    static {
        nullBytes = new byte[] { 0, 0, 0, 0 };
        BILLION_BIG_DECIMAL = BigDecimal.valueOf(1000000000L);
        threadLocalDateFormat = new ThreadLocal<DateFormat>() {
            @Override
            protected synchronized DateFormat initialValue() {
                return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            }
        };
    }
}
