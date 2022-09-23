// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.services.io;

import java.io.InputStream;
import java.io.DataInput;
import java.io.OutputStream;
import java.io.IOException;
import java.io.DataOutput;

public abstract class CompressedNumber
{
    public static final int MAX_INT_STORED_SIZE = 4;
    public static final int MAX_LONG_STORED_SIZE = 8;
    public static final int MAX_COMPRESSED_INT_ONE_BYTE = 63;
    public static final int MAX_COMPRESSED_INT_TWO_BYTES = 16383;
    
    public static final int writeInt(final DataOutput dataOutput, final int n) throws IOException {
        if (n < 0) {
            throw new IOException();
        }
        if (n <= 63) {
            dataOutput.writeByte(n);
            return 1;
        }
        if (n <= 16383) {
            dataOutput.writeByte(0x40 | n >>> 8);
            dataOutput.writeByte(n & 0xFF);
            return 2;
        }
        dataOutput.writeByte((n >>> 24 | 0x80) & 0xFF);
        dataOutput.writeByte(n >>> 16 & 0xFF);
        dataOutput.writeByte(n >>> 8 & 0xFF);
        dataOutput.writeByte(n & 0xFF);
        return 4;
    }
    
    public static final int writeInt(final OutputStream outputStream, final int n) throws IOException {
        if (n < 0) {
            throw new IOException();
        }
        if (n <= 63) {
            outputStream.write(n);
            return 1;
        }
        if (n <= 16383) {
            outputStream.write(0x40 | n >>> 8);
            outputStream.write(n & 0xFF);
            return 2;
        }
        outputStream.write((n >>> 24 | 0x80) & 0xFF);
        outputStream.write(n >>> 16 & 0xFF);
        outputStream.write(n >>> 8 & 0xFF);
        outputStream.write(n & 0xFF);
        return 4;
    }
    
    public static final int readInt(final DataInput dataInput) throws IOException {
        final int unsignedByte = dataInput.readUnsignedByte();
        if ((unsignedByte & 0xFFFFFFC0) == 0x0) {
            return unsignedByte;
        }
        if ((unsignedByte & 0x80) == 0x0) {
            return (unsignedByte & 0x3F) << 8 | dataInput.readUnsignedByte();
        }
        return (unsignedByte & 0x7F) << 24 | dataInput.readUnsignedByte() << 16 | dataInput.readUnsignedByte() << 8 | dataInput.readUnsignedByte();
    }
    
    public static final int readInt(final InputStream inputStream) throws IOException {
        final int unsignedByte = InputStreamUtil.readUnsignedByte(inputStream);
        if ((unsignedByte & 0xFFFFFFC0) == 0x0) {
            return unsignedByte;
        }
        if ((unsignedByte & 0x80) == 0x0) {
            return (unsignedByte & 0x3F) << 8 | InputStreamUtil.readUnsignedByte(inputStream);
        }
        return (unsignedByte & 0x7F) << 24 | InputStreamUtil.readUnsignedByte(inputStream) << 16 | InputStreamUtil.readUnsignedByte(inputStream) << 8 | InputStreamUtil.readUnsignedByte(inputStream);
    }
    
    public static final int readInt(final byte[] array, int n) {
        final byte b = array[n++];
        if ((b & 0xFFFFFFC0) == 0x0) {
            return b;
        }
        if ((b & 0x80) == 0x0) {
            return (b & 0x3F) << 8 | (array[n] & 0xFF);
        }
        return (b & 0x7F) << 24 | (array[n++] & 0xFF) << 16 | (array[n++] & 0xFF) << 8 | (array[n] & 0xFF);
    }
    
    public static final int sizeInt(final int n) {
        if (n <= 63) {
            return 1;
        }
        if (n <= 16383) {
            return 2;
        }
        return 4;
    }
    
    public static final int writeLong(final DataOutput dataOutput, final long n) throws IOException {
        if (n < 0L) {
            throw new IOException();
        }
        if (n <= 16383L) {
            dataOutput.writeByte((int)(n >>> 8 & 0xFFL));
            dataOutput.writeByte((int)(n & 0xFFL));
            return 2;
        }
        if (n <= 1073741823L) {
            dataOutput.writeByte((int)((n >>> 24 | 0x40L) & 0xFFL));
            dataOutput.writeByte((int)(n >>> 16 & 0xFFL));
            dataOutput.writeByte((int)(n >>> 8 & 0xFFL));
            dataOutput.writeByte((int)(n & 0xFFL));
            return 4;
        }
        dataOutput.writeByte((int)((n >>> 56 | 0x80L) & 0xFFL));
        dataOutput.writeByte((int)(n >>> 48 & 0xFFL));
        dataOutput.writeByte((int)(n >>> 40 & 0xFFL));
        dataOutput.writeByte((int)(n >>> 32 & 0xFFL));
        dataOutput.writeByte((int)(n >>> 24 & 0xFFL));
        dataOutput.writeByte((int)(n >>> 16 & 0xFFL));
        dataOutput.writeByte((int)(n >>> 8 & 0xFFL));
        dataOutput.writeByte((int)(n & 0xFFL));
        return 8;
    }
    
