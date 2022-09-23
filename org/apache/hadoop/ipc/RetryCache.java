// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.ipc;

import java.util.UUID;
import org.slf4j.LoggerFactory;
import com.google.common.base.Preconditions;
import com.google.common.annotations.VisibleForTesting;
import java.util.Arrays;
import org.apache.hadoop.util.LightWeightCache;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.hadoop.util.LightWeightGSet;
import org.apache.hadoop.ipc.metrics.RetryCacheMetrics;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
public class RetryCache
{
    public static final Logger LOG;
    private final RetryCacheMetrics retryCacheMetrics;
    private static final int MAX_CAPACITY = 16;
    private final LightWeightGSet<CacheEntry, CacheEntry> set;
    private final long expirationTime;
    private String cacheName;
    private final ReentrantLock lock;
    
    public RetryCache(final String cacheName, final double percentage, final long expirationTime) {
        this.lock = new ReentrantLock();
        int capacity = LightWeightGSet.computeCapacity(percentage, cacheName);
        capacity = ((capacity > 16) ? capacity : 16);
        this.set = new LightWeightCache<CacheEntry, CacheEntry>(capacity, capacity, expirationTime, 0L);
        this.expirationTime = expirationTime;
        this.cacheName = cacheName;
        this.retryCacheMetrics = RetryCacheMetrics.create(this);
    }
    
    private static boolean skipRetryCache() {
        return !Server.isRpcInvocation() || Server.getCallId() < 0 || Arrays.equals(Server.getClientId(), RpcConstants.DUMMY_CLIENT_ID);
    }
    
    public void lock() {
        this.lock.lock();
    }
    
    public void unlock() {
        this.lock.unlock();
    }
    
    private void incrCacheClearedCounter() {
        this.retryCacheMetrics.incrCacheCleared();
    }
    
    @VisibleForTesting
    public LightWeightGSet<CacheEntry, CacheEntry> getCacheSet() {
        return this.set;
    }
    
    @VisibleForTesting
    public RetryCacheMetrics getMetricsForTests() {
        return this.retryCacheMetrics;
    }
    
    public String getCacheName() {
        return this.cacheName;
    }
    
