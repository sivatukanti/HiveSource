// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind;

import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.SettableBeanProperty;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.impl.ObjectIdReader;
import java.util.Collection;
import org.apache.htrace.shaded.fasterxml.jackson.databind.util.NameTransformer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonParser;

public abstract class JsonDeserializer<T>
{
    public abstract T deserialize(final JsonParser p0, final DeserializationContext p1) throws IOException, JsonProcessingException;
    
    public T deserialize(final JsonParser jp, final DeserializationContext ctxt, final T intoValue) throws IOException, JsonProcessingException {
        throw new UnsupportedOperationException("Can not update object of type " + intoValue.getClass().getName() + " (by deserializer of type " + this.getClass().getName() + ")");
    }
    
    public Object deserializeWithType(final JsonParser jp, final DeserializationContext ctxt, final TypeDeserializer typeDeserializer) throws IOException, JsonProcessingException {
        return typeDeserializer.deserializeTypedFromAny(jp, ctxt);
    }
    
    public JsonDeserializer<T> unwrappingDeserializer(final NameTransformer unwrapper) {
        return this;
    }
    
    public JsonDeserializer<?> replaceDelegatee(final JsonDeserializer<?> delegatee) {
        throw new UnsupportedOperationException();
    }
    
    public Class<?> handledType() {
        return null;
    }
    
    public T getNullValue() {
        return null;
    }
    
    public T getEmptyValue() {
        return this.getNullValue();
    }
    
    public Collection<Object> getKnownPropertyNames() {
        return null;
    }
    
    public boolean isCachable() {
        return false;
    }
    
    public ObjectIdReader getObjectIdReader() {
        return null;
    }
    
    public JsonDeserializer<?> getDelegatee() {
        return null;
    }
    
    public SettableBeanProperty findBackReference(final String refName) {
        throw new IllegalArgumentException("Can not handle managed/back reference '" + refName + "': type: value deserializer of type " + this.getClass().getName() + " does not support them");
    }
    
    public abstract static class None extends JsonDeserializer<Object>
    {
        private None() {
        }
    }
}
