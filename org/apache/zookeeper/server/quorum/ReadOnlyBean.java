// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.server.quorum;

import org.apache.zookeeper.server.ZooKeeperServer;
import org.apache.zookeeper.server.ZooKeeperServerBean;

public class ReadOnlyBean extends ZooKeeperServerBean
{
    public ReadOnlyBean(final ZooKeeperServer zks) {
        super(zks);
    }
    
    @Override
    public String getName() {
        return "ReadOnlyServer";
    }
}
