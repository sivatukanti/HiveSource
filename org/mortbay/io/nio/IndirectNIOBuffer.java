// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.io.nio;

import java.nio.ByteBuffer;
import org.mortbay.io.ByteArrayBuffer;

public class IndirectNIOBuffer extends ByteArrayBuffer implements NIOBuffer
{
    protected ByteBuffer _buf;
    
    public IndirectNIOBuffer(final int size) {
        super(2, false);
        (this._buf = ByteBuffer.allocate(size)).position(0);
        this._buf.limit(this._buf.capacity());
        this._bytes = this._buf.array();
    }
    
    public IndirectNIOBuffer(final ByteBuffer buffer, final boolean immutable) {
        super(immutable ? 0 : 2, false);
        if (buffer.isDirect()) {
            throw new IllegalArgumentException();
        }
        this._buf = buffer;
        this.setGetIndex(buffer.position());
        this.setPutIndex(buffer.limit());
        this._bytes = this._buf.array();
    }
    
    public ByteBuffer getByteBuffer() {
        return this._buf;
    }
    
    public boolean isDirect() {
        return false;
    }
}
