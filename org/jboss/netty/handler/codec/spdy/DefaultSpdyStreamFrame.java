// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.spdy;

public abstract class DefaultSpdyStreamFrame implements SpdyStreamFrame
{
    private int streamId;
    private boolean last;
    
    protected DefaultSpdyStreamFrame(final int streamId) {
        this.setStreamId(streamId);
    }
    
    public int getStreamId() {
        return this.streamId;
    }
    
    public void setStreamId(final int streamId) {
        if (streamId <= 0) {
            throw new IllegalArgumentException("Stream-ID must be positive: " + streamId);
        }
        this.streamId = streamId;
    }
    
    public boolean isLast() {
        return this.last;
    }
    
    public void setLast(final boolean last) {
        this.last = last;
    }
}
