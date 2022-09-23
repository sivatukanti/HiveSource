// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.recipes.leader;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.state.ConnectionStateListener;

public interface LeaderSelectorListener extends ConnectionStateListener
{
    void takeLeadership(final CuratorFramework p0) throws Exception;
}
