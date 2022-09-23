// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.util;

public class CacheableIPList implements IPList
{
    private final long cacheTimeout;
    private volatile long cacheExpiryTimeStamp;
    private volatile FileBasedIPList ipList;
    
    public CacheableIPList(final FileBasedIPList ipList, final long cacheTimeout) {
        this.cacheTimeout = cacheTimeout;
        this.ipList = ipList;
        this.updateCacheExpiryTime();
    }
    
    private void reset() {
        this.ipList = this.ipList.reload();
        this.updateCacheExpiryTime();
    }
    
    private void updateCacheExpiryTime() {
        if (this.cacheTimeout < 0L) {
            this.cacheExpiryTimeStamp = -1L;
        }
        else {
            this.cacheExpiryTimeStamp = System.currentTimeMillis() + this.cacheTimeout;
        }
    }
    
    public void refresh() {
        this.cacheExpiryTimeStamp = 0L;
    }
    
    @Override
    public boolean isIn(final String ipAddress) {
        if (this.cacheExpiryTimeStamp >= 0L && this.cacheExpiryTimeStamp < System.currentTimeMillis()) {
            synchronized (this) {
                if (this.cacheExpiryTimeStamp < System.currentTimeMillis()) {
                    this.reset();
                }
            }
        }
        return this.ipList.isIn(ipAddress);
    }
}
