// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.internal.util;

import java.util.NoSuchElementException;
import java.util.Iterator;
import java.io.Serializable;
import java.util.Collection;

public abstract class $ImmutableCollection<E> implements Collection<E>, Serializable
{
    static final $ImmutableCollection<Object> EMPTY_IMMUTABLE_COLLECTION;
    private static final Object[] EMPTY_ARRAY;
    private static final $UnmodifiableIterator<Object> EMPTY_ITERATOR;
    
    $ImmutableCollection() {
    }
    
    public abstract $UnmodifiableIterator<E> iterator();
    
    public Object[] toArray() {
        final Object[] newArray = new Object[this.size()];
        return this.toArray(newArray);
    }
    
    public <T> T[] toArray(T[] other) {
        final int size = this.size();
        if (other.length < size) {
            other = $ObjectArrays.newArray(other, size);
        }
        else if (other.length > size) {
            other[size] = null;
        }
        int index = 0;
        for (final T elementAsT : this) {
            final E element = (E)elementAsT;
            other[index++] = elementAsT;
        }
        return other;
    }
    
    public boolean contains(@$Nullable final Object object) {
        if (object == null) {
            return false;
        }
        for (final E element : this) {
            if (element.equals(object)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean containsAll(final Collection<?> targets) {
        for (final Object target : targets) {
            if (!this.contains(target)) {
                return false;
            }
        }
        return true;
    }
    
    public boolean isEmpty() {
        return this.size() == 0;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(this.size() * 16);
        sb.append('[');
        final Iterator<E> i = this.iterator();
        if (i.hasNext()) {
            sb.append(i.next());
        }
        while (i.hasNext()) {
            sb.append(", ");
            sb.append(i.next());
        }
        return sb.append(']').toString();
    }
    
    public final boolean add(final E e) {
        throw new UnsupportedOperationException();
    }
    
    public final boolean remove(final Object object) {
        throw new UnsupportedOperationException();
    }
    
    public final boolean addAll(final Collection<? extends E> newElements) {
        throw new UnsupportedOperationException();
    }
    
    public final boolean removeAll(final Collection<?> oldElements) {
        throw new UnsupportedOperationException();
    }
    
    public final boolean retainAll(final Collection<?> elementsToKeep) {
        throw new UnsupportedOperationException();
    }
    
    public final void clear() {
        throw new UnsupportedOperationException();
    }
    
    Object writeReplace() {
        return new SerializedForm(this.toArray());
    }
    
    static {
        EMPTY_IMMUTABLE_COLLECTION = new EmptyImmutableCollection();
        EMPTY_ARRAY = new Object[0];
        EMPTY_ITERATOR = new $UnmodifiableIterator<Object>() {
            public boolean hasNext() {
                return false;
            }
            
            public Object next() {
                throw new NoSuchElementException();
            }
        };
    }
    
    private static class EmptyImmutableCollection extends $ImmutableCollection<Object>
    {
        public int size() {
            return 0;
        }
        
        @Override
        public boolean isEmpty() {
            return true;
        }
        
        @Override
        public boolean contains(@$Nullable final Object object) {
            return false;
        }
        
        @Override
        public $UnmodifiableIterator<Object> iterator() {
            return $ImmutableCollection.EMPTY_ITERATOR;
        }
        
        @Override
        public Object[] toArray() {
            return $ImmutableCollection.EMPTY_ARRAY;
        }
        
        @Override
        public <T> T[] toArray(final T[] array) {
            if (array.length > 0) {
                array[0] = null;
            }
            return array;
        }
    }
    
    private static class ArrayImmutableCollection<E> extends $ImmutableCollection<E>
    {
        private final E[] elements;
        
        ArrayImmutableCollection(final E[] elements) {
            this.elements = elements;
        }
        
        public int size() {
            return this.elements.length;
        }
        
        @Override
        public boolean isEmpty() {
            return false;
        }
        
        @Override
        public $UnmodifiableIterator<E> iterator() {
            return new $UnmodifiableIterator<E>() {
                int i = 0;
                
                public boolean hasNext() {
                    return this.i < ArrayImmutableCollection.this.elements.length;
                }
                
                public E next() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    return ArrayImmutableCollection.this.elements[this.i++];
                }
            };
        }
    }
    
    private static class SerializedForm implements Serializable
    {
        final Object[] elements;
        private static final long serialVersionUID = 0L;
        
        SerializedForm(final Object[] elements) {
            this.elements = elements;
        }
        
        Object readResolve() {
            return (this.elements.length == 0) ? $ImmutableCollection.EMPTY_IMMUTABLE_COLLECTION : new ArrayImmutableCollection<Object>(this.elements.clone());
        }
    }
}
