// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.core.util;

import java.util.Comparator;

public interface KeyComparator<K> extends Comparator<K>
{
    boolean equals(final K p0, final K p1);
    
    int hash(final K p0);
}
