// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.api.client;

import com.sun.jersey.core.header.LinkHeader;
import javax.ws.rs.core.MultivaluedMap;
import com.sun.jersey.core.header.LinkHeaders;

public class WebResourceLinkHeaders extends LinkHeaders
{
    private final Client c;
    
    public WebResourceLinkHeaders(final Client c, final MultivaluedMap<String, String> headers) {
        super(headers);
        this.c = c;
    }
    
    public WebResource resource(final String rel) {
        final LinkHeader lh = this.getLink(rel);
        if (lh == null) {
            return null;
        }
        return this.c.resource(lh.getUri());
    }
    
    public ViewResource viewResource(final String rel) {
        final LinkHeader lh = this.getLink(rel);
        if (lh == null) {
            return null;
        }
        return this.c.viewResource(lh.getUri());
    }
}
