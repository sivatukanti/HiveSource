// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.http.websocketx;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

public class BinaryWebSocketFrame extends WebSocketFrame
{
    public BinaryWebSocketFrame() {
        this.setBinaryData(ChannelBuffers.EMPTY_BUFFER);
    }
    
    public BinaryWebSocketFrame(final ChannelBuffer binaryData) {
        this.setBinaryData(binaryData);
    }
    
    public BinaryWebSocketFrame(final boolean finalFragment, final int rsv, final ChannelBuffer binaryData) {
        this.setFinalFragment(finalFragment);
        this.setRsv(rsv);
        this.setBinaryData(binaryData);
    }
    
    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "(data: " + this.getBinaryData() + ')';
    }
}
