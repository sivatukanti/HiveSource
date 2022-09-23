// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.serialization;

import java.util.Collection;
import java.util.Set;
import java.util.Iterator;
import java.lang.ref.Reference;
import java.util.Map;

abstract class ReferenceMap<K, V> implements Map<K, V>
{
    private final Map<K, Reference<V>> delegate;
    
    protected ReferenceMap(final Map<K, Reference<V>> delegate) {
        this.delegate = delegate;
    }
    
    abstract Reference<V> fold(final V p0);
    
    private V unfold(final Reference<V> ref) {
        if (ref == null) {
            return null;
        }
        return ref.get();
    }
    
    public int size() {
        return this.delegate.size();
    }
    
    public boolean isEmpty() {
        return this.delegate.isEmpty();
    }
    
    public boolean containsKey(final Object key) {
        return this.delegate.containsKey(key);
    }
    
    public boolean containsValue(final Object value) {
        throw new UnsupportedOperationException();
    }
    
    public V get(final Object key) {
        return this.unfold(this.delegate.get(key));
    }
    
    public V put(final K key, final V value) {
        return this.unfold(this.delegate.put(key, this.fold(value)));
    }
    
    public V remove(final Object key) {
        return this.unfold(this.delegate.remove(key));
    }
    
    public void putAll(final Map<? extends K, ? extends V> m) {
        for (final Entry<? extends K, ? extends V> entry : m.entrySet()) {
            this.delegate.put((K)entry.getKey(), (Reference<V>)this.fold(entry.getValue()));
        }
    }
    
    public void clear() {
        this.delegate.clear();
    }
    
    public Set<K> keySet() {
        return this.delegate.keySet();
    }
    
    public Collection<V> values() {
        throw new UnsupportedOperationException();
    }
    
    public Set<Entry<K, V>> entrySet() {
        throw new UnsupportedOperationException();
    }
}
