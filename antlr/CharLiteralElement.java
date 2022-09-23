// 
// Decompiled by Procyon v0.5.36
// 

package antlr;

class CharLiteralElement extends GrammarAtom
{
    public CharLiteralElement(final LexerGrammar lexerGrammar, final Token token, final boolean not, final int autoGenType) {
        super(lexerGrammar, token, 1);
        this.tokenType = ANTLRLexer.tokenTypeForCharLiteral(token.getText());
        lexerGrammar.charVocabulary.add(this.tokenType);
        this.line = token.getLine();
        this.not = not;
        this.autoGenType = autoGenType;
    }
    
    public void generate() {
        this.grammar.generator.gen(this);
    }
    
    public Lookahead look(final int n) {
        return this.grammar.theLLkAnalyzer.look(n, this);
    }
}
