// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.io;

public class View extends AbstractBuffer
{
    Buffer _buffer;
    
    public View(final Buffer buffer, final int mark, final int get, final int put, final int access) {
        super(2, !buffer.isImmutable());
        this._buffer = buffer.buffer();
        this.setPutIndex(put);
        this.setGetIndex(get);
        this.setMarkIndex(mark);
        this._access = access;
    }
    
    public View(final Buffer buffer) {
        super(2, !buffer.isImmutable());
        this._buffer = buffer.buffer();
        this.setPutIndex(buffer.putIndex());
        this.setGetIndex(buffer.getIndex());
        this.setMarkIndex(buffer.markIndex());
        this._access = (buffer.isReadOnly() ? 1 : 2);
    }
    
    public View() {
        super(2, true);
    }
    
    public void update(final Buffer buffer) {
        this._access = 2;
        this._buffer = buffer.buffer();
        this.setGetIndex(0);
        this.setPutIndex(buffer.putIndex());
        this.setGetIndex(buffer.getIndex());
        this.setMarkIndex(buffer.markIndex());
        this._access = (buffer.isReadOnly() ? 1 : 2);
    }
    
    public void update(final int get, final int put) {
        final int a = this._access;
        this._access = 2;
        this.setGetIndex(0);
        this.setPutIndex(put);
        this.setGetIndex(get);
        this.setMarkIndex(-1);
        this._access = a;
    }
    
    public byte[] array() {
        return this._buffer.array();
    }
    
    @Override
    public Buffer buffer() {
        return this._buffer.buffer();
    }
    
    public int capacity() {
        return this._buffer.capacity();
    }
    
    @Override
    public void clear() {
        this.setMarkIndex(-1);
        this.setGetIndex(0);
        this.setPutIndex(this._buffer.getIndex());
        this.setGetIndex(this._buffer.getIndex());
    }
    
    @Override
    public void compact() {
    }
    
    @Override
    public boolean equals(final Object obj) {
        return this == obj || (obj instanceof Buffer && obj.equals(this)) || super.equals(obj);
    }
    
    @Override
    public boolean isReadOnly() {
        return this._buffer.isReadOnly();
    }
    
    @Override
    public boolean isVolatile() {
        return true;
    }
    
    public byte peek(final int index) {
        return this._buffer.peek(index);
    }
    
    public int peek(final int index, final byte[] b, final int offset, final int length) {
        return this._buffer.peek(index, b, offset, length);
    }
    
    @Override
    public Buffer peek(final int index, final int length) {
        return this._buffer.peek(index, length);
    }
    
    @Override
    public int poke(final int index, final Buffer src) {
        return this._buffer.poke(index, src);
    }
    
    public void poke(final int index, final byte b) {
        this._buffer.poke(index, b);
    }
    
    @Override
    public int poke(final int index, final byte[] b, final int offset, final int length) {
        return this._buffer.poke(index, b, offset, length);
    }
    
    @Override
    public String toString() {
        if (this._buffer == null) {
            return "INVALID";
        }
        return super.toString();
    }
    
    public static class CaseInsensitive extends View implements Buffer.CaseInsensitve
    {
        public CaseInsensitive() {
        }
        
        public CaseInsensitive(final Buffer buffer, final int mark, final int get, final int put, final int access) {
            super(buffer, mark, get, put, access);
        }
        
        public CaseInsensitive(final Buffer buffer) {
            super(buffer);
        }
        
        @Override
        public boolean equals(final Object obj) {
            return this == obj || (obj instanceof Buffer && ((Buffer)obj).equalsIgnoreCase(this)) || super.equals(obj);
        }
    }
}
