// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.spdy;

import java.util.Iterator;
import java.util.Set;
import org.jboss.netty.buffer.ChannelBuffers;
import java.nio.ByteOrder;
import org.jboss.netty.buffer.ChannelBuffer;

public class SpdyFrameEncoder
{
    private final int version;
    
    public SpdyFrameEncoder(final SpdyVersion spdyVersion) {
        if (spdyVersion == null) {
            throw new NullPointerException("spdyVersion");
        }
        this.version = spdyVersion.getVersion();
    }
    
    private void writeControlFrameHeader(final ChannelBuffer buffer, final int type, final byte flags, final int length) {
        buffer.writeShort(this.version | 0x8000);
        buffer.writeShort(type);
        buffer.writeByte(flags);
        buffer.writeMedium(length);
    }
    
    public ChannelBuffer encodeDataFrame(final int streamId, final boolean last, final ChannelBuffer data) {
        final byte flags = (byte)(last ? 1 : 0);
        final ChannelBuffer header = ChannelBuffers.buffer(ByteOrder.BIG_ENDIAN, 8);
        header.writeInt(streamId & Integer.MAX_VALUE);
        header.writeByte(flags);
        header.writeMedium(data.readableBytes());
        return ChannelBuffers.wrappedBuffer(header, data);
    }
    
    public ChannelBuffer encodeSynStreamFrame(final int streamId, final int associatedToStreamId, final byte priority, final boolean last, final boolean unidirectional, final ChannelBuffer headerBlock) {
        byte flags = (byte)(last ? 1 : 0);
        if (unidirectional) {
            flags |= 0x2;
        }
        final int length = 10 + headerBlock.readableBytes();
        final ChannelBuffer frame = ChannelBuffers.buffer(ByteOrder.BIG_ENDIAN, 18);
        this.writeControlFrameHeader(frame, 1, flags, length);
        frame.writeInt(streamId);
        frame.writeInt(associatedToStreamId);
        frame.writeShort((priority & 0xFF) << 13);
        return ChannelBuffers.wrappedBuffer(frame, headerBlock);
    }
    
    public ChannelBuffer encodeSynReplyFrame(final int streamId, final boolean last, final ChannelBuffer headerBlock) {
        final byte flags = (byte)(last ? 1 : 0);
        final int length = 4 + headerBlock.readableBytes();
        final ChannelBuffer frame = ChannelBuffers.buffer(ByteOrder.BIG_ENDIAN, 12);
        this.writeControlFrameHeader(frame, 2, flags, length);
        frame.writeInt(streamId);
        return ChannelBuffers.wrappedBuffer(frame, headerBlock);
    }
    
    public ChannelBuffer encodeRstStreamFrame(final int streamId, final int statusCode) {
        final byte flags = 0;
        final int length = 8;
        final ChannelBuffer frame = ChannelBuffers.buffer(ByteOrder.BIG_ENDIAN, 8 + length);
        this.writeControlFrameHeader(frame, 3, flags, length);
        frame.writeInt(streamId);
        frame.writeInt(statusCode);
        return frame;
    }
    
    public ChannelBuffer encodeSettingsFrame(final SpdySettingsFrame spdySettingsFrame) {
        final Set<Integer> ids = spdySettingsFrame.getIds();
        final int numSettings = ids.size();
        byte flags = (byte)(spdySettingsFrame.clearPreviouslyPersistedSettings() ? 1 : 0);
        final int length = 4 + 8 * numSettings;
        final ChannelBuffer frame = ChannelBuffers.buffer(ByteOrder.BIG_ENDIAN, 8 + length);
        this.writeControlFrameHeader(frame, 4, flags, length);
        frame.writeInt(numSettings);
        for (final Integer id : ids) {
            flags = 0;
            if (spdySettingsFrame.isPersistValue(id)) {
                flags |= 0x1;
            }
            if (spdySettingsFrame.isPersisted(id)) {
                flags |= 0x2;
            }
            frame.writeByte(flags);
            frame.writeMedium(id);
            frame.writeInt(spdySettingsFrame.getValue(id));
        }
        return frame;
    }
    
    public ChannelBuffer encodePingFrame(final int id) {
        final byte flags = 0;
        final int length = 4;
        final ChannelBuffer frame = ChannelBuffers.buffer(ByteOrder.BIG_ENDIAN, 8 + length);
        this.writeControlFrameHeader(frame, 6, flags, length);
        frame.writeInt(id);
        return frame;
    }
    
    public ChannelBuffer encodeGoAwayFrame(final int lastGoodStreamId, final int statusCode) {
        final byte flags = 0;
        final int length = 8;
        final ChannelBuffer frame = ChannelBuffers.buffer(ByteOrder.BIG_ENDIAN, 8 + length);
        this.writeControlFrameHeader(frame, 7, flags, length);
        frame.writeInt(lastGoodStreamId);
        frame.writeInt(statusCode);
        return frame;
    }
    
    public ChannelBuffer encodeHeadersFrame(final int streamId, final boolean last, final ChannelBuffer headerBlock) {
        final byte flags = (byte)(last ? 1 : 0);
        final int length = 4 + headerBlock.readableBytes();
        final ChannelBuffer frame = ChannelBuffers.buffer(ByteOrder.BIG_ENDIAN, 12);
        this.writeControlFrameHeader(frame, 8, flags, length);
        frame.writeInt(streamId);
        return ChannelBuffers.wrappedBuffer(frame, headerBlock);
    }
    
    public ChannelBuffer encodeWindowUpdateFrame(final int streamId, final int deltaWindowSize) {
        final byte flags = 0;
        final int length = 8;
        final ChannelBuffer frame = ChannelBuffers.buffer(ByteOrder.BIG_ENDIAN, 8 + length);
        this.writeControlFrameHeader(frame, 9, flags, length);
        frame.writeInt(streamId);
        frame.writeInt(deltaWindowSize);
        return frame;
    }
}
