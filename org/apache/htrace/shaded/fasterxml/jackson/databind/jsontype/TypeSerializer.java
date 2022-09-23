// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.jsontype;

import org.apache.htrace.shaded.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonGenerator;
import org.apache.htrace.shaded.fasterxml.jackson.annotation.JsonTypeInfo;
import org.apache.htrace.shaded.fasterxml.jackson.databind.BeanProperty;

public abstract class TypeSerializer
{
    public abstract TypeSerializer forProperty(final BeanProperty p0);
    
    public abstract JsonTypeInfo.As getTypeInclusion();
    
    public abstract String getPropertyName();
    
    public abstract TypeIdResolver getTypeIdResolver();
    
    public abstract void writeTypePrefixForScalar(final Object p0, final JsonGenerator p1) throws IOException;
    
    public abstract void writeTypePrefixForObject(final Object p0, final JsonGenerator p1) throws IOException;
    
    public abstract void writeTypePrefixForArray(final Object p0, final JsonGenerator p1) throws IOException;
    
    public abstract void writeTypeSuffixForScalar(final Object p0, final JsonGenerator p1) throws IOException;
    
    public abstract void writeTypeSuffixForObject(final Object p0, final JsonGenerator p1) throws IOException;
    
    public abstract void writeTypeSuffixForArray(final Object p0, final JsonGenerator p1) throws IOException;
    
    public void writeTypePrefixForScalar(final Object value, final JsonGenerator jgen, final Class<?> type) throws IOException {
        this.writeTypePrefixForScalar(value, jgen);
    }
    
    public void writeTypePrefixForObject(final Object value, final JsonGenerator jgen, final Class<?> type) throws IOException {
        this.writeTypePrefixForObject(value, jgen);
    }
    
    public void writeTypePrefixForArray(final Object value, final JsonGenerator jgen, final Class<?> type) throws IOException {
        this.writeTypePrefixForArray(value, jgen);
    }
    
    public abstract void writeCustomTypePrefixForScalar(final Object p0, final JsonGenerator p1, final String p2) throws IOException, JsonProcessingException;
    
    public abstract void writeCustomTypePrefixForObject(final Object p0, final JsonGenerator p1, final String p2) throws IOException;
    
    public abstract void writeCustomTypePrefixForArray(final Object p0, final JsonGenerator p1, final String p2) throws IOException;
    
    public abstract void writeCustomTypeSuffixForScalar(final Object p0, final JsonGenerator p1, final String p2) throws IOException;
    
    public abstract void writeCustomTypeSuffixForObject(final Object p0, final JsonGenerator p1, final String p2) throws IOException;
    
    public abstract void writeCustomTypeSuffixForArray(final Object p0, final JsonGenerator p1, final String p2) throws IOException;
}
