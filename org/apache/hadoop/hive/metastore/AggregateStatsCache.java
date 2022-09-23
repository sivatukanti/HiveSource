// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.metastore;

import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import org.apache.commons.logging.LogFactory;
import java.util.ArrayList;
import org.apache.hive.common.util.BloomFilter;
import org.apache.hadoop.hive.metastore.api.ColumnStatisticsObj;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.conf.Configuration;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.logging.Log;

public class AggregateStatsCache
{
    private static final Log LOG;
    private static AggregateStatsCache self;
    private final ConcurrentHashMap<Key, AggrColStatsList> cacheStore;
    private final int maxCacheNodes;
    private AtomicInteger currentNodes;
    private final float maxFull;
    private final float cleanUntil;
    private final long timeToLiveMs;
    private final long maxWriterWaitTime;
    private final long maxReaderWaitTime;
    private final int maxPartsPerCacheNode;
    private final float falsePositiveProbability;
    private final float maxVariance;
    private boolean isCleaning;
    private AtomicLong cacheHits;
    private AtomicLong cacheMisses;
    int numRemovedTTL;
    int numRemovedLRU;
    
    private AggregateStatsCache(final int maxCacheNodes, final int maxPartsPerCacheNode, final long timeToLiveMs, final float falsePositiveProbability, final float maxVariance, final long maxWriterWaitTime, final long maxReaderWaitTime, final float maxFull, final float cleanUntil) {
        this.currentNodes = new AtomicInteger(0);
        this.isCleaning = false;
        this.cacheHits = new AtomicLong(0L);
        this.cacheMisses = new AtomicLong(0L);
        this.numRemovedTTL = 0;
        this.numRemovedLRU = 0;
        this.maxCacheNodes = maxCacheNodes;
        this.maxPartsPerCacheNode = maxPartsPerCacheNode;
        this.timeToLiveMs = timeToLiveMs;
        this.falsePositiveProbability = falsePositiveProbability;
        this.maxVariance = maxVariance;
        this.maxWriterWaitTime = maxWriterWaitTime;
        this.maxReaderWaitTime = maxReaderWaitTime;
        this.maxFull = maxFull;
        this.cleanUntil = cleanUntil;
        this.cacheStore = new ConcurrentHashMap<Key, AggrColStatsList>();
    }
    
    public static synchronized AggregateStatsCache getInstance(final Configuration conf) {
        if (AggregateStatsCache.self == null) {
            final int maxCacheNodes = HiveConf.getIntVar(conf, HiveConf.ConfVars.METASTORE_AGGREGATE_STATS_CACHE_SIZE);
            final int maxPartitionsPerCacheNode = HiveConf.getIntVar(conf, HiveConf.ConfVars.METASTORE_AGGREGATE_STATS_CACHE_MAX_PARTITIONS);
            final long timeToLiveMs = HiveConf.getTimeVar(conf, HiveConf.ConfVars.METASTORE_AGGREGATE_STATS_CACHE_TTL, TimeUnit.SECONDS) * 1000L;
            final float falsePositiveProbability = HiveConf.getFloatVar(conf, HiveConf.ConfVars.METASTORE_AGGREGATE_STATS_CACHE_FPP);
            final float maxVariance = HiveConf.getFloatVar(conf, HiveConf.ConfVars.METASTORE_AGGREGATE_STATS_CACHE_MAX_VARIANCE);
            final long maxWriterWaitTime = HiveConf.getTimeVar(conf, HiveConf.ConfVars.METASTORE_AGGREGATE_STATS_CACHE_MAX_WRITER_WAIT, TimeUnit.MILLISECONDS);
            final long maxReaderWaitTime = HiveConf.getTimeVar(conf, HiveConf.ConfVars.METASTORE_AGGREGATE_STATS_CACHE_MAX_READER_WAIT, TimeUnit.MILLISECONDS);
            final float maxFull = HiveConf.getFloatVar(conf, HiveConf.ConfVars.METASTORE_AGGREGATE_STATS_CACHE_MAX_FULL);
            final float cleanUntil = HiveConf.getFloatVar(conf, HiveConf.ConfVars.METASTORE_AGGREGATE_STATS_CACHE_CLEAN_UNTIL);
            AggregateStatsCache.self = new AggregateStatsCache(maxCacheNodes, maxPartitionsPerCacheNode, timeToLiveMs, falsePositiveProbability, maxVariance, maxWriterWaitTime, maxReaderWaitTime, maxFull, cleanUntil);
        }
        return AggregateStatsCache.self;
    }
    
