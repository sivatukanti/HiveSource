// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io.compress;

import java.util.HashMap;
import org.slf4j.LoggerFactory;
import java.lang.annotation.Annotation;
import org.apache.hadoop.conf.Configuration;
import java.util.HashSet;
import org.apache.hadoop.util.ReflectionUtils;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.CacheBuilder;
import java.util.concurrent.atomic.AtomicInteger;
import com.google.common.cache.LoadingCache;
import java.util.Set;
import java.util.Map;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public class CodecPool
{
    private static final Logger LOG;
    private static final Map<Class<Compressor>, Set<Compressor>> compressorPool;
    private static final Map<Class<Decompressor>, Set<Decompressor>> decompressorPool;
    private static final LoadingCache<Class<Compressor>, AtomicInteger> compressorCounts;
    private static final LoadingCache<Class<Decompressor>, AtomicInteger> decompressorCounts;
    
    private static <T> LoadingCache<Class<T>, AtomicInteger> createCache(final Class<T> klass) {
        return CacheBuilder.newBuilder().build((CacheLoader<? super Class<T>, AtomicInteger>)new CacheLoader<Class<T>, AtomicInteger>() {
            @Override
            public AtomicInteger load(final Class<T> key) throws Exception {
                return new AtomicInteger();
            }
        });
    }
    
    private static <T> T borrow(final Map<Class<T>, Set<T>> pool, final Class<? extends T> codecClass) {
        T codec = null;
        final Set<T> codecSet;
        synchronized (pool) {
            codecSet = pool.get(codecClass);
        }
        if (codecSet != null) {
            synchronized (codecSet) {
                if (!codecSet.isEmpty()) {
                    codec = codecSet.iterator().next();
                    codecSet.remove(codec);
                }
            }
        }
        return codec;
    }
    
    private static <T> boolean payback(final Map<Class<T>, Set<T>> pool, final T codec) {
        if (codec != null) {
            final Class<T> codecClass = ReflectionUtils.getClass(codec);
            Set<T> codecSet;
            synchronized (pool) {
                codecSet = pool.get(codecClass);
                if (codecSet == null) {
                    codecSet = new HashSet<T>();
                    pool.put(codecClass, codecSet);
                }
            }
            synchronized (codecSet) {
                return codecSet.add(codec);
            }
        }
        return false;
    }
    
    private static <T> int getLeaseCount(final LoadingCache<Class<T>, AtomicInteger> usageCounts, final Class<? extends T> codecClass) {
        return usageCounts.getUnchecked((Class<T>)codecClass).get();
    }
    
    private static <T> void updateLeaseCount(final LoadingCache<Class<T>, AtomicInteger> usageCounts, final T codec, final int delta) {
        if (codec != null) {
            final Class<T> codecClass = ReflectionUtils.getClass(codec);
            usageCounts.getUnchecked(codecClass).addAndGet(delta);
        }
    }
    
    public static Compressor getCompressor(final CompressionCodec codec, final Configuration conf) {
        Compressor compressor = borrow(CodecPool.compressorPool, codec.getCompressorType());
        if (compressor == null) {
            compressor = codec.createCompressor();
            CodecPool.LOG.info("Got brand-new compressor [" + codec.getDefaultExtension() + "]");
        }
        else {
            compressor.reinit(conf);
            if (CodecPool.LOG.isDebugEnabled()) {
                CodecPool.LOG.debug("Got recycled compressor");
            }
        }
        if (compressor != null && !compressor.getClass().isAnnotationPresent(DoNotPool.class)) {
            updateLeaseCount(CodecPool.compressorCounts, compressor, 1);
        }
        return compressor;
    }
    
    public static Compressor getCompressor(final CompressionCodec codec) {
        return getCompressor(codec, null);
    }
    
    public static Decompressor getDecompressor(final CompressionCodec codec) {
        Decompressor decompressor = borrow(CodecPool.decompressorPool, codec.getDecompressorType());
        if (decompressor == null) {
            decompressor = codec.createDecompressor();
            CodecPool.LOG.info("Got brand-new decompressor [" + codec.getDefaultExtension() + "]");
        }
        else if (CodecPool.LOG.isDebugEnabled()) {
            CodecPool.LOG.debug("Got recycled decompressor");
        }
        if (decompressor != null && !decompressor.getClass().isAnnotationPresent(DoNotPool.class)) {
            updateLeaseCount(CodecPool.decompressorCounts, decompressor, 1);
        }
        return decompressor;
    }
    
    public static void returnCompressor(final Compressor compressor) {
        if (compressor == null) {
            return;
        }
        if (compressor.getClass().isAnnotationPresent(DoNotPool.class)) {
            return;
        }
        compressor.reset();
        if (payback(CodecPool.compressorPool, compressor)) {
            updateLeaseCount(CodecPool.compressorCounts, compressor, -1);
        }
    }
    
    public static void returnDecompressor(final Decompressor decompressor) {
        if (decompressor == null) {
            return;
        }
        if (decompressor.getClass().isAnnotationPresent(DoNotPool.class)) {
            return;
        }
        decompressor.reset();
        if (payback(CodecPool.decompressorPool, decompressor)) {
            updateLeaseCount(CodecPool.decompressorCounts, decompressor, -1);
        }
    }
    
    public static int getLeasedCompressorsCount(final CompressionCodec codec) {
        return (codec == null) ? 0 : getLeaseCount(CodecPool.compressorCounts, codec.getCompressorType());
    }
    
    public static int getLeasedDecompressorsCount(final CompressionCodec codec) {
        return (codec == null) ? 0 : getLeaseCount(CodecPool.decompressorCounts, codec.getDecompressorType());
    }
    
    static {
        LOG = LoggerFactory.getLogger(CodecPool.class);
        compressorPool = new HashMap<Class<Compressor>, Set<Compressor>>();
        decompressorPool = new HashMap<Class<Decompressor>, Set<Decompressor>>();
        compressorCounts = createCache(Compressor.class);
        decompressorCounts = createCache(Decompressor.class);
    }
}
