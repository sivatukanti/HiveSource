// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.http.websocketx;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

public class PongWebSocketFrame extends WebSocketFrame
{
    public PongWebSocketFrame() {
        this.setBinaryData(ChannelBuffers.EMPTY_BUFFER);
    }
    
    public PongWebSocketFrame(final ChannelBuffer binaryData) {
        this.setBinaryData(binaryData);
    }
    
    public PongWebSocketFrame(final boolean finalFragment, final int rsv, final ChannelBuffer binaryData) {
        this.setFinalFragment(finalFragment);
        this.setRsv(rsv);
        this.setBinaryData(binaryData);
    }
    
    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "(data: " + this.getBinaryData() + ')';
    }
}
