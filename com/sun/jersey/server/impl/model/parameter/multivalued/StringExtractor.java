// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.impl.model.parameter.multivalued;

import javax.ws.rs.core.MultivaluedMap;

final class StringExtractor implements MultivaluedParameterExtractor
{
    final String parameter;
    final String defaultValue;
    
    public StringExtractor(final String parameter) {
        this(parameter, null);
    }
    
    public StringExtractor(final String parameter, final String defaultValue) {
        this.parameter = parameter;
        this.defaultValue = defaultValue;
    }
    
    @Override
    public String getName() {
        return this.parameter;
    }
    
    @Override
    public String getDefaultStringValue() {
        return this.defaultValue;
    }
    
    @Override
    public Object extract(final MultivaluedMap<String, String> parameters) {
        final String value = parameters.getFirst(this.parameter);
        return (value != null) ? value : this.defaultValue;
    }
}
