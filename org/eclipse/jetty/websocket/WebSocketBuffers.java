// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.websocket;

import org.eclipse.jetty.io.Buffer;
import org.eclipse.jetty.io.BuffersFactory;
import org.eclipse.jetty.io.Buffers;

public class WebSocketBuffers
{
    private final int _bufferSize;
    private final Buffers _buffers;
    
    public WebSocketBuffers(final int bufferSize) {
        this._bufferSize = bufferSize;
        this._buffers = BuffersFactory.newBuffers(Buffers.Type.DIRECT, bufferSize, Buffers.Type.INDIRECT, bufferSize, Buffers.Type.INDIRECT, -1);
    }
    
    public Buffer getBuffer() {
        return this._buffers.getBuffer();
    }
    
    public Buffer getDirectBuffer() {
        return this._buffers.getHeader();
    }
    
    public void returnBuffer(final Buffer buffer) {
        this._buffers.returnBuffer(buffer);
    }
    
    public int getBufferSize() {
        return this._bufferSize;
    }
}
