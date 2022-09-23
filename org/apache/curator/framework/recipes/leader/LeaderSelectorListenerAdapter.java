// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.recipes.leader;

import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.CuratorFramework;

public abstract class LeaderSelectorListenerAdapter implements LeaderSelectorListener
{
    @Override
    public void stateChanged(final CuratorFramework client, final ConnectionState newState) {
        if (newState == ConnectionState.SUSPENDED || newState == ConnectionState.LOST) {
            throw new CancelLeadershipException();
        }
    }
}
