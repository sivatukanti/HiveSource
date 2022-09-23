// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel;

public class DefaultWriteCompletionEvent implements WriteCompletionEvent
{
    private final Channel channel;
    private final long writtenAmount;
    
    public DefaultWriteCompletionEvent(final Channel channel, final long writtenAmount) {
        if (channel == null) {
            throw new NullPointerException("channel");
        }
        if (writtenAmount <= 0L) {
            throw new IllegalArgumentException("writtenAmount must be a positive integer: " + writtenAmount);
        }
        this.channel = channel;
        this.writtenAmount = writtenAmount;
    }
    
    public Channel getChannel() {
        return this.channel;
    }
    
    public ChannelFuture getFuture() {
        return Channels.succeededFuture(this.getChannel());
    }
    
    public long getWrittenAmount() {
        return this.writtenAmount;
    }
    
    @Override
    public String toString() {
        final String channelString = this.getChannel().toString();
        final StringBuilder buf = new StringBuilder(channelString.length() + 32);
        buf.append(channelString);
        buf.append(" WRITTEN_AMOUNT: ");
        buf.append(this.getWrittenAmount());
        return buf.toString();
    }
}
