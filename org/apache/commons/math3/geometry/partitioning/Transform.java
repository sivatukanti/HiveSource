// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.math3.geometry.partitioning;

import org.apache.commons.math3.geometry.Vector;
import org.apache.commons.math3.geometry.Space;

public interface Transform<S extends Space, T extends Space>
{
    Vector<S> apply(final Vector<S> p0);
    
    Hyperplane<S> apply(final Hyperplane<S> p0);
    
    SubHyperplane<T> apply(final SubHyperplane<T> p0, final Hyperplane<S> p1, final Hyperplane<S> p2);
}
