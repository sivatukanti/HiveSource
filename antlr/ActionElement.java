// 
// Decompiled by Procyon v0.5.36
// 

package antlr;

class ActionElement extends AlternativeElement
{
    protected String actionText;
    protected boolean isSemPred;
    
    public ActionElement(final Grammar grammar, final Token token) {
        super(grammar);
        this.isSemPred = false;
        this.actionText = token.getText();
        this.line = token.getLine();
        this.column = token.getColumn();
    }
    
    public void generate() {
        this.grammar.generator.gen(this);
    }
    
    public Lookahead look(final int n) {
        return this.grammar.theLLkAnalyzer.look(n, this);
    }
    
    public String toString() {
        return " " + this.actionText + (this.isSemPred ? "?" : "");
    }
}
