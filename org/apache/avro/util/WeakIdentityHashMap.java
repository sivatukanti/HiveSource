// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.avro.util;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Iterator;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.HashMap;
import java.lang.ref.ReferenceQueue;
import java.util.Map;

public class WeakIdentityHashMap<K, V> implements Map<K, V>
{
    private final ReferenceQueue<K> queue;
    private Map<IdentityWeakReference, V> backingStore;
    
    public WeakIdentityHashMap() {
        this.queue = new ReferenceQueue<K>();
        this.backingStore = new HashMap<IdentityWeakReference, V>();
    }
    
    @Override
    public void clear() {
        this.backingStore.clear();
        this.reap();
    }
    
    @Override
    public boolean containsKey(final Object key) {
        this.reap();
        return this.backingStore.containsKey(new IdentityWeakReference(key));
    }
    
    @Override
    public boolean containsValue(final Object value) {
        this.reap();
        return this.backingStore.containsValue(value);
    }
    
    @Override
    public Set<Entry<K, V>> entrySet() {
        this.reap();
        final Set<Entry<K, V>> ret = new HashSet<Entry<K, V>>();
        for (final Entry<IdentityWeakReference, V> ref : this.backingStore.entrySet()) {
            final K key = ref.getKey().get();
            final V value = ref.getValue();
            final Entry<K, V> entry = new Entry<K, V>() {
                @Override
                public K getKey() {
                    return key;
                }
                
                @Override
                public V getValue() {
                    return value;
                }
                
                @Override
                public V setValue(final V value) {
                    throw new UnsupportedOperationException();
                }
            };
            ret.add(entry);
        }
        return Collections.unmodifiableSet((Set<? extends Entry<K, V>>)ret);
    }
    
    @Override
    public Set<K> keySet() {
        this.reap();
        final Set<K> ret = new HashSet<K>();
        for (final IdentityWeakReference ref : this.backingStore.keySet()) {
            ret.add(ref.get());
        }
        return Collections.unmodifiableSet((Set<? extends K>)ret);
    }
    
    @Override
    public boolean equals(final Object o) {
        return this.backingStore.equals(((WeakIdentityHashMap)o).backingStore);
    }
    
    @Override
    public V get(final Object key) {
        this.reap();
        return this.backingStore.get(new IdentityWeakReference(key));
    }
    
    @Override
    public V put(final K key, final V value) {
        this.reap();
        return this.backingStore.put(new IdentityWeakReference(key), value);
    }
    
    @Override
    public int hashCode() {
        this.reap();
        return this.backingStore.hashCode();
    }
    
    @Override
    public boolean isEmpty() {
        this.reap();
        return this.backingStore.isEmpty();
    }
    
    @Override
    public void putAll(final Map t) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public V remove(final Object key) {
        this.reap();
        return this.backingStore.remove(new IdentityWeakReference(key));
    }
    
    @Override
    public int size() {
        this.reap();
        return this.backingStore.size();
    }
    
    @Override
    public Collection<V> values() {
        this.reap();
        return this.backingStore.values();
    }
    
    private synchronized void reap() {
        for (Object zombie = this.queue.poll(); zombie != null; zombie = this.queue.poll()) {
            final IdentityWeakReference victim = (IdentityWeakReference)zombie;
            this.backingStore.remove(victim);
        }
    }
    
    class IdentityWeakReference extends WeakReference<K>
    {
        int hash;
        
        IdentityWeakReference(final Object obj) {
            super(obj, WeakIdentityHashMap.this.queue);
            this.hash = System.identityHashCode(obj);
        }
        
        @Override
        public int hashCode() {
            return this.hash;
        }
        
        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            final IdentityWeakReference ref = (IdentityWeakReference)o;
            return this.get() == ref.get();
        }
    }
}
