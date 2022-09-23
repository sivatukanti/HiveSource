// 
// Decompiled by Procyon v0.5.36
// 

package org.antlr.runtime;

public class CommonTokenStream extends BufferedTokenStream
{
    protected int channel;
    
    public CommonTokenStream() {
        this.channel = 0;
    }
    
    public CommonTokenStream(final TokenSource tokenSource) {
        super(tokenSource);
        this.channel = 0;
    }
    
    public CommonTokenStream(final TokenSource tokenSource, final int channel) {
        this(tokenSource);
        this.channel = channel;
    }
    
    public void consume() {
        if (this.p == -1) {
            this.setup();
        }
        this.sync(++this.p);
        while (this.tokens.get(this.p).getChannel() != this.channel) {
            this.sync(++this.p);
        }
    }
    
    protected Token LB(final int k) {
        if (k == 0 || this.p - k < 0) {
            return null;
        }
        int i = this.p;
        for (int n = 1; n <= k; ++n) {
            i = this.skipOffTokenChannelsReverse(i - 1);
        }
        if (i < 0) {
            return null;
        }
        return this.tokens.get(i);
    }
    
    public Token LT(final int k) {
        if (this.p == -1) {
            this.setup();
        }
        if (k == 0) {
            return null;
        }
        if (k < 0) {
            return this.LB(-k);
        }
        int i = this.p;
        for (int n = 1; n < k; ++n) {
            i = this.skipOffTokenChannels(i + 1);
        }
        if (i > this.range) {
            this.range = i;
        }
        return this.tokens.get(i);
    }
    
    protected int skipOffTokenChannels(int i) {
        this.sync(i);
        while (this.tokens.get(i).getChannel() != this.channel) {
            ++i;
            this.sync(i);
        }
        return i;
    }
    
    protected int skipOffTokenChannelsReverse(int i) {
        while (i >= 0 && this.tokens.get(i).getChannel() != this.channel) {
            --i;
        }
        return i;
    }
    
    public void reset() {
        super.reset();
        this.p = this.skipOffTokenChannels(0);
    }
    
    protected void setup() {
        this.sync(this.p = 0);
        int i = 0;
        while (this.tokens.get(i).getChannel() != this.channel) {
            ++i;
            this.sync(i);
        }
        this.p = i;
    }
    
    public int getNumberOfOnChannelTokens() {
        int n = 0;
        this.fill();
        for (int i = 0; i < this.tokens.size(); ++i) {
            final Token t = this.tokens.get(i);
            if (t.getChannel() == this.channel) {
                ++n;
            }
            if (t.getType() == -1) {
                break;
            }
        }
        return n;
    }
    
    public void setTokenSource(final TokenSource tokenSource) {
        super.setTokenSource(tokenSource);
        this.channel = 0;
    }
}
