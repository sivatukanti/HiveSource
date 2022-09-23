// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io.compress;

import org.apache.hadoop.io.compress.lz4.Lz4Decompressor;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.hadoop.io.compress.lz4.Lz4Compressor;
import org.apache.hadoop.util.NativeCodeLoader;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configurable;

public class Lz4Codec implements Configurable, CompressionCodec
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
    
    public static boolean isNativeCodeLoaded() {
        return NativeCodeLoader.isNativeCodeLoaded();
    }
    
    public static String getLibraryName() {
        return Lz4Compressor.getLibraryName();
    }
    
    @Override
    public CompressionOutputStream createOutputStream(final OutputStream out) throws IOException {
        return Util.createOutputStreamWithCodecPool(this, this.conf, out);
    }
    
    @Override
    public CompressionOutputStream createOutputStream(final OutputStream out, final Compressor compressor) throws IOException {
        if (!isNativeCodeLoaded()) {
            throw new RuntimeException("native lz4 library not available");
        }
        final int bufferSize = this.conf.getInt("io.compression.codec.lz4.buffersize", 262144);
        final int compressionOverhead = bufferSize / 255 + 16;
        return new BlockCompressorStream(out, compressor, bufferSize, compressionOverhead);
    }
    
    @Override
    public Class<? extends Compressor> getCompressorType() {
        if (!isNativeCodeLoaded()) {
            throw new RuntimeException("native lz4 library not available");
        }
        return Lz4Compressor.class;
    }
    
    @Override
    public Compressor createCompressor() {
        if (!isNativeCodeLoaded()) {
            throw new RuntimeException("native lz4 library not available");
        }
        final int bufferSize = this.conf.getInt("io.compression.codec.lz4.buffersize", 262144);
        final boolean useLz4HC = this.conf.getBoolean("io.compression.codec.lz4.use.lz4hc", false);
        return new Lz4Compressor(bufferSize, useLz4HC);
    }
    
    @Override
    public CompressionInputStream createInputStream(final InputStream in) throws IOException {
        return Util.createInputStreamWithCodecPool(this, this.conf, in);
    }
    
    @Override
    public CompressionInputStream createInputStream(final InputStream in, final Decompressor decompressor) throws IOException {
        if (!isNativeCodeLoaded()) {
            throw new RuntimeException("native lz4 library not available");
        }
        return new BlockDecompressorStream(in, decompressor, this.conf.getInt("io.compression.codec.lz4.buffersize", 262144));
    }
    
    @Override
    public Class<? extends Decompressor> getDecompressorType() {
        if (!isNativeCodeLoaded()) {
            throw new RuntimeException("native lz4 library not available");
        }
        return Lz4Decompressor.class;
    }
    
    @Override
    public Decompressor createDecompressor() {
        if (!isNativeCodeLoaded()) {
            throw new RuntimeException("native lz4 library not available");
        }
        final int bufferSize = this.conf.getInt("io.compression.codec.lz4.buffersize", 262144);
        return new Lz4Decompressor(bufferSize);
    }
    
    @Override
    public String getDefaultExtension() {
        return ".lz4";
    }
    
    static {
        NativeCodeLoader.isNativeCodeLoaded();
    }
}
