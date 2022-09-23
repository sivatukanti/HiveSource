// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.avro.io;

import java.util.Iterator;
import org.apache.avro.generic.GenericDatumReader;
import java.io.IOException;
import org.apache.avro.AvroRuntimeException;
import org.apache.avro.Schema;

public class BinaryData
{
    private static final ThreadLocal<Decoders> DECODERS;
    private static final ThreadLocal<HashData> HASH_DATA;
    
    private BinaryData() {
    }
    
    public static int compare(final byte[] b1, final int s1, final byte[] b2, final int s2, final Schema schema) {
        return compare(b1, s1, b1.length - s1, b2, s2, b2.length - s2, schema);
    }
    
    public static int compare(final byte[] b1, final int s1, final int l1, final byte[] b2, final int s2, final int l2, final Schema schema) {
        final Decoders decoders = BinaryData.DECODERS.get();
        decoders.set(b1, s1, l1, b2, s2, l2);
        try {
            return compare(decoders, schema);
        }
        catch (IOException e) {
            throw new AvroRuntimeException(e);
        }
        finally {
            decoders.clear();
        }
    }
    
    private static int compare(final Decoders d, final Schema schema) throws IOException {
        final Decoder d2 = d.d1;
        final Decoder d3 = d.d2;
        switch (schema.getType()) {
            case RECORD: {
                for (final Schema.Field field : schema.getFields()) {
                    if (field.order() == Schema.Field.Order.IGNORE) {
                        GenericDatumReader.skip(field.schema(), d2);
                        GenericDatumReader.skip(field.schema(), d3);
                    }
                    else {
                        final int c = compare(d, field.schema());
                        if (c != 0) {
                            return (field.order() != Schema.Field.Order.DESCENDING) ? c : (-c);
                        }
                        continue;
                    }
                }
                return 0;
            }
            case ENUM:
            case INT: {
                final int i1 = d2.readInt();
                final int i2 = d3.readInt();
                return (i1 == i2) ? 0 : ((i1 > i2) ? 1 : -1);
            }
            case LONG: {
                final long l1 = d2.readLong();
                final long l2 = d3.readLong();
                return (l1 == l2) ? 0 : ((l1 > l2) ? 1 : -1);
            }
            case ARRAY: {
                long j = 0L;
                long r1 = 0L;
                long r2 = 0L;
                long l3 = 0L;
                long l4 = 0L;
                while (true) {
                    if (r1 == 0L) {
                        r1 = d2.readLong();
                        if (r1 < 0L) {
                            r1 = -r1;
                            d2.readLong();
                        }
                        l3 += r1;
                    }
                    if (r2 == 0L) {
                        r2 = d3.readLong();
                        if (r2 < 0L) {
                            r2 = -r2;
                            d3.readLong();
                        }
                        l4 += r2;
                    }
                    if (r1 == 0L || r2 == 0L) {
                        return (l3 == l4) ? 0 : ((l3 > l4) ? 1 : -1);
                    }
                    for (long k = Math.min(l3, l4); j < k; ++j, --r1, --r2) {
                        final int c2 = compare(d, schema.getElementType());
                        if (c2 != 0) {
                            return c2;
                        }
                    }
                }
                break;
            }
            case MAP: {
                throw new AvroRuntimeException("Can't compare maps!");
            }
            case UNION: {
                final int i1 = d2.readInt();
                final int i2 = d3.readInt();
                if (i1 == i2) {
                    return compare(d, schema.getTypes().get(i1));
                }
                return i1 - i2;
            }
            case FIXED: {
                final int size = schema.getFixedSize();
                final int c3 = compareBytes(d.d1.getBuf(), d.d1.getPos(), size, d.d2.getBuf(), d.d2.getPos(), size);
                d.d1.skipFixed(size);
                d.d2.skipFixed(size);
                return c3;
            }
            case STRING:
            case BYTES: {
                final int l5 = d2.readInt();
                final int l6 = d3.readInt();
                final int c = compareBytes(d.d1.getBuf(), d.d1.getPos(), l5, d.d2.getBuf(), d.d2.getPos(), l6);
                d.d1.skipFixed(l5);
                d.d2.skipFixed(l6);
                return c;
            }
            case FLOAT: {
                final float f1 = d2.readFloat();
                final float f2 = d3.readFloat();
                return (f1 == f2) ? 0 : ((f1 > f2) ? 1 : -1);
            }
            case DOUBLE: {
                final double f3 = d2.readDouble();
                final double f4 = d3.readDouble();
                return (f3 == f4) ? 0 : ((f3 > f4) ? 1 : -1);
            }
            case BOOLEAN: {
                final boolean b1 = d2.readBoolean();
                final boolean b2 = d3.readBoolean();
                return (b1 == b2) ? 0 : (b1 ? 1 : -1);
            }
            case NULL: {
                return 0;
            }
            default: {
                throw new AvroRuntimeException("Unexpected schema to compare!");
            }
        }
    }
    
