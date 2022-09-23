// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.impl.container.filter;

import java.net.URI;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import com.sun.jersey.server.impl.uri.UriHelper;
import com.sun.jersey.spi.container.ContainerRequest;
import javax.ws.rs.core.Context;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.spi.container.ContainerRequestFilter;

public class NormalizeFilter implements ContainerRequestFilter
{
    @Context
    ResourceConfig resourceConfig;
    
    @Override
    public ContainerRequest filter(final ContainerRequest request) {
        if (this.resourceConfig.getFeature("com.sun.jersey.config.feature.NormalizeURI")) {
            final URI uri = request.getRequestUri();
            final URI normalizedUri = UriHelper.normalize(uri, !this.resourceConfig.getFeature("com.sun.jersey.config.feature.CanonicalizeURIPath"));
            if (uri != normalizedUri) {
                if (this.resourceConfig.getFeature("com.sun.jersey.config.feature.Redirect")) {
                    throw new WebApplicationException(Response.temporaryRedirect(normalizedUri).build());
                }
                final URI baseUri = UriHelper.normalize(request.getBaseUri(), !this.resourceConfig.getFeature("com.sun.jersey.config.feature.CanonicalizeURIPath"));
                request.setUris(baseUri, normalizedUri);
            }
        }
        return request;
    }
}
