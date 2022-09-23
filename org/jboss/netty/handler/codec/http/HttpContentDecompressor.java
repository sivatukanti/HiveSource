// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.http;

import org.jboss.netty.handler.codec.compression.ZlibDecoder;
import org.jboss.netty.handler.codec.compression.ZlibWrapper;
import org.jboss.netty.channel.ChannelUpstreamHandler;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.handler.codec.embedder.DecoderEmbedder;

public class HttpContentDecompressor extends HttpContentDecoder
{
    @Override
    protected DecoderEmbedder<ChannelBuffer> newContentDecoder(final String contentEncoding) throws Exception {
        if ("gzip".equalsIgnoreCase(contentEncoding) || "x-gzip".equalsIgnoreCase(contentEncoding)) {
            return new DecoderEmbedder<ChannelBuffer>(new ChannelUpstreamHandler[] { new ZlibDecoder(ZlibWrapper.GZIP) });
        }
        if ("deflate".equalsIgnoreCase(contentEncoding) || "x-deflate".equalsIgnoreCase(contentEncoding)) {
            return new DecoderEmbedder<ChannelBuffer>(new ChannelUpstreamHandler[] { new ZlibDecoder(ZlibWrapper.ZLIB_OR_NONE) });
        }
        return null;
    }
}
