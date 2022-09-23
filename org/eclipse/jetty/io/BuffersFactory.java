// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.io;

public class BuffersFactory
{
    public static Buffers newBuffers(final Buffers.Type headerType, final int headerSize, final Buffers.Type bufferType, final int bufferSize, final Buffers.Type otherType, final int maxSize) {
        if (maxSize >= 0) {
            return new PooledBuffers(headerType, headerSize, bufferType, bufferSize, otherType, maxSize);
        }
        return new ThreadLocalBuffers(headerType, headerSize, bufferType, bufferSize, otherType);
    }
}
