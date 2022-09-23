// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.util;

import java.util.NoSuchElementException;
import java.util.AbstractCollection;
import java.util.Set;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Collection;
import java.util.HashMap;

public class MultiMap extends HashMap
{
    private transient Collection values;
    
    public MultiMap() {
        this.values = null;
    }
    
    public MultiMap(final int initialCapacity) {
        super(initialCapacity);
        this.values = null;
    }
    
    public MultiMap(final int initialCapacity, final float loadFactor) {
        super(initialCapacity, loadFactor);
        this.values = null;
    }
    
    public MultiMap(final MultiMap map) {
        this.values = null;
        if (map != null) {
            for (final Map.Entry entry : map.entrySet()) {
                super.put(entry.getKey(), new ArrayList(entry.getValue()));
            }
        }
    }
    
    @Override
    public boolean containsValue(final Object value) {
        final Set pairs = super.entrySet();
        if (pairs == null) {
            return false;
        }
        for (final Map.Entry keyValuePair : pairs) {
            final Collection coll = keyValuePair.getValue();
            if (coll.contains(value)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public Object put(final Object key, final Object value) {
        Collection c = super.get(key);
        if (c == null) {
            c = this.createCollection(null);
            super.put(key, c);
        }
        final boolean results = c.add(value);
        return results ? value : null;
    }
    
    public Object remove(final Object key, final Object item) {
        final Collection valuesForKey = super.get(key);
        if (valuesForKey == null) {
            return null;
        }
        valuesForKey.remove(item);
        if (valuesForKey.isEmpty()) {
            this.remove(key);
        }
        return item;
    }
    
    @Override
    public void clear() {
        final Set pairs = super.entrySet();
        for (final Map.Entry keyValuePair : pairs) {
            final Collection coll = keyValuePair.getValue();
            coll.clear();
        }
        super.clear();
    }
    
    @Override
    public Collection values() {
        final Collection vs = this.values;
        return (vs != null) ? vs : (this.values = new ValueElement());
    }
    
    @Override
    public Object clone() {
        final MultiMap obj = (MultiMap)super.clone();
        for (final Map.Entry entry : this.entrySet()) {
            final Collection coll = entry.getValue();
            final Collection newColl = this.createCollection(coll);
            entry.setValue(newColl);
        }
        return obj;
    }
    
    protected Collection createCollection(final Collection c) {
        if (c == null) {
            return new ArrayList();
        }
        return new ArrayList(c);
    }
    
    private class ValueElement extends AbstractCollection
    {
        @Override
        public Iterator iterator() {
            return new ValueElementIter();
        }
        
        @Override
        public int size() {
            int i = 0;
            final Iterator iter = this.iterator();
            while (iter.hasNext()) {
                iter.next();
                ++i;
            }
            return i;
        }
        
        @Override
        public void clear() {
            MultiMap.this.clear();
        }
    }
    
    private class ValueElementIter implements Iterator
    {
        private Iterator backing;
        private Iterator temp;
        
        private ValueElementIter() {
            this.backing = MultiMap.this.values().iterator();
        }
        
        private boolean searchNextIterator() {
            while (this.temp == null || !this.temp.hasNext()) {
                if (!this.backing.hasNext()) {
                    return false;
                }
                this.temp = this.backing.next().iterator();
            }
            return true;
        }
        
        @Override
        public boolean hasNext() {
            return this.searchNextIterator();
        }
        
        @Override
        public Object next() {
            if (!this.searchNextIterator()) {
                throw new NoSuchElementException();
            }
            return this.temp.next();
        }
        
        @Override
        public void remove() {
            if (this.temp == null) {
                throw new IllegalStateException();
            }
            this.temp.remove();
        }
    }
}
