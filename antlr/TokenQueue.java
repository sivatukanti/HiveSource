// 
// Decompiled by Procyon v0.5.36
// 

package antlr;

class TokenQueue
{
    private Token[] buffer;
    private int sizeLessOne;
    private int offset;
    protected int nbrEntries;
    
    public TokenQueue(final int n) {
        if (n < 0) {
            this.init(16);
            return;
        }
        if (n >= 1073741823) {
            this.init(Integer.MAX_VALUE);
            return;
        }
        int i;
        for (i = 2; i < n; i *= 2) {}
        this.init(i);
    }
    
    public final void append(final Token token) {
        if (this.nbrEntries == this.buffer.length) {
            this.expand();
        }
        this.buffer[this.offset + this.nbrEntries & this.sizeLessOne] = token;
        ++this.nbrEntries;
    }
    
    public final Token elementAt(final int n) {
        return this.buffer[this.offset + n & this.sizeLessOne];
    }
    
    private final void expand() {
        final Token[] buffer = new Token[this.buffer.length * 2];
        for (int i = 0; i < this.buffer.length; ++i) {
            buffer[i] = this.elementAt(i);
        }
        this.buffer = buffer;
        this.sizeLessOne = this.buffer.length - 1;
        this.offset = 0;
    }
    
    private final void init(final int n) {
        this.buffer = new Token[n];
        this.sizeLessOne = n - 1;
        this.offset = 0;
        this.nbrEntries = 0;
    }
    
    public final void reset() {
        this.offset = 0;
        this.nbrEntries = 0;
    }
    
    public final void removeFirst() {
        this.offset = (this.offset + 1 & this.sizeLessOne);
        --this.nbrEntries;
    }
}
