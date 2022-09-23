// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.impl.model.parameter.multivalued;

import javax.ws.rs.core.MultivaluedMap;

public interface MultivaluedParameterExtractor
{
    String getName();
    
    String getDefaultStringValue();
    
    Object extract(final MultivaluedMap<String, String> p0);
}
