// 
// Decompiled by Procyon v0.5.36
// 

package antlr;

class WildcardElement extends GrammarAtom
{
    protected String label;
    
    public WildcardElement(final Grammar grammar, final Token token, final int n) {
        super(grammar, token, n);
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
        String string = " ";
        if (this.label != null) {
            string = string + this.label + ":";
        }
        return string + ".";
    }
}
