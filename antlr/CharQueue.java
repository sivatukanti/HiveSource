// 
// Decompiled by Procyon v0.5.36
// 

package antlr;

public class CharQueue
{
    protected char[] buffer;
    private int sizeLessOne;
    private int offset;
    protected int nbrEntries;
    
    public CharQueue(final int n) {
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
    
    public final void append(final char c) {
        if (this.nbrEntries == this.buffer.length) {
            this.expand();
        }
        this.buffer[this.offset + this.nbrEntries & this.sizeLessOne] = c;
        ++this.nbrEntries;
    }
    
    public final char elementAt(final int n) {
        return this.buffer[this.offset + n & this.sizeLessOne];
    }
    
    private final void expand() {
        final char[] buffer = new char[this.buffer.length * 2];
        for (int i = 0; i < this.buffer.length; ++i) {
            buffer[i] = this.elementAt(i);
        }
        this.buffer = buffer;
        this.sizeLessOne = this.buffer.length - 1;
        this.offset = 0;
    }
    
    public void init(final int n) {
        this.buffer = new char[n];
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
