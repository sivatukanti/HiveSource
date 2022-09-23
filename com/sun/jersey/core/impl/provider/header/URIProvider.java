// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.core.impl.provider.header;

import java.net.URISyntaxException;
import java.net.URI;
import com.sun.jersey.spi.HeaderDelegateProvider;

public class URIProvider implements HeaderDelegateProvider<URI>
{
    @Override
    public boolean supports(final Class<?> type) {
        return type == URI.class;
    }
    
    @Override
    public String toString(final URI header) {
        return header.toASCIIString();
    }
    
    @Override
    public URI fromString(final String header) {
        try {
            return new URI(header);
        }
        catch (URISyntaxException e) {
            throw new IllegalArgumentException("Error parsing uri '" + header + "'", e);
        }
    }
}
