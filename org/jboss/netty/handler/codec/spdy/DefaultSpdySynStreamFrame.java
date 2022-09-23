// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.spdy;

import org.jboss.netty.util.internal.StringUtil;

public class DefaultSpdySynStreamFrame extends DefaultSpdyHeadersFrame implements SpdySynStreamFrame
{
    private int associatedToStreamId;
    private byte priority;
    private boolean unidirectional;
    
    public DefaultSpdySynStreamFrame(final int streamId, final int associatedToStreamId, final byte priority) {
        super(streamId);
        this.setAssociatedToStreamId(associatedToStreamId);
        this.setPriority(priority);
    }
    
    public int getAssociatedToStreamId() {
        return this.associatedToStreamId;
    }
    
    public void setAssociatedToStreamId(final int associatedToStreamId) {
        if (associatedToStreamId < 0) {
            throw new IllegalArgumentException("Associated-To-Stream-ID cannot be negative: " + associatedToStreamId);
        }
        this.associatedToStreamId = associatedToStreamId;
    }
    
    public byte getPriority() {
        return this.priority;
    }
    
    public void setPriority(final byte priority) {
        if (priority < 0 || priority > 7) {
            throw new IllegalArgumentException("Priority must be between 0 and 7 inclusive: " + priority);
        }
        this.priority = priority;
    }
    
    public boolean isUnidirectional() {
        return this.unidirectional;
    }
    
    public void setUnidirectional(final boolean unidirectional) {
        this.unidirectional = unidirectional;
    }
    
    @Override
    public String toString() {
        final StringBuilder buf = new StringBuilder();
        buf.append(this.getClass().getSimpleName());
        buf.append("(last: ");
        buf.append(this.isLast());
        buf.append("; unidirectional: ");
        buf.append(this.isUnidirectional());
        buf.append(')');
        buf.append(StringUtil.NEWLINE);
        buf.append("--> Stream-ID = ");
        buf.append(this.getStreamId());
        buf.append(StringUtil.NEWLINE);
        if (this.associatedToStreamId != 0) {
            buf.append("--> Associated-To-Stream-ID = ");
            buf.append(this.getAssociatedToStreamId());
            buf.append(StringUtil.NEWLINE);
        }
        buf.append("--> Priority = ");
        buf.append(this.getPriority());
        buf.append(StringUtil.NEWLINE);
        buf.append("--> Headers:");
        buf.append(StringUtil.NEWLINE);
        this.appendHeaders(buf);
        buf.setLength(buf.length() - StringUtil.NEWLINE.length());
        return buf.toString();
    }
}
