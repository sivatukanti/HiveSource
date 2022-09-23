// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.deser.std;

import org.apache.htrace.shaded.fasterxml.jackson.core.JsonProcessingException;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonMappingException;
import org.apache.htrace.shaded.fasterxml.jackson.databind.BeanProperty;
import org.apache.htrace.shaded.fasterxml.jackson.databind.DeserializationFeature;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import java.io.IOException;
import org.apache.htrace.shaded.fasterxml.jackson.databind.util.ObjectBuffer;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonToken;
import org.apache.htrace.shaded.fasterxml.jackson.databind.DeserializationContext;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonParser;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonDeserializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.ContextualDeserializer;

@JacksonStdImpl
public final class StringArrayDeserializer extends StdDeserializer<String[]> implements ContextualDeserializer
{
    private static final long serialVersionUID = -7589512013334920693L;
    public static final StringArrayDeserializer instance;
    protected JsonDeserializer<String> _elementDeserializer;
    
    public StringArrayDeserializer() {
        super(String[].class);
        this._elementDeserializer = null;
    }
    
    protected StringArrayDeserializer(final JsonDeserializer<?> deser) {
        super(String[].class);
        this._elementDeserializer = (JsonDeserializer<String>)deser;
    }
    
    @Override
    public String[] deserialize(final JsonParser jp, final DeserializationContext ctxt) throws IOException {
        if (!jp.isExpectedStartArrayToken()) {
            return this.handleNonArray(jp, ctxt);
        }
        if (this._elementDeserializer != null) {
            return this._deserializeCustom(jp, ctxt);
        }
        final ObjectBuffer buffer = ctxt.leaseObjectBuffer();
        Object[] chunk = buffer.resetAndStart();
        int ix = 0;
        JsonToken t;
        while ((t = jp.nextToken()) != JsonToken.END_ARRAY) {
            String value;
            if (t == JsonToken.VALUE_STRING) {
                value = jp.getText();
            }
            else if (t == JsonToken.VALUE_NULL) {
                value = this._elementDeserializer.getNullValue();
            }
            else {
                value = this._parseString(jp, ctxt);
            }
            if (ix >= chunk.length) {
                chunk = buffer.appendCompletedChunk(chunk);
                ix = 0;
            }
            chunk[ix++] = value;
        }
        final String[] result = buffer.completeAndClearBuffer(chunk, ix, String.class);
        ctxt.returnObjectBuffer(buffer);
        return result;
    }
    
    protected final String[] _deserializeCustom(final JsonParser jp, final DeserializationContext ctxt) throws IOException {
        final ObjectBuffer buffer = ctxt.leaseObjectBuffer();
        Object[] chunk = buffer.resetAndStart();
        final JsonDeserializer<String> deser = this._elementDeserializer;
        int ix = 0;
        JsonToken t;
        while ((t = jp.nextToken()) != JsonToken.END_ARRAY) {
            final String value = (t == JsonToken.VALUE_NULL) ? null : deser.deserialize(jp, ctxt);
            if (ix >= chunk.length) {
                chunk = buffer.appendCompletedChunk(chunk);
                ix = 0;
            }
            chunk[ix++] = value;
        }
        final String[] result = buffer.completeAndClearBuffer(chunk, ix, String.class);
        ctxt.returnObjectBuffer(buffer);
        return result;
    }
    
    @Override
    public Object deserializeWithType(final JsonParser jp, final DeserializationContext ctxt, final TypeDeserializer typeDeserializer) throws IOException {
        return typeDeserializer.deserializeTypedFromArray(jp, ctxt);
    }
    
    private final String[] handleNonArray(final JsonParser jp, final DeserializationContext ctxt) throws IOException {
        if (!ctxt.isEnabled(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)) {
            if (jp.getCurrentToken() == JsonToken.VALUE_STRING && ctxt.isEnabled(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)) {
                final String str = jp.getText();
                if (str.length() == 0) {
                    return null;
                }
            }
            throw ctxt.mappingException(this._valueClass);
        }
        return new String[] { (jp.getCurrentToken() == JsonToken.VALUE_NULL) ? null : this._parseString(jp, ctxt) };
    }
    
    @Override
    public JsonDeserializer<?> createContextual(final DeserializationContext ctxt, final BeanProperty property) throws JsonMappingException {
        JsonDeserializer<?> deser = this._elementDeserializer;
        deser = this.findConvertingContentDeserializer(ctxt, property, deser);
        if (deser == null) {
            deser = ctxt.findContextualValueDeserializer(ctxt.constructType(String.class), property);
        }
        else {
            deser = ctxt.handleSecondaryContextualization(deser, property);
        }
        if (deser != null && this.isDefaultDeserializer(deser)) {
            deser = null;
        }
        if (this._elementDeserializer != deser) {
            return new StringArrayDeserializer(deser);
        }
        return this;
    }
    
    static {
        instance = new StringArrayDeserializer();
    }
}