    public static final int writeLong(final OutputStream outputStream, final long n) throws IOException {
        if (n < 0L) {
            throw new IOException();
        }
        if (n <= 16383L) {
            outputStream.write((int)(n >>> 8 & 0xFFL));
            outputStream.write((int)(n & 0xFFL));
            return 2;
        }
        if (n <= 1073741823L) {
            outputStream.write((int)((n >>> 24 | 0x40L) & 0xFFL));
            outputStream.write((int)(n >>> 16 & 0xFFL));
            outputStream.write((int)(n >>> 8 & 0xFFL));
            outputStream.write((int)(n & 0xFFL));
            return 4;
        }
        outputStream.write((int)((n >>> 56 | 0x80L) & 0xFFL));
        outputStream.write((int)(n >>> 48 & 0xFFL));
        outputStream.write((int)(n >>> 40 & 0xFFL));
        outputStream.write((int)(n >>> 32 & 0xFFL));
        outputStream.write((int)(n >>> 24 & 0xFFL));
        outputStream.write((int)(n >>> 16 & 0xFFL));
        outputStream.write((int)(n >>> 8 & 0xFFL));
        outputStream.write((int)(n & 0xFFL));
        return 8;
    }
    
    public static final long readLong(final DataInput dataInput) throws IOException {
        final int unsignedByte = dataInput.readUnsignedByte();
        if ((unsignedByte & 0xFFFFFFC0) == 0x0) {
            return unsignedByte << 8 | dataInput.readUnsignedByte();
        }
        if ((unsignedByte & 0x80) == 0x0) {
            return (unsignedByte & 0x3F) << 24 | dataInput.readUnsignedByte() << 16 | dataInput.readUnsignedByte() << 8 | dataInput.readUnsignedByte();
        }
        return (long)(unsignedByte & 0x7F) << 56 | (long)dataInput.readUnsignedByte() << 48 | (long)dataInput.readUnsignedByte() << 40 | (long)dataInput.readUnsignedByte() << 32 | (long)dataInput.readUnsignedByte() << 24 | (long)dataInput.readUnsignedByte() << 16 | (long)dataInput.readUnsignedByte() << 8 | (long)dataInput.readUnsignedByte();
    }
    
    public static final long readLong(final InputStream inputStream) throws IOException {
        final int unsignedByte = InputStreamUtil.readUnsignedByte(inputStream);
        if ((unsignedByte & 0xFFFFFFC0) == 0x0) {
            return unsignedByte << 8 | InputStreamUtil.readUnsignedByte(inputStream);
        }
        if ((unsignedByte & 0x80) == 0x0) {
            return (unsignedByte & 0x3F) << 24 | InputStreamUtil.readUnsignedByte(inputStream) << 16 | InputStreamUtil.readUnsignedByte(inputStream) << 8 | InputStreamUtil.readUnsignedByte(inputStream);
        }
        return ((long)unsignedByte & 0x7FL) << 56 | (long)InputStreamUtil.readUnsignedByte(inputStream) << 48 | (long)InputStreamUtil.readUnsignedByte(inputStream) << 40 | (long)InputStreamUtil.readUnsignedByte(inputStream) << 32 | (long)InputStreamUtil.readUnsignedByte(inputStream) << 24 | (long)InputStreamUtil.readUnsignedByte(inputStream) << 16 | (long)InputStreamUtil.readUnsignedByte(inputStream) << 8 | (long)InputStreamUtil.readUnsignedByte(inputStream);
    }
    
    public static final long readLong(final byte[] array, int n) {
        final byte b = array[n++];
        if ((b & 0xFFFFFFC0) == 0x0) {
            return b << 8 | (array[n] & 0xFF);
        }
        if ((b & 0x80) == 0x0) {
            return (b & 0x3F) << 24 | (array[n++] & 0xFF) << 16 | (array[n++] & 0xFF) << 8 | (array[n] & 0xFF);
        }
        return (long)(b & 0x7F) << 56 | (long)(array[n++] & 0xFF) << 48 | (long)(array[n++] & 0xFF) << 40 | (long)(array[n++] & 0xFF) << 32 | (long)(array[n++] & 0xFF) << 24 | (long)(array[n++] & 0xFF) << 16 | (long)(array[n++] & 0xFF) << 8 | (long)(array[n] & 0xFF);
    }
    
    public static final int sizeLong(final long n) {
        if (n <= 16383L) {
            return 2;
        }
        if (n <= 1073741823L) {
            return 4;
        }
        return 8;
    }
}
