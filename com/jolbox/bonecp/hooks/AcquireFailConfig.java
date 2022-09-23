// 
// Decompiled by Procyon v0.5.36
// 

package com.jolbox.bonecp.hooks;

import java.util.concurrent.atomic.AtomicInteger;

public class AcquireFailConfig
{
    private long acquireRetryDelayInMs;
    private AtomicInteger acquireRetryAttempts;
    private String logMessage;
    private Object debugHandle;
    
    public AcquireFailConfig() {
        this.acquireRetryAttempts = new AtomicInteger();
        this.logMessage = "";
    }
    
    @Deprecated
    public long getAcquireRetryDelay() {
        return this.getAcquireRetryDelayInMs();
    }
    
    public long getAcquireRetryDelayInMs() {
        return this.acquireRetryDelayInMs;
    }
    
    @Deprecated
    public void setAcquireRetryDelay(final long acquireRetryDelayInMs) {
        this.setAcquireRetryDelayInMs(acquireRetryDelayInMs);
    }
    
    public void setAcquireRetryDelayInMs(final long acquireRetryDelayInMs) {
        this.acquireRetryDelayInMs = acquireRetryDelayInMs;
    }
    
    public AtomicInteger getAcquireRetryAttempts() {
        return this.acquireRetryAttempts;
    }
    
    public void setAcquireRetryAttempts(final AtomicInteger acquireRetryAttempts) {
        this.acquireRetryAttempts = acquireRetryAttempts;
    }
    
    public String getLogMessage() {
        return this.logMessage;
    }
    
    public void setLogMessage(final String logMessage) {
        this.logMessage = logMessage;
    }
    
    public Object getDebugHandle() {
        return this.debugHandle;
    }
    
    public void setDebugHandle(final Object debugHandle) {
        this.debugHandle = debugHandle;
    }
}
