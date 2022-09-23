// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.deser.impl;

import org.apache.htrace.shaded.fasterxml.jackson.core.JsonProcessingException;
import org.apache.htrace.shaded.fasterxml.jackson.databind.DeserializationFeature;
import org.apache.htrace.shaded.fasterxml.jackson.databind.DeserializationContext;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JavaType;
import java.io.Serializable;

public final class NullProvider implements Serializable
{
    private static final long serialVersionUID = 1L;
    private final Object _nullValue;
    private final boolean _isPrimitive;
    private final Class<?> _rawType;
    
    public NullProvider(final JavaType type, final Object nullValue) {
        this._nullValue = nullValue;
        this._isPrimitive = type.isPrimitive();
        this._rawType = type.getRawClass();
    }
    
    public Object nullValue(final DeserializationContext ctxt) throws JsonProcessingException {
        if (this._isPrimitive && ctxt.isEnabled(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)) {
            throw ctxt.mappingException("Can not map JSON null into type " + this._rawType.getName() + " (set DeserializationConfig.DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES to 'false' to allow)");
        }
        return this._nullValue;
    }
}
