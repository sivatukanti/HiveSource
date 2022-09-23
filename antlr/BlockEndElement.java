// 
// Decompiled by Procyon v0.5.36
// 

package antlr;

class BlockEndElement extends AlternativeElement
{
    protected boolean[] lock;
    protected AlternativeBlock block;
    
    public BlockEndElement(final Grammar grammar) {
        super(grammar);
        this.lock = new boolean[grammar.maxk + 1];
    }
    
    public Lookahead look(final int n) {
        return this.grammar.theLLkAnalyzer.look(n, this);
    }
    
    public String toString() {
        return "";
    }
}
