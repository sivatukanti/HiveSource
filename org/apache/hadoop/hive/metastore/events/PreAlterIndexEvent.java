// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.metastore.events;

import org.apache.hadoop.hive.metastore.HiveMetaStore;
import org.apache.hadoop.hive.metastore.api.Index;

public class PreAlterIndexEvent extends PreEventContext
{
    private final Index newIndex;
    private final Index oldIndex;
    
    public PreAlterIndexEvent(final Index oldIndex, final Index newIndex, final HiveMetaStore.HMSHandler handler) {
        super(PreEventType.ALTER_INDEX, handler);
        this.oldIndex = oldIndex;
        this.newIndex = newIndex;
    }
    
    public Index getOldIndex() {
        return this.oldIndex;
    }
    
    public Index getNewIndex() {
        return this.newIndex;
    }
}
