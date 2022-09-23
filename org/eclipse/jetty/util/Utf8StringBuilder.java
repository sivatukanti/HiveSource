// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.util;

public class Utf8StringBuilder extends Utf8Appendable
{
    final StringBuilder _buffer;
    
    public Utf8StringBuilder() {
        super(new StringBuilder());
        this._buffer = (StringBuilder)this._appendable;
    }
    
    public Utf8StringBuilder(final int capacity) {
        super(new StringBuilder(capacity));
        this._buffer = (StringBuilder)this._appendable;
    }
    
    @Override
    public int length() {
        return this._buffer.length();
    }
    
    public void reset() {
        super.reset();
        this._buffer.setLength(0);
    }
    
    public StringBuilder getStringBuilder() {
        this.checkState();
        return this._buffer;
    }
    
    @Override
    public String toString() {
        this.checkState();
        return this._buffer.toString();
    }
}
