// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.core.util;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.Map;
import java.util.List;
import javax.ws.rs.core.MultivaluedMap;

public class UnmodifiableMultivaluedMap<K, V> implements MultivaluedMap<K, V>
{
    private final MultivaluedMap<K, V> delegate;
    
    public UnmodifiableMultivaluedMap(final MultivaluedMap<K, V> delegate) {
        this.delegate = delegate;
    }
    
    @Override
    public void putSingle(final K key, final V value) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void add(final K key, final V value) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public V getFirst(final K key) {
        return this.delegate.getFirst(key);
    }
    
    @Override
    public int size() {
        return this.delegate.size();
    }
    
    @Override
    public boolean isEmpty() {
        return this.delegate.isEmpty();
    }
    
    @Override
    public boolean containsKey(final Object key) {
        return this.delegate.containsKey(key);
    }
    
    @Override
    public boolean containsValue(final Object value) {
        return this.delegate.containsValue(value);
    }
    
    @Override
    public List<V> get(final Object key) {
        return this.delegate.get(key);
    }
    
    @Override
    public List<V> put(final K key, final List<V> value) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public List<V> remove(final Object key) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void putAll(final Map<? extends K, ? extends List<V>> m) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public Set<K> keySet() {
        return Collections.unmodifiableSet(this.delegate.keySet());
    }
    
    @Override
    public Collection<List<V>> values() {
        return Collections.unmodifiableCollection(this.delegate.values());
    }
    
    @Override
    public Set<Map.Entry<K, List<V>>> entrySet() {
        return Collections.unmodifiableSet((Set<? extends Map.Entry<K, List<V>>>)this.delegate.entrySet());
    }
}
