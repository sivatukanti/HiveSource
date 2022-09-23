// 
// Decompiled by Procyon v0.5.36
// 

package org.antlr.runtime;

import java.util.ArrayList;
import java.util.List;

public class ANTLRStringStream implements CharStream
{
    protected char[] data;
    protected int n;
    protected int p;
    protected int line;
    protected int charPositionInLine;
    protected int markDepth;
    protected List markers;
    protected int lastMarker;
    public String name;
    
    public ANTLRStringStream() {
        this.p = 0;
        this.line = 1;
        this.charPositionInLine = 0;
        this.markDepth = 0;
    }
    
    public ANTLRStringStream(final String input) {
        this();
        this.data = input.toCharArray();
        this.n = input.length();
    }
    
    public ANTLRStringStream(final char[] data, final int numberOfActualCharsInArray) {
        this();
        this.data = data;
        this.n = numberOfActualCharsInArray;
    }
    
    public void reset() {
        this.p = 0;
        this.line = 1;
        this.charPositionInLine = 0;
        this.markDepth = 0;
    }
    
    public void consume() {
        if (this.p < this.n) {
            ++this.charPositionInLine;
            if (this.data[this.p] == '\n') {
                ++this.line;
                this.charPositionInLine = 0;
            }
            ++this.p;
        }
    }
    
    public int LA(int i) {
        if (i == 0) {
            return 0;
        }
        if (i < 0) {
            ++i;
            if (this.p + i - 1 < 0) {
                return -1;
            }
        }
        if (this.p + i - 1 >= this.n) {
            return -1;
        }
        return this.data[this.p + i - 1];
    }
    
    public int LT(final int i) {
        return this.LA(i);
    }
    
    public int index() {
        return this.p;
    }
    
    public int size() {
        return this.n;
    }
    
    public int mark() {
        if (this.markers == null) {
            (this.markers = new ArrayList()).add(null);
        }
        ++this.markDepth;
        CharStreamState state = null;
        if (this.markDepth >= this.markers.size()) {
            state = new CharStreamState();
            this.markers.add(state);
        }
        else {
            state = this.markers.get(this.markDepth);
        }
        state.p = this.p;
        state.line = this.line;
        state.charPositionInLine = this.charPositionInLine;
        this.lastMarker = this.markDepth;
        return this.markDepth;
    }
    
    public void rewind(final int m) {
        final CharStreamState state = this.markers.get(m);
        this.seek(state.p);
        this.line = state.line;
        this.charPositionInLine = state.charPositionInLine;
        this.release(m);
    }
    
    public void rewind() {
        this.rewind(this.lastMarker);
    }
    
    public void release(final int marker) {
        this.markDepth = marker;
        --this.markDepth;
    }
    
    public void seek(final int index) {
        if (index <= this.p) {
            this.p = index;
            return;
        }
        while (this.p < index) {
            this.consume();
        }
    }
    
    public String substring(final int start, final int stop) {
        return new String(this.data, start, stop - start + 1);
    }
    
    public int getLine() {
        return this.line;
    }
    
    public int getCharPositionInLine() {
        return this.charPositionInLine;
    }
    
    public void setLine(final int line) {
        this.line = line;
    }
    
    public void setCharPositionInLine(final int pos) {
        this.charPositionInLine = pos;
    }
    
    public String getSourceName() {
        return this.name;
    }
    
    public String toString() {
        return new String(this.data);
    }
}
