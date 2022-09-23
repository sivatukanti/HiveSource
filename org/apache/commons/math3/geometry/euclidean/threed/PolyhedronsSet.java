// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.math3.geometry.euclidean.threed;

import org.apache.commons.math3.geometry.partitioning.AbstractSubHyperplane;
import org.apache.commons.math3.geometry.euclidean.twod.SubLine;
import java.awt.geom.AffineTransform;
import org.apache.commons.math3.geometry.euclidean.oned.Euclidean1D;
import org.apache.commons.math3.geometry.partitioning.Transform;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math3.geometry.partitioning.BoundaryAttribute;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.geometry.Vector;
import org.apache.commons.math3.geometry.partitioning.BSPTreeVisitor;
import org.apache.commons.math3.geometry.partitioning.Region;
import org.apache.commons.math3.geometry.partitioning.Hyperplane;
import org.apache.commons.math3.geometry.partitioning.RegionFactory;
import org.apache.commons.math3.geometry.partitioning.SubHyperplane;
import java.util.Collection;
import org.apache.commons.math3.geometry.partitioning.BSPTree;
import org.apache.commons.math3.geometry.euclidean.twod.Euclidean2D;
import org.apache.commons.math3.geometry.partitioning.AbstractRegion;

public class PolyhedronsSet extends AbstractRegion<Euclidean3D, Euclidean2D>
{
    public PolyhedronsSet() {
    }
    
    public PolyhedronsSet(final BSPTree<Euclidean3D> tree) {
        super(tree);
    }
    
    public PolyhedronsSet(final Collection<SubHyperplane<Euclidean3D>> boundary) {
        super(boundary);
    }
    
    public PolyhedronsSet(final double xMin, final double xMax, final double yMin, final double yMax, final double zMin, final double zMax) {
        super(buildBoundary(xMin, xMax, yMin, yMax, zMin, zMax));
    }
    
    private static BSPTree<Euclidean3D> buildBoundary(final double xMin, final double xMax, final double yMin, final double yMax, final double zMin, final double zMax) {
        final Plane pxMin = new Plane(new Vector3D(xMin, 0.0, 0.0), Vector3D.MINUS_I);
        final Plane pxMax = new Plane(new Vector3D(xMax, 0.0, 0.0), Vector3D.PLUS_I);
        final Plane pyMin = new Plane(new Vector3D(0.0, yMin, 0.0), Vector3D.MINUS_J);
        final Plane pyMax = new Plane(new Vector3D(0.0, yMax, 0.0), Vector3D.PLUS_J);
        final Plane pzMin = new Plane(new Vector3D(0.0, 0.0, zMin), Vector3D.MINUS_K);
        final Plane pzMax = new Plane(new Vector3D(0.0, 0.0, zMax), Vector3D.PLUS_K);
        final Region<Euclidean3D> boundary = new RegionFactory<Euclidean3D>().buildConvex(pxMin, pxMax, pyMin, pyMax, pzMin, pzMax);
        return boundary.getTree(false);
    }
    
    @Override
    public PolyhedronsSet buildNew(final BSPTree<Euclidean3D> tree) {
        return new PolyhedronsSet(tree);
    }
    
    @Override
    protected void computeGeometricalProperties() {
        ((AbstractRegion<Euclidean3D, T>)this).getTree(true).visit(new FacetsContributionVisitor());
        if (this.getSize() < 0.0) {
            this.setSize(Double.POSITIVE_INFINITY);
            ((AbstractRegion<Euclidean3D, T>)this).setBarycenter(Vector3D.NaN);
        }
        else {
            this.setSize(this.getSize() / 3.0);
            ((AbstractRegion<Euclidean3D, T>)this).setBarycenter(new Vector3D(1.0 / (4.0 * this.getSize()), (Vector3D)this.getBarycenter()));
        }
    }
    
    public SubHyperplane<Euclidean3D> firstIntersection(final Vector3D point, final Line line) {
        return this.recurseFirstIntersection(((AbstractRegion<Euclidean3D, T>)this).getTree(true), point, line);
    }
    