    private CacheEntry waitForCompletion(final CacheEntry newEntry) {
        CacheEntry mapEntry = null;
        this.lock.lock();
        try {
            mapEntry = this.set.get(newEntry);
            if (mapEntry == null) {
                if (RetryCache.LOG.isTraceEnabled()) {
                    RetryCache.LOG.trace("Adding Rpc request clientId " + newEntry.clientIdMsb + newEntry.clientIdLsb + " callId " + newEntry.callId + " to retryCache");
                }
                this.set.put(newEntry);
                this.retryCacheMetrics.incrCacheUpdated();
                return newEntry;
            }
            this.retryCacheMetrics.incrCacheHit();
        }
        finally {
            this.lock.unlock();
        }
        Preconditions.checkNotNull(mapEntry, (Object)"Entry from the cache should not be null");
        synchronized (mapEntry) {
            while (mapEntry.state == CacheEntry.INPROGRESS) {
                try {
                    mapEntry.wait();
                }
                catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
            if (mapEntry.state != CacheEntry.SUCCESS) {
                mapEntry.state = CacheEntry.INPROGRESS;
            }
        }
        return mapEntry;
    }
    
    public void addCacheEntry(final byte[] clientId, final int callId) {
        final CacheEntry newEntry = new CacheEntry(clientId, callId, System.nanoTime() + this.expirationTime, true);
        this.lock.lock();
        try {
            this.set.put(newEntry);
        }
        finally {
            this.lock.unlock();
        }
        this.retryCacheMetrics.incrCacheUpdated();
    }
    
    public void addCacheEntryWithPayload(final byte[] clientId, final int callId, final Object payload) {
        final CacheEntry newEntry = new CacheEntryWithPayload(clientId, callId, payload, System.nanoTime() + this.expirationTime, true);
        this.lock.lock();
        try {
            this.set.put(newEntry);
        }
        finally {
            this.lock.unlock();
        }
        this.retryCacheMetrics.incrCacheUpdated();
    }
    
    private static CacheEntry newEntry(final long expirationTime) {
        return new CacheEntry(Server.getClientId(), Server.getCallId(), System.nanoTime() + expirationTime);
    }
    
    private static CacheEntryWithPayload newEntry(final Object payload, final long expirationTime) {
        return new CacheEntryWithPayload(Server.getClientId(), Server.getCallId(), payload, System.nanoTime() + expirationTime);
    }
    
    public static CacheEntry waitForCompletion(final RetryCache cache) {
        if (skipRetryCache()) {
            return null;
        }
        return (cache != null) ? cache.waitForCompletion(newEntry(cache.expirationTime)) : null;
    }
    
    public static CacheEntryWithPayload waitForCompletion(final RetryCache cache, final Object payload) {
        if (skipRetryCache()) {
            return null;
        }
        return (CacheEntryWithPayload)((cache != null) ? cache.waitForCompletion(newEntry(payload, cache.expirationTime)) : null);
    }
    
    public static void setState(final CacheEntry e, final boolean success) {
        if (e == null) {
            return;
        }
        e.completed(success);
    }
    
    public static void setState(final CacheEntryWithPayload e, final boolean success, final Object payload) {
        if (e == null) {
            return;
        }
        e.payload = payload;
        e.completed(success);
    }
    
    public static void clear(final RetryCache cache) {
        if (cache != null) {
            cache.set.clear();
            cache.incrCacheClearedCounter();
        }
    }
    
    static {
        LOG = LoggerFactory.getLogger(RetryCache.class);
    }
    
    public static class CacheEntry implements LightWeightCache.Entry
    {
        private static byte INPROGRESS;
        private static byte SUCCESS;
        private static byte FAILED;
        private byte state;
        private final long clientIdMsb;
        private final long clientIdLsb;
        private final int callId;
        private final long expirationTime;
        private LightWeightGSet.LinkedElement next;
        
        CacheEntry(final byte[] clientId, final int callId, final long expirationTime) {
            this.state = CacheEntry.INPROGRESS;
            Preconditions.checkArgument(clientId.length == 16, (Object)("Invalid clientId - length is " + clientId.length + " expected length " + 16));
            this.clientIdMsb = ClientId.getMsb(clientId);
            this.clientIdLsb = ClientId.getLsb(clientId);
            this.callId = callId;
            this.expirationTime = expirationTime;
        }
        
        CacheEntry(final byte[] clientId, final int callId, final long expirationTime, final boolean success) {
            this(clientId, callId, expirationTime);
            this.state = (success ? CacheEntry.SUCCESS : CacheEntry.FAILED);
        }
        
        private static int hashCode(final long value) {
            return (int)(value ^ value >>> 32);
        }
        
        @Override
        public int hashCode() {
            return (hashCode(this.clientIdMsb) * 31 + hashCode(this.clientIdLsb)) * 31 + this.callId;
        }
        
        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof CacheEntry)) {
                return false;
            }
            final CacheEntry other = (CacheEntry)obj;
            return this.callId == other.callId && this.clientIdMsb == other.clientIdMsb && this.clientIdLsb == other.clientIdLsb;
        }
        
        @Override
        public void setNext(final LightWeightGSet.LinkedElement next) {
            this.next = next;
        }
        
        @Override
        public LightWeightGSet.LinkedElement getNext() {
            return this.next;
        }
        
        synchronized void completed(final boolean success) {
            this.state = (success ? CacheEntry.SUCCESS : CacheEntry.FAILED);
            this.notifyAll();
        }
        
        public synchronized boolean isSuccess() {
            return this.state == CacheEntry.SUCCESS;
        }
        
        @Override
        public void setExpirationTime(final long timeNano) {
        }
        
        @Override
        public long getExpirationTime() {
            return this.expirationTime;
        }
        
        @Override
        public String toString() {
            return new UUID(this.clientIdMsb, this.clientIdLsb).toString() + ":" + this.callId + ":" + this.state;
        }
        
        static {
            CacheEntry.INPROGRESS = 0;
            CacheEntry.SUCCESS = 1;
            CacheEntry.FAILED = 2;
        }
    }
    
    public static class CacheEntryWithPayload extends CacheEntry
    {
        private Object payload;
        
        CacheEntryWithPayload(final byte[] clientId, final int callId, final Object payload, final long expirationTime) {
            super(clientId, callId, expirationTime);
            this.payload = payload;
        }
        
        CacheEntryWithPayload(final byte[] clientId, final int callId, final Object payload, final long expirationTime, final boolean success) {
            super(clientId, callId, expirationTime, success);
            this.payload = payload;
        }
        
        @Override
        public boolean equals(final Object obj) {
            return super.equals(obj);
        }
        
        @Override
        public int hashCode() {
            return super.hashCode();
        }
        
        public Object getPayload() {
            return this.payload;
        }
    }
}
