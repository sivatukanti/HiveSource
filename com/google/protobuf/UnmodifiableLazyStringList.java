// 
// Decompiled by Procyon v0.5.36
// 

package com.google.protobuf;

import java.util.List;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.RandomAccess;
import java.util.AbstractList;

public class UnmodifiableLazyStringList extends AbstractList<String> implements LazyStringList, RandomAccess
{
    private final LazyStringList list;
    
    public UnmodifiableLazyStringList(final LazyStringList list) {
        this.list = list;
    }
    
    @Override
    public String get(final int index) {
        return this.list.get(index);
    }
    
    @Override
    public int size() {
        return this.list.size();
    }
    
    public ByteString getByteString(final int index) {
        return this.list.getByteString(index);
    }
    
    public void add(final ByteString element) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public ListIterator<String> listIterator(final int index) {
        return new ListIterator<String>() {
            ListIterator<String> iter = UnmodifiableLazyStringList.this.list.listIterator(index);
            
            public boolean hasNext() {
                return this.iter.hasNext();
            }
            
            public String next() {
                return this.iter.next();
            }
            
            public boolean hasPrevious() {
                return this.iter.hasPrevious();
            }
            
            public String previous() {
                return this.iter.previous();
            }
            
            public int nextIndex() {
                return this.iter.nextIndex();
            }
            
            public int previousIndex() {
                return this.iter.previousIndex();
            }
            
            public void remove() {
                throw new UnsupportedOperationException();
            }
            
            public void set(final String o) {
                throw new UnsupportedOperationException();
            }
            
            public void add(final String o) {
                throw new UnsupportedOperationException();
            }
        };
    }
    
    @Override
    public Iterator<String> iterator() {
        return new Iterator<String>() {
            Iterator<String> iter = UnmodifiableLazyStringList.this.list.iterator();
            
            public boolean hasNext() {
                return this.iter.hasNext();
            }
            
            public String next() {
                return this.iter.next();
            }
            
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
    
    public List<?> getUnderlyingElements() {
        return this.list.getUnderlyingElements();
    }
}
