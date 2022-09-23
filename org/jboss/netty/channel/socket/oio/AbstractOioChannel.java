// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel.socket.oio;

import java.io.IOException;
import org.jboss.netty.channel.ChannelFuture;
import java.net.SocketAddress;
import org.jboss.netty.channel.ChannelSink;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.socket.Worker;
import java.net.InetSocketAddress;
import org.jboss.netty.channel.AbstractChannel;

abstract class AbstractOioChannel extends AbstractChannel
{
    private volatile InetSocketAddress localAddress;
    volatile InetSocketAddress remoteAddress;
    volatile Thread workerThread;
    volatile Worker worker;
    final Object interestOpsLock;
    
    AbstractOioChannel(final Channel parent, final ChannelFactory factory, final ChannelPipeline pipeline, final ChannelSink sink) {
        super(parent, factory, pipeline, sink);
        this.interestOpsLock = new Object();
    }
    
    @Override
    protected boolean setClosed() {
        return super.setClosed();
    }
    
    @Override
    protected int getInternalInterestOps() {
        return super.getInternalInterestOps();
    }
    
    @Override
    protected void setInternalInterestOps(final int interestOps) {
        super.setInternalInterestOps(interestOps);
    }
    
    @Override
    public ChannelFuture write(final Object message, final SocketAddress remoteAddress) {
        if (remoteAddress == null || remoteAddress.equals(this.getRemoteAddress())) {
            return super.write(message, null);
        }
        return super.write(message, remoteAddress);
    }
    
    public boolean isBound() {
        return this.isOpen() && this.isSocketBound();
    }
    
    public boolean isConnected() {
        return this.isOpen() && this.isSocketConnected();
    }
    
    public InetSocketAddress getLocalAddress() {
        InetSocketAddress localAddress = this.localAddress;
        if (localAddress == null) {
            try {
                localAddress = (this.localAddress = this.getLocalSocketAddress());
            }
            catch (Throwable t) {
                return null;
            }
        }
        return localAddress;
    }
    
    public InetSocketAddress getRemoteAddress() {
        InetSocketAddress remoteAddress = this.remoteAddress;
        if (remoteAddress == null) {
            try {
                remoteAddress = (this.remoteAddress = this.getRemoteSocketAddress());
            }
            catch (Throwable t) {
                return null;
            }
        }
        return remoteAddress;
    }
    
    abstract boolean isSocketBound();
    
    abstract boolean isSocketConnected();
    
    abstract boolean isSocketClosed();
    
    abstract InetSocketAddress getLocalSocketAddress() throws Exception;
    
    abstract InetSocketAddress getRemoteSocketAddress() throws Exception;
    
    abstract void closeSocket() throws IOException;
}
