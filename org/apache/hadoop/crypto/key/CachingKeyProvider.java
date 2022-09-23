// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.crypto.key;

import com.google.common.cache.CacheLoader;
import java.util.concurrent.TimeUnit;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutionException;
import java.io.IOException;

public class CachingKeyProvider extends KeyProviderExtension<CacheExtension>
{
    public CachingKeyProvider(final KeyProvider keyProvider, final long keyTimeoutMillis, final long currKeyTimeoutMillis) {
        super(keyProvider, new CacheExtension(keyProvider, keyTimeoutMillis, currKeyTimeoutMillis));
    }
    
    @Override
    public KeyVersion getCurrentKey(final String name) throws IOException {
        try {
            return this.getExtension().currentKeyCache.get(name);
        }
        catch (ExecutionException ex) {
            final Throwable cause = ex.getCause();
            if (cause instanceof KeyNotFoundException) {
                return null;
            }
            if (cause instanceof IOException) {
                throw (IOException)cause;
            }
            throw new IOException(cause);
        }
    }
    
    @Override
    public KeyVersion getKeyVersion(final String versionName) throws IOException {
        try {
            return this.getExtension().keyVersionCache.get(versionName);
        }
        catch (ExecutionException ex) {
            final Throwable cause = ex.getCause();
            if (cause instanceof KeyNotFoundException) {
                return null;
            }
            if (cause instanceof IOException) {
                throw (IOException)cause;
            }
            throw new IOException(cause);
        }
    }
    
    @Override
    public void deleteKey(final String name) throws IOException {
        this.getKeyProvider().deleteKey(name);
        this.getExtension().currentKeyCache.invalidate(name);
        this.getExtension().keyMetadataCache.invalidate(name);
        this.getExtension().keyVersionCache.invalidateAll();
    }
    
    @Override
    public KeyVersion rollNewVersion(final String name, final byte[] material) throws IOException {
        final KeyVersion key = this.getKeyProvider().rollNewVersion(name, material);
        this.invalidateCache(name);
        return key;
    }
    
    @Override
    public KeyVersion rollNewVersion(final String name) throws NoSuchAlgorithmException, IOException {
        final KeyVersion key = this.getKeyProvider().rollNewVersion(name);
        this.invalidateCache(name);
        return key;
    }
    
    @Override
    public void invalidateCache(final String name) throws IOException {
        this.getKeyProvider().invalidateCache(name);
        this.getExtension().currentKeyCache.invalidate(name);
        this.getExtension().keyMetadataCache.invalidate(name);
        this.getExtension().keyVersionCache.invalidateAll();
    }
    
    @Override
    public Metadata getMetadata(final String name) throws IOException {
        try {
            return this.getExtension().keyMetadataCache.get(name);
        }
        catch (ExecutionException ex) {
            final Throwable cause = ex.getCause();
            if (cause instanceof KeyNotFoundException) {
                return null;
            }
            if (cause instanceof IOException) {
                throw (IOException)cause;
            }
            throw new IOException(cause);
        }
    }
    
    static class CacheExtension implements Extension
    {
        private final KeyProvider provider;
        private LoadingCache<String, KeyVersion> keyVersionCache;
        private LoadingCache<String, KeyVersion> currentKeyCache;
        private LoadingCache<String, Metadata> keyMetadataCache;
        
        CacheExtension(final KeyProvider prov, final long keyTimeoutMillis, final long currKeyTimeoutMillis) {
            this.provider = prov;
            this.keyVersionCache = CacheBuilder.newBuilder().expireAfterAccess(keyTimeoutMillis, TimeUnit.MILLISECONDS).build((CacheLoader<? super String, KeyVersion>)new CacheLoader<String, KeyVersion>() {
                @Override
                public KeyVersion load(final String key) throws Exception {
                    final KeyVersion kv = CacheExtension.this.provider.getKeyVersion(key);
                    if (kv == null) {
                        throw new KeyNotFoundException();
                    }
                    return kv;
                }
            });
            this.keyMetadataCache = CacheBuilder.newBuilder().expireAfterAccess(keyTimeoutMillis, TimeUnit.MILLISECONDS).build((CacheLoader<? super String, Metadata>)new CacheLoader<String, Metadata>() {
                @Override
                public Metadata load(final String key) throws Exception {
                    final Metadata meta = CacheExtension.this.provider.getMetadata(key);
                    if (meta == null) {
                        throw new KeyNotFoundException();
                    }
                    return meta;
                }
            });
            this.currentKeyCache = CacheBuilder.newBuilder().expireAfterWrite(currKeyTimeoutMillis, TimeUnit.MILLISECONDS).build((CacheLoader<? super String, KeyVersion>)new CacheLoader<String, KeyVersion>() {
                @Override
                public KeyVersion load(final String key) throws Exception {
                    final KeyVersion kv = CacheExtension.this.provider.getCurrentKey(key);
                    if (kv == null) {
                        throw new KeyNotFoundException();
                    }
                    return kv;
                }
            });
        }
    }
    
    private static class KeyNotFoundException extends Exception
    {
    }
}
