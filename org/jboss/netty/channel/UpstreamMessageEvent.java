// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel;

import org.jboss.netty.util.internal.StringUtil;
import java.net.SocketAddress;

public class UpstreamMessageEvent implements MessageEvent
{
    private final Channel channel;
    private final Object message;
    private final SocketAddress remoteAddress;
    
    public UpstreamMessageEvent(final Channel channel, final Object message, final SocketAddress remoteAddress) {
        if (channel == null) {
            throw new NullPointerException("channel");
        }
        if (message == null) {
            throw new NullPointerException("message");
        }
        this.channel = channel;
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
        return Channels.succeededFuture(this.getChannel());
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
            return this.getChannel().toString() + " RECEIVED: " + StringUtil.stripControlCharacters(this.getMessage());
        }
        return this.getChannel().toString() + " RECEIVED: " + StringUtil.stripControlCharacters(this.getMessage()) + " from " + this.getRemoteAddress();
    }
}
