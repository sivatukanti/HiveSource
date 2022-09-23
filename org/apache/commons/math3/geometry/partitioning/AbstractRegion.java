// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.math3.geometry.partitioning;

import org.apache.commons.math3.exception.MathInternalError;
import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.TreeSet;
import java.util.Comparator;
import java.util.Collection;
import org.apache.commons.math3.geometry.Vector;
import org.apache.commons.math3.geometry.Space;

public abstract class AbstractRegion<S extends Space, T extends Space> implements Region<S>
{
    private BSPTree<S> tree;
    private double size;
    private Vector<S> barycenter;
    
    protected AbstractRegion() {
        this.tree = new BSPTree<S>(Boolean.TRUE);
    }
    
    protected AbstractRegion(final BSPTree<S> tree) {
        this.tree = tree;
    }
    
    protected AbstractRegion(final Collection<SubHyperplane<S>> boundary) {
        if (boundary.size() == 0) {
            this.tree = new BSPTree<S>(Boolean.TRUE);
        }
        else {
            final TreeSet<SubHyperplane<S>> ordered = new TreeSet<SubHyperplane<S>>(new Comparator<SubHyperplane<S>>() {
                public int compare(final SubHyperplane<S> o1, final SubHyperplane<S> o2) {
                    final double size1 = o1.getSize();
                    final double size2 = o2.getSize();
                    return (size2 < size1) ? -1 : ((o1 == o2) ? 0 : 1);
                }
            });
            ordered.addAll(boundary);
            this.insertCuts(this.tree = new BSPTree<S>(), ordered);
            this.tree.visit(new BSPTreeVisitor<S>() {
                public Order visitOrder(final BSPTree<S> node) {
                    return Order.PLUS_SUB_MINUS;
                }
                
                public void visitInternalNode(final BSPTree<S> node) {
                }
                
                public void visitLeafNode(final BSPTree<S> node) {
                    node.setAttribute((node == node.getParent().getPlus()) ? Boolean.FALSE : Boolean.TRUE);
                }
            });
        }
    }
    
    public AbstractRegion(final Hyperplane<S>[] hyperplanes) {
        if (hyperplanes == null || hyperplanes.length == 0) {
            this.tree = new BSPTree<S>(Boolean.FALSE);
        }
        else {
            this.tree = hyperplanes[0].wholeSpace().getTree(false);
            BSPTree<S> node = this.tree;
            node.setAttribute(Boolean.TRUE);
            for (final Hyperplane<S> hyperplane : hyperplanes) {
                if (node.insertCut(hyperplane)) {
                    node.setAttribute(null);
                    node.getPlus().setAttribute(Boolean.FALSE);
                    node = node.getMinus();
                    node.setAttribute(Boolean.TRUE);
                }
            }
        }
    }
    
    public abstract AbstractRegion<S, T> buildNew(final BSPTree<S> p0);
    
    private void insertCuts(final BSPTree<S> node, final Collection<SubHyperplane<S>> boundary) {
        Iterator<SubHyperplane<S>> iterator;
        Hyperplane<S> inserted;
        for (iterator = boundary.iterator(), inserted = null; inserted == null && iterator.hasNext(); inserted = null) {
            inserted = iterator.next().getHyperplane();
            if (!node.insertCut(inserted.copySelf())) {}
        }
        if (!iterator.hasNext()) {
            return;
        }
        final ArrayList<SubHyperplane<S>> plusList = new ArrayList<SubHyperplane<S>>();
        final ArrayList<SubHyperplane<S>> minusList = new ArrayList<SubHyperplane<S>>();
        while (iterator.hasNext()) {
            final SubHyperplane<S> other = iterator.next();
            switch (other.side(inserted)) {
                case PLUS: {
                    plusList.add(other);
                    continue;
                }
                case MINUS: {
                    minusList.add(other);
                    continue;
                }
                case BOTH: {
                    final SubHyperplane.SplitSubHyperplane<S> split = other.split(inserted);
                    plusList.add(split.getPlus());
                    minusList.add(split.getMinus());
                    continue;
                }
            }
        }
        this.insertCuts(node.getPlus(), plusList);
        this.insertCuts(node.getMinus(), minusList);
    }
    
    public AbstractRegion<S, T> copySelf() {
        return this.buildNew(this.tree.copySelf());
    }
    
    public boolean isEmpty() {
        return this.isEmpty(this.tree);
    }
    
    public boolean isEmpty(final BSPTree<S> node) {
        if (node.getCut() == null) {
            return !(boolean)node.getAttribute();
        }
        return this.isEmpty(node.getMinus()) && this.isEmpty(node.getPlus());
    }
    
    public boolean contains(final Region<S> region) {
        return new RegionFactory<S>().difference(region, this).isEmpty();
    }
    
    public Location checkPoint(final Vector<S> point) {
        return this.checkPoint(this.tree, point);
    }
    
