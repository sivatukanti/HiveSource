// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io.compress;

import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.hadoop.io.compress.zstd.ZStandardDecompressor;
import org.apache.hadoop.io.compress.zstd.ZStandardCompressor;
import org.apache.hadoop.util.NativeCodeLoader;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configurable;

public class ZStandardCodec implements Configurable, CompressionCodec, DirectDecompressionCodec
{
    private Configuration conf;
    
    @Override
    public void setConf(final Configuration conf) {
        this.conf = conf;
    }
    
    @Override
    public Configuration getConf() {
        return this.conf;
    }
    
    public static void checkNativeCodeLoaded() {
        if (!NativeCodeLoader.isNativeCodeLoaded() || !NativeCodeLoader.buildSupportsZstd()) {
            throw new RuntimeException("native zStandard library not available: this version of libhadoop was built without zstd support.");
        }
        if (!ZStandardCompressor.isNativeCodeLoaded()) {
            throw new RuntimeException("native zStandard library not available: ZStandardCompressor has not been loaded.");
        }
        if (!ZStandardDecompressor.isNativeCodeLoaded()) {
            throw new RuntimeException("native zStandard library not available: ZStandardDecompressor has not been loaded.");
        }
    }
    
    public static boolean isNativeCodeLoaded() {
        return ZStandardCompressor.isNativeCodeLoaded() && ZStandardDecompressor.isNativeCodeLoaded();
    }
    
    public static String getLibraryName() {
        return ZStandardCompressor.getLibraryName();
    }
    
    public static int getCompressionLevel(final Configuration conf) {
        return conf.getInt("io.compression.codec.zstd.level", 3);
    }
    
    public static int getCompressionBufferSize(final Configuration conf) {
        final int bufferSize = getBufferSize(conf);
        return (bufferSize == 0) ? ZStandardCompressor.getRecommendedBufferSize() : bufferSize;
    }
    
    public static int getDecompressionBufferSize(final Configuration conf) {
        final int bufferSize = getBufferSize(conf);
        return (bufferSize == 0) ? ZStandardDecompressor.getRecommendedBufferSize() : bufferSize;
    }
    
    private static int getBufferSize(final Configuration conf) {
        return conf.getInt("io.compression.codec.zstd.buffersize", 0);
    }
    
    @Override
    public CompressionOutputStream createOutputStream(final OutputStream out) throws IOException {
        return Util.createOutputStreamWithCodecPool(this, this.conf, out);
    }
    
    @Override
    public CompressionOutputStream createOutputStream(final OutputStream out, final Compressor compressor) throws IOException {
        checkNativeCodeLoaded();
        return new CompressorStream(out, compressor, getCompressionBufferSize(this.conf));
    }
    
    @Override
    public Class<? extends Compressor> getCompressorType() {
        checkNativeCodeLoaded();
        return ZStandardCompressor.class;
    }
    
    @Override
    public Compressor createCompressor() {
        checkNativeCodeLoaded();
        return new ZStandardCompressor(getCompressionLevel(this.conf), getCompressionBufferSize(this.conf));
    }
    
    @Override
    public CompressionInputStream createInputStream(final InputStream in) throws IOException {
        return Util.createInputStreamWithCodecPool(this, this.conf, in);
    }
    
    @Override
    public CompressionInputStream createInputStream(final InputStream in, final Decompressor decompressor) throws IOException {
        checkNativeCodeLoaded();
        return new DecompressorStream(in, decompressor, getDecompressionBufferSize(this.conf));
    }
    
    @Override
    public Class<? extends Decompressor> getDecompressorType() {
        checkNativeCodeLoaded();
        return ZStandardDecompressor.class;
    }
    
    @Override
    public Decompressor createDecompressor() {
        checkNativeCodeLoaded();
        return new ZStandardDecompressor(getDecompressionBufferSize(this.conf));
    }
    
    @Override
    public String getDefaultExtension() {
        return ".zst";
    }
    
    @Override
    public DirectDecompressor createDirectDecompressor() {
        return new ZStandardDecompressor.ZStandardDirectDecompressor(getDecompressionBufferSize(this.conf));
    }
}
