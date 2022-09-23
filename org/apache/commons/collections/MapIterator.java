// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.collections;

import java.util.Iterator;

public interface MapIterator extends Iterator
{
    boolean hasNext();
    
    Object next();
    
    Object getKey();
    
    Object getValue();
    
    void remove();
    
    Object setValue(final Object p0);
}
