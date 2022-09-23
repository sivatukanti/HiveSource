// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.types.resources;

import java.util.NoSuchElementException;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Set;
import java.util.WeakHashMap;
import org.apache.tools.ant.types.Resource;
import java.util.Iterator;

class FailFast implements Iterator<Resource>
{
    private static final WeakHashMap<Object, Set<FailFast>> MAP;
    private final Object parent;
    private Iterator<Resource> wrapped;
    
    static synchronized void invalidate(final Object o) {
        final Set<FailFast> s = FailFast.MAP.get(o);
        if (s != null) {
            s.clear();
        }
    }
    
    private static synchronized void add(final FailFast f) {
        Set<FailFast> s = FailFast.MAP.get(f.parent);
        if (s == null) {
            s = new HashSet<FailFast>();
            FailFast.MAP.put(f.parent, s);
        }
        s.add(f);
    }
    
    private static synchronized void remove(final FailFast f) {
        final Set<FailFast> s = FailFast.MAP.get(f.parent);
        if (s != null) {
            s.remove(f);
        }
    }
    
    private static synchronized void failFast(final FailFast f) {
        final Set<FailFast> s = FailFast.MAP.get(f.parent);
        if (!s.contains(f)) {
            throw new ConcurrentModificationException();
        }
    }
    
    FailFast(final Object o, final Iterator<Resource> i) {
        if (o == null) {
            throw new IllegalArgumentException("parent object is null");
        }
        if (i == null) {
            throw new IllegalArgumentException("cannot wrap null iterator");
        }
        this.parent = o;
        if (i.hasNext()) {
            this.wrapped = i;
            add(this);
        }
    }
    
    public boolean hasNext() {
        if (this.wrapped == null) {
            return false;
        }
        failFast(this);
        return this.wrapped.hasNext();
    }
    
    public Resource next() {
        if (this.wrapped == null || !this.wrapped.hasNext()) {
            throw new NoSuchElementException();
        }
        failFast(this);
        try {
            return this.wrapped.next();
        }
        finally {
            if (!this.wrapped.hasNext()) {
                this.wrapped = null;
                remove(this);
            }
        }
    }
    
    public void remove() {
        throw new UnsupportedOperationException();
    }
    
    static {
        MAP = new WeakHashMap<Object, Set<FailFast>>();
    }
}
