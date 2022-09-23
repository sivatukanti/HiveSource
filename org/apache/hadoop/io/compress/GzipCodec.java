// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io.compress;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.compress.zlib.ZlibCompressor;
import java.util.zip.GZIPOutputStream;
import org.apache.hadoop.io.compress.zlib.ZlibDecompressor;
import org.apache.hadoop.io.compress.zlib.BuiltInGzipDecompressor;
import java.io.InputStream;
import java.io.IOException;
import org.apache.hadoop.io.compress.zlib.ZlibFactory;
import java.io.OutputStream;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public class GzipCodec extends DefaultCodec
{
    @Override
    public CompressionOutputStream createOutputStream(final OutputStream out) throws IOException {
        if (!ZlibFactory.isNativeZlibLoaded(this.conf)) {
            return new GzipOutputStream(out);
        }
        return CompressionCodec.Util.createOutputStreamWithCodecPool(this, this.conf, out);
    }
    
    @Override
    public CompressionOutputStream createOutputStream(final OutputStream out, final Compressor compressor) throws IOException {
        return (compressor != null) ? new CompressorStream(out, compressor, this.conf.getInt("io.file.buffer.size", 4096)) : this.createOutputStream(out);
    }
    
    @Override
    public Compressor createCompressor() {
        return ZlibFactory.isNativeZlibLoaded(this.conf) ? new GzipZlibCompressor(this.conf) : null;
    }
    
    @Override
    public Class<? extends Compressor> getCompressorType() {
        return ZlibFactory.isNativeZlibLoaded(this.conf) ? GzipZlibCompressor.class : null;
    }
    
    @Override
    public CompressionInputStream createInputStream(final InputStream in) throws IOException {
        return CompressionCodec.Util.createInputStreamWithCodecPool(this, this.conf, in);
    }
    
    @Override
    public CompressionInputStream createInputStream(final InputStream in, Decompressor decompressor) throws IOException {
        if (decompressor == null) {
            decompressor = this.createDecompressor();
        }
        return new DecompressorStream(in, decompressor, this.conf.getInt("io.file.buffer.size", 4096));
    }
    
    @Override
    public Decompressor createDecompressor() {
        return ZlibFactory.isNativeZlibLoaded(this.conf) ? new GzipZlibDecompressor() : new BuiltInGzipDecompressor();
    }
    
    @Override
    public Class<? extends Decompressor> getDecompressorType() {
        return (Class<? extends Decompressor>)(ZlibFactory.isNativeZlibLoaded(this.conf) ? GzipZlibDecompressor.class : BuiltInGzipDecompressor.class);
    }
    
    @Override
    public DirectDecompressor createDirectDecompressor() {
        return ZlibFactory.isNativeZlibLoaded(this.conf) ? new ZlibDecompressor.ZlibDirectDecompressor(ZlibDecompressor.CompressionHeader.AUTODETECT_GZIP_ZLIB, 0) : null;
    }
    
    @Override
    public String getDefaultExtension() {
        return ".gz";
    }
    
    @InterfaceStability.Evolving
    protected static class GzipOutputStream extends CompressorStream
    {
        public GzipOutputStream(final OutputStream out) throws IOException {
            super(new ResetableGZIPOutputStream(out));
        }
        
        protected GzipOutputStream(final CompressorStream out) {
            super(out);
        }
        
        @Override
        public void close() throws IOException {
            this.out.close();
        }
        
        @Override
        public void flush() throws IOException {
            this.out.flush();
        }
        
        @Override
        public void write(final int b) throws IOException {
            this.out.write(b);
        }
        
        @Override
        public void write(final byte[] data, final int offset, final int length) throws IOException {
            this.out.write(data, offset, length);
        }
        
        @Override
        public void finish() throws IOException {
            ((ResetableGZIPOutputStream)this.out).finish();
        }
        
        @Override
        public void resetState() throws IOException {
            ((ResetableGZIPOutputStream)this.out).resetState();
        }
        
        private static class ResetableGZIPOutputStream extends GZIPOutputStream
        {
            private static final byte[] GZIP_HEADER;
            private boolean reset;
            
            public ResetableGZIPOutputStream(final OutputStream out) throws IOException {
                super(out);
                this.reset = false;
            }
            
            public synchronized void resetState() throws IOException {
                this.reset = true;
            }
            
            @Override
            public synchronized void write(final byte[] buf, final int off, final int len) throws IOException {
                if (this.reset) {
                    this.def.reset();
                    this.crc.reset();
                    this.out.write(ResetableGZIPOutputStream.GZIP_HEADER);
                    this.reset = false;
                }
                super.write(buf, off, len);
            }
            
            @Override
            public synchronized void close() throws IOException {
                this.reset = false;
                super.close();
            }
            
            static {
                GZIP_HEADER = new byte[] { 31, -117, 8, 0, 0, 0, 0, 0, 0, 0 };
            }
        }
    }
    
    static final class GzipZlibCompressor extends ZlibCompressor
    {
        public GzipZlibCompressor() {
            super(CompressionLevel.DEFAULT_COMPRESSION, CompressionStrategy.DEFAULT_STRATEGY, CompressionHeader.GZIP_FORMAT, 65536);
        }
        
        public GzipZlibCompressor(final Configuration conf) {
            super(ZlibFactory.getCompressionLevel(conf), ZlibFactory.getCompressionStrategy(conf), CompressionHeader.GZIP_FORMAT, 65536);
        }
    }
    
    static final class GzipZlibDecompressor extends ZlibDecompressor
    {
        public GzipZlibDecompressor() {
            super(CompressionHeader.AUTODETECT_GZIP_ZLIB, 65536);
        }
    }
}
