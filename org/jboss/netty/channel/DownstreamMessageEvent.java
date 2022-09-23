// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel;

import org.jboss.netty.util.internal.StringUtil;
import java.net.SocketAddress;

public class DownstreamMessageEvent implements MessageEvent
{
    private final Channel channel;
    private final ChannelFuture future;
    private final Object message;
    private final SocketAddress remoteAddress;
    
    public DownstreamMessageEvent(final Channel channel, final ChannelFuture future, final Object message, final SocketAddress remoteAddress) {
        if (channel == null) {
            throw new NullPointerException("channel");
        }
        if (future == null) {
            throw new NullPointerException("future");
        }
        if (message == null) {
            throw new NullPointerException("message");
        }
        this.channel = channel;
        this.future = future;
        this.message = message;
        if (remoteAddress != null) {
            this.remoteAddress = remoteAddress;
        }
        else {
            this.remoteAddress = channel.getRemoteAddress();
        }
    }
    
    public Channel getChannel() {
        return this.channel;
    }
    
    public ChannelFuture getFuture() {
        return this.future;
    }
    
    public Object getMessage() {
        return this.message;
    }
    
    public SocketAddress getRemoteAddress() {
        return this.remoteAddress;
    }
    
    @Override
    public String toString() {
        if (this.getRemoteAddress() == this.getChannel().getRemoteAddress()) {
            return this.getChannel().toString() + " WRITE: " + StringUtil.stripControlCharacters(this.getMessage());
        }
        return this.getChannel().toString() + " WRITE: " + StringUtil.stripControlCharacters(this.getMessage()) + " to " + this.getRemoteAddress();
    }
}
