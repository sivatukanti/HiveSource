// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.shims;

import org.apache.hadoop.fs.ReadOption;
import java.util.EnumSet;
import java.nio.ByteBuffer;
import org.apache.hadoop.io.ByteBufferPool;
import org.apache.hadoop.io.compress.DirectDecompressor;
import org.apache.hadoop.io.compress.snappy.SnappyDecompressor;
import org.apache.hadoop.io.compress.zlib.ZlibDecompressor;
import java.io.IOException;
import org.apache.hadoop.fs.FSDataInputStream;

class ZeroCopyShims
{
    public static HadoopShims.ZeroCopyReaderShim getZeroCopyReader(final FSDataInputStream in, final HadoopShims.ByteBufferPoolShim pool) throws IOException {
        return new ZeroCopyAdapter(in, pool);
    }
    
    public static HadoopShims.DirectDecompressorShim getDirectDecompressor(final HadoopShims.DirectCompressionType codec) {
        DirectDecompressor decompressor = null;
        switch (codec) {
            case ZLIB: {
                decompressor = new ZlibDecompressor.ZlibDirectDecompressor();
                break;
            }
            case ZLIB_NOHEADER: {
                decompressor = new ZlibDecompressor.ZlibDirectDecompressor(ZlibDecompressor.CompressionHeader.NO_HEADER, 0);
                break;
            }
            case SNAPPY: {
                decompressor = new SnappyDecompressor.SnappyDirectDecompressor();
                break;
            }
        }
        if (decompressor != null) {
            return new DirectDecompressorAdapter(decompressor);
        }
        return null;
    }
    
    private static final class ByteBufferPoolAdapter implements ByteBufferPool
    {
        private HadoopShims.ByteBufferPoolShim pool;
        
        public ByteBufferPoolAdapter(final HadoopShims.ByteBufferPoolShim pool) {
            this.pool = pool;
        }
        
        @Override
        public final ByteBuffer getBuffer(final boolean direct, final int length) {
            return this.pool.getBuffer(direct, length);
        }
        
        @Override
        public final void putBuffer(final ByteBuffer buffer) {
            this.pool.putBuffer(buffer);
        }
    }
    
    private static final class ZeroCopyAdapter implements HadoopShims.ZeroCopyReaderShim
    {
        private final FSDataInputStream in;
        private final ByteBufferPoolAdapter pool;
        private static final EnumSet<ReadOption> CHECK_SUM;
        private static final EnumSet<ReadOption> NO_CHECK_SUM;
        
        public ZeroCopyAdapter(final FSDataInputStream in, final HadoopShims.ByteBufferPoolShim poolshim) {
            this.in = in;
            if (poolshim != null) {
                this.pool = new ByteBufferPoolAdapter(poolshim);
            }
            else {
                this.pool = null;
            }
        }
        
        @Override
        public final ByteBuffer readBuffer(final int maxLength, final boolean verifyChecksums) throws IOException {
            EnumSet<ReadOption> options = ZeroCopyAdapter.NO_CHECK_SUM;
            if (verifyChecksums) {
                options = ZeroCopyAdapter.CHECK_SUM;
            }
            return this.in.read(this.pool, maxLength, options);
        }
        
        @Override
        public final void releaseBuffer(final ByteBuffer buffer) {
            this.in.releaseBuffer(buffer);
        }
        
        static {
            CHECK_SUM = EnumSet.noneOf(ReadOption.class);
            NO_CHECK_SUM = EnumSet.of(ReadOption.SKIP_CHECKSUMS);
        }
    }
    
    private static final class DirectDecompressorAdapter implements HadoopShims.DirectDecompressorShim
    {
        private final DirectDecompressor decompressor;
        
        public DirectDecompressorAdapter(final DirectDecompressor decompressor) {
            this.decompressor = decompressor;
        }
        
        @Override
        public void decompress(final ByteBuffer src, final ByteBuffer dst) throws IOException {
            this.decompressor.decompress(src, dst);
        }
    }
}
