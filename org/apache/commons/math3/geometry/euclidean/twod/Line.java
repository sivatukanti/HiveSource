// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.math3.geometry.euclidean.twod;

import org.apache.commons.math3.geometry.euclidean.oned.OrientedPoint;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.geometry.partitioning.SubHyperplane;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.geometry.partitioning.Transform;
import java.awt.geom.AffineTransform;
import org.apache.commons.math3.geometry.partitioning.Region;
import org.apache.commons.math3.geometry.euclidean.oned.IntervalsSet;
import org.apache.commons.math3.geometry.euclidean.oned.Vector1D;
import org.apache.commons.math3.geometry.Vector;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.MathUtils;
import org.apache.commons.math3.geometry.euclidean.oned.Euclidean1D;
import org.apache.commons.math3.geometry.partitioning.Embedding;
import org.apache.commons.math3.geometry.partitioning.Hyperplane;

public class Line implements Hyperplane<Euclidean2D>, Embedding<Euclidean2D, Euclidean1D>
{
    private double angle;
    private double cos;
    private double sin;
    private double originOffset;
    
    public Line(final Vector2D p1, final Vector2D p2) {
        this.reset(p1, p2);
    }
    
    public Line(final Vector2D p, final double angle) {
        this.reset(p, angle);
    }
    
    private Line(final double angle, final double cos, final double sin, final double originOffset) {
        this.angle = angle;
        this.cos = cos;
        this.sin = sin;
        this.originOffset = originOffset;
    }
    
    public Line(final Line line) {
        this.angle = MathUtils.normalizeAngle(line.angle, 3.141592653589793);
        this.cos = FastMath.cos(this.angle);
        this.sin = FastMath.sin(this.angle);
        this.originOffset = line.originOffset;
    }
    
    public Line copySelf() {
        return new Line(this);
    }
    
    public void reset(final Vector2D p1, final Vector2D p2) {
        final double dx = p2.getX() - p1.getX();
        final double dy = p2.getY() - p1.getY();
        final double d = FastMath.hypot(dx, dy);
        if (d == 0.0) {
            this.angle = 0.0;
            this.cos = 1.0;
            this.sin = 0.0;
            this.originOffset = p1.getY();
        }
        else {
            this.angle = 3.141592653589793 + FastMath.atan2(-dy, -dx);
            this.cos = FastMath.cos(this.angle);
            this.sin = FastMath.sin(this.angle);
            this.originOffset = (p2.getX() * p1.getY() - p1.getX() * p2.getY()) / d;
        }
    }
    
    public void reset(final Vector2D p, final double alpha) {
        this.angle = MathUtils.normalizeAngle(alpha, 3.141592653589793);
        this.cos = FastMath.cos(this.angle);
        this.sin = FastMath.sin(this.angle);
        this.originOffset = this.cos * p.getY() - this.sin * p.getX();
    }
    
    public void revertSelf() {
        if (this.angle < 3.141592653589793) {
            this.angle += 3.141592653589793;
        }
        else {
            this.angle -= 3.141592653589793;
        }
        this.cos = -this.cos;
        this.sin = -this.sin;
        this.originOffset = -this.originOffset;
    }
    
    public Line getReverse() {
        return new Line((this.angle < 3.141592653589793) ? (this.angle + 3.141592653589793) : (this.angle - 3.141592653589793), -this.cos, -this.sin, -this.originOffset);
    }
    
    public Vector1D toSubSpace(final Vector<Euclidean2D> point) {
        final Vector2D p2 = (Vector2D)point;
        return new Vector1D(this.cos * p2.getX() + this.sin * p2.getY());
    }
    
    public Vector2D toSpace(final Vector<Euclidean1D> point) {
        final double abscissa = ((Vector1D)point).getX();
        return new Vector2D(abscissa * this.cos - this.originOffset * this.sin, abscissa * this.sin + this.originOffset * this.cos);
    }
    
    public Vector2D intersection(final Line other) {
        final double d = this.sin * other.cos - other.sin * this.cos;
        if (FastMath.abs(d) < 1.0E-10) {
            return null;
        }
        return new Vector2D((this.cos * other.originOffset - other.cos * this.originOffset) / d, (this.sin * other.originOffset - other.sin * this.originOffset) / d);
    }
    
    public SubLine wholeHyperplane() {
        return new SubLine(this, new IntervalsSet());
    }
    
    public PolygonsSet wholeSpace() {
        return new PolygonsSet();
    }
    
    public double getOffset(final Line line) {
        return this.originOffset + ((this.cos * line.cos + this.sin * line.sin > 0.0) ? (-line.originOffset) : line.originOffset);
    }
    
