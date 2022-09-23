// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel.socket.nio;

import org.jboss.netty.util.internal.ByteBufferUtil;
import java.nio.ByteBuffer;
import org.jboss.netty.util.ExternalResourceReleasable;

final class SocketReceiveBufferAllocator implements ExternalResourceReleasable
{
    private ByteBuffer buf;
    private int exceedCount;
    private final int maxExceedCount;
    private final int percentual;
    
    SocketReceiveBufferAllocator() {
        this(16, 80);
    }
    
    SocketReceiveBufferAllocator(final int maxExceedCount, final int percentual) {
        this.maxExceedCount = maxExceedCount;
        this.percentual = percentual;
    }
    
    ByteBuffer get(final int size) {
        if (this.buf == null) {
            return this.newBuffer(size);
        }
        if (this.buf.capacity() < size) {
            return this.newBuffer(size);
        }
        if (this.buf.capacity() * this.percentual / 100 > size) {
            if (++this.exceedCount == this.maxExceedCount) {
                return this.newBuffer(size);
            }
            this.buf.clear();
        }
        else {
            this.exceedCount = 0;
            this.buf.clear();
        }
        return this.buf;
    }
    
    private ByteBuffer newBuffer(final int size) {
        if (this.buf != null) {
            this.exceedCount = 0;
            ByteBufferUtil.destroy(this.buf);
        }
        return this.buf = ByteBuffer.allocateDirect(normalizeCapacity(size));
    }
    
    private static int normalizeCapacity(final int capacity) {
        int q = capacity >>> 10;
        final int r = capacity & 0x3FF;
        if (r != 0) {
            ++q;
        }
        return q << 10;
    }
    
    public void releaseExternalResources() {
        if (this.buf != null) {
            ByteBufferUtil.destroy(this.buf);
        }
    }
}
