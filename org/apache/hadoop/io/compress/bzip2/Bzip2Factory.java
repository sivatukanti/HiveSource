// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io.compress.bzip2;

import org.slf4j.LoggerFactory;
import org.apache.hadoop.io.compress.Decompressor;
import org.apache.hadoop.io.compress.Compressor;
import org.apache.hadoop.util.NativeCodeLoader;
import org.apache.hadoop.conf.Configuration;
import org.slf4j.Logger;

public class Bzip2Factory
{
    private static final Logger LOG;
    private static String bzip2LibraryName;
    private static boolean nativeBzip2Loaded;
    
    public static synchronized boolean isNativeBzip2Loaded(final Configuration conf) {
        final String libname = conf.get("io.compression.codec.bzip2.library", "system-native");
        if (!Bzip2Factory.bzip2LibraryName.equals(libname)) {
            Bzip2Factory.nativeBzip2Loaded = false;
            Bzip2Factory.bzip2LibraryName = libname;
            if (libname.equals("java-builtin")) {
                Bzip2Factory.LOG.info("Using pure-Java version of bzip2 library");
            }
            else if (NativeCodeLoader.isNativeCodeLoaded()) {
                try {
                    Bzip2Compressor.initSymbols(libname);
                    Bzip2Decompressor.initSymbols(libname);
                    Bzip2Factory.nativeBzip2Loaded = true;
                    Bzip2Factory.LOG.info("Successfully loaded & initialized native-bzip2 library " + libname);
                }
                catch (Throwable t) {
                    Bzip2Factory.LOG.warn("Failed to load/initialize native-bzip2 library " + libname + ", will use pure-Java version");
                }
            }
        }
        return Bzip2Factory.nativeBzip2Loaded;
    }
    
    public static String getLibraryName(final Configuration conf) {
        if (isNativeBzip2Loaded(conf)) {
            return Bzip2Compressor.getLibraryName();
        }
        return Bzip2Factory.bzip2LibraryName;
    }
    
    public static Class<? extends Compressor> getBzip2CompressorType(final Configuration conf) {
        return (Class<? extends Compressor>)(isNativeBzip2Loaded(conf) ? Bzip2Compressor.class : BZip2DummyCompressor.class);
    }
    
    public static Compressor getBzip2Compressor(final Configuration conf) {
        return isNativeBzip2Loaded(conf) ? new Bzip2Compressor(conf) : new BZip2DummyCompressor();
    }
    
    public static Class<? extends Decompressor> getBzip2DecompressorType(final Configuration conf) {
        return (Class<? extends Decompressor>)(isNativeBzip2Loaded(conf) ? Bzip2Decompressor.class : BZip2DummyDecompressor.class);
    }
    
    public static Decompressor getBzip2Decompressor(final Configuration conf) {
        return isNativeBzip2Loaded(conf) ? new Bzip2Decompressor() : new BZip2DummyDecompressor();
    }
    
    public static void setBlockSize(final Configuration conf, final int blockSize) {
        conf.setInt("bzip2.compress.blocksize", blockSize);
    }
    
    public static int getBlockSize(final Configuration conf) {
        return conf.getInt("bzip2.compress.blocksize", 9);
    }
    
    public static void setWorkFactor(final Configuration conf, final int workFactor) {
        conf.setInt("bzip2.compress.workfactor", workFactor);
    }
    
    public static int getWorkFactor(final Configuration conf) {
        return conf.getInt("bzip2.compress.workfactor", 30);
    }
    
    static {
        LOG = LoggerFactory.getLogger(Bzip2Factory.class);
        Bzip2Factory.bzip2LibraryName = "";
    }
}
