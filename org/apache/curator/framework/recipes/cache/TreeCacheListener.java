// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.recipes.cache;

import org.apache.curator.framework.CuratorFramework;

public interface TreeCacheListener
{
    void childEvent(final CuratorFramework p0, final TreeCacheEvent p1) throws Exception;
}
