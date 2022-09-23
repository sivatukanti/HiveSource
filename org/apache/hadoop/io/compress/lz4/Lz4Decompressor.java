// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io.compress.lz4;

import org.apache.hadoop.util.NativeCodeLoader;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.Buffer;
import org.slf4j.Logger;
import org.apache.hadoop.io.compress.Decompressor;

public class Lz4Decompressor implements Decompressor
{
    private static final Logger LOG;
    private static final int DEFAULT_DIRECT_BUFFER_SIZE = 65536;
    private int directBufferSize;
    private Buffer compressedDirectBuf;
    private int compressedDirectBufLen;
    private Buffer uncompressedDirectBuf;
    private byte[] userBuf;
    private int userBufOff;
    private int userBufLen;
    private boolean finished;
    
    public Lz4Decompressor(final int directBufferSize) {
        this.compressedDirectBuf = null;
        this.uncompressedDirectBuf = null;
        this.userBuf = null;
        this.userBufOff = 0;
        this.userBufLen = 0;
        this.directBufferSize = directBufferSize;
        this.compressedDirectBuf = ByteBuffer.allocateDirect(directBufferSize);
        (this.uncompressedDirectBuf = ByteBuffer.allocateDirect(directBufferSize)).position(directBufferSize);
    }
    
    public Lz4Decompressor() {
        this(65536);
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
        this.compressedDirectBufLen = Math.min(this.userBufLen, this.directBufferSize);
        this.compressedDirectBuf.rewind();
        ((ByteBuffer)this.compressedDirectBuf).put(this.userBuf, this.userBufOff, this.compressedDirectBufLen);
        this.userBufOff += this.compressedDirectBufLen;
        this.userBufLen -= this.compressedDirectBufLen;
    }
    
    @Override
    public synchronized void setDictionary(final byte[] b, final int off, final int len) {
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
        int n = 0;
        n = this.uncompressedDirectBuf.remaining();
        if (n > 0) {
            n = Math.min(n, len);
            ((ByteBuffer)this.uncompressedDirectBuf).get(b, off, n);
            return n;
        }
        if (this.compressedDirectBufLen > 0) {
            this.uncompressedDirectBuf.rewind();
            this.uncompressedDirectBuf.limit(this.directBufferSize);
            n = this.decompressBytesDirect();
            this.uncompressedDirectBuf.limit(n);
            if (this.userBufLen <= 0) {
                this.finished = true;
            }
            n = Math.min(n, len);
            ((ByteBuffer)this.uncompressedDirectBuf).get(b, off, n);
        }
        return n;
    }
    
    @Override
    public synchronized int getRemaining() {
        return 0;
    }
    
    @Override
    public synchronized void reset() {
        this.finished = false;
        this.compressedDirectBufLen = 0;
        this.uncompressedDirectBuf.limit(this.directBufferSize);
        this.uncompressedDirectBuf.position(this.directBufferSize);
        final int n = 0;
        this.userBufLen = n;
        this.userBufOff = n;
    }
    
    @Override
    public synchronized void end() {
    }
    
    private static native void initIDs();
    
    private native int decompressBytesDirect();
    
    static {
        LOG = LoggerFactory.getLogger(Lz4Compressor.class.getName());
        if (NativeCodeLoader.isNativeCodeLoaded()) {
            try {
                initIDs();
            }
            catch (Throwable t) {
                Lz4Decompressor.LOG.warn(t.toString());
            }
        }
        else {
            Lz4Decompressor.LOG.error("Cannot load " + Lz4Compressor.class.getName() + " without native hadoop library!");
        }
    }
}
