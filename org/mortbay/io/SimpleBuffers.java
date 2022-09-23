// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.io;

public class SimpleBuffers implements Buffers
{
    Buffer[] _buffers;
    
    public SimpleBuffers(final Buffer[] buffers) {
        this._buffers = buffers;
    }
    
    public Buffer getBuffer(final int size) {
        if (this._buffers != null) {
            for (int i = 0; i < this._buffers.length; ++i) {
                if (this._buffers[i] != null && this._buffers[i].capacity() == size) {
                    final Buffer b = this._buffers[i];
                    this._buffers[i] = null;
                    return b;
                }
            }
        }
        return new ByteArrayBuffer(size);
    }
    
    public void returnBuffer(final Buffer buffer) {
        buffer.clear();
        if (this._buffers != null) {
            for (int i = 0; i < this._buffers.length; ++i) {
                if (this._buffers[i] == null) {
                    this._buffers[i] = buffer;
                }
            }
        }
    }
}
