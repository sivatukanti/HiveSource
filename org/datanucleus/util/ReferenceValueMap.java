// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.util;

import java.util.List;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Collection;
import java.lang.ref.Reference;
import java.util.Iterator;
import java.util.Set;
import java.lang.ref.ReferenceQueue;
import java.util.HashMap;
import java.util.Map;

public abstract class ReferenceValueMap implements Map, Cloneable
{
    private HashMap map;
    private ReferenceQueue reaped;
    
    public ReferenceValueMap() {
        this.reaped = new ReferenceQueue();
        this.map = new HashMap();
    }
    
    public ReferenceValueMap(final int initial_capacity) {
        this.reaped = new ReferenceQueue();
        this.map = new HashMap(initial_capacity);
    }
    
    public ReferenceValueMap(final int initial_capacity, final float load_factor) {
        this.reaped = new ReferenceQueue();
        this.map = new HashMap(initial_capacity, load_factor);
    }
    
    public ReferenceValueMap(final Map m) {
        this.reaped = new ReferenceQueue();
        this.map = new HashMap();
        this.putAll(m);
    }
    
    public Object clone() {
        this.reap();
        ReferenceValueMap rvm = null;
        try {
            rvm = (ReferenceValueMap)super.clone();
        }
        catch (CloneNotSupportedException ex) {}
        (rvm.map = (HashMap)this.map.clone()).clear();
        rvm.reaped = new ReferenceQueue();
        rvm.putAll(this.entrySet());
        return rvm;
    }
    
    protected abstract ValueReference newValueReference(final Object p0, final Object p1, final ReferenceQueue p2);
    
    @Override
    public Object put(final Object key, final Object value) {
        this.reap();
        return this.unwrapReference(this.map.put(key, this.newValueReference(key, value, this.reaped)));
    }
    
    @Override
    public void putAll(final Map m) {
        this.putAll(m.entrySet());
    }
    
    private void putAll(final Set entrySet) {
        for (final Entry entry : entrySet) {
            this.put(entry.getKey(), entry.getValue());
        }
    }
    
    @Override
    public Object get(final Object key) {
        this.reap();
        return this.unwrapReference(this.map.get(key));
    }
    
    @Override
    public void clear() {
        this.reap();
        this.map.clear();
    }
    
    @Override
    public int size() {
        this.reap();
        return this.map.size();
    }
    
    @Override
    public boolean containsKey(final Object obj) {
        this.reap();
        return this.map.containsKey(obj);
    }
    
    @Override
    public boolean containsValue(final Object obj) {
        this.reap();
        if (obj != null) {
            for (final Reference ref : this.map.values()) {
                if (obj.equals(ref.get())) {
                    return true;
                }
            }
        }
        return false;
    }
    
    @Override
    public boolean isEmpty() {
        this.reap();
        return this.map.isEmpty();
    }
    
    @Override
    public Set keySet() {
        this.reap();
        return this.map.keySet();
    }
    
    @Override
    public Collection values() {
        this.reap();
        final Collection c = this.map.values();
        final Iterator i = c.iterator();
        final ArrayList l = new ArrayList(c.size());
        while (i.hasNext()) {
            final Reference ref = i.next();
            final Object obj = ref.get();
            if (obj != null) {
                l.add(obj);
            }
        }
        return Collections.unmodifiableList((List<?>)l);
    }
    
    @Override
    public Set entrySet() {
        this.reap();
        final Set s = this.map.entrySet();
        final Iterator i = s.iterator();
        final HashMap m = new HashMap(s.size());
        while (i.hasNext()) {
            final Entry entry = i.next();
            final Reference ref = entry.getValue();
            final Object obj = ref.get();
            if (obj != null) {
                m.put(entry.getKey(), obj);
            }
        }
        return Collections.unmodifiableSet((Set<?>)m.entrySet());
    }
    
    @Override
    public Object remove(final Object key) {
        this.reap();
        return this.unwrapReference(this.map.remove(key));
    }
    
    @Override
    public int hashCode() {
        this.reap();
        return this.map.hashCode();
    }
    
    @Override
    public boolean equals(final Object o) {
        this.reap();
        return this.map.equals(o);
    }
    
    public void reap() {
        ValueReference ref;
        while ((ref = (ValueReference)this.reaped.poll()) != null) {
            this.map.remove(ref.getKey());
        }
    }
    
    private Object unwrapReference(final Object obj) {
        if (obj == null) {
            return null;
        }
        final Reference ref = (Reference)obj;
        return ref.get();
    }
    
    public interface ValueReference
    {
        Object getKey();
    }
}
