// 
// Decompiled by Procyon v0.5.36
// 

package org.antlr.runtime.tree;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseTree implements Tree
{
    protected List children;
    
    public BaseTree() {
    }
    
    public BaseTree(final Tree node) {
    }
    
    public Tree getChild(final int i) {
        if (this.children == null || i >= this.children.size()) {
            return null;
        }
        return this.children.get(i);
    }
    
    public List getChildren() {
        return this.children;
    }
    
    public Tree getFirstChildWithType(final int type) {
        for (int i = 0; this.children != null && i < this.children.size(); ++i) {
            final Tree t = this.children.get(i);
            if (t.getType() == type) {
                return t;
            }
        }
        return null;
    }
    
    public int getChildCount() {
        if (this.children == null) {
            return 0;
        }
        return this.children.size();
    }
    
    public void addChild(final Tree t) {
        if (t == null) {
            return;
        }
        final BaseTree childTree = (BaseTree)t;
        if (childTree.isNil()) {
            if (this.children != null && this.children == childTree.children) {
                throw new RuntimeException("attempt to add child list to itself");
            }
            if (childTree.children != null) {
                if (this.children != null) {
                    for (int n = childTree.children.size(), i = 0; i < n; ++i) {
                        final Tree c = childTree.children.get(i);
                        this.children.add(c);
                        c.setParent(this);
                        c.setChildIndex(this.children.size() - 1);
                    }
                }
                else {
                    this.children = childTree.children;
                    this.freshenParentAndChildIndexes();
                }
            }
        }
        else {
            if (this.children == null) {
                this.children = this.createChildrenList();
            }
            this.children.add(t);
            childTree.setParent(this);
            childTree.setChildIndex(this.children.size() - 1);
        }
    }
    
    public void addChildren(final List kids) {
        for (int i = 0; i < kids.size(); ++i) {
            final Tree t = kids.get(i);
            this.addChild(t);
        }
    }
    
    public void setChild(final int i, final Tree t) {
        if (t == null) {
            return;
        }
        if (t.isNil()) {
            throw new IllegalArgumentException("Can't set single child to a list");
        }
        if (this.children == null) {
            this.children = this.createChildrenList();
        }
        this.children.set(i, t);
        t.setParent(this);
        t.setChildIndex(i);
    }
    
    public void insertChild(final int i, final Object t) {
        if (this.children == null) {
            return;
        }
        this.children.add(i, t);
        this.freshenParentAndChildIndexes(i);
    }
    
    public Object deleteChild(final int i) {
        if (this.children == null) {
            return null;
        }
        final Tree killed = this.children.remove(i);
        this.freshenParentAndChildIndexes(i);
        return killed;
    }
    
    public void replaceChildren(final int startChildIndex, final int stopChildIndex, final Object t) {
        if (this.children == null) {
            throw new IllegalArgumentException("indexes invalid; no children in list");
        }
        final int replacingHowMany = stopChildIndex - startChildIndex + 1;
        final BaseTree newTree = (BaseTree)t;
        List newChildren = null;
        if (newTree.isNil()) {
            newChildren = newTree.children;
        }
        else {
            newChildren = new ArrayList(1);
            newChildren.add(newTree);
        }
        final int replacingWithHowMany = newChildren.size();
        final int numNewChildren = newChildren.size();
        final int delta = replacingHowMany - replacingWithHowMany;
        if (delta == 0) {
            int j = 0;
            for (int i = startChildIndex; i <= stopChildIndex; ++i) {
                final BaseTree child = newChildren.get(j);
                this.children.set(i, child);
                child.setParent(this);
                child.setChildIndex(i);
                ++j;
            }
        }
        else if (delta > 0) {
            for (int j = 0; j < numNewChildren; ++j) {
                this.children.set(startChildIndex + j, newChildren.get(j));
            }
            int c;
            for (int indexToDelete = c = startChildIndex + numNewChildren; c <= stopChildIndex; ++c) {
                this.children.remove(indexToDelete);
            }
            this.freshenParentAndChildIndexes(startChildIndex);
        }
        else {
            for (int j = 0; j < replacingHowMany; ++j) {
                this.children.set(startChildIndex + j, newChildren.get(j));
            }
            final int numToInsert = replacingWithHowMany - replacingHowMany;
            for (int k = replacingHowMany; k < replacingWithHowMany; ++k) {
                this.children.add(startChildIndex + k, newChildren.get(k));
            }
            this.freshenParentAndChildIndexes(startChildIndex);
        }
    }
    
    protected List createChildrenList() {
        return new ArrayList();
    }
    
    public boolean isNil() {
        return false;
    }
    
    public void freshenParentAndChildIndexes() {
        this.freshenParentAndChildIndexes(0);
    }
    
    public void freshenParentAndChildIndexes(final int offset) {
        for (int n = this.getChildCount(), c = offset; c < n; ++c) {
            final Tree child = this.getChild(c);
            child.setChildIndex(c);
            child.setParent(this);
        }
    }
    
    public void freshenParentAndChildIndexesDeeply() {
        this.freshenParentAndChildIndexesDeeply(0);
    }
    
    public void freshenParentAndChildIndexesDeeply(final int offset) {
        for (int n = this.getChildCount(), c = offset; c < n; ++c) {
            final BaseTree child = (BaseTree)this.getChild(c);
            child.setChildIndex(c);
            child.setParent(this);
            child.freshenParentAndChildIndexesDeeply();
        }
    }
    
    public void sanityCheckParentAndChildIndexes() {
        this.sanityCheckParentAndChildIndexes(null, -1);
    }
    
    public void sanityCheckParentAndChildIndexes(final Tree parent, final int i) {
        if (parent != this.getParent()) {
            throw new IllegalStateException("parents don't match; expected " + parent + " found " + this.getParent());
        }
        if (i != this.getChildIndex()) {
            throw new IllegalStateException("child indexes don't match; expected " + i + " found " + this.getChildIndex());
        }
        for (int n = this.getChildCount(), c = 0; c < n; ++c) {
            final CommonTree child = (CommonTree)this.getChild(c);
            child.sanityCheckParentAndChildIndexes(this, c);
        }
    }
    
    public int getChildIndex() {
        return 0;
    }
    
    public void setChildIndex(final int index) {
    }
    
    public Tree getParent() {
        return null;
    }
    
    public void setParent(final Tree t) {
    }
    
    public boolean hasAncestor(final int ttype) {
        return this.getAncestor(ttype) != null;
    }
    
    public Tree getAncestor(final int ttype) {
        Tree t;
        for (t = this, t = t.getParent(); t != null; t = t.getParent()) {
            if (t.getType() == ttype) {
                return t;
            }
        }
        return null;
    }
    
    public List getAncestors() {
        if (this.getParent() == null) {
            return null;
        }
        final List ancestors = new ArrayList();
        Tree t;
        for (t = this, t = t.getParent(); t != null; t = t.getParent()) {
            ancestors.add(0, t);
        }
        return ancestors;
    }
    
    public String toStringTree() {
        if (this.children == null || this.children.size() == 0) {
            return this.toString();
        }
        final StringBuffer buf = new StringBuffer();
        if (!this.isNil()) {
            buf.append("(");
            buf.append(this.toString());
            buf.append(' ');
        }
        for (int i = 0; this.children != null && i < this.children.size(); ++i) {
            final Tree t = this.children.get(i);
            if (i > 0) {
                buf.append(' ');
            }
            buf.append(t.toStringTree());
        }
        if (!this.isNil()) {
            buf.append(")");
        }
        return buf.toString();
    }
    
    public int getLine() {
        return 0;
    }
    
    public int getCharPositionInLine() {
        return 0;
    }
    
    public abstract String toString();
}
