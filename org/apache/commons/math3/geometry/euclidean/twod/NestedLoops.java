// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.math3.geometry.euclidean.twod;

import java.util.Iterator;
import org.apache.commons.math3.geometry.partitioning.RegionFactory;
import java.util.Collection;
import org.apache.commons.math3.geometry.euclidean.oned.Euclidean1D;
import org.apache.commons.math3.geometry.partitioning.Hyperplane;
import org.apache.commons.math3.geometry.euclidean.oned.IntervalsSet;
import org.apache.commons.math3.geometry.Vector;
import org.apache.commons.math3.geometry.partitioning.SubHyperplane;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.geometry.partitioning.Region;
import java.util.ArrayList;

class NestedLoops
{
    private Vector2D[] loop;
    private ArrayList<NestedLoops> surrounded;
    private Region<Euclidean2D> polygon;
    private boolean originalIsClockwise;
    
    public NestedLoops() {
        this.surrounded = new ArrayList<NestedLoops>();
    }
    
    private NestedLoops(final Vector2D[] loop) throws MathIllegalArgumentException {
        if (loop[0] == null) {
            throw new MathIllegalArgumentException(LocalizedFormats.OUTLINE_BOUNDARY_LOOP_OPEN, new Object[0]);
        }
        this.loop = loop;
        this.surrounded = new ArrayList<NestedLoops>();
        final ArrayList<SubHyperplane<Euclidean2D>> edges = new ArrayList<SubHyperplane<Euclidean2D>>();
        Vector2D current = loop[loop.length - 1];
        for (int i = 0; i < loop.length; ++i) {
            final Vector2D previous = current;
            current = loop[i];
            final Line line = new Line(previous, current);
            final IntervalsSet region = new IntervalsSet(line.toSubSpace((Vector<Euclidean2D>)previous).getX(), line.toSubSpace((Vector<Euclidean2D>)current).getX());
            edges.add(new SubLine(line, region));
        }
        this.polygon = new PolygonsSet(edges);
        if (Double.isInfinite(this.polygon.getSize())) {
            this.polygon = new RegionFactory<Euclidean2D>().getComplement(this.polygon);
            this.originalIsClockwise = false;
        }
        else {
            this.originalIsClockwise = true;
        }
    }
    
    public void add(final Vector2D[] bLoop) throws MathIllegalArgumentException {
        this.add(new NestedLoops(bLoop));
    }
    
    private void add(final NestedLoops node) throws MathIllegalArgumentException {
        for (final NestedLoops child : this.surrounded) {
            if (child.polygon.contains(node.polygon)) {
                child.add(node);
                return;
            }
        }
        final Iterator<NestedLoops> iterator = this.surrounded.iterator();
        while (iterator.hasNext()) {
            final NestedLoops child = iterator.next();
            if (node.polygon.contains(child.polygon)) {
                node.surrounded.add(child);
                iterator.remove();
            }
        }
        final RegionFactory<Euclidean2D> factory = new RegionFactory<Euclidean2D>();
        for (final NestedLoops child2 : this.surrounded) {
            if (!factory.intersection(node.polygon, child2.polygon).isEmpty()) {
                throw new MathIllegalArgumentException(LocalizedFormats.CROSSING_BOUNDARY_LOOPS, new Object[0]);
            }
        }
        this.surrounded.add(node);
    }
    
    public void correctOrientation() {
        for (final NestedLoops child : this.surrounded) {
            child.setClockWise(true);
        }
    }
    
    private void setClockWise(final boolean clockwise) {
        if (this.originalIsClockwise ^ clockwise) {
            int min = -1;
            int max = this.loop.length;
            while (++min < --max) {
                final Vector2D tmp = this.loop[min];
                this.loop[min] = this.loop[max];
                this.loop[max] = tmp;
            }
        }
        for (final NestedLoops child : this.surrounded) {
            child.setClockWise(!clockwise);
        }
    }
}