    private SubHyperplane<Euclidean3D> recurseFirstIntersection(final BSPTree<Euclidean3D> node, final Vector3D point, final Line line) {
        final SubHyperplane<Euclidean3D> cut = node.getCut();
        if (cut == null) {
            return null;
        }
        final BSPTree<Euclidean3D> minus = node.getMinus();
        final BSPTree<Euclidean3D> plus = node.getPlus();
        final Plane plane = (Plane)cut.getHyperplane();
        final double offset = plane.getOffset(point);
        final boolean in = FastMath.abs(offset) < 1.0E-10;
        BSPTree<Euclidean3D> near;
        BSPTree<Euclidean3D> far;
        if (offset < 0.0) {
            near = minus;
            far = plus;
        }
        else {
            near = plus;
            far = minus;
        }
        if (in) {
            final SubHyperplane<Euclidean3D> facet = this.boundaryFacet(point, node);
            if (facet != null) {
                return facet;
            }
        }
        final SubHyperplane<Euclidean3D> crossed = this.recurseFirstIntersection(near, point, line);
        if (crossed != null) {
            return crossed;
        }
        if (!in) {
            final Vector3D hit3D = plane.intersection(line);
            if (hit3D != null) {
                final SubHyperplane<Euclidean3D> facet2 = this.boundaryFacet(hit3D, node);
                if (facet2 != null) {
                    return facet2;
                }
            }
        }
        return this.recurseFirstIntersection(far, point, line);
    }
    
    private SubHyperplane<Euclidean3D> boundaryFacet(final Vector3D point, final BSPTree<Euclidean3D> node) {
        final Vector2D point2D = ((Plane)node.getCut().getHyperplane()).toSubSpace((Vector<Euclidean3D>)point);
        final BoundaryAttribute<Euclidean3D> attribute = (BoundaryAttribute<Euclidean3D>)node.getAttribute();
        if (attribute.getPlusOutside() != null && ((AbstractSubHyperplane<S, Euclidean2D>)attribute.getPlusOutside()).getRemainingRegion().checkPoint(point2D) == Region.Location.INSIDE) {
            return attribute.getPlusOutside();
        }
        if (attribute.getPlusInside() != null && ((AbstractSubHyperplane<S, Euclidean2D>)attribute.getPlusInside()).getRemainingRegion().checkPoint(point2D) == Region.Location.INSIDE) {
            return attribute.getPlusInside();
        }
        return null;
    }
    
    public PolyhedronsSet rotate(final Vector3D center, final Rotation rotation) {
        return (PolyhedronsSet)this.applyTransform(new RotationTransform(center, rotation));
    }
    
    public PolyhedronsSet translate(final Vector3D translation) {
        return (PolyhedronsSet)this.applyTransform(new TranslationTransform(translation));
    }
    
    private class FacetsContributionVisitor implements BSPTreeVisitor<Euclidean3D>
    {
        public FacetsContributionVisitor() {
            PolyhedronsSet.this.setSize(0.0);
            ((AbstractRegion<Euclidean3D, T>)PolyhedronsSet.this).setBarycenter(new Vector3D(0.0, 0.0, 0.0));
        }
        
        public Order visitOrder(final BSPTree<Euclidean3D> node) {
            return Order.MINUS_SUB_PLUS;
        }
        
        public void visitInternalNode(final BSPTree<Euclidean3D> node) {
            final BoundaryAttribute<Euclidean3D> attribute = (BoundaryAttribute<Euclidean3D>)node.getAttribute();
            if (attribute.getPlusOutside() != null) {
                this.addContribution(attribute.getPlusOutside(), false);
            }
            if (attribute.getPlusInside() != null) {
                this.addContribution(attribute.getPlusInside(), true);
            }
        }
        
        public void visitLeafNode(final BSPTree<Euclidean3D> node) {
        }
        
        private void addContribution(final SubHyperplane<Euclidean3D> facet, final boolean reversed) {
            final Region<Euclidean2D> polygon = ((AbstractSubHyperplane<S, Euclidean2D>)facet).getRemainingRegion();
            final double area = polygon.getSize();
            if (Double.isInfinite(area)) {
                AbstractRegion.this.setSize(Double.POSITIVE_INFINITY);
                AbstractRegion.this.setBarycenter(Vector3D.NaN);
            }
            else {
                final Plane plane = (Plane)facet.getHyperplane();
                final Vector3D facetB = plane.toSpace(polygon.getBarycenter());
                double scaled = area * facetB.dotProduct(plane.getNormal());
                if (reversed) {
                    scaled = -scaled;
                }
                AbstractRegion.this.setSize(PolyhedronsSet.this.getSize() + scaled);
                AbstractRegion.this.setBarycenter(new Vector3D(1.0, (Vector3D)PolyhedronsSet.this.getBarycenter(), scaled, facetB));
            }
        }
    }
    
