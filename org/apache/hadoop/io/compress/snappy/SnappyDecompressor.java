// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io.compress.snappy;

import org.apache.hadoop.io.compress.DirectDecompressor;
import org.apache.hadoop.util.NativeCodeLoader;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.Buffer;
import org.slf4j.Logger;
import org.apache.hadoop.io.compress.Decompressor;

public class SnappyDecompressor implements Decompressor
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
    private static boolean nativeSnappyLoaded;
    
    public static boolean isNativeCodeLoaded() {
        return SnappyDecompressor.nativeSnappyLoaded;
    }
    
    public SnappyDecompressor(final int directBufferSize) {
        this.compressedDirectBuf = null;
        this.uncompressedDirectBuf = null;
        this.userBuf = null;
        this.userBufOff = 0;
        this.userBufLen = 0;
        this.directBufferSize = directBufferSize;
        this.compressedDirectBuf = ByteBuffer.allocateDirect(directBufferSize);
        (this.uncompressedDirectBuf = ByteBuffer.allocateDirect(directBufferSize)).position(directBufferSize);
    }
    
    public SnappyDecompressor() {
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
        this.userBuf = b;
        this.userBufOff = off;
        this.userBufLen = len;
        this.setInputFromSavedData();
        this.uncompressedDirectBuf.limit(this.directBufferSize);
        this.uncompressedDirectBuf.position(this.directBufferSize);
    }
    
    void setInputFromSavedData() {
        this.compressedDirectBufLen = Math.min(this.userBufLen, this.directBufferSize);
        this.compressedDirectBuf.rewind();
        ((ByteBuffer)this.compressedDirectBuf).put(this.userBuf, this.userBufOff, this.compressedDirectBufLen);
        this.userBufOff += this.compressedDirectBufLen;
        this.userBufLen -= this.compressedDirectBufLen;
    }
    
    @Override
    public void setDictionary(final byte[] b, final int off, final int len) {
    }
    
    @Override
    public boolean needsInput() {
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
    public boolean needsDictionary() {
        return false;
    }
    
    @Override
    public boolean finished() {
        return this.finished && this.uncompressedDirectBuf.remaining() == 0;
    }
    
    @Override
    public int decompress(final byte[] b, final int off, final int len) throws IOException {
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
    public int getRemaining() {
        return 0;
    }
    
    @Override
    public void reset() {
        this.finished = false;
        this.compressedDirectBufLen = 0;
        this.uncompressedDirectBuf.limit(this.directBufferSize);
        this.uncompressedDirectBuf.position(this.directBufferSize);
        final int n = 0;
        this.userBufLen = n;
        this.userBufOff = n;
    }
    
    @Override
    public void end() {
    }
    
    private static native void initIDs();
    
    private native int decompressBytesDirect();
    
    int decompressDirect(final ByteBuffer src, ByteBuffer dst) throws IOException {
        assert this instanceof SnappyDirectDecompressor;
        ByteBuffer presliced = dst;
        if (dst.position() > 0) {
            presliced = dst;
            dst = dst.slice();
        }
        final Buffer originalCompressed = this.compressedDirectBuf;
        final Buffer originalUncompressed = this.uncompressedDirectBuf;
        final int originalBufferSize = this.directBufferSize;
        this.compressedDirectBuf = src.slice();
        this.compressedDirectBufLen = src.remaining();
        this.uncompressedDirectBuf = dst;
        this.directBufferSize = dst.remaining();
        int n = 0;
        try {
            n = this.decompressBytesDirect();
            presliced.position(presliced.position() + n);
            src.position(src.limit());
            this.finished = true;
        }
        finally {
            this.compressedDirectBuf = originalCompressed;
            this.uncompressedDirectBuf = originalUncompressed;
            this.compressedDirectBufLen = 0;
            this.directBufferSize = originalBufferSize;
        }
        return n;
    }
    
    static {
        LOG = LoggerFactory.getLogger(SnappyDecompressor.class.getName());
        SnappyDecompressor.nativeSnappyLoaded = false;
        if (NativeCodeLoader.isNativeCodeLoaded() && NativeCodeLoader.buildSupportsSnappy()) {
            try {
                initIDs();
                SnappyDecompressor.nativeSnappyLoaded = true;
            }
            catch (Throwable t) {
                SnappyDecompressor.LOG.error("failed to load SnappyDecompressor", t);
            }
        }
    }
    
    public static class SnappyDirectDecompressor extends SnappyDecompressor implements DirectDecompressor
    {
        private boolean endOfInput;
        
        @Override
        public boolean finished() {
            return this.endOfInput && super.finished();
        }
        
        @Override
        public void reset() {
            super.reset();
            this.endOfInput = true;
        }
        
        @Override
        public void decompress(final ByteBuffer src, final ByteBuffer dst) throws IOException {
            assert dst.isDirect() : "dst.isDirect()";
            assert src.isDirect() : "src.isDirect()";
            assert dst.remaining() > 0 : "dst.remaining() > 0";
            this.decompressDirect(src, dst);
            this.endOfInput = !src.hasRemaining();
        }
        
        @Override
        public void setDictionary(final byte[] b, final int off, final int len) {
            throw new UnsupportedOperationException("byte[] arrays are not supported for DirectDecompressor");
        }
        
        @Override
        public int decompress(final byte[] b, final int off, final int len) {
            throw new UnsupportedOperationException("byte[] arrays are not supported for DirectDecompressor");
        }
    }
}
