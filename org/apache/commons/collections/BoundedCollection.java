// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.collections;

import java.util.Collection;

public interface BoundedCollection extends Collection
{
    boolean isFull();
    
    int maxSize();
}
