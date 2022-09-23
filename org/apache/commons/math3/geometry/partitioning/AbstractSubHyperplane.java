// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.math3.geometry.partitioning;

import org.apache.commons.math3.geometry.Space;

public abstract class AbstractSubHyperplane<S extends Space, T extends Space> implements SubHyperplane<S>
{
    private final Hyperplane<S> hyperplane;
    private final Region<T> remainingRegion;
    
    protected AbstractSubHyperplane(final Hyperplane<S> hyperplane, final Region<T> remainingRegion) {
        this.hyperplane = hyperplane;
        this.remainingRegion = remainingRegion;
    }
    
    protected abstract AbstractSubHyperplane<S, T> buildNew(final Hyperplane<S> p0, final Region<T> p1);
    
    public AbstractSubHyperplane<S, T> copySelf() {
        return this.buildNew(this.hyperplane, this.remainingRegion);
    }
    
    public Hyperplane<S> getHyperplane() {
        return this.hyperplane;
    }
    
    public Region<T> getRemainingRegion() {
        return this.remainingRegion;
    }
    
    public double getSize() {
        return this.remainingRegion.getSize();
    }
    
    public AbstractSubHyperplane<S, T> reunite(final SubHyperplane<S> other) {
        final AbstractSubHyperplane<S, T> o = (AbstractSubHyperplane)other;
        return this.buildNew(this.hyperplane, new RegionFactory<T>().union(this.remainingRegion, o.remainingRegion));
    }
    
    public AbstractSubHyperplane<S, T> applyTransform(final Transform<S, T> transform) {
        final Hyperplane<S> tHyperplane = transform.apply(this.hyperplane);
        final BSPTree<T> tTree = this.recurseTransform(this.remainingRegion.getTree(false), tHyperplane, transform);
        return this.buildNew(tHyperplane, this.remainingRegion.buildNew(tTree));
    }
    
    private BSPTree<T> recurseTransform(final BSPTree<T> node, final Hyperplane<S> transformed, final Transform<S, T> transform) {
        if (node.getCut() == null) {
            return new BSPTree<T>(node.getAttribute());
        }
        BoundaryAttribute<T> attribute = (BoundaryAttribute<T>)node.getAttribute();
        if (attribute != null) {
            final SubHyperplane<T> tPO = (attribute.getPlusOutside() == null) ? null : transform.apply(attribute.getPlusOutside(), this.hyperplane, transformed);
            final SubHyperplane<T> tPI = (attribute.getPlusInside() == null) ? null : transform.apply(attribute.getPlusInside(), this.hyperplane, transformed);
            attribute = new BoundaryAttribute<T>(tPO, tPI);
        }
        return new BSPTree<T>(transform.apply(node.getCut(), this.hyperplane, transformed), this.recurseTransform(node.getPlus(), transformed, transform), this.recurseTransform(node.getMinus(), transformed, transform), attribute);
    }
    
    public abstract Side side(final Hyperplane<S> p0);
    
    public abstract SplitSubHyperplane<S> split(final Hyperplane<S> p0);
    
    public boolean isEmpty() {
        return this.remainingRegion.isEmpty();
    }
}
