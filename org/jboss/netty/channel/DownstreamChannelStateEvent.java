// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel;

public class DownstreamChannelStateEvent implements ChannelStateEvent
{
    private final Channel channel;
    private final ChannelFuture future;
    private final ChannelState state;
    private final Object value;
    
    public DownstreamChannelStateEvent(final Channel channel, final ChannelFuture future, final ChannelState state, final Object value) {
        if (channel == null) {
            throw new NullPointerException("channel");
        }
        if (future == null) {
            throw new NullPointerException("future");
        }
        if (state == null) {
            throw new NullPointerException("state");
        }
        this.channel = channel;
        this.future = future;
        this.state = state;
        this.value = value;
    }
    
    public Channel getChannel() {
        return this.channel;
    }
    
    public ChannelFuture getFuture() {
        return this.future;
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
                buf.append(" CLOSE");
                break;
            }
            case BOUND: {
                if (this.getValue() != null) {
                    buf.append(" BIND: ");
                    buf.append(this.getValue());
                    break;
                }
                buf.append(" UNBIND");
                break;
            }
            case CONNECTED: {
                if (this.getValue() != null) {
                    buf.append(" CONNECT: ");
                    buf.append(this.getValue());
                    break;
                }
                buf.append(" DISCONNECT");
                break;
            }
            case INTEREST_OPS: {
                buf.append(" CHANGE_INTEREST: ");
                buf.append(this.getValue());
                break;
            }
            default: {
                buf.append(' ');
                buf.append(this.getState().name());
                buf.append(": ");
                buf.append(this.getValue());
                break;
            }
        }
        return buf.toString();
    }
}
