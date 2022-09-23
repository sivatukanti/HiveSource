// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.server;

import org.apache.zookeeper.jmx.ZKMBeanInfo;

public class DataTreeBean implements DataTreeMXBean, ZKMBeanInfo
{
    DataTree dataTree;
    
    public DataTreeBean(final DataTree dataTree) {
        this.dataTree = dataTree;
    }
    
    @Override
    public int getNodeCount() {
        return this.dataTree.getNodeCount();
    }
    
    @Override
    public long approximateDataSize() {
        return this.dataTree.approximateDataSize();
    }
    
    @Override
    public int countEphemerals() {
        return this.dataTree.getEphemeralsCount();
    }
    
    @Override
    public int getWatchCount() {
        return this.dataTree.getWatchCount();
    }
    
    @Override
    public String getName() {
        return "InMemoryDataTree";
    }
    
    @Override
    public boolean isHidden() {
        return false;
    }
    
    @Override
    public String getLastZxid() {
        return "0x" + Long.toHexString(this.dataTree.lastProcessedZxid);
    }
}
