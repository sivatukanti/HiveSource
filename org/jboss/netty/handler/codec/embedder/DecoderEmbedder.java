// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.embedder;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.buffer.ChannelBufferFactory;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelUpstreamHandler;

public class DecoderEmbedder<E> extends AbstractCodecEmbedder<E>
{
    public DecoderEmbedder(final ChannelUpstreamHandler... handlers) {
        super((ChannelHandler[])handlers);
    }
    
    public DecoderEmbedder(final ChannelBufferFactory bufferFactory, final ChannelUpstreamHandler... handlers) {
        super(bufferFactory, (ChannelHandler[])handlers);
    }
    
    public boolean offer(final Object input) {
        Channels.fireMessageReceived(this.getChannel(), input);
        return !this.isEmpty();
    }
}
