// 
// Decompiled by Procyon v0.5.36
// 

package antlr;

class CharRangeElement extends AlternativeElement
{
    String label;
    protected char begin;
    protected char end;
    protected String beginText;
    protected String endText;
    
    public CharRangeElement(final LexerGrammar lexerGrammar, final Token token, final Token token2, final int autoGenType) {
        super(lexerGrammar);
        this.begin = '\0';
        this.end = '\0';
        this.begin = (char)ANTLRLexer.tokenTypeForCharLiteral(token.getText());
        this.beginText = token.getText();
        this.end = (char)ANTLRLexer.tokenTypeForCharLiteral(token2.getText());
        this.endText = token2.getText();
        this.line = token.getLine();
        for (char begin = this.begin; begin <= this.end; ++begin) {
            lexerGrammar.charVocabulary.add(begin);
        }
        this.autoGenType = autoGenType;
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
