// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.http;

public class HttpRequestDecoder extends HttpMessageDecoder
{
    public HttpRequestDecoder() {
    }
    
    public HttpRequestDecoder(final int maxInitialLineLength, final int maxHeaderSize, final int maxChunkSize) {
        super(maxInitialLineLength, maxHeaderSize, maxChunkSize);
    }
    
    @Override
    protected HttpMessage createMessage(final String[] initialLine) throws Exception {
        return new DefaultHttpRequest(HttpVersion.valueOf(initialLine[2]), HttpMethod.valueOf(initialLine[0]), initialLine[1]);
    }
    
    @Override
    protected boolean isDecodingRequest() {
        return true;
    }
}
