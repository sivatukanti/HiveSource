// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.deser.std;

import org.apache.htrace.shaded.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonParser;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonMappingException;
import org.apache.htrace.shaded.fasterxml.jackson.databind.BeanProperty;
import org.apache.htrace.shaded.fasterxml.jackson.databind.DeserializationContext;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonDeserializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JavaType;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.ContextualDeserializer;
import java.util.concurrent.atomic.AtomicReference;

public class AtomicReferenceDeserializer extends StdDeserializer<AtomicReference<?>> implements ContextualDeserializer
{
    private static final long serialVersionUID = 1L;
    protected final JavaType _referencedType;
    protected final TypeDeserializer _valueTypeDeserializer;
    protected final JsonDeserializer<?> _valueDeserializer;
    
    public AtomicReferenceDeserializer(final JavaType referencedType) {
        this(referencedType, null, null);
    }
    
    public AtomicReferenceDeserializer(final JavaType referencedType, final TypeDeserializer typeDeser, final JsonDeserializer<?> deser) {
        super(AtomicReference.class);
        this._referencedType = referencedType;
        this._valueDeserializer = deser;
        this._valueTypeDeserializer = typeDeser;
    }
    
    public AtomicReferenceDeserializer withResolved(final TypeDeserializer typeDeser, final JsonDeserializer<?> valueDeser) {
        return new AtomicReferenceDeserializer(this._referencedType, typeDeser, valueDeser);
    }
    
    @Override
    public AtomicReference<?> getNullValue() {
        return new AtomicReference<Object>();
    }
    
    @Override
    public JsonDeserializer<?> createContextual(final DeserializationContext ctxt, final BeanProperty property) throws JsonMappingException {
        JsonDeserializer<?> deser = this._valueDeserializer;
        TypeDeserializer typeDeser = this._valueTypeDeserializer;
        if (deser == null) {
            deser = ctxt.findContextualValueDeserializer(this._referencedType, property);
        }
        if (typeDeser != null) {
            typeDeser = typeDeser.forProperty(property);
        }
        if (deser == this._valueDeserializer && typeDeser == this._valueTypeDeserializer) {
            return this;
        }
        return this.withResolved(typeDeser, deser);
    }
    
    @Override
    public AtomicReference<?> deserialize(final JsonParser jp, final DeserializationContext ctxt) throws IOException {
        if (this._valueTypeDeserializer != null) {
            return new AtomicReference<Object>(this._valueDeserializer.deserializeWithType(jp, ctxt, this._valueTypeDeserializer));
        }
        return new AtomicReference<Object>(this._valueDeserializer.deserialize(jp, ctxt));
    }
    
    @Override
    public Object[] deserializeWithType(final JsonParser jp, final DeserializationContext ctxt, final TypeDeserializer typeDeserializer) throws IOException {
        return (Object[])typeDeserializer.deserializeTypedFromAny(jp, ctxt);
    }
}
