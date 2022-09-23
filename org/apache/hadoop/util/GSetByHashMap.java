// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.HashMap;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
public class GSetByHashMap<K, E extends K> implements GSet<K, E>
{
    private final HashMap<K, E> m;
    
    public GSetByHashMap(final int initialCapacity, final float loadFactor) {
        this.m = new HashMap<K, E>(initialCapacity, loadFactor);
    }
    
    @Override
    public int size() {
        return this.m.size();
    }
    
    @Override
    public boolean contains(final K k) {
        return this.m.containsKey(k);
    }
    
    @Override
    public E get(final K k) {
        return this.m.get(k);
    }
    
    @Override
    public E put(final E element) {
        if (element == null) {
            throw new UnsupportedOperationException("Null element is not supported.");
        }
        return this.m.put(element, element);
    }
    
    @Override
    public E remove(final K k) {
        return this.m.remove(k);
    }
    
    @Override
    public Iterator<E> iterator() {
        return this.m.values().iterator();
    }
    
    @Override
    public void clear() {
        this.m.clear();
    }
    
    @Override
    public Collection<E> values() {
        return this.m.values();
    }
}
