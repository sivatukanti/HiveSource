// 
// Decompiled by Procyon v0.5.36
// 

package antlr;

public class TokenWithIndex extends CommonToken
{
    int index;
    
    public TokenWithIndex() {
    }
    
    public TokenWithIndex(final int n, final String s) {
        super(n, s);
    }
    
    public void setIndex(final int index) {
        this.index = index;
    }
    
    public int getIndex() {
        return this.index;
    }
    
    public String toString() {
        return "[" + this.index + ":\"" + this.getText() + "\",<" + this.getType() + ">,line=" + this.line + ",col=" + this.col + "]\n";
    }
}
