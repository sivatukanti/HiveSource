// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.spdy;

import org.jboss.netty.util.internal.StringUtil;

public class DefaultSpdyRstStreamFrame extends DefaultSpdyStreamFrame implements SpdyRstStreamFrame
{
    private SpdyStreamStatus status;
    
    public DefaultSpdyRstStreamFrame(final int streamId, final int statusCode) {
        this(streamId, SpdyStreamStatus.valueOf(statusCode));
    }
    
    public DefaultSpdyRstStreamFrame(final int streamId, final SpdyStreamStatus status) {
        super(streamId);
        this.setStatus(status);
    }
    
    public SpdyStreamStatus getStatus() {
        return this.status;
    }
    
    public void setStatus(final SpdyStreamStatus status) {
        this.status = status;
    }
    
    @Override
    public String toString() {
        final StringBuilder buf = new StringBuilder();
        buf.append(this.getClass().getSimpleName());
        buf.append(StringUtil.NEWLINE);
        buf.append("--> Stream-ID = ");
        buf.append(this.getStreamId());
        buf.append(StringUtil.NEWLINE);
        buf.append("--> Status: ");
        buf.append(this.getStatus().toString());
        return buf.toString();
    }
}
