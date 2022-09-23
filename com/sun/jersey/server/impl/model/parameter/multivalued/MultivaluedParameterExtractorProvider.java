// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.impl.model.parameter.multivalued;

import com.sun.jersey.api.model.Parameter;

public interface MultivaluedParameterExtractorProvider
{
    MultivaluedParameterExtractor get(final Parameter p0);
    
    MultivaluedParameterExtractor getWithoutDefaultValue(final Parameter p0);
}
