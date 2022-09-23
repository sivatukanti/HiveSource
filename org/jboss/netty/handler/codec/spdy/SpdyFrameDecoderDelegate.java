// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.spdy;

import org.jboss.netty.buffer.ChannelBuffer;

public interface SpdyFrameDecoderDelegate
{
    void readDataFrame(final int p0, final boolean p1, final ChannelBuffer p2);
    
    void readSynStreamFrame(final int p0, final int p1, final byte p2, final boolean p3, final boolean p4);
    
    void readSynReplyFrame(final int p0, final boolean p1);
    
    void readRstStreamFrame(final int p0, final int p1);
    
    void readSettingsFrame(final boolean p0);
    
    void readSetting(final int p0, final int p1, final boolean p2, final boolean p3);
    
    void readSettingsEnd();
    
    void readPingFrame(final int p0);
    
    void readGoAwayFrame(final int p0, final int p1);
    
    void readHeadersFrame(final int p0, final boolean p1);
    
    void readWindowUpdateFrame(final int p0, final int p1);
    
    void readHeaderBlock(final ChannelBuffer p0);
    
    void readHeaderBlockEnd();
    
    void readFrameError(final String p0);
}