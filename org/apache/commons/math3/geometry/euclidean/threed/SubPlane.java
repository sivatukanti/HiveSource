// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.math3.geometry.euclidean.threed;

import org.apache.commons.math3.geometry.euclidean.twod.PolygonsSet;
import org.apache.commons.math3.geometry.partitioning.BSPTree;
import org.apache.commons.math3.geometry.partitioning.SubHyperplane;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math3.geometry.euclidean.twod.Line;
import org.apache.commons.math3.geometry.euclidean.oned.Euclidean1D;
import org.apache.commons.math3.geometry.Vector;
import org.apache.commons.math3.geometry.euclidean.oned.Vector1D;
import org.apache.commons.math3.geometry.partitioning.Side;
import org.apache.commons.math3.geometry.partitioning.Region;
import org.apache.commons.math3.geometry.partitioning.Hyperplane;
import org.apache.commons.math3.geometry.euclidean.twod.Euclidean2D;
import org.apache.commons.math3.geometry.partitioning.AbstractSubHyperplane;

public class SubPlane extends AbstractSubHyperplane<Euclidean3D, Euclidean2D>
{
    public SubPlane(final Hyperplane<Euclidean3D> hyperplane, final Region<Euclidean2D> remainingRegion) {
        super(hyperplane, remainingRegion);
    }
    
    @Override
    protected AbstractSubHyperplane<Euclidean3D, Euclidean2D> buildNew(final Hyperplane<Euclidean3D> hyperplane, final Region<Euclidean2D> remainingRegion) {
        return new SubPlane(hyperplane, remainingRegion);
    }
    
    @Override
    public Side side(final Hyperplane<Euclidean3D> hyperplane) {
        final Plane otherPlane = (Plane)hyperplane;
        final Plane thisPlane = (Plane)this.getHyperplane();
        final org.apache.commons.math3.geometry.euclidean.threed.Line inter = otherPlane.intersection(thisPlane);
        if (inter == null) {
            final double global = otherPlane.getOffset(thisPlane);
            return (global < -1.0E-10) ? Side.MINUS : ((global > 1.0E-10) ? Side.PLUS : Side.HYPER);
        }
        Vector2D p = thisPlane.toSubSpace((Vector<Euclidean3D>)inter.toSpace((Vector<Euclidean1D>)Vector1D.ZERO));
        Vector2D q = thisPlane.toSubSpace((Vector<Euclidean3D>)inter.toSpace((Vector<Euclidean1D>)Vector1D.ONE));
        final Vector3D crossP = Vector3D.crossProduct(inter.getDirection(), thisPlane.getNormal());
        if (crossP.dotProduct(otherPlane.getNormal()) < 0.0) {
            final Vector2D tmp = p;
            p = q;
            q = tmp;
        }
        final Line line2D = new Line(p, q);
        return ((AbstractSubHyperplane<S, Euclidean2D>)this).getRemainingRegion().side(line2D);
    }
    
    @Override
    public SubHyperplane.SplitSubHyperplane<Euclidean3D> split(final Hyperplane<Euclidean3D> hyperplane) {
        final Plane otherPlane = (Plane)hyperplane;
        final Plane thisPlane = (Plane)this.getHyperplane();
        final org.apache.commons.math3.geometry.euclidean.threed.Line inter = otherPlane.intersection(thisPlane);
        if (inter == null) {
            final double global = otherPlane.getOffset(thisPlane);
            return (global < -1.0E-10) ? new SubHyperplane.SplitSubHyperplane<Euclidean3D>(null, this) : new SubHyperplane.SplitSubHyperplane<Euclidean3D>(this, null);
        }
        Vector2D p = thisPlane.toSubSpace((Vector<Euclidean3D>)inter.toSpace((Vector<Euclidean1D>)Vector1D.ZERO));
        Vector2D q = thisPlane.toSubSpace((Vector<Euclidean3D>)inter.toSpace((Vector<Euclidean1D>)Vector1D.ONE));
        final Vector3D crossP = Vector3D.crossProduct(inter.getDirection(), thisPlane.getNormal());
        if (crossP.dotProduct(otherPlane.getNormal()) < 0.0) {
            final Vector2D tmp = p;
            p = q;
            q = tmp;
        }
        final SubHyperplane<Euclidean2D> l2DMinus = new Line(p, q).wholeHyperplane();
        final SubHyperplane<Euclidean2D> l2DPlus = new Line(q, p).wholeHyperplane();
        final BSPTree<Euclidean2D> splitTree = ((AbstractSubHyperplane<S, Euclidean2D>)this).getRemainingRegion().getTree(false).split(l2DMinus);
        final BSPTree<Euclidean2D> plusTree = ((AbstractSubHyperplane<S, Euclidean2D>)this).getRemainingRegion().isEmpty(splitTree.getPlus()) ? new BSPTree<Euclidean2D>(Boolean.FALSE) : new BSPTree<Euclidean2D>(l2DPlus, new BSPTree<Euclidean2D>(Boolean.FALSE), splitTree.getPlus(), null);
        final BSPTree<Euclidean2D> minusTree = ((AbstractSubHyperplane<S, Euclidean2D>)this).getRemainingRegion().isEmpty(splitTree.getMinus()) ? new BSPTree<Euclidean2D>(Boolean.FALSE) : new BSPTree<Euclidean2D>(l2DMinus, new BSPTree<Euclidean2D>(Boolean.FALSE), splitTree.getMinus(), null);
        return new SubHyperplane.SplitSubHyperplane<Euclidean3D>(new SubPlane(thisPlane.copySelf(), new PolygonsSet(plusTree)), new SubPlane(thisPlane.copySelf(), new PolygonsSet(minusTree)));
    }
}
