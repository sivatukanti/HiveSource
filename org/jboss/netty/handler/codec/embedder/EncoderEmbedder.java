// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.embedder;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.buffer.ChannelBufferFactory;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelDownstreamHandler;

public class EncoderEmbedder<E> extends AbstractCodecEmbedder<E>
{
    public EncoderEmbedder(final ChannelDownstreamHandler... handlers) {
        super((ChannelHandler[])handlers);
    }
    
    public EncoderEmbedder(final ChannelBufferFactory bufferFactory, final ChannelDownstreamHandler... handlers) {
        super(bufferFactory, (ChannelHandler[])handlers);
    }
    
    public boolean offer(final Object input) {
        Channels.write(this.getChannel(), input).setSuccess();
        return !this.isEmpty();
    }
}
