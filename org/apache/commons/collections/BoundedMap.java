// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.collections;

import java.util.Map;

public interface BoundedMap extends Map
{
    boolean isFull();
    
    int maxSize();
}
