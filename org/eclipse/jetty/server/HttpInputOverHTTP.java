// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.server;

import java.io.IOException;

public class HttpInputOverHTTP extends HttpInput
{
    public HttpInputOverHTTP(final HttpChannelState state) {
        super(state);
    }
    
    @Override
    protected void produceContent() throws IOException {
        ((HttpConnection)this.getHttpChannelState().getHttpChannel().getEndPoint().getConnection()).fillAndParseForContent();
    }
}
