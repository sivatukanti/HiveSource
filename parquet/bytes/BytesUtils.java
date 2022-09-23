// 
// Decompiled by Procyon v0.5.36
// 

package parquet.bytes;

import java.io.OutputStream;
import java.io.EOFException;
import java.io.InputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import parquet.Log;

public class BytesUtils
{
    private static final Log LOG;
    public static final Charset UTF8;
    
    public static int getWidthFromMaxInt(final int bound) {
        return 32 - Integer.numberOfLeadingZeros(bound);
    }
    
    public static int readIntLittleEndian(final byte[] in, final int offset) throws IOException {
        final int ch4 = in[offset] & 0xFF;
        final int ch5 = in[offset + 1] & 0xFF;
        final int ch6 = in[offset + 2] & 0xFF;
        final int ch7 = in[offset + 3] & 0xFF;
        return (ch7 << 24) + (ch6 << 16) + (ch5 << 8) + (ch4 << 0);
    }
    
    public static int readIntLittleEndian(final InputStream in) throws IOException {
        final int ch1 = in.read();
        final int ch2 = in.read();
        final int ch3 = in.read();
        final int ch4 = in.read();
        if ((ch1 | ch2 | ch3 | ch4) < 0) {
            throw new EOFException();
        }
        return (ch4 << 24) + (ch3 << 16) + (ch2 << 8) + (ch1 << 0);
    }
    
    public static int readIntLittleEndianOnOneByte(final InputStream in) throws IOException {
        final int ch1 = in.read();
        if (ch1 < 0) {
            throw new EOFException();
        }
        return ch1;
    }
    
    public static int readIntLittleEndianOnTwoBytes(final InputStream in) throws IOException {
        final int ch1 = in.read();
        final int ch2 = in.read();
        if ((ch1 | ch2) < 0) {
            throw new EOFException();
        }
        return (ch2 << 8) + (ch1 << 0);
    }
    
    public static int readIntLittleEndianOnThreeBytes(final InputStream in) throws IOException {
        final int ch1 = in.read();
        final int ch2 = in.read();
        final int ch3 = in.read();
        if ((ch1 | ch2 | ch3) < 0) {
            throw new EOFException();
        }
        return (ch3 << 16) + (ch2 << 8) + (ch1 << 0);
    }
    
    public static int readIntLittleEndianPaddedOnBitWidth(final InputStream in, final int bitWidth) throws IOException {
        final int bytesWidth = paddedByteCountFromBits(bitWidth);
        switch (bytesWidth) {
            case 0: {
                return 0;
            }
            case 1: {
                return readIntLittleEndianOnOneByte(in);
            }
            case 2: {
                return readIntLittleEndianOnTwoBytes(in);
            }
            case 3: {
                return readIntLittleEndianOnThreeBytes(in);
            }
            case 4: {
                return readIntLittleEndian(in);
            }
            default: {
                throw new IOException(String.format("Encountered bitWidth (%d) that requires more than 4 bytes", bitWidth));
            }
        }
    }
    
    public static void writeIntLittleEndianOnOneByte(final OutputStream out, final int v) throws IOException {
        out.write(v >>> 0 & 0xFF);
    }
    
    public static void writeIntLittleEndianOnTwoBytes(final OutputStream out, final int v) throws IOException {
        out.write(v >>> 0 & 0xFF);
        out.write(v >>> 8 & 0xFF);
    }
    
    public static void writeIntLittleEndianOnThreeBytes(final OutputStream out, final int v) throws IOException {
        out.write(v >>> 0 & 0xFF);
        out.write(v >>> 8 & 0xFF);
        out.write(v >>> 16 & 0xFF);
    }
    
    public static void writeIntLittleEndian(final OutputStream out, final int v) throws IOException {
        out.write(v >>> 0 & 0xFF);
        out.write(v >>> 8 & 0xFF);
        out.write(v >>> 16 & 0xFF);
        out.write(v >>> 24 & 0xFF);
        if (Log.DEBUG) {
            BytesUtils.LOG.debug("write le int: " + v + " => " + (v >>> 0 & 0xFF) + " " + (v >>> 8 & 0xFF) + " " + (v >>> 16 & 0xFF) + " " + (v >>> 24 & 0xFF));
        }
    }
    
