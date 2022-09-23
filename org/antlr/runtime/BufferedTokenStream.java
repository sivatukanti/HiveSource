// 
// Decompiled by Procyon v0.5.36
// 

package org.antlr.runtime;

import java.util.NoSuchElementException;
import java.util.ArrayList;
import java.util.List;

public class BufferedTokenStream implements TokenStream
{
    protected TokenSource tokenSource;
    protected List<Token> tokens;
    protected int lastMarker;
    protected int p;
    protected int range;
    
    public BufferedTokenStream() {
        this.tokens = new ArrayList<Token>(100);
        this.p = -1;
        this.range = -1;
    }
    
    public BufferedTokenStream(final TokenSource tokenSource) {
        this.tokens = new ArrayList<Token>(100);
        this.p = -1;
        this.range = -1;
        this.tokenSource = tokenSource;
    }
    
    public TokenSource getTokenSource() {
        return this.tokenSource;
    }
    
    public int index() {
        return this.p;
    }
    
    public int range() {
        return this.range;
    }
    
    public int mark() {
        if (this.p == -1) {
            this.setup();
        }
        return this.lastMarker = this.index();
    }
    
    public void release(final int marker) {
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
    
    public int size() {
        return this.tokens.size();
    }
    
    public void consume() {
        if (this.p == -1) {
            this.setup();
        }
        this.sync(++this.p);
    }
    
    protected void sync(final int i) {
        final int n = i - this.tokens.size() + 1;
        if (n > 0) {
            this.fetch(n);
        }
    }
    
    protected void fetch(final int n) {
        for (int i = 1; i <= n; ++i) {
            final Token t = this.tokenSource.nextToken();
            t.setTokenIndex(this.tokens.size());
            this.tokens.add(t);
            if (t.getType() == -1) {
                break;
            }
        }
    }
    
    public Token get(final int i) {
        if (i < 0 || i >= this.tokens.size()) {
            throw new NoSuchElementException("token index " + i + " out of range 0.." + (this.tokens.size() - 1));
        }
        return this.tokens.get(i);
    }
    
    public List get(final int start, int stop) {
        if (start < 0 || stop < 0) {
            return null;
        }
        if (this.p == -1) {
            this.setup();
        }
        final List subset = new ArrayList();
        if (stop >= this.tokens.size()) {
            stop = this.tokens.size() - 1;
        }
        for (int i = start; i <= stop; ++i) {
            final Token t = this.tokens.get(i);
            if (t.getType() == -1) {
                break;
            }
            subset.add(t);
        }
        return subset;
    }
    
    public int LA(final int i) {
        return this.LT(i).getType();
    }
    
    protected Token LB(final int k) {
        if (this.p - k < 0) {
            return null;
        }
        return this.tokens.get(this.p - k);
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
        final int i = this.p + k - 1;
        this.sync(i);
        if (i >= this.tokens.size()) {
            return this.tokens.get(this.tokens.size() - 1);
        }
        if (i > this.range) {
            this.range = i;
        }
        return this.tokens.get(i);
    }
    
    protected void setup() {
        this.sync(0);
        this.p = 0;
    }
    
    public void setTokenSource(final TokenSource tokenSource) {
        this.tokenSource = tokenSource;
        this.tokens.clear();
        this.p = -1;
    }
    
    public List getTokens() {
        return this.tokens;
    }
    
    public List getTokens(final int start, final int stop) {
        return this.getTokens(start, stop, (BitSet)null);
    }
    
    public List getTokens(int start, int stop, final BitSet types) {
        if (this.p == -1) {
            this.setup();
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
        List<Token> filteredTokens = new ArrayList<Token>();
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
    
    public String getSourceName() {
        return this.tokenSource.getSourceName();
    }
    
    public String toString() {
        if (this.p == -1) {
            this.setup();
        }
        this.fill();
        return this.toString(0, this.tokens.size() - 1);
    }
    
    public String toString(final int start, int stop) {
        if (start < 0 || stop < 0) {
            return null;
        }
        if (this.p == -1) {
            this.setup();
        }
        if (stop >= this.tokens.size()) {
            stop = this.tokens.size() - 1;
        }
        final StringBuffer buf = new StringBuffer();
        for (int i = start; i <= stop; ++i) {
            final Token t = this.tokens.get(i);
            if (t.getType() == -1) {
                break;
            }
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
    
    public void fill() {
        if (this.p == -1) {
            this.setup();
        }
        if (this.tokens.get(this.p).getType() == -1) {
            return;
        }
        int i = this.p + 1;
        this.sync(i);
        while (this.tokens.get(i).getType() != -1) {
            ++i;
            this.sync(i);
        }
    }
}
