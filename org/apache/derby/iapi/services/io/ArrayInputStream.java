// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.services.io;

import java.io.UTFDataFormatException;
import java.io.EOFException;
import java.io.IOException;
import org.apache.derby.iapi.util.ReuseFactory;
import java.io.InputStream;

public final class ArrayInputStream extends InputStream implements LimitObjectInput
{
    private byte[] pageData;
    private int start;
    private int end;
    private int position;
    private ErrorObjectInput oi;
    
    public ArrayInputStream() {
        this(ReuseFactory.getZeroLenByteArray());
    }
    
    public ArrayInputStream(final byte[] data) {
        this.setData(data);
        this.oi = new FormatIdInputStream(this);
    }
    
    public void setData(final byte[] pageData) {
        this.pageData = pageData;
        final int n = 0;
        this.position = n;
        this.start = n;
        this.end = pageData.length;
    }
    
    public byte[] getData() {
        return this.pageData;
    }
    
    public int read() throws IOException {
        if (this.position == this.end) {
            return -1;
        }
        return this.pageData[this.position++] & 0xFF;
    }
    
    public int read(final byte[] array, final int n, int n2) throws IOException {
        final int available = this.available();
        if (n2 > available) {
            if (available == 0) {
                return -1;
            }
            n2 = available;
        }
        System.arraycopy(this.pageData, this.position, array, n, n2);
        this.position += n2;
        return n2;
    }
    
    public long skip(final long a) throws IOException {
        if (a <= 0L) {
            return 0L;
        }
        final long min = Math.min(a, this.available());
        this.position += (int)min;
        return min;
    }
    
    public int getPosition() {
        return this.position;
    }
    
    public final void setPosition(final int position) throws IOException {
        if (position >= this.start && position < this.end) {
            this.position = position;
            return;
        }
        throw new EOFException();
    }
    
    public int available() throws IOException {
        return this.end - this.position;
    }
    
    public void setLimit(final int start, final int n) throws IOException {
        this.start = start;
        this.end = start + n;
        this.position = this.start;
        if (start < 0 || n < 0 || this.end > this.pageData.length) {
            final int start2 = 0;
            this.position = start2;
            this.end = start2;
            this.start = start2;
            throw new EOFException();
        }
    }
    
    public final void setLimit(final int n) throws IOException {
        this.start = this.position;
        this.end = this.position + n;
        if (this.end > this.pageData.length) {
            final int start = 0;
            this.position = start;
            this.end = start;
            this.start = start;
            throw new EOFException();
        }
    }
    
    public final int clearLimit() {
        this.start = 0;
        final int n = this.end - this.position;
        this.end = this.pageData.length;
        return n;
    }
    
    public final void readFully(final byte[] array) throws IOException {
        this.readFully(array, 0, array.length);
    }
    
    public final void readFully(final byte[] array, final int n, final int n2) throws IOException {
        if (n2 > this.available()) {
            throw new EOFException();
        }
        System.arraycopy(this.pageData, this.position, array, n, n2);
        this.position += n2;
    }
    
    public final int skipBytes(final int n) throws IOException {
        return (int)this.skip(n);
    }
    
    public final boolean readBoolean() throws IOException {
        if (this.position == this.end) {
            throw new EOFException();
        }
        return this.pageData[this.position++] != 0;
    }
    
    public final byte readByte() throws IOException {
        if (this.position == this.end) {
            throw new EOFException();
        }
        return this.pageData[this.position++];
    }
    
    public final int readUnsignedByte() throws IOException {
        if (this.position == this.end) {
            throw new EOFException();
        }
        return this.pageData[this.position++] & 0xFF;
    }
    
    public final short readShort() throws IOException {
        int position = this.position;
        final byte[] pageData = this.pageData;
        if (position >= this.end - 1) {
            throw new EOFException();
        }
        final int n = (pageData[position++] & 0xFF) << 8 | (pageData[position++] & 0xFF);
        this.position = position;
        return (short)n;
    }
    
    public final int readUnsignedShort() throws IOException {
        int position = this.position;
        final byte[] pageData = this.pageData;
        if (position >= this.end - 1) {
            throw new EOFException();
        }
        final int n = (pageData[position++] & 0xFF) << 8 | (pageData[position++] & 0xFF);
        this.position = position;
        return n;
    }
    
    public final char readChar() throws IOException {
        int position = this.position;
        final byte[] pageData = this.pageData;
        if (position >= this.end - 1) {
            throw new EOFException();
        }
        final int n = (pageData[position++] & 0xFF) << 8 | (pageData[position++] & 0xFF);
        this.position = position;
        return (char)n;
    }
    
    public final int readInt() throws IOException {
        int position = this.position;
        final byte[] pageData = this.pageData;
        if (position >= this.end - 3) {
            throw new EOFException();
        }
        final int n = (pageData[position++] & 0xFF) << 24 | (pageData[position++] & 0xFF) << 16 | (pageData[position++] & 0xFF) << 8 | (pageData[position++] & 0xFF);
        this.position = position;
        return n;
    }
    
