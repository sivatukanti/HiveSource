// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.http.websocketx;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;

@ChannelHandler.Sharable
public class WebSocket00FrameEncoder extends OneToOneEncoder
{
    @Override
    protected Object encode(final ChannelHandlerContext ctx, final Channel channel, final Object msg) throws Exception {
        if (!(msg instanceof WebSocketFrame)) {
            return msg;
        }
        final WebSocketFrame frame = (WebSocketFrame)msg;
        if (frame instanceof TextWebSocketFrame) {
            final ChannelBuffer data = frame.getBinaryData();
            final ChannelBuffer encoded = channel.getConfig().getBufferFactory().getBuffer(data.order(), data.readableBytes() + 2);
            encoded.writeByte(0);
            encoded.writeBytes(data, data.readerIndex(), data.readableBytes());
            encoded.writeByte(-1);
            return encoded;
        }
        if (frame instanceof CloseWebSocketFrame) {
            final ChannelBuffer data = frame.getBinaryData();
            final ChannelBuffer encoded = channel.getConfig().getBufferFactory().getBuffer(data.order(), 2);
            encoded.writeByte(-1);
            encoded.writeByte(0);
            return encoded;
        }
        final ChannelBuffer data = frame.getBinaryData();
        final int dataLen = data.readableBytes();
        final ChannelBuffer encoded2 = channel.getConfig().getBufferFactory().getBuffer(data.order(), dataLen + 5);
        encoded2.writeByte(-128);
        final int b1 = dataLen >>> 28 & 0x7F;
        final int b2 = dataLen >>> 14 & 0x7F;
        final int b3 = dataLen >>> 7 & 0x7F;
        final int b4 = dataLen & 0x7F;
        if (b1 == 0) {
            if (b2 == 0) {
                if (b3 == 0) {
                    encoded2.writeByte(b4);
                }
                else {
                    encoded2.writeByte(b3 | 0x80);
                    encoded2.writeByte(b4);
                }
            }
            else {
                encoded2.writeByte(b2 | 0x80);
                encoded2.writeByte(b3 | 0x80);
                encoded2.writeByte(b4);
            }
        }
        else {
            encoded2.writeByte(b1 | 0x80);
            encoded2.writeByte(b2 | 0x80);
            encoded2.writeByte(b3 | 0x80);
            encoded2.writeByte(b4);
        }
        encoded2.writeBytes(data, data.readerIndex(), dataLen);
        return encoded2;
    }
}
