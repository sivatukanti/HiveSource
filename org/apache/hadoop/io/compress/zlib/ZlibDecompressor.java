// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io.compress.zlib;

import org.apache.hadoop.io.compress.DirectDecompressor;
import org.apache.hadoop.util.NativeCodeLoader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.Buffer;
import org.apache.hadoop.io.compress.Decompressor;

public class ZlibDecompressor implements Decompressor
{
    private static final int DEFAULT_DIRECT_BUFFER_SIZE = 65536;
    private long stream;
    private CompressionHeader header;
    private int directBufferSize;
    private Buffer compressedDirectBuf;
    private int compressedDirectBufOff;
    private int compressedDirectBufLen;
    private Buffer uncompressedDirectBuf;
    private byte[] userBuf;
    private int userBufOff;
    private int userBufLen;
    private boolean finished;
    private boolean needDict;
    private static boolean nativeZlibLoaded;
    
    static boolean isNativeZlibLoaded() {
        return ZlibDecompressor.nativeZlibLoaded;
    }
    
    public ZlibDecompressor(final CompressionHeader header, final int directBufferSize) {
        this.compressedDirectBuf = null;
        this.uncompressedDirectBuf = null;
        this.userBuf = null;
        this.userBufOff = 0;
        this.userBufLen = 0;
        this.header = header;
        this.directBufferSize = directBufferSize;
        this.compressedDirectBuf = ByteBuffer.allocateDirect(directBufferSize);
        (this.uncompressedDirectBuf = ByteBuffer.allocateDirect(directBufferSize)).position(directBufferSize);
        this.stream = init(this.header.windowBits());
    }
    
    public ZlibDecompressor() {
        this(CompressionHeader.DEFAULT_HEADER, 65536);
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
    public void setDictionary(final byte[] b, final int off, final int len) {
        if (this.stream == 0L || b == null) {
            throw new NullPointerException();
        }
        if (off < 0 || len < 0 || off > b.length - len) {
            throw new ArrayIndexOutOfBoundsException();
        }
        setDictionary(this.stream, b, off, len);
        this.needDict = false;
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
        return this.needDict;
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
        this.uncompressedDirectBuf.rewind();
        this.uncompressedDirectBuf.limit(this.directBufferSize);
        n = this.inflateBytesDirect();
        this.uncompressedDirectBuf.limit(n);
        n = Math.min(n, len);
        ((ByteBuffer)this.uncompressedDirectBuf).get(b, off, n);
        return n;
    }
    
    public long getBytesWritten() {
        this.checkStream();
        return getBytesWritten(this.stream);
    }
    
    public long getBytesRead() {
        this.checkStream();
        return getBytesRead(this.stream);
    }
    
    @Override
    public int getRemaining() {
        this.checkStream();
        return this.userBufLen + getRemaining(this.stream);
    }
    
    @Override
    public void reset() {
        this.checkStream();
        reset(this.stream);
        this.finished = false;
        this.needDict = false;
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
    public void end() {
        if (this.stream != 0L) {
            end(this.stream);
            this.stream = 0L;
        }
    }
    
    @Override
    protected void finalize() {
        this.end();
    }
    
    private void checkStream() {
        if (this.stream == 0L) {
            throw new NullPointerException();
        }
    }
    
    private static native void initIDs();
    
    private static native long init(final int p0);
    
    private static native void setDictionary(final long p0, final byte[] p1, final int p2, final int p3);
    
    private native int inflateBytesDirect();
    
    private static native long getBytesRead(final long p0);
    
    private static native long getBytesWritten(final long p0);
    
    private static native int getRemaining(final long p0);
    
    private static native void reset(final long p0);
    
    private static native void end(final long p0);
    
    int inflateDirect(final ByteBuffer src, ByteBuffer dst) throws IOException {
        assert this instanceof ZlibDirectDecompressor;
        ByteBuffer presliced = dst;
        if (dst.position() > 0) {
            presliced = dst;
            dst = dst.slice();
        }
        final Buffer originalCompressed = this.compressedDirectBuf;
        final Buffer originalUncompressed = this.uncompressedDirectBuf;
        final int originalBufferSize = this.directBufferSize;
        this.compressedDirectBuf = src;
        this.compressedDirectBufOff = src.position();
        this.compressedDirectBufLen = src.remaining();
        this.uncompressedDirectBuf = dst;
        this.directBufferSize = dst.remaining();
        int n = 0;
        try {
            n = this.inflateBytesDirect();
            presliced.position(presliced.position() + n);
            if (this.compressedDirectBufLen > 0) {
                src.position(this.compressedDirectBufOff);
            }
            else {
                src.position(src.limit());
            }
        }
        finally {
            this.compressedDirectBuf = originalCompressed;
            this.uncompressedDirectBuf = originalUncompressed;
            this.compressedDirectBufOff = 0;
            this.compressedDirectBufLen = 0;
            this.directBufferSize = originalBufferSize;
        }
        return n;
    }
    
    static {
        ZlibDecompressor.nativeZlibLoaded = false;
        if (NativeCodeLoader.isNativeCodeLoaded()) {
            try {
                initIDs();
                ZlibDecompressor.nativeZlibLoaded = true;
            }
            catch (Throwable t) {}
        }
    }
    
    public enum CompressionHeader
    {
        NO_HEADER(-15), 
        DEFAULT_HEADER(15), 
        GZIP_FORMAT(31), 
        AUTODETECT_GZIP_ZLIB(47);
        
        private final int windowBits;
        
        private CompressionHeader(final int windowBits) {
            this.windowBits = windowBits;
        }
        
        public int windowBits() {
            return this.windowBits;
        }
    }
    
    public static class ZlibDirectDecompressor extends ZlibDecompressor implements DirectDecompressor
    {
        private boolean endOfInput;
        
        public ZlibDirectDecompressor() {
            super(CompressionHeader.DEFAULT_HEADER, 0);
        }
        
        public ZlibDirectDecompressor(final CompressionHeader header, final int directBufferSize) {
            super(header, directBufferSize);
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
