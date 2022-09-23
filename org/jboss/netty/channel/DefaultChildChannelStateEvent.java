// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel;

public class DefaultChildChannelStateEvent implements ChildChannelStateEvent
{
    private final Channel parentChannel;
    private final Channel childChannel;
    
    public DefaultChildChannelStateEvent(final Channel parentChannel, final Channel childChannel) {
        if (parentChannel == null) {
            throw new NullPointerException("parentChannel");
        }
        if (childChannel == null) {
            throw new NullPointerException("childChannel");
        }
        this.parentChannel = parentChannel;
        this.childChannel = childChannel;
    }
    
    public Channel getChannel() {
        return this.parentChannel;
    }
    
    public ChannelFuture getFuture() {
        return Channels.succeededFuture(this.getChannel());
    }
    
    public Channel getChildChannel() {
        return this.childChannel;
    }
    
    @Override
    public String toString() {
        final String channelString = this.getChannel().toString();
        final StringBuilder buf = new StringBuilder(channelString.length() + 32);
        buf.append(channelString);
        buf.append(this.getChildChannel().isOpen() ? " CHILD_OPEN: " : " CHILD_CLOSED: ");
        buf.append(this.getChildChannel().getId());
        return buf.toString();
    }
}
