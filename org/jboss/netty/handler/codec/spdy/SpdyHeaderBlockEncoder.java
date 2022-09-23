// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.spdy;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.util.internal.DetectionUtil;

abstract class SpdyHeaderBlockEncoder
{
    static SpdyHeaderBlockEncoder newInstance(final SpdyVersion spdyVersion, final int compressionLevel, final int windowBits, final int memLevel) {
        if (DetectionUtil.javaVersion() >= 7) {
            return new SpdyHeaderBlockZlibEncoder(spdyVersion, compressionLevel);
        }
        return new SpdyHeaderBlockJZlibEncoder(spdyVersion, compressionLevel, windowBits, memLevel);
    }
    
    abstract ChannelBuffer encode(final SpdyHeadersFrame p0) throws Exception;
    
    abstract void end();
}
