// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.api;

import org.apache.zookeeper.CreateMode;

public interface CreateModable<T>
{
    T withMode(final CreateMode p0);
}
