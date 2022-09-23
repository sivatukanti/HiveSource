// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.api;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.data.ACL;
import java.util.List;
import org.apache.zookeeper.data.Stat;

public interface CuratorEvent
{
    CuratorEventType getType();
    
    int getResultCode();
    
    String getPath();
    
    Object getContext();
    
    Stat getStat();
    
    byte[] getData();
    
    String getName();
    
    List<String> getChildren();
    
    List<ACL> getACLList();
    
    WatchedEvent getWatchedEvent();
}
