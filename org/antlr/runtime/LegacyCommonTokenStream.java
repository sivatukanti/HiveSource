// 
// Decompiled by Procyon v0.5.36
// 

package org.antlr.runtime;

import java.util.HashSet;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Set;
import java.util.Map;
import java.util.List;

public class LegacyCommonTokenStream implements TokenStream
{
    protected TokenSource tokenSource;
    protected List tokens;
    protected Map channelOverrideMap;
    protected Set discardSet;
    protected int channel;
    protected boolean discardOffChannelTokens;
    protected int lastMarker;
    protected int range;
    protected int p;
    
    public LegacyCommonTokenStream() {
        this.channel = 0;
        this.discardOffChannelTokens = false;
        this.range = -1;
        this.p = -1;
        this.tokens = new ArrayList(500);
    }
    
    public LegacyCommonTokenStream(final TokenSource tokenSource) {
        this();
        this.tokenSource = tokenSource;
    }
    
    public LegacyCommonTokenStream(final TokenSource tokenSource, final int channel) {
        this(tokenSource);
        this.channel = channel;
    }
    
    public void setTokenSource(final TokenSource tokenSource) {
        this.tokenSource = tokenSource;
        this.tokens.clear();
        this.p = -1;
        this.channel = 0;
    }
    
    protected void fillBuffer() {
        int index = 0;
        for (Token t = this.tokenSource.nextToken(); t != null && t.getType() != -1; t = this.tokenSource.nextToken()) {
            boolean discard = false;
            if (this.channelOverrideMap != null) {
                final Integer channelI = this.channelOverrideMap.get(new Integer(t.getType()));
                if (channelI != null) {
                    t.setChannel(channelI);
                }
            }
            if (this.discardSet != null && this.discardSet.contains(new Integer(t.getType()))) {
                discard = true;
            }
            else if (this.discardOffChannelTokens && t.getChannel() != this.channel) {
                discard = true;
            }
            if (!discard) {
                t.setTokenIndex(index);
                this.tokens.add(t);
                ++index;
            }
        }
        this.p = 0;
        this.p = this.skipOffTokenChannels(this.p);
    }
    
    public void consume() {
        if (this.p < this.tokens.size()) {
            ++this.p;
            this.p = this.skipOffTokenChannels(this.p);
        }
    }
    
    protected int skipOffTokenChannels(int i) {
        for (int n = this.tokens.size(); i < n && this.tokens.get(i).getChannel() != this.channel; ++i) {}
        return i;
    }
    
    protected int skipOffTokenChannelsReverse(int i) {
        while (i >= 0 && this.tokens.get(i).getChannel() != this.channel) {
            --i;
        }
        return i;
    }
    
    public void setTokenTypeChannel(final int ttype, final int channel) {
        if (this.channelOverrideMap == null) {
            this.channelOverrideMap = new HashMap();
        }
        this.channelOverrideMap.put(new Integer(ttype), new Integer(channel));
    }
    
    public void discardTokenType(final int ttype) {
        if (this.discardSet == null) {
            this.discardSet = new HashSet();
        }
        this.discardSet.add(new Integer(ttype));
    }
    
    public void discardOffChannelTokens(final boolean discardOffChannelTokens) {
        this.discardOffChannelTokens = discardOffChannelTokens;
    }
    
    public List getTokens() {
        if (this.p == -1) {
            this.fillBuffer();
        }
        return this.tokens;
    }
    
    public List getTokens(final int start, final int stop) {
        return this.getTokens(start, stop, (BitSet)null);
    }
    
    public List getTokens(int start, int stop, final BitSet types) {
        if (this.p == -1) {
            this.fillBuffer();
        }
        if (stop >= this.tokens.size()) {
            stop = this.tokens.size() - 1;
        }
        if (start < 0) {
            start = 0;
        }
        if (start > stop) {
            return null;
        }
        List filteredTokens = new ArrayList();
        for (int i = start; i <= stop; ++i) {
            final Token t = this.tokens.get(i);
            if (types == null || types.member(t.getType())) {
                filteredTokens.add(t);
            }
        }
        if (filteredTokens.size() == 0) {
            filteredTokens = null;
        }
        return filteredTokens;
    }
    
    public List getTokens(final int start, final int stop, final List types) {
        return this.getTokens(start, stop, new BitSet(types));
    }
    
    public List getTokens(final int start, final int stop, final int ttype) {
        return this.getTokens(start, stop, BitSet.of(ttype));
    }
    
    public Token LT(final int k) {
        if (this.p == -1) {
            this.fillBuffer();
        }
        if (k == 0) {
            return null;
        }
        if (k < 0) {
            return this.LB(-k);
        }
        if (this.p + k - 1 >= this.tokens.size()) {
            return this.tokens.get(this.tokens.size() - 1);
        }
        int i = this.p;
        for (int n = 1; n < k; ++n) {
            i = this.skipOffTokenChannels(i + 1);
        }
        if (i >= this.tokens.size()) {
            return this.tokens.get(this.tokens.size() - 1);
        }
        if (i > this.range) {
            this.range = i;
        }
        return this.tokens.get(i);
    }
    
    protected Token LB(final int k) {
        if (this.p == -1) {
            this.fillBuffer();
        }
        if (k == 0) {
            return null;
        }
        if (this.p - k < 0) {
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
    
    public Token get(final int i) {
        return this.tokens.get(i);
    }
    
    public List get(final int start, final int stop) {
        if (this.p == -1) {
            this.fillBuffer();
        }
        if (start < 0 || stop < 0) {
            return null;
        }
        return this.tokens.subList(start, stop);
    }
    
    public int LA(final int i) {
        return this.LT(i).getType();
    }
    
    public int mark() {
        if (this.p == -1) {
            this.fillBuffer();
        }
        return this.lastMarker = this.index();
    }
    
    public void release(final int marker) {
    }
    
    public int size() {
        return this.tokens.size();
    }
    
    public int index() {
        return this.p;
    }
    
    public int range() {
        return this.range;
    }
    
    public void rewind(final int marker) {
        this.seek(marker);
    }
    
    public void rewind() {
        this.seek(this.lastMarker);
    }
    
    public void reset() {
        this.p = 0;
        this.lastMarker = 0;
    }
    
    public void seek(final int index) {
        this.p = index;
    }
    
    public TokenSource getTokenSource() {
        return this.tokenSource;
    }
    
    public String getSourceName() {
        return this.getTokenSource().getSourceName();
    }
    
    public String toString() {
        if (this.p == -1) {
            this.fillBuffer();
        }
        return this.toString(0, this.tokens.size() - 1);
    }
    
    public String toString(final int start, int stop) {
        if (start < 0 || stop < 0) {
            return null;
        }
        if (this.p == -1) {
            this.fillBuffer();
        }
        if (stop >= this.tokens.size()) {
            stop = this.tokens.size() - 1;
        }
        final StringBuffer buf = new StringBuffer();
        for (int i = start; i <= stop; ++i) {
            final Token t = this.tokens.get(i);
            buf.append(t.getText());
        }
        return buf.toString();
    }
    
    public String toString(final Token start, final Token stop) {
        if (start != null && stop != null) {
            return this.toString(start.getTokenIndex(), stop.getTokenIndex());
        }
        return null;
    }
}
