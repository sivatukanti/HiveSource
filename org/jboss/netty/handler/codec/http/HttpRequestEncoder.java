// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.http;

import org.jboss.netty.buffer.ChannelBuffer;

public class HttpRequestEncoder extends HttpMessageEncoder
{
    private static final char SLASH = '/';
    private static final char QUESTION_MARK = '?';
    
    @Override
    protected void encodeInitialLine(final ChannelBuffer buf, final HttpMessage message) throws Exception {
        final HttpRequest request = (HttpRequest)message;
        buf.writeBytes(request.getMethod().toString().getBytes("ASCII"));
        buf.writeByte(32);
        String uri = request.getUri();
        final int start = uri.indexOf("://");
        if (start != -1) {
            final int startIndex = start + 3;
            final int index = uri.indexOf(63, startIndex);
            if (index == -1) {
                if (uri.lastIndexOf(47) <= startIndex) {
                    uri += '/';
                }
            }
            else if (uri.lastIndexOf(47, index) <= startIndex) {
                final int len = uri.length();
                final StringBuilder sb = new StringBuilder(len + 1);
                sb.append(uri, 0, index);
                sb.append('/');
                sb.append(uri, index, len);
                uri = sb.toString();
            }
        }
        buf.writeBytes(uri.getBytes("UTF-8"));
        buf.writeByte(32);
        buf.writeBytes(request.getProtocolVersion().toString().getBytes("ASCII"));
        buf.writeByte(13);
        buf.writeByte(10);
    }
}
