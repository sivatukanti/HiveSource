// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.http;

import java.util.Iterator;
import java.util.Map;
import org.jboss.netty.util.internal.StringUtil;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.buffer.ChannelBuffer;

public class DefaultHttpMessage implements HttpMessage
{
    private final HttpHeaders headers;
    private HttpVersion version;
    private ChannelBuffer content;
    private boolean chunked;
    
    protected DefaultHttpMessage(final HttpVersion version) {
        this.headers = new DefaultHttpHeaders(true);
        this.content = ChannelBuffers.EMPTY_BUFFER;
        this.setProtocolVersion(version);
    }
    
    public HttpHeaders headers() {
        return this.headers;
    }
    
    public boolean isChunked() {
        return this.chunked || HttpCodecUtil.isTransferEncodingChunked(this);
    }
    
    public void setChunked(final boolean chunked) {
        this.chunked = chunked;
        if (chunked) {
            this.setContent(ChannelBuffers.EMPTY_BUFFER);
        }
    }
    
    public void setContent(ChannelBuffer content) {
        if (content == null) {
            content = ChannelBuffers.EMPTY_BUFFER;
        }
        if (content.readable() && this.isChunked()) {
            throw new IllegalArgumentException("non-empty content disallowed if this.chunked == true");
        }
        this.content = content;
    }
    
    public HttpVersion getProtocolVersion() {
        return this.version;
    }
    
    public void setProtocolVersion(final HttpVersion version) {
        if (version == null) {
            throw new NullPointerException("version");
        }
        this.version = version;
    }
    
    public ChannelBuffer getContent() {
        return this.content;
    }
    
    @Override
    public String toString() {
        final StringBuilder buf = new StringBuilder();
        buf.append(this.getClass().getSimpleName());
        buf.append("(version: ");
        buf.append(this.getProtocolVersion().getText());
        buf.append(", keepAlive: ");
        buf.append(HttpHeaders.isKeepAlive(this));
        buf.append(", chunked: ");
        buf.append(this.isChunked());
        buf.append(')');
        buf.append(StringUtil.NEWLINE);
        this.appendHeaders(buf);
        buf.setLength(buf.length() - StringUtil.NEWLINE.length());
        return buf.toString();
    }
    
    void appendHeaders(final StringBuilder buf) {
        for (final Map.Entry<String, String> e : this.headers()) {
            buf.append(e.getKey());
            buf.append(": ");
            buf.append(e.getValue());
            buf.append(StringUtil.NEWLINE);
        }
    }
}
