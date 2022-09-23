// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.access.sort;

final class NodeAllocator
{
    private static final int DEFAULT_INIT_SIZE = 128;
    private static final int GROWTH_MULTIPLIER = 2;
    private static final int DEFAULT_MAX_SIZE = 1024;
    private Node[] array;
    private int maxSize;
    private int nAllocated;
    private Node freeList;
    
    public NodeAllocator() {
        this.freeList = null;
        this.array = null;
        this.nAllocated = 0;
        this.maxSize = 0;
    }
    
    public Node newNode() {
        if (this.array == null && !this.init()) {
            return null;
        }
        if (this.freeList != null) {
            final Node freeList = this.freeList;
            this.freeList = freeList.rightLink;
            freeList.rightLink = null;
            return freeList;
        }
        if (this.nAllocated == this.array.length) {
            if (this.array.length >= this.maxSize) {
                return null;
            }
            final int n = (int)Math.min(this.array.length * 2L, this.maxSize);
            Node[] array;
            try {
                array = new Node[n];
            }
            catch (OutOfMemoryError outOfMemoryError) {
                return null;
            }
            System.arraycopy(this.array, 0, array, 0, this.array.length);
            this.array = array;
        }
        if (this.array[this.nAllocated] == null) {
            this.array[this.nAllocated] = new Node(this.nAllocated);
        }
        return this.array[this.nAllocated++];
    }
    
    public void freeNode(final Node freeList) {
        freeList.reset();
        freeList.rightLink = this.freeList;
        this.freeList = freeList;
    }
    
    public boolean init() {
        return this.init(128, 1024);
    }
    
    public boolean init(final int n) {
        return this.init(128, n);
    }
    
    public boolean init(int n, final int maxSize) {
        this.maxSize = maxSize;
        if (maxSize < n) {
            n = maxSize;
        }
        this.array = new Node[n];
        if (this.array == null) {
            return false;
        }
        this.nAllocated = 0;
        return true;
    }
    
    public void grow(final int n) {
        if (n > 0) {
            this.maxSize = (int)Math.min(this.maxSize * (long)(100 + n) / 100L, 2147483647L);
        }
    }
    
    public void reset() {
        if (this.array == null) {
            return;
        }
        for (int i = 0; i < this.nAllocated; ++i) {
            this.array[i].reset();
        }
        this.nAllocated = 0;
        this.freeList = null;
    }
    
    public void close() {
        this.array = null;
        this.nAllocated = 0;
        this.maxSize = 0;
        this.freeList = null;
    }
    
    public int capacity() {
        return this.maxSize;
    }
}
