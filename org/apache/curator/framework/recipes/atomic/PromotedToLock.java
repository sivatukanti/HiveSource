// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.recipes.atomic;

import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.utils.PathUtils;
import org.apache.curator.shaded.com.google.common.base.Preconditions;
import org.apache.curator.RetryPolicy;
import java.util.concurrent.TimeUnit;

public class PromotedToLock
{
    private final String path;
    private final long maxLockTime;
    private final TimeUnit maxLockTimeUnit;
    private final RetryPolicy retryPolicy;
    
    public static Builder builder() {
        return new Builder();
    }
    
    String getPath() {
        return this.path;
    }
    
    long getMaxLockTime() {
        return this.maxLockTime;
    }
    
    TimeUnit getMaxLockTimeUnit() {
        return this.maxLockTimeUnit;
    }
    
    RetryPolicy getRetryPolicy() {
        return this.retryPolicy;
    }
    
    private PromotedToLock(final String path, final long maxLockTime, final TimeUnit maxLockTimeUnit, final RetryPolicy retryPolicy) {
        this.path = path;
        this.maxLockTime = maxLockTime;
        this.maxLockTimeUnit = maxLockTimeUnit;
        this.retryPolicy = retryPolicy;
    }
    
    public static class Builder
    {
        private PromotedToLock instance;
        
        public PromotedToLock build() {
            Preconditions.checkNotNull(this.instance.path, (Object)"path cannot be null");
            Preconditions.checkNotNull(this.instance.retryPolicy, (Object)"retryPolicy cannot be null");
            return new PromotedToLock(this.instance.path, this.instance.maxLockTime, this.instance.maxLockTimeUnit, this.instance.retryPolicy, null);
        }
        
        public Builder lockPath(final String path) {
            this.instance = new PromotedToLock(PathUtils.validatePath(path), this.instance.maxLockTime, this.instance.maxLockTimeUnit, this.instance.retryPolicy, null);
            return this;
        }
        
        public Builder retryPolicy(final RetryPolicy retryPolicy) {
            this.instance = new PromotedToLock(this.instance.path, this.instance.maxLockTime, this.instance.maxLockTimeUnit, retryPolicy, null);
            return this;
        }
        
        public Builder timeout(final long maxLockTime, final TimeUnit maxLockTimeUnit) {
            this.instance = new PromotedToLock(this.instance.path, maxLockTime, maxLockTimeUnit, this.instance.retryPolicy, null);
            return this;
        }
        
        private Builder() {
            this.instance = new PromotedToLock(null, -1L, null, new RetryNTimes(0, 0), null);
        }
    }
}
