// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.http.websocketx;

import org.jboss.netty.logging.InternalLoggerFactory;
import org.jboss.netty.handler.codec.frame.TooLongFrameException;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.handler.codec.frame.CorruptedFrameException;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.handler.codec.replay.ReplayingDecoder;

public class WebSocket08FrameDecoder extends ReplayingDecoder<State>
{
    private static final InternalLogger logger;
    private static final byte OPCODE_CONT = 0;
    private static final byte OPCODE_TEXT = 1;
    private static final byte OPCODE_BINARY = 2;
    private static final byte OPCODE_CLOSE = 8;
    private static final byte OPCODE_PING = 9;
    private static final byte OPCODE_PONG = 10;
    private Utf8Validator utf8Validator;
    private int fragmentedFramesCount;
    private final long maxFramePayloadLength;
    private boolean frameFinalFlag;
    private int frameRsv;
    private int frameOpcode;
    private long framePayloadLength;
    private ChannelBuffer framePayload;
    private int framePayloadBytesRead;
    private ChannelBuffer maskingKey;
    private final boolean allowExtensions;
    private final boolean maskedPayload;
    private boolean receivedClosingHandshake;
    
    public WebSocket08FrameDecoder(final boolean maskedPayload, final boolean allowExtensions) {
        this(maskedPayload, allowExtensions, Long.MAX_VALUE);
    }
    
    public WebSocket08FrameDecoder(final boolean maskedPayload, final boolean allowExtensions, final long maxFramePayloadLength) {
        super(State.FRAME_START);
        this.maskedPayload = maskedPayload;
        this.allowExtensions = allowExtensions;
        this.maxFramePayloadLength = maxFramePayloadLength;
    }
    