    public int getMaxCacheNodes() {
        return this.maxCacheNodes;
    }
    
    public int getCurrentNodes() {
        return this.currentNodes.intValue();
    }
    
    public float getFullPercent() {
        return this.currentNodes.intValue() / (float)this.maxCacheNodes * 100.0f;
    }
    
    public int getMaxPartsPerCacheNode() {
        return this.maxPartsPerCacheNode;
    }
    
    public float getFalsePositiveProbability() {
        return this.falsePositiveProbability;
    }
    
    public Float getHitRatio() {
        if (this.cacheHits.longValue() + this.cacheMisses.longValue() > 0L) {
            return this.cacheHits.longValue() / (float)(this.cacheHits.longValue() + this.cacheMisses.longValue());
        }
        return null;
    }
    
    public AggrColStats get(final String dbName, final String tblName, final String colName, final List<String> partNames) {
        final Key key = new Key(dbName, tblName, colName);
        final AggrColStatsList candidateList = this.cacheStore.get(key);
        if (candidateList == null || candidateList.nodes.size() == 0) {
            AggregateStatsCache.LOG.info("No aggregate stats cached for " + key.toString());
            return null;
        }
        AggrColStats match = null;
        boolean isLocked = false;
        try {
            isLocked = candidateList.readLock.tryLock(this.maxReaderWaitTime, TimeUnit.MILLISECONDS);
            if (isLocked) {
                match = this.findBestMatch(partNames, candidateList.nodes);
            }
            if (match != null) {
                candidateList.updateLastAccessTime();
                this.cacheHits.incrementAndGet();
                AggregateStatsCache.LOG.info("Returning aggregate stats from the cache; total hits: " + this.cacheHits.longValue() + ", total misses: " + this.cacheMisses.longValue() + ", hit ratio: " + this.getHitRatio());
            }
            else {
                this.cacheMisses.incrementAndGet();
            }
        }
        catch (InterruptedException e) {
            AggregateStatsCache.LOG.debug(e);
        }
        finally {
            if (isLocked) {
                candidateList.readLock.unlock();
            }
        }
        return match;
    }
    
    private AggrColStats findBestMatch(final List<String> partNames, final List<AggrColStats> candidates) {
        final Map<AggrColStats, MatchStats> candidateMatchStats = new HashMap<AggrColStats, MatchStats>();
        AggrColStats bestMatch = null;
        final int bestMatchHits = 0;
        final int numPartsRequested = partNames.size();
        for (final AggrColStats candidate : candidates) {
            if (Math.abs((candidate.getNumPartsCached() - numPartsRequested) / numPartsRequested) > this.maxVariance) {
                continue;
            }
            if (this.isExpired(candidate)) {
                continue;
            }
            candidateMatchStats.put(candidate, new MatchStats(0, 0));
        }
        final int maxMisses = (int)this.maxVariance * numPartsRequested;
        for (final String partName : partNames) {
            final Iterator<Map.Entry<AggrColStats, MatchStats>> iterator = candidateMatchStats.entrySet().iterator();
            while (iterator.hasNext()) {
                final Map.Entry<AggrColStats, MatchStats> entry = iterator.next();
                final AggrColStats candidate2 = entry.getKey();
                final MatchStats matchStats = entry.getValue();
                if (candidate2.getBloomFilter().test(partName.getBytes())) {
                    ++matchStats.hits;
                }
                else {
                    ++matchStats.misses;
                }
                if (matchStats.misses > maxMisses) {
                    iterator.remove();
                }
                else {
                    if (matchStats.hits <= bestMatchHits) {
                        continue;
                    }
                    bestMatch = candidate2;
                }
            }
        }
        if (bestMatch != null) {
            bestMatch.updateLastAccessTime();
        }
        return bestMatch;
    }
    
    public void add(final String dbName, final String tblName, final String colName, final long numPartsCached, final ColumnStatisticsObj colStats, final BloomFilter bloomFilter) {
        if (this.getCurrentNodes() / this.maxCacheNodes > this.maxFull) {
            this.spawnCleaner();
        }
        final Key key = new Key(dbName, tblName, colName);
        final AggrColStats node = new AggrColStats(numPartsCached, bloomFilter, colStats);
        final AggrColStatsList newNodeList = new AggrColStatsList();
        newNodeList.nodes = (List<AggrColStats>)new ArrayList();
        AggrColStatsList nodeList = this.cacheStore.putIfAbsent(key, newNodeList);
        if (nodeList == null) {
            nodeList = newNodeList;
        }
        boolean isLocked = false;
        try {
            isLocked = nodeList.writeLock.tryLock(this.maxWriterWaitTime, TimeUnit.MILLISECONDS);
            if (isLocked) {
                nodeList.nodes.add(node);
                node.updateLastAccessTime();
                nodeList.updateLastAccessTime();
                this.currentNodes.getAndIncrement();
            }
        }
        catch (InterruptedException e) {
            AggregateStatsCache.LOG.debug(e);
        }
        finally {
            if (isLocked) {
                nodeList.writeLock.unlock();
            }
        }
    }
    
