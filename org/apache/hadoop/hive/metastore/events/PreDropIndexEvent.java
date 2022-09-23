// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.metastore.events;

import org.apache.hadoop.hive.metastore.HiveMetaStore;
import org.apache.hadoop.hive.metastore.api.Index;

public class PreDropIndexEvent extends PreEventContext
{
    private final Index index;
    
    public PreDropIndexEvent(final Index index, final HiveMetaStore.HMSHandler handler) {
        super(PreEventType.DROP_INDEX, handler);
        this.index = index;
    }
    
    public Index getIndex() {
        return this.index;
    }
}
