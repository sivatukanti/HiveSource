// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.http.websocketx;

import org.jboss.netty.buffer.ChannelBuffer;

public abstract class WebSocketFrame
{
    private boolean finalFragment;
    private int rsv;
    private ChannelBuffer binaryData;
    
    public WebSocketFrame() {
        this.finalFragment = true;
    }
    
    public ChannelBuffer getBinaryData() {
        return this.binaryData;
    }
    
    public void setBinaryData(final ChannelBuffer binaryData) {
        this.binaryData = binaryData;
    }
    
    public boolean isFinalFragment() {
        return this.finalFragment;
    }
    
    public void setFinalFragment(final boolean finalFragment) {
        this.finalFragment = finalFragment;
    }
    
    public int getRsv() {
        return this.rsv;
    }
    
    public void setRsv(final int rsv) {
        this.rsv = rsv;
    }
}
