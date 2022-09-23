// 
// Decompiled by Procyon v0.5.36
// 

package antlr;

class TokenRangeElement extends AlternativeElement
{
    String label;
    protected int begin;
    protected int end;
    protected String beginText;
    protected String endText;
    
    public TokenRangeElement(final Grammar grammar, final Token token, final Token token2, final int n) {
        super(grammar, token, n);
        this.begin = 0;
        this.end = 0;
        this.begin = this.grammar.tokenManager.getTokenSymbol(token.getText()).getTokenType();
        this.beginText = token.getText();
        this.end = this.grammar.tokenManager.getTokenSymbol(token2.getText()).getTokenType();
        this.endText = token2.getText();
        this.line = token.getLine();
    }
    
    public void generate() {
        this.grammar.generator.gen(this);
    }
    
    public String getLabel() {
        return this.label;
    }
    
    public Lookahead look(final int n) {
        return this.grammar.theLLkAnalyzer.look(n, this);
    }
    
    public void setLabel(final String label) {
        this.label = label;
    }
    
    public String toString() {
        if (this.label != null) {
            return " " + this.label + ":" + this.beginText + ".." + this.endText;
        }
        return " " + this.beginText + ".." + this.endText;
    }
}
