// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose.util;

import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public abstract class AbstractRestrictedResourceRetriever implements RestrictedResourceRetriever
{
    private int connectTimeout;
    private int readTimeout;
    private int sizeLimit;
    
    public AbstractRestrictedResourceRetriever(final int connectTimeout, final int readTimeout, final int sizeLimit) {
        this.setConnectTimeout(connectTimeout);
        this.setReadTimeout(readTimeout);
        this.setSizeLimit(sizeLimit);
    }
    
    @Override
    public int getConnectTimeout() {
        return this.connectTimeout;
    }
    
    @Override
    public void setConnectTimeout(final int connectTimeoutMs) {
        if (connectTimeoutMs < 0) {
            throw new IllegalArgumentException("The connect timeout must not be negative");
        }
        this.connectTimeout = connectTimeoutMs;
    }
    
    @Override
    public int getReadTimeout() {
        return this.readTimeout;
    }
    
    @Override
    public void setReadTimeout(final int readTimeoutMs) {
        if (readTimeoutMs < 0) {
            throw new IllegalArgumentException("The read timeout must not be negative");
        }
        this.readTimeout = readTimeoutMs;
    }
    
    @Override
    public int getSizeLimit() {
        return this.sizeLimit;
    }
    
    @Override
    public void setSizeLimit(final int sizeLimitBytes) {
        if (sizeLimitBytes < 0) {
            throw new IllegalArgumentException("The size limit must not be negative");
        }
        this.sizeLimit = sizeLimitBytes;
    }
}
