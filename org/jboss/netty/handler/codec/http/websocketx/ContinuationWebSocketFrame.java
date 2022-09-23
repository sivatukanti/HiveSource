// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.http.websocketx;

import org.jboss.netty.util.CharsetUtil;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

public class ContinuationWebSocketFrame extends WebSocketFrame
{
    public ContinuationWebSocketFrame() {
        this.setBinaryData(ChannelBuffers.EMPTY_BUFFER);
    }
    
    public ContinuationWebSocketFrame(final ChannelBuffer binaryData) {
        this.setBinaryData(binaryData);
    }
    
    public ContinuationWebSocketFrame(final boolean finalFragment, final int rsv, final ChannelBuffer binaryData) {
        this.setFinalFragment(finalFragment);
        this.setRsv(rsv);
        this.setBinaryData(binaryData);
    }
    
    public ContinuationWebSocketFrame(final boolean finalFragment, final int rsv, final String text) {
        this.setFinalFragment(finalFragment);
        this.setRsv(rsv);
        this.setText(text);
    }
    
    public String getText() {
        if (this.getBinaryData() == null) {
            return null;
        }
        return this.getBinaryData().toString(CharsetUtil.UTF_8);
    }
    
    public void setText(final String text) {
        if (text == null || text.length() == 0) {
            this.setBinaryData(ChannelBuffers.EMPTY_BUFFER);
        }
        else {
            this.setBinaryData(ChannelBuffers.copiedBuffer(text, CharsetUtil.UTF_8));
        }
    }
    
    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "(data: " + this.getBinaryData() + ')';
    }
}
