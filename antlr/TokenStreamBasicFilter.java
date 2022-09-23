// 
// Decompiled by Procyon v0.5.36
// 

package antlr;

import antlr.ASdebug.TokenOffsetInfo;
import antlr.ASdebug.ASDebugStream;
import antlr.collections.impl.BitSet;
import antlr.ASdebug.IASDebugStream;

public class TokenStreamBasicFilter implements TokenStream, IASDebugStream
{
    protected BitSet discardMask;
    protected TokenStream input;
    
    public TokenStreamBasicFilter(final TokenStream input) {
        this.input = input;
        this.discardMask = new BitSet();
    }
    
    public void discard(final int n) {
        this.discardMask.add(n);
    }
    
    public void discard(final BitSet discardMask) {
        this.discardMask = discardMask;
    }
    
    public Token nextToken() throws TokenStreamException {
        Token token;
        for (token = this.input.nextToken(); token != null && this.discardMask.member(token.getType()); token = this.input.nextToken()) {}
        return token;
    }
    
    public String getEntireText() {
        return ASDebugStream.getEntireText(this.input);
    }
    
    public TokenOffsetInfo getOffsetInfo(final Token token) {
        return ASDebugStream.getOffsetInfo(this.input, token);
    }
}
