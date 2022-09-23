// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io.compress.bzip2;

import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.Buffer;
import org.slf4j.Logger;
import org.apache.hadoop.io.compress.Decompressor;

public class Bzip2Decompressor implements Decompressor
{
    private static final int DEFAULT_DIRECT_BUFFER_SIZE = 65536;
    private static final Logger LOG;
    private long stream;
    private boolean conserveMemory;
    private int directBufferSize;
    private Buffer compressedDirectBuf;
    private int compressedDirectBufOff;
    private int compressedDirectBufLen;
    private Buffer uncompressedDirectBuf;
    private byte[] userBuf;
    private int userBufOff;
    private int userBufLen;
    private boolean finished;
    
    public Bzip2Decompressor(final boolean conserveMemory, final int directBufferSize) {
        this.compressedDirectBuf = null;
        this.uncompressedDirectBuf = null;
        this.userBuf = null;
        this.userBufOff = 0;
        this.userBufLen = 0;
        this.conserveMemory = conserveMemory;
        this.directBufferSize = directBufferSize;
        this.compressedDirectBuf = ByteBuffer.allocateDirect(directBufferSize);
        (this.uncompressedDirectBuf = ByteBuffer.allocateDirect(directBufferSize)).position(directBufferSize);
        this.stream = init(conserveMemory ? 1 : 0);
    }
    
    public Bzip2Decompressor() {
        this(false, 65536);
    }
    
    @Override
    public synchronized void setInput(final byte[] b, final int off, final int len) {
        if (b == null) {
            throw new NullPointerException();
        }
        if (off < 0 || len < 0 || off > b.length - len) {
            throw new ArrayIndexOutOfBoundsException();
        }
        this.userBuf = b;
        this.userBufOff = off;
        this.userBufLen = len;
        this.setInputFromSavedData();
        this.uncompressedDirectBuf.limit(this.directBufferSize);
        this.uncompressedDirectBuf.position(this.directBufferSize);
    }
    
    synchronized void setInputFromSavedData() {
        this.compressedDirectBufOff = 0;
        this.compressedDirectBufLen = this.userBufLen;
        if (this.compressedDirectBufLen > this.directBufferSize) {
            this.compressedDirectBufLen = this.directBufferSize;
        }
        this.compressedDirectBuf.rewind();
        ((ByteBuffer)this.compressedDirectBuf).put(this.userBuf, this.userBufOff, this.compressedDirectBufLen);
        this.userBufOff += this.compressedDirectBufLen;
        this.userBufLen -= this.compressedDirectBufLen;
    }
    
    @Override
    public synchronized void setDictionary(final byte[] b, final int off, final int len) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public synchronized boolean needsInput() {
        if (this.uncompressedDirectBuf.remaining() > 0) {
            return false;
        }
        if (this.compressedDirectBufLen <= 0) {
            if (this.userBufLen <= 0) {
                return true;
            }
            this.setInputFromSavedData();
        }
        return false;
    }
    
    @Override
    public synchronized boolean needsDictionary() {
        return false;
    }
    
    @Override
    public synchronized boolean finished() {
        return this.finished && this.uncompressedDirectBuf.remaining() == 0;
    }
    
    @Override
    public synchronized int decompress(final byte[] b, final int off, final int len) throws IOException {
        if (b == null) {
            throw new NullPointerException();
        }
        if (off < 0 || len < 0 || off > b.length - len) {
            throw new ArrayIndexOutOfBoundsException();
        }
        int n = this.uncompressedDirectBuf.remaining();
        if (n > 0) {
            n = Math.min(n, len);
            ((ByteBuffer)this.uncompressedDirectBuf).get(b, off, n);
            return n;
        }
        this.uncompressedDirectBuf.rewind();
        this.uncompressedDirectBuf.limit(this.directBufferSize);
        n = (this.finished ? 0 : this.inflateBytesDirect());
        this.uncompressedDirectBuf.limit(n);
        n = Math.min(n, len);
        ((ByteBuffer)this.uncompressedDirectBuf).get(b, off, n);
        return n;
    }
    
    public synchronized long getBytesWritten() {
        this.checkStream();
        return getBytesWritten(this.stream);
    }
    
    public synchronized long getBytesRead() {
        this.checkStream();
        return getBytesRead(this.stream);
    }
    
    @Override
    public synchronized int getRemaining() {
        this.checkStream();
        return this.userBufLen + getRemaining(this.stream);
    }
    
    @Override
    public synchronized void reset() {
        this.checkStream();
        end(this.stream);
        this.stream = init(this.conserveMemory ? 1 : 0);
        this.finished = false;
        final int n = 0;
        this.compressedDirectBufLen = n;
        this.compressedDirectBufOff = n;
        this.uncompressedDirectBuf.limit(this.directBufferSize);
        this.uncompressedDirectBuf.position(this.directBufferSize);
        final int n2 = 0;
        this.userBufLen = n2;
        this.userBufOff = n2;
    }
    
    @Override
    public synchronized void end() {
        if (this.stream != 0L) {
            end(this.stream);
            this.stream = 0L;
        }
    }
    
    static void initSymbols(final String libname) {
        initIDs(libname);
    }
    
    private void checkStream() {
        if (this.stream == 0L) {
            throw new NullPointerException();
        }
    }
    
    private static native void initIDs(final String p0);
    
    private static native long init(final int p0);
    
    private native int inflateBytesDirect();
    
    private static native long getBytesRead(final long p0);
    
    private static native long getBytesWritten(final long p0);
    
    private static native int getRemaining(final long p0);
    
    private static native void end(final long p0);
    
    static {
        LOG = LoggerFactory.getLogger(Bzip2Decompressor.class);
    }
}
