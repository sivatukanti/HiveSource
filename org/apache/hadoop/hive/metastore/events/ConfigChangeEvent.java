// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.metastore.events;

import org.apache.hadoop.hive.metastore.HiveMetaStore;

public class ConfigChangeEvent extends ListenerEvent
{
    private final String key;
    private final String oldValue;
    private final String newValue;
    
    public ConfigChangeEvent(final HiveMetaStore.HMSHandler handler, final String key, final String oldValue, final String newValue) {
        super(true, handler);
        this.key = key;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }
    
    public String getKey() {
        return this.key;
    }
    
    public String getOldValue() {
        return this.oldValue;
    }
    
    public String getNewValue() {
        return this.newValue;
    }
}
