// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.math3.stat.clustering;

import java.util.Collection;

public interface Clusterable<T>
{
    double distanceFrom(final T p0);
    
    T centroidOf(final Collection<T> p0);
}
