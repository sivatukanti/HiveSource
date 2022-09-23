// 
// Decompiled by Procyon v0.5.36
// 

package antlr;

class TokenRefElement extends GrammarAtom
{
    public TokenRefElement(final Grammar grammar, final Token token, final boolean not, final int n) {
        super(grammar, token, n);
        this.not = not;
        final TokenSymbol tokenSymbol = this.grammar.tokenManager.getTokenSymbol(this.atomText);
        if (tokenSymbol == null) {
            grammar.antlrTool.error("Undefined token symbol: " + this.atomText, this.grammar.getFilename(), token.getLine(), token.getColumn());
        }
        else {
            this.tokenType = tokenSymbol.getTokenType();
            this.setASTNodeType(tokenSymbol.getASTNodeType());
        }
        this.line = token.getLine();
    }
    
    public void generate() {
        this.grammar.generator.gen(this);
    }
    
    public Lookahead look(final int n) {
        return this.grammar.theLLkAnalyzer.look(n, this);
    }
}
