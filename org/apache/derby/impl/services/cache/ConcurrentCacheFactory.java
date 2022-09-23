// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.services.cache;

import org.apache.derby.iapi.services.cache.CacheManager;
import org.apache.derby.iapi.services.cache.CacheableFactory;
import org.apache.derby.iapi.services.cache.CacheFactory;

public class ConcurrentCacheFactory implements CacheFactory
{
    public CacheManager newCacheManager(final CacheableFactory cacheableFactory, final String s, final int n, final int n2) {
        return new ConcurrentCache(cacheableFactory, s, n, n2);
    }
}
