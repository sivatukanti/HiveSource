// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.collections;

public interface PriorityQueue
{
    void clear();
    
    boolean isEmpty();
    
    void insert(final Object p0);
    
    Object peek();
    
    Object pop();
}
