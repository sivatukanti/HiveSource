// 
// Decompiled by Procyon v0.5.36
// 

package antlr;

class TokenSymbol extends GrammarSymbol
{
    protected int ttype;
    protected String paraphrase;
    protected String ASTNodeType;
    
    public TokenSymbol(final String s) {
        super(s);
        this.paraphrase = null;
        this.ttype = 0;
    }
    
    public String getASTNodeType() {
        return this.ASTNodeType;
    }
    
    public void setASTNodeType(final String astNodeType) {
        this.ASTNodeType = astNodeType;
    }
    
    public String getParaphrase() {
        return this.paraphrase;
    }
    
    public int getTokenType() {
        return this.ttype;
    }
    
    public void setParaphrase(final String paraphrase) {
        this.paraphrase = paraphrase;
    }
    
    public void setTokenType(final int ttype) {
        this.ttype = ttype;
    }
}
