// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.math3.geometry.partitioning;

import org.apache.commons.math3.geometry.Space;

public class RegionFactory<S extends Space>
{
    private final NodesCleaner nodeCleaner;
    
    public RegionFactory() {
        this.nodeCleaner = new NodesCleaner();
    }
    
    public Region<S> buildConvex(final Hyperplane<S>... hyperplanes) {
        if (hyperplanes == null || hyperplanes.length == 0) {
            return null;
        }
        final Region<S> region = hyperplanes[0].wholeSpace();
        BSPTree<S> node = region.getTree(false);
        node.setAttribute(Boolean.TRUE);
        for (final Hyperplane<S> hyperplane : hyperplanes) {
            if (node.insertCut(hyperplane)) {
                node.setAttribute(null);
                node.getPlus().setAttribute(Boolean.FALSE);
                node = node.getMinus();
                node.setAttribute(Boolean.TRUE);
            }
        }
        return region;
    }
    
    public Region<S> union(final Region<S> region1, final Region<S> region2) {
        final BSPTree<S> tree = region1.getTree(false).merge(region2.getTree(false), new UnionMerger());
        tree.visit(this.nodeCleaner);
        return region1.buildNew(tree);
    }
    
    public Region<S> intersection(final Region<S> region1, final Region<S> region2) {
        final BSPTree<S> tree = region1.getTree(false).merge(region2.getTree(false), new IntersectionMerger());
        tree.visit(this.nodeCleaner);
        return region1.buildNew(tree);
    }
    
    public Region<S> xor(final Region<S> region1, final Region<S> region2) {
        final BSPTree<S> tree = region1.getTree(false).merge(region2.getTree(false), new XorMerger());
        tree.visit(this.nodeCleaner);
        return region1.buildNew(tree);
    }
    
    public Region<S> difference(final Region<S> region1, final Region<S> region2) {
        final BSPTree<S> tree = region1.getTree(false).merge(region2.getTree(false), new DifferenceMerger());
        tree.visit(this.nodeCleaner);
        return region1.buildNew(tree);
    }
    
    public Region<S> getComplement(final Region<S> region) {
        return region.buildNew(this.recurseComplement(region.getTree(false)));
    }
    
    private BSPTree<S> recurseComplement(final BSPTree<S> node) {
        if (node.getCut() == null) {
            return new BSPTree<S>(node.getAttribute() ? Boolean.FALSE : Boolean.TRUE);
        }
        BoundaryAttribute<S> attribute = (BoundaryAttribute<S>)node.getAttribute();
        if (attribute != null) {
            final SubHyperplane<S> plusOutside = (attribute.getPlusInside() == null) ? null : attribute.getPlusInside().copySelf();
            final SubHyperplane<S> plusInside = (attribute.getPlusOutside() == null) ? null : attribute.getPlusOutside().copySelf();
            attribute = new BoundaryAttribute<S>(plusOutside, plusInside);
        }
        return new BSPTree<S>(node.getCut().copySelf(), this.recurseComplement(node.getPlus()), this.recurseComplement(node.getMinus()), attribute);
    }
    
    private class UnionMerger implements BSPTree.LeafMerger<S>
    {
        public BSPTree<S> merge(final BSPTree<S> leaf, final BSPTree<S> tree, final BSPTree<S> parentTree, final boolean isPlusChild, final boolean leafFromInstance) {
            if (leaf.getAttribute()) {
                leaf.insertInTree(parentTree, isPlusChild);
                return leaf;
            }
            tree.insertInTree(parentTree, isPlusChild);
            return tree;
        }
    }
    
    private class IntersectionMerger implements BSPTree.LeafMerger<S>
    {
        public BSPTree<S> merge(final BSPTree<S> leaf, final BSPTree<S> tree, final BSPTree<S> parentTree, final boolean isPlusChild, final boolean leafFromInstance) {
            if (leaf.getAttribute()) {
                tree.insertInTree(parentTree, isPlusChild);
                return tree;
            }
            leaf.insertInTree(parentTree, isPlusChild);
            return leaf;
        }
    }
    
    private class XorMerger implements BSPTree.LeafMerger<S>
    {
        public BSPTree<S> merge(final BSPTree<S> leaf, final BSPTree<S> tree, final BSPTree<S> parentTree, final boolean isPlusChild, final boolean leafFromInstance) {
            BSPTree<S> t = tree;
            if (leaf.getAttribute()) {
                t = (BSPTree<S>)RegionFactory.this.recurseComplement(t);
            }
            t.insertInTree(parentTree, isPlusChild);
            return t;
        }
    }
    
    private class DifferenceMerger implements BSPTree.LeafMerger<S>
    {
        public BSPTree<S> merge(final BSPTree<S> leaf, final BSPTree<S> tree, final BSPTree<S> parentTree, final boolean isPlusChild, final boolean leafFromInstance) {
            if (leaf.getAttribute()) {
                final BSPTree<S> argTree = (BSPTree<S>)RegionFactory.this.recurseComplement(leafFromInstance ? tree : leaf);
                argTree.insertInTree(parentTree, isPlusChild);
                return argTree;
            }
            final BSPTree<S> instanceTree = leafFromInstance ? leaf : tree;
            instanceTree.insertInTree(parentTree, isPlusChild);
            return instanceTree;
        }
    }
    
    private class NodesCleaner implements BSPTreeVisitor<S>
    {
        public Order visitOrder(final BSPTree<S> node) {
            return Order.PLUS_SUB_MINUS;
        }
        
        public void visitInternalNode(final BSPTree<S> node) {
            node.setAttribute(null);
        }
        
        public void visitLeafNode(final BSPTree<S> node) {
        }
    }
}
