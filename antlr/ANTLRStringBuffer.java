// 
// Decompiled by Procyon v0.5.36
// 

package antlr;

public class ANTLRStringBuffer
{
    protected char[] buffer;
    protected int length;
    
    public ANTLRStringBuffer() {
        this.buffer = null;
        this.length = 0;
        this.buffer = new char[50];
    }
    
    public ANTLRStringBuffer(final int n) {
        this.buffer = null;
        this.length = 0;
        this.buffer = new char[n];
    }
    
    public final void append(final char c) {
        if (this.length >= this.buffer.length) {
            int length;
            for (length = this.buffer.length; this.length >= length; length *= 2) {}
            final char[] buffer = new char[length];
            for (int i = 0; i < this.length; ++i) {
                buffer[i] = this.buffer[i];
            }
            this.buffer = buffer;
        }
        this.buffer[this.length] = c;
        ++this.length;
    }
    
    public final void append(final String s) {
        for (int i = 0; i < s.length(); ++i) {
            this.append(s.charAt(i));
        }
    }
    
    public final char charAt(final int n) {
        return this.buffer[n];
    }
    
    public final char[] getBuffer() {
        return this.buffer;
    }
    
    public final int length() {
        return this.length;
    }
    
    public final void setCharAt(final int n, final char c) {
        this.buffer[n] = c;
    }
    
    public final void setLength(final int i) {
        if (i < this.length) {
            this.length = i;
        }
        else {
            while (i > this.length) {
                this.append('\0');
            }
        }
    }
    
    public final String toString() {
        return new String(this.buffer, 0, this.length);
    }
}
