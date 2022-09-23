// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.util;

import org.apache.hadoop.HadoopIllegalArgumentException;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
public class LightWeightResizableGSet<K, E extends K> extends LightWeightGSet<K, E>
{
    static final int DEFAULT_INITIAL_CAPACITY = 16;
    static final float DEFAULT_LOAD_FACTOR = 0.75f;
    private int capacity;
    private final float loadFactor;
    private int threshold;
    
    public LightWeightResizableGSet(final int initCapacity, final float loadFactor) {
        if (initCapacity < 0) {
            throw new HadoopIllegalArgumentException("Illegal initial capacity: " + initCapacity);
        }
        if (loadFactor <= 0.0f || loadFactor > 1.0f) {
            throw new HadoopIllegalArgumentException("Illegal load factor: " + loadFactor);
        }
        this.capacity = LightWeightGSet.actualArrayLength(initCapacity);
        this.hash_mask = this.capacity - 1;
        this.loadFactor = loadFactor;
        this.threshold = (int)(this.capacity * loadFactor);
        this.entries = new LinkedElement[this.capacity];
    }
    
    public LightWeightResizableGSet() {
        this(16, 0.75f);
    }
    
    public LightWeightResizableGSet(final int initCapacity) {
        this(initCapacity, 0.75f);
    }
    
    @Override
    public E put(final E element) {
        final E existing = super.put(element);
        this.expandIfNecessary();
        return existing;
    }
    
    protected void resize(final int cap) {
        final int newCapacity = LightWeightGSet.actualArrayLength(cap);
        if (newCapacity == this.capacity) {
            return;
        }
        this.capacity = newCapacity;
        this.threshold = (int)(this.capacity * this.loadFactor);
        this.hash_mask = this.capacity - 1;
        final LinkedElement[] oldEntries = this.entries;
        this.entries = new LinkedElement[this.capacity];
        for (int i = 0; i < oldEntries.length; ++i) {
            LinkedElement next;
            for (LinkedElement e = oldEntries[i]; e != null; e = next) {
                next = e.getNext();
                final int index = this.getIndex((K)e);
                e.setNext(this.entries[index]);
                this.entries[index] = e;
            }
        }
    }
    
    protected void expandIfNecessary() {
        if (this.size > this.threshold && this.capacity < 1073741824) {
            this.resize(this.capacity * 2);
        }
    }
}
