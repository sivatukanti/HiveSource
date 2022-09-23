// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.math3.geometry.euclidean.twod;

import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.geometry.Vector;

public class Segment
{
    private final Vector2D start;
    private final Vector2D end;
    private final Line line;
    
    public Segment(final Vector2D start, final Vector2D end, final Line line) {
        this.start = start;
        this.end = end;
        this.line = line;
    }
    
    public Vector2D getStart() {
        return this.start;
    }
    
    public Vector2D getEnd() {
        return this.end;
    }
    
    public Line getLine() {
        return this.line;
    }
    
    public double distance(final Vector2D p) {
        final double deltaX = this.end.getX() - this.start.getX();
        final double deltaY = this.end.getY() - this.start.getY();
        final double r = ((p.getX() - this.start.getX()) * deltaX + (p.getY() - this.start.getY()) * deltaY) / (deltaX * deltaX + deltaY * deltaY);
        if (r < 0.0 || r > 1.0) {
            final double dist1 = this.getStart().distance(p);
            final double dist2 = this.getEnd().distance(p);
            return FastMath.min(dist1, dist2);
        }
        final double px = this.start.getX() + r * deltaX;
        final double py = this.start.getY() + r * deltaY;
        final Vector2D interPt = new Vector2D(px, py);
        return interPt.distance(p);
    }
}
