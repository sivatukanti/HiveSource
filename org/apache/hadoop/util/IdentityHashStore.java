// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.util;

import com.google.common.base.Preconditions;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Evolving
public final class IdentityHashStore<K, V>
{
    private Object[] buffer;
    private int numInserted;
    private int capacity;
    private static final int DEFAULT_MAX_CAPACITY = 2;
    
    public IdentityHashStore(final int capacity) {
        this.numInserted = 0;
        Preconditions.checkArgument(capacity >= 0);
        if (capacity == 0) {
            this.capacity = 0;
            this.buffer = null;
        }
        else {
            this.realloc((int)Math.pow(2.0, Math.ceil(Math.log(capacity) / Math.log(2.0))));
        }
    }
    
    private void realloc(final int newCapacity) {
        Preconditions.checkArgument(newCapacity > 0);
        final Object[] prevBuffer = this.buffer;
        this.capacity = newCapacity;
        this.buffer = new Object[4 * newCapacity];
        this.numInserted = 0;
        if (prevBuffer != null) {
            for (int i = 0; i < prevBuffer.length; i += 2) {
                if (prevBuffer[i] != null) {
                    this.putInternal(prevBuffer[i], prevBuffer[i + 1]);
                }
            }
        }
    }
    
    private void putInternal(final Object k, final Object v) {
        final int hash = System.identityHashCode(k);
        int numEntries;
        int index;
        for (numEntries = this.buffer.length >> 1, index = (hash & numEntries - 1); this.buffer[2 * index] != null; index = (index + 1) % numEntries) {}
        this.buffer[2 * index] = k;
        this.buffer[1 + 2 * index] = v;
        ++this.numInserted;
    }
    
    public void put(final K k, final V v) {
        Preconditions.checkNotNull(k);
        if (this.buffer == null) {
            this.realloc(2);
        }
        else if (this.numInserted + 1 > this.capacity) {
            this.realloc(this.capacity * 2);
        }
        this.putInternal(k, v);
    }
    
    private int getElementIndex(final K k) {
        if (this.buffer == null) {
            return -1;
        }
        final int numEntries = this.buffer.length >> 1;
        final int hash = System.identityHashCode(k);
        final int firstIndex;
        int index = firstIndex = (hash & numEntries - 1);
        while (this.buffer[2 * index] != k) {
            index = (index + 1) % numEntries;
            if (index == firstIndex) {
                return -1;
            }
        }
        return index;
    }
    
    public V get(final K k) {
        final int index = this.getElementIndex(k);
        if (index < 0) {
            return null;
        }
        return (V)this.buffer[1 + 2 * index];
    }
    
    public V remove(final K k) {
        final int index = this.getElementIndex(k);
        if (index < 0) {
            return null;
        }
        final V val = (V)this.buffer[1 + 2 * index];
        this.buffer[2 * index] = null;
        this.buffer[1 + 2 * index] = null;
        --this.numInserted;
        return val;
    }
    
    public boolean isEmpty() {
        return this.numInserted == 0;
    }
    
    public int numElements() {
        return this.numInserted;
    }
    
    public int capacity() {
        return this.capacity;
    }
    
    public void visitAll(final Visitor<K, V> visitor) {
        for (int length = (this.buffer == null) ? 0 : this.buffer.length, i = 0; i < length; i += 2) {
            if (this.buffer[i] != null) {
                visitor.accept((K)this.buffer[i], (V)this.buffer[i + 1]);
            }
        }
    }
    
    public interface Visitor<K, V>
    {
        void accept(final K p0, final V p1);
    }
}
