// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.jsontype.impl;

import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonDeserializer;
import org.apache.htrace.shaded.fasterxml.jackson.core.util.JsonParserSequence;
import org.apache.htrace.shaded.fasterxml.jackson.core.ObjectCodec;
import org.apache.htrace.shaded.fasterxml.jackson.databind.util.TokenBuffer;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonToken;
import java.io.IOException;
import org.apache.htrace.shaded.fasterxml.jackson.databind.DeserializationContext;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonParser;
import org.apache.htrace.shaded.fasterxml.jackson.annotation.JsonTypeInfo;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.BeanProperty;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JavaType;
import java.io.Serializable;

public class AsArrayTypeDeserializer extends TypeDeserializerBase implements Serializable
{
    private static final long serialVersionUID = 5345570420394408290L;
    
    public AsArrayTypeDeserializer(final JavaType bt, final TypeIdResolver idRes, final String typePropertyName, final boolean typeIdVisible, final Class<?> defaultImpl) {
        super(bt, idRes, typePropertyName, typeIdVisible, defaultImpl);
    }
    
    public AsArrayTypeDeserializer(final AsArrayTypeDeserializer src, final BeanProperty property) {
        super(src, property);
    }
    
    @Override
    public TypeDeserializer forProperty(final BeanProperty prop) {
        return (prop == this._property) ? this : new AsArrayTypeDeserializer(this, prop);
    }
    
    @Override
    public JsonTypeInfo.As getTypeInclusion() {
        return JsonTypeInfo.As.WRAPPER_ARRAY;
    }
    
    @Override
    public Object deserializeTypedFromArray(final JsonParser jp, final DeserializationContext ctxt) throws IOException {
        return this._deserialize(jp, ctxt);
    }
    
    @Override
    public Object deserializeTypedFromObject(final JsonParser jp, final DeserializationContext ctxt) throws IOException {
        return this._deserialize(jp, ctxt);
    }
    
    @Override
    public Object deserializeTypedFromScalar(final JsonParser jp, final DeserializationContext ctxt) throws IOException {
        return this._deserialize(jp, ctxt);
    }
    
    @Override
    public Object deserializeTypedFromAny(final JsonParser jp, final DeserializationContext ctxt) throws IOException {
        return this._deserialize(jp, ctxt);
    }
    
    private final Object _deserialize(JsonParser jp, final DeserializationContext ctxt) throws IOException {
        if (jp.canReadTypeId()) {
            final Object typeId = jp.getTypeId();
            if (typeId != null) {
                return this._deserializeWithNativeTypeId(jp, ctxt, typeId);
            }
        }
        final boolean hadStartArray = jp.isExpectedStartArrayToken();
        final String typeId2 = this._locateTypeId(jp, ctxt);
        final JsonDeserializer<Object> deser = this._findDeserializer(ctxt, typeId2);
        if (this._typeIdVisible && jp.getCurrentToken() == JsonToken.START_OBJECT) {
            final TokenBuffer tb = new TokenBuffer(null, false);
            tb.writeStartObject();
            tb.writeFieldName(this._typePropertyName);
            tb.writeString(typeId2);
            jp = JsonParserSequence.createFlattened(tb.asParser(jp), jp);
            jp.nextToken();
        }
        final Object value = deser.deserialize(jp, ctxt);
        if (hadStartArray && jp.nextToken() != JsonToken.END_ARRAY) {
            throw ctxt.wrongTokenException(jp, JsonToken.END_ARRAY, "expected closing END_ARRAY after type information and deserialized value");
        }
        return value;
    }
    
    protected final String _locateTypeId(final JsonParser jp, final DeserializationContext ctxt) throws IOException {
        if (!jp.isExpectedStartArrayToken()) {
            if (this._defaultImpl != null) {
                return this._idResolver.idFromBaseType();
            }
            throw ctxt.wrongTokenException(jp, JsonToken.START_ARRAY, "need JSON Array to contain As.WRAPPER_ARRAY type information for class " + this.baseTypeName());
        }
        else {
            final JsonToken t = jp.nextToken();
            if (t == JsonToken.VALUE_STRING) {
                final String result = jp.getText();
                jp.nextToken();
                return result;
            }
            if (this._defaultImpl != null) {
                return this._idResolver.idFromBaseType();
            }
            throw ctxt.wrongTokenException(jp, JsonToken.VALUE_STRING, "need JSON String that contains type id (for subtype of " + this.baseTypeName() + ")");
        }
    }
}
