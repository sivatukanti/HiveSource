// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.math3.geometry.euclidean.oned;

import org.apache.commons.math3.geometry.partitioning.SubHyperplane;
import org.apache.commons.math3.geometry.Vector;
import org.apache.commons.math3.geometry.partitioning.Side;
import org.apache.commons.math3.geometry.partitioning.Region;
import org.apache.commons.math3.geometry.partitioning.Hyperplane;
import org.apache.commons.math3.geometry.partitioning.AbstractSubHyperplane;

public class SubOrientedPoint extends AbstractSubHyperplane<Euclidean1D, Euclidean1D>
{
    public SubOrientedPoint(final Hyperplane<Euclidean1D> hyperplane, final Region<Euclidean1D> remainingRegion) {
        super(hyperplane, remainingRegion);
    }
    
    @Override
    public double getSize() {
        return 0.0;
    }
    
    @Override
    protected AbstractSubHyperplane<Euclidean1D, Euclidean1D> buildNew(final Hyperplane<Euclidean1D> hyperplane, final Region<Euclidean1D> remainingRegion) {
        return new SubOrientedPoint(hyperplane, remainingRegion);
    }
    
    @Override
    public Side side(final Hyperplane<Euclidean1D> hyperplane) {
        final double global = hyperplane.getOffset(((OrientedPoint)this.getHyperplane()).getLocation());
        return (global < -1.0E-10) ? Side.MINUS : ((global > 1.0E-10) ? Side.PLUS : Side.HYPER);
    }
    
    @Override
    public SubHyperplane.SplitSubHyperplane<Euclidean1D> split(final Hyperplane<Euclidean1D> hyperplane) {
        final double global = hyperplane.getOffset(((OrientedPoint)this.getHyperplane()).getLocation());
        return (global < -1.0E-10) ? new SubHyperplane.SplitSubHyperplane<Euclidean1D>(null, this) : new SubHyperplane.SplitSubHyperplane<Euclidean1D>(this, null);
    }
}
