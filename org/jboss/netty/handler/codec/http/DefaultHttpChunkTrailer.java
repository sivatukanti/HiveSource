// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.http;

import java.util.Iterator;
import java.util.Map;
import org.jboss.netty.util.internal.StringUtil;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.buffer.ChannelBuffer;

public class DefaultHttpChunkTrailer implements HttpChunkTrailer
{
    private final HttpHeaders trailingHeaders;
    
    public DefaultHttpChunkTrailer() {
        this.trailingHeaders = new TrailingHeaders(true);
    }
    
    public boolean isLast() {
        return true;
    }
    
    public ChannelBuffer getContent() {
        return ChannelBuffers.EMPTY_BUFFER;
    }
    
    public void setContent(final ChannelBuffer content) {
        throw new IllegalStateException("read-only");
    }
    
    public HttpHeaders trailingHeaders() {
        return this.trailingHeaders;
    }
    
    @Override
    public String toString() {
        final StringBuilder buf = new StringBuilder(super.toString());
        buf.append(StringUtil.NEWLINE);
        this.appendHeaders(buf);
        buf.setLength(buf.length() - StringUtil.NEWLINE.length());
        return buf.toString();
    }
    
    private void appendHeaders(final StringBuilder buf) {
        for (final Map.Entry<String, String> e : this.trailingHeaders()) {
            buf.append(e.getKey());
            buf.append(": ");
            buf.append(e.getValue());
            buf.append(StringUtil.NEWLINE);
        }
    }
    
    private static final class TrailingHeaders extends DefaultHttpHeaders
    {
        TrailingHeaders(final boolean validateHeaders) {
            super(validateHeaders);
        }
        
        @Override
        public HttpHeaders add(final String name, final Object value) {
            if (this.validate) {
                validateName(name);
            }
            return super.add(name, value);
        }
        
        @Override
        public HttpHeaders add(final String name, final Iterable<?> values) {
            if (this.validate) {
                validateName(name);
            }
            return super.add(name, values);
        }
        
        @Override
        public HttpHeaders set(final String name, final Iterable<?> values) {
            if (this.validate) {
                validateName(name);
            }
            return super.set(name, values);
        }
        
        @Override
        public HttpHeaders set(final String name, final Object value) {
            if (this.validate) {
                validateName(name);
            }
            return super.set(name, value);
        }
        
        private static void validateName(final String name) {
            if (name.equalsIgnoreCase("Content-Length") || name.equalsIgnoreCase("Transfer-Encoding") || name.equalsIgnoreCase("Trailer")) {
                throw new IllegalArgumentException("prohibited trailing header: " + name);
            }
        }
    }
}
