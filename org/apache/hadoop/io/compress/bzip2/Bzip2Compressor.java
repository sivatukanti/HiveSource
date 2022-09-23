// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io.compress.bzip2;

import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.nio.ByteBuffer;
import org.apache.hadoop.conf.Configuration;
import java.nio.Buffer;
import org.slf4j.Logger;
import org.apache.hadoop.io.compress.Compressor;

public class Bzip2Compressor implements Compressor
{
    private static final int DEFAULT_DIRECT_BUFFER_SIZE = 65536;
    static final int DEFAULT_BLOCK_SIZE = 9;
    static final int DEFAULT_WORK_FACTOR = 30;
    private static final Logger LOG;
    private long stream;
    private int blockSize;
    private int workFactor;
    private int directBufferSize;
    private byte[] userBuf;
    private int userBufOff;
    private int userBufLen;
    private Buffer uncompressedDirectBuf;
    private int uncompressedDirectBufOff;
    private int uncompressedDirectBufLen;
    private boolean keepUncompressedBuf;
    private Buffer compressedDirectBuf;
    private boolean finish;
    private boolean finished;
    
    public Bzip2Compressor() {
        this(9, 30, 65536);
    }
    
    public Bzip2Compressor(final Configuration conf) {
        this(Bzip2Factory.getBlockSize(conf), Bzip2Factory.getWorkFactor(conf), 65536);
    }
    
    public Bzip2Compressor(final int blockSize, final int workFactor, final int directBufferSize) {
        this.userBuf = null;
        this.userBufOff = 0;
        this.userBufLen = 0;
        this.uncompressedDirectBuf = null;
        this.uncompressedDirectBufOff = 0;
        this.uncompressedDirectBufLen = 0;
        this.keepUncompressedBuf = false;
        this.compressedDirectBuf = null;
        this.blockSize = blockSize;
        this.workFactor = workFactor;
        this.directBufferSize = directBufferSize;
        this.stream = init(blockSize, workFactor);
        this.uncompressedDirectBuf = ByteBuffer.allocateDirect(directBufferSize);
        (this.compressedDirectBuf = ByteBuffer.allocateDirect(directBufferSize)).position(directBufferSize);
    }
    
    @Override
    public synchronized void reinit(final Configuration conf) {
        this.reset();
        end(this.stream);
        if (conf == null) {
            this.stream = init(this.blockSize, this.workFactor);
            return;
        }
        this.blockSize = Bzip2Factory.getBlockSize(conf);
        this.workFactor = Bzip2Factory.getWorkFactor(conf);
        this.stream = init(this.blockSize, this.workFactor);
        if (Bzip2Compressor.LOG.isDebugEnabled()) {
            Bzip2Compressor.LOG.debug("Reinit compressor with new compression configuration");
        }
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
        this.uncompressedDirectBufOff = 0;
        this.setInputFromSavedData();
        this.compressedDirectBuf.limit(this.directBufferSize);
        this.compressedDirectBuf.position(this.directBufferSize);
    }
    
    synchronized void setInputFromSavedData() {
        final int len = Math.min(this.userBufLen, this.uncompressedDirectBuf.remaining());
        ((ByteBuffer)this.uncompressedDirectBuf).put(this.userBuf, this.userBufOff, len);
        this.userBufLen -= len;
        this.userBufOff += len;
        this.uncompressedDirectBufLen = this.uncompressedDirectBuf.position();
    }
    
    @Override
    public synchronized void setDictionary(final byte[] b, final int off, final int len) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public synchronized boolean needsInput() {
        if (this.compressedDirectBuf.remaining() > 0) {
            return false;
        }
        if (this.keepUncompressedBuf && this.uncompressedDirectBufLen > 0) {
            return false;
        }
        if (this.uncompressedDirectBuf.remaining() <= 0) {
            return false;
        }
        if (this.userBufLen <= 0) {
            return true;
        }
        this.setInputFromSavedData();
        return this.uncompressedDirectBuf.remaining() > 0;
    }
    
    @Override
    public synchronized void finish() {
        this.finish = true;
    }
    
    @Override
    public synchronized boolean finished() {
        return this.finished && this.compressedDirectBuf.remaining() == 0;
    }
    
    @Override
    public synchronized int compress(final byte[] b, final int off, final int len) throws IOException {
        if (b == null) {
            throw new NullPointerException();
        }
        if (off < 0 || len < 0 || off > b.length - len) {
            throw new ArrayIndexOutOfBoundsException();
        }
        int n = this.compressedDirectBuf.remaining();
        if (n > 0) {
            n = Math.min(n, len);
            ((ByteBuffer)this.compressedDirectBuf).get(b, off, n);
            return n;
        }
        this.compressedDirectBuf.rewind();
        this.compressedDirectBuf.limit(this.directBufferSize);
        n = this.deflateBytesDirect();
        this.compressedDirectBuf.limit(n);
        if (this.uncompressedDirectBufLen <= 0) {
            this.keepUncompressedBuf = false;
            this.uncompressedDirectBuf.clear();
            this.uncompressedDirectBufOff = 0;
            this.uncompressedDirectBufLen = 0;
        }
        else {
            this.keepUncompressedBuf = true;
        }
        n = Math.min(n, len);
        ((ByteBuffer)this.compressedDirectBuf).get(b, off, n);
        return n;
    }
    
    @Override
    public synchronized long getBytesWritten() {
        this.checkStream();
        return getBytesWritten(this.stream);
    }
    
    @Override
    public synchronized long getBytesRead() {
        this.checkStream();
        return getBytesRead(this.stream);
    }
    
    @Override
    public synchronized void reset() {
        this.checkStream();
        end(this.stream);
        this.stream = init(this.blockSize, this.workFactor);
        this.finish = false;
        this.finished = false;
        this.uncompressedDirectBuf.rewind();
        final int n = 0;
        this.uncompressedDirectBufLen = n;
        this.uncompressedDirectBufOff = n;
        this.keepUncompressedBuf = false;
        this.compressedDirectBuf.limit(this.directBufferSize);
        this.compressedDirectBuf.position(this.directBufferSize);
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
    
    private static native long init(final int p0, final int p1);
    
    private native int deflateBytesDirect();
    
    private static native long getBytesRead(final long p0);
    
    private static native long getBytesWritten(final long p0);
    
    private static native void end(final long p0);
    
    public static native String getLibraryName();
    
    static {
        LOG = LoggerFactory.getLogger(Bzip2Compressor.class);
    }
}