    public final long readLong() throws IOException {
        int position = this.position;
        final byte[] pageData = this.pageData;
        if (position >= this.end - 7) {
            throw new EOFException();
        }
        final long n = (long)(pageData[position++] & 0xFF) << 56 | (long)(pageData[position++] & 0xFF) << 48 | (long)(pageData[position++] & 0xFF) << 40 | (long)(pageData[position++] & 0xFF) << 32 | (long)(pageData[position++] & 0xFF) << 24 | (long)(pageData[position++] & 0xFF) << 16 | (long)(pageData[position++] & 0xFF) << 8 | (long)(pageData[position++] & 0xFF);
        this.position = position;
        return n;
    }
    
    public final float readFloat() throws IOException {
        return Float.intBitsToFloat(this.readInt());
    }
    
    public final double readDouble() throws IOException {
        return Double.longBitsToDouble(this.readLong());
    }
    
    public final String readLine() throws IOException {
        return this.oi.readLine();
    }
    
    public final String readUTF() throws IOException {
        return this.oi.readUTF();
    }
    
    public final int readDerbyUTF(final char[][] array, final int n) throws IOException {
        final byte[] pageData = this.pageData;
        final int end = this.end;
        int i = this.position;
        int n2;
        if (n != 0) {
            if (n > end - i) {
                throw new EOFException();
            }
            n2 = n;
        }
        else {
            n2 = end - i;
        }
        char[] array2 = array[0];
        if (array2 == null || n2 > array2.length) {
            array2 = new char[n2];
            array[0] = array2;
        }
        final int n3 = i + n2;
        int n4 = 0;
        while (i < n3) {
            final int n5 = pageData[i++] & 0xFF;
            if ((n5 & 0x80) == 0x0) {
                array2[n4++] = (char)n5;
            }
            else if ((n5 & 0x60) == 0x40) {
                if (i >= n3) {
                    throw new UTFDataFormatException();
                }
                final int n6 = pageData[i++] & 0xFF;
                if ((n6 & 0xC0) != 0x80) {
                    throw new UTFDataFormatException();
                }
                array2[n4++] = (char)((n5 & 0x1F) << 6 | (n6 & 0x3F));
            }
            else {
                if ((n5 & 0x70) != 0x60) {
                    throw new UTFDataFormatException();
                }
                if (i + 1 >= n3) {
                    throw new UTFDataFormatException();
                }
                final int n7 = pageData[i++] & 0xFF;
                final int n8 = pageData[i++] & 0xFF;
                if (n5 == 224 && n7 == 0 && n8 == 0 && n == 0) {
                    break;
                }
                if ((n7 & 0xC0) != 0x80 || (n8 & 0xC0) != 0x80) {
                    throw new UTFDataFormatException();
                }
                array2[n4++] = (char)((n5 & 0xF) << 12 | (n7 & 0x3F) << 6 | (n8 & 0x3F) << 0);
            }
        }
        this.position = i;
        return n4;
    }
    
    public final int readCompressedInt() throws IOException {
        int position = this.position;
        final byte[] pageData = this.pageData;
        try {
            int n = pageData[position++];
            if ((n & 0xFFFFFFC0) != 0x0) {
                if ((n & 0x80) == 0x0) {
                    n = ((n & 0x3F) << 8 | (pageData[position++] & 0xFF));
                }
                else {
                    n = ((n & 0x7F) << 24 | (pageData[position++] & 0xFF) << 16 | (pageData[position++] & 0xFF) << 8 | (pageData[position++] & 0xFF));
                }
            }
            this.position = position;
            return n;
        }
        catch (ArrayIndexOutOfBoundsException ex) {
            throw new EOFException();
        }
    }
    
    public final long readCompressedLong() throws IOException {
        try {
            int position = this.position;
            final byte[] pageData = this.pageData;
            final byte b = pageData[position++];
            long n;
            if ((b & 0xFFFFFFC0) == 0x0) {
                n = (b << 8 | (pageData[position++] & 0xFF));
            }
            else if ((b & 0x80) == 0x0) {
                n = ((b & 0x3F) << 24 | (pageData[position++] & 0xFF) << 16 | (pageData[position++] & 0xFF) << 8 | (pageData[position++] & 0xFF));
            }
            else {
                n = ((long)(b & 0x7F) << 56 | (long)(pageData[position++] & 0xFF) << 48 | (long)(pageData[position++] & 0xFF) << 40 | (long)(pageData[position++] & 0xFF) << 32 | (long)(pageData[position++] & 0xFF) << 24 | (long)(pageData[position++] & 0xFF) << 16 | (long)(pageData[position++] & 0xFF) << 8 | (long)(pageData[position++] & 0xFF));
            }
            this.position = position;
            return n;
        }
        catch (ArrayIndexOutOfBoundsException ex) {
            throw new EOFException();
        }
    }
    
    public Object readObject() throws ClassNotFoundException, IOException {
        return this.oi.readObject();
    }
    
    public String getErrorInfo() {
        return this.oi.getErrorInfo();
    }
    
    public Exception getNestedException() {
        return this.oi.getNestedException();
    }
}
