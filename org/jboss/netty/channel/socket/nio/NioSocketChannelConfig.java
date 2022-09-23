// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel.socket.nio;

import org.jboss.netty.channel.ReceiveBufferSizePredictorFactory;
import org.jboss.netty.channel.ReceiveBufferSizePredictor;
import org.jboss.netty.channel.socket.SocketChannelConfig;

public interface NioSocketChannelConfig extends SocketChannelConfig, NioChannelConfig
{
    ReceiveBufferSizePredictor getReceiveBufferSizePredictor();
    
    void setReceiveBufferSizePredictor(final ReceiveBufferSizePredictor p0);
    
    ReceiveBufferSizePredictorFactory getReceiveBufferSizePredictorFactory();
    
    void setReceiveBufferSizePredictorFactory(final ReceiveBufferSizePredictorFactory p0);
}