    public static int compareBytes(final byte[] b1, final int s1, final int l1, final byte[] b2, final int s2, final int l2) {
        for (int end1 = s1 + l1, end2 = s2 + l2, i = s1, j = s2; i < end1 && j < end2; ++i, ++j) {
            final int a = b1[i] & 0xFF;
            final int b3 = b2[j] & 0xFF;
            if (a != b3) {
                return a - b3;
            }
        }
        return l1 - l2;
    }
    
    public static int hashCode(final byte[] bytes, final int start, final int length, final Schema schema) {
        final HashData data = BinaryData.HASH_DATA.get();
        data.set(bytes, start, length);
        try {
            return hashCode(data, schema);
        }
        catch (IOException e) {
            throw new AvroRuntimeException(e);
        }
    }
    
    private static int hashCode(final HashData data, final Schema schema) throws IOException {
        final Decoder decoder = data.decoder;
        switch (schema.getType()) {
            case RECORD: {
                int hashCode = 1;
                for (final Schema.Field field : schema.getFields()) {
                    if (field.order() == Schema.Field.Order.IGNORE) {
                        GenericDatumReader.skip(field.schema(), decoder);
                    }
                    else {
                        hashCode = hashCode * 31 + hashCode(data, field.schema());
                    }
                }
                return hashCode;
            }
            case ENUM:
            case INT: {
                return decoder.readInt();
            }
            case FLOAT: {
                return Float.floatToIntBits(decoder.readFloat());
            }
            case LONG: {
                final long l = decoder.readLong();
                return (int)(l ^ l >>> 32);
            }
            case DOUBLE: {
                final long l = Double.doubleToLongBits(decoder.readDouble());
                return (int)(l ^ l >>> 32);
            }
            case ARRAY: {
                final Schema elementType = schema.getElementType();
                int hashCode2 = 1;
                for (long i = decoder.readArrayStart(); i != 0L; i = decoder.arrayNext()) {
                    for (long j = 0L; j < i; ++j) {
                        hashCode2 = hashCode2 * 31 + hashCode(data, elementType);
                    }
                }
                return hashCode2;
            }
            case MAP: {
                throw new AvroRuntimeException("Can't hashCode maps!");
            }
            case UNION: {
                return hashCode(data, schema.getTypes().get(decoder.readInt()));
            }
            case FIXED: {
                return hashBytes(1, data, schema.getFixedSize(), false);
            }
            case STRING: {
                return hashBytes(0, data, decoder.readInt(), false);
            }
            case BYTES: {
                return hashBytes(1, data, decoder.readInt(), true);
            }
            case BOOLEAN: {
                return decoder.readBoolean() ? 1231 : 1237;
            }
            case NULL: {
                return 0;
            }
            default: {
                throw new AvroRuntimeException("Unexpected schema to hashCode!");
            }
        }
    }
    
    private static int hashBytes(final int init, final HashData data, final int len, final boolean rev) throws IOException {
        int hashCode = init;
        final byte[] bytes = data.decoder.getBuf();
        final int start = data.decoder.getPos();
        final int end = start + len;
        if (rev) {
            for (int i = end - 1; i >= start; --i) {
                hashCode = hashCode * 31 + bytes[i];
            }
        }
        else {
            for (int i = start; i < end; ++i) {
                hashCode = hashCode * 31 + bytes[i];
            }
        }
        data.decoder.skipFixed(len);
        return hashCode;
    }
    
    public static int skipLong(final byte[] bytes, final int start) {
        int i = start;
        for (int b = bytes[i++]; (b & 0x80) != 0x0; b = bytes[i++]) {}
        return i;
    }
    
    public static int encodeBoolean(final boolean b, final byte[] buf, final int pos) {
        buf[pos] = (byte)(b ? 1 : 0);
        return 1;
    }
    
