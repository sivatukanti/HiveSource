// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.http.websocketx;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

public class PingWebSocketFrame extends WebSocketFrame
{
    public PingWebSocketFrame() {
        this.setFinalFragment(true);
        this.setBinaryData(ChannelBuffers.EMPTY_BUFFER);
    }
    
    public PingWebSocketFrame(final ChannelBuffer binaryData) {
        this.setBinaryData(binaryData);
    }
    
    public PingWebSocketFrame(final boolean finalFragment, final int rsv, final ChannelBuffer binaryData) {
        this.setFinalFragment(finalFragment);
        this.setRsv(rsv);
        this.setBinaryData(binaryData);
    }
    
    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "(data: " + this.getBinaryData() + ')';
    }
}
