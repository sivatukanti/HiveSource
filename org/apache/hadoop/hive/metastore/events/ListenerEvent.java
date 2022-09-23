// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.metastore.events;

import org.apache.hadoop.hive.metastore.api.EnvironmentContext;
import org.apache.hadoop.hive.metastore.HiveMetaStore;

public abstract class ListenerEvent
{
    private final boolean status;
    private final HiveMetaStore.HMSHandler handler;
    private EnvironmentContext environmentContext;
    
    public ListenerEvent(final boolean status, final HiveMetaStore.HMSHandler handler) {
        this.environmentContext = null;
        this.status = status;
        this.handler = handler;
    }
    
    public boolean getStatus() {
        return this.status;
    }
    
    public void setEnvironmentContext(final EnvironmentContext environmentContext) {
        this.environmentContext = environmentContext;
    }
    
    public EnvironmentContext getEnvironmentContext() {
        return this.environmentContext;
    }
    
    public HiveMetaStore.HMSHandler getHandler() {
        return this.handler;
    }
}
