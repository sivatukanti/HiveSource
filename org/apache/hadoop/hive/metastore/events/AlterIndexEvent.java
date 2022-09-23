// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.metastore.events;

import org.apache.hadoop.hive.metastore.HiveMetaStore;
import org.apache.hadoop.hive.metastore.api.Index;

public class AlterIndexEvent extends ListenerEvent
{
    private final Index newIndex;
    private final Index oldIndex;
    
    public AlterIndexEvent(final Index oldIndex, final Index newIndex, final boolean status, final HiveMetaStore.HMSHandler handler) {
        super(status, handler);
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
