// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.impl.uri.rules.automata;

import java.util.NoSuchElementException;
import java.util.Iterator;

public class TrieNodeValue<T>
{
    private Object value;
    
    public TrieNodeValue() {
        this.value = null;
    }
    
    public void set(final T value) {
        if (this.value == null) {
            this.value = value;
        }
        else if (this.value.getClass().isArray()) {
            final Object[] old = (Object[])this.value;
            final Object[] copy = new Object[old.length + 1];
            System.arraycopy(old, 0, copy, 0, old.length + 1);
            copy[copy.length - 1] = value;
            this.value = copy;
        }
        else {
            this.value = new Object[] { this.value, value };
        }
    }
    
    public Iterator<T> getIterator() {
        if (this.value == null) {
            return new EmptyIterator<T>();
        }
        if (this.value.getClass().isArray()) {
            return new ArrayIterator<T>((Object[])this.value);
        }
        return new SingleEntryIterator<T>((T)this.value);
    }
    
    public boolean isEmpty() {
        return this.value == null;
    }
    
    private static final class ArrayIterator<T> implements Iterator<T>
    {
        private Object[] data;
        private int cursor;
        
        public ArrayIterator(final Object[] data) {
            this.cursor = 0;
            this.data = data;
        }
        
        @Override
        public boolean hasNext() {
            return this.cursor < this.data.length;
        }
        
        @Override
        public T next() {
            if (this.hasNext()) {
                return (T)this.data[this.cursor++];
            }
            throw new NoSuchElementException();
        }
        
        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
    
    private static final class SingleEntryIterator<T> implements Iterator<T>
    {
        private T t;
        
        SingleEntryIterator(final T t) {
            this.t = t;
        }
        
        @Override
        public boolean hasNext() {
            return this.t != null;
        }
        
        @Override
        public T next() {
            if (this.hasNext()) {
                final T _t = this.t;
                this.t = null;
                return _t;
            }
            throw new NoSuchElementException();
        }
        
        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
    
    static final class EmptyIterator<T> implements Iterator<T>
    {
        @Override
        public boolean hasNext() {
            return false;
        }
        
        @Override
        public T next() {
            throw new NoSuchElementException();
        }
        
        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
