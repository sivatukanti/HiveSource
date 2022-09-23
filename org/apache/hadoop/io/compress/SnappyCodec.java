// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io.compress;

import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.hadoop.io.compress.snappy.SnappyDecompressor;
import org.apache.hadoop.io.compress.snappy.SnappyCompressor;
import org.apache.hadoop.util.NativeCodeLoader;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configurable;

public class SnappyCodec implements Configurable, CompressionCodec, DirectDecompressionCodec
{
    Configuration conf;
    
    @Override
    public void setConf(final Configuration conf) {
        this.conf = conf;
    }
    
    @Override
    public Configuration getConf() {
        return this.conf;
    }
    
    public static void checkNativeCodeLoaded() {
        if (!NativeCodeLoader.buildSupportsSnappy()) {
            throw new RuntimeException("native snappy library not available: this version of libhadoop was built without snappy support.");
        }
        if (!NativeCodeLoader.isNativeCodeLoaded()) {
            throw new RuntimeException("Failed to load libhadoop.");
        }
        if (!SnappyCompressor.isNativeCodeLoaded()) {
            throw new RuntimeException("native snappy library not available: SnappyCompressor has not been loaded.");
        }
        if (!SnappyDecompressor.isNativeCodeLoaded()) {
            throw new RuntimeException("native snappy library not available: SnappyDecompressor has not been loaded.");
        }
    }
    
    public static boolean isNativeCodeLoaded() {
        return SnappyCompressor.isNativeCodeLoaded() && SnappyDecompressor.isNativeCodeLoaded();
    }
    
    public static String getLibraryName() {
        return SnappyCompressor.getLibraryName();
    }
    
    @Override
    public CompressionOutputStream createOutputStream(final OutputStream out) throws IOException {
        return Util.createOutputStreamWithCodecPool(this, this.conf, out);
    }
    
    @Override
    public CompressionOutputStream createOutputStream(final OutputStream out, final Compressor compressor) throws IOException {
        checkNativeCodeLoaded();
        final int bufferSize = this.conf.getInt("io.compression.codec.snappy.buffersize", 262144);
        final int compressionOverhead = bufferSize / 6 + 32;
        return new BlockCompressorStream(out, compressor, bufferSize, compressionOverhead);
    }
    
    @Override
    public Class<? extends Compressor> getCompressorType() {
        checkNativeCodeLoaded();
        return SnappyCompressor.class;
    }
    
    @Override
    public Compressor createCompressor() {
        checkNativeCodeLoaded();
        final int bufferSize = this.conf.getInt("io.compression.codec.snappy.buffersize", 262144);
        return new SnappyCompressor(bufferSize);
    }
    
    @Override
    public CompressionInputStream createInputStream(final InputStream in) throws IOException {
        return Util.createInputStreamWithCodecPool(this, this.conf, in);
    }
    
    @Override
    public CompressionInputStream createInputStream(final InputStream in, final Decompressor decompressor) throws IOException {
        checkNativeCodeLoaded();
        return new BlockDecompressorStream(in, decompressor, this.conf.getInt("io.compression.codec.snappy.buffersize", 262144));
    }
    
    @Override
    public Class<? extends Decompressor> getDecompressorType() {
        checkNativeCodeLoaded();
        return SnappyDecompressor.class;
    }
    
    @Override
    public Decompressor createDecompressor() {
        checkNativeCodeLoaded();
        final int bufferSize = this.conf.getInt("io.compression.codec.snappy.buffersize", 262144);
        return new SnappyDecompressor(bufferSize);
    }
    
    @Override
    public DirectDecompressor createDirectDecompressor() {
        return isNativeCodeLoaded() ? new SnappyDecompressor.SnappyDirectDecompressor() : null;
    }
    
    @Override
    public String getDefaultExtension() {
        return ".snappy";
    }
}
