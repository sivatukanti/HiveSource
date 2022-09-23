// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io;

import org.apache.hadoop.util.ReflectionUtils;
import org.apache.hadoop.conf.Configuration;
import java.io.Closeable;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;
import java.io.DataOutput;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInput;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Stable
public final class WritableUtils
{
    public static byte[] readCompressedByteArray(final DataInput in) throws IOException {
        final int length = in.readInt();
        if (length == -1) {
            return null;
        }
        final byte[] buffer = new byte[length];
        in.readFully(buffer);
        final GZIPInputStream gzi = new GZIPInputStream(new ByteArrayInputStream(buffer, 0, buffer.length));
        final byte[] outbuf = new byte[length];
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        int len;
        while ((len = gzi.read(outbuf, 0, outbuf.length)) != -1) {
            bos.write(outbuf, 0, len);
        }
        final byte[] decompressed = bos.toByteArray();
        bos.close();
        gzi.close();
        return decompressed;
    }
    
    public static void skipCompressedByteArray(final DataInput in) throws IOException {
        final int length = in.readInt();
        if (length != -1) {
            skipFully(in, length);
        }
    }
    
    public static int writeCompressedByteArray(final DataOutput out, final byte[] bytes) throws IOException {
        if (bytes != null) {
            final ByteArrayOutputStream bos = new ByteArrayOutputStream();
            GZIPOutputStream gzout = new GZIPOutputStream(bos);
            try {
                gzout.write(bytes, 0, bytes.length);
                gzout.close();
                gzout = null;
            }
            finally {
                IOUtils.closeStream(gzout);
            }
            final byte[] buffer = bos.toByteArray();
            final int len = buffer.length;
            out.writeInt(len);
            out.write(buffer, 0, len);
            return (bytes.length != 0) ? (100 * buffer.length / bytes.length) : 0;
        }
        out.writeInt(-1);
        return -1;
    }
    
    public static String readCompressedString(final DataInput in) throws IOException {
        final byte[] bytes = readCompressedByteArray(in);
        if (bytes == null) {
            return null;
        }
        return new String(bytes, "UTF-8");
    }
    
    public static int writeCompressedString(final DataOutput out, final String s) throws IOException {
        return writeCompressedByteArray(out, (byte[])((s != null) ? s.getBytes("UTF-8") : null));
    }
    
    public static void writeString(final DataOutput out, final String s) throws IOException {
        if (s != null) {
            final byte[] buffer = s.getBytes("UTF-8");
            final int len = buffer.length;
            out.writeInt(len);
            out.write(buffer, 0, len);
        }
        else {
            out.writeInt(-1);
        }
    }
    
    public static String readString(final DataInput in) throws IOException {
        final int length = in.readInt();
        if (length == -1) {
            return null;
        }
        final byte[] buffer = new byte[length];
        in.readFully(buffer);
        return new String(buffer, "UTF-8");
    }
    
    public static void writeStringArray(final DataOutput out, final String[] s) throws IOException {
        out.writeInt(s.length);
        for (int i = 0; i < s.length; ++i) {
            writeString(out, s[i]);
        }
    }
    
    public static void writeCompressedStringArray(final DataOutput out, final String[] s) throws IOException {
        if (s == null) {
            out.writeInt(-1);
            return;
        }
        out.writeInt(s.length);
        for (int i = 0; i < s.length; ++i) {
            writeCompressedString(out, s[i]);
        }
    }
    
    public static String[] readStringArray(final DataInput in) throws IOException {
        final int len = in.readInt();
        if (len == -1) {
            return null;
        }
        final String[] s = new String[len];
        for (int i = 0; i < len; ++i) {
            s[i] = readString(in);
        }
        return s;
    }
    
    public static String[] readCompressedStringArray(final DataInput in) throws IOException {
        final int len = in.readInt();
        if (len == -1) {
            return null;
        }
        final String[] s = new String[len];
        for (int i = 0; i < len; ++i) {
            s[i] = readCompressedString(in);
        }
        return s;
    }
    
    public static void displayByteArray(final byte[] record) {
        int i;
        for (i = 0; i < record.length - 1; ++i) {
            if (i % 16 == 0) {
                System.out.println();
            }
            System.out.print(Integer.toHexString(record[i] >> 4 & 0xF));
            System.out.print(Integer.toHexString(record[i] & 0xF));
            System.out.print(",");
        }
        System.out.print(Integer.toHexString(record[i] >> 4 & 0xF));
        System.out.print(Integer.toHexString(record[i] & 0xF));
        System.out.println();
    }
    
