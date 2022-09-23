// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.math3.geometry.euclidean.oned;

import org.apache.commons.math3.geometry.partitioning.SubHyperplane;
import org.apache.commons.math3.geometry.partitioning.Region;
import org.apache.commons.math3.geometry.Vector;
import org.apache.commons.math3.geometry.partitioning.Hyperplane;

public class OrientedPoint implements Hyperplane<Euclidean1D>
{
    private Vector1D location;
    private boolean direct;
    
    public OrientedPoint(final Vector1D location, final boolean direct) {
        this.location = location;
        this.direct = direct;
    }
    
    public OrientedPoint copySelf() {
        return this;
    }
    
    public double getOffset(final Vector<Euclidean1D> point) {
        final double delta = ((Vector1D)point).getX() - this.location.getX();
        return this.direct ? delta : (-delta);
    }
    
    public SubOrientedPoint wholeHyperplane() {
        return new SubOrientedPoint(this, null);
    }
    
    public IntervalsSet wholeSpace() {
        return new IntervalsSet();
    }
    
    public boolean sameOrientationAs(final Hyperplane<Euclidean1D> other) {
        return !(this.direct ^ ((OrientedPoint)other).direct);
    }
    
    public Vector1D getLocation() {
        return this.location;
    }
    
    public boolean isDirect() {
        return this.direct;
    }
    
    public void revertSelf() {
        this.direct = !this.direct;
    }
}
