// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.rtsp;

import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpMessage;
import org.jboss.netty.buffer.ChannelBuffer;

public class RtspResponseEncoder extends RtspMessageEncoder
{
    @Override
    protected void encodeInitialLine(final ChannelBuffer buf, final HttpMessage message) throws Exception {
        final HttpResponse response = (HttpResponse)message;
        buf.writeBytes(response.getProtocolVersion().toString().getBytes("ASCII"));
        buf.writeByte(32);
        buf.writeBytes(String.valueOf(response.getStatus().getCode()).getBytes("ASCII"));
        buf.writeByte(32);
        buf.writeBytes(String.valueOf(response.getStatus().getReasonPhrase()).getBytes("ASCII"));
        buf.writeByte(13);
        buf.writeByte(10);
    }
}
