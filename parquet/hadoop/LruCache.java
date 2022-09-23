// 
// Decompiled by Procyon v0.5.36
// 

package parquet.hadoop;

import java.util.Map;
import java.util.LinkedHashMap;
import parquet.Log;

final class LruCache<K, V extends Value<K, V>>
{
    private static final Log LOG;
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;
    private final LinkedHashMap<K, V> cacheMap;
    
    public LruCache(final int maxSize) {
        this(maxSize, 0.75f, true);
    }
    
    public LruCache(final int maxSize, final float loadFactor, final boolean accessOrder) {
        final int initialCapacity = Math.round(maxSize / loadFactor);
        this.cacheMap = new LinkedHashMap<K, V>(initialCapacity, loadFactor, accessOrder) {
            public boolean removeEldestEntry(final Map.Entry<K, V> eldest) {
                final boolean result = this.size() > maxSize;
                if (result && Log.DEBUG) {
                    LruCache.LOG.debug("Removing eldest entry in cache: " + eldest.getKey());
                }
                return result;
            }
        };
    }
    
    public V remove(final K key) {
        final V oldValue = this.cacheMap.remove(key);
        if (oldValue != null && Log.DEBUG) {
            LruCache.LOG.debug("Removed cache entry for '" + key + "'");
        }
        return oldValue;
    }
    
    public void put(final K key, final V newValue) {
        if (newValue == null || !((Value<K, V>)newValue).isCurrent(key)) {
            if (Log.WARN) {
                LruCache.LOG.warn("Ignoring new cache entry for '" + key + "' because it is " + ((newValue == null) ? "null" : "not current"));
            }
            return;
        }
        V oldValue = this.cacheMap.get(key);
        if (oldValue != null && ((Value<K, V>)oldValue).isNewerThan(newValue)) {
            if (Log.WARN) {
                LruCache.LOG.warn("Ignoring new cache entry for '" + key + "' because " + "existing cache entry is newer");
            }
            return;
        }
        oldValue = this.cacheMap.put(key, newValue);
        if (Log.DEBUG) {
            if (oldValue == null) {
                LruCache.LOG.debug("Added new cache entry for '" + key + "'");
            }
            else {
                LruCache.LOG.debug("Overwrote existing cache entry for '" + key + "'");
            }
        }
    }
    
    public void clear() {
        this.cacheMap.clear();
    }
    
    public V getCurrentValue(final K key) {
        final V value = this.cacheMap.get(key);
        if (Log.DEBUG) {
            LruCache.LOG.debug("Value for '" + key + "' " + ((value == null) ? "not " : "") + "in cache");
        }
        if (value != null && !((Value<K, V>)value).isCurrent(key)) {
            this.remove(key);
            return null;
        }
        return value;
    }
    
    public int size() {
        return this.cacheMap.size();
    }
    
    static {
        LOG = Log.getLog(LruCache.class);
    }
    
    interface Value<K, V>
    {
        boolean isCurrent(final K p0);
        
        boolean isNewerThan(final V p0);
    }
}
