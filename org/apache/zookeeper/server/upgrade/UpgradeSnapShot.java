// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.server.upgrade;

import java.util.concurrent.ConcurrentHashMap;
import java.io.IOException;
import org.apache.zookeeper.server.DataTree;

public interface UpgradeSnapShot
{
    DataTree getNewDataTree() throws IOException;
    
    ConcurrentHashMap<Long, Integer> getSessionWithTimeOuts();
}
