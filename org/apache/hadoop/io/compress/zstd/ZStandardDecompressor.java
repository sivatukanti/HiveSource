// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io.compress.zstd;

import org.apache.hadoop.io.compress.DirectDecompressor;
import org.apache.hadoop.util.NativeCodeLoader;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.nio.ByteBuffer;
import org.slf4j.Logger;
import org.apache.hadoop.io.compress.Decompressor;

public class ZStandardDecompressor implements Decompressor
{
    private static final Logger LOG;
    private long stream;
    private int directBufferSize;
    private ByteBuffer compressedDirectBuf;
    private int compressedDirectBufOff;
    private int bytesInCompressedBuffer;
    private ByteBuffer uncompressedDirectBuf;
    private byte[] userBuf;
    private int userBufOff;
    private int userBufferBytesToConsume;
    private boolean finished;
    private int remaining;
    private static boolean nativeZStandardLoaded;
    
    public static boolean isNativeCodeLoaded() {
        return ZStandardDecompressor.nativeZStandardLoaded;
    }
    
    public static int getRecommendedBufferSize() {
        return getStreamSize();
    }
    
    public ZStandardDecompressor() {
        this(getStreamSize());
    }
    
    public ZStandardDecompressor(final int bufferSize) {
        this.compressedDirectBuf = null;
        this.uncompressedDirectBuf = null;
        this.userBuf = null;
        this.userBufOff = 0;
        this.userBufferBytesToConsume = 0;
        this.remaining = 0;
        this.directBufferSize = bufferSize;
        this.compressedDirectBuf = ByteBuffer.allocateDirect(this.directBufferSize);
        (this.uncompressedDirectBuf = ByteBuffer.allocateDirect(this.directBufferSize)).position(this.directBufferSize);
        this.stream = create();
        this.reset();
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
        this.userBufferBytesToConsume = len;
        this.setInputFromSavedData();
        this.uncompressedDirectBuf.limit(this.directBufferSize);
        this.uncompressedDirectBuf.position(this.directBufferSize);
    }
    
    private void setInputFromSavedData() {
        this.compressedDirectBufOff = 0;
        this.bytesInCompressedBuffer = this.userBufferBytesToConsume;
        if (this.bytesInCompressedBuffer > this.directBufferSize) {
            this.bytesInCompressedBuffer = this.directBufferSize;
        }
        this.compressedDirectBuf.rewind();
        this.compressedDirectBuf.put(this.userBuf, this.userBufOff, this.bytesInCompressedBuffer);
        this.userBufOff += this.bytesInCompressedBuffer;
        this.userBufferBytesToConsume -= this.bytesInCompressedBuffer;
    }
    
    @Override
    public void setDictionary(final byte[] b, final int off, final int len) {
        throw new UnsupportedOperationException("Dictionary support is not enabled");
    }
    
    @Override
    public boolean needsInput() {
        if (this.uncompressedDirectBuf.remaining() > 0) {
            return false;
        }
        if (this.bytesInCompressedBuffer - this.compressedDirectBufOff <= 0) {
            if (this.userBufferBytesToConsume <= 0) {
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
        this.checkStream();
        if (b == null) {
            throw new NullPointerException();
        }
        if (off < 0 || len < 0 || off > b.length - len) {
            throw new ArrayIndexOutOfBoundsException();
        }
        int n = this.uncompressedDirectBuf.remaining();
        if (n > 0) {
            return this.populateUncompressedBuffer(b, off, len, n);
        }
        this.uncompressedDirectBuf.rewind();
        this.uncompressedDirectBuf.limit(this.directBufferSize);
        n = this.inflateBytesDirect(this.compressedDirectBuf, this.compressedDirectBufOff, this.bytesInCompressedBuffer, this.uncompressedDirectBuf, 0, this.directBufferSize);
        this.uncompressedDirectBuf.limit(n);
        return this.populateUncompressedBuffer(b, off, len, n);
    }
    
    @Override
    public int getRemaining() {
        this.checkStream();
        return this.userBufferBytesToConsume + this.remaining;
    }
    
    @Override
    public void reset() {
        this.checkStream();
        init(this.stream);
        this.remaining = 0;
        this.finished = false;
        this.compressedDirectBufOff = 0;
        this.bytesInCompressedBuffer = 0;
        this.uncompressedDirectBuf.limit(this.directBufferSize);
        this.uncompressedDirectBuf.position(this.directBufferSize);
        this.userBufOff = 0;
        this.userBufferBytesToConsume = 0;
    }
    
    @Override
    public void end() {
        if (this.stream != 0L) {
            free(this.stream);
            this.stream = 0L;
        }
    }
    
    @Override
    protected void finalize() {
        this.reset();
    }
    
    private void checkStream() {
        if (this.stream == 0L) {
            throw new NullPointerException("Stream not initialized");
        }
    }
    
    private int populateUncompressedBuffer(final byte[] b, final int off, final int len, int n) {
        n = Math.min(n, len);
        this.uncompressedDirectBuf.get(b, off, n);
        return n;
    }
    
    private static native void initIDs();
    
    private static native long create();
    
    private static native void init(final long p0);
    
    private native int inflateBytesDirect(final ByteBuffer p0, final int p1, final int p2, final ByteBuffer p3, final int p4, final int p5);
    
    private static native void free(final long p0);
    
    private static native int getStreamSize();
    
    int inflateDirect(final ByteBuffer src, final ByteBuffer dst) throws IOException {
        assert this instanceof ZStandardDirectDecompressor;
        final int originalPosition = dst.position();
        final int n = this.inflateBytesDirect(src, src.position(), src.limit(), dst, dst.position(), dst.limit());
        dst.position(originalPosition + n);
        if (this.bytesInCompressedBuffer > 0) {
            src.position(this.compressedDirectBufOff);
        }
        else {
            src.position(src.limit());
        }
        return n;
    }
    
    static {
        LOG = LoggerFactory.getLogger(ZStandardDecompressor.class);
        ZStandardDecompressor.nativeZStandardLoaded = false;
        if (NativeCodeLoader.isNativeCodeLoaded()) {
            try {
                initIDs();
                ZStandardDecompressor.nativeZStandardLoaded = true;
            }
            catch (Throwable t) {
                ZStandardDecompressor.LOG.warn("Error loading zstandard native libraries: " + t);
            }
        }
    }
    
    public static class ZStandardDirectDecompressor extends ZStandardDecompressor implements DirectDecompressor
    {
        private boolean endOfInput;
        
        public ZStandardDirectDecompressor(final int directBufferSize) {
            super(directBufferSize);
        }
        
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
            this.inflateDirect(src, dst);
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
