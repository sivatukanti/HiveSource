// 
// Decompiled by Procyon v0.5.36
// 

package antlr;

import antlr.collections.AST;

public abstract class ParseTree extends BaseAST
{
    public String getLeftmostDerivationStep(final int n) {
        if (n <= 0) {
            return this.toString();
        }
        final StringBuffer sb = new StringBuffer(2000);
        this.getLeftmostDerivation(sb, n);
        return sb.toString();
    }
    
    public String getLeftmostDerivation(final int n) {
        final StringBuffer sb = new StringBuffer(2000);
        sb.append("    " + this.toString());
        sb.append("\n");
        for (int i = 1; i < n; ++i) {
            sb.append(" =>");
            sb.append(this.getLeftmostDerivationStep(i));
            sb.append("\n");
        }
        return sb.toString();
    }
    
    protected abstract int getLeftmostDerivation(final StringBuffer p0, final int p1);
    
    public void initialize(final int n, final String s) {
    }
    
    public void initialize(final AST ast) {
    }
    
    public void initialize(final Token token) {
    }
}
