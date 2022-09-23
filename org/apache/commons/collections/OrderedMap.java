// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.collections;

public interface OrderedMap extends IterableMap
{
    OrderedMapIterator orderedMapIterator();
    
    Object firstKey();
    
    Object lastKey();
    
    Object nextKey(final Object p0);
    
    Object previousKey(final Object p0);
}
