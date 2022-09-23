// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.util;

import java.util.NoSuchElementException;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.AbstractList;

public class SingletonList extends AbstractList
{
    private Object o;
    
    private SingletonList(final Object o) {
        this.o = o;
    }
    
    public static SingletonList newSingletonList(final Object o) {
        return new SingletonList(o);
    }
    
    public Object get(final int i) {
        if (i != 0) {
            throw new IndexOutOfBoundsException("index " + i);
        }
        return this.o;
    }
    
    public int size() {
        return 1;
    }
    
    public ListIterator listIterator() {
        return new SIterator();
    }
    
    public ListIterator listIterator(final int i) {
        return new SIterator(i);
    }
    
    public Iterator iterator() {
        return new SIterator();
    }
    
    private class SIterator implements ListIterator
    {
        int i;
        
        SIterator() {
            this.i = 0;
        }
        
        SIterator(final int i) {
            if (i < 0 || i > 1) {
                throw new IndexOutOfBoundsException("index " + i);
            }
            this.i = i;
        }
        
        public void add(final Object o) {
            throw new UnsupportedOperationException("SingletonList.add()");
        }
        
        public boolean hasNext() {
            return this.i == 0;
        }
        
        public boolean hasPrevious() {
            return this.i == 1;
        }
        
        public Object next() {
            if (this.i != 0) {
                throw new NoSuchElementException();
            }
            ++this.i;
            return SingletonList.this.o;
        }
        
        public int nextIndex() {
            return this.i;
        }
        
        public Object previous() {
            if (this.i != 1) {
                throw new NoSuchElementException();
            }
            --this.i;
            return SingletonList.this.o;
        }
        
        public int previousIndex() {
            return this.i - 1;
        }
        
        public void remove() {
            throw new UnsupportedOperationException("SingletonList.remove()");
        }
        
        public void set(final Object o) {
            throw new UnsupportedOperationException("SingletonList.add()");
        }
    }
}
