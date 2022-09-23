// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io.compress.zstd;

import org.apache.hadoop.util.NativeCodeLoader;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import org.apache.hadoop.io.compress.ZStandardCodec;
import org.apache.hadoop.conf.Configuration;
import com.google.common.annotations.VisibleForTesting;
import java.nio.ByteBuffer;
import org.slf4j.Logger;
import org.apache.hadoop.io.compress.Compressor;

public class ZStandardCompressor implements Compressor
{
    private static final Logger LOG;
    private long stream;
    private int level;
    private int directBufferSize;
    private byte[] userBuf;
    private int userBufOff;
    private int userBufLen;
    private ByteBuffer uncompressedDirectBuf;
    private int uncompressedDirectBufOff;
    private int uncompressedDirectBufLen;
    private boolean keepUncompressedBuf;
    private ByteBuffer compressedDirectBuf;
    private boolean finish;
    private boolean finished;
    private long bytesRead;
    private long bytesWritten;
    private static boolean nativeZStandardLoaded;
    
    public static boolean isNativeCodeLoaded() {
        return ZStandardCompressor.nativeZStandardLoaded;
    }
    
    public static int getRecommendedBufferSize() {
        return getStreamSize();
    }
    
    @VisibleForTesting
    ZStandardCompressor() {
        this(3, 4096);
    }
    
    public ZStandardCompressor(final int level, final int bufferSize) {
        this(level, bufferSize, bufferSize);
    }
    
    @VisibleForTesting
    ZStandardCompressor(final int level, final int inputBufferSize, final int outputBufferSize) {
        this.userBuf = null;
        this.userBufOff = 0;
        this.userBufLen = 0;
        this.uncompressedDirectBuf = null;
        this.uncompressedDirectBufOff = 0;
        this.uncompressedDirectBufLen = 0;
        this.keepUncompressedBuf = false;
        this.compressedDirectBuf = null;
        this.bytesRead = 0L;
        this.bytesWritten = 0L;
        this.level = level;
        this.stream = create();
        this.directBufferSize = outputBufferSize;
        this.uncompressedDirectBuf = ByteBuffer.allocateDirect(inputBufferSize);
        (this.compressedDirectBuf = ByteBuffer.allocateDirect(outputBufferSize)).position(outputBufferSize);
        this.reset();
    }
    
    @Override
    public void reinit(final Configuration conf) {
        if (conf == null) {
            return;
        }
        this.level = ZStandardCodec.getCompressionLevel(conf);
        this.reset();
        ZStandardCompressor.LOG.debug("Reinit compressor with new compression configuration");
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
    
    private void setInputFromSavedData() {
        final int len = Math.min(this.userBufLen, this.uncompressedDirectBuf.remaining());
        this.uncompressedDirectBuf.put(this.userBuf, this.userBufOff, len);
        this.userBufLen -= len;
        this.userBufOff += len;
        this.uncompressedDirectBufLen = this.uncompressedDirectBuf.position();
    }
    
    @Override
    public void setDictionary(final byte[] b, final int off, final int len) {
        throw new UnsupportedOperationException("Dictionary support is not enabled");
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
        this.checkStream();
        if (b == null) {
            throw new NullPointerException();
        }
        if (off < 0 || len < 0 || off > b.length - len) {
            throw new ArrayIndexOutOfBoundsException();
        }
        int n = this.compressedDirectBuf.remaining();
        if (n > 0) {
            n = Math.min(n, len);
            this.compressedDirectBuf.get(b, off, n);
            return n;
        }
        this.compressedDirectBuf.rewind();
        this.compressedDirectBuf.limit(this.directBufferSize);
        n = this.deflateBytesDirect(this.uncompressedDirectBuf, this.uncompressedDirectBufOff, this.uncompressedDirectBufLen, this.compressedDirectBuf, this.directBufferSize);
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
        this.compressedDirectBuf.get(b, off, n);
        return n;
    }
    
    @Override
    public long getBytesWritten() {
        this.checkStream();
        return this.bytesWritten;
    }
    
    @Override
    public long getBytesRead() {
        this.checkStream();
        return this.bytesRead;
    }
    
    @Override
    public void reset() {
        this.checkStream();
        init(this.level, this.stream);
        this.finish = false;
        this.finished = false;
        this.bytesRead = 0L;
        this.bytesWritten = 0L;
        this.uncompressedDirectBuf.rewind();
        this.uncompressedDirectBufOff = 0;
        this.uncompressedDirectBufLen = 0;
        this.keepUncompressedBuf = false;
        this.compressedDirectBuf.limit(this.directBufferSize);
        this.compressedDirectBuf.position(this.directBufferSize);
        this.userBufOff = 0;
        this.userBufLen = 0;
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
    
    private static native long create();
    
    private static native void init(final int p0, final long p1);
    
    private native int deflateBytesDirect(final ByteBuffer p0, final int p1, final int p2, final ByteBuffer p3, final int p4);
    
    private static native int getStreamSize();
    
    private static native void end(final long p0);
    
    private static native void initIDs();
    
    public static native String getLibraryName();
    
    static {
        LOG = LoggerFactory.getLogger(ZStandardCompressor.class);
        ZStandardCompressor.nativeZStandardLoaded = false;
        if (NativeCodeLoader.isNativeCodeLoaded()) {
            try {
                initIDs();
                ZStandardCompressor.nativeZStandardLoaded = true;
            }
            catch (Throwable t) {
                ZStandardCompressor.LOG.warn("Error loading zstandard native libraries: " + t);
            }
        }
    }
}
