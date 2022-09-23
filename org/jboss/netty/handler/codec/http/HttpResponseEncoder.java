// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.http;

import org.jboss.netty.buffer.ChannelBuffer;

public class HttpResponseEncoder extends HttpMessageEncoder
{
    @Override
    protected void encodeInitialLine(final ChannelBuffer buf, final HttpMessage message) throws Exception {
        final HttpResponse response = (HttpResponse)message;
        HttpMessageEncoder.encodeAscii(response.getProtocolVersion().toString(), buf);
        buf.writeByte(32);
        HttpMessageEncoder.encodeAscii(String.valueOf(response.getStatus().getCode()), buf);
        buf.writeByte(32);
        HttpMessageEncoder.encodeAscii(String.valueOf(response.getStatus().getReasonPhrase()), buf);
        buf.writeByte(13);
        buf.writeByte(10);
    }
}
