// 
// Decompiled by Procyon v0.5.36
// 

package org.antlr.runtime;

import org.antlr.runtime.misc.LookaheadStream;

public class UnbufferedTokenStream extends LookaheadStream<Token> implements TokenStream
{
    protected TokenSource tokenSource;
    protected int tokenIndex;
    protected int channel;
    
    public UnbufferedTokenStream(final TokenSource tokenSource) {
        this.tokenIndex = 0;
        this.channel = 0;
        this.tokenSource = tokenSource;
    }
    
    public Token nextElement() {
        final Token t = this.tokenSource.nextToken();
        t.setTokenIndex(this.tokenIndex++);
        return t;
    }
    
    public boolean isEOF(final Token o) {
        return o.getType() == -1;
    }
    
    public TokenSource getTokenSource() {
        return this.tokenSource;
    }
    
    public String toString(final int start, final int stop) {
        return "n/a";
    }
    
    public String toString(final Token start, final Token stop) {
        return "n/a";
    }
    
    public int LA(final int i) {
        return this.LT(i).getType();
    }
    
    public Token get(final int i) {
        throw new UnsupportedOperationException("Absolute token indexes are meaningless in an unbuffered stream");
    }
    
    public String getSourceName() {
        return this.tokenSource.getSourceName();
    }
}
