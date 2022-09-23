// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.impl.model.parameter.multivalued;

import com.sun.jersey.spi.StringReader;

abstract class AbstractStringReaderExtractor implements MultivaluedParameterExtractor
{
    protected final StringReader sr;
    protected final String parameter;
    protected final String defaultStringValue;
    
    public AbstractStringReaderExtractor(final StringReader sr, final String parameter, final String defaultStringValue) {
        this.sr = sr;
        this.parameter = parameter;
        this.defaultStringValue = defaultStringValue;
        if (defaultStringValue != null) {
            final StringReader.ValidateDefaultValue validate = sr.getClass().getAnnotation(StringReader.ValidateDefaultValue.class);
            if (validate == null || validate.value()) {
                sr.fromString(defaultStringValue);
            }
        }
    }
    
    @Override
    public String getName() {
        return this.parameter;
    }
    
    @Override
    public String getDefaultStringValue() {
        return this.defaultStringValue;
    }
}
