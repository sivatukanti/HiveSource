// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.jsontype;

import org.apache.htrace.shaded.fasterxml.jackson.annotation.JsonTypeInfo;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JavaType;

public interface TypeIdResolver
{
    void init(final JavaType p0);
    
    String idFromValue(final Object p0);
    
    String idFromValueAndType(final Object p0, final Class<?> p1);
    
    String idFromBaseType();
    
    JavaType typeFromId(final String p0);
    
    JsonTypeInfo.Id getMechanism();
}
