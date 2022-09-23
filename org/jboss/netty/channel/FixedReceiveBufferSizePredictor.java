// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel;

public class FixedReceiveBufferSizePredictor implements ReceiveBufferSizePredictor
{
    private final int bufferSize;
    
    public FixedReceiveBufferSizePredictor(final int bufferSize) {
        if (bufferSize <= 0) {
            throw new IllegalArgumentException("bufferSize must greater than 0: " + bufferSize);
        }
        this.bufferSize = bufferSize;
    }
    
    public int nextReceiveBufferSize() {
        return this.bufferSize;
    }
    
    public void previousReceiveBufferSize(final int previousReceiveBufferSize) {
    }
}
