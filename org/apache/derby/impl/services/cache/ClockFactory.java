// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.services.cache;

import org.apache.derby.iapi.services.cache.CacheManager;
import org.apache.derby.iapi.services.cache.CacheableFactory;
import org.apache.derby.iapi.services.cache.CacheFactory;

public class ClockFactory implements CacheFactory
{
    public static final String CacheTrace;
    
    public CacheManager newCacheManager(final CacheableFactory cacheableFactory, final String s, int n, final int n2) {
        if (n <= 0) {
            n = 1;
        }
        return new Clock(cacheableFactory, s, n, n2, false);
    }
    
    public CacheManager newSizedCacheManager(final CacheableFactory cacheableFactory, final String s, int n, final long n2) {
        if (n <= 0) {
            n = 1;
        }
        return new Clock(cacheableFactory, s, n, n2, true);
    }
    
    static {
        CacheTrace = null;
    }
}
