// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.http;

public class HttpResponseDecoder extends HttpMessageDecoder
{
    public HttpResponseDecoder() {
    }
    
    public HttpResponseDecoder(final int maxInitialLineLength, final int maxHeaderSize, final int maxChunkSize) {
        super(maxInitialLineLength, maxHeaderSize, maxChunkSize);
    }
    
    @Override
    protected HttpMessage createMessage(final String[] initialLine) {
        return new DefaultHttpResponse(HttpVersion.valueOf(initialLine[0]), new HttpResponseStatus(Integer.valueOf(initialLine[1]), initialLine[2]));
    }
    
    @Override
    protected boolean isDecodingRequest() {
        return false;
    }
}
