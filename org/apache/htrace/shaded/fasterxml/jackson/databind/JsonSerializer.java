// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind;

import org.apache.htrace.shaded.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsontype.TypeSerializer;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonGenerator;
import org.apache.htrace.shaded.fasterxml.jackson.databind.util.NameTransformer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitable;

public abstract class JsonSerializer<T> implements JsonFormatVisitable
{
    public JsonSerializer<T> unwrappingSerializer(final NameTransformer unwrapper) {
        return this;
    }
    
    public JsonSerializer<T> replaceDelegatee(final JsonSerializer<?> delegatee) {
        throw new UnsupportedOperationException();
    }
    
    public abstract void serialize(final T p0, final JsonGenerator p1, final SerializerProvider p2) throws IOException, JsonProcessingException;
    
    public void serializeWithType(final T value, final JsonGenerator jgen, final SerializerProvider provider, final TypeSerializer typeSer) throws IOException, JsonProcessingException {
        Class<?> clz = this.handledType();
        if (clz == null) {
            clz = value.getClass();
        }
        throw new UnsupportedOperationException("Type id handling not implemented for type " + clz.getName());
    }
    
    public Class<T> handledType() {
        return null;
    }
    
    public boolean isEmpty(final T value) {
        return value == null;
    }
    
    public boolean usesObjectId() {
        return false;
    }
    
    public boolean isUnwrappingSerializer() {
        return false;
    }
    
    public JsonSerializer<?> getDelegatee() {
        return null;
    }
    
    @Override
    public void acceptJsonFormatVisitor(final JsonFormatVisitorWrapper visitor, final JavaType type) throws JsonMappingException {
        if (visitor != null) {
            visitor.expectAnyFormat(type);
        }
    }
    
    public abstract static class None extends JsonSerializer<Object>
    {
    }
}