    public static int encodeInt(int n, final byte[] buf, int pos) {
        n = (n << 1 ^ n >> 31);
        final int start = pos;
        if ((n & 0xFFFFFF80) != 0x0) {
            buf[pos++] = (byte)((n | 0x80) & 0xFF);
            n >>>= 7;
            if (n > 127) {
                buf[pos++] = (byte)((n | 0x80) & 0xFF);
                n >>>= 7;
                if (n > 127) {
                    buf[pos++] = (byte)((n | 0x80) & 0xFF);
                    n >>>= 7;
                    if (n > 127) {
                        buf[pos++] = (byte)((n | 0x80) & 0xFF);
                        n >>>= 7;
                    }
                }
            }
        }
        buf[pos++] = (byte)n;
        return pos - start;
    }
    
    public static int encodeLong(long n, final byte[] buf, int pos) {
        n = (n << 1 ^ n >> 63);
        final int start = pos;
        if ((n & 0xFFFFFFFFFFFFFF80L) != 0x0L) {
            buf[pos++] = (byte)((n | 0x80L) & 0xFFL);
            n >>>= 7;
            if (n > 127L) {
                buf[pos++] = (byte)((n | 0x80L) & 0xFFL);
                n >>>= 7;
                if (n > 127L) {
                    buf[pos++] = (byte)((n | 0x80L) & 0xFFL);
                    n >>>= 7;
                    if (n > 127L) {
                        buf[pos++] = (byte)((n | 0x80L) & 0xFFL);
                        n >>>= 7;
                        if (n > 127L) {
                            buf[pos++] = (byte)((n | 0x80L) & 0xFFL);
                            n >>>= 7;
                            if (n > 127L) {
                                buf[pos++] = (byte)((n | 0x80L) & 0xFFL);
                                n >>>= 7;
                                if (n > 127L) {
                                    buf[pos++] = (byte)((n | 0x80L) & 0xFFL);
                                    n >>>= 7;
                                    if (n > 127L) {
                                        buf[pos++] = (byte)((n | 0x80L) & 0xFFL);
                                        n >>>= 7;
                                        if (n > 127L) {
                                            buf[pos++] = (byte)((n | 0x80L) & 0xFFL);
                                            n >>>= 7;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        buf[pos++] = (byte)n;
        return pos - start;
    }
    
    public static int encodeFloat(final float f, final byte[] buf, final int pos) {
        int len = 1;
        final int bits = Float.floatToRawIntBits(f);
        buf[pos] = (byte)(bits & 0xFF);
        buf[pos + len++] = (byte)(bits >>> 8 & 0xFF);
        buf[pos + len++] = (byte)(bits >>> 16 & 0xFF);
        buf[pos + len++] = (byte)(bits >>> 24 & 0xFF);
        return 4;
    }
    
    public static int encodeDouble(final double d, final byte[] buf, final int pos) {
        final long bits = Double.doubleToRawLongBits(d);
        final int first = (int)(bits & -1L);
        final int second = (int)(bits >>> 32 & -1L);
        buf[pos] = (byte)(first & 0xFF);
        buf[pos + 4] = (byte)(second & 0xFF);
        buf[pos + 5] = (byte)(second >>> 8 & 0xFF);
        buf[pos + 1] = (byte)(first >>> 8 & 0xFF);
        buf[pos + 2] = (byte)(first >>> 16 & 0xFF);
        buf[pos + 6] = (byte)(second >>> 16 & 0xFF);
        buf[pos + 7] = (byte)(second >>> 24 & 0xFF);
        buf[pos + 3] = (byte)(first >>> 24 & 0xFF);
        return 8;
    }
    
    static {
        DECODERS = new ThreadLocal<Decoders>() {
            @Override
            protected Decoders initialValue() {
                return new Decoders();
            }
        };
        HASH_DATA = new ThreadLocal<HashData>() {
            @Override
            protected HashData initialValue() {
                return new HashData();
            }
        };
    }
    
    private static class Decoders
    {
        private final BinaryDecoder d1;
        private final BinaryDecoder d2;
        
        public Decoders() {
            this.d1 = new BinaryDecoder(new byte[0], 0, 0);
            this.d2 = new BinaryDecoder(new byte[0], 0, 0);
        }
        
        public void set(final byte[] data1, final int off1, final int len1, final byte[] data2, final int off2, final int len2) {
            this.d1.setBuf(data1, off1, len1);
            this.d2.setBuf(data2, off2, len2);
        }
        
        public void clear() {
            this.d1.clearBuf();
            this.d2.clearBuf();
        }
    }
    
    private static class HashData
    {
        private final BinaryDecoder decoder;
        
        public HashData() {
            this.decoder = new BinaryDecoder(new byte[0], 0, 0);
        }
        
        public void set(final byte[] bytes, final int start, final int len) {
            this.decoder.setBuf(bytes, start, len);
        }
    }
}
