// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io.compress.snappy;

import org.apache.hadoop.util.NativeCodeLoader;
import org.slf4j.LoggerFactory;
import org.apache.hadoop.conf.Configuration;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.Buffer;
import org.slf4j.Logger;
import org.apache.hadoop.io.compress.Compressor;

public class SnappyCompressor implements Compressor
{
    private static final Logger LOG;
    private static final int DEFAULT_DIRECT_BUFFER_SIZE = 65536;
    private int directBufferSize;
    private Buffer compressedDirectBuf;
    private int uncompressedDirectBufLen;
    private Buffer uncompressedDirectBuf;
    private byte[] userBuf;
    private int userBufOff;
    private int userBufLen;
    private boolean finish;
    private boolean finished;
    private long bytesRead;
    private long bytesWritten;
    private static boolean nativeSnappyLoaded;
    
    public static boolean isNativeCodeLoaded() {
        return SnappyCompressor.nativeSnappyLoaded;
    }
    
    public SnappyCompressor(final int directBufferSize) {
        this.compressedDirectBuf = null;
        this.uncompressedDirectBuf = null;
        this.userBuf = null;
        this.userBufOff = 0;
        this.userBufLen = 0;
        this.bytesRead = 0L;
        this.bytesWritten = 0L;
        this.directBufferSize = directBufferSize;
        this.uncompressedDirectBuf = ByteBuffer.allocateDirect(directBufferSize);
        (this.compressedDirectBuf = ByteBuffer.allocateDirect(directBufferSize)).position(directBufferSize);
    }
    
    public SnappyCompressor() {
        this(65536);
    }
    
    @Override
    public void setInput(final byte[] b, final int off, final int len) {
        if (b == null) {
            throw new NullPointerException();
        }
        if (off < 0 || len < 0 || off > b.length - len) {
            throw new ArrayIndexOutOfBoundsException();
        }
        this.finished = false;
        if (len > this.uncompressedDirectBuf.remaining()) {
            this.userBuf = b;
            this.userBufOff = off;
            this.userBufLen = len;
        }
        else {
            ((ByteBuffer)this.uncompressedDirectBuf).put(b, off, len);
            this.uncompressedDirectBufLen = this.uncompressedDirectBuf.position();
        }
        this.bytesRead += len;
    }
    
    void setInputFromSavedData() {
        if (0 >= this.userBufLen) {
            return;
        }
        this.finished = false;
        this.uncompressedDirectBufLen = Math.min(this.userBufLen, this.directBufferSize);
        ((ByteBuffer)this.uncompressedDirectBuf).put(this.userBuf, this.userBufOff, this.uncompressedDirectBufLen);
        this.userBufOff += this.uncompressedDirectBufLen;
        this.userBufLen -= this.uncompressedDirectBufLen;
    }
    
    @Override
    public void setDictionary(final byte[] b, final int off, final int len) {
    }
    
    @Override
    public boolean needsInput() {
        return this.compressedDirectBuf.remaining() <= 0 && this.uncompressedDirectBuf.remaining() != 0 && this.userBufLen <= 0;
    }
    
    @Override
    public void finish() {
        this.finish = true;
    }
    
    @Override
    public boolean finished() {
        return this.finish && this.finished && this.compressedDirectBuf.remaining() == 0;
    }
    
    @Override
    public int compress(final byte[] b, final int off, final int len) throws IOException {
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
            this.bytesWritten += n;
            return n;
        }
        this.compressedDirectBuf.clear();
        this.compressedDirectBuf.limit(0);
        if (0 == this.uncompressedDirectBuf.position()) {
            this.setInputFromSavedData();
            if (0 == this.uncompressedDirectBuf.position()) {
                this.finished = true;
                return 0;
            }
        }
        n = this.compressBytesDirect();
        this.compressedDirectBuf.limit(n);
        this.uncompressedDirectBuf.clear();
        if (0 == this.userBufLen) {
            this.finished = true;
        }
        n = Math.min(n, len);
        this.bytesWritten += n;
        ((ByteBuffer)this.compressedDirectBuf).get(b, off, n);
        return n;
    }
    
    @Override
    public void reset() {
        this.finish = false;
        this.finished = false;
        this.uncompressedDirectBuf.clear();
        this.uncompressedDirectBufLen = 0;
        this.compressedDirectBuf.clear();
        this.compressedDirectBuf.limit(0);
        final int n = 0;
        this.userBufLen = n;
        this.userBufOff = n;
        final long n2 = 0L;
        this.bytesWritten = n2;
        this.bytesRead = n2;
    }
    
    @Override
    public void reinit(final Configuration conf) {
        this.reset();
    }
    
    @Override
    public long getBytesRead() {
        return this.bytesRead;
    }
    
    @Override
    public long getBytesWritten() {
        return this.bytesWritten;
    }
    
    @Override
    public void end() {
    }
    
    private static native void initIDs();
    
    private native int compressBytesDirect();
    
    public static native String getLibraryName();
    
    static {
        LOG = LoggerFactory.getLogger(SnappyCompressor.class.getName());
        SnappyCompressor.nativeSnappyLoaded = false;
        if (NativeCodeLoader.isNativeCodeLoaded() && NativeCodeLoader.buildSupportsSnappy()) {
            try {
                initIDs();
                SnappyCompressor.nativeSnappyLoaded = true;
            }
            catch (Throwable t) {
                SnappyCompressor.LOG.error("failed to load SnappyCompressor", t);
            }
        }
    }
}
