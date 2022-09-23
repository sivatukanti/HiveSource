// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.math3.geometry.partitioning;

import org.apache.commons.math3.geometry.Vector;
import org.apache.commons.math3.geometry.Space;

public interface Hyperplane<S extends Space>
{
    Hyperplane<S> copySelf();
    
    double getOffset(final Vector<S> p0);
    
    boolean sameOrientationAs(final Hyperplane<S> p0);
    
    SubHyperplane<S> wholeHyperplane();
    
    Region<S> wholeSpace();
}
