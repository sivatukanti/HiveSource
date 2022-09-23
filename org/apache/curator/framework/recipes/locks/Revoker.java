// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.recipes.locks;

import org.apache.zookeeper.KeeperException;
import org.apache.curator.framework.CuratorFramework;

public class Revoker
{
    public static void attemptRevoke(final CuratorFramework client, final String path) throws Exception {
        try {
            client.setData().forPath(path, LockInternals.REVOKE_MESSAGE);
        }
        catch (KeeperException.NoNodeException ex) {}
    }
    
    private Revoker() {
    }
}
