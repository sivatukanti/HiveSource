// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.http.websocketx;

import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.handler.codec.frame.TooLongFrameException;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneDecoder;

public class WebSocketFrameAggregator extends OneToOneDecoder
{
    private final int maxFrameSize;
    private WebSocketFrame currentFrame;
    private boolean tooLongFrameFound;
    
    public WebSocketFrameAggregator(final int maxFrameSize) {
        if (maxFrameSize < 1) {
            throw new IllegalArgumentException("maxFrameSize must be > 0");
        }
        this.maxFrameSize = maxFrameSize;
    }
    
    @Override
    protected Object decode(final ChannelHandlerContext ctx, final Channel channel, final Object message) throws Exception {
        if (!(message instanceof WebSocketFrame)) {
            return message;
        }
        final WebSocketFrame msg = (WebSocketFrame)message;
        if (this.currentFrame == null) {
            this.tooLongFrameFound = false;
            if (msg.isFinalFragment()) {
                return msg;
            }
            final ChannelBuffer buf = msg.getBinaryData();
            if (msg instanceof TextWebSocketFrame) {
                this.currentFrame = new TextWebSocketFrame(true, msg.getRsv(), buf);
            }
            else {
                if (!(msg instanceof BinaryWebSocketFrame)) {
                    throw new IllegalStateException("WebSocket frame was not of type TextWebSocketFrame or BinaryWebSocketFrame");
                }
                this.currentFrame = new BinaryWebSocketFrame(true, msg.getRsv(), buf);
            }
            return null;
        }
        else {
            if (!(msg instanceof ContinuationWebSocketFrame)) {
                return msg;
            }
            if (this.tooLongFrameFound) {
                if (msg.isFinalFragment()) {
                    this.currentFrame = null;
                }
                return null;
            }
            final ChannelBuffer content = this.currentFrame.getBinaryData();
            if (content.readableBytes() > this.maxFrameSize - msg.getBinaryData().readableBytes()) {
                this.tooLongFrameFound = true;
                throw new TooLongFrameException("WebSocketFrame length exceeded " + content + " bytes.");
            }
            this.currentFrame.setBinaryData(ChannelBuffers.wrappedBuffer(content, msg.getBinaryData()));
            if (msg.isFinalFragment()) {
                final WebSocketFrame currentFrame = this.currentFrame;
                this.currentFrame = null;
                return currentFrame;
            }
            return null;
        }
    }
}
