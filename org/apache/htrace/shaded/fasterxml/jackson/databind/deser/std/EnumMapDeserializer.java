// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.deser.std;

import org.apache.htrace.shaded.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import org.apache.htrace.shaded.fasterxml.jackson.databind.DeserializationFeature;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonToken;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonParser;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonMappingException;
import org.apache.htrace.shaded.fasterxml.jackson.databind.BeanProperty;
import org.apache.htrace.shaded.fasterxml.jackson.databind.DeserializationContext;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonDeserializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JavaType;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.ContextualDeserializer;
import java.util.EnumMap;

public class EnumMapDeserializer extends StdDeserializer<EnumMap<?, ?>> implements ContextualDeserializer
{
    private static final long serialVersionUID = 4564890642370311174L;
    protected final JavaType _mapType;
    protected final Class<?> _enumClass;
    protected JsonDeserializer<Enum<?>> _keyDeserializer;
    protected JsonDeserializer<Object> _valueDeserializer;
    protected final TypeDeserializer _valueTypeDeserializer;
    
    public EnumMapDeserializer(final JavaType mapType, final JsonDeserializer<?> keyDeserializer, final JsonDeserializer<?> valueDeser, final TypeDeserializer valueTypeDeser) {
        super(EnumMap.class);
        this._mapType = mapType;
        this._enumClass = mapType.getKeyType().getRawClass();
        this._keyDeserializer = (JsonDeserializer<Enum<?>>)keyDeserializer;
        this._valueDeserializer = (JsonDeserializer<Object>)valueDeser;
        this._valueTypeDeserializer = valueTypeDeser;
    }
    
    public EnumMapDeserializer withResolved(final JsonDeserializer<?> keyDeserializer, final JsonDeserializer<?> valueDeserializer, final TypeDeserializer valueTypeDeser) {
        if (keyDeserializer == this._keyDeserializer && valueDeserializer == this._valueDeserializer && valueTypeDeser == this._valueTypeDeserializer) {
            return this;
        }
        return new EnumMapDeserializer(this._mapType, keyDeserializer, valueDeserializer, this._valueTypeDeserializer);
    }
    
    @Override
    public JsonDeserializer<?> createContextual(final DeserializationContext ctxt, final BeanProperty property) throws JsonMappingException {
        JsonDeserializer<?> kd = this._keyDeserializer;
        if (kd == null) {
            kd = ctxt.findContextualValueDeserializer(this._mapType.getKeyType(), property);
        }
        JsonDeserializer<?> vd = this._valueDeserializer;
        if (vd == null) {
            vd = ctxt.findContextualValueDeserializer(this._mapType.getContentType(), property);
        }
        else {
            vd = ctxt.handleSecondaryContextualization(vd, property);
        }
        TypeDeserializer vtd = this._valueTypeDeserializer;
        if (vtd != null) {
            vtd = vtd.forProperty(property);
        }
        return this.withResolved(kd, vd, vtd);
    }
    
    @Override
    public boolean isCachable() {
        return true;
    }
    
    @Override
    public EnumMap<?, ?> deserialize(final JsonParser jp, final DeserializationContext ctxt) throws IOException, JsonProcessingException {
        if (jp.getCurrentToken() != JsonToken.START_OBJECT) {
            throw ctxt.mappingException(EnumMap.class);
        }
        final EnumMap result = this.constructMap();
        final JsonDeserializer<Object> valueDes = this._valueDeserializer;
        final TypeDeserializer typeDeser = this._valueTypeDeserializer;
        while (jp.nextToken() != JsonToken.END_OBJECT) {
            final Enum<?> key = this._keyDeserializer.deserialize(jp, ctxt);
            if (key == null) {
                if (!ctxt.isEnabled(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL)) {
                    String value = null;
                    try {
                        if (jp.hasCurrentToken()) {
                            value = jp.getText();
                        }
                    }
                    catch (Exception ex) {}
                    throw ctxt.weirdStringException(value, this._enumClass, "value not one of declared Enum instance names");
                }
                jp.nextToken();
                jp.skipChildren();
            }
            else {
                final JsonToken t = jp.nextToken();
                Object value2;
                if (t == JsonToken.VALUE_NULL) {
                    value2 = valueDes.getNullValue();
                }
                else if (typeDeser == null) {
                    value2 = valueDes.deserialize(jp, ctxt);
                }
                else {
                    value2 = valueDes.deserializeWithType(jp, ctxt, typeDeser);
                }
                result.put(key, value2);
            }
        }
        return (EnumMap<?, ?>)result;
    }
    
    @Override
    public Object deserializeWithType(final JsonParser jp, final DeserializationContext ctxt, final TypeDeserializer typeDeserializer) throws IOException, JsonProcessingException {
        return typeDeserializer.deserializeTypedFromObject(jp, ctxt);
    }
    
    private EnumMap<?, ?> constructMap() {
        return new EnumMap<Object, Object>(this._enumClass);
    }
}
