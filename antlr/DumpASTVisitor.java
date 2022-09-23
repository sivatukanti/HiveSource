// 
// Decompiled by Procyon v0.5.36
// 

package antlr;

import antlr.collections.AST;

public class DumpASTVisitor implements ASTVisitor
{
    protected int level;
    
    public DumpASTVisitor() {
        this.level = 0;
    }
    
    private void tabs() {
        for (int i = 0; i < this.level; ++i) {
            System.out.print("   ");
        }
    }
    
    public void visit(final AST ast) {
        boolean b = false;
        for (AST nextSibling = ast; nextSibling != null; nextSibling = nextSibling.getNextSibling()) {
            if (nextSibling.getFirstChild() != null) {
                b = false;
                break;
            }
        }
        for (AST nextSibling2 = ast; nextSibling2 != null; nextSibling2 = nextSibling2.getNextSibling()) {
            if (!b || nextSibling2 == ast) {
                this.tabs();
            }
            if (nextSibling2.getText() == null) {
                System.out.print("nil");
            }
            else {
                System.out.print(nextSibling2.getText());
            }
            System.out.print(" [" + nextSibling2.getType() + "] ");
            if (b) {
                System.out.print(" ");
            }
            else {
                System.out.println("");
            }
            if (nextSibling2.getFirstChild() != null) {
                ++this.level;
                this.visit(nextSibling2.getFirstChild());
                --this.level;
            }
        }
        if (b) {
            System.out.println("");
        }
    }
}
