// 
// Decompiled by Procyon v0.5.36
// 

package antlr;

class RuleEndElement extends BlockEndElement
{
    protected Lookahead[] cache;
    protected boolean noFOLLOW;
    
    public RuleEndElement(final Grammar grammar) {
        super(grammar);
        this.cache = new Lookahead[grammar.maxk + 1];
    }
    
    public Lookahead look(final int n) {
        return this.grammar.theLLkAnalyzer.look(n, this);
    }
    
    public String toString() {
        return "";
    }
}
