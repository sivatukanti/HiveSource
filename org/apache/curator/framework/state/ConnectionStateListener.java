// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.state;

import org.apache.curator.framework.CuratorFramework;

public interface ConnectionStateListener
{
    void stateChanged(final CuratorFramework p0, final ConnectionState p1);
}
