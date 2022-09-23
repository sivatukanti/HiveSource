// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.util;

public class Utf8StringBuffer
{
    StringBuffer _buffer;
    int _more;
    int _bits;
    boolean _errors;
    
    public Utf8StringBuffer() {
        this._buffer = new StringBuffer();
    }
    
    public Utf8StringBuffer(final int capacity) {
        this._buffer = new StringBuffer(capacity);
    }
    
    public void append(final byte[] b, final int offset, final int length) {
        for (int end = offset + length, i = offset; i < end; ++i) {
            this.append(b[i]);
        }
    }
    
    public void append(final byte b) {
        if (b >= 0) {
            if (this._more > 0) {
                this._buffer.append('?');
                this._more = 0;
                this._bits = 0;
            }
            else {
                this._buffer.append((char)(0x7F & b));
            }
        }
        else if (this._more == 0) {
            if ((b & 0xC0) != 0xC0) {
                this._buffer.append('?');
                this._more = 0;
                this._bits = 0;
            }
            else if ((b & 0xE0) == 0xC0) {
                this._more = 1;
                this._bits = (b & 0x1F);
            }
            else if ((b & 0xF0) == 0xE0) {
                this._more = 2;
                this._bits = (b & 0xF);
            }
            else if ((b & 0xF8) == 0xF0) {
                this._more = 3;
                this._bits = (b & 0x7);
            }
            else if ((b & 0xFC) == 0xF8) {
                this._more = 4;
                this._bits = (b & 0x3);
            }
            else if ((b & 0xFE) == 0xFC) {
                this._more = 5;
                this._bits = (b & 0x1);
            }
        }
        else if ((b & 0xC0) == 0xC0) {
            this._buffer.append('?');
            this._more = 0;
            this._bits = 0;
            this._errors = true;
        }
        else {
            this._bits = (this._bits << 6 | (b & 0x3F));
            if (--this._more == 0) {
                this._buffer.append((char)this._bits);
            }
        }
    }
    
    public int length() {
        return this._buffer.length();
    }
    
    public void reset() {
        this._buffer.setLength(0);
        this._more = 0;
        this._bits = 0;
        this._errors = false;
    }
    
    public StringBuffer getStringBuffer() {
        return this._buffer;
    }
    
    public String toString() {
        return this._buffer.toString();
    }
    
    public boolean isError() {
        return this._errors || this._more > 0;
    }
}
