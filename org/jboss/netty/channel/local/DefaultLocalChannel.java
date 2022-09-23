// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel.local;

import java.net.SocketAddress;
import java.nio.channels.NotYetConnectedException;
import org.jboss.netty.channel.ChannelException;
import java.nio.channels.ClosedChannelException;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.DefaultChannelConfig;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelSink;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.MessageEvent;
import java.util.Queue;
import org.jboss.netty.util.internal.ThreadLocalBoolean;
import org.jboss.netty.channel.ChannelConfig;
import java.util.concurrent.atomic.AtomicInteger;
import org.jboss.netty.channel.AbstractChannel;

final class DefaultLocalChannel extends AbstractChannel implements LocalChannel
{
    private static final int ST_OPEN = 0;
    private static final int ST_BOUND = 1;
    private static final int ST_CONNECTED = 2;
    private static final int ST_CLOSED = -1;
    final AtomicInteger state;
    private final ChannelConfig config;
    private final ThreadLocalBoolean delivering;
    final Queue<MessageEvent> writeBuffer;
    volatile DefaultLocalChannel pairedChannel;
    volatile LocalAddress localAddress;
    volatile LocalAddress remoteAddress;
    
    DefaultLocalChannel(final LocalServerChannel parent, final ChannelFactory factory, final ChannelPipeline pipeline, final ChannelSink sink, final DefaultLocalChannel pairedChannel) {
        super(parent, factory, pipeline, sink);
        this.state = new AtomicInteger(0);
        this.delivering = new ThreadLocalBoolean();
        this.writeBuffer = new ConcurrentLinkedQueue<MessageEvent>();
        this.pairedChannel = pairedChannel;
        this.config = new DefaultChannelConfig();
        this.getCloseFuture().addListener(new ChannelFutureListener() {
            public void operationComplete(final ChannelFuture future) throws Exception {
                DefaultLocalChannel.this.state.set(-1);
            }
        });
        Channels.fireChannelOpen(this);
    }
    
    public ChannelConfig getConfig() {
        return this.config;
    }
    
    @Override
    public boolean isOpen() {
        return this.state.get() >= 0;
    }
    
    public boolean isBound() {
        return this.state.get() >= 1;
    }
    
    public boolean isConnected() {
        return this.state.get() == 2;
    }
    
    void setBound() throws ClosedChannelException {
        if (this.state.compareAndSet(0, 1)) {
            return;
        }
        switch (this.state.get()) {
            case -1: {
                throw new ClosedChannelException();
            }
            default: {
                throw new ChannelException("already bound");
            }
        }
    }
    
    void setConnected() {
        if (this.state.get() != -1) {
            this.state.set(2);
        }
    }
    
    @Override
    protected boolean setClosed() {
        return super.setClosed();
    }
    
    public LocalAddress getLocalAddress() {
        return this.localAddress;
    }
    
    public LocalAddress getRemoteAddress() {
        return this.remoteAddress;
    }
    
    void closeNow(final ChannelFuture future) {
        final LocalAddress localAddress = this.localAddress;
        try {
            if (!this.setClosed()) {
                return;
            }
            final DefaultLocalChannel pairedChannel = this.pairedChannel;
            if (pairedChannel != null) {
                this.pairedChannel = null;
                Channels.fireChannelDisconnected(this);
                Channels.fireChannelUnbound(this);
            }
            Channels.fireChannelClosed(this);
            if (pairedChannel == null || !pairedChannel.setClosed()) {
                return;
            }
            final DefaultLocalChannel me = pairedChannel.pairedChannel;
            if (me != null) {
                pairedChannel.pairedChannel = null;
                Channels.fireChannelDisconnected(pairedChannel);
                Channels.fireChannelUnbound(pairedChannel);
            }
            Channels.fireChannelClosed(pairedChannel);
        }
        finally {
            future.setSuccess();
            if (localAddress != null && this.getParent() == null) {
                LocalChannelRegistry.unregister(localAddress);
            }
        }
    }
    
    void flushWriteBuffer() {
        final DefaultLocalChannel pairedChannel = this.pairedChannel;
        if (pairedChannel != null) {
            if (pairedChannel.isConnected() && !this.delivering.get()) {
                this.delivering.set(true);
                try {
                    while (true) {
                        final MessageEvent e = this.writeBuffer.poll();
                        if (e == null) {
                            break;
                        }
                        Channels.fireMessageReceived(pairedChannel, e.getMessage());
                        e.getFuture().setSuccess();
                        Channels.fireWriteComplete(this, 1L);
                    }
                }
                finally {
                    this.delivering.set(false);
                }
            }
        }
        else {
            Exception cause;
            if (this.isOpen()) {
                cause = new NotYetConnectedException();
            }
            else {
                cause = new ClosedChannelException();
            }
            while (true) {
                final MessageEvent e2 = this.writeBuffer.poll();
                if (e2 == null) {
                    break;
                }
                e2.getFuture().setFailure(cause);
                Channels.fireExceptionCaught(this, cause);
            }
        }
    }
}
