// 
// Decompiled by Procyon v0.5.36
// 

package antlr;

public class CommonHiddenStreamToken extends CommonToken
{
    protected CommonHiddenStreamToken hiddenBefore;
    protected CommonHiddenStreamToken hiddenAfter;
    
    public CommonHiddenStreamToken() {
    }
    
    public CommonHiddenStreamToken(final int n, final String s) {
        super(n, s);
    }
    
    public CommonHiddenStreamToken(final String s) {
        super(s);
    }
    
    public CommonHiddenStreamToken getHiddenAfter() {
        return this.hiddenAfter;
    }
    
    public CommonHiddenStreamToken getHiddenBefore() {
        return this.hiddenBefore;
    }
    
    protected void setHiddenAfter(final CommonHiddenStreamToken hiddenAfter) {
        this.hiddenAfter = hiddenAfter;
    }
    
    protected void setHiddenBefore(final CommonHiddenStreamToken hiddenBefore) {
        this.hiddenBefore = hiddenBefore;
    }
}
