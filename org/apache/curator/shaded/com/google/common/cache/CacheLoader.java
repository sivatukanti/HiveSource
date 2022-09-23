// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.shaded.com.google.common.cache;

import java.io.Serializable;
import org.apache.curator.shaded.com.google.common.base.Supplier;
import org.apache.curator.shaded.com.google.common.annotations.Beta;
import com.google.common.base.Function;
import java.util.Map;
import org.apache.curator.shaded.com.google.common.annotations.GwtIncompatible;
import org.apache.curator.shaded.com.google.common.util.concurrent.Futures;
import org.apache.curator.shaded.com.google.common.base.Preconditions;
import org.apache.curator.shaded.com.google.common.util.concurrent.ListenableFuture;
import org.apache.curator.shaded.com.google.common.annotations.GwtCompatible;

@GwtCompatible(emulated = true)
public abstract class CacheLoader<K, V>
{
    protected CacheLoader() {
    }
    
    public abstract V load(final K p0) throws Exception;
    
    @GwtIncompatible("Futures")
    public ListenableFuture<V> reload(final K key, final V oldValue) throws Exception {
        Preconditions.checkNotNull(key);
        Preconditions.checkNotNull(oldValue);
        return Futures.immediateFuture(this.load(key));
    }
    
    public Map<K, V> loadAll(final Iterable<? extends K> keys) throws Exception {
        throw new UnsupportedLoadingOperationException();
    }
    
    @Beta
    public static <K, V> CacheLoader<K, V> from(final Function<K, V> function) {
        return new FunctionToCacheLoader<K, V>(function);
    }
    
    @Beta
    public static <V> CacheLoader<Object, V> from(final Supplier<V> supplier) {
        return (CacheLoader<Object, V>)new SupplierToCacheLoader((Supplier<Object>)supplier);
    }
    
    private static final class FunctionToCacheLoader<K, V> extends CacheLoader<K, V> implements Serializable
    {
        private final Function<K, V> computingFunction;
        private static final long serialVersionUID = 0L;
        
        public FunctionToCacheLoader(final Function<K, V> computingFunction) {
            this.computingFunction = Preconditions.checkNotNull(computingFunction);
        }
        
        @Override
        public V load(final K key) {
            return this.computingFunction.apply(Preconditions.checkNotNull(key));
        }
    }
    
    private static final class SupplierToCacheLoader<V> extends CacheLoader<Object, V> implements Serializable
    {
        private final Supplier<V> computingSupplier;
        private static final long serialVersionUID = 0L;
        
        public SupplierToCacheLoader(final Supplier<V> computingSupplier) {
            this.computingSupplier = Preconditions.checkNotNull(computingSupplier);
        }
        
        @Override
        public V load(final Object key) {
            Preconditions.checkNotNull(key);
            return this.computingSupplier.get();
        }
    }
    
    static final class UnsupportedLoadingOperationException extends UnsupportedOperationException
    {
    }
    
    public static final class InvalidCacheLoadException extends RuntimeException
    {
        public InvalidCacheLoadException(final String message) {
            super(message);
        }
    }
}
