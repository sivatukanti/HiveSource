// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.http.websocketx;

import org.jboss.netty.handler.codec.frame.TooLongFrameException;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.replay.VoidEnum;
import org.jboss.netty.handler.codec.replay.ReplayingDecoder;

public class WebSocket00FrameDecoder extends ReplayingDecoder<VoidEnum>
{
    private static final long DEFAULT_MAX_FRAME_SIZE = 16384L;
    private final long maxFrameSize;
    private boolean receivedClosingHandshake;
    
    public WebSocket00FrameDecoder() {
        this(16384L);
    }
    
    public WebSocket00FrameDecoder(final long maxFrameSize) {
        this.maxFrameSize = maxFrameSize;
    }
    
    @Override
    protected Object decode(final ChannelHandlerContext ctx, final Channel channel, final ChannelBuffer buffer, final VoidEnum state) throws Exception {
        if (this.receivedClosingHandshake) {
            buffer.skipBytes(this.actualReadableBytes());
            return null;
        }
        final byte type = buffer.readByte();
        if ((type & 0x80) == 0x80) {
            return this.decodeBinaryFrame(type, buffer);
        }
        return this.decodeTextFrame(buffer);
    }
    
    private WebSocketFrame decodeBinaryFrame(final byte type, final ChannelBuffer buffer) throws TooLongFrameException {
        long frameSize = 0L;
        int lengthFieldSize = 0;
        byte b;
        do {
            b = buffer.readByte();
            frameSize <<= 7;
            frameSize |= (b & 0x7F);
            if (frameSize > this.maxFrameSize) {
                throw new TooLongFrameException();
            }
            if (++lengthFieldSize > 8) {
                throw new TooLongFrameException();
            }
        } while ((b & 0x80) == 0x80);
        if (type == -1 && frameSize == 0L) {
            this.receivedClosingHandshake = true;
            return new CloseWebSocketFrame();
        }
        return new BinaryWebSocketFrame(buffer.readBytes((int)frameSize));
    }
    
    private WebSocketFrame decodeTextFrame(final ChannelBuffer buffer) throws TooLongFrameException {
        final int ridx = buffer.readerIndex();
        final int rbytes = this.actualReadableBytes();
        final int delimPos = buffer.indexOf(ridx, ridx + rbytes, (byte)(-1));
        if (delimPos == -1) {
            if (rbytes > this.maxFrameSize) {
                throw new TooLongFrameException();
            }
            return null;
        }
        else {
            final int frameSize = delimPos - ridx;
            if (frameSize > this.maxFrameSize) {
                throw new TooLongFrameException();
            }
            final ChannelBuffer binaryData = buffer.readBytes(frameSize);
            buffer.skipBytes(1);
            final int ffDelimPos = binaryData.indexOf(binaryData.readerIndex(), binaryData.writerIndex(), (byte)(-1));
            if (ffDelimPos >= 0) {
                throw new IllegalArgumentException("a text frame should not contain 0xFF.");
            }
            return new TextWebSocketFrame(binaryData);
        }
    }
}
