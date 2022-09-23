// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.frame;

import org.jboss.netty.channel.Channels;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;

public class LengthFieldBasedFrameDecoder extends FrameDecoder
{
    private final int maxFrameLength;
    private final int lengthFieldOffset;
    private final int lengthFieldLength;
    private final int lengthFieldEndOffset;
    private final int lengthAdjustment;
    private final int initialBytesToStrip;
    private final boolean failFast;
    private boolean discardingTooLongFrame;
    private long tooLongFrameLength;
    private long bytesToDiscard;
    
    public LengthFieldBasedFrameDecoder(final int maxFrameLength, final int lengthFieldOffset, final int lengthFieldLength) {
        this(maxFrameLength, lengthFieldOffset, lengthFieldLength, 0, 0);
    }
    
    public LengthFieldBasedFrameDecoder(final int maxFrameLength, final int lengthFieldOffset, final int lengthFieldLength, final int lengthAdjustment, final int initialBytesToStrip) {
        this(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip, false);
    }
    
    public LengthFieldBasedFrameDecoder(final int maxFrameLength, final int lengthFieldOffset, final int lengthFieldLength, final int lengthAdjustment, final int initialBytesToStrip, final boolean failFast) {
        if (maxFrameLength <= 0) {
            throw new IllegalArgumentException("maxFrameLength must be a positive integer: " + maxFrameLength);
        }
        if (lengthFieldOffset < 0) {
            throw new IllegalArgumentException("lengthFieldOffset must be a non-negative integer: " + lengthFieldOffset);
        }
        if (initialBytesToStrip < 0) {
            throw new IllegalArgumentException("initialBytesToStrip must be a non-negative integer: " + initialBytesToStrip);
        }
        if (lengthFieldLength != 1 && lengthFieldLength != 2 && lengthFieldLength != 3 && lengthFieldLength != 4 && lengthFieldLength != 8) {
            throw new IllegalArgumentException("lengthFieldLength must be either 1, 2, 3, 4, or 8: " + lengthFieldLength);
        }
        if (lengthFieldOffset > maxFrameLength - lengthFieldLength) {
            throw new IllegalArgumentException("maxFrameLength (" + maxFrameLength + ") " + "must be equal to or greater than " + "lengthFieldOffset (" + lengthFieldOffset + ") + " + "lengthFieldLength (" + lengthFieldLength + ").");
        }
        this.maxFrameLength = maxFrameLength;
        this.lengthFieldOffset = lengthFieldOffset;
        this.lengthFieldLength = lengthFieldLength;
        this.lengthAdjustment = lengthAdjustment;
        this.lengthFieldEndOffset = lengthFieldOffset + lengthFieldLength;
        this.initialBytesToStrip = initialBytesToStrip;
        this.failFast = failFast;
    }
    
    @Override
    protected Object decode(final ChannelHandlerContext ctx, final Channel channel, final ChannelBuffer buffer) throws Exception {
        if (this.discardingTooLongFrame) {
            long bytesToDiscard = this.bytesToDiscard;
            final int localBytesToDiscard = (int)Math.min(bytesToDiscard, buffer.readableBytes());
            buffer.skipBytes(localBytesToDiscard);
            bytesToDiscard -= localBytesToDiscard;
            this.bytesToDiscard = bytesToDiscard;
            this.failIfNecessary(ctx, false);
            return null;
        }
        if (buffer.readableBytes() < this.lengthFieldEndOffset) {
            return null;
        }
        final int actualLengthFieldOffset = buffer.readerIndex() + this.lengthFieldOffset;
        long frameLength = 0L;
        switch (this.lengthFieldLength) {
            case 1: {
                frameLength = buffer.getUnsignedByte(actualLengthFieldOffset);
                break;
            }
            case 2: {
                frameLength = buffer.getUnsignedShort(actualLengthFieldOffset);
                break;
            }
            case 3: {
                frameLength = buffer.getUnsignedMedium(actualLengthFieldOffset);
                break;
            }
            case 4: {
                frameLength = buffer.getUnsignedInt(actualLengthFieldOffset);
                break;
            }
            case 8: {
                frameLength = buffer.getLong(actualLengthFieldOffset);
                break;
            }
            default: {
                throw new Error("should not reach here");
            }
        }
        if (frameLength < 0L) {
            buffer.skipBytes(this.lengthFieldEndOffset);
            throw new CorruptedFrameException("negative pre-adjustment length field: " + frameLength);
        }
        frameLength += this.lengthAdjustment + this.lengthFieldEndOffset;
        if (frameLength < this.lengthFieldEndOffset) {
            buffer.skipBytes(this.lengthFieldEndOffset);
            throw new CorruptedFrameException("Adjusted frame length (" + frameLength + ") is less " + "than lengthFieldEndOffset: " + this.lengthFieldEndOffset);
        }
        if (frameLength > this.maxFrameLength) {
            this.discardingTooLongFrame = true;
            this.tooLongFrameLength = frameLength;
            this.bytesToDiscard = frameLength - buffer.readableBytes();
            buffer.skipBytes(buffer.readableBytes());
            this.failIfNecessary(ctx, true);
            return null;
        }
        final int frameLengthInt = (int)frameLength;
        if (buffer.readableBytes() < frameLengthInt) {
            return null;
        }
        if (this.initialBytesToStrip > frameLengthInt) {
            buffer.skipBytes(frameLengthInt);
            throw new CorruptedFrameException("Adjusted frame length (" + frameLength + ") is less " + "than initialBytesToStrip: " + this.initialBytesToStrip);
        }
        buffer.skipBytes(this.initialBytesToStrip);
        final int readerIndex = buffer.readerIndex();
        final int actualFrameLength = frameLengthInt - this.initialBytesToStrip;
        final ChannelBuffer frame = this.extractFrame(buffer, readerIndex, actualFrameLength);
        buffer.readerIndex(readerIndex + actualFrameLength);
        return frame;
    }
    
    private void failIfNecessary(final ChannelHandlerContext ctx, final boolean firstDetectionOfTooLongFrame) {
        if (this.bytesToDiscard == 0L) {
            final long tooLongFrameLength = this.tooLongFrameLength;
            this.tooLongFrameLength = 0L;
            this.discardingTooLongFrame = false;
            if (!this.failFast || (this.failFast && firstDetectionOfTooLongFrame)) {
                this.fail(ctx, tooLongFrameLength);
            }
        }
        else if (this.failFast && firstDetectionOfTooLongFrame) {
            this.fail(ctx, this.tooLongFrameLength);
        }
    }
    
    private void fail(final ChannelHandlerContext ctx, final long frameLength) {
        if (frameLength > 0L) {
            Channels.fireExceptionCaught(ctx.getChannel(), new TooLongFrameException("Adjusted frame length exceeds " + this.maxFrameLength + ": " + frameLength + " - discarded"));
        }
        else {
            Channels.fireExceptionCaught(ctx.getChannel(), new TooLongFrameException("Adjusted frame length exceeds " + this.maxFrameLength + " - discarding"));
        }
    }
}
