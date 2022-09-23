// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.jetty.nio;

import org.mortbay.io.nio.DirectNIOBuffer;
import org.mortbay.io.nio.IndirectNIOBuffer;
import org.mortbay.io.Buffer;
import org.mortbay.jetty.AbstractConnector;

public abstract class AbstractNIOConnector extends AbstractConnector implements NIOConnector
{
    private boolean _useDirectBuffers;
    
    public AbstractNIOConnector() {
        this._useDirectBuffers = true;
    }
    
    public boolean getUseDirectBuffers() {
        return this._useDirectBuffers;
    }
    
    public void setUseDirectBuffers(final boolean direct) {
        this._useDirectBuffers = direct;
    }
    
    protected Buffer newBuffer(final int size) {
        Buffer buf = null;
        if (size == this.getHeaderBufferSize()) {
            buf = new IndirectNIOBuffer(size);
        }
        else {
            buf = (this._useDirectBuffers ? new DirectNIOBuffer(size) : new IndirectNIOBuffer(size));
        }
        return buf;
    }
}
