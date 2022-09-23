// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.ipc.metrics;

import org.slf4j.LoggerFactory;
import org.apache.hadoop.metrics2.lib.DefaultMetricsSystem;
import org.apache.hadoop.ipc.RetryCache;
import org.apache.hadoop.metrics2.annotation.Metric;
import org.apache.hadoop.metrics2.lib.MutableCounterLong;
import org.apache.hadoop.metrics2.lib.MetricsRegistry;
import org.slf4j.Logger;
import org.apache.hadoop.metrics2.annotation.Metrics;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@Metrics(about = "Aggregate RetryCache metrics", context = "rpc")
public class RetryCacheMetrics
{
    static final Logger LOG;
    final MetricsRegistry registry;
    final String name;
    @Metric({ "Number of RetryCache hit" })
    MutableCounterLong cacheHit;
    @Metric({ "Number of RetryCache cleared" })
    MutableCounterLong cacheCleared;
    @Metric({ "Number of RetryCache updated" })
    MutableCounterLong cacheUpdated;
    
    RetryCacheMetrics(final RetryCache retryCache) {
        this.name = "RetryCache." + retryCache.getCacheName();
        this.registry = new MetricsRegistry(this.name);
        if (RetryCacheMetrics.LOG.isDebugEnabled()) {
            RetryCacheMetrics.LOG.debug("Initialized " + this.registry);
        }
    }
    
    public String getName() {
        return this.name;
    }
    
    public static RetryCacheMetrics create(final RetryCache cache) {
        final RetryCacheMetrics m = new RetryCacheMetrics(cache);
        return DefaultMetricsSystem.instance().register(m.name, (String)null, m);
    }
    
    public void incrCacheHit() {
        this.cacheHit.incr();
    }
    
    public void incrCacheCleared() {
        this.cacheCleared.incr();
    }
    
    public void incrCacheUpdated() {
        this.cacheUpdated.incr();
    }
    
    public long getCacheHit() {
        return this.cacheHit.value();
    }
    
    public long getCacheCleared() {
        return this.cacheCleared.value();
    }
    
    public long getCacheUpdated() {
        return this.cacheUpdated.value();
    }
    
    static {
        LOG = LoggerFactory.getLogger(RetryCacheMetrics.class);
    }
}
