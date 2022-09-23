// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.client.impl;

import java.util.Collections;
import java.util.Collection;
import java.util.Set;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.Map;

public class CopyOnWriteHashMap<K, V> implements Map<K, V>
{
    private volatile Map<K, V> core;
    private volatile Map<K, V> view;
    private final AtomicBoolean requiresCopyOnWrite;
    
    public CopyOnWriteHashMap() {
        this.core = new HashMap<K, V>();
        this.requiresCopyOnWrite = new AtomicBoolean(false);
    }
    
    private CopyOnWriteHashMap(final CopyOnWriteHashMap<K, V> that) {
        this.core = that.core;
        this.requiresCopyOnWrite = new AtomicBoolean(true);
    }
    
    public CopyOnWriteHashMap<K, V> clone() {
        try {
            return new CopyOnWriteHashMap<K, V>(this);
        }
        finally {
            this.requiresCopyOnWrite.set(true);
        }
    }
    
    private void copy() {
        if (this.requiresCopyOnWrite.compareAndSet(true, false)) {
            this.core = new HashMap<K, V>((Map<? extends K, ? extends V>)this.core);
            this.view = null;
        }
    }
    
    @Override
    public int size() {
        return this.core.size();
    }
    
    @Override
    public boolean isEmpty() {
        return this.core.isEmpty();
    }
    
    @Override
    public boolean containsKey(final Object key) {
        return this.core.containsKey(key);
    }
    
    @Override
    public boolean containsValue(final Object value) {
        return this.core.containsValue(value);
    }
    
    @Override
    public V get(final Object key) {
        return this.core.get(key);
    }
    
    @Override
    public V put(final K key, final V value) {
        this.copy();
        return this.core.put(key, value);
    }
    
    @Override
    public V remove(final Object key) {
        this.copy();
        return this.core.remove(key);
    }
    
    @Override
    public void putAll(final Map<? extends K, ? extends V> t) {
        this.copy();
        this.core.putAll(t);
    }
    
    @Override
    public void clear() {
        this.core = new HashMap<K, V>();
        this.copy();
    }
    
    @Override
    public Set<K> keySet() {
        return this.getView().keySet();
    }
    
    @Override
    public Collection<V> values() {
        return this.getView().values();
    }
    
    @Override
    public Set<Entry<K, V>> entrySet() {
        return this.getView().entrySet();
    }
    
    @Override
    public String toString() {
        return this.core.toString();
    }
    
    private Map<K, V> getView() {
        if (this.view == null) {
            this.view = Collections.unmodifiableMap((Map<? extends K, ? extends V>)this.core);
        }
        return this.view;
    }
}
