// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.server.quorum;

import java.util.Date;
import org.apache.zookeeper.jmx.ZKMBeanInfo;

public abstract class ServerBean implements ServerMXBean, ZKMBeanInfo
{
    private final Date startTime;
    
    public ServerBean() {
        this.startTime = new Date();
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
