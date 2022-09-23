// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.scheduler;

import java.util.Iterator;
import java.util.HashMap;

class TimeBucketMetrics<OBJ>
{
    private final HashMap<OBJ, Long> map;
    private final int[] counts;
    private final long[] cuts;
    
    TimeBucketMetrics(final long[] cuts) {
        this.map = new HashMap<OBJ, Long>();
        this.cuts = cuts;
        this.counts = new int[cuts.length + 1];
    }
    
    synchronized void add(final OBJ key, final long time) {
        this.map.put(key, time);
    }
    
    synchronized void remove(final OBJ key) {
        this.map.remove(key);
    }
    
    private int findBucket(final long val) {
        for (int i = 0; i < this.cuts.length; ++i) {
            if (val < this.cuts[i]) {
                return i;
            }
        }
        return this.cuts.length;
    }
    
    synchronized int[] getBucketCounts(final long now) {
        for (int i = 0; i < this.counts.length; ++i) {
            this.counts[i] = 0;
        }
        for (final Long time : this.map.values()) {
            final int[] counts = this.counts;
            final int bucket = this.findBucket(now - time);
            ++counts[bucket];
        }
        return this.counts;
    }
}
