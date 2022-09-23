// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.deser.std;

import java.lang.reflect.Array;
import org.apache.htrace.shaded.fasterxml.jackson.databind.DeserializationFeature;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import org.apache.htrace.shaded.fasterxml.jackson.databind.util.ObjectBuffer;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonToken;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonParser;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonMappingException;
import org.apache.htrace.shaded.fasterxml.jackson.databind.BeanProperty;
import org.apache.htrace.shaded.fasterxml.jackson.databind.DeserializationContext;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JavaType;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonDeserializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.type.ArrayType;
import org.apache.htrace.shaded.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.ContextualDeserializer;

@JacksonStdImpl
public class ObjectArrayDeserializer extends ContainerDeserializerBase<Object[]> implements ContextualDeserializer
{
    private static final long serialVersionUID = 1L;
    protected final ArrayType _arrayType;
    protected final boolean _untyped;
    protected final Class<?> _elementClass;
    protected JsonDeserializer<Object> _elementDeserializer;
    protected final TypeDeserializer _elementTypeDeserializer;
    
    public ObjectArrayDeserializer(final ArrayType arrayType, final JsonDeserializer<Object> elemDeser, final TypeDeserializer elemTypeDeser) {
        super(arrayType);
        this._arrayType = arrayType;
        this._elementClass = arrayType.getContentType().getRawClass();
        this._untyped = (this._elementClass == Object.class);
        this._elementDeserializer = elemDeser;
        this._elementTypeDeserializer = elemTypeDeser;
    }
    
    public ObjectArrayDeserializer withDeserializer(final TypeDeserializer elemTypeDeser, final JsonDeserializer<?> elemDeser) {
        if (elemDeser == this._elementDeserializer && elemTypeDeser == this._elementTypeDeserializer) {
            return this;
        }
        return new ObjectArrayDeserializer(this._arrayType, (JsonDeserializer<Object>)elemDeser, elemTypeDeser);
    }
    
    @Override
    public JsonDeserializer<?> createContextual(final DeserializationContext ctxt, final BeanProperty property) throws JsonMappingException {
        JsonDeserializer<?> deser = this._elementDeserializer;
        deser = this.findConvertingContentDeserializer(ctxt, property, deser);
        if (deser == null) {
            deser = ctxt.findContextualValueDeserializer(this._arrayType.getContentType(), property);
        }
        else {
            deser = ctxt.handleSecondaryContextualization(deser, property);
        }
        TypeDeserializer elemTypeDeser = this._elementTypeDeserializer;
        if (elemTypeDeser != null) {
            elemTypeDeser = elemTypeDeser.forProperty(property);
        }
        return this.withDeserializer(elemTypeDeser, deser);
    }
    
    @Override
    public JavaType getContentType() {
        return this._arrayType.getContentType();
    }
    
    @Override
    public JsonDeserializer<Object> getContentDeserializer() {
        return this._elementDeserializer;
    }
    
    @Override
    public Object[] deserialize(final JsonParser jp, final DeserializationContext ctxt) throws IOException, JsonProcessingException {
        if (!jp.isExpectedStartArrayToken()) {
            return this.handleNonArray(jp, ctxt);
        }
        final ObjectBuffer buffer = ctxt.leaseObjectBuffer();
        Object[] chunk = buffer.resetAndStart();
        int ix = 0;
        final TypeDeserializer typeDeser = this._elementTypeDeserializer;
        JsonToken t;
        while ((t = jp.nextToken()) != JsonToken.END_ARRAY) {
            Object value;
            if (t == JsonToken.VALUE_NULL) {
                value = this._elementDeserializer.getNullValue();
            }
            else if (typeDeser == null) {
                value = this._elementDeserializer.deserialize(jp, ctxt);
            }
            else {
                value = this._elementDeserializer.deserializeWithType(jp, ctxt, typeDeser);
            }
            if (ix >= chunk.length) {
                chunk = buffer.appendCompletedChunk(chunk);
                ix = 0;
            }
            chunk[ix++] = value;
        }
        Object[] result;
        if (this._untyped) {
            result = buffer.completeAndClearBuffer(chunk, ix);
        }
        else {
            result = buffer.completeAndClearBuffer(chunk, ix, this._elementClass);
        }
        ctxt.returnObjectBuffer(buffer);
        return result;
    }
    
    @Override
    public Object[] deserializeWithType(final JsonParser jp, final DeserializationContext ctxt, final TypeDeserializer typeDeserializer) throws IOException, JsonProcessingException {
        return (Object[])typeDeserializer.deserializeTypedFromArray(jp, ctxt);
    }
    
    protected Byte[] deserializeFromBase64(final JsonParser jp, final DeserializationContext ctxt) throws IOException, JsonProcessingException {
        final byte[] b = jp.getBinaryValue(ctxt.getBase64Variant());
        final Byte[] result = new Byte[b.length];
        for (int i = 0, len = b.length; i < len; ++i) {
            result[i] = b[i];
        }
        return result;
    }
    
    private final Object[] handleNonArray(final JsonParser jp, final DeserializationContext ctxt) throws IOException, JsonProcessingException {
        if (jp.getCurrentToken() == JsonToken.VALUE_STRING && ctxt.isEnabled(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)) {
            final String str = jp.getText();
            if (str.length() == 0) {
                return null;
            }
        }
        if (ctxt.isEnabled(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)) {
            final JsonToken t = jp.getCurrentToken();
            Object value;
            if (t == JsonToken.VALUE_NULL) {
                value = this._elementDeserializer.getNullValue();
            }
            else if (this._elementTypeDeserializer == null) {
                value = this._elementDeserializer.deserialize(jp, ctxt);
            }
            else {
                value = this._elementDeserializer.deserializeWithType(jp, ctxt, this._elementTypeDeserializer);
            }
            Object[] result;
            if (this._untyped) {
                result = new Object[] { null };
            }
            else {
                result = (Object[])Array.newInstance(this._elementClass, 1);
            }
            result[0] = value;
            return result;
        }
        if (jp.getCurrentToken() == JsonToken.VALUE_STRING && this._elementClass == Byte.class) {
            return this.deserializeFromBase64(jp, ctxt);
        }
        throw ctxt.mappingException(this._arrayType.getRawClass());
    }
}
