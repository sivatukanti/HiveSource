// 
// Decompiled by Procyon v0.5.36
// 

package antlr;

abstract class BlockWithImpliedExitPath extends AlternativeBlock
{
    protected int exitLookaheadDepth;
    protected Lookahead[] exitCache;
    
    public BlockWithImpliedExitPath(final Grammar grammar) {
        super(grammar);
        this.exitCache = new Lookahead[this.grammar.maxk + 1];
    }
    
    public BlockWithImpliedExitPath(final Grammar grammar, final Token token) {
        super(grammar, token, false);
        this.exitCache = new Lookahead[this.grammar.maxk + 1];
    }
}