    protected Location checkPoint(final BSPTree<S> node, final Vector<S> point) {
        final BSPTree<S> cell = node.getCell(point);
        if (cell.getCut() == null) {
            return cell.getAttribute() ? Location.INSIDE : Location.OUTSIDE;
        }
        final Location minusCode = this.checkPoint(cell.getMinus(), point);
        final Location plusCode = this.checkPoint(cell.getPlus(), point);
        return (minusCode == plusCode) ? minusCode : Location.BOUNDARY;
    }
    
    public BSPTree<S> getTree(final boolean includeBoundaryAttributes) {
        if (includeBoundaryAttributes && this.tree.getCut() != null && this.tree.getAttribute() == null) {
            this.tree.visit(new BoundaryBuilder<S>());
        }
        return this.tree;
    }
    
    public double getBoundarySize() {
        final BoundarySizeVisitor<S> visitor = new BoundarySizeVisitor<S>();
        this.getTree(true).visit(visitor);
        return visitor.getSize();
    }
    
    public double getSize() {
        if (this.barycenter == null) {
            this.computeGeometricalProperties();
        }
        return this.size;
    }
    
    protected void setSize(final double size) {
        this.size = size;
    }
    
    public Vector<S> getBarycenter() {
        if (this.barycenter == null) {
            this.computeGeometricalProperties();
        }
        return this.barycenter;
    }
    
    protected void setBarycenter(final Vector<S> barycenter) {
        this.barycenter = barycenter;
    }
    
    protected abstract void computeGeometricalProperties();
    
    public Side side(final Hyperplane<S> hyperplane) {
        final Sides sides = new Sides();
        this.recurseSides(this.tree, hyperplane.wholeHyperplane(), sides);
        return sides.plusFound() ? (sides.minusFound() ? Side.BOTH : Side.PLUS) : (sides.minusFound() ? Side.MINUS : Side.HYPER);
    }
    
    private void recurseSides(final BSPTree<S> node, final SubHyperplane<S> sub, final Sides sides) {
        if (node.getCut() == null) {
            if (node.getAttribute()) {
                sides.rememberPlusFound();
                sides.rememberMinusFound();
            }
            return;
        }
        final Hyperplane<S> hyperplane = node.getCut().getHyperplane();
        switch (sub.side(hyperplane)) {
            case PLUS: {
                if (node.getCut().side(sub.getHyperplane()) == Side.PLUS) {
                    if (!this.isEmpty(node.getMinus())) {
                        sides.rememberPlusFound();
                    }
                }
                else if (!this.isEmpty(node.getMinus())) {
                    sides.rememberMinusFound();
                }
                if (!sides.plusFound() || !sides.minusFound()) {
                    this.recurseSides(node.getPlus(), sub, sides);
                    break;
                }
                break;
            }
            case MINUS: {
                if (node.getCut().side(sub.getHyperplane()) == Side.PLUS) {
                    if (!this.isEmpty(node.getPlus())) {
                        sides.rememberPlusFound();
                    }
                }
                else if (!this.isEmpty(node.getPlus())) {
                    sides.rememberMinusFound();
                }
                if (!sides.plusFound() || !sides.minusFound()) {
                    this.recurseSides(node.getMinus(), sub, sides);
                    break;
                }
                break;
            }
            case BOTH: {
                final SubHyperplane.SplitSubHyperplane<S> split = sub.split(hyperplane);
                this.recurseSides(node.getPlus(), split.getPlus(), sides);
                if (!sides.plusFound() || !sides.minusFound()) {
                    this.recurseSides(node.getMinus(), split.getMinus(), sides);
                    break;
                }
                break;
            }
            default: {
                if (node.getCut().getHyperplane().sameOrientationAs(sub.getHyperplane())) {
                    if (node.getPlus().getCut() != null || (boolean)node.getPlus().getAttribute()) {
                        sides.rememberPlusFound();
                    }
                    if (node.getMinus().getCut() != null || (boolean)node.getMinus().getAttribute()) {
                        sides.rememberMinusFound();
                        break;
                    }
                    break;
                }
                else {
                    if (node.getPlus().getCut() != null || (boolean)node.getPlus().getAttribute()) {
                        sides.rememberMinusFound();
                    }
                    if (node.getMinus().getCut() != null || (boolean)node.getMinus().getAttribute()) {
                        sides.rememberPlusFound();
                        break;
                    }
                    break;
                }
                break;
            }
        }
    }
    
    public SubHyperplane<S> intersection(final SubHyperplane<S> sub) {
        return this.recurseIntersection(this.tree, sub);
    }
    
    private SubHyperplane<S> recurseIntersection(final BSPTree<S> node, final SubHyperplane<S> sub) {
        if (node.getCut() == null) {
            return node.getAttribute() ? sub.copySelf() : null;
        }
        final Hyperplane<S> hyperplane = node.getCut().getHyperplane();
        switch (sub.side(hyperplane)) {
            case PLUS: {
                return this.recurseIntersection(node.getPlus(), sub);
            }
            case MINUS: {
                return this.recurseIntersection(node.getMinus(), sub);
            }
            case BOTH: {
                final SubHyperplane.SplitSubHyperplane<S> split = sub.split(hyperplane);
                final SubHyperplane<S> plus = this.recurseIntersection(node.getPlus(), split.getPlus());
                final SubHyperplane<S> minus = this.recurseIntersection(node.getMinus(), split.getMinus());
                if (plus == null) {
                    return minus;
                }
                if (minus == null) {
                    return plus;
                }
                return plus.reunite(minus);
            }
            default: {
                return this.recurseIntersection(node.getPlus(), this.recurseIntersection(node.getMinus(), sub));
            }
        }
    }
    
