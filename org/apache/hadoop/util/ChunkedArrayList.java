// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.util;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Iterables;
import java.util.Iterator;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import java.util.List;
import org.apache.hadoop.classification.InterfaceAudience;
import java.util.AbstractList;

@InterfaceAudience.Private
public class ChunkedArrayList<T> extends AbstractList<T>
{
    private final List<List<T>> chunks;
    private List<T> lastChunk;
    private int lastChunkCapacity;
    private final int initialChunkCapacity;
    private final int maxChunkSize;
    private int size;
    private static final int DEFAULT_INITIAL_CHUNK_CAPACITY = 6;
    private static final int DEFAULT_MAX_CHUNK_SIZE = 8192;
    
    public ChunkedArrayList() {
        this(6, 8192);
    }
    
    public ChunkedArrayList(final int initialChunkCapacity, final int maxChunkSize) {
        this.chunks = (List<List<T>>)Lists.newArrayList();
        this.lastChunk = null;
        Preconditions.checkArgument(maxChunkSize >= initialChunkCapacity);
        this.initialChunkCapacity = initialChunkCapacity;
        this.maxChunkSize = maxChunkSize;
    }
    
    @Override
    public Iterator<T> iterator() {
        final Iterator<T> it = Iterables.concat((Iterable<? extends Iterable<? extends T>>)this.chunks).iterator();
        return new Iterator<T>() {
            @Override
            public boolean hasNext() {
                return it.hasNext();
            }
            
            @Override
            public T next() {
                return it.next();
            }
            
            @Override
            public void remove() {
                it.remove();
                ChunkedArrayList.this.size--;
            }
        };
    }
    
    @Override
    public boolean add(final T e) {
        if (this.size == Integer.MAX_VALUE) {
            throw new RuntimeException("Can't add an additional element to the list; list already has INT_MAX elements.");
        }
        if (this.lastChunk == null) {
            this.addChunk(this.initialChunkCapacity);
        }
        else if (this.lastChunk.size() >= this.lastChunkCapacity) {
            final int newCapacity = this.lastChunkCapacity + (this.lastChunkCapacity >> 1);
            this.addChunk(Math.min(newCapacity, this.maxChunkSize));
        }
        ++this.size;
        return this.lastChunk.add(e);
    }
    
    @Override
    public void clear() {
        this.chunks.clear();
        this.lastChunk = null;
        this.lastChunkCapacity = 0;
        this.size = 0;
    }
    
    private void addChunk(final int capacity) {
        this.lastChunk = (List<T>)Lists.newArrayListWithCapacity(capacity);
        this.chunks.add(this.lastChunk);
        this.lastChunkCapacity = capacity;
    }
    
    @Override
    public boolean isEmpty() {
        return this.size == 0;
    }
    
    @Override
    public int size() {
        return this.size;
    }
    
    @VisibleForTesting
    int getNumChunks() {
        return this.chunks.size();
    }
    
    @VisibleForTesting
    int getMaxChunkSize() {
        int size = 0;
        for (final List<T> chunk : this.chunks) {
            size = Math.max(size, chunk.size());
        }
        return size;
    }
    
    @Override
    public T get(final int idx) {
        if (idx < 0) {
            throw new IndexOutOfBoundsException();
        }
        int base = 0;
        for (final List<T> list : this.chunks) {
            final int size = list.size();
            if (idx < base + size) {
                return list.get(idx - base);
            }
            base += size;
        }
        throw new IndexOutOfBoundsException();
    }
}
