// 
// Decompiled by Procyon v0.5.36
// 

package org.antlr.runtime.tree;

import org.antlr.runtime.Token;

public class CommonTree extends BaseTree
{
    public Token token;
    protected int startIndex;
    protected int stopIndex;
    public CommonTree parent;
    public int childIndex;
    
    public CommonTree() {
        this.startIndex = -1;
        this.stopIndex = -1;
        this.childIndex = -1;
    }
    
    public CommonTree(final CommonTree node) {
        super(node);
        this.startIndex = -1;
        this.stopIndex = -1;
        this.childIndex = -1;
        this.token = node.token;
        this.startIndex = node.startIndex;
        this.stopIndex = node.stopIndex;
    }
    
    public CommonTree(final Token t) {
        this.startIndex = -1;
        this.stopIndex = -1;
        this.childIndex = -1;
        this.token = t;
    }
    
    public Token getToken() {
        return this.token;
    }
    
    public Tree dupNode() {
        return new CommonTree(this);
    }
    
    public boolean isNil() {
        return this.token == null;
    }
    
    public int getType() {
        if (this.token == null) {
            return 0;
        }
        return this.token.getType();
    }
    
    public String getText() {
        if (this.token == null) {
            return null;
        }
        return this.token.getText();
    }
    
    public int getLine() {
        if (this.token != null && this.token.getLine() != 0) {
            return this.token.getLine();
        }
        if (this.getChildCount() > 0) {
            return this.getChild(0).getLine();
        }
        return 0;
    }
    
    public int getCharPositionInLine() {
        if (this.token != null && this.token.getCharPositionInLine() != -1) {
            return this.token.getCharPositionInLine();
        }
        if (this.getChildCount() > 0) {
            return this.getChild(0).getCharPositionInLine();
        }
        return 0;
    }
    
    public int getTokenStartIndex() {
        if (this.startIndex == -1 && this.token != null) {
            return this.token.getTokenIndex();
        }
        return this.startIndex;
    }
    
    public void setTokenStartIndex(final int index) {
        this.startIndex = index;
    }
    
    public int getTokenStopIndex() {
        if (this.stopIndex == -1 && this.token != null) {
            return this.token.getTokenIndex();
        }
        return this.stopIndex;
    }
    
    public void setTokenStopIndex(final int index) {
        this.stopIndex = index;
    }
    
    public void setUnknownTokenBoundaries() {
        if (this.children == null) {
            if (this.startIndex < 0 || this.stopIndex < 0) {
                final int tokenIndex = this.token.getTokenIndex();
                this.stopIndex = tokenIndex;
                this.startIndex = tokenIndex;
            }
            return;
        }
        for (int i = 0; i < this.children.size(); ++i) {
            this.children.get(i).setUnknownTokenBoundaries();
        }
        if (this.startIndex >= 0 && this.stopIndex >= 0) {
            return;
        }
        if (this.children.size() > 0) {
            final CommonTree firstChild = this.children.get(0);
            final CommonTree lastChild = this.children.get(this.children.size() - 1);
            this.startIndex = firstChild.getTokenStartIndex();
            this.stopIndex = lastChild.getTokenStopIndex();
        }
    }
    
    public int getChildIndex() {
        return this.childIndex;
    }
    
    public Tree getParent() {
        return this.parent;
    }
    
    public void setParent(final Tree t) {
        this.parent = (CommonTree)t;
    }
    
    public void setChildIndex(final int index) {
        this.childIndex = index;
    }
    
    public String toString() {
        if (this.isNil()) {
            return "nil";
        }
        if (this.getType() == 0) {
            return "<errornode>";
        }
        if (this.token == null) {
            return null;
        }
        return this.token.getText();
    }
}