    @Override
    protected Object decode(final ChannelHandlerContext ctx, final Channel channel, final ChannelBuffer buffer, final State state) throws Exception {
        if (this.receivedClosingHandshake) {
            buffer.skipBytes(this.actualReadableBytes());
            return null;
        }
        switch (state) {
            case FRAME_START: {
                this.framePayloadBytesRead = 0;
                this.framePayloadLength = -1L;
                this.framePayload = null;
                byte b = buffer.readByte();
                this.frameFinalFlag = ((b & 0x80) != 0x0);
                this.frameRsv = (b & 0x70) >> 4;
                this.frameOpcode = (b & 0xF);
                if (WebSocket08FrameDecoder.logger.isDebugEnabled()) {
                    WebSocket08FrameDecoder.logger.debug("Decoding WebSocket Frame opCode=" + this.frameOpcode);
                }
                b = buffer.readByte();
                final boolean frameMasked = (b & 0x80) != 0x0;
                final int framePayloadLen1 = b & 0x7F;
                if (this.frameRsv != 0 && !this.allowExtensions) {
                    this.protocolViolation(channel, "RSV != 0 and no extension negotiated, RSV:" + this.frameRsv);
                    return null;
                }
                if (this.maskedPayload && !frameMasked) {
                    this.protocolViolation(channel, "unmasked client to server frame");
                    return null;
                }
                if (this.frameOpcode > 7) {
                    if (!this.frameFinalFlag) {
                        this.protocolViolation(channel, "fragmented control frame");
                        return null;
                    }
                    if (framePayloadLen1 > 125) {
                        this.protocolViolation(channel, "control frame with payload length > 125 octets");
                        return null;
                    }
                    if (this.frameOpcode != 8 && this.frameOpcode != 9 && this.frameOpcode != 10) {
                        this.protocolViolation(channel, "control frame using reserved opcode " + this.frameOpcode);
                        return null;
                    }
                    if (this.frameOpcode == 8 && framePayloadLen1 == 1) {
                        this.protocolViolation(channel, "received close control frame with payload len 1");
                        return null;
                    }
                }
                else {
                    if (this.frameOpcode != 0 && this.frameOpcode != 1 && this.frameOpcode != 2) {
                        this.protocolViolation(channel, "data frame using reserved opcode " + this.frameOpcode);
                        return null;
                    }
                    if (this.fragmentedFramesCount == 0 && this.frameOpcode == 0) {
                        this.protocolViolation(channel, "received continuation data frame outside fragmented message");
                        return null;
                    }
                    if (this.fragmentedFramesCount != 0 && this.frameOpcode != 0 && this.frameOpcode != 9) {
                        this.protocolViolation(channel, "received non-continuation data frame while inside fragmented message");
                        return null;
                    }
                }
                if (framePayloadLen1 == 126) {
                    this.framePayloadLength = buffer.readUnsignedShort();
                    if (this.framePayloadLength < 126L) {
                        this.protocolViolation(channel, "invalid data frame length (not using minimal length encoding)");
                        return null;
                    }
                }
                else if (framePayloadLen1 == 127) {
                    this.framePayloadLength = buffer.readLong();
                    if (this.framePayloadLength < 65536L) {
                        this.protocolViolation(channel, "invalid data frame length (not using minimal length encoding)");
                        return null;
                    }
                }
                else {
                    this.framePayloadLength = framePayloadLen1;
                }
                if (this.framePayloadLength > this.maxFramePayloadLength) {
                    this.protocolViolation(channel, "Max frame length of " + this.maxFramePayloadLength + " has been exceeded.");
                    return null;
                }
                if (WebSocket08FrameDecoder.logger.isDebugEnabled()) {
                    WebSocket08FrameDecoder.logger.debug("Decoding WebSocket Frame length=" + this.framePayloadLength);
                }
                this.checkpoint(State.MASKING_KEY);
            }
            case MASKING_KEY: {
                if (this.maskedPayload) {
                    this.maskingKey = buffer.readBytes(4);
                }
                this.checkpoint(State.PAYLOAD);
            }
            case PAYLOAD: {
                final int rbytes = this.actualReadableBytes();
                ChannelBuffer payloadBuffer = null;
                final long willHaveReadByteCount = this.framePayloadBytesRead + rbytes;
                if (willHaveReadByteCount == this.framePayloadLength) {
                    payloadBuffer = buffer.readBytes(rbytes);
                }
                else {
                    if (willHaveReadByteCount < this.framePayloadLength) {
                        payloadBuffer = buffer.readBytes(rbytes);
                        if (this.framePayload == null) {
                            this.framePayload = channel.getConfig().getBufferFactory().getBuffer(toFrameLength(this.framePayloadLength));
                        }
                        this.framePayload.writeBytes(payloadBuffer);
                        this.framePayloadBytesRead += rbytes;
                        return null;
                    }
                    if (willHaveReadByteCount > this.framePayloadLength) {
                        payloadBuffer = buffer.readBytes(toFrameLength(this.framePayloadLength - this.framePayloadBytesRead));
                    }
                }
                this.checkpoint(State.FRAME_START);
                if (this.framePayload == null) {
                    this.framePayload = payloadBuffer;
                }
                else {
                    this.framePayload.writeBytes(payloadBuffer);
                }
                if (this.maskedPayload) {
                    this.unmask(this.framePayload);
                }
                if (this.frameOpcode == 9) {
                    return new PingWebSocketFrame(this.frameFinalFlag, this.frameRsv, this.framePayload);
                }
                if (this.frameOpcode == 10) {
                    return new PongWebSocketFrame(this.frameFinalFlag, this.frameRsv, this.framePayload);
                }
                if (this.frameOpcode == 8) {
                    this.checkCloseFrameBody(channel, this.framePayload);
                    this.receivedClosingHandshake = true;
                    return new CloseWebSocketFrame(this.frameFinalFlag, this.frameRsv, this.framePayload);
                }
                if (this.frameFinalFlag) {
                    if (this.frameOpcode != 9) {
                        this.fragmentedFramesCount = 0;
                        if (this.frameOpcode == 1 || (this.utf8Validator != null && this.utf8Validator.isChecking())) {
                            this.checkUTF8String(channel, this.framePayload.array());
                            this.utf8Validator.finish();
                        }
                    }
                }
                else {
                    if (this.fragmentedFramesCount == 0) {
                        if (this.frameOpcode == 1) {
                            this.checkUTF8String(channel, this.framePayload.array());
                        }
                    }
                    else if (this.utf8Validator != null && this.utf8Validator.isChecking()) {
                        this.checkUTF8String(channel, this.framePayload.array());
                    }
                    ++this.fragmentedFramesCount;
                }
                if (this.frameOpcode == 1) {
                    return new TextWebSocketFrame(this.frameFinalFlag, this.frameRsv, this.framePayload);
                }
                if (this.frameOpcode == 2) {
                    return new BinaryWebSocketFrame(this.frameFinalFlag, this.frameRsv, this.framePayload);
                }
                if (this.frameOpcode == 0) {
                    return new ContinuationWebSocketFrame(this.frameFinalFlag, this.frameRsv, this.framePayload);
                }
                throw new UnsupportedOperationException("Cannot decode web socket frame with opcode: " + this.frameOpcode);
            }
            case CORRUPT: {
                buffer.readByte();
                return null;
            }
            default: {
                throw new Error("Shouldn't reach here.");
            }
        }
    }
    
