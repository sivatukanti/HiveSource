// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.rmapp;

import org.apache.hadoop.yarn.api.records.ApplicationId;
import com.google.common.util.concurrent.SettableFuture;

public class RMAppMoveEvent extends RMAppEvent
{
    private String targetQueue;
    private SettableFuture<Object> result;
    
    public RMAppMoveEvent(final ApplicationId id, final String newQueue, final SettableFuture<Object> resultFuture) {
        super(id, RMAppEventType.MOVE);
        this.targetQueue = newQueue;
        this.result = resultFuture;
    }
    
    public String getTargetQueue() {
        return this.targetQueue;
    }
    
    public SettableFuture<Object> getResult() {
        return this.result;
    }
}
