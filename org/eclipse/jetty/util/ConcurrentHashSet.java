// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.util;

import java.util.Iterator;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.Set;
import java.util.AbstractSet;

public class ConcurrentHashSet<E> extends AbstractSet<E> implements Set<E>
{
    private final Map<E, Boolean> _map;
    private transient Set<E> _keys;
    
    public ConcurrentHashSet() {
        this._map = new ConcurrentHashMap<E, Boolean>();
        this._keys = this._map.keySet();
    }
    
    @Override
    public boolean add(final E e) {
        return this._map.put(e, Boolean.TRUE) == null;
    }
    
    @Override
    public void clear() {
        this._map.clear();
    }
    
    @Override
    public boolean contains(final Object o) {
        return this._map.containsKey(o);
    }
    
    @Override
    public boolean containsAll(final Collection<?> c) {
        return this._keys.containsAll(c);
    }
    
    @Override
    public boolean equals(final Object o) {
        return o == this || this._keys.equals(o);
    }
    
    @Override
    public int hashCode() {
        return this._keys.hashCode();
    }
    
    @Override
    public boolean isEmpty() {
        return this._map.isEmpty();
    }
    
    @Override
    public Iterator<E> iterator() {
        return this._keys.iterator();
    }
    
    @Override
    public boolean remove(final Object o) {
        return this._map.remove(o) != null;
    }
    
    @Override
    public boolean removeAll(final Collection<?> c) {
        return this._keys.removeAll(c);
    }
    
    @Override
    public boolean retainAll(final Collection<?> c) {
        return this._keys.retainAll(c);
    }
    
    @Override
    public int size() {
        return this._map.size();
    }
    
    @Override
    public Object[] toArray() {
        return this._keys.toArray();
    }
    
    @Override
    public <T> T[] toArray(final T[] a) {
        return this._keys.toArray(a);
    }
    
    @Override
    public String toString() {
        return this._keys.toString();
    }
}