    private void spawnCleaner() {
        synchronized (this) {
            if (this.isCleaning) {
                return;
            }
            this.isCleaning = true;
        }
        final Thread cleaner = new Thread("AggregateStatsCache-CleanerThread") {
            @Override
            public void run() {
                AggregateStatsCache.this.numRemovedTTL = 0;
                AggregateStatsCache.this.numRemovedLRU = 0;
                final long cleanerStartTime = System.currentTimeMillis();
                AggregateStatsCache.LOG.info("AggregateStatsCache is " + AggregateStatsCache.this.getFullPercent() + "% full, with " + AggregateStatsCache.this.getCurrentNodes() + " nodes; starting cleaner thread");
                try {
                    final Iterator<Map.Entry<Key, AggrColStatsList>> mapIterator = AggregateStatsCache.this.cacheStore.entrySet().iterator();
                    while (mapIterator.hasNext()) {
                        final Map.Entry<Key, AggrColStatsList> pair = mapIterator.next();
                        final AggrColStatsList candidateList = pair.getValue();
                        final List<AggrColStats> nodes = candidateList.nodes;
                        if (nodes.size() == 0) {
                            mapIterator.remove();
                        }
                        else {
                            boolean isLocked = false;
                            try {
                                isLocked = candidateList.writeLock.tryLock(AggregateStatsCache.this.maxWriterWaitTime, TimeUnit.MILLISECONDS);
                                if (isLocked) {
                                    final Iterator<AggrColStats> listIterator = nodes.iterator();
                                    while (listIterator.hasNext()) {
                                        final AggrColStats node = listIterator.next();
                                        if (AggregateStatsCache.this.isExpired(node)) {
                                            listIterator.remove();
                                            final AggregateStatsCache this$0 = AggregateStatsCache.this;
                                            ++this$0.numRemovedTTL;
                                            AggregateStatsCache.this.currentNodes.getAndDecrement();
                                        }
                                    }
                                }
                            }
                            catch (InterruptedException e) {
                                AggregateStatsCache.LOG.debug(e);
                            }
                            finally {
                                if (isLocked) {
                                    candidateList.writeLock.unlock();
                                }
                            }
                            Thread.yield();
                        }
                    }
                    while (AggregateStatsCache.this.getCurrentNodes() / AggregateStatsCache.this.maxCacheNodes > AggregateStatsCache.this.cleanUntil) {
                        AggregateStatsCache.this.evictOneNode();
                    }
                }
                finally {
                    AggregateStatsCache.this.isCleaning = false;
                    AggregateStatsCache.LOG.info("Stopping cleaner thread; AggregateStatsCache is now " + AggregateStatsCache.this.getFullPercent() + "% full, with " + AggregateStatsCache.this.getCurrentNodes() + " nodes");
                    AggregateStatsCache.LOG.info("Number of expired nodes removed: " + AggregateStatsCache.this.numRemovedTTL);
                    AggregateStatsCache.LOG.info("Number of LRU nodes removed: " + AggregateStatsCache.this.numRemovedLRU);
                    AggregateStatsCache.LOG.info("Cleaner ran for: " + (System.currentTimeMillis() - cleanerStartTime) + "ms");
                }
            }
        };
        cleaner.setPriority(1);
        cleaner.setDaemon(true);
        cleaner.start();
    }
    
