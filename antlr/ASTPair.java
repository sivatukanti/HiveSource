// 
// Decompiled by Procyon v0.5.36
// 

package antlr;

import antlr.collections.AST;

public class ASTPair
{
    public AST root;
    public AST child;
    
    public final void advanceChildToEnd() {
        if (this.child != null) {
            while (this.child.getNextSibling() != null) {
                this.child = this.child.getNextSibling();
            }
        }
    }
    
    public ASTPair copy() {
        final ASTPair astPair = new ASTPair();
        astPair.root = this.root;
        astPair.child = this.child;
        return astPair;
    }
    
    public String toString() {
        return "[" + ((this.root == null) ? "null" : this.root.getText()) + "," + ((this.child == null) ? "null" : this.child.getText()) + "]";
    }
}
