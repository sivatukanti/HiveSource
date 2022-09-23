// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.frame;

import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.buffer.ChannelBuffer;

public class DelimiterBasedFrameDecoder extends FrameDecoder
{
    private final ChannelBuffer[] delimiters;
    private final int maxFrameLength;
    private final boolean stripDelimiter;
    private final boolean failFast;
    private boolean discardingTooLongFrame;
    private int tooLongFrameLength;
    private final LineBasedFrameDecoder lineBasedDecoder;
    
    public DelimiterBasedFrameDecoder(final int maxFrameLength, final ChannelBuffer delimiter) {
        this(maxFrameLength, true, delimiter);
    }
    
    public DelimiterBasedFrameDecoder(final int maxFrameLength, final boolean stripDelimiter, final ChannelBuffer delimiter) {
        this(maxFrameLength, stripDelimiter, false, delimiter);
    }
    
    public DelimiterBasedFrameDecoder(final int maxFrameLength, final boolean stripDelimiter, final boolean failFast, final ChannelBuffer delimiter) {
        this(maxFrameLength, stripDelimiter, failFast, new ChannelBuffer[] { delimiter.slice(delimiter.readerIndex(), delimiter.readableBytes()) });
    }
    
    public DelimiterBasedFrameDecoder(final int maxFrameLength, final ChannelBuffer... delimiters) {
        this(maxFrameLength, true, delimiters);
    }
    
    public DelimiterBasedFrameDecoder(final int maxFrameLength, final boolean stripDelimiter, final ChannelBuffer... delimiters) {
        this(maxFrameLength, stripDelimiter, false, delimiters);
    }
    
    public DelimiterBasedFrameDecoder(final int maxFrameLength, final boolean stripDelimiter, final boolean failFast, final ChannelBuffer... delimiters) {
        validateMaxFrameLength(maxFrameLength);
        if (delimiters == null) {
            throw new NullPointerException("delimiters");
        }
        if (delimiters.length == 0) {
            throw new IllegalArgumentException("empty delimiters");
        }
        if (isLineBased(delimiters) && !this.isSubclass()) {
            this.lineBasedDecoder = new LineBasedFrameDecoder(maxFrameLength, stripDelimiter, failFast);
            this.delimiters = null;
        }
        else {
            this.delimiters = new ChannelBuffer[delimiters.length];
            for (int i = 0; i < delimiters.length; ++i) {
                final ChannelBuffer d = delimiters[i];
                validateDelimiter(d);
                this.delimiters[i] = d.slice(d.readerIndex(), d.readableBytes());
            }
            this.lineBasedDecoder = null;
        }
        this.maxFrameLength = maxFrameLength;
        this.stripDelimiter = stripDelimiter;
        this.failFast = failFast;
    }
    
    private static boolean isLineBased(final ChannelBuffer[] delimiters) {
        if (delimiters.length != 2) {
            return false;
        }
        ChannelBuffer a = delimiters[0];
        ChannelBuffer b = delimiters[1];
        if (a.capacity() < b.capacity()) {
            a = delimiters[1];
            b = delimiters[0];
        }
        return a.capacity() == 2 && b.capacity() == 1 && a.getByte(0) == 13 && a.getByte(1) == 10 && b.getByte(0) == 10;
    }
    
    private boolean isSubclass() {
        return this.getClass() != DelimiterBasedFrameDecoder.class;
    }
    
    @Override
    protected Object decode(final ChannelHandlerContext ctx, final Channel channel, final ChannelBuffer buffer) throws Exception {
        if (this.lineBasedDecoder != null) {
            return this.lineBasedDecoder.decode(ctx, channel, buffer);
        }
        int minFrameLength = Integer.MAX_VALUE;
        ChannelBuffer minDelim = null;
        for (final ChannelBuffer delim : this.delimiters) {
            final int frameLength = indexOf(buffer, delim);
            if (frameLength >= 0 && frameLength < minFrameLength) {
                minFrameLength = frameLength;
                minDelim = delim;
            }
        }
        if (minDelim == null) {
            if (!this.discardingTooLongFrame) {
                if (buffer.readableBytes() > this.maxFrameLength) {
                    this.tooLongFrameLength = buffer.readableBytes();
                    buffer.skipBytes(buffer.readableBytes());
                    this.discardingTooLongFrame = true;
                    if (this.failFast) {
                        this.fail(ctx, this.tooLongFrameLength);
                    }
                }
            }
            else {
                this.tooLongFrameLength += buffer.readableBytes();
                buffer.skipBytes(buffer.readableBytes());
            }
            return null;
        }
        final int minDelimLength = minDelim.capacity();
        if (this.discardingTooLongFrame) {
            this.discardingTooLongFrame = false;
            buffer.skipBytes(minFrameLength + minDelimLength);
            final int tooLongFrameLength = this.tooLongFrameLength;
            this.tooLongFrameLength = 0;
            if (!this.failFast) {
                this.fail(ctx, tooLongFrameLength);
            }
            return null;
        }
        if (minFrameLength > this.maxFrameLength) {
            buffer.skipBytes(minFrameLength + minDelimLength);
            this.fail(ctx, minFrameLength);
            return null;
        }
        ChannelBuffer frame;
        if (this.stripDelimiter) {
            frame = this.extractFrame(buffer, buffer.readerIndex(), minFrameLength);
        }
        else {
            frame = this.extractFrame(buffer, buffer.readerIndex(), minFrameLength + minDelimLength);
        }
        buffer.skipBytes(minFrameLength + minDelimLength);
        return frame;
    }
    
    private void fail(final ChannelHandlerContext ctx, final long frameLength) {
        if (frameLength > 0L) {
            Channels.fireExceptionCaught(ctx.getChannel(), new TooLongFrameException("frame length exceeds " + this.maxFrameLength + ": " + frameLength + " - discarded"));
        }
        else {
            Channels.fireExceptionCaught(ctx.getChannel(), new TooLongFrameException("frame length exceeds " + this.maxFrameLength + " - discarding"));
        }
    }
    
    private static int indexOf(final ChannelBuffer haystack, final ChannelBuffer needle) {
        for (int i = haystack.readerIndex(); i < haystack.writerIndex(); ++i) {
            int haystackIndex;
            int needleIndex;
            for (haystackIndex = i, needleIndex = 0; needleIndex < needle.capacity() && haystack.getByte(haystackIndex) == needle.getByte(needleIndex); ++needleIndex) {
                if (++haystackIndex == haystack.writerIndex() && needleIndex != needle.capacity() - 1) {
                    return -1;
                }
            }
            if (needleIndex == needle.capacity()) {
                return i - haystack.readerIndex();
            }
        }
        return -1;
    }
    
    private static void validateDelimiter(final ChannelBuffer delimiter) {
        if (delimiter == null) {
            throw new NullPointerException("delimiter");
        }
        if (!delimiter.readable()) {
            throw new IllegalArgumentException("empty delimiter");
        }
    }
    
    private static void validateMaxFrameLength(final int maxFrameLength) {
        if (maxFrameLength <= 0) {
            throw new IllegalArgumentException("maxFrameLength must be a positive integer: " + maxFrameLength);
        }
    }
}
