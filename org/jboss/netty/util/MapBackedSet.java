// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.util;

import java.util.Iterator;
import java.util.Map;
import java.io.Serializable;
import java.util.AbstractSet;

final class MapBackedSet<E> extends AbstractSet<E> implements Serializable
{
    private static final long serialVersionUID = -6761513279741915432L;
    private final Map<E, Boolean> map;
    
    MapBackedSet(final Map<E, Boolean> map) {
        this.map = map;
    }
    
    @Override
    public int size() {
        return this.map.size();
    }
    
    @Override
    public boolean contains(final Object o) {
        return this.map.containsKey(o);
    }
    
    @Override
    public boolean add(final E o) {
        return this.map.put(o, Boolean.TRUE) == null;
    }
    
    @Override
    public boolean remove(final Object o) {
        return this.map.remove(o) != null;
    }
    
    @Override
    public void clear() {
        this.map.clear();
    }
    
    @Override
    public Iterator<E> iterator() {
        return this.map.keySet().iterator();
    }
}
