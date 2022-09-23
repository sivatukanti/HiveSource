// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.services.cache;

public interface CacheFactory
{
    CacheManager newCacheManager(final CacheableFactory p0, final String p1, final int p2, final int p3);
}
