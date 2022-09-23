// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.server.nio;

import org.eclipse.jetty.io.Buffers;
import org.eclipse.jetty.server.AbstractConnector;

public abstract class AbstractNIOConnector extends AbstractConnector implements NIOConnector
{
    public AbstractNIOConnector() {
        this._buffers.setRequestBufferType(Buffers.Type.DIRECT);
        this._buffers.setRequestHeaderType(Buffers.Type.INDIRECT);
        this._buffers.setResponseBufferType(Buffers.Type.DIRECT);
        this._buffers.setResponseHeaderType(Buffers.Type.INDIRECT);
    }
    
    public boolean getUseDirectBuffers() {
        return this.getRequestBufferType() == Buffers.Type.DIRECT;
    }
    
    public void setUseDirectBuffers(final boolean direct) {
        this._buffers.setRequestBufferType(direct ? Buffers.Type.DIRECT : Buffers.Type.INDIRECT);
        this._buffers.setResponseBufferType(direct ? Buffers.Type.DIRECT : Buffers.Type.INDIRECT);
    }
}