    public static void writeIntLittleEndianPaddedOnBitWidth(final OutputStream out, final int v, final int bitWidth) throws IOException {
        final int bytesWidth = paddedByteCountFromBits(bitWidth);
        switch (bytesWidth) {
            case 0: {
                break;
            }
            case 1: {
                writeIntLittleEndianOnOneByte(out, v);
                break;
            }
            case 2: {
                writeIntLittleEndianOnTwoBytes(out, v);
                break;
            }
            case 3: {
                writeIntLittleEndianOnThreeBytes(out, v);
                break;
            }
            case 4: {
                writeIntLittleEndian(out, v);
                break;
            }
            default: {
                throw new IOException(String.format("Encountered value (%d) that requires more than 4 bytes", v));
            }
        }
    }
    
    public static int readUnsignedVarInt(final InputStream in) throws IOException {
        int value = 0;
        int i = 0;
        int b;
        while (((b = in.read()) & 0x80) != 0x0) {
            value |= (b & 0x7F) << i;
            i += 7;
        }
        return value | b << i;
    }
    
    public static int readZigZagVarInt(final InputStream in) throws IOException {
        final int raw = readUnsignedVarInt(in);
        final int temp = (raw << 31 >> 31 ^ raw) >> 1;
        return temp ^ (raw & Integer.MIN_VALUE);
    }
    
    public static void writeUnsignedVarInt(int value, final OutputStream out) throws IOException {
        while ((value & 0xFFFFFF80) != 0x0L) {
            out.write((value & 0x7F) | 0x80);
            value >>>= 7;
        }
        out.write(value & 0x7F);
    }
    
    public static void writeZigZagVarInt(final int intValue, final OutputStream out) throws IOException {
        writeUnsignedVarInt(intValue << 1 ^ intValue >> 31, out);
    }
    
    public static int paddedByteCountFromBits(final int bitLength) {
        return (bitLength + 7) / 8;
    }
    
    public static byte[] intToBytes(final int value) {
        final byte[] outBuffer = { (byte)(value >>> 0), (byte)(value >>> 8), (byte)(value >>> 16), (byte)(value >>> 24) };
        return outBuffer;
    }
    
    public static int bytesToInt(final byte[] bytes) {
        return ((bytes[3] & 0xFF) << 24) + ((bytes[2] & 0xFF) << 16) + ((bytes[1] & 0xFF) << 8) + ((bytes[0] & 0xFF) << 0);
    }
    
    public static byte[] longToBytes(final long value) {
        final byte[] outBuffer = { (byte)(value >>> 0), (byte)(value >>> 8), (byte)(value >>> 16), (byte)(value >>> 24), (byte)(value >>> 32), (byte)(value >>> 40), (byte)(value >>> 48), (byte)(value >>> 56) };
        return outBuffer;
    }
    
    public static long bytesToLong(final byte[] bytes) {
        return ((long)bytes[7] << 56) + ((long)(bytes[6] & 0xFF) << 48) + ((long)(bytes[5] & 0xFF) << 40) + ((long)(bytes[4] & 0xFF) << 32) + ((long)(bytes[3] & 0xFF) << 24) + ((long)(bytes[2] & 0xFF) << 16) + ((long)(bytes[1] & 0xFF) << 8) + ((long)(bytes[0] & 0xFF) << 0);
    }
    
    public static byte[] booleanToBytes(final boolean value) {
        final byte[] outBuffer = { (byte)(value ? 1 : 0) };
        return outBuffer;
    }
    
    public static boolean bytesToBool(final byte[] bytes) {
        return (bytes[0] & 0xFF) != 0x0;
    }
    
    static {
        LOG = Log.getLog(BytesUtils.class);
        UTF8 = Charset.forName("UTF-8");
    }
}
