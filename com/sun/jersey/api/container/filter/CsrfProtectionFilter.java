// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.api.container.filter;

import java.util.Collections;
import java.util.HashSet;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import com.sun.jersey.spi.container.ContainerRequest;
import java.util.Set;
import com.sun.jersey.spi.container.ContainerRequestFilter;

public class CsrfProtectionFilter implements ContainerRequestFilter
{
    private static final Set<String> METHODS_TO_IGNORE;
    private static final String HEADER_NAME = "X-Requested-By";
    
    @Override
    public ContainerRequest filter(final ContainerRequest request) {
        if (!CsrfProtectionFilter.METHODS_TO_IGNORE.contains(request.getMethod()) && !request.getRequestHeaders().containsKey("X-Requested-By")) {
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
        return request;
    }
    
    static {
        final HashSet<String> mti = new HashSet<String>();
        mti.add("GET");
        mti.add("OPTIONS");
        mti.add("HEAD");
        METHODS_TO_IGNORE = Collections.unmodifiableSet((Set<? extends String>)mti);
    }
}
