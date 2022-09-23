// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io.compress.zlib;

import org.apache.hadoop.util.NativeCodeLoader;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.nio.ByteBuffer;
import org.apache.hadoop.conf.Configuration;
import java.nio.Buffer;
import org.slf4j.Logger;
import org.apache.hadoop.io.compress.Compressor;

public class ZlibCompressor implements Compressor
{
    private static final Logger LOG;
    private static final int DEFAULT_DIRECT_BUFFER_SIZE = 65536;
    private long stream;
    private CompressionLevel level;
    private CompressionStrategy strategy;
    private final CompressionHeader windowBits;
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
    private static boolean nativeZlibLoaded;
    
    static boolean isNativeZlibLoaded() {
        return ZlibCompressor.nativeZlibLoaded;
    }
    
    protected final void construct(final CompressionLevel level, final CompressionStrategy strategy, final CompressionHeader header, final int directBufferSize) {
    }
    
    public ZlibCompressor() {
        this(CompressionLevel.DEFAULT_COMPRESSION, CompressionStrategy.DEFAULT_STRATEGY, CompressionHeader.DEFAULT_HEADER, 65536);
    }
    
    public ZlibCompressor(final Configuration conf) {
        this(ZlibFactory.getCompressionLevel(conf), ZlibFactory.getCompressionStrategy(conf), CompressionHeader.DEFAULT_HEADER, 65536);
    }
    
    public ZlibCompressor(final CompressionLevel level, final CompressionStrategy strategy, final CompressionHeader header, final int directBufferSize) {
        this.userBuf = null;
        this.userBufOff = 0;
        this.userBufLen = 0;
        this.uncompressedDirectBuf = null;
        this.uncompressedDirectBufOff = 0;
        this.uncompressedDirectBufLen = 0;
        this.keepUncompressedBuf = false;
        this.compressedDirectBuf = null;
        this.level = level;
        this.strategy = strategy;
        this.windowBits = header;
        this.stream = init(this.level.compressionLevel(), this.strategy.compressionStrategy(), this.windowBits.windowBits());
        this.directBufferSize = directBufferSize;
        this.uncompressedDirectBuf = ByteBuffer.allocateDirect(directBufferSize);
        (this.compressedDirectBuf = ByteBuffer.allocateDirect(directBufferSize)).position(directBufferSize);
    }
    
    @Override
    public void reinit(final Configuration conf) {
        this.reset();
        if (conf == null) {
            return;
        }
        end(this.stream);
        this.level = ZlibFactory.getCompressionLevel(conf);
        this.strategy = ZlibFactory.getCompressionStrategy(conf);
        this.stream = init(this.level.compressionLevel(), this.strategy.compressionStrategy(), this.windowBits.windowBits());
        if (ZlibCompressor.LOG.isDebugEnabled()) {
            ZlibCompressor.LOG.debug("Reinit compressor with new compression configuration");
        }
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
        this.uncompressedDirectBufOff = 0;
        this.setInputFromSavedData();
        this.compressedDirectBuf.limit(this.directBufferSize);
        this.compressedDirectBuf.position(this.directBufferSize);
    }
    
    void setInputFromSavedData() {
        final int len = Math.min(this.userBufLen, this.uncompressedDirectBuf.remaining());
        ((ByteBuffer)this.uncompressedDirectBuf).put(this.userBuf, this.userBufOff, len);
        this.userBufLen -= len;
        this.userBufOff += len;
        this.uncompressedDirectBufLen = this.uncompressedDirectBuf.position();
    }
    
    @Override
    public void setDictionary(final byte[] b, final int off, final int len) {
        if (this.stream == 0L || b == null) {
            throw new NullPointerException();
        }
        if (off < 0 || len < 0 || off > b.length - len) {
            throw new ArrayIndexOutOfBoundsException();
        }
        setDictionary(this.stream, b, off, len);
    }
    
    @Override
    public boolean needsInput() {
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
    public void finish() {
        this.finish = true;
    }
    
    @Override
    public boolean finished() {
        return this.finished && this.compressedDirectBuf.remaining() == 0;
    }
    
    @Override
    public int compress(final byte[] b, final int off, final int len) throws IOException {
        if (b == null) {
            throw new NullPointerException();
        }
        if (off < 0 || len < 0 || off > b.length - len) {
            throw new ArrayIndexOutOfBoundsException();
        }
        int n = 0;
        n = this.compressedDirectBuf.remaining();
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
    public long getBytesWritten() {
        this.checkStream();
        return getBytesWritten(this.stream);
    }
    
    @Override
    public long getBytesRead() {
        this.checkStream();
        return getBytesRead(this.stream);
    }
    
    @Override
    public void reset() {
        this.checkStream();
        reset(this.stream);
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
    public void end() {
        if (this.stream != 0L) {
            end(this.stream);
            this.stream = 0L;
        }
    }
    
    private void checkStream() {
        if (this.stream == 0L) {
            throw new NullPointerException();
        }
    }
    
    private static native void initIDs();
    
    private static native long init(final int p0, final int p1, final int p2);
    
    private static native void setDictionary(final long p0, final byte[] p1, final int p2, final int p3);
    
    private native int deflateBytesDirect();
    
    private static native long getBytesRead(final long p0);
    
    private static native long getBytesWritten(final long p0);
    
    private static native void reset(final long p0);
    
    private static native void end(final long p0);
    
    public static native String getLibraryName();
    
    static {
        LOG = LoggerFactory.getLogger(ZlibCompressor.class);
        ZlibCompressor.nativeZlibLoaded = false;
        if (NativeCodeLoader.isNativeCodeLoaded()) {
            try {
                initIDs();
                ZlibCompressor.nativeZlibLoaded = true;
            }
            catch (Throwable t) {}
        }
    }
    
    public enum CompressionLevel
    {
        NO_COMPRESSION(0), 
        BEST_SPEED(1), 
        TWO(2), 
        THREE(3), 
        FOUR(4), 
        FIVE(5), 
        SIX(6), 
        SEVEN(7), 
        EIGHT(8), 
        BEST_COMPRESSION(9), 
        DEFAULT_COMPRESSION(-1);
        
        private final int compressionLevel;
        
        private CompressionLevel(final int level) {
            this.compressionLevel = level;
        }
        
        int compressionLevel() {
            return this.compressionLevel;
        }
    }
    
    public enum CompressionStrategy
    {
        FILTERED(1), 
        HUFFMAN_ONLY(2), 
        RLE(3), 
        FIXED(4), 
        DEFAULT_STRATEGY(0);
        
        private final int compressionStrategy;
        
        private CompressionStrategy(final int strategy) {
            this.compressionStrategy = strategy;
        }
        
        int compressionStrategy() {
            return this.compressionStrategy;
        }
    }
    
    public enum CompressionHeader
    {
        NO_HEADER(-15), 
        DEFAULT_HEADER(15), 
        GZIP_FORMAT(31);
        
        private final int windowBits;
        
        private CompressionHeader(final int windowBits) {
            this.windowBits = windowBits;
        }
        
        public int windowBits() {
            return this.windowBits;
        }
    }
}
