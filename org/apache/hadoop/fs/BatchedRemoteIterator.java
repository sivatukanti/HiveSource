// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs;

import java.util.List;
import java.util.NoSuchElementException;
import java.io.IOException;

public abstract class BatchedRemoteIterator<K, E> implements RemoteIterator<E>
{
    private K prevKey;
    private BatchedEntries<E> entries;
    private int idx;
    
    public BatchedRemoteIterator(final K prevKey) {
        this.prevKey = prevKey;
        this.entries = null;
        this.idx = -1;
    }
    
    public abstract BatchedEntries<E> makeRequest(final K p0) throws IOException;
    
    private void makeRequest() throws IOException {
        this.idx = 0;
        this.entries = null;
        this.entries = this.makeRequest(this.prevKey);
        if (this.entries.size() == 0) {
            this.entries = null;
        }
    }
    
    private void makeRequestIfNeeded() throws IOException {
        if (this.idx == -1) {
            this.makeRequest();
        }
        else if (this.entries != null && this.idx >= this.entries.size()) {
            if (!this.entries.hasMore()) {
                this.entries = null;
            }
            else {
                this.makeRequest();
            }
        }
    }
    
    @Override
    public boolean hasNext() throws IOException {
        this.makeRequestIfNeeded();
        return this.entries != null;
    }
    
    public abstract K elementToPrevKey(final E p0);
    
    @Override
    public E next() throws IOException {
        this.makeRequestIfNeeded();
        if (this.entries == null) {
            throw new NoSuchElementException();
        }
        final E entry = this.entries.get(this.idx++);
        this.prevKey = this.elementToPrevKey(entry);
        return entry;
    }
    
    public static class BatchedListEntries<E> implements BatchedEntries<E>
    {
        private final List<E> entries;
        private final boolean hasMore;
        
        public BatchedListEntries(final List<E> entries, final boolean hasMore) {
            this.entries = entries;
            this.hasMore = hasMore;
        }
        
        @Override
        public E get(final int i) {
            return this.entries.get(i);
        }
        
        @Override
        public int size() {
            return this.entries.size();
        }
        
        @Override
        public boolean hasMore() {
            return this.hasMore;
        }
    }
    
    public interface BatchedEntries<E>
    {
        E get(final int p0);
        
        int size();
        
        boolean hasMore();
    }
}
