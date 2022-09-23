// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.protobuf;

import com.google.protobuf.ExtensionRegistryLite;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.MessageLite;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.handler.codec.oneone.OneToOneDecoder;

@ChannelHandler.Sharable
public class ProtobufDecoder extends OneToOneDecoder
{
    private static final boolean HAS_PARSER;
    private final MessageLite prototype;
    private final ExtensionRegistry extensionRegistry;
    
    public ProtobufDecoder(final MessageLite prototype) {
        this(prototype, null);
    }
    
    public ProtobufDecoder(final MessageLite prototype, final ExtensionRegistry extensionRegistry) {
        if (prototype == null) {
            throw new NullPointerException("prototype");
        }
        this.prototype = prototype.getDefaultInstanceForType();
        this.extensionRegistry = extensionRegistry;
    }
    
    @Override
    protected Object decode(final ChannelHandlerContext ctx, final Channel channel, final Object msg) throws Exception {
        if (!(msg instanceof ChannelBuffer)) {
            return msg;
        }
        final ChannelBuffer buf = (ChannelBuffer)msg;
        final int length = buf.readableBytes();
        byte[] array;
        int offset;
        if (buf.hasArray()) {
            array = buf.array();
            offset = buf.arrayOffset() + buf.readerIndex();
        }
        else {
            array = new byte[length];
            buf.getBytes(buf.readerIndex(), array, 0, length);
            offset = 0;
        }
        if (this.extensionRegistry == null) {
            if (ProtobufDecoder.HAS_PARSER) {
                return this.prototype.getParserForType().parseFrom(array, offset, length);
            }
            return this.prototype.newBuilderForType().mergeFrom(array, offset, length).build();
        }
        else {
            if (ProtobufDecoder.HAS_PARSER) {
                return this.prototype.getParserForType().parseFrom(array, offset, length, this.extensionRegistry);
            }
            return this.prototype.newBuilderForType().mergeFrom(array, offset, length, this.extensionRegistry).build();
        }
    }
    
    static {
        boolean hasParser = false;
        try {
            MessageLite.class.getDeclaredMethod("getParserForType", (Class<?>[])new Class[0]);
            hasParser = true;
        }
        catch (Throwable t) {}
        HAS_PARSER = hasParser;
    }
}
