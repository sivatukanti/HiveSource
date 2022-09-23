// 
// Decompiled by Procyon v0.5.36
// 

package antlr;

import antlr.collections.impl.BitSet;

public class ANTLRTokdefParser extends LLkParser implements ANTLRTokdefParserTokenTypes
{
    private Tool antlrTool;
    public static final String[] _tokenNames;
    public static final BitSet _tokenSet_0;
    public static final BitSet _tokenSet_1;
    
    public void setTool(final Tool antlrTool) {
        if (this.antlrTool == null) {
            this.antlrTool = antlrTool;
            return;
        }
        throw new IllegalStateException("antlr.Tool already registered");
    }
    
    protected Tool getTool() {
        return this.antlrTool;
    }
    
    public void reportError(final String s) {
        if (this.getTool() != null) {
            this.getTool().error(s, this.getFilename(), -1, -1);
        }
        else {
            super.reportError(s);
        }
    }
    
    public void reportError(final RecognitionException ex) {
        if (this.getTool() != null) {
            this.getTool().error(ex.getErrorMessage(), ex.getFilename(), ex.getLine(), ex.getColumn());
        }
        else {
            super.reportError(ex);
        }
    }
    
    public void reportWarning(final String s) {
        if (this.getTool() != null) {
            this.getTool().warning(s, this.getFilename(), -1, -1);
        }
        else {
            super.reportWarning(s);
        }
    }
    
    protected ANTLRTokdefParser(final TokenBuffer tokenBuffer, final int n) {
        super(tokenBuffer, n);
        this.tokenNames = ANTLRTokdefParser._tokenNames;
    }
    
    public ANTLRTokdefParser(final TokenBuffer tokenBuffer) {
        this(tokenBuffer, 3);
    }
    
    protected ANTLRTokdefParser(final TokenStream tokenStream, final int n) {
        super(tokenStream, n);
        this.tokenNames = ANTLRTokdefParser._tokenNames;
    }
    
    public ANTLRTokdefParser(final TokenStream tokenStream) {
        this(tokenStream, 3);
    }
    
    public ANTLRTokdefParser(final ParserSharedInputState parserSharedInputState) {
        super(parserSharedInputState, 3);
        this.tokenNames = ANTLRTokdefParser._tokenNames;
    }
    
    public final void file(final ImportVocabTokenManager importVocabTokenManager) throws RecognitionException, TokenStreamException {
        try {
            this.LT(1);
            this.match(4);
            while (this.LA(1) == 4 || this.LA(1) == 5) {
                this.line(importVocabTokenManager);
            }
        }
        catch (RecognitionException ex) {
            this.reportError(ex);
            this.consume();
            this.consumeUntil(ANTLRTokdefParser._tokenSet_0);
        }
    }
    
    public final void line(final ImportVocabTokenManager importVocabTokenManager) throws RecognitionException, TokenStreamException {
        Token lt = null;
        Token token = null;
        Token token2 = null;
        try {
            if (this.LA(1) == 5) {
                final Token lt2 = this.LT(1);
                this.match(5);
                token2 = lt2;
            }
            else if (this.LA(1) == 4 && this.LA(2) == 6 && this.LA(3) == 5) {
                final Token lt3 = this.LT(1);
                this.match(4);
                token = lt3;
                this.match(6);
                final Token lt4 = this.LT(1);
                this.match(5);
                token2 = lt4;
            }
            else if (this.LA(1) == 4 && this.LA(2) == 7) {
                final Token lt5 = this.LT(1);
                this.match(4);
                token = lt5;
                this.match(7);
                lt = this.LT(1);
                this.match(5);
                this.match(8);
            }
            else {
                if (this.LA(1) != 4 || this.LA(2) != 6 || this.LA(3) != 9) {
                    throw new NoViableAltException(this.LT(1), this.getFilename());
                }
                final Token lt6 = this.LT(1);
                this.match(4);
                token = lt6;
            }
            this.match(6);
            final Token lt7 = this.LT(1);
            this.match(9);
            final Integer value = Integer.valueOf(lt7.getText());
            if (token2 != null) {
                importVocabTokenManager.define(token2.getText(), value);
                if (token != null) {
                    final StringLiteralSymbol stringLiteralSymbol = (StringLiteralSymbol)importVocabTokenManager.getTokenSymbol(token2.getText());
                    stringLiteralSymbol.setLabel(token.getText());
                    importVocabTokenManager.mapToTokenSymbol(token.getText(), stringLiteralSymbol);
                }
            }
            else if (token != null) {
                importVocabTokenManager.define(token.getText(), value);
                if (lt != null) {
                    importVocabTokenManager.getTokenSymbol(token.getText()).setParaphrase(lt.getText());
                }
            }
        }
        catch (RecognitionException ex) {
            this.reportError(ex);
            this.consume();
            this.consumeUntil(ANTLRTokdefParser._tokenSet_1);
        }
    }
    
    private static final long[] mk_tokenSet_0() {
        return new long[] { 2L, 0L };
    }
    
    private static final long[] mk_tokenSet_1() {
        return new long[] { 50L, 0L };
    }
    
    static {
        _tokenNames = new String[] { "<0>", "EOF", "<2>", "NULL_TREE_LOOKAHEAD", "ID", "STRING", "ASSIGN", "LPAREN", "RPAREN", "INT", "WS", "SL_COMMENT", "ML_COMMENT", "ESC", "DIGIT", "XDIGIT" };
        _tokenSet_0 = new BitSet(mk_tokenSet_0());
        _tokenSet_1 = new BitSet(mk_tokenSet_1());
    }
}
