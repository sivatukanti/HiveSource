// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io.compress.zlib;

import org.slf4j.LoggerFactory;
import org.apache.hadoop.io.compress.DirectDecompressor;
import org.apache.hadoop.io.compress.Decompressor;
import org.apache.hadoop.io.compress.Compressor;
import org.apache.hadoop.conf.Configuration;
import com.google.common.annotations.VisibleForTesting;
import org.apache.hadoop.util.NativeCodeLoader;
import org.slf4j.Logger;

public class ZlibFactory
{
    private static final Logger LOG;
    private static boolean nativeZlibLoaded;
    
    @VisibleForTesting
    public static void loadNativeZLib() {
        if (NativeCodeLoader.isNativeCodeLoaded()) {
            ZlibFactory.nativeZlibLoaded = (ZlibCompressor.isNativeZlibLoaded() && ZlibDecompressor.isNativeZlibLoaded());
            if (ZlibFactory.nativeZlibLoaded) {
                ZlibFactory.LOG.info("Successfully loaded & initialized native-zlib library");
            }
            else {
                ZlibFactory.LOG.warn("Failed to load/initialize native-zlib library");
            }
        }
    }
    
    @VisibleForTesting
    public static void setNativeZlibLoaded(final boolean isLoaded) {
        ZlibFactory.nativeZlibLoaded = isLoaded;
    }
    
    public static boolean isNativeZlibLoaded(final Configuration conf) {
        return ZlibFactory.nativeZlibLoaded;
    }
    
    public static String getLibraryName() {
        return ZlibCompressor.getLibraryName();
    }
    
    public static Class<? extends Compressor> getZlibCompressorType(final Configuration conf) {
        return (Class<? extends Compressor>)(isNativeZlibLoaded(conf) ? ZlibCompressor.class : BuiltInZlibDeflater.class);
    }
    
    public static Compressor getZlibCompressor(final Configuration conf) {
        return isNativeZlibLoaded(conf) ? new ZlibCompressor(conf) : new BuiltInZlibDeflater(getCompressionLevel(conf).compressionLevel());
    }
    
    public static Class<? extends Decompressor> getZlibDecompressorType(final Configuration conf) {
        return (Class<? extends Decompressor>)(isNativeZlibLoaded(conf) ? ZlibDecompressor.class : BuiltInZlibInflater.class);
    }
    
    public static Decompressor getZlibDecompressor(final Configuration conf) {
        return isNativeZlibLoaded(conf) ? new ZlibDecompressor() : new BuiltInZlibInflater();
    }
    
    public static DirectDecompressor getZlibDirectDecompressor(final Configuration conf) {
        return isNativeZlibLoaded(conf) ? new ZlibDecompressor.ZlibDirectDecompressor() : null;
    }
    
    public static void setCompressionStrategy(final Configuration conf, final ZlibCompressor.CompressionStrategy strategy) {
        conf.setEnum("zlib.compress.strategy", strategy);
    }
    
    public static ZlibCompressor.CompressionStrategy getCompressionStrategy(final Configuration conf) {
        return conf.getEnum("zlib.compress.strategy", ZlibCompressor.CompressionStrategy.DEFAULT_STRATEGY);
    }
    
    public static void setCompressionLevel(final Configuration conf, final ZlibCompressor.CompressionLevel level) {
        conf.setEnum("zlib.compress.level", level);
    }
    
    public static ZlibCompressor.CompressionLevel getCompressionLevel(final Configuration conf) {
        return conf.getEnum("zlib.compress.level", ZlibCompressor.CompressionLevel.DEFAULT_COMPRESSION);
    }
    
    static {
        LOG = LoggerFactory.getLogger(ZlibFactory.class);
        ZlibFactory.nativeZlibLoaded = false;
        loadNativeZLib();
    }
}
