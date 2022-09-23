// 
// Decompiled by Procyon v0.5.36
// 

package jline;

public class CursorBuffer
{
    public int cursor;
    StringBuffer buffer;
    private boolean overtyping;
    
    public CursorBuffer() {
        this.cursor = 0;
        this.buffer = new StringBuffer();
        this.overtyping = false;
    }
    
    public int length() {
        return this.buffer.length();
    }
    
    public char current() {
        if (this.cursor <= 0) {
            return '\0';
        }
        return this.buffer.charAt(this.cursor - 1);
    }
    
    public boolean clearBuffer() {
        if (this.buffer.length() == 0) {
            return false;
        }
        this.buffer.delete(0, this.buffer.length());
        this.cursor = 0;
        return true;
    }
    
    public void write(final char c) {
        this.buffer.insert(this.cursor++, c);
        if (this.isOvertyping() && this.cursor < this.buffer.length()) {
            this.buffer.deleteCharAt(this.cursor);
        }
    }
    
    public void write(final String str) {
        if (this.buffer.length() == 0) {
            this.buffer.append(str);
        }
        else {
            this.buffer.insert(this.cursor, str);
        }
        this.cursor += str.length();
        if (this.isOvertyping() && this.cursor < this.buffer.length()) {
            this.buffer.delete(this.cursor, this.cursor + str.length());
        }
    }
    
    public String toString() {
        return this.buffer.toString();
    }
    
    public boolean isOvertyping() {
        return this.overtyping;
    }
    
    public void setOvertyping(final boolean b) {
        this.overtyping = b;
    }
    
    public StringBuffer getBuffer() {
        return this.buffer;
    }
    
    public void setBuffer(final StringBuffer buffer) {
        buffer.setLength(0);
        buffer.append(this.buffer.toString());
        this.buffer = buffer;
    }
}
