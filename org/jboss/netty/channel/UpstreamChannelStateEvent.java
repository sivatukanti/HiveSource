// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel;

public class UpstreamChannelStateEvent implements ChannelStateEvent
{
    private final Channel channel;
    private final ChannelState state;
    private final Object value;
    
    public UpstreamChannelStateEvent(final Channel channel, final ChannelState state, final Object value) {
        if (channel == null) {
            throw new NullPointerException("channel");
        }
        if (state == null) {
            throw new NullPointerException("state");
        }
        this.channel = channel;
        this.state = state;
        this.value = value;
    }
    
    public Channel getChannel() {
        return this.channel;
    }
    
    public ChannelFuture getFuture() {
        return Channels.succeededFuture(this.getChannel());
    }
    
    public ChannelState getState() {
        return this.state;
    }
    
    public Object getValue() {
        return this.value;
    }
    
    @Override
    public String toString() {
        final String channelString = this.getChannel().toString();
        final StringBuilder buf = new StringBuilder(channelString.length() + 64);
        buf.append(channelString);
        switch (this.getState()) {
            case OPEN: {
                if (Boolean.TRUE.equals(this.getValue())) {
                    buf.append(" OPEN");
                    break;
                }
                buf.append(" CLOSED");
                break;
            }
            case BOUND: {
                if (this.getValue() != null) {
                    buf.append(" BOUND: ");
                    buf.append(this.getValue());
                    break;
                }
                buf.append(" UNBOUND");
                break;
            }
            case CONNECTED: {
                if (this.getValue() != null) {
                    buf.append(" CONNECTED: ");
                    buf.append(this.getValue());
                    break;
                }
                buf.append(" DISCONNECTED");
                break;
            }
            case INTEREST_OPS: {
                buf.append(" INTEREST_CHANGED");
                break;
            }
            default: {
                buf.append(this.getState().name());
                buf.append(": ");
                buf.append(this.getValue());
                break;
            }
        }
        return buf.toString();
    }
}
