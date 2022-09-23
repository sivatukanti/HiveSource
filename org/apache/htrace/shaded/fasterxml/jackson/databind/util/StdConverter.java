// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.util;

import org.apache.htrace.shaded.fasterxml.jackson.databind.JavaType;
import org.apache.htrace.shaded.fasterxml.jackson.databind.type.TypeFactory;

public abstract class StdConverter<IN, OUT> implements Converter<IN, OUT>
{
    @Override
    public abstract OUT convert(final IN p0);
    
    @Override
    public JavaType getInputType(final TypeFactory typeFactory) {
        final JavaType[] types = typeFactory.findTypeParameters(this.getClass(), Converter.class);
        if (types == null || types.length < 2) {
            throw new IllegalStateException("Can not find OUT type parameter for Converter of type " + this.getClass().getName());
        }
        return types[0];
    }
    
    @Override
    public JavaType getOutputType(final TypeFactory typeFactory) {
        final JavaType[] types = typeFactory.findTypeParameters(this.getClass(), Converter.class);
        if (types == null || types.length < 2) {
            throw new IllegalStateException("Can not find OUT type parameter for Converter of type " + this.getClass().getName());
        }
        return types[1];
    }
}
