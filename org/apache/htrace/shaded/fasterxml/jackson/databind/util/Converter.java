// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.util;

import org.apache.htrace.shaded.fasterxml.jackson.databind.JavaType;
import org.apache.htrace.shaded.fasterxml.jackson.databind.type.TypeFactory;

public interface Converter<IN, OUT>
{
    OUT convert(final IN p0);
    
    JavaType getInputType(final TypeFactory p0);
    
    JavaType getOutputType(final TypeFactory p0);
    
    public abstract static class None implements Converter<Object, Object>
    {
    }
}