    public double getOffset(final Vector<Euclidean2D> point) {
        final Vector2D p2 = (Vector2D)point;
        return this.sin * p2.getX() - this.cos * p2.getY() + this.originOffset;
    }
    
    public boolean sameOrientationAs(final Hyperplane<Euclidean2D> other) {
        final Line otherL = (Line)other;
        return this.sin * otherL.sin + this.cos * otherL.cos >= 0.0;
    }
    
    public Vector2D getPointAt(final Vector1D abscissa, final double offset) {
        final double x = abscissa.getX();
        final double dOffset = offset - this.originOffset;
        return new Vector2D(x * this.cos + dOffset * this.sin, x * this.sin - dOffset * this.cos);
    }
    
    public boolean contains(final Vector2D p) {
        return FastMath.abs(this.getOffset(p)) < 1.0E-10;
    }
    
    public double distance(final Vector2D p) {
        return FastMath.abs(this.getOffset(p));
    }
    
    public boolean isParallelTo(final Line line) {
        return FastMath.abs(this.sin * line.cos - this.cos * line.sin) < 1.0E-10;
    }
    
    public void translateToPoint(final Vector2D p) {
        this.originOffset = this.cos * p.getY() - this.sin * p.getX();
    }
    
    public double getAngle() {
        return MathUtils.normalizeAngle(this.angle, 3.141592653589793);
    }
    
    public void setAngle(final double angle) {
        this.angle = MathUtils.normalizeAngle(angle, 3.141592653589793);
        this.cos = FastMath.cos(this.angle);
        this.sin = FastMath.sin(this.angle);
    }
    
    public double getOriginOffset() {
        return this.originOffset;
    }
    
    public void setOriginOffset(final double offset) {
        this.originOffset = offset;
    }
    
    public static Transform<Euclidean2D, Euclidean1D> getTransform(final AffineTransform transform) throws MathIllegalArgumentException {
        return new LineTransform(transform);
    }
    
    private static class LineTransform implements Transform<Euclidean2D, Euclidean1D>
    {
        private double cXX;
        private double cXY;
        private double cX1;
        private double cYX;
        private double cYY;
        private double cY1;
        private double c1Y;
        private double c1X;
        private double c11;
        
        public LineTransform(final AffineTransform transform) throws MathIllegalArgumentException {
            final double[] m = new double[6];
            transform.getMatrix(m);
            this.cXX = m[0];
            this.cXY = m[2];
            this.cX1 = m[4];
            this.cYX = m[1];
            this.cYY = m[3];
            this.cY1 = m[5];
            this.c1Y = this.cXY * this.cY1 - this.cYY * this.cX1;
            this.c1X = this.cXX * this.cY1 - this.cYX * this.cX1;
            this.c11 = this.cXX * this.cYY - this.cYX * this.cXY;
            if (FastMath.abs(this.c11) < 1.0E-20) {
                throw new MathIllegalArgumentException(LocalizedFormats.NON_INVERTIBLE_TRANSFORM, new Object[0]);
            }
        }
        
        public Vector2D apply(final Vector<Euclidean2D> point) {
            final Vector2D p2D = (Vector2D)point;
            final double x = p2D.getX();
            final double y = p2D.getY();
            return new Vector2D(this.cXX * x + this.cXY * y + this.cX1, this.cYX * x + this.cYY * y + this.cY1);
        }
        
        public Line apply(final Hyperplane<Euclidean2D> hyperplane) {
            final Line line = (Line)hyperplane;
            final double rOffset = this.c1X * line.cos + this.c1Y * line.sin + this.c11 * line.originOffset;
            final double rCos = this.cXX * line.cos + this.cXY * line.sin;
            final double rSin = this.cYX * line.cos + this.cYY * line.sin;
            final double inv = 1.0 / FastMath.sqrt(rSin * rSin + rCos * rCos);
            return new Line(3.141592653589793 + FastMath.atan2(-rSin, -rCos), inv * rCos, inv * rSin, inv * rOffset, null);
        }
        
        public SubHyperplane<Euclidean1D> apply(final SubHyperplane<Euclidean1D> sub, final Hyperplane<Euclidean2D> original, final Hyperplane<Euclidean2D> transformed) {
            final OrientedPoint op = (OrientedPoint)sub.getHyperplane();
            final Line originalLine = (Line)original;
            final Line transformedLine = (Line)transformed;
            final Vector1D newLoc = transformedLine.toSubSpace((Vector<Euclidean2D>)this.apply((Vector<Euclidean2D>)originalLine.toSpace((Vector<Euclidean1D>)op.getLocation())));
            return new OrientedPoint(newLoc, op.isDirect()).wholeHyperplane();
        }
    }
}
