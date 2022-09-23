// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.timeout;

import java.util.Date;
import java.text.DateFormat;
import java.util.Locale;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.Channel;

public class DefaultIdleStateEvent implements IdleStateEvent
{
    private final Channel channel;
    private final IdleState state;
    private final long lastActivityTimeMillis;
    
    public DefaultIdleStateEvent(final Channel channel, final IdleState state, final long lastActivityTimeMillis) {
        if (channel == null) {
            throw new NullPointerException("channel");
        }
        if (state == null) {
            throw new NullPointerException("state");
        }
        this.channel = channel;
        this.state = state;
        this.lastActivityTimeMillis = lastActivityTimeMillis;
    }
    
    public Channel getChannel() {
        return this.channel;
    }
    
    public ChannelFuture getFuture() {
        return Channels.succeededFuture(this.getChannel());
    }
    
    public IdleState getState() {
        return this.state;
    }
    
    public long getLastActivityTimeMillis() {
        return this.lastActivityTimeMillis;
    }
    
    @Override
    public String toString() {
        return this.getChannel().toString() + ' ' + this.getState() + " since " + DateFormat.getDateTimeInstance(3, 3, Locale.US).format(new Date(this.getLastActivityTimeMillis()));
    }
}
