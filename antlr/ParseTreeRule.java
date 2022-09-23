// 
// Decompiled by Procyon v0.5.36
// 

package antlr;

import antlr.collections.AST;

public class ParseTreeRule extends ParseTree
{
    public static final int INVALID_ALT = -1;
    protected String ruleName;
    protected int altNumber;
    
    public ParseTreeRule(final String s) {
        this(s, -1);
    }
    
    public ParseTreeRule(final String ruleName, final int altNumber) {
        this.ruleName = ruleName;
        this.altNumber = altNumber;
    }
    
    public String getRuleName() {
        return this.ruleName;
    }
    
    protected int getLeftmostDerivation(final StringBuffer sb, final int n) {
        final int n2 = 0;
        if (n <= 0) {
            sb.append(' ');
            sb.append(this.toString());
            return n2;
        }
        AST ast = this.getFirstChild();
        int n3 = 1;
        while (ast != null) {
            if (n3 >= n || ast instanceof ParseTreeToken) {
                sb.append(' ');
                sb.append(ast.toString());
            }
            else {
                n3 += ((ParseTree)ast).getLeftmostDerivation(sb, n - n3);
            }
            ast = ast.getNextSibling();
        }
        return n3;
    }
    
    public String toString() {
        if (this.altNumber == -1) {
            return '<' + this.ruleName + '>';
        }
        return '<' + this.ruleName + "[" + this.altNumber + "]>";
    }
}
