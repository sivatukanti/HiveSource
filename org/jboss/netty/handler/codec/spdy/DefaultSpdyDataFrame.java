// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.spdy;

import org.jboss.netty.util.internal.StringUtil;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.buffer.ChannelBuffer;

public class DefaultSpdyDataFrame extends DefaultSpdyStreamFrame implements SpdyDataFrame
{
    private ChannelBuffer data;
    
    public DefaultSpdyDataFrame(final int streamId) {
        super(streamId);
        this.data = ChannelBuffers.EMPTY_BUFFER;
    }
    
    public ChannelBuffer getData() {
        return this.data;
    }
    
    public void setData(ChannelBuffer data) {
        if (data == null) {
            data = ChannelBuffers.EMPTY_BUFFER;
        }
        if (data.readableBytes() > 16777215) {
            throw new IllegalArgumentException("data payload cannot exceed 16777215 bytes");
        }
        this.data = data;
    }
    
    @Override
    public String toString() {
        final StringBuilder buf = new StringBuilder();
        buf.append(this.getClass().getSimpleName());
        buf.append("(last: ");
        buf.append(this.isLast());
        buf.append(')');
        buf.append(StringUtil.NEWLINE);
        buf.append("--> Stream-ID = ");
        buf.append(this.getStreamId());
        buf.append(StringUtil.NEWLINE);
        buf.append("--> Size = ");
        buf.append(this.getData().readableBytes());
        return buf.toString();
    }
}
