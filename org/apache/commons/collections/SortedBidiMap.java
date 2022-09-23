// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.collections;

import java.util.SortedMap;

public interface SortedBidiMap extends OrderedBidiMap, SortedMap
{
    BidiMap inverseBidiMap();
    
    SortedBidiMap inverseSortedBidiMap();
}
