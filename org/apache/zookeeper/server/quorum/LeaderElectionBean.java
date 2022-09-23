// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.server.quorum;

import java.util.Date;
import org.apache.zookeeper.jmx.ZKMBeanInfo;

public class LeaderElectionBean implements LeaderElectionMXBean, ZKMBeanInfo
{
    private final Date startTime;
    
    public LeaderElectionBean() {
        this.startTime = new Date();
    }
    
    @Override
    public String getName() {
        return "LeaderElection";
    }
    
    @Override
    public boolean isHidden() {
        return false;
    }
    
    @Override
    public String getStartTime() {
        return this.startTime.toString();
    }
}
