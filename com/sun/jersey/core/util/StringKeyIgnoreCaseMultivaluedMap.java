// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.core.util;

import java.util.LinkedList;
import java.util.Iterator;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Map;
import javax.ws.rs.core.MultivaluedMap;
import java.util.List;

public class StringKeyIgnoreCaseMultivaluedMap<V> extends KeyComparatorLinkedHashMap<String, List<V>> implements MultivaluedMap<String, V>
{
    public StringKeyIgnoreCaseMultivaluedMap() {
        super(StringIgnoreCaseKeyComparator.SINGLETON);
    }
    
    public StringKeyIgnoreCaseMultivaluedMap(final StringKeyIgnoreCaseMultivaluedMap<V> that) {
        super(StringIgnoreCaseKeyComparator.SINGLETON);
        for (final Map.Entry<String, List<V>> e : that.entrySet()) {
            this.put(e.getKey(), new ArrayList<V>((Collection<? extends V>)e.getValue()));
        }
    }
    
    @Override
    public void putSingle(final String key, final V value) {
        if (value == null) {
            return;
        }
        final List<V> l = this.getList(key);
        l.clear();
        l.add(value);
    }
    
    @Override
    public void add(final String key, final V value) {
        if (value == null) {
            return;
        }
        final List<V> l = this.getList(key);
        l.add(value);
    }
    
    @Override
    public V getFirst(final String key) {
        final List<V> values = this.get(key);
        if (values != null && values.size() > 0) {
            return values.get(0);
        }
        return null;
    }
    
    protected List<V> getList(final String key) {
        List<V> l = this.get(key);
        if (l == null) {
            l = new LinkedList<V>();
            this.put(key, l);
        }
        return l;
    }
}
