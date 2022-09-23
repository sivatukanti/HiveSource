// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.marshalling;

import java.io.IOException;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.buffer.ChannelBufferFactory;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.marshalling.ByteOutput;

class ChannelBufferByteOutput implements ByteOutput
{
    private final ChannelBuffer buffer;
    
    public ChannelBufferByteOutput(final ChannelBuffer buffer) {
        this.buffer = buffer;
    }
    
    public ChannelBufferByteOutput(final ChannelBufferFactory factory, final int estimatedLength) {
        this(ChannelBuffers.dynamicBuffer(estimatedLength, factory));
    }
    
    public void close() throws IOException {
    }
    
    public void flush() throws IOException {
    }
    
    public void write(final int b) throws IOException {
        this.buffer.writeByte(b);
    }
    
    public void write(final byte[] bytes) throws IOException {
        this.buffer.writeBytes(bytes);
    }
    
    public void write(final byte[] bytes, final int srcIndex, final int length) throws IOException {
        this.buffer.writeBytes(bytes, srcIndex, length);
    }
    
    public ChannelBuffer getBuffer() {
        return this.buffer;
    }
}
