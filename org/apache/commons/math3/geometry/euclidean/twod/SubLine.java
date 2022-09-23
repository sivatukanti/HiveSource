// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.math3.geometry.euclidean.twod;

import org.apache.commons.math3.geometry.partitioning.BSPTree;
import org.apache.commons.math3.geometry.partitioning.SubHyperplane;
import org.apache.commons.math3.geometry.euclidean.oned.OrientedPoint;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.geometry.partitioning.Side;
import java.util.Iterator;
import org.apache.commons.math3.geometry.Vector;
import org.apache.commons.math3.geometry.euclidean.oned.Vector1D;
import org.apache.commons.math3.geometry.euclidean.oned.Interval;
import java.util.ArrayList;
import org.apache.commons.math3.geometry.euclidean.oned.IntervalsSet;
import java.util.List;
import org.apache.commons.math3.geometry.partitioning.Region;
import org.apache.commons.math3.geometry.partitioning.Hyperplane;
import org.apache.commons.math3.geometry.euclidean.oned.Euclidean1D;
import org.apache.commons.math3.geometry.partitioning.AbstractSubHyperplane;

public class SubLine extends AbstractSubHyperplane<Euclidean2D, Euclidean1D>
{
    public SubLine(final Hyperplane<Euclidean2D> hyperplane, final Region<Euclidean1D> remainingRegion) {
        super(hyperplane, remainingRegion);
    }
    
    public SubLine(final Vector2D start, final Vector2D end) {
        super(new Line(start, end), buildIntervalSet(start, end));
    }
    
    public SubLine(final Segment segment) {
        super(segment.getLine(), buildIntervalSet(segment.getStart(), segment.getEnd()));
    }
    
    public List<Segment> getSegments() {
        final Line line = (Line)this.getHyperplane();
        final List<Interval> list = ((IntervalsSet)this.getRemainingRegion()).asList();
        final List<Segment> segments = new ArrayList<Segment>();
        for (final Interval interval : list) {
            final Vector2D start = line.toSpace((Vector<Euclidean1D>)new Vector1D(interval.getInf()));
            final Vector2D end = line.toSpace((Vector<Euclidean1D>)new Vector1D(interval.getSup()));
            segments.add(new Segment(start, end, line));
        }
        return segments;
    }
    
    public Vector2D intersection(final SubLine subLine, final boolean includeEndPoints) {
        final Line line1 = (Line)this.getHyperplane();
        final Line line2 = (Line)subLine.getHyperplane();
        final Vector2D v2D = line1.intersection(line2);
        final Region.Location loc1 = ((AbstractSubHyperplane<S, Euclidean1D>)this).getRemainingRegion().checkPoint(line1.toSubSpace((Vector<Euclidean2D>)v2D));
        final Region.Location loc2 = ((AbstractSubHyperplane<S, Euclidean1D>)subLine).getRemainingRegion().checkPoint(line2.toSubSpace((Vector<Euclidean2D>)v2D));
        if (includeEndPoints) {
            return (loc1 != Region.Location.OUTSIDE && loc2 != Region.Location.OUTSIDE) ? v2D : null;
        }
        return (loc1 == Region.Location.INSIDE && loc2 == Region.Location.INSIDE) ? v2D : null;
    }
    
    private static IntervalsSet buildIntervalSet(final Vector2D start, final Vector2D end) {
        final Line line = new Line(start, end);
        return new IntervalsSet(line.toSubSpace((Vector<Euclidean2D>)start).getX(), line.toSubSpace((Vector<Euclidean2D>)end).getX());
    }
    
    @Override
    protected AbstractSubHyperplane<Euclidean2D, Euclidean1D> buildNew(final Hyperplane<Euclidean2D> hyperplane, final Region<Euclidean1D> remainingRegion) {
        return new SubLine(hyperplane, remainingRegion);
    }
    
    @Override
    public Side side(final Hyperplane<Euclidean2D> hyperplane) {
        final Line thisLine = (Line)this.getHyperplane();
        final Line otherLine = (Line)hyperplane;
        final Vector2D crossing = thisLine.intersection(otherLine);
        if (crossing == null) {
            final double global = otherLine.getOffset(thisLine);
            return (global < -1.0E-10) ? Side.MINUS : ((global > 1.0E-10) ? Side.PLUS : Side.HYPER);
        }
        final boolean direct = FastMath.sin(thisLine.getAngle() - otherLine.getAngle()) < 0.0;
        final Vector1D x = thisLine.toSubSpace((Vector<Euclidean2D>)crossing);
        return ((AbstractSubHyperplane<S, Euclidean1D>)this).getRemainingRegion().side(new OrientedPoint(x, direct));
    }
    
    @Override
    public SubHyperplane.SplitSubHyperplane<Euclidean2D> split(final Hyperplane<Euclidean2D> hyperplane) {
        final Line thisLine = (Line)this.getHyperplane();
        final Line otherLine = (Line)hyperplane;
        final Vector2D crossing = thisLine.intersection(otherLine);
        if (crossing == null) {
            final double global = otherLine.getOffset(thisLine);
            return (global < -1.0E-10) ? new SubHyperplane.SplitSubHyperplane<Euclidean2D>(null, this) : new SubHyperplane.SplitSubHyperplane<Euclidean2D>(this, null);
        }
        final boolean direct = FastMath.sin(thisLine.getAngle() - otherLine.getAngle()) < 0.0;
        final Vector1D x = thisLine.toSubSpace((Vector<Euclidean2D>)crossing);
        final SubHyperplane<Euclidean1D> subPlus = new OrientedPoint(x, !direct).wholeHyperplane();
        final SubHyperplane<Euclidean1D> subMinus = new OrientedPoint(x, direct).wholeHyperplane();
        final BSPTree<Euclidean1D> splitTree = ((AbstractSubHyperplane<S, Euclidean1D>)this).getRemainingRegion().getTree(false).split(subMinus);
        final BSPTree<Euclidean1D> plusTree = ((AbstractSubHyperplane<S, Euclidean1D>)this).getRemainingRegion().isEmpty(splitTree.getPlus()) ? new BSPTree<Euclidean1D>(Boolean.FALSE) : new BSPTree<Euclidean1D>(subPlus, new BSPTree<Euclidean1D>(Boolean.FALSE), splitTree.getPlus(), null);
        final BSPTree<Euclidean1D> minusTree = ((AbstractSubHyperplane<S, Euclidean1D>)this).getRemainingRegion().isEmpty(splitTree.getMinus()) ? new BSPTree<Euclidean1D>(Boolean.FALSE) : new BSPTree<Euclidean1D>(subMinus, new BSPTree<Euclidean1D>(Boolean.FALSE), splitTree.getMinus(), null);
        return new SubHyperplane.SplitSubHyperplane<Euclidean2D>(new SubLine(thisLine.copySelf(), new IntervalsSet(plusTree)), new SubLine(thisLine.copySelf(), new IntervalsSet(minusTree)));
    }
}
