// 
// Decompiled by Procyon v0.5.36
// 

package org.antlr.runtime.tree;

import java.util.HashMap;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.TokenStream;
import org.antlr.runtime.Token;
import java.util.Map;

public abstract class BaseTreeAdaptor implements TreeAdaptor
{
    protected Map treeToUniqueIDMap;
    protected int uniqueNodeID;
    
    public BaseTreeAdaptor() {
        this.uniqueNodeID = 1;
    }
    
    public Object nil() {
        return this.create(null);
    }
    
    public Object errorNode(final TokenStream input, final Token start, final Token stop, final RecognitionException e) {
        final CommonErrorNode t = new CommonErrorNode(input, start, stop, e);
        return t;
    }
    
    public boolean isNil(final Object tree) {
        return ((Tree)tree).isNil();
    }
    
    public Object dupTree(final Object tree) {
        return this.dupTree(tree, null);
    }
    
    public Object dupTree(final Object t, final Object parent) {
        if (t == null) {
            return null;
        }
        final Object newTree = this.dupNode(t);
        this.setChildIndex(newTree, this.getChildIndex(t));
        this.setParent(newTree, parent);
        for (int n = this.getChildCount(t), i = 0; i < n; ++i) {
            final Object child = this.getChild(t, i);
            final Object newSubTree = this.dupTree(child, t);
            this.addChild(newTree, newSubTree);
        }
        return newTree;
    }
    
    public void addChild(final Object t, final Object child) {
        if (t != null && child != null) {
            ((Tree)t).addChild((Tree)child);
        }
    }
    
    public Object becomeRoot(final Object newRoot, final Object oldRoot) {
        Tree newRootTree = (Tree)newRoot;
        final Tree oldRootTree = (Tree)oldRoot;
        if (oldRoot == null) {
            return newRoot;
        }
        if (newRootTree.isNil()) {
            final int nc = newRootTree.getChildCount();
            if (nc == 1) {
                newRootTree = newRootTree.getChild(0);
            }
            else if (nc > 1) {
                throw new RuntimeException("more than one node as root (TODO: make exception hierarchy)");
            }
        }
        newRootTree.addChild(oldRootTree);
        return newRootTree;
    }
    
    public Object rulePostProcessing(final Object root) {
        Tree r = (Tree)root;
        if (r != null && r.isNil()) {
            if (r.getChildCount() == 0) {
                r = null;
            }
            else if (r.getChildCount() == 1) {
                r = r.getChild(0);
                r.setParent(null);
                r.setChildIndex(-1);
            }
        }
        return r;
    }
    
    public Object becomeRoot(final Token newRoot, final Object oldRoot) {
        return this.becomeRoot(this.create(newRoot), oldRoot);
    }
    
    public Object create(final int tokenType, Token fromToken) {
        fromToken = this.createToken(fromToken);
        fromToken.setType(tokenType);
        final Tree t = (Tree)this.create(fromToken);
        return t;
    }
    
    public Object create(final int tokenType, Token fromToken, final String text) {
        if (fromToken == null) {
            return this.create(tokenType, text);
        }
        fromToken = this.createToken(fromToken);
        fromToken.setType(tokenType);
        fromToken.setText(text);
        final Tree t = (Tree)this.create(fromToken);
        return t;
    }
    
    public Object create(final int tokenType, final String text) {
        final Token fromToken = this.createToken(tokenType, text);
        final Tree t = (Tree)this.create(fromToken);
        return t;
    }
    
    public int getType(final Object t) {
        return ((Tree)t).getType();
    }
    
    public void setType(final Object t, final int type) {
        throw new NoSuchMethodError("don't know enough about Tree node");
    }
    
    public String getText(final Object t) {
        return ((Tree)t).getText();
    }
    
    public void setText(final Object t, final String text) {
        throw new NoSuchMethodError("don't know enough about Tree node");
    }
    
    public Object getChild(final Object t, final int i) {
        return ((Tree)t).getChild(i);
    }
    
    public void setChild(final Object t, final int i, final Object child) {
        ((Tree)t).setChild(i, (Tree)child);
    }
    
    public Object deleteChild(final Object t, final int i) {
        return ((Tree)t).deleteChild(i);
    }
    
    public int getChildCount(final Object t) {
        return ((Tree)t).getChildCount();
    }
    
    public int getUniqueID(final Object node) {
        if (this.treeToUniqueIDMap == null) {
            this.treeToUniqueIDMap = new HashMap();
        }
        final Integer prevID = this.treeToUniqueIDMap.get(node);
        if (prevID != null) {
            return prevID;
        }
        final int ID = this.uniqueNodeID;
        this.treeToUniqueIDMap.put(node, new Integer(ID));
        ++this.uniqueNodeID;
        return ID;
    }
    
    public abstract Token createToken(final int p0, final String p1);
    
    public abstract Token createToken(final Token p0);
}
