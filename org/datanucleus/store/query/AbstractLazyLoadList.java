// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.query;

import java.util.NoSuchElementException;
import org.datanucleus.ClassConstants;
import java.util.ListIterator;
import java.util.Iterator;
import java.util.Collection;
import java.util.HashMap;
import org.datanucleus.util.WeakValueMap;
import org.datanucleus.util.SoftValueMap;
import java.util.Map;
import org.datanucleus.util.Localiser;
import java.util.List;

public abstract class AbstractLazyLoadList implements List
{
    protected static final Localiser LOCALISER;
    private Map<Integer, Object> itemsByIndex;
    protected int size;
    
    public AbstractLazyLoadList(final String cacheType) {
        this.itemsByIndex = null;
        this.size = -1;
        if (cacheType != null) {
            if (cacheType.equalsIgnoreCase("soft")) {
                this.itemsByIndex = (Map<Integer, Object>)new SoftValueMap();
            }
            else if (cacheType.equalsIgnoreCase("weak")) {
                this.itemsByIndex = (Map<Integer, Object>)new WeakValueMap();
            }
            else if (cacheType.equalsIgnoreCase("strong")) {
                this.itemsByIndex = new HashMap<Integer, Object>();
            }
            else if (cacheType.equalsIgnoreCase("none")) {
                this.itemsByIndex = null;
            }
            else {
                this.itemsByIndex = (Map<Integer, Object>)new WeakValueMap();
            }
        }
        else {
            this.itemsByIndex = (Map<Integer, Object>)new WeakValueMap();
        }
    }
    
    protected abstract Object retrieveObjectForIndex(final int p0);
    
    protected abstract int getSize();
    
    protected boolean isOpen() {
        return true;
    }
    
    @Override
    public void add(final int index, final Object element) {
        throw new UnsupportedOperationException(AbstractLazyLoadList.LOCALISER.msg("052603"));
    }
    
    @Override
    public boolean add(final Object e) {
        throw new UnsupportedOperationException(AbstractLazyLoadList.LOCALISER.msg("052603"));
    }
    
    @Override
    public boolean addAll(final Collection c) {
        throw new UnsupportedOperationException(AbstractLazyLoadList.LOCALISER.msg("052603"));
    }
    
    @Override
    public boolean addAll(final int index, final Collection c) {
        throw new UnsupportedOperationException(AbstractLazyLoadList.LOCALISER.msg("052603"));
    }
    
    @Override
    public void clear() {
        throw new UnsupportedOperationException(AbstractLazyLoadList.LOCALISER.msg("052603"));
    }
    
    @Override
    public boolean contains(final Object o) {
        throw new UnsupportedOperationException(AbstractLazyLoadList.LOCALISER.msg("052603"));
    }
    
    @Override
    public boolean containsAll(final Collection c) {
        throw new UnsupportedOperationException(AbstractLazyLoadList.LOCALISER.msg("052603"));
    }
    
    @Override
    public Object get(final int index) {
        if (this.itemsByIndex != null && this.itemsByIndex.containsKey(index)) {
            return this.itemsByIndex.get(index);
        }
        final Object obj = this.retrieveObjectForIndex(index);
        this.itemsByIndex.put(index, obj);
        return obj;
    }
    
    @Override
    public int indexOf(final Object o) {
        throw new UnsupportedOperationException(AbstractLazyLoadList.LOCALISER.msg("052603"));
    }
    
    @Override
    public boolean isEmpty() {
        return this.size() == 0;
    }
    
    @Override
    public Iterator iterator() {
        return this.listIterator(0);
    }
    
    @Override
    public int lastIndexOf(final Object o) {
        throw new UnsupportedOperationException(AbstractLazyLoadList.LOCALISER.msg("052603"));
    }
    
    @Override
    public ListIterator listIterator() {
        return this.listIterator(0);
    }
    
    @Override
    public ListIterator listIterator(final int index) {
        return new LazyLoadListIterator();
    }
    
    @Override
    public Object remove(final int index) {
        throw new UnsupportedOperationException(AbstractLazyLoadList.LOCALISER.msg("052603"));
    }
    
    @Override
    public boolean remove(final Object o) {
        throw new UnsupportedOperationException(AbstractLazyLoadList.LOCALISER.msg("052603"));
    }
    
    @Override
    public boolean removeAll(final Collection c) {
        throw new UnsupportedOperationException(AbstractLazyLoadList.LOCALISER.msg("052603"));
    }
    
    @Override
    public boolean retainAll(final Collection c) {
        throw new UnsupportedOperationException(AbstractLazyLoadList.LOCALISER.msg("052603"));
    }
    
    @Override
    public Object set(final int index, final Object element) {
        throw new UnsupportedOperationException(AbstractLazyLoadList.LOCALISER.msg("052603"));
    }
    