    private void evictOneNode() {
        Key lruKey = null;
        AggrColStatsList lruValue = null;
        for (final Map.Entry<Key, AggrColStatsList> entry : this.cacheStore.entrySet()) {
            final Key key = entry.getKey();
            final AggrColStatsList value = entry.getValue();
            if (lruKey == null) {
                lruKey = key;
                lruValue = value;
            }
            else {
                if (value.lastAccessTime >= lruValue.lastAccessTime || value.nodes.isEmpty()) {
                    continue;
                }
                lruKey = key;
                lruValue = value;
            }
        }
        final AggrColStatsList candidateList = this.cacheStore.get(lruKey);
        boolean isLocked = false;
        try {
            isLocked = candidateList.writeLock.tryLock(this.maxWriterWaitTime, TimeUnit.MILLISECONDS);
            if (isLocked) {
                AggrColStats lruNode = null;
                int currentIndex = 0;
                int deleteIndex = 0;
                final Iterator<AggrColStats> iterator = candidateList.nodes.iterator();
                while (iterator.hasNext()) {
                    final AggrColStats candidate = iterator.next();
                    if (this.isExpired(candidate)) {
                        iterator.remove();
                        this.currentNodes.getAndDecrement();
                        ++this.numRemovedTTL;
                        return;
                    }
                    if (lruNode == null) {
                        lruNode = candidate;
                        ++currentIndex;
                    }
                    else {
                        if (lruNode == null || candidate.lastAccessTime >= lruNode.lastAccessTime) {
                            continue;
                        }
                        lruNode = candidate;
                        deleteIndex = currentIndex;
                    }
                }
                candidateList.nodes.remove(deleteIndex);
                this.currentNodes.getAndDecrement();
                ++this.numRemovedLRU;
            }
        }
        catch (InterruptedException e) {
            AggregateStatsCache.LOG.debug(e);
        }
        finally {
            if (isLocked) {
                candidateList.writeLock.unlock();
            }
        }
    }
    
    private boolean isExpired(final AggrColStats aggrColStats) {
        return System.currentTimeMillis() - aggrColStats.lastAccessTime > this.timeToLiveMs;
    }
    
    static {
        LOG = LogFactory.getLog(AggregateStatsCache.class.getName());
        AggregateStatsCache.self = null;
    }
    
    static class Key
    {
        private final String dbName;
        private final String tblName;
        private final String colName;
        
        Key(final String db, final String table, final String col) {
            if (db == null || table == null || col == null) {
                throw new IllegalArgumentException("dbName, tblName, colName can't be null");
            }
            this.dbName = db;
            this.tblName = table;
            this.colName = col;
        }
        
        @Override
        public boolean equals(final Object other) {
            if (other == null || !(other instanceof Key)) {
                return false;
            }
            final Key that = (Key)other;
            return this.dbName.equals(that.dbName) && this.tblName.equals(that.tblName) && this.colName.equals(that.colName);
        }
        
        @Override
        public int hashCode() {
            return this.dbName.hashCode() * 31 + this.tblName.hashCode() * 31 + this.colName.hashCode();
        }
        
        @Override
        public String toString() {
            return "database:" + this.dbName + ", table:" + this.tblName + ", column:" + this.colName;
        }
    }
    
    static class AggrColStatsList
    {
        private List<AggrColStats> nodes;
        private ReadWriteLock lock;
        private Lock readLock;
        private Lock writeLock;
        private volatile long lastAccessTime;
        
        AggrColStatsList() {
            this.nodes = new ArrayList<AggrColStats>();
            this.lock = new ReentrantReadWriteLock();
            this.readLock = this.lock.readLock();
            this.writeLock = this.lock.writeLock();
            this.lastAccessTime = 0L;
        }
        
        List<AggrColStats> getNodes() {
            return this.nodes;
        }
        
        void updateLastAccessTime() {
            this.lastAccessTime = System.currentTimeMillis();
        }
    }
    
    public static class AggrColStats
    {
        private final long numPartsCached;
        private final BloomFilter bloomFilter;
        private final ColumnStatisticsObj colStats;
        private volatile long lastAccessTime;
        
        public AggrColStats(final long numPartsCached, final BloomFilter bloomFilter, final ColumnStatisticsObj colStats) {
            this.numPartsCached = numPartsCached;
            this.bloomFilter = bloomFilter;
            this.colStats = colStats;
            this.lastAccessTime = System.currentTimeMillis();
        }
        
        public long getNumPartsCached() {
            return this.numPartsCached;
        }
        
        public ColumnStatisticsObj getColStats() {
            return this.colStats;
        }
        
        public BloomFilter getBloomFilter() {
            return this.bloomFilter;
        }
        
        void updateLastAccessTime() {
            this.lastAccessTime = System.currentTimeMillis();
        }
    }
    
    private static class MatchStats
    {
        private int hits;
        private int misses;
        
        MatchStats(final int hits, final int misses) {
            this.hits = 0;
            this.misses = 0;
            this.hits = hits;
            this.misses = misses;
        }
    }
}
