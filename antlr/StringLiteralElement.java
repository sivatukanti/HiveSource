// 
// Decompiled by Procyon v0.5.36
// 

package antlr;

class StringLiteralElement extends GrammarAtom
{
    protected String processedAtomText;
    
    public StringLiteralElement(final Grammar grammar, final Token token, final int n) {
        super(grammar, token, n);
        if (!(grammar instanceof LexerGrammar)) {
            final TokenSymbol tokenSymbol = this.grammar.tokenManager.getTokenSymbol(this.atomText);
            if (tokenSymbol == null) {
                grammar.antlrTool.error("Undefined literal: " + this.atomText, this.grammar.getFilename(), token.getLine(), token.getColumn());
            }
            else {
                this.tokenType = tokenSymbol.getTokenType();
            }
        }
        this.line = token.getLine();
        this.processedAtomText = new String();
        for (int i = 1; i < this.atomText.length() - 1; ++i) {
            char c = this.atomText.charAt(i);
            if (c == '\\' && i + 1 < this.atomText.length() - 1) {
                ++i;
                c = this.atomText.charAt(i);
                switch (c) {
                    case 110: {
                        c = '\n';
                        break;
                    }
                    case 114: {
                        c = '\r';
                        break;
                    }
                    case 116: {
                        c = '\t';
                        break;
                    }
                }
            }
            if (grammar instanceof LexerGrammar) {
                ((LexerGrammar)grammar).charVocabulary.add(c);
            }
            this.processedAtomText += c;
        }
    }
    
    public void generate() {
        this.grammar.generator.gen(this);
    }
    
    public Lookahead look(final int n) {
        return this.grammar.theLLkAnalyzer.look(n, this);
    }
}