    private static class RotationTransform implements Transform<Euclidean3D, Euclidean2D>
    {
        private Vector3D center;
        private Rotation rotation;
        private Plane cachedOriginal;
        private Transform<Euclidean2D, Euclidean1D> cachedTransform;
        
        public RotationTransform(final Vector3D center, final Rotation rotation) {
            this.center = center;
            this.rotation = rotation;
        }
        
        public Vector3D apply(final Vector<Euclidean3D> point) {
            final Vector3D delta = ((Vector3D)point).subtract((Vector<Euclidean3D>)this.center);
            return new Vector3D(1.0, this.center, 1.0, this.rotation.applyTo(delta));
        }
        
        public Plane apply(final Hyperplane<Euclidean3D> hyperplane) {
            return ((Plane)hyperplane).rotate(this.center, this.rotation);
        }
        
        public SubHyperplane<Euclidean2D> apply(final SubHyperplane<Euclidean2D> sub, final Hyperplane<Euclidean3D> original, final Hyperplane<Euclidean3D> transformed) {
            if (original != this.cachedOriginal) {
                final Plane oPlane = (Plane)original;
                final Plane tPlane = (Plane)transformed;
                final Vector3D p00 = oPlane.getOrigin();
                final Vector3D p2 = oPlane.toSpace((Vector<Euclidean2D>)new Vector2D(1.0, 0.0));
                final Vector3D p3 = oPlane.toSpace((Vector<Euclidean2D>)new Vector2D(0.0, 1.0));
                final Vector2D tP00 = tPlane.toSubSpace((Vector<Euclidean3D>)this.apply((Vector<Euclidean3D>)p00));
                final Vector2D tP2 = tPlane.toSubSpace((Vector<Euclidean3D>)this.apply((Vector<Euclidean3D>)p2));
                final Vector2D tP3 = tPlane.toSubSpace((Vector<Euclidean3D>)this.apply((Vector<Euclidean3D>)p3));
                final AffineTransform at = new AffineTransform(tP2.getX() - tP00.getX(), tP2.getY() - tP00.getY(), tP3.getX() - tP00.getX(), tP3.getY() - tP00.getY(), tP00.getX(), tP00.getY());
                this.cachedOriginal = (Plane)original;
                this.cachedTransform = org.apache.commons.math3.geometry.euclidean.twod.Line.getTransform(at);
            }
            return ((SubLine)sub).applyTransform(this.cachedTransform);
        }
    }
    
    private static class TranslationTransform implements Transform<Euclidean3D, Euclidean2D>
    {
        private Vector3D translation;
        private Plane cachedOriginal;
        private Transform<Euclidean2D, Euclidean1D> cachedTransform;
        
        public TranslationTransform(final Vector3D translation) {
            this.translation = translation;
        }
        
        public Vector3D apply(final Vector<Euclidean3D> point) {
            return new Vector3D(1.0, (Vector3D)point, 1.0, this.translation);
        }
        
        public Plane apply(final Hyperplane<Euclidean3D> hyperplane) {
            return ((Plane)hyperplane).translate(this.translation);
        }
        
        public SubHyperplane<Euclidean2D> apply(final SubHyperplane<Euclidean2D> sub, final Hyperplane<Euclidean3D> original, final Hyperplane<Euclidean3D> transformed) {
            if (original != this.cachedOriginal) {
                final Plane oPlane = (Plane)original;
                final Plane tPlane = (Plane)transformed;
                final Vector2D shift = tPlane.toSubSpace((Vector<Euclidean3D>)this.apply((Vector<Euclidean3D>)oPlane.getOrigin()));
                final AffineTransform at = AffineTransform.getTranslateInstance(shift.getX(), shift.getY());
                this.cachedOriginal = (Plane)original;
                this.cachedTransform = org.apache.commons.math3.geometry.euclidean.twod.Line.getTransform(at);
            }
            return ((SubLine)sub).applyTransform(this.cachedTransform);
        }
    }
}
