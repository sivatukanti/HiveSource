// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.math3.geometry.euclidean.oned;

import org.apache.commons.math3.geometry.partitioning.Region;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import org.apache.commons.math3.util.Precision;
import org.apache.commons.math3.geometry.Vector;
import org.apache.commons.math3.geometry.partitioning.SubHyperplane;
import java.util.Collection;
import org.apache.commons.math3.geometry.partitioning.BSPTree;
import org.apache.commons.math3.geometry.partitioning.AbstractRegion;

public class IntervalsSet extends AbstractRegion<Euclidean1D, Euclidean1D>
{
    public IntervalsSet() {
    }
    
    public IntervalsSet(final double lower, final double upper) {
        super(buildTree(lower, upper));
    }
    
    public IntervalsSet(final BSPTree<Euclidean1D> tree) {
        super(tree);
    }
    
    public IntervalsSet(final Collection<SubHyperplane<Euclidean1D>> boundary) {
        super(boundary);
    }
    
    private static BSPTree<Euclidean1D> buildTree(final double lower, final double upper) {
        if (Double.isInfinite(lower) && lower < 0.0) {
            if (Double.isInfinite(upper) && upper > 0.0) {
                return new BSPTree<Euclidean1D>(Boolean.TRUE);
            }
            final SubHyperplane<Euclidean1D> upperCut = new OrientedPoint(new Vector1D(upper), true).wholeHyperplane();
            return new BSPTree<Euclidean1D>(upperCut, new BSPTree<Euclidean1D>(Boolean.FALSE), new BSPTree<Euclidean1D>(Boolean.TRUE), null);
        }
        else {
            final SubHyperplane<Euclidean1D> lowerCut = new OrientedPoint(new Vector1D(lower), false).wholeHyperplane();
            if (Double.isInfinite(upper) && upper > 0.0) {
                return new BSPTree<Euclidean1D>(lowerCut, new BSPTree<Euclidean1D>(Boolean.FALSE), new BSPTree<Euclidean1D>(Boolean.TRUE), null);
            }
            final SubHyperplane<Euclidean1D> upperCut2 = new OrientedPoint(new Vector1D(upper), true).wholeHyperplane();
            return new BSPTree<Euclidean1D>(lowerCut, new BSPTree<Euclidean1D>(Boolean.FALSE), new BSPTree<Euclidean1D>(upperCut2, new BSPTree<Euclidean1D>(Boolean.FALSE), new BSPTree<Euclidean1D>(Boolean.TRUE), null), null);
        }
    }
    
    @Override
    public IntervalsSet buildNew(final BSPTree<Euclidean1D> tree) {
        return new IntervalsSet(tree);
    }
    
    @Override
    protected void computeGeometricalProperties() {
        if (((AbstractRegion<Euclidean1D, T>)this).getTree(false).getCut() == null) {
            ((AbstractRegion<Euclidean1D, T>)this).setBarycenter(Vector1D.NaN);
            this.setSize(((boolean)((AbstractRegion<Euclidean1D, T>)this).getTree(false).getAttribute()) ? Double.POSITIVE_INFINITY : 0.0);
        }
        else {
            double size = 0.0;
            double sum = 0.0;
            for (final Interval interval : this.asList()) {
                size += interval.getSize();
                sum += interval.getSize() * interval.getBarycenter();
            }
            this.setSize(size);
            if (Double.isInfinite(size)) {
                ((AbstractRegion<Euclidean1D, T>)this).setBarycenter(Vector1D.NaN);
            }
            else if (size >= Precision.SAFE_MIN) {
                ((AbstractRegion<Euclidean1D, T>)this).setBarycenter(new Vector1D(sum / size));
            }
            else {
                ((AbstractRegion<Euclidean1D, T>)this).setBarycenter(((OrientedPoint)((AbstractRegion<Euclidean1D, T>)this).getTree(false).getCut().getHyperplane()).getLocation());
            }
        }
    }
    
    public double getInf() {
        BSPTree<Euclidean1D> node = ((AbstractRegion<Euclidean1D, T>)this).getTree(false);
        double inf = Double.POSITIVE_INFINITY;
        while (node.getCut() != null) {
            final OrientedPoint op = (OrientedPoint)node.getCut().getHyperplane();
            inf = op.getLocation().getX();
            node = (op.isDirect() ? node.getMinus() : node.getPlus());
        }
        return node.getAttribute() ? Double.NEGATIVE_INFINITY : inf;
    }
    
    public double getSup() {
        BSPTree<Euclidean1D> node = ((AbstractRegion<Euclidean1D, T>)this).getTree(false);
        double sup = Double.NEGATIVE_INFINITY;
        while (node.getCut() != null) {
            final OrientedPoint op = (OrientedPoint)node.getCut().getHyperplane();
            sup = op.getLocation().getX();
            node = (op.isDirect() ? node.getPlus() : node.getMinus());
        }
        return node.getAttribute() ? Double.POSITIVE_INFINITY : sup;
    }
    
    public List<Interval> asList() {
        final List<Interval> list = new ArrayList<Interval>();
        this.recurseList(((AbstractRegion<Euclidean1D, T>)this).getTree(false), list, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
        return list;
    }
    
    private void recurseList(final BSPTree<Euclidean1D> node, final List<Interval> list, final double lower, final double upper) {
        if (node.getCut() == null) {
            if (node.getAttribute()) {
                list.add(new Interval(lower, upper));
            }
        }
        else {
            final OrientedPoint op = (OrientedPoint)node.getCut().getHyperplane();
            final Vector1D loc = op.getLocation();
            double x = loc.getX();
            final BSPTree<Euclidean1D> low = op.isDirect() ? node.getMinus() : node.getPlus();
            final BSPTree<Euclidean1D> high = op.isDirect() ? node.getPlus() : node.getMinus();
            this.recurseList(low, list, lower, x);
            if (((AbstractRegion<Euclidean1D, T>)this).checkPoint(low, loc) == Region.Location.INSIDE && ((AbstractRegion<Euclidean1D, T>)this).checkPoint(high, loc) == Region.Location.INSIDE) {
                x = list.remove(list.size() - 1).getInf();
            }
            this.recurseList(high, list, x, upper);
        }
    }
}
