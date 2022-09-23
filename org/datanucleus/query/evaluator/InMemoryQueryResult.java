// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.query.evaluator;

import java.util.NoSuchElementException;
import org.datanucleus.ClassConstants;
import java.util.ListIterator;
import java.util.Iterator;
import java.util.Collection;
import java.util.List;
import org.datanucleus.api.ApiAdapter;
import org.datanucleus.util.Localiser;
import java.io.Serializable;
import org.datanucleus.store.query.QueryResult;
import java.util.AbstractList;

public class InMemoryQueryResult extends AbstractList implements QueryResult, Serializable
{
    protected static final Localiser LOCALISER;
    ApiAdapter api;
    List results;
    protected boolean closed;
    
    public InMemoryQueryResult(final List results, final ApiAdapter api) {
        this.results = null;
        this.closed = false;
        this.results = results;
        this.api = api;
    }
    
    @Override
    public void close() {
        if (this.closed) {
            return;
        }
        this.closed = true;
    }
    
    @Override
    public void disconnect() {
    }
    
    @Override
    public boolean contains(final Object o) {
        this.assertIsOpen();
        return this.results.contains(o);
    }
    
    @Override
    public boolean containsAll(final Collection c) {
        this.assertIsOpen();
        return this.results.containsAll(c);
    }
    
    @Override
    public Object get(final int index) {
        this.assertIsOpen();
        return this.results.get(index);
    }
    
    @Override
    public int indexOf(final Object o) {
        this.assertIsOpen();
        return this.results.indexOf(o);
    }
    
    @Override
    public boolean isEmpty() {
        this.assertIsOpen();
        return this.results.isEmpty();
    }
    
    @Override
    public int lastIndexOf(final Object o) {
        this.assertIsOpen();
        return this.results.lastIndexOf(o);
    }
    
    @Override
    public List subList(final int fromIndex, final int toIndex) {
        this.assertIsOpen();
        return this.results.subList(fromIndex, toIndex);
    }
    
    @Override
    public Object[] toArray() {
        this.assertIsOpen();
        return this.results.toArray();
    }
    
    @Override
    public Object[] toArray(final Object[] a) {
        this.assertIsOpen();
        return this.results.toArray(a);
    }
    
    @Override
    public int size() {
        this.assertIsOpen();
        return this.results.size();
    }
    
    @Override
    public Iterator iterator() {
        final Iterator resultIter = this.results.iterator();
        return new ResultIterator(resultIter);
    }
    
    @Override
    public ListIterator listIterator() {
        final ListIterator resultIter = this.results.listIterator();
        return new ResultIterator(resultIter);
    }
    
    @Override
    public ListIterator listIterator(final int index) {
        final ListIterator resultIter = this.results.listIterator(index);
        return new ResultIterator(resultIter);
    }
    
    protected void assertIsOpen() {
        if (this.closed) {
            final String msg = InMemoryQueryResult.LOCALISER.msg("052600");
            throw this.api.getUserExceptionForException(msg, null);
        }
    }
    
    @Override
    public boolean addAll(final int index, final Collection c) {
        throw new UnsupportedOperationException(InMemoryQueryResult.LOCALISER.msg("052604"));
    }
    
    @Override
    public boolean addAll(final Collection c) {
        throw new UnsupportedOperationException(InMemoryQueryResult.LOCALISER.msg("052604"));
    }
    
    @Override
    public boolean add(final Object e) {
        throw new UnsupportedOperationException(InMemoryQueryResult.LOCALISER.msg("052604"));
    }
    
    @Override
    public void add(final int index, final Object element) {
        throw new UnsupportedOperationException(InMemoryQueryResult.LOCALISER.msg("052604"));
    }
    
    @Override
    public void clear() {
        throw new UnsupportedOperationException(InMemoryQueryResult.LOCALISER.msg("052604"));
    }
    
    @Override
    public Object remove(final int index) {
        throw new UnsupportedOperationException(InMemoryQueryResult.LOCALISER.msg("052604"));
    }
    
    @Override
    public boolean remove(final Object o) {
        throw new UnsupportedOperationException(InMemoryQueryResult.LOCALISER.msg("052604"));
    }
    
    @Override
    public boolean removeAll(final Collection c) {
        throw new UnsupportedOperationException(InMemoryQueryResult.LOCALISER.msg("052604"));
    }
    
    @Override
    public boolean retainAll(final Collection c) {
        throw new UnsupportedOperationException(InMemoryQueryResult.LOCALISER.msg("052604"));
    }
    
    @Override
    public Object set(final int index, final Object element) {
        throw new UnsupportedOperationException(InMemoryQueryResult.LOCALISER.msg("052604"));
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
    }
    
    private class ResultIterator implements ListIterator
    {
        Iterator resultIter;
        
        public ResultIterator(final Iterator iter) {
            this.resultIter = null;
            this.resultIter = iter;
        }
        
        @Override
        public boolean hasNext() {
            return !InMemoryQueryResult.this.closed && this.resultIter.hasNext();
        }
        
        @Override
        public Object next() {
            if (InMemoryQueryResult.this.closed) {
                throw new NoSuchElementException();
            }
            return this.resultIter.next();
        }
        
        @Override
        public boolean hasPrevious() {
            return !InMemoryQueryResult.this.closed && ((ListIterator)this.resultIter).hasPrevious();
        }
        
        @Override
        public Object previous() {
            if (InMemoryQueryResult.this.closed) {
                throw new NoSuchElementException();
            }
            return ((ListIterator)this.resultIter).previous();
        }
        
        @Override
        public int nextIndex() {
            return ((ListIterator)this.resultIter).nextIndex();
        }
        
        @Override
        public int previousIndex() {
            return ((ListIterator)this.resultIter).previousIndex();
        }
        
        @Override
        public void remove() {
            throw new UnsupportedOperationException(InMemoryQueryResult.LOCALISER.msg("052604"));
        }
        
        @Override
        public void set(final Object e) {
            throw new UnsupportedOperationException(InMemoryQueryResult.LOCALISER.msg("052604"));
        }
        
        @Override
        public void add(final Object e) {
            throw new UnsupportedOperationException(InMemoryQueryResult.LOCALISER.msg("052604"));
        }
    }
}
