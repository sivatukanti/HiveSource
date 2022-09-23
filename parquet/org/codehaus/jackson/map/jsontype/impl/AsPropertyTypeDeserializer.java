// 
// Decompiled by Procyon v0.5.36
// 

package parquet.org.codehaus.jackson.map.jsontype.impl;

import parquet.org.codehaus.jackson.JsonProcessingException;
import java.io.IOException;
import parquet.org.codehaus.jackson.map.JsonDeserializer;
import parquet.org.codehaus.jackson.ObjectCodec;
import parquet.org.codehaus.jackson.util.JsonParserSequence;
import parquet.org.codehaus.jackson.util.TokenBuffer;
import parquet.org.codehaus.jackson.JsonToken;
import parquet.org.codehaus.jackson.map.DeserializationContext;
import parquet.org.codehaus.jackson.JsonParser;
import parquet.org.codehaus.jackson.annotate.JsonTypeInfo;
import parquet.org.codehaus.jackson.map.BeanProperty;
import parquet.org.codehaus.jackson.map.jsontype.TypeIdResolver;
import parquet.org.codehaus.jackson.type.JavaType;

public class AsPropertyTypeDeserializer extends AsArrayTypeDeserializer
{
    protected final String _typePropertyName;
    
    @Deprecated
    public AsPropertyTypeDeserializer(final JavaType bt, final TypeIdResolver idRes, final BeanProperty property, final String typePropName) {
        this(bt, idRes, property, null, typePropName);
    }
    
    public AsPropertyTypeDeserializer(final JavaType bt, final TypeIdResolver idRes, final BeanProperty property, final Class<?> defaultImpl, final String typePropName) {
        super(bt, idRes, property, defaultImpl);
        this._typePropertyName = typePropName;
    }
    
    @Override
    public JsonTypeInfo.As getTypeInclusion() {
        return JsonTypeInfo.As.PROPERTY;
    }
    
    @Override
    public String getPropertyName() {
        return this._typePropertyName;
    }
    
    @Override
    public Object deserializeTypedFromObject(JsonParser jp, final DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonToken t = jp.getCurrentToken();
        if (t == JsonToken.START_OBJECT) {
            t = jp.nextToken();
        }
        else {
            if (t == JsonToken.START_ARRAY) {
                return this._deserializeTypedUsingDefaultImpl(jp, ctxt, null);
            }
            if (t != JsonToken.FIELD_NAME) {
                return this._deserializeTypedUsingDefaultImpl(jp, ctxt, null);
            }
        }
        TokenBuffer tb = null;
        while (t == JsonToken.FIELD_NAME) {
            final String name = jp.getCurrentName();
            jp.nextToken();
            if (this._typePropertyName.equals(name)) {
                final String typeId = jp.getText();
                final JsonDeserializer<Object> deser = this._findDeserializer(ctxt, typeId);
                if (tb != null) {
                    jp = JsonParserSequence.createFlattened(tb.asParser(jp), jp);
                }
                jp.nextToken();
                return deser.deserialize(jp, ctxt);
            }
            if (tb == null) {
                tb = new TokenBuffer(null);
            }
            tb.writeFieldName(name);
            tb.copyCurrentStructure(jp);
            t = jp.nextToken();
        }
        return this._deserializeTypedUsingDefaultImpl(jp, ctxt, tb);
    }
    
    protected Object _deserializeTypedUsingDefaultImpl(JsonParser jp, final DeserializationContext ctxt, final TokenBuffer tb) throws IOException, JsonProcessingException {
        if (this._defaultImpl != null) {
            final JsonDeserializer<Object> deser = this._findDefaultImplDeserializer(ctxt);
            if (tb != null) {
                tb.writeEndObject();
                jp = tb.asParser(jp);
                jp.nextToken();
            }
            return deser.deserialize(jp, ctxt);
        }
        final Object result = this._deserializeIfNatural(jp, ctxt);
        if (result != null) {
            return result;
        }
        if (jp.getCurrentToken() == JsonToken.START_ARRAY) {
            return super.deserializeTypedFromAny(jp, ctxt);
        }
        throw ctxt.wrongTokenException(jp, JsonToken.FIELD_NAME, "missing property '" + this._typePropertyName + "' that is to contain type id  (for class " + this.baseTypeName() + ")");
    }
    
    @Override
    public Object deserializeTypedFromAny(final JsonParser jp, final DeserializationContext ctxt) throws IOException, JsonProcessingException {
        if (jp.getCurrentToken() == JsonToken.START_ARRAY) {
            return super.deserializeTypedFromArray(jp, ctxt);
        }
        return this.deserializeTypedFromObject(jp, ctxt);
    }
    
    protected Object _deserializeIfNatural(final JsonParser jp, final DeserializationContext ctxt) throws IOException, JsonProcessingException {
        switch (jp.getCurrentToken()) {
            case VALUE_STRING: {
                if (this._baseType.getRawClass().isAssignableFrom(String.class)) {
                    return jp.getText();
                }
                break;
            }
            case VALUE_NUMBER_INT: {
                if (this._baseType.getRawClass().isAssignableFrom(Integer.class)) {
                    return jp.getIntValue();
                }
                break;
            }
            case VALUE_NUMBER_FLOAT: {
                if (this._baseType.getRawClass().isAssignableFrom(Double.class)) {
                    return jp.getDoubleValue();
                }
                break;
            }
            case VALUE_TRUE: {
                if (this._baseType.getRawClass().isAssignableFrom(Boolean.class)) {
                    return Boolean.TRUE;
                }
                break;
            }
            case VALUE_FALSE: {
                if (this._baseType.getRawClass().isAssignableFrom(Boolean.class)) {
                    return Boolean.FALSE;
                }
                break;
            }
        }
        return null;
    }
}
