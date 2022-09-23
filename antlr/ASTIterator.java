// 
// Decompiled by Procyon v0.5.36
// 

package antlr;

import antlr.collections.AST;

public class ASTIterator
{
    protected AST cursor;
    protected AST original;
    
    public ASTIterator(final AST ast) {
        this.cursor = null;
        this.original = null;
        this.cursor = ast;
        this.original = ast;
    }
    
    public boolean isSubtree(final AST ast, AST nextSibling) {
        if (nextSibling == null) {
            return true;
        }
        if (ast == null) {
            return nextSibling == null;
        }
        for (AST nextSibling2 = ast; nextSibling2 != null && nextSibling != null; nextSibling2 = nextSibling2.getNextSibling(), nextSibling = nextSibling.getNextSibling()) {
            if (nextSibling2.getType() != nextSibling.getType()) {
                return false;
            }
            if (nextSibling2.getFirstChild() != null && !this.isSubtree(nextSibling2.getFirstChild(), nextSibling.getFirstChild())) {
                return false;
            }
        }
        return true;
    }
    
    public AST next(final AST ast) {
        final AST ast2 = null;
        if (this.cursor == null) {
            return null;
        }
        while (this.cursor != null) {
            if (this.cursor.getType() == ast.getType() && this.cursor.getFirstChild() != null && this.isSubtree(this.cursor.getFirstChild(), ast.getFirstChild())) {
                return this.cursor;
            }
            this.cursor = this.cursor.getNextSibling();
        }
        return ast2;
    }
}