    public static <T extends Writable> T clone(final T orig, final Configuration conf) {
        try {
            final T newInst = ReflectionUtils.newInstance(orig.getClass(), conf);
            ReflectionUtils.copy(conf, orig, newInst);
            return newInst;
        }
        catch (IOException e) {
            throw new RuntimeException("Error writing/reading clone buffer", e);
        }
    }
    
    @Deprecated
    public static void cloneInto(final Writable dst, final Writable src) throws IOException {
        ReflectionUtils.cloneWritableInto(dst, src);
    }
    
    public static void writeVInt(final DataOutput stream, final int i) throws IOException {
        writeVLong(stream, i);
    }
    
    public static void writeVLong(final DataOutput stream, long i) throws IOException {
        if (i >= -112L && i <= 127L) {
            stream.writeByte((byte)i);
            return;
        }
        int len = -112;
        if (i < 0L) {
            i ^= -1L;
            len = -120;
        }
        for (long tmp = i; tmp != 0L; tmp >>= 8, --len) {}
        stream.writeByte((byte)len);
        int idx;
        for (len = (idx = ((len < -120) ? (-(len + 120)) : (-(len + 112)))); idx != 0; --idx) {
            final int shiftbits = (idx - 1) * 8;
            final long mask = 255L << shiftbits;
            stream.writeByte((byte)((i & mask) >> shiftbits));
        }
    }
    
    public static long readVLong(final DataInput stream) throws IOException {
        final byte firstByte = stream.readByte();
        final int len = decodeVIntSize(firstByte);
        if (len == 1) {
            return firstByte;
        }
        long i = 0L;
        for (int idx = 0; idx < len - 1; ++idx) {
            final byte b = stream.readByte();
            i <<= 8;
            i |= (b & 0xFF);
        }
        return isNegativeVInt(firstByte) ? (~i) : i;
    }
    
    public static int readVInt(final DataInput stream) throws IOException {
        final long n = readVLong(stream);
        if (n > 2147483647L || n < -2147483648L) {
            throw new IOException("value too long to fit in integer");
        }
        return (int)n;
    }
    
    public static int readVIntInRange(final DataInput stream, final int lower, final int upper) throws IOException {
        final long n = readVLong(stream);
        if (n < lower) {
            if (lower == 0) {
                throw new IOException("expected non-negative integer, got " + n);
            }
            throw new IOException("expected integer greater than or equal to " + lower + ", got " + n);
        }
        else {
            if (n > upper) {
                throw new IOException("expected integer less or equal to " + upper + ", got " + n);
            }
            return (int)n;
        }
    }
    
    public static boolean isNegativeVInt(final byte value) {
        return value < -120 || (value >= -112 && value < 0);
    }
    
    public static int decodeVIntSize(final byte value) {
        if (value >= -112) {
            return 1;
        }
        if (value < -120) {
            return -119 - value;
        }
        return -111 - value;
    }
    
    public static int getVIntSize(long i) {
        if (i >= -112L && i <= 127L) {
            return 1;
        }
        if (i < 0L) {
            i ^= -1L;
        }
        final int dataBits = 64 - Long.numberOfLeadingZeros(i);
        return (dataBits + 7) / 8 + 1;
    }
    
    public static <T extends Enum<T>> T readEnum(final DataInput in, final Class<T> enumType) throws IOException {
        return Enum.valueOf(enumType, Text.readString(in));
    }
    
    public static void writeEnum(final DataOutput out, final Enum<?> enumVal) throws IOException {
        Text.writeString(out, enumVal.name());
    }
    
    public static void skipFully(final DataInput in, final int len) throws IOException {
        int total = 0;
        for (int cur = 0; total < len && (cur = in.skipBytes(len - total)) > 0; total += cur) {}
        if (total < len) {
            throw new IOException("Not able to skip " + len + " bytes, possibly due to end of input.");
        }
    }
    
    public static byte[] toByteArray(final Writable... writables) {
        final DataOutputBuffer out = new DataOutputBuffer();
        try {
            for (final Writable w : writables) {
                w.write(out);
            }
            out.close();
        }
        catch (IOException e) {
            throw new RuntimeException("Fail to convert writables to a byte array", e);
        }
        return out.getData();
    }
    
    public static String readStringSafely(final DataInput in, final int maxLength) throws IOException, IllegalArgumentException {
        final int length = readVInt(in);
        if (length < 0 || length > maxLength) {
            throw new IllegalArgumentException("Encoded byte size for String was " + length + ", which is outside of 0.." + maxLength + " range.");
        }
        final byte[] bytes = new byte[length];
        in.readFully(bytes, 0, length);
        return Text.decode(bytes);
    }
}
