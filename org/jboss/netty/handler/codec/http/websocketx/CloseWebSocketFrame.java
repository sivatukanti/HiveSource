// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.http.websocketx;

import org.jboss.netty.util.CharsetUtil;
import java.io.UnsupportedEncodingException;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

public class CloseWebSocketFrame extends WebSocketFrame
{
    public CloseWebSocketFrame() {
        this.setBinaryData(ChannelBuffers.EMPTY_BUFFER);
    }
    
    public CloseWebSocketFrame(final int statusCode, final String reasonText) {
        this(true, 0, statusCode, reasonText);
    }
    
    public CloseWebSocketFrame(final boolean finalFragment, final int rsv) {
        this(finalFragment, rsv, null);
    }
    
    public CloseWebSocketFrame(final boolean finalFragment, final int rsv, final int statusCode, final String reasonText) {
        this.setFinalFragment(finalFragment);
        this.setRsv(rsv);
        byte[] reasonBytes = new byte[0];
        if (reasonText != null) {
            try {
                reasonBytes = reasonText.getBytes("UTF-8");
            }
            catch (UnsupportedEncodingException e) {
                reasonBytes = reasonText.getBytes();
            }
        }
        final ChannelBuffer binaryData = ChannelBuffers.buffer(2 + reasonBytes.length);
        binaryData.writeShort(statusCode);
        if (reasonBytes.length > 0) {
            binaryData.writeBytes(reasonBytes);
        }
        binaryData.readerIndex(0);
        this.setBinaryData(binaryData);
    }
    
    public CloseWebSocketFrame(final boolean finalFragment, final int rsv, final ChannelBuffer binaryData) {
        this.setFinalFragment(finalFragment);
        this.setRsv(rsv);
        if (binaryData == null) {
            this.setBinaryData(ChannelBuffers.EMPTY_BUFFER);
        }
        else {
            this.setBinaryData(binaryData);
        }
    }
    
    public int getStatusCode() {
        final ChannelBuffer binaryData = this.getBinaryData();
        if (binaryData == null || binaryData.capacity() == 0) {
            return -1;
        }
        binaryData.readerIndex(0);
        final int statusCode = binaryData.readShort();
        binaryData.readerIndex(0);
        return statusCode;
    }
    
    public String getReasonText() {
        final ChannelBuffer binaryData = this.getBinaryData();
        if (binaryData == null || binaryData.capacity() <= 2) {
            return "";
        }
        binaryData.readerIndex(2);
        final String reasonText = binaryData.toString(CharsetUtil.UTF_8);
        binaryData.readerIndex(0);
        return reasonText;
    }
    
    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
