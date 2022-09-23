// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.spdy;

import java.util.Iterator;
import java.util.Map;
import org.jboss.netty.util.internal.StringUtil;

public class DefaultSpdyHeadersFrame extends DefaultSpdyStreamFrame implements SpdyHeadersFrame
{
    private boolean invalid;
    private boolean truncated;
    private final SpdyHeaders headers;
    
    public DefaultSpdyHeadersFrame(final int streamId) {
        super(streamId);
        this.headers = new DefaultSpdyHeaders();
    }
    
    public boolean isInvalid() {
        return this.invalid;
    }
    
    public void setInvalid() {
        this.invalid = true;
    }
    
    public boolean isTruncated() {
        return this.truncated;
    }
    
    public void setTruncated() {
        this.truncated = true;
    }
    
    public SpdyHeaders headers() {
        return this.headers;
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
        buf.append("--> Headers:");
        buf.append(StringUtil.NEWLINE);
        this.appendHeaders(buf);
        buf.setLength(buf.length() - StringUtil.NEWLINE.length());
        return buf.toString();
    }
    
    protected void appendHeaders(final StringBuilder buf) {
        for (final Map.Entry<String, String> e : this.headers()) {
            buf.append("    ");
            buf.append(e.getKey());
            buf.append(": ");
            buf.append(e.getValue());
            buf.append(StringUtil.NEWLINE);
        }
    }
}
