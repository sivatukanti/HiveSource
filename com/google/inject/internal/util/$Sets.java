// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.internal.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Collection;
import java.io.Serializable;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Map;
import java.util.LinkedHashSet;
import java.util.HashSet;

public final class $Sets
{
    private $Sets() {
    }
    
    public static <E> HashSet<E> newHashSet() {
        return new HashSet<E>();
    }
    
    public static <E> LinkedHashSet<E> newLinkedHashSet() {
        return new LinkedHashSet<E>();
    }
    
    public static <E> Set<E> newSetFromMap(final Map<E, Boolean> map) {
        return new SetFromMap<E>(map);
    }
    
    static int hashCodeImpl(final Set<?> s) {
        int hashCode = 0;
        for (final Object o : s) {
            hashCode += ((o != null) ? o.hashCode() : 0);
        }
        return hashCode;
    }
    
    private static class SetFromMap<E> extends AbstractSet<E> implements Set<E>, Serializable
    {
        private final Map<E, Boolean> m;
        private transient Set<E> s;
        static final long serialVersionUID = 0L;
        
        SetFromMap(final Map<E, Boolean> map) {
            $Preconditions.checkArgument(map.isEmpty(), (Object)"Map is non-empty");
            this.m = map;
            this.s = map.keySet();
        }
        
        @Override
        public void clear() {
            this.m.clear();
        }
        
        @Override
        public int size() {
            return this.m.size();
        }
        
        @Override
        public boolean isEmpty() {
            return this.m.isEmpty();
        }
        
        @Override
        public boolean contains(final Object o) {
            return this.m.containsKey(o);
        }
        
        @Override
        public boolean remove(final Object o) {
            return this.m.remove(o) != null;
        }
        
        @Override
        public boolean add(final E e) {
            return this.m.put(e, Boolean.TRUE) == null;
        }
        
        @Override
        public Iterator<E> iterator() {
            return this.s.iterator();
        }
        
        @Override
        public Object[] toArray() {
            return this.s.toArray();
        }
        
        @Override
        public <T> T[] toArray(final T[] a) {
            return this.s.toArray(a);
        }
        
        @Override
        public String toString() {
            return this.s.toString();
        }
        
        @Override
        public int hashCode() {
            return this.s.hashCode();
        }
        
        @Override
        public boolean equals(@$Nullable final Object object) {
            return this == object || this.s.equals(object);
        }
        
        @Override
        public boolean containsAll(final Collection<?> c) {
            return this.s.containsAll(c);
        }
        
        @Override
        public boolean removeAll(final Collection<?> c) {
            return this.s.removeAll(c);
        }
        
        @Override
        public boolean retainAll(final Collection<?> c) {
            return this.s.retainAll(c);
        }
        
        private void readObject(final ObjectInputStream stream) throws IOException, ClassNotFoundException {
            stream.defaultReadObject();
            this.s = this.m.keySet();
        }
    }
}
