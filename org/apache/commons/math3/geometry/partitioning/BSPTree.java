// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.math3.geometry.partitioning;

import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.geometry.Vector;
import org.apache.commons.math3.exception.MathInternalError;
import org.apache.commons.math3.geometry.Space;

public class BSPTree<S extends Space>
{
    private SubHyperplane<S> cut;
    private BSPTree<S> plus;
    private BSPTree<S> minus;
    private BSPTree<S> parent;
    private Object attribute;
    
    public BSPTree() {
        this.cut = null;
        this.plus = null;
        this.minus = null;
        this.parent = null;
        this.attribute = null;
    }
    
    public BSPTree(final Object attribute) {
        this.cut = null;
        this.plus = null;
        this.minus = null;
        this.parent = null;
        this.attribute = attribute;
    }
    
    public BSPTree(final SubHyperplane<S> cut, final BSPTree<S> plus, final BSPTree<S> minus, final Object attribute) {
        this.cut = cut;
        this.plus = plus;
        this.minus = minus;
        this.parent = null;
        this.attribute = attribute;
        plus.parent = this;
        minus.parent = this;
    }
    
    public boolean insertCut(final Hyperplane<S> hyperplane) {
        if (this.cut != null) {
            this.plus.parent = null;
            this.minus.parent = null;
        }
        final SubHyperplane<S> chopped = this.fitToCell(hyperplane.wholeHyperplane());
        if (chopped == null || chopped.isEmpty()) {
            this.cut = null;
            this.plus = null;
            this.minus = null;
            return false;
        }
        this.cut = chopped;
        this.plus = new BSPTree<S>();
        this.plus.parent = this;
        this.minus = new BSPTree<S>();
        this.minus.parent = this;
        return true;
    }
    
    public BSPTree<S> copySelf() {
        if (this.cut == null) {
            return new BSPTree<S>(this.attribute);
        }
        return new BSPTree<S>(this.cut.copySelf(), this.plus.copySelf(), this.minus.copySelf(), this.attribute);
    }
    
    public SubHyperplane<S> getCut() {
        return this.cut;
    }
    
    public BSPTree<S> getPlus() {
        return this.plus;
    }
    
    public BSPTree<S> getMinus() {
        return this.minus;
    }
    
    public BSPTree<S> getParent() {
        return this.parent;
    }
    
    public void setAttribute(final Object attribute) {
        this.attribute = attribute;
    }
    
    public Object getAttribute() {
        return this.attribute;
    }
    
    public void visit(final BSPTreeVisitor<S> visitor) {
        if (this.cut == null) {
            visitor.visitLeafNode(this);
        }
        else {
            switch (visitor.visitOrder(this)) {
                case PLUS_MINUS_SUB: {
                    this.plus.visit(visitor);
                    this.minus.visit(visitor);
                    visitor.visitInternalNode(this);
                    break;
                }
                case PLUS_SUB_MINUS: {
                    this.plus.visit(visitor);
                    visitor.visitInternalNode(this);
                    this.minus.visit(visitor);
                    break;
                }
                case MINUS_PLUS_SUB: {
                    this.minus.visit(visitor);
                    this.plus.visit(visitor);
                    visitor.visitInternalNode(this);
                    break;
                }
                case MINUS_SUB_PLUS: {
                    this.minus.visit(visitor);
                    visitor.visitInternalNode(this);
                    this.plus.visit(visitor);
                    break;
                }
                case SUB_PLUS_MINUS: {
                    visitor.visitInternalNode(this);
                    this.plus.visit(visitor);
                    this.minus.visit(visitor);
                    break;
                }
                case SUB_MINUS_PLUS: {
                    visitor.visitInternalNode(this);
                    this.minus.visit(visitor);
                    this.plus.visit(visitor);
                    break;
                }
                default: {
                    throw new MathInternalError();
                }
            }
        }
    }
    
    private SubHyperplane<S> fitToCell(final SubHyperplane<S> sub) {
        SubHyperplane<S> s = sub;
        for (BSPTree<S> tree = this; tree.parent != null; tree = tree.parent) {
            if (tree == tree.parent.plus) {
                s = s.split(tree.parent.cut.getHyperplane()).getPlus();
            }
            else {
                s = s.split(tree.parent.cut.getHyperplane()).getMinus();
            }
        }
        return s;
    }
    
    public BSPTree<S> getCell(final Vector<S> point) {
        if (this.cut == null) {
            return this;
        }
        final double offset = this.cut.getHyperplane().getOffset(point);
        if (FastMath.abs(offset) < 1.0E-10) {
            return this;
        }
        if (offset <= 0.0) {
            return this.minus.getCell(point);
        }
        return this.plus.getCell(point);
    }
    
    private void condense() {
        if (this.cut != null && this.plus.cut == null && this.minus.cut == null && ((this.plus.attribute == null && this.minus.attribute == null) || (this.plus.attribute != null && this.plus.attribute.equals(this.minus.attribute)))) {
            this.attribute = ((this.plus.attribute == null) ? this.minus.attribute : this.plus.attribute);
            this.cut = null;
            this.plus = null;
            this.minus = null;
        }
    }
    
    public BSPTree<S> merge(final BSPTree<S> tree, final LeafMerger<S> leafMerger) {
        return this.merge(tree, leafMerger, null, false);
    }
    
