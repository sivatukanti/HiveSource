// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.lazy;

import java.io.IOException;
import java.io.OutputStream;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.hive.serde2.lazy.objectinspector.primitive.LazyLongObjectInspector;

public class LazyLong extends LazyPrimitive<LazyLongObjectInspector, LongWritable>
{
    public LazyLong(final LazyLongObjectInspector oi) {
        super(oi);
        this.data = (T)new LongWritable();
    }
    
    public LazyLong(final LazyLong copy) {
        super(copy);
        this.data = (T)new LongWritable(((LongWritable)copy.data).get());
    }
    
    @Override
    public void init(final ByteArrayRef bytes, final int start, final int length) {
        try {
            ((LongWritable)this.data).set(parseLong(bytes.getData(), start, length, 10));
            this.isNull = false;
        }
        catch (NumberFormatException e) {
            this.isNull = true;
            this.logExceptionMessage(bytes, start, length, "BIGINT");
        }
    }
    
    public static long parseLong(final byte[] bytes, final int start, final int length) {
        return parseLong(bytes, start, length, 10);
    }
    
    public static long parseLong(final byte[] bytes, final int start, final int length, final int radix) {
        if (bytes == null) {
            throw new NumberFormatException("String is null");
        }
        if (radix < 2 || radix > 36) {
            throw new NumberFormatException("Invalid radix: " + radix);
        }
        if (length == 0) {
            throw new NumberFormatException("Empty string!");
        }
        int offset = start;
        final boolean negative = bytes[start] == 45;
        if (negative || bytes[start] == 43) {
            ++offset;
            if (length == 1) {
                throw new NumberFormatException(LazyUtils.convertToString(bytes, start, length));
            }
        }
        return parse(bytes, start, length, offset, radix, negative);
    }
    
    private static long parse(final byte[] bytes, final int start, final int length, int offset, final int radix, final boolean negative) {
        final byte separator = 46;
        final long max = Long.MIN_VALUE / radix;
        long result = 0L;
        final long end = start + length;
        while (offset < end) {
            final int digit = LazyUtils.digit(bytes[offset++], radix);
            if (digit == -1 || max > result) {
                if (bytes[offset - 1] == separator) {
                    break;
                }
                throw new NumberFormatException(LazyUtils.convertToString(bytes, start, length));
            }
            else {
                final long next = result * radix - digit;
                if (next > result) {
                    throw new NumberFormatException(LazyUtils.convertToString(bytes, start, length));
                }
                result = next;
            }
        }
        while (offset < end) {
            final int digit = LazyUtils.digit(bytes[offset++], radix);
            if (digit == -1) {
                throw new NumberFormatException(LazyUtils.convertToString(bytes, start, length));
            }
        }
        if (!negative) {
            result = -result;
            if (result < 0L) {
                throw new NumberFormatException(LazyUtils.convertToString(bytes, start, length));
            }
        }
        return result;
    }
    
    public static void writeUTF8(final OutputStream out, long i) throws IOException {
        if (i == 0L) {
            out.write(48);
            return;
        }
        final boolean negative = i < 0L;
        if (negative) {
            out.write(45);
        }
        else {
            i = -i;
        }
        long start;
        for (start = 1000000000000000000L; i / start == 0L; start /= 10L) {}
        while (start > 0L) {
            out.write(48 - (int)(i / start % 10L));
            start /= 10L;
        }
    }
    
    public static void writeUTF8NoException(final OutputStream out, final long i) {
        try {
            writeUTF8(out, i);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
