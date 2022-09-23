// 
// Decompiled by Procyon v0.5.36
// 

package antlr;

import antlr.collections.impl.BitSet;

public class TokenStreamHiddenTokenFilter extends TokenStreamBasicFilter implements TokenStream
{
    protected BitSet hideMask;
    protected CommonHiddenStreamToken nextMonitoredToken;
    protected CommonHiddenStreamToken lastHiddenToken;
    protected CommonHiddenStreamToken firstHidden;
    
    public TokenStreamHiddenTokenFilter(final TokenStream tokenStream) {
        super(tokenStream);
        this.firstHidden = null;
        this.hideMask = new BitSet();
    }
    
    protected void consume() throws TokenStreamException {
        this.nextMonitoredToken = (CommonHiddenStreamToken)this.input.nextToken();
    }
    
    private void consumeFirst() throws TokenStreamException {
        this.consume();
        CommonHiddenStreamToken firstHidden = null;
        while (this.hideMask.member(this.LA(1).getType()) || this.discardMask.member(this.LA(1).getType())) {
            if (this.hideMask.member(this.LA(1).getType())) {
                if (firstHidden == null) {
                    firstHidden = this.LA(1);
                }
                else {
                    firstHidden.setHiddenAfter(this.LA(1));
                    this.LA(1).setHiddenBefore(firstHidden);
                    firstHidden = this.LA(1);
                }
                this.lastHiddenToken = firstHidden;
                if (this.firstHidden == null) {
                    this.firstHidden = firstHidden;
                }
            }
            this.consume();
        }
    }
    
    public BitSet getDiscardMask() {
        return this.discardMask;
    }
    
    public CommonHiddenStreamToken getHiddenAfter(final CommonHiddenStreamToken commonHiddenStreamToken) {
        return commonHiddenStreamToken.getHiddenAfter();
    }
    
    public CommonHiddenStreamToken getHiddenBefore(final CommonHiddenStreamToken commonHiddenStreamToken) {
        return commonHiddenStreamToken.getHiddenBefore();
    }
    
    public BitSet getHideMask() {
        return this.hideMask;
    }
    
    public CommonHiddenStreamToken getInitialHiddenToken() {
        return this.firstHidden;
    }
    
    public void hide(final int n) {
        this.hideMask.add(n);
    }
    
    public void hide(final BitSet hideMask) {
        this.hideMask = hideMask;
    }
    
    protected CommonHiddenStreamToken LA(final int n) {
        return this.nextMonitoredToken;
    }
    
    public Token nextToken() throws TokenStreamException {
        if (this.LA(1) == null) {
            this.consumeFirst();
        }
        final CommonHiddenStreamToken la = this.LA(1);
        la.setHiddenBefore(this.lastHiddenToken);
        this.lastHiddenToken = null;
        this.consume();
        CommonHiddenStreamToken hiddenBefore = la;
        while (this.hideMask.member(this.LA(1).getType()) || this.discardMask.member(this.LA(1).getType())) {
            if (this.hideMask.member(this.LA(1).getType())) {
                hiddenBefore.setHiddenAfter(this.LA(1));
                if (hiddenBefore != la) {
                    this.LA(1).setHiddenBefore(hiddenBefore);
                }
                final CommonHiddenStreamToken la2 = this.LA(1);
                this.lastHiddenToken = la2;
                hiddenBefore = la2;
            }
            this.consume();
        }
        return la;
    }
}
