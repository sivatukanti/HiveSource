// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.raw.data;

import java.io.OutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.util.Vector;

public class MemByteHolder implements ByteHolder
{
    int bufSize;
    boolean writing;
    Vector bufV;
    int curBufVEleAt;
    byte[] curBuf;
    int curBufPos;
    int curBufDataBytes;
    int lastBufVEleAt;
    int lastBufDataBytes;
    
    public MemByteHolder(final int bufSize) {
        this.writing = true;
        this.lastBufVEleAt = 0;
        this.lastBufDataBytes = 0;
        this.bufSize = bufSize;
        this.curBuf = new byte[bufSize];
        this.curBufPos = 0;
        (this.bufV = new Vector(128)).addElement(this.curBuf);
        this.curBufVEleAt = 0;
    }
    
    public void write(final int n) throws IOException {
        if (this.curBufPos >= this.curBuf.length) {
            this.getNextBuffer_w();
        }
        this.curBuf[this.curBufPos++] = (byte)n;
    }
    
    public void write(final byte[] array, int n, int i) throws IOException {
        while (i > 0) {
            if (this.curBufPos >= this.curBuf.length) {
                this.getNextBuffer_w();
            }
            int n2 = i;
            final int n3 = this.curBuf.length - this.curBufPos;
            if (n2 > n3) {
                n2 = n3;
            }
            System.arraycopy(array, n, this.curBuf, this.curBufPos, n2);
            n += n2;
            this.curBufPos += n2;
            i -= n2;
        }
    }
    
    public long write(final InputStream inputStream, final long n) throws IOException {
        long n2 = n;
        int read;
        do {
            if (this.curBufPos >= this.curBuf.length) {
                this.getNextBuffer_w();
            }
            final int n3 = this.curBuf.length - this.curBufPos;
            int len;
            if (n2 >= n3) {
                len = n3;
            }
            else {
                len = (int)n2;
            }
            read = inputStream.read(this.curBuf, this.curBufPos, len);
            if (read > 0) {
                n2 -= read;
                this.curBufPos += read;
            }
        } while (n2 > 0L && read > 0);
        return n - n2;
    }
    
    public void clear() throws IOException {
        this.writing = true;
        this.curBuf = this.bufV.elementAt(0);
        this.curBufVEleAt = 0;
        this.curBufPos = 0;
        this.lastBufVEleAt = 0;
        this.lastBufDataBytes = 0;
    }
    
    public void startReading() throws IOException {
        if (this.writing) {
            this.writing = false;
            this.lastBufDataBytes = this.curBufPos;
            this.lastBufVEleAt = this.curBufVEleAt;
        }
        this.curBuf = this.bufV.elementAt(0);
        this.curBufVEleAt = 0;
        this.curBufPos = 0;
        if (this.curBufVEleAt == this.lastBufVEleAt) {
            this.curBufDataBytes = this.lastBufDataBytes;
        }
        else {
            this.curBufDataBytes = this.bufSize;
        }
    }
    
    public int read() throws IOException {
        if (this.curBufPos >= this.curBufDataBytes) {
            this.getNextBuffer_r();
        }
        if (this.curBufPos >= this.curBufDataBytes) {
            return -1;
        }
        return 0xFF & this.curBuf[this.curBufPos++];
    }
    
    public int read(final byte[] array, final int n, final int n2) throws IOException {
        return this.read(array, n, null, n2);
    }
    
    public int read(final OutputStream outputStream, final int n) throws IOException {
        return this.read(null, 0, outputStream, n);
    }
    
    public int read(final byte[] array, int n, final OutputStream outputStream, int n2) throws IOException {
        int n3 = 0;
        boolean b = false;
        if (this.curBufPos >= this.curBufDataBytes) {
            b = this.getNextBuffer_r();
        }
        if (b) {
            return -1;
        }
        while (n2 > 0 && !b) {
            final int n4 = this.curBufDataBytes - this.curBufPos;
            int len;
            if (n2 >= n4) {
                len = n4;
            }
            else {
                len = n2;
            }
            if (outputStream == null) {
                System.arraycopy(this.curBuf, this.curBufPos, array, n, len);
            }
            else {
                outputStream.write(this.curBuf, this.curBufPos, len);
            }
            n += len;
            this.curBufPos += len;
            n2 -= len;
            n3 += len;
            if (this.curBufPos >= this.curBufDataBytes) {
                b = this.getNextBuffer_r();
            }
        }
        return n3;
    }
    
