// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.util;

import java.util.Collection;
import java.util.Set;
import java.util.Enumeration;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Hashtable;

public class LinkedHashtable<K, V> extends Hashtable<K, V>
{
    private static final long serialVersionUID = 1L;
    private final LinkedHashMap<K, V> map;
    
    public LinkedHashtable() {
        this.map = new LinkedHashMap<K, V>();
    }
    
    public LinkedHashtable(final int initialCapacity) {
        this.map = new LinkedHashMap<K, V>(initialCapacity);
    }
    
    public LinkedHashtable(final int initialCapacity, final float loadFactor) {
        this.map = new LinkedHashMap<K, V>(initialCapacity, loadFactor);
    }
    
    public LinkedHashtable(final Map<K, V> m) {
        this.map = new LinkedHashMap<K, V>((Map<? extends K, ? extends V>)m);
    }
    
    @Override
    public synchronized void clear() {
        this.map.clear();
    }
    
    @Override
    public boolean contains(final Object value) {
        return this.containsKey(value);
    }
    
    @Override
    public synchronized boolean containsKey(final Object value) {
        return this.map.containsKey(value);
    }
    
    @Override
    public synchronized boolean containsValue(final Object value) {
        return this.map.containsValue(value);
    }
    
    @Override
    public Enumeration<V> elements() {
        return CollectionUtils.asEnumeration(this.values().iterator());
    }
    
    @Override
    public synchronized Set<Map.Entry<K, V>> entrySet() {
        return this.map.entrySet();
    }
    
    @Override
    public synchronized boolean equals(final Object o) {
        return this.map.equals(o);
    }
    
    @Override
    public synchronized V get(final Object k) {
        return this.map.get(k);
    }
    
    @Override
    public synchronized int hashCode() {
        return this.map.hashCode();
    }
    
    @Override
    public synchronized boolean isEmpty() {
        return this.map.isEmpty();
    }
    
    @Override
    public Enumeration<K> keys() {
        return CollectionUtils.asEnumeration(this.keySet().iterator());
    }
    
    @Override
    public synchronized Set<K> keySet() {
        return this.map.keySet();
    }
    
    @Override
    public synchronized V put(final K k, final V v) {
        return this.map.put(k, v);
    }
    
    @Override
    public synchronized void putAll(final Map<? extends K, ? extends V> m) {
        this.map.putAll((Map<?, ?>)m);
    }
    
    @Override
    public synchronized V remove(final Object k) {
        return this.map.remove(k);
    }
    
    @Override
    public synchronized int size() {
        return this.map.size();
    }
    
    @Override
    public synchronized String toString() {
        return this.map.toString();
    }
    
    @Override
    public synchronized Collection<V> values() {
        return this.map.values();
    }
}
