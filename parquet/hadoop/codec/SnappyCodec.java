// 
// Decompiled by Procyon v0.5.36
// 

package parquet.hadoop.codec;

import org.apache.hadoop.io.compress.CompressionOutputStream;
import java.io.OutputStream;
import java.io.IOException;
import org.apache.hadoop.io.compress.CompressionInputStream;
import java.io.InputStream;
import org.apache.hadoop.io.compress.Decompressor;
import org.apache.hadoop.io.compress.Compressor;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.compress.CompressionCodec;
import org.apache.hadoop.conf.Configurable;

public class SnappyCodec implements Configurable, CompressionCodec
{
    private Configuration conf;
    private final String BUFFER_SIZE_CONFIG = "io.file.buffer.size";
    
    @Override
    public void setConf(final Configuration conf) {
        this.conf = conf;
    }
    
    @Override
    public Configuration getConf() {
        return this.conf;
    }
    
    @Override
    public Compressor createCompressor() {
        return new SnappyCompressor();
    }
    
    @Override
    public Decompressor createDecompressor() {
        return new SnappyDecompressor();
    }
    
    @Override
    public CompressionInputStream createInputStream(final InputStream stream) throws IOException {
        return this.createInputStream(stream, this.createDecompressor());
    }
    
    @Override
    public CompressionInputStream createInputStream(final InputStream stream, final Decompressor decompressor) throws IOException {
        return new NonBlockedDecompressorStream(stream, decompressor, this.conf.getInt("io.file.buffer.size", 4096));
    }
    
    @Override
    public CompressionOutputStream createOutputStream(final OutputStream stream) throws IOException {
        return this.createOutputStream(stream, this.createCompressor());
    }
    
    @Override
    public CompressionOutputStream createOutputStream(final OutputStream stream, final Compressor compressor) throws IOException {
        return new NonBlockedCompressorStream(stream, compressor, this.conf.getInt("io.file.buffer.size", 4096));
    }
    
    @Override
    public Class<? extends Compressor> getCompressorType() {
        return SnappyCompressor.class;
    }
    
    @Override
    public Class<? extends Decompressor> getDecompressorType() {
        return SnappyDecompressor.class;
    }
    
    @Override
    public String getDefaultExtension() {
        return ".snappy";
    }
}
