// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.server;

import org.apache.zookeeper.server.quorum.Observer;
import org.apache.zookeeper.server.quorum.ObserverMXBean;

public class ObserverBean extends ZooKeeperServerBean implements ObserverMXBean
{
    private Observer observer;
    
    public ObserverBean(final Observer observer, final ZooKeeperServer zks) {
        super(zks);
        this.observer = observer;
    }
    
    @Override
    public String getName() {
        return "Observer";
    }
    
    @Override
    public int getPendingRevalidationCount() {
        return this.observer.getPendingRevalidationsCount();
    }
    
    @Override
    public String getQuorumAddress() {
        return this.observer.getSocket().toString();
    }
}
