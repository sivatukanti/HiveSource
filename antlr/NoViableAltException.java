// 
// Decompiled by Procyon v0.5.36
// 

package antlr;

import antlr.collections.AST;

public class NoViableAltException extends RecognitionException
{
    public Token token;
    public AST node;
    
    public NoViableAltException(final AST node) {
        super("NoViableAlt", "<AST>", node.getLine(), node.getColumn());
        this.node = node;
    }
    
    public NoViableAltException(final Token token, final String s) {
        super("NoViableAlt", s, token.getLine(), token.getColumn());
        this.token = token;
    }
    
    public String getMessage() {
        if (this.token != null) {
            return "unexpected token: " + this.token.getText();
        }
        if (this.node == TreeParser.ASTNULL) {
            return "unexpected end of subtree";
        }
        return "unexpected AST node: " + this.node.toString();
    }
}
