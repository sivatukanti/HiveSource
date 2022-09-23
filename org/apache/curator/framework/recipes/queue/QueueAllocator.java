// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.recipes.queue;

import org.apache.curator.framework.CuratorFramework;

public interface QueueAllocator<U, T extends QueueBase<U>>
{
    T allocateQueue(final CuratorFramework p0, final String p1);
}
