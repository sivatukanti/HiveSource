// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.http.pathmap;

import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.function.Predicate;
import java.util.Set;

public class PathSpecSet implements Set<String>, Predicate<String>
{
    private final Set<PathSpec> specs;
    
    public PathSpecSet() {
        this.specs = new TreeSet<PathSpec>();
    }
    
    @Override
    public boolean test(final String s) {
        for (final PathSpec spec : this.specs) {
            if (spec.matches(s)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean isEmpty() {
        return this.specs.isEmpty();
    }
    
    @Override
    public Iterator<String> iterator() {
        return new Iterator<String>() {
            private Iterator<PathSpec> iter = PathSpecSet.this.specs.iterator();
            
            @Override
            public boolean hasNext() {
                return this.iter.hasNext();
            }
            
            @Override
            public String next() {
                final PathSpec spec = this.iter.next();
                if (spec == null) {
                    return null;
                }
                return spec.getDeclaration();
            }
            
            @Override
            public void remove() {
                throw new UnsupportedOperationException("Remove not supported by this Iterator");
            }
        };
    }
    
    @Override
    public int size() {
        return this.specs.size();
    }
    
    @Override
    public boolean contains(final Object o) {
        if (o instanceof PathSpec) {
            return this.specs.contains(o);
        }
        return o instanceof String && this.specs.contains(this.toPathSpec((String)o));
    }
    
    private PathSpec asPathSpec(final Object o) {
        if (o == null) {
            return null;
        }
        if (o instanceof PathSpec) {
            return (PathSpec)o;
        }
        if (o instanceof String) {
            return this.toPathSpec((String)o);
        }
        return this.toPathSpec(o.toString());
    }
    
    private PathSpec toPathSpec(final String rawSpec) {
        if (rawSpec == null || rawSpec.length() < 1) {
            throw new RuntimeException("Path Spec String must start with '^', '/', or '*.': got [" + rawSpec + "]");
        }
        if (rawSpec.charAt(0) == '^') {
            return new RegexPathSpec(rawSpec);
        }
        return new ServletPathSpec(rawSpec);
    }
    
    @Override
    public Object[] toArray() {
        return this.toArray(new String[this.specs.size()]);
    }
    
    @Override
    public <T> T[] toArray(final T[] a) {
        int i = 0;
        for (final PathSpec spec : this.specs) {
            a[i++] = (T)spec.getDeclaration();
        }
        return a;
    }
    
    @Override
    public boolean add(final String e) {
        return this.specs.add(this.toPathSpec(e));
    }
    
    @Override
    public boolean remove(final Object o) {
        return this.specs.remove(this.asPathSpec(o));
    }
    
    @Override
    public boolean containsAll(final Collection<?> coll) {
        for (final Object o : coll) {
            if (!this.specs.contains(this.asPathSpec(o))) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public boolean addAll(final Collection<? extends String> coll) {
        boolean ret = false;
        for (final String s : coll) {
            ret |= this.add(s);
        }
        return ret;
    }
    
    @Override
    public boolean retainAll(final Collection<?> coll) {
        final List<PathSpec> collSpecs = new ArrayList<PathSpec>();
        for (final Object o : coll) {
            collSpecs.add(this.asPathSpec(o));
        }
        return this.specs.retainAll(collSpecs);
    }
    
    @Override
    public boolean removeAll(final Collection<?> coll) {
        final List<PathSpec> collSpecs = new ArrayList<PathSpec>();
        for (final Object o : coll) {
            collSpecs.add(this.asPathSpec(o));
        }
        return this.specs.removeAll(collSpecs);
    }
    
    @Override
    public void clear() {
        this.specs.clear();
    }
}
