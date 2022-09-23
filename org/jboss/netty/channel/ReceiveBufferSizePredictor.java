// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel;

public interface ReceiveBufferSizePredictor
{
    int nextReceiveBufferSize();
    
    void previousReceiveBufferSize(final int p0);
}
