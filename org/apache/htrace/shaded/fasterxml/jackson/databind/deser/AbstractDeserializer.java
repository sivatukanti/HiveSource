// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.deser;

import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.impl.ReadableObjectId;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonToken;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.DeserializationContext;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonParser;
import org.apache.htrace.shaded.fasterxml.jackson.databind.BeanDescription;
import java.util.Map;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.impl.ObjectIdReader;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JavaType;
import java.io.Serializable;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonDeserializer;

public class AbstractDeserializer extends JsonDeserializer<Object> implements Serializable
{
    private static final long serialVersionUID = -3010349050434697698L;
    protected final JavaType _baseType;
    protected final ObjectIdReader _objectIdReader;
    protected final Map<String, SettableBeanProperty> _backRefProperties;
    protected final boolean _acceptString;
    protected final boolean _acceptBoolean;
    protected final boolean _acceptInt;
    protected final boolean _acceptDouble;
    
    public AbstractDeserializer(final BeanDeserializerBuilder builder, final BeanDescription beanDesc, final Map<String, SettableBeanProperty> backRefProps) {
        this._baseType = beanDesc.getType();
        this._objectIdReader = builder.getObjectIdReader();
        this._backRefProperties = backRefProps;
        final Class<?> cls = this._baseType.getRawClass();
        this._acceptString = cls.isAssignableFrom(String.class);
        this._acceptBoolean = (cls == Boolean.TYPE || cls.isAssignableFrom(Boolean.class));
        this._acceptInt = (cls == Integer.TYPE || cls.isAssignableFrom(Integer.class));
        this._acceptDouble = (cls == Double.TYPE || cls.isAssignableFrom(Double.class));
    }
    
    protected AbstractDeserializer(final BeanDescription beanDesc) {
        this._baseType = beanDesc.getType();
        this._objectIdReader = null;
        this._backRefProperties = null;
        final Class<?> cls = this._baseType.getRawClass();
        this._acceptString = cls.isAssignableFrom(String.class);
        this._acceptBoolean = (cls == Boolean.TYPE || cls.isAssignableFrom(Boolean.class));
        this._acceptInt = (cls == Integer.TYPE || cls.isAssignableFrom(Integer.class));
        this._acceptDouble = (cls == Double.TYPE || cls.isAssignableFrom(Double.class));
    }
    
    public static AbstractDeserializer constructForNonPOJO(final BeanDescription beanDesc) {
        return new AbstractDeserializer(beanDesc);
    }
    
    @Override
    public Class<?> handledType() {
        return this._baseType.getRawClass();
    }
    
    @Override
    public boolean isCachable() {
        return true;
    }
    
    @Override
    public ObjectIdReader getObjectIdReader() {
        return this._objectIdReader;
    }
    
    @Override
    public SettableBeanProperty findBackReference(final String logicalName) {
        return (this._backRefProperties == null) ? null : this._backRefProperties.get(logicalName);
    }
    
    @Override
    public Object deserializeWithType(final JsonParser jp, final DeserializationContext ctxt, final TypeDeserializer typeDeserializer) throws IOException, JsonProcessingException {
        if (this._objectIdReader != null) {
            final JsonToken t = jp.getCurrentToken();
            if (t != null && t.isScalarValue()) {
                return this._deserializeFromObjectId(jp, ctxt);
            }
        }
        final Object result = this._deserializeIfNatural(jp, ctxt);
        if (result != null) {
            return result;
        }
        return typeDeserializer.deserializeTypedFromObject(jp, ctxt);
    }
    
    @Override
    public Object deserialize(final JsonParser jp, final DeserializationContext ctxt) throws IOException, JsonProcessingException {
        throw ctxt.instantiationException(this._baseType.getRawClass(), "abstract types either need to be mapped to concrete types, have custom deserializer, or be instantiated with additional type information");
    }
    
    protected Object _deserializeIfNatural(final JsonParser jp, final DeserializationContext ctxt) throws IOException, JsonProcessingException {
        final JsonToken t = jp.getCurrentToken();
        if (t.isScalarValue()) {
            if (t == JsonToken.VALUE_STRING) {
                if (this._acceptString) {
                    return jp.getText();
                }
            }
            else if (t == JsonToken.VALUE_NUMBER_INT) {
                if (this._acceptInt) {
                    return jp.getIntValue();
                }
            }
            else if (t == JsonToken.VALUE_NUMBER_FLOAT) {
                if (this._acceptDouble) {
                    return jp.getDoubleValue();
                }
            }
            else if (t == JsonToken.VALUE_TRUE) {
                if (this._acceptBoolean) {
                    return Boolean.TRUE;
                }
            }
            else if (t == JsonToken.VALUE_FALSE && this._acceptBoolean) {
                return Boolean.FALSE;
            }
        }
        return null;
    }
    
    protected Object _deserializeFromObjectId(final JsonParser jp, final DeserializationContext ctxt) throws IOException, JsonProcessingException {
        final Object id = this._objectIdReader.readObjectReference(jp, ctxt);
        final ReadableObjectId roid = ctxt.findObjectId(id, this._objectIdReader.generator, this._objectIdReader.resolver);
        final Object pojo = roid.resolve();
        if (pojo == null) {
            throw new IllegalStateException("Could not resolve Object Id [" + id + "] -- unresolved forward-reference?");
        }
        return pojo;
    }
}
