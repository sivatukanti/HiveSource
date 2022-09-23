// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.imps;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import org.apache.curator.shaded.com.google.common.cache.CacheBuilder;
import org.apache.curator.shaded.com.google.common.cache.LoadingCache;
import org.apache.curator.shaded.com.google.common.cache.CacheLoader;

class NamespaceFacadeCache
{
    private final CuratorFrameworkImpl client;
    private final NamespaceFacade nullNamespace;
    private final CacheLoader<String, NamespaceFacade> loader;
    private final LoadingCache<String, NamespaceFacade> cache;
    
    NamespaceFacadeCache(final CuratorFrameworkImpl client) {
        this.loader = new CacheLoader<String, NamespaceFacade>() {
            @Override
            public NamespaceFacade load(final String namespace) throws Exception {
                return new NamespaceFacade(NamespaceFacadeCache.this.client, namespace);
            }
        };
        this.cache = CacheBuilder.newBuilder().expireAfterAccess(5L, TimeUnit.MINUTES).build((CacheLoader<? super String, NamespaceFacade>)this.loader);
        this.client = client;
        this.nullNamespace = new NamespaceFacade(client, null);
    }
    
    NamespaceFacade get(final String namespace) {
        try {
            return (namespace != null) ? this.cache.get(namespace) : this.nullNamespace;
        }
        catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
