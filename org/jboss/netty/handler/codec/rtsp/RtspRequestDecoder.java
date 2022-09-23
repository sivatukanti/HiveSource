// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.rtsp;

import org.jboss.netty.handler.codec.http.DefaultHttpRequest;
import org.jboss.netty.handler.codec.http.HttpMessage;

public class RtspRequestDecoder extends RtspMessageDecoder
{
    public RtspRequestDecoder() {
    }
    
    public RtspRequestDecoder(final int maxInitialLineLength, final int maxHeaderSize, final int maxContentLength) {
        super(maxInitialLineLength, maxHeaderSize, maxContentLength);
    }
    
    @Override
    protected HttpMessage createMessage(final String[] initialLine) throws Exception {
        return new DefaultHttpRequest(RtspVersions.valueOf(initialLine[2]), RtspMethods.valueOf(initialLine[0]), initialLine[1]);
    }
    
    @Override
    protected boolean isDecodingRequest() {
        return true;
    }
}
