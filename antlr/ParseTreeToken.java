// 
// Decompiled by Procyon v0.5.36
// 

package antlr;

public class ParseTreeToken extends ParseTree
{
    protected Token token;
    
    public ParseTreeToken(final Token token) {
        this.token = token;
    }
    
    protected int getLeftmostDerivation(final StringBuffer sb, final int n) {
        sb.append(' ');
        sb.append(this.toString());
        return n;
    }
    
    public String toString() {
        if (this.token != null) {
            return this.token.getText();
        }
        return "<missing token>";
    }
}
