// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.exc;

import org.apache.htrace.shaded.fasterxml.jackson.core.JsonParser;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonLocation;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonMappingException;

public class InvalidFormatException extends JsonMappingException
{
    private static final long serialVersionUID = 1L;
    protected final Object _value;
    protected final Class<?> _targetType;
    
    public InvalidFormatException(final String msg, final Object value, final Class<?> targetType) {
        super(msg);
        this._value = value;
        this._targetType = targetType;
    }
    
    public InvalidFormatException(final String msg, final JsonLocation loc, final Object value, final Class<?> targetType) {
        super(msg, loc);
        this._value = value;
        this._targetType = targetType;
    }
    
    public static InvalidFormatException from(final JsonParser jp, final String msg, final Object value, final Class<?> targetType) {
        return new InvalidFormatException(msg, jp.getTokenLocation(), value, targetType);
    }
    
    public Object getValue() {
        return this._value;
    }
    
    public Class<?> getTargetType() {
        return this._targetType;
    }
}
