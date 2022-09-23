// 
// Decompiled by Procyon v0.5.36
// 

package antlr;

class TreeElement extends AlternativeBlock
{
    GrammarAtom root;
    
    public TreeElement(final Grammar grammar, final Token token) {
        super(grammar, token, false);
    }
    
    public void generate() {
        this.grammar.generator.gen(this);
    }
    
    public Lookahead look(final int n) {
        return this.grammar.theLLkAnalyzer.look(n, this);
    }
    
    public String toString() {
        String s = " #(" + this.root;
        for (AlternativeElement obj = ((Alternative)this.alternatives.elementAt(0)).head; obj != null; obj = obj.next) {
            s += obj;
        }
        return s + " )";
    }
}
