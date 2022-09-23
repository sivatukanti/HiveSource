// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.collections;

public interface OrderedMapIterator extends MapIterator, OrderedIterator
{
    boolean hasPrevious();
    
    Object previous();
}
