// 
// Decompiled by Procyon v0.5.36
// 

package antlr;

class TreeSpecifierNode
{
    private TreeSpecifierNode parent;
    private TreeSpecifierNode firstChild;
    private TreeSpecifierNode nextSibling;
    private Token tok;
    
    TreeSpecifierNode(final Token tok) {
        this.parent = null;
        this.firstChild = null;
        this.nextSibling = null;
        this.tok = tok;
    }
    
    public TreeSpecifierNode getFirstChild() {
        return this.firstChild;
    }
    
    public TreeSpecifierNode getNextSibling() {
        return this.nextSibling;
    }
    
    public TreeSpecifierNode getParent() {
        return this.parent;
    }
    
    public Token getToken() {
        return this.tok;
    }
    
    public void setFirstChild(final TreeSpecifierNode firstChild) {
        this.firstChild = firstChild;
        firstChild.parent = this;
    }
    
    public void setNextSibling(final TreeSpecifierNode nextSibling) {
        this.nextSibling = nextSibling;
        nextSibling.parent = this.parent;
    }
}
