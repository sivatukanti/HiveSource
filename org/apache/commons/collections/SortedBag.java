// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.collections;

import java.util.Comparator;

public interface SortedBag extends Bag
{
    Comparator comparator();
    
    Object first();
    
    Object last();
}