    private void unmask(final ChannelBuffer frame) {
        final byte[] bytes = frame.array();
        for (int i = 0; i < bytes.length; ++i) {
            frame.setByte(i, frame.getByte(i) ^ this.maskingKey.getByte(i % 4));
        }
    }
    
    private void protocolViolation(final Channel channel, final String reason) throws CorruptedFrameException {
        this.protocolViolation(channel, new CorruptedFrameException(reason));
    }
    
    private void protocolViolation(final Channel channel, final CorruptedFrameException ex) throws CorruptedFrameException {
        this.checkpoint(State.CORRUPT);
        if (channel.isConnected()) {
            channel.write(ChannelBuffers.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        }
        throw ex;
    }
    
    private static int toFrameLength(final long l) throws TooLongFrameException {
        if (l > 2147483647L) {
            throw new TooLongFrameException("Length:" + l);
        }
        return (int)l;
    }
    
    private void checkUTF8String(final Channel channel, final byte[] bytes) throws CorruptedFrameException {
        try {
            if (this.utf8Validator == null) {
                this.utf8Validator = new Utf8Validator();
            }
            this.utf8Validator.check(bytes);
        }
        catch (CorruptedFrameException ex) {
            this.protocolViolation(channel, ex);
        }
    }
    
    protected void checkCloseFrameBody(final Channel channel, final ChannelBuffer buffer) throws CorruptedFrameException {
        if (buffer == null || buffer.capacity() == 0) {
            return;
        }
        if (buffer.capacity() == 1) {
            this.protocolViolation(channel, "Invalid close frame body");
        }
        final int idx = buffer.readerIndex();
        buffer.readerIndex(0);
        final int statusCode = buffer.readShort();
        if ((statusCode >= 0 && statusCode <= 999) || (statusCode >= 1004 && statusCode <= 1006) || (statusCode >= 1012 && statusCode <= 2999)) {
            this.protocolViolation(channel, "Invalid close frame status code: " + statusCode);
        }
        if (buffer.readableBytes() > 0) {
            final byte[] b = new byte[buffer.readableBytes()];
            buffer.readBytes(b);
            try {
                final Utf8Validator validator = new Utf8Validator();
                validator.check(b);
            }
            catch (CorruptedFrameException ex) {
                this.protocolViolation(channel, ex);
            }
        }
        buffer.readerIndex(idx);
    }
    
    static {
        logger = InternalLoggerFactory.getInstance(WebSocket08FrameDecoder.class);
    }
    
    public enum State
    {
        FRAME_START, 
        MASKING_KEY, 
        PAYLOAD, 
        CORRUPT;
    }
}
