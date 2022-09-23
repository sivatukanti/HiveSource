// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.math3.geometry.euclidean.threed;

import org.apache.commons.math3.geometry.partitioning.AbstractRegion;
import org.apache.commons.math3.geometry.partitioning.Region;
import java.util.Iterator;
import org.apache.commons.math3.geometry.euclidean.oned.Euclidean1D;
import org.apache.commons.math3.geometry.Vector;
import org.apache.commons.math3.geometry.euclidean.oned.Vector1D;
import org.apache.commons.math3.geometry.euclidean.oned.Interval;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.geometry.euclidean.oned.IntervalsSet;

public class SubLine
{
    private final Line line;
    private final IntervalsSet remainingRegion;
    
    public SubLine(final Line line, final IntervalsSet remainingRegion) {
        this.line = line;
        this.remainingRegion = remainingRegion;
    }
    
    public SubLine(final Vector3D start, final Vector3D end) throws MathIllegalArgumentException {
        this(new Line(start, end), buildIntervalSet(start, end));
    }
    
    public SubLine(final Segment segment) throws MathIllegalArgumentException {
        this(segment.getLine(), buildIntervalSet(segment.getStart(), segment.getEnd()));
    }
    
    public List<Segment> getSegments() {
        final List<Interval> list = this.remainingRegion.asList();
        final List<Segment> segments = new ArrayList<Segment>();
        for (final Interval interval : list) {
            final Vector3D start = this.line.toSpace((Vector<Euclidean1D>)new Vector1D(interval.getInf()));
            final Vector3D end = this.line.toSpace((Vector<Euclidean1D>)new Vector1D(interval.getSup()));
            segments.add(new Segment(start, end, this.line));
        }
        return segments;
    }
    
    public Vector3D intersection(final SubLine subLine, final boolean includeEndPoints) {
        final Vector3D v1D = this.line.intersection(subLine.line);
        final Region.Location loc1 = ((AbstractRegion<Euclidean1D, T>)this.remainingRegion).checkPoint(this.line.toSubSpace((Vector<Euclidean3D>)v1D));
        final Region.Location loc2 = ((AbstractRegion<Euclidean1D, T>)subLine.remainingRegion).checkPoint(subLine.line.toSubSpace((Vector<Euclidean3D>)v1D));
        if (includeEndPoints) {
            return (loc1 != Region.Location.OUTSIDE && loc2 != Region.Location.OUTSIDE) ? v1D : null;
        }
        return (loc1 == Region.Location.INSIDE && loc2 == Region.Location.INSIDE) ? v1D : null;
    }
    
    private static IntervalsSet buildIntervalSet(final Vector3D start, final Vector3D end) throws MathIllegalArgumentException {
        final Line line = new Line(start, end);
        return new IntervalsSet(line.toSubSpace((Vector<Euclidean3D>)start).getX(), line.toSubSpace((Vector<Euclidean3D>)end).getX());
    }
}
