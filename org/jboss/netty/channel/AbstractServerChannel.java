// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel;

import java.net.SocketAddress;

public abstract class AbstractServerChannel extends AbstractChannel implements ServerChannel
{
    protected AbstractServerChannel(final ChannelFactory factory, final ChannelPipeline pipeline, final ChannelSink sink) {
        super(null, factory, pipeline, sink);
    }
    
    @Override
    public ChannelFuture connect(final SocketAddress remoteAddress) {
        return this.getUnsupportedOperationFuture();
    }
    
    @Override
    public ChannelFuture disconnect() {
        return this.getUnsupportedOperationFuture();
    }
    
    @Override
    public int getInterestOps() {
        return 0;
    }
    
    @Override
    public ChannelFuture setInterestOps(final int interestOps) {
        return this.getUnsupportedOperationFuture();
    }
    
    @Override
    protected void setInternalInterestOps(final int interestOps) {
    }
    
    @Override
    public ChannelFuture write(final Object message) {
        return this.getUnsupportedOperationFuture();
    }
    
    @Override
    public ChannelFuture write(final Object message, final SocketAddress remoteAddress) {
        return this.getUnsupportedOperationFuture();
    }
    
    public boolean isConnected() {
        return false;
    }
}
