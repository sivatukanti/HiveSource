// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.util;

public class Utf8StringBuffer extends Utf8Appendable
{
    final StringBuffer _buffer;
    
    public Utf8StringBuffer() {
        super(new StringBuffer());
        this._buffer = (StringBuffer)this._appendable;
    }
    
    public Utf8StringBuffer(final int capacity) {
        super(new StringBuffer(capacity));
        this._buffer = (StringBuffer)this._appendable;
    }
    
    @Override
    public int length() {
        return this._buffer.length();
    }
    
    public void reset() {
        super.reset();
        this._buffer.setLength(0);
    }
    
    public StringBuffer getStringBuffer() {
        this.checkState();
        return this._buffer;
    }
    
    @Override
    public String toString() {
        this.checkState();
        return this._buffer.toString();
    }
}