    public int shiftToFront() throws IOException {
        final int available = this.available();
        final int n = (available > 0) ? available : (-1 * available);
        final byte[] array = new byte[n + 1];
        final int read = this.read(array, 0, n);
        this.clear();
        this.writing = true;
        this.write(array, 0, read);
        this.curBufDataBytes = 0;
        return read;
    }
    
    public int available() {
        final int n = this.curBufDataBytes - this.curBufPos;
        int lastBufDataBytes = 0;
        int n2 = 0;
        if (this.curBufVEleAt != this.lastBufVEleAt) {
            n2 = this.lastBufVEleAt - this.curBufVEleAt - 1;
            lastBufDataBytes = this.lastBufDataBytes;
        }
        return n + lastBufDataBytes + n2 * this.bufSize;
    }
    
    public int numBytesSaved() {
        int n;
        if (this.writing) {
            n = this.curBufVEleAt * this.bufSize + this.curBufPos;
        }
        else {
            n = this.lastBufVEleAt * this.bufSize + this.lastBufDataBytes;
        }
        return n;
    }
    
    public long skip(long n) throws IOException {
        long n2 = 0L;
        boolean b = false;
        if (this.curBufPos >= this.curBufDataBytes) {
            b = this.getNextBuffer_r();
        }
        while (n > 0L && !b) {
            final int n3 = this.curBufDataBytes - this.curBufPos;
            int n4;
            if (n >= n3) {
                n4 = n3;
            }
            else {
                n4 = (int)n;
            }
            this.curBufPos += n4;
            n -= n4;
            n2 += n4;
            if (n > 0L) {
                b = this.getNextBuffer_r();
            }
        }
        return n2;
    }
    
    public boolean writingMode() {
        return this.writing;
    }
    
    public ByteHolder cloneEmpty() {
        return new MemByteHolder(this.bufSize);
    }
    
    protected void getNextBuffer_w() throws IOException {
        ++this.curBufVEleAt;
        if (this.bufV.size() <= this.curBufVEleAt) {
            this.curBuf = new byte[this.bufSize];
            this.bufV.addElement(this.curBuf);
        }
        else {
            this.curBuf = this.bufV.elementAt(this.curBufVEleAt);
        }
        this.initBuffer_w();
    }
    
    protected void getNextBuffer_w_Sanity() {
    }
    
    protected void initBuffer_w() {
        this.curBufPos = 0;
    }
    
    protected boolean getNextBuffer_r() throws IOException {
        if (this.curBufVEleAt >= this.lastBufVEleAt) {
            return true;
        }
        this.curBuf = this.bufV.elementAt(++this.curBufVEleAt);
        this.curBufPos = 0;
        if (this.curBufVEleAt == this.lastBufVEleAt) {
            this.curBufDataBytes = this.lastBufDataBytes;
        }
        else {
            this.curBufDataBytes = this.bufSize;
        }
        return false;
    }
    
    private String dumpBuf(final int index) {
        final StringBuffer sb = new StringBuffer(100);
        final byte[] array = this.bufV.elementAt(index);
        sb.append("(");
        for (int i = 0; i < array.length; ++i) {
            sb.append(array[i] + ".");
        }
        sb.append(")");
        return sb.toString();
    }
    
    public String toString() {
        return " writing: " + this.writing + " curBufVEleAt: " + this.curBufVEleAt + " curBufPos: " + this.curBufPos + " curBufDataBytes: " + this.curBufDataBytes + " lastBufVEleAt: " + this.lastBufVEleAt + " lastBufDataBytes: " + this.lastBufDataBytes + " curBuf: " + this.dumpBuf(this.curBufVEleAt);
    }
}
