// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.api.client.filter;

import java.util.Collections;
import java.util.HashSet;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientRequest;
import java.util.Set;

public class CsrfProtectionFilter extends ClientFilter
{
    private static final Set<String> METHODS_TO_IGNORE;
    private static final String HEADER_NAME = "X-Requested-By";
    private final String requestedBy;
    
    public CsrfProtectionFilter() {
        this("");
    }
    
    public CsrfProtectionFilter(final String requestedBy) {
        this.requestedBy = requestedBy;
    }
    
    @Override
    public ClientResponse handle(final ClientRequest cr) throws ClientHandlerException {
        if (!CsrfProtectionFilter.METHODS_TO_IGNORE.contains(cr.getMethod()) && !cr.getHeaders().containsKey("X-Requested-By")) {
            cr.getHeaders().add("X-Requested-By", this.requestedBy);
        }
        return this.getNext().handle(cr);
    }
    
    static {
        final HashSet<String> mti = new HashSet<String>();
        mti.add("GET");
        mti.add("OPTIONS");
        mti.add("HEAD");
        METHODS_TO_IGNORE = Collections.unmodifiableSet((Set<? extends String>)mti);
    }
}
