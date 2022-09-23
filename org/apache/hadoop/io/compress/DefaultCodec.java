// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io.compress;

import org.slf4j.LoggerFactory;
import java.io.InputStream;
import org.apache.hadoop.io.compress.zlib.ZlibFactory;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.hadoop.conf.Configuration;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.conf.Configurable;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public class DefaultCodec implements Configurable, CompressionCodec, DirectDecompressionCodec
{
    private static final Logger LOG;
    Configuration conf;
    
    @Override
    public void setConf(final Configuration conf) {
        this.conf = conf;
    }
    
    @Override
    public Configuration getConf() {
        return this.conf;
    }
    
    @Override
    public CompressionOutputStream createOutputStream(final OutputStream out) throws IOException {
        return Util.createOutputStreamWithCodecPool(this, this.conf, out);
    }
    
    @Override
    public CompressionOutputStream createOutputStream(final OutputStream out, final Compressor compressor) throws IOException {
        return new CompressorStream(out, compressor, this.conf.getInt("io.file.buffer.size", 4096));
    }
    
    @Override
    public Class<? extends Compressor> getCompressorType() {
        return ZlibFactory.getZlibCompressorType(this.conf);
    }
    
    @Override
    public Compressor createCompressor() {
        return ZlibFactory.getZlibCompressor(this.conf);
    }
    
    @Override
    public CompressionInputStream createInputStream(final InputStream in) throws IOException {
        return Util.createInputStreamWithCodecPool(this, this.conf, in);
    }
    
    @Override
    public CompressionInputStream createInputStream(final InputStream in, final Decompressor decompressor) throws IOException {
        return new DecompressorStream(in, decompressor, this.conf.getInt("io.file.buffer.size", 4096));
    }
    
    @Override
    public Class<? extends Decompressor> getDecompressorType() {
        return ZlibFactory.getZlibDecompressorType(this.conf);
    }
    
    @Override
    public Decompressor createDecompressor() {
        return ZlibFactory.getZlibDecompressor(this.conf);
    }
    
    @Override
    public DirectDecompressor createDirectDecompressor() {
        return ZlibFactory.getZlibDirectDecompressor(this.conf);
    }
    
    @Override
    public String getDefaultExtension() {
        return ".deflate";
    }
    
    static {
        LOG = LoggerFactory.getLogger(DefaultCodec.class);
    }
}
