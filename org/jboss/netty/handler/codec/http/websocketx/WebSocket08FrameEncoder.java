// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.http.websocketx;

import org.jboss.netty.logging.InternalLoggerFactory;
import org.jboss.netty.buffer.ChannelBuffer;
import java.nio.ByteBuffer;
import org.jboss.netty.handler.codec.frame.TooLongFrameException;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;

public class WebSocket08FrameEncoder extends OneToOneEncoder
{
    private static final InternalLogger logger;
    private static final byte OPCODE_CONT = 0;
    private static final byte OPCODE_TEXT = 1;
    private static final byte OPCODE_BINARY = 2;
    private static final byte OPCODE_CLOSE = 8;
    private static final byte OPCODE_PING = 9;
    private static final byte OPCODE_PONG = 10;
    private final boolean maskPayload;
    
    public WebSocket08FrameEncoder(final boolean maskPayload) {
        this.maskPayload = maskPayload;
    }
    
    @Override
    protected Object encode(final ChannelHandlerContext ctx, final Channel channel, final Object msg) throws Exception {
        if (!(msg instanceof WebSocketFrame)) {
            return msg;
        }
        final WebSocketFrame frame = (WebSocketFrame)msg;
        ChannelBuffer data = frame.getBinaryData();
        if (data == null) {
            data = ChannelBuffers.EMPTY_BUFFER;
        }
        byte opcode;
        if (frame instanceof TextWebSocketFrame) {
            opcode = 1;
        }
        else if (frame instanceof PingWebSocketFrame) {
            opcode = 9;
        }
        else if (frame instanceof PongWebSocketFrame) {
            opcode = 10;
        }
        else if (frame instanceof CloseWebSocketFrame) {
            opcode = 8;
        }
        else if (frame instanceof BinaryWebSocketFrame) {
            opcode = 2;
        }
        else {
            if (!(frame instanceof ContinuationWebSocketFrame)) {
                throw new UnsupportedOperationException("Cannot encode frame of type: " + frame.getClass().getName());
            }
            opcode = 0;
        }
        final int length = data.readableBytes();
        if (WebSocket08FrameEncoder.logger.isDebugEnabled()) {
            WebSocket08FrameEncoder.logger.debug("Encoding WebSocket Frame opCode=" + opcode + " length=" + length);
        }
        int b0 = 0;
        if (frame.isFinalFragment()) {
            b0 |= 0x80;
        }
        b0 |= frame.getRsv() % 8 << 4;
        b0 |= opcode % 128;
        if (opcode == 9 && length > 125) {
            throw new TooLongFrameException("invalid payload for PING (payload length must be <= 125, was " + length);
        }
        final int maskLength = this.maskPayload ? 4 : 0;
        ChannelBuffer header;
        if (length <= 125) {
            header = ChannelBuffers.buffer(2 + maskLength);
            header.writeByte(b0);
            final byte b2 = (byte)(this.maskPayload ? (0x80 | (byte)length) : ((byte)length));
            header.writeByte(b2);
        }
        else if (length <= 65535) {
            header = ChannelBuffers.buffer(4 + maskLength);
            header.writeByte(b0);
            header.writeByte(this.maskPayload ? 254 : 126);
            header.writeByte(length >>> 8 & 0xFF);
            header.writeByte(length & 0xFF);
        }
        else {
            header = ChannelBuffers.buffer(10 + maskLength);
            header.writeByte(b0);
            header.writeByte(this.maskPayload ? 255 : 127);
            header.writeLong(length);
        }
        ChannelBuffer body;
        if (this.maskPayload) {
            final Integer random = (int)(Math.random() * 2.147483647E9);
            final byte[] mask = ByteBuffer.allocate(4).putInt(random).array();
            header.writeBytes(mask);
            body = ChannelBuffers.buffer(length);
            int counter = 0;
            while (data.readableBytes() > 0) {
                final byte byteData = data.readByte();
                body.writeByte(byteData ^ mask[counter++ % 4]);
            }
        }
        else {
            body = data;
        }
        return ChannelBuffers.wrappedBuffer(header, body);
    }
    
    static {
        logger = InternalLoggerFactory.getInstance(WebSocket08FrameEncoder.class);
    }
}
