// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.jsontype.impl;

import org.apache.htrace.shaded.fasterxml.jackson.core.JsonParser;
import org.apache.htrace.shaded.fasterxml.jackson.databind.util.ClassUtil;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.std.NullifyingDeserializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.DeserializationFeature;
import java.io.IOException;
import org.apache.htrace.shaded.fasterxml.jackson.databind.DatabindContext;
import org.apache.htrace.shaded.fasterxml.jackson.databind.DeserializationContext;
import org.apache.htrace.shaded.fasterxml.jackson.annotation.JsonTypeInfo;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonDeserializer;
import java.util.HashMap;
import org.apache.htrace.shaded.fasterxml.jackson.databind.BeanProperty;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JavaType;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import java.io.Serializable;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsontype.TypeDeserializer;

public abstract class TypeDeserializerBase extends TypeDeserializer implements Serializable
{
    private static final long serialVersionUID = 278445030337366675L;
    protected final TypeIdResolver _idResolver;
    protected final JavaType _baseType;
    protected final BeanProperty _property;
    protected final JavaType _defaultImpl;
    protected final String _typePropertyName;
    protected final boolean _typeIdVisible;
    protected final HashMap<String, JsonDeserializer<Object>> _deserializers;
    protected JsonDeserializer<Object> _defaultImplDeserializer;
    
    protected TypeDeserializerBase(final JavaType baseType, final TypeIdResolver idRes, final String typePropertyName, final boolean typeIdVisible, final Class<?> defaultImpl) {
        this._baseType = baseType;
        this._idResolver = idRes;
        this._typePropertyName = typePropertyName;
        this._typeIdVisible = typeIdVisible;
        this._deserializers = new HashMap<String, JsonDeserializer<Object>>();
        if (defaultImpl == null) {
            this._defaultImpl = null;
        }
        else {
            this._defaultImpl = baseType.forcedNarrowBy(defaultImpl);
        }
        this._property = null;
    }
    
    protected TypeDeserializerBase(final TypeDeserializerBase src, final BeanProperty property) {
        this._baseType = src._baseType;
        this._idResolver = src._idResolver;
        this._typePropertyName = src._typePropertyName;
        this._typeIdVisible = src._typeIdVisible;
        this._deserializers = src._deserializers;
        this._defaultImpl = src._defaultImpl;
        this._defaultImplDeserializer = src._defaultImplDeserializer;
        this._property = property;
    }
    
    @Override
    public abstract TypeDeserializer forProperty(final BeanProperty p0);
    
    @Override
    public abstract JsonTypeInfo.As getTypeInclusion();
    
    public String baseTypeName() {
        return this._baseType.getRawClass().getName();
    }
    
    @Override
    public final String getPropertyName() {
        return this._typePropertyName;
    }
    
    @Override
    public TypeIdResolver getTypeIdResolver() {
        return this._idResolver;
    }
    
    @Override
    public Class<?> getDefaultImpl() {
        return (this._defaultImpl == null) ? null : this._defaultImpl.getRawClass();
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append('[').append(this.getClass().getName());
        sb.append("; base-type:").append(this._baseType);
        sb.append("; id-resolver: ").append(this._idResolver);
        sb.append(']');
        return sb.toString();
    }
    
    protected final JsonDeserializer<Object> _findDeserializer(final DeserializationContext ctxt, final String typeId) throws IOException {
        JsonDeserializer<Object> deser;
        synchronized (this._deserializers) {
            deser = this._deserializers.get(typeId);
            if (deser == null) {
                JavaType type;
                if (this._idResolver instanceof TypeIdResolverBase) {
                    type = ((TypeIdResolverBase)this._idResolver).typeFromId(ctxt, typeId);
                }
                else {
                    type = this._idResolver.typeFromId(typeId);
                }
                if (type == null) {
                    if (this._defaultImpl == null) {
                        throw ctxt.unknownTypeException(this._baseType, typeId);
                    }
                    deser = this._findDefaultImplDeserializer(ctxt);
                }
                else {
                    if (this._baseType != null && this._baseType.getClass() == type.getClass()) {
                        type = this._baseType.narrowBy(type.getRawClass());
                    }
                    deser = ctxt.findContextualValueDeserializer(type, this._property);
                }
                this._deserializers.put(typeId, deser);
            }
        }
        return deser;
    }
    
    protected final JsonDeserializer<Object> _findDefaultImplDeserializer(final DeserializationContext ctxt) throws IOException {
        if (this._defaultImpl == null) {
            if (!ctxt.isEnabled(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE)) {
                return NullifyingDeserializer.instance;
            }
            return null;
        }
        else {
            final Class<?> raw = this._defaultImpl.getRawClass();
            if (ClassUtil.isBogusClass(raw)) {
                return NullifyingDeserializer.instance;
            }
            synchronized (this._defaultImpl) {
                if (this._defaultImplDeserializer == null) {
                    this._defaultImplDeserializer = ctxt.findContextualValueDeserializer(this._defaultImpl, this._property);
                }
                return this._defaultImplDeserializer;
            }
        }
    }
    
    @Deprecated
    protected Object _deserializeWithNativeTypeId(final JsonParser jp, final DeserializationContext ctxt) throws IOException {
        return this._deserializeWithNativeTypeId(jp, ctxt, jp.getTypeId());
    }
    
    protected Object _deserializeWithNativeTypeId(final JsonParser jp, final DeserializationContext ctxt, final Object typeId) throws IOException {
        JsonDeserializer<Object> deser;
        if (typeId == null) {
            if (this._defaultImpl == null) {
                throw ctxt.mappingException("No (native) type id found when one was expected for polymorphic type handling");
            }
            deser = this._findDefaultImplDeserializer(ctxt);
        }
        else {
            final String typeIdStr = (String)((typeId instanceof String) ? typeId : String.valueOf(typeId));
            deser = this._findDeserializer(ctxt, typeIdStr);
        }
        return deser.deserialize(jp, ctxt);
    }
}
