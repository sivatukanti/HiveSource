// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.api;

import org.apache.zookeeper.data.Stat;

public interface Statable<T>
{
    T storingStatIn(final Stat p0);
}
