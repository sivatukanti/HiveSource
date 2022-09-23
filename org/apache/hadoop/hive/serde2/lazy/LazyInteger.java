// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.lazy;

import java.io.IOException;
import java.io.OutputStream;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.hive.serde2.lazy.objectinspector.primitive.LazyIntObjectInspector;

public class LazyInteger extends LazyPrimitive<LazyIntObjectInspector, IntWritable>
{
    public LazyInteger(final LazyIntObjectInspector oi) {
        super(oi);
        this.data = (T)new IntWritable();
    }
    
    public LazyInteger(final LazyInteger copy) {
        super(copy);
        this.data = (T)new IntWritable(((IntWritable)copy.data).get());
    }
    
    @Override
    public void init(final ByteArrayRef bytes, final int start, final int length) {
        try {
            ((IntWritable)this.data).set(parseInt(bytes.getData(), start, length, 10));
            this.isNull = false;
        }
        catch (NumberFormatException e) {
            this.isNull = true;
            this.logExceptionMessage(bytes, start, length, "INT");
        }
    }
    
    public static int parseInt(final byte[] bytes, final int start, final int length) {
        return parseInt(bytes, start, length, 10);
    }
    
    public static int parseInt(final byte[] bytes, final int start, final int length, final int radix) {
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
    
    private static int parse(final byte[] bytes, final int start, final int length, int offset, final int radix, final boolean negative) {
        final byte separator = 46;
        final int max = Integer.MIN_VALUE / radix;
        int result = 0;
        final int end = start + length;
        while (offset < end) {
            final int digit = LazyUtils.digit(bytes[offset++], radix);
            if (digit == -1) {
                if (bytes[offset - 1] == separator) {
                    break;
                }
                throw new NumberFormatException(LazyUtils.convertToString(bytes, start, length));
            }
            else {
                if (max > result) {
                    throw new NumberFormatException(LazyUtils.convertToString(bytes, start, length));
                }
                final int next = result * radix - digit;
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
            if (result < 0) {
                throw new NumberFormatException(LazyUtils.convertToString(bytes, start, length));
            }
        }
        return result;
    }
    
    public static void writeUTF8(final OutputStream out, int i) throws IOException {
        if (i == 0) {
            out.write(48);
            return;
        }
        final boolean negative = i < 0;
        if (negative) {
            out.write(45);
        }
        else {
            i = -i;
        }
        int start;
        for (start = 1000000000; i / start == 0; start /= 10) {}
        while (start > 0) {
            out.write(48 - i / start % 10);
            start /= 10;
        }
    }
    
    public static void writeUTF8NoException(final OutputStream out, final int i) {
        try {
            writeUTF8(out, i);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