    private BSPTree<S> merge(final BSPTree<S> tree, final LeafMerger<S> leafMerger, final BSPTree<S> parentTree, final boolean isPlusChild) {
        if (this.cut == null) {
            return leafMerger.merge(this, tree, parentTree, isPlusChild, true);
        }
        if (tree.cut == null) {
            return leafMerger.merge(tree, this, parentTree, isPlusChild, false);
        }
        final BSPTree<S> merged = tree.split(this.cut);
        if (parentTree != null) {
            merged.parent = parentTree;
            if (isPlusChild) {
                parentTree.plus = merged;
            }
            else {
                parentTree.minus = merged;
            }
        }
        this.plus.merge(merged.plus, leafMerger, merged, true);
        this.minus.merge(merged.minus, leafMerger, merged, false);
        merged.condense();
        if (merged.cut != null) {
            merged.cut = merged.fitToCell(merged.cut.getHyperplane().wholeHyperplane());
        }
        return merged;
    }
    
    public BSPTree<S> split(final SubHyperplane<S> sub) {
        if (this.cut == null) {
            return new BSPTree<S>(sub, this.copySelf(), new BSPTree<S>(this.attribute), null);
        }
        final Hyperplane<S> cHyperplane = this.cut.getHyperplane();
        final Hyperplane<S> sHyperplane = sub.getHyperplane();
        switch (sub.side(cHyperplane)) {
            case PLUS: {
                final BSPTree<S> split = this.plus.split(sub);
                if (this.cut.side(sHyperplane) == Side.PLUS) {
                    (split.plus = new BSPTree<S>(this.cut.copySelf(), split.plus, this.minus.copySelf(), this.attribute)).condense();
                    split.plus.parent = split;
                }
                else {
                    (split.minus = new BSPTree<S>(this.cut.copySelf(), split.minus, this.minus.copySelf(), this.attribute)).condense();
                    split.minus.parent = split;
                }
                return split;
            }
            case MINUS: {
                final BSPTree<S> split = this.minus.split(sub);
                if (this.cut.side(sHyperplane) == Side.PLUS) {
                    (split.plus = new BSPTree<S>(this.cut.copySelf(), this.plus.copySelf(), split.plus, this.attribute)).condense();
                    split.plus.parent = split;
                }
                else {
                    (split.minus = new BSPTree<S>(this.cut.copySelf(), this.plus.copySelf(), split.minus, this.attribute)).condense();
                    split.minus.parent = split;
                }
                return split;
            }
            case BOTH: {
                final SubHyperplane.SplitSubHyperplane<S> cutParts = this.cut.split(sHyperplane);
                final SubHyperplane.SplitSubHyperplane<S> subParts = sub.split(cHyperplane);
                final BSPTree<S> split2 = new BSPTree<S>(sub, this.plus.split(subParts.getPlus()), this.minus.split(subParts.getMinus()), null);
                split2.plus.cut = cutParts.getPlus();
                split2.minus.cut = cutParts.getMinus();
                final BSPTree<S> tmp = split2.plus.minus;
                split2.plus.minus = split2.minus.plus;
                split2.plus.minus.parent = split2.plus;
                split2.minus.plus = tmp;
                split2.minus.plus.parent = split2.minus;
                split2.plus.condense();
                split2.minus.condense();
                return split2;
            }
            default: {
                return cHyperplane.sameOrientationAs(sHyperplane) ? new BSPTree<S>(sub, this.plus.copySelf(), this.minus.copySelf(), this.attribute) : new BSPTree<S>(sub, this.minus.copySelf(), this.plus.copySelf(), this.attribute);
            }
        }
    }
    
    public void insertInTree(final BSPTree<S> parentTree, final boolean isPlusChild) {
        this.parent = parentTree;
        if (parentTree != null) {
            if (isPlusChild) {
                parentTree.plus = this;
            }
            else {
                parentTree.minus = this;
            }
        }
        if (this.cut != null) {
            for (BSPTree<S> tree = this; tree.parent != null; tree = tree.parent) {
                final Hyperplane<S> hyperplane = tree.parent.cut.getHyperplane();
                if (tree == tree.parent.plus) {
                    this.cut = this.cut.split(hyperplane).getPlus();
                    this.plus.chopOffMinus(hyperplane);
                    this.minus.chopOffMinus(hyperplane);
                }
                else {
                    this.cut = this.cut.split(hyperplane).getMinus();
                    this.plus.chopOffPlus(hyperplane);
                    this.minus.chopOffPlus(hyperplane);
                }
            }
            this.condense();
        }
    }
    
    private void chopOffMinus(final Hyperplane<S> hyperplane) {
        if (this.cut != null) {
            this.cut = this.cut.split(hyperplane).getPlus();
            this.plus.chopOffMinus(hyperplane);
            this.minus.chopOffMinus(hyperplane);
        }
    }
    
    private void chopOffPlus(final Hyperplane<S> hyperplane) {
        if (this.cut != null) {
            this.cut = this.cut.split(hyperplane).getMinus();
            this.plus.chopOffPlus(hyperplane);
            this.minus.chopOffPlus(hyperplane);
        }
    }
    
    public interface LeafMerger<S extends Space>
    {
        BSPTree<S> merge(final BSPTree<S> p0, final BSPTree<S> p1, final BSPTree<S> p2, final boolean p3, final boolean p4);
    }
}
