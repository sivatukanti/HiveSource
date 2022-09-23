// 
// Decompiled by Procyon v0.5.36
// 

package org.antlr.runtime;

public class Parser extends BaseRecognizer
{
    public TokenStream input;
    
    public Parser(final TokenStream input) {
        this.setTokenStream(input);
    }
    
    public Parser(final TokenStream input, final RecognizerSharedState state) {
        super(state);
        this.input = input;
    }
    
    public void reset() {
        super.reset();
        if (this.input != null) {
            this.input.seek(0);
        }
    }
    
    protected Object getCurrentInputSymbol(final IntStream input) {
        return ((TokenStream)input).LT(1);
    }
    
    protected Object getMissingSymbol(final IntStream input, final RecognitionException e, final int expectedTokenType, final BitSet follow) {
        String tokenText = null;
        if (expectedTokenType == -1) {
            tokenText = "<missing EOF>";
        }
        else {
            tokenText = "<missing " + this.getTokenNames()[expectedTokenType] + ">";
        }
        final CommonToken t = new CommonToken(expectedTokenType, tokenText);
        Token current = ((TokenStream)input).LT(1);
        if (current.getType() == -1) {
            current = ((TokenStream)input).LT(-1);
        }
        t.line = current.getLine();
        t.charPositionInLine = current.getCharPositionInLine();
        t.channel = 0;
        t.input = current.getInputStream();
        return t;
    }
    
    public void setTokenStream(final TokenStream input) {
        this.input = null;
        this.reset();
        this.input = input;
    }
    
    public TokenStream getTokenStream() {
        return this.input;
    }
    
    public String getSourceName() {
        return this.input.getSourceName();
    }
    
    public void traceIn(final String ruleName, final int ruleIndex) {
        super.traceIn(ruleName, ruleIndex, this.input.LT(1));
    }
    
    public void traceOut(final String ruleName, final int ruleIndex) {
        super.traceOut(ruleName, ruleIndex, this.input.LT(1));
    }
}
