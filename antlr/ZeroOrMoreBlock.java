// 
// Decompiled by Procyon v0.5.36
// 

package antlr;

class ZeroOrMoreBlock extends BlockWithImpliedExitPath
{
    public ZeroOrMoreBlock(final Grammar grammar) {
        super(grammar);
    }
    
    public ZeroOrMoreBlock(final Grammar grammar, final Token token) {
        super(grammar, token);
    }
    
    public void generate() {
        this.grammar.generator.gen(this);
    }
    
    public Lookahead look(final int n) {
        return this.grammar.theLLkAnalyzer.look(n, this);
    }
    
    public String toString() {
        return super.toString() + "*";
    }
}
