// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.jsontype.impl;

import org.apache.htrace.shaded.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.DeserializationConfig;
import org.apache.htrace.shaded.fasterxml.jackson.databind.BeanProperty;
import org.apache.htrace.shaded.fasterxml.jackson.databind.cfg.MapperConfig;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsontype.TypeSerializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsontype.NamedType;
import java.util.Collection;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JavaType;
import org.apache.htrace.shaded.fasterxml.jackson.databind.SerializationConfig;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import org.apache.htrace.shaded.fasterxml.jackson.annotation.JsonTypeInfo;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;

public class StdTypeResolverBuilder implements TypeResolverBuilder<StdTypeResolverBuilder>
{
    protected JsonTypeInfo.Id _idType;
    protected JsonTypeInfo.As _includeAs;
    protected String _typeProperty;
    protected boolean _typeIdVisible;
    protected Class<?> _defaultImpl;
    protected TypeIdResolver _customIdResolver;
    
    public StdTypeResolverBuilder() {
        this._typeIdVisible = false;
    }
    
    public static StdTypeResolverBuilder noTypeInfoBuilder() {
        return new StdTypeResolverBuilder().init(JsonTypeInfo.Id.NONE, (TypeIdResolver)null);
    }
    
    @Override
    public StdTypeResolverBuilder init(final JsonTypeInfo.Id idType, final TypeIdResolver idRes) {
        if (idType == null) {
            throw new IllegalArgumentException("idType can not be null");
        }
        this._idType = idType;
        this._customIdResolver = idRes;
        this._typeProperty = idType.getDefaultPropertyName();
        return this;
    }
    
    @Override
    public TypeSerializer buildTypeSerializer(final SerializationConfig config, final JavaType baseType, final Collection<NamedType> subtypes) {
        if (this._idType == JsonTypeInfo.Id.NONE) {
            return null;
        }
        final TypeIdResolver idRes = this.idResolver(config, baseType, subtypes, true, false);
        switch (this._includeAs) {
            case WRAPPER_ARRAY: {
                return new AsArrayTypeSerializer(idRes, null);
            }
            case PROPERTY: {
                return new AsPropertyTypeSerializer(idRes, null, this._typeProperty);
            }
            case WRAPPER_OBJECT: {
                return new AsWrapperTypeSerializer(idRes, null);
            }
            case EXTERNAL_PROPERTY: {
                return new AsExternalTypeSerializer(idRes, null, this._typeProperty);
            }
            default: {
                throw new IllegalStateException("Do not know how to construct standard type serializer for inclusion type: " + this._includeAs);
            }
        }
    }
    
    @Override
    public TypeDeserializer buildTypeDeserializer(final DeserializationConfig config, final JavaType baseType, final Collection<NamedType> subtypes) {
        if (this._idType == JsonTypeInfo.Id.NONE) {
            return null;
        }
        final TypeIdResolver idRes = this.idResolver(config, baseType, subtypes, false, true);
        switch (this._includeAs) {
            case WRAPPER_ARRAY: {
                return new AsArrayTypeDeserializer(baseType, idRes, this._typeProperty, this._typeIdVisible, this._defaultImpl);
            }
            case PROPERTY: {
                return new AsPropertyTypeDeserializer(baseType, idRes, this._typeProperty, this._typeIdVisible, this._defaultImpl);
            }
            case WRAPPER_OBJECT: {
                return new AsWrapperTypeDeserializer(baseType, idRes, this._typeProperty, this._typeIdVisible, this._defaultImpl);
            }
            case EXTERNAL_PROPERTY: {
                return new AsExternalTypeDeserializer(baseType, idRes, this._typeProperty, this._typeIdVisible, this._defaultImpl);
            }
            default: {
                throw new IllegalStateException("Do not know how to construct standard type serializer for inclusion type: " + this._includeAs);
            }
        }
    }
    
    @Override
    public StdTypeResolverBuilder inclusion(final JsonTypeInfo.As includeAs) {
        if (includeAs == null) {
            throw new IllegalArgumentException("includeAs can not be null");
        }
        this._includeAs = includeAs;
        return this;
    }
    
    @Override
    public StdTypeResolverBuilder typeProperty(String typeIdPropName) {
        if (typeIdPropName == null || typeIdPropName.length() == 0) {
            typeIdPropName = this._idType.getDefaultPropertyName();
        }
        this._typeProperty = typeIdPropName;
        return this;
    }
    
    @Override
    public StdTypeResolverBuilder defaultImpl(final Class<?> defaultImpl) {
        this._defaultImpl = defaultImpl;
        return this;
    }
    
    @Override
    public StdTypeResolverBuilder typeIdVisibility(final boolean isVisible) {
        this._typeIdVisible = isVisible;
        return this;
    }
    
    @Override
    public Class<?> getDefaultImpl() {
        return this._defaultImpl;
    }
    
    public String getTypeProperty() {
        return this._typeProperty;
    }
    
    public boolean isTypeIdVisible() {
        return this._typeIdVisible;
    }
    
    protected TypeIdResolver idResolver(final MapperConfig<?> config, final JavaType baseType, final Collection<NamedType> subtypes, final boolean forSer, final boolean forDeser) {
        if (this._customIdResolver != null) {
            return this._customIdResolver;
        }
        if (this._idType == null) {
            throw new IllegalStateException("Can not build, 'init()' not yet called");
        }
        switch (this._idType) {
            case CLASS: {
                return new ClassNameIdResolver(baseType, config.getTypeFactory());
            }
            case MINIMAL_CLASS: {
                return new MinimalClassNameIdResolver(baseType, config.getTypeFactory());
            }
            case NAME: {
                return TypeNameIdResolver.construct(config, baseType, subtypes, forSer, forDeser);
            }
            case NONE: {
                return null;
            }
            default: {
                throw new IllegalStateException("Do not know how to construct standard type id resolver for idType: " + this._idType);
            }
        }
    }
}
