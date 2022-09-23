// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.spdy;

import org.jboss.netty.buffer.ChannelBuffer;

abstract class SpdyHeaderBlockDecoder
{
    static SpdyHeaderBlockDecoder newInstance(final SpdyVersion spdyVersion, final int maxHeaderSize) {
        return new SpdyHeaderBlockZlibDecoder(spdyVersion, maxHeaderSize);
    }
    
    abstract void decode(final ChannelBuffer p0, final SpdyHeadersFrame p1) throws Exception;
    
    abstract void endHeaderBlock(final SpdyHeadersFrame p0) throws Exception;
    
    abstract void end();
}