    @Override
    public int size() {
        if (this.size >= 0) {
            return this.size;
        }
        return this.size = this.getSize();
    }
    
    @Override
    public List subList(final int fromIndex, final int toIndex) {
        throw new UnsupportedOperationException(AbstractLazyLoadList.LOCALISER.msg("052603"));
    }
    
    @Override
    public Object[] toArray() {
        final Object[] array = new Object[this.size()];
        for (int i = 0; i < array.length; ++i) {
            if (this.itemsByIndex != null && this.itemsByIndex.containsKey(i)) {
                array[i] = this.itemsByIndex.get(i);
            }
            else {
                array[i] = this.retrieveObjectForIndex(i);
            }
        }
        return array;
    }
    
    @Override
    public Object[] toArray(final Object[] a) {
        if (a == null) {
            throw new NullPointerException("null argument is illegal!");
        }
        Object[] array = a;
        final int ourSize = this.size();
        if (a.length < ourSize) {
            array = new Object[this.size()];
        }
        for (int i = 0; i < ourSize; ++i) {
            if (this.itemsByIndex != null && this.itemsByIndex.containsKey(i)) {
                array[i] = this.itemsByIndex.get(i);
            }
            else {
                array[i] = this.retrieveObjectForIndex(i);
            }
        }
        return array;
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
    }
    
    private class LazyLoadListIterator implements ListIterator
    {
        private int iteratorIndex;
        
        private LazyLoadListIterator() {
            this.iteratorIndex = 0;
        }
        
        @Override
        public boolean hasNext() {
            synchronized (AbstractLazyLoadList.this) {
                return AbstractLazyLoadList.this.isOpen() && this.iteratorIndex <= AbstractLazyLoadList.this.size() - 1;
            }
        }
        
        @Override
        public boolean hasPrevious() {
            synchronized (AbstractLazyLoadList.this) {
                return AbstractLazyLoadList.this.isOpen() && this.iteratorIndex > 0;
            }
        }
        
        @Override
        public Object next() {
            synchronized (AbstractLazyLoadList.this) {
                if (!AbstractLazyLoadList.this.isOpen()) {
                    throw new NoSuchElementException(AbstractLazyLoadList.LOCALISER.msg("052600"));
                }
                if (!this.hasNext()) {
                    throw new NoSuchElementException("No next element");
                }
                if (AbstractLazyLoadList.this.itemsByIndex != null && AbstractLazyLoadList.this.itemsByIndex.containsKey(this.iteratorIndex)) {
                    return AbstractLazyLoadList.this.itemsByIndex.get(this.iteratorIndex);
                }
                final Object obj = AbstractLazyLoadList.this.retrieveObjectForIndex(this.iteratorIndex);
                if (AbstractLazyLoadList.this.itemsByIndex != null) {
                    AbstractLazyLoadList.this.itemsByIndex.put(this.iteratorIndex, obj);
                }
                ++this.iteratorIndex;
                return obj;
            }
        }
        
        @Override
        public int nextIndex() {
            if (this.hasNext()) {
                return this.iteratorIndex;
            }
            return AbstractLazyLoadList.this.size();
        }
        
        @Override
        public Object previous() {
            synchronized (AbstractLazyLoadList.this) {
                if (!AbstractLazyLoadList.this.isOpen()) {
                    throw new NoSuchElementException(AbstractLazyLoadList.LOCALISER.msg("052600"));
                }
                if (!this.hasPrevious()) {
                    throw new NoSuchElementException("No previous element");
                }
                --this.iteratorIndex;
                if (AbstractLazyLoadList.this.itemsByIndex != null && AbstractLazyLoadList.this.itemsByIndex.containsKey(this.iteratorIndex)) {
                    return AbstractLazyLoadList.this.itemsByIndex.get(this.iteratorIndex);
                }
                final Object obj = AbstractLazyLoadList.this.retrieveObjectForIndex(this.iteratorIndex);
                if (AbstractLazyLoadList.this.itemsByIndex != null) {
                    AbstractLazyLoadList.this.itemsByIndex.put(this.iteratorIndex, obj);
                }
                ++this.iteratorIndex;
                return obj;
            }
        }
        
        @Override
        public int previousIndex() {
            if (this.iteratorIndex == 0) {
                return -1;
            }
            return this.iteratorIndex - 1;
        }
        
        @Override
        public void add(final Object e) {
            throw new UnsupportedOperationException(AbstractLazyLoadList.LOCALISER.msg("052603"));
        }
        
        @Override
        public void remove() {
            throw new UnsupportedOperationException(AbstractLazyLoadList.LOCALISER.msg("052603"));
        }
        
        @Override
        public void set(final Object e) {
            throw new UnsupportedOperationException(AbstractLazyLoadList.LOCALISER.msg("052603"));
        }
    }
}
