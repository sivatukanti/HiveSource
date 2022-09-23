// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.rtsp;

import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpMessage;
import org.jboss.netty.buffer.ChannelBuffer;

public class RtspRequestEncoder extends RtspMessageEncoder
{
    @Override
    protected void encodeInitialLine(final ChannelBuffer buf, final HttpMessage message) throws Exception {
        final HttpRequest request = (HttpRequest)message;
        buf.writeBytes(request.getMethod().toString().getBytes("ASCII"));
        buf.writeByte(32);
        buf.writeBytes(request.getUri().getBytes("UTF-8"));
        buf.writeByte(32);
        buf.writeBytes(request.getProtocolVersion().toString().getBytes("ASCII"));
        buf.writeByte(13);
        buf.writeByte(10);
    }
}
