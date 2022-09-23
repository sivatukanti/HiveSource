// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io;

import org.slf4j.LoggerFactory;
import java.io.UTFDataFormatException;
import org.apache.hadoop.util.StringUtils;
import java.io.DataInput;
import java.io.IOException;
import java.io.DataOutput;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@Deprecated
@InterfaceAudience.LimitedPrivate({ "HDFS", "MapReduce" })
@InterfaceStability.Stable
public class UTF8 implements WritableComparable<UTF8>
{
    private static final Logger LOG;
    private static final DataInputBuffer IBUF;
    private static final ThreadLocal<DataOutputBuffer> OBUF_FACTORY;
    private static final byte[] EMPTY_BYTES;
    private byte[] bytes;
    private int length;
    
    public UTF8() {
        this.bytes = UTF8.EMPTY_BYTES;
    }
    
    public UTF8(final String string) {
        this.bytes = UTF8.EMPTY_BYTES;
        this.set(string);
    }
    
    public UTF8(final UTF8 utf8) {
        this.bytes = UTF8.EMPTY_BYTES;
        this.set(utf8);
    }
    
    public byte[] getBytes() {
        return this.bytes;
    }
    
    public int getLength() {
        return this.length;
    }
    
    public void set(String string) {
        if (string.length() > 21845) {
            UTF8.LOG.warn("truncating long string: " + string.length() + " chars, starting with " + string.substring(0, 20));
            string = string.substring(0, 21845);
        }
        this.length = utf8Length(string);
        if (this.length > 65535) {
            throw new RuntimeException("string too long!");
        }
        if (this.bytes == null || this.length > this.bytes.length) {
            this.bytes = new byte[this.length];
        }
        try {
            final DataOutputBuffer obuf = UTF8.OBUF_FACTORY.get();
            obuf.reset();
            writeChars(obuf, string, 0, string.length());
            System.arraycopy(obuf.getData(), 0, this.bytes, 0, this.length);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public void set(final UTF8 other) {
        this.length = other.length;
        if (this.bytes == null || this.length > this.bytes.length) {
            this.bytes = new byte[this.length];
        }
        System.arraycopy(other.bytes, 0, this.bytes, 0, this.length);
    }
    
    @Override
    public void readFields(final DataInput in) throws IOException {
        this.length = in.readUnsignedShort();
        if (this.bytes == null || this.bytes.length < this.length) {
            this.bytes = new byte[this.length];
        }
        in.readFully(this.bytes, 0, this.length);
    }
    
    public static void skip(final DataInput in) throws IOException {
        final int length = in.readUnsignedShort();
        WritableUtils.skipFully(in, length);
    }
    
    @Override
    public void write(final DataOutput out) throws IOException {
        out.writeShort(this.length);
        out.write(this.bytes, 0, this.length);
    }
    
    @Override
    public int compareTo(final UTF8 o) {
        return WritableComparator.compareBytes(this.bytes, 0, this.length, o.bytes, 0, o.length);
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder(this.length);
        try {
            synchronized (UTF8.IBUF) {
                UTF8.IBUF.reset(this.bytes, this.length);
                readChars(UTF8.IBUF, buffer, this.length);
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        return buffer.toString();
    }
    
    public String toStringChecked() throws IOException {
        final StringBuilder buffer = new StringBuilder(this.length);
        synchronized (UTF8.IBUF) {
            UTF8.IBUF.reset(this.bytes, this.length);
            readChars(UTF8.IBUF, buffer, this.length);
        }
        return buffer.toString();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof UTF8)) {
            return false;
        }
        final UTF8 that = (UTF8)o;
        return this.length == that.length && WritableComparator.compareBytes(this.bytes, 0, this.length, that.bytes, 0, that.length) == 0;
    }
    
    @Override
    public int hashCode() {
        return WritableComparator.hashBytes(this.bytes, this.length);
    }
    
    public static byte[] getBytes(final String string) {
        final byte[] result = new byte[utf8Length(string)];
        try {
            final DataOutputBuffer obuf = UTF8.OBUF_FACTORY.get();
            obuf.reset();
            writeChars(obuf, string, 0, string.length());
            System.arraycopy(obuf.getData(), 0, result, 0, obuf.getLength());
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        return result;
    }
    
    public static String fromBytes(final byte[] bytes) throws IOException {
        final DataInputBuffer dbuf = new DataInputBuffer();
        dbuf.reset(bytes, 0, bytes.length);
        final StringBuilder buf = new StringBuilder(bytes.length);
        readChars(dbuf, buf, bytes.length);
        return buf.toString();
    }
    
    public static String readString(final DataInput in) throws IOException {
        final int bytes = in.readUnsignedShort();
        final StringBuilder buffer = new StringBuilder(bytes);
        readChars(in, buffer, bytes);
        return buffer.toString();
    }
    
    private static void readChars(final DataInput in, final StringBuilder buffer, final int nBytes) throws UTFDataFormatException, IOException {
        final DataOutputBuffer obuf = UTF8.OBUF_FACTORY.get();
        obuf.reset();
        obuf.write(in, nBytes);
        final byte[] bytes = obuf.getData();
        int i = 0;
        while (i < nBytes) {
            final byte b = bytes[i++];
            if ((b & 0x80) == 0x0) {
                buffer.append((char)(b & 0x7F));
            }
            else if ((b & 0xE0) == 0xC0) {
                if (i >= nBytes) {
                    throw new UTFDataFormatException("Truncated UTF8 at " + StringUtils.byteToHexString(bytes, i - 1, 1));
                }
                buffer.append((char)((b & 0x1F) << 6 | (bytes[i++] & 0x3F)));
            }
            else if ((b & 0xF0) == 0xE0) {
                if (i + 1 >= nBytes) {
                    throw new UTFDataFormatException("Truncated UTF8 at " + StringUtils.byteToHexString(bytes, i - 1, 2));
                }
                buffer.append((char)((b & 0xF) << 12 | (bytes[i++] & 0x3F) << 6 | (bytes[i++] & 0x3F)));
            }
            else {
                if ((b & 0xF8) != 0xF0) {
                    final int endForError = Math.min(i + 5, nBytes);
                    throw new UTFDataFormatException("Invalid UTF8 at " + StringUtils.byteToHexString(bytes, i - 1, endForError));
                }
                if (i + 2 >= nBytes) {
                    throw new UTFDataFormatException("Truncated UTF8 at " + StringUtils.byteToHexString(bytes, i - 1, 3));
                }
                final int codepoint = (b & 0x7) << 18 | (bytes[i++] & 0x3F) << 12 | (bytes[i++] & 0x3F) << 6 | (bytes[i++] & 0x3F);
                buffer.append(highSurrogate(codepoint)).append(lowSurrogate(codepoint));
            }
        }
    }
    
    private static char highSurrogate(final int codePoint) {
        return (char)((codePoint >>> 10) + 55232);
    }
    
    private static char lowSurrogate(final int codePoint) {
        return (char)((codePoint & 0x3FF) + 56320);
    }
    
    public static int writeString(final DataOutput out, String s) throws IOException {
        if (s.length() > 21845) {
            UTF8.LOG.warn("truncating long string: " + s.length() + " chars, starting with " + s.substring(0, 20));
            s = s.substring(0, 21845);
        }
        final int len = utf8Length(s);
        if (len > 65535) {
            throw new IOException("string too long!");
        }
        out.writeShort(len);
        writeChars(out, s, 0, s.length());
        return len;
    }
    
    private static int utf8Length(final String string) {
        final int stringLength = string.length();
        int utf8Length = 0;
        for (int i = 0; i < stringLength; ++i) {
            final int c = string.charAt(i);
            if (c <= 127) {
                ++utf8Length;
            }
            else if (c > 2047) {
                utf8Length += 3;
            }
            else {
                utf8Length += 2;
            }
        }
        return utf8Length;
    }
    
    private static void writeChars(final DataOutput out, final String s, final int start, final int length) throws IOException {
        for (int end = start + length, i = start; i < end; ++i) {
            final int code = s.charAt(i);
            if (code <= 127) {
                out.writeByte((byte)code);
            }
            else if (code <= 2047) {
                out.writeByte((byte)(0xC0 | (code >> 6 & 0x1F)));
                out.writeByte((byte)(0x80 | (code & 0x3F)));
            }
            else {
                out.writeByte((byte)(0xE0 | (code >> 12 & 0xF)));
                out.writeByte((byte)(0x80 | (code >> 6 & 0x3F)));
                out.writeByte((byte)(0x80 | (code & 0x3F)));
            }
        }
    }
    
    static {
        LOG = LoggerFactory.getLogger(UTF8.class);
        IBUF = new DataInputBuffer();
        OBUF_FACTORY = new ThreadLocal<DataOutputBuffer>() {
            @Override
            protected DataOutputBuffer initialValue() {
                return new DataOutputBuffer();
            }
        };
        EMPTY_BYTES = new byte[0];
        WritableComparator.define(UTF8.class, new Comparator());
    }
    
    public static class Comparator extends WritableComparator
    {
        public Comparator() {
            super(UTF8.class);
        }
        
        @Override
        public int compare(final byte[] b1, final int s1, final int l1, final byte[] b2, final int s2, final int l2) {
            final int n1 = WritableComparator.readUnsignedShort(b1, s1);
            final int n2 = WritableComparator.readUnsignedShort(b2, s2);
            return WritableComparator.compareBytes(b1, s1 + 2, n1, b2, s2 + 2, n2);
        }
    }
}