    public AbstractRegion<S, T> applyTransform(final Transform<S, T> transform) {
        return this.buildNew(this.recurseTransform(this.getTree(false), transform));
    }
    
    private BSPTree<S> recurseTransform(final BSPTree<S> node, final Transform<S, T> transform) {
        if (node.getCut() == null) {
            return new BSPTree<S>(node.getAttribute());
        }
        final SubHyperplane<S> sub = node.getCut();
        final SubHyperplane<S> tSub = ((AbstractSubHyperplane)sub).applyTransform(transform);
        BoundaryAttribute<S> attribute = (BoundaryAttribute<S>)node.getAttribute();
        if (attribute != null) {
            final SubHyperplane<S> tPO = (attribute.getPlusOutside() == null) ? null : ((AbstractSubHyperplane)attribute.getPlusOutside()).applyTransform(transform);
            final SubHyperplane<S> tPI = (attribute.getPlusInside() == null) ? null : ((AbstractSubHyperplane)attribute.getPlusInside()).applyTransform(transform);
            attribute = new BoundaryAttribute<S>(tPO, tPI);
        }
        return new BSPTree<S>(tSub, this.recurseTransform(node.getPlus(), transform), this.recurseTransform(node.getMinus(), transform), attribute);
    }
    
    private static class BoundaryBuilder<S extends Space> implements BSPTreeVisitor<S>
    {
        public Order visitOrder(final BSPTree<S> node) {
            return Order.PLUS_MINUS_SUB;
        }
        
        public void visitInternalNode(final BSPTree<S> node) {
            SubHyperplane<S> plusOutside = null;
            SubHyperplane<S> plusInside = null;
            final SubHyperplane<S>[] plusChar = (SubHyperplane<S>[])Array.newInstance(SubHyperplane.class, 2);
            this.characterize(node.getPlus(), node.getCut().copySelf(), plusChar);
            if (plusChar[0] != null && !plusChar[0].isEmpty()) {
                final SubHyperplane<S>[] minusChar = (SubHyperplane<S>[])Array.newInstance(SubHyperplane.class, 2);
                this.characterize(node.getMinus(), plusChar[0], minusChar);
                if (minusChar[1] != null && !minusChar[1].isEmpty()) {
                    plusOutside = minusChar[1];
                }
            }
            if (plusChar[1] != null && !plusChar[1].isEmpty()) {
                final SubHyperplane<S>[] minusChar = (SubHyperplane<S>[])Array.newInstance(SubHyperplane.class, 2);
                this.characterize(node.getMinus(), plusChar[1], minusChar);
                if (minusChar[0] != null && !minusChar[0].isEmpty()) {
                    plusInside = minusChar[0];
                }
            }
            node.setAttribute(new BoundaryAttribute((SubHyperplane<Space>)plusOutside, (SubHyperplane<Space>)plusInside));
        }
        
        public void visitLeafNode(final BSPTree<S> node) {
        }
        
        private void characterize(final BSPTree<S> node, final SubHyperplane<S> sub, final SubHyperplane<S>[] characterization) {
            if (node.getCut() == null) {
                final boolean inside = (boolean)node.getAttribute();
                if (inside) {
                    if (characterization[1] == null) {
                        characterization[1] = sub;
                    }
                    else {
                        characterization[1] = characterization[1].reunite(sub);
                    }
                }
                else if (characterization[0] == null) {
                    characterization[0] = sub;
                }
                else {
                    characterization[0] = characterization[0].reunite(sub);
                }
            }
            else {
                final Hyperplane<S> hyperplane = node.getCut().getHyperplane();
                switch (sub.side(hyperplane)) {
                    case PLUS: {
                        this.characterize(node.getPlus(), sub, characterization);
                        break;
                    }
                    case MINUS: {
                        this.characterize(node.getMinus(), sub, characterization);
                        break;
                    }
                    case BOTH: {
                        final SubHyperplane.SplitSubHyperplane<S> split = sub.split(hyperplane);
                        this.characterize(node.getPlus(), split.getPlus(), characterization);
                        this.characterize(node.getMinus(), split.getMinus(), characterization);
                        break;
                    }
                    default: {
                        throw new MathInternalError();
                    }
                }
            }
        }
    }
    
    private static final class Sides
    {
        private boolean plusFound;
        private boolean minusFound;
        
        public Sides() {
            this.plusFound = false;
            this.minusFound = false;
        }
        
        public void rememberPlusFound() {
            this.plusFound = true;
        }
        
        public boolean plusFound() {
            return this.plusFound;
        }
        
        public void rememberMinusFound() {
            this.minusFound = true;
        }
        
        public boolean minusFound() {
            return this.minusFound;
        }
    }
}
