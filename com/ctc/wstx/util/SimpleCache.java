// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.util;

import java.util.Map;
import java.util.LinkedHashMap;

public final class SimpleCache<K, V>
{
    final LimitMap<K, V> mItems;
    final int mMaxSize;
    
    public SimpleCache(final int maxSize) {
        this.mItems = new LimitMap<K, V>(maxSize);
        this.mMaxSize = maxSize;
    }
    
    public V find(final K key) {
        return this.mItems.get(key);
    }
    
    public void add(final K key, final V value) {
        this.mItems.put(key, value);
    }
    
    static final class LimitMap<K, V> extends LinkedHashMap<K, V>
    {
        final int mMaxSize;
        
        public LimitMap(final int size) {
            super(size, 0.8f, true);
            this.mMaxSize = size;
        }
        
        public boolean removeEldestEntry(final Map.Entry<K, V> eldest) {
            return this.size() >= this.mMaxSize;
        }
    }
}
