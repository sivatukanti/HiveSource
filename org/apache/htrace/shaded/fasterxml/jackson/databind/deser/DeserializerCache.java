// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.deser;

import org.apache.htrace.shaded.fasterxml.jackson.databind.util.ClassUtil;
import org.apache.htrace.shaded.fasterxml.jackson.databind.AnnotationIntrospector;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonNode;
import org.apache.htrace.shaded.fasterxml.jackson.databind.type.CollectionType;
import org.apache.htrace.shaded.fasterxml.jackson.databind.type.CollectionLikeType;
import org.apache.htrace.shaded.fasterxml.jackson.annotation.JsonFormat;
import org.apache.htrace.shaded.fasterxml.jackson.databind.type.MapType;
import org.apache.htrace.shaded.fasterxml.jackson.databind.type.MapLikeType;
import org.apache.htrace.shaded.fasterxml.jackson.databind.type.ArrayType;
import org.apache.htrace.shaded.fasterxml.jackson.databind.util.Converter;
import org.apache.htrace.shaded.fasterxml.jackson.databind.BeanDescription;
import org.apache.htrace.shaded.fasterxml.jackson.databind.DeserializationConfig;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.std.StdDelegatingDeserializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.Annotated;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonLocation;
import org.apache.htrace.shaded.fasterxml.jackson.databind.KeyDeserializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonMappingException;
import org.apache.htrace.shaded.fasterxml.jackson.databind.DeserializationContext;
import java.util.HashMap;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonDeserializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JavaType;
import java.util.concurrent.ConcurrentHashMap;
import java.io.Serializable;

public final class DeserializerCache implements Serializable
{
    private static final long serialVersionUID = 1L;
    protected final ConcurrentHashMap<JavaType, JsonDeserializer<Object>> _cachedDeserializers;
    protected final HashMap<JavaType, JsonDeserializer<Object>> _incompleteDeserializers;
    
    public DeserializerCache() {
        this._cachedDeserializers = new ConcurrentHashMap<JavaType, JsonDeserializer<Object>>(64, 0.75f, 2);
        this._incompleteDeserializers = new HashMap<JavaType, JsonDeserializer<Object>>(8);
    }
    
    Object writeReplace() {
        this._incompleteDeserializers.clear();
        return this;
    }
    
    public int cachedDeserializersCount() {
        return this._cachedDeserializers.size();
    }
    
    public void flushCachedDeserializers() {
        this._cachedDeserializers.clear();
    }
    
    public JsonDeserializer<Object> findValueDeserializer(final DeserializationContext ctxt, final DeserializerFactory factory, final JavaType propertyType) throws JsonMappingException {
        JsonDeserializer<Object> deser = this._findCachedDeserializer(propertyType);
        if (deser == null) {
            deser = this._createAndCacheValueDeserializer(ctxt, factory, propertyType);
            if (deser == null) {
                deser = this._handleUnknownValueDeserializer(propertyType);
            }
        }
        return deser;
    }
    
    public KeyDeserializer findKeyDeserializer(final DeserializationContext ctxt, final DeserializerFactory factory, final JavaType type) throws JsonMappingException {
        final KeyDeserializer kd = factory.createKeyDeserializer(ctxt, type);
        if (kd == null) {
            return this._handleUnknownKeyDeserializer(type);
        }
        if (kd instanceof ResolvableDeserializer) {
            ((ResolvableDeserializer)kd).resolve(ctxt);
        }
        return kd;
    }
    
    public boolean hasValueDeserializerFor(final DeserializationContext ctxt, final DeserializerFactory factory, final JavaType type) throws JsonMappingException {
        JsonDeserializer<Object> deser = this._findCachedDeserializer(type);
        if (deser == null) {
            deser = this._createAndCacheValueDeserializer(ctxt, factory, type);
        }
        return deser != null;
    }
    
    protected JsonDeserializer<Object> _findCachedDeserializer(final JavaType type) {
        if (type == null) {
            throw new IllegalArgumentException("Null JavaType passed");
        }
        return this._cachedDeserializers.get(type);
    }
    
    protected JsonDeserializer<Object> _createAndCacheValueDeserializer(final DeserializationContext ctxt, final DeserializerFactory factory, final JavaType type) throws JsonMappingException {
        synchronized (this._incompleteDeserializers) {
            JsonDeserializer<Object> deser = this._findCachedDeserializer(type);
            if (deser != null) {
                return deser;
            }
            final int count = this._incompleteDeserializers.size();
            if (count > 0) {
                deser = this._incompleteDeserializers.get(type);
                if (deser != null) {
                    return deser;
                }
            }
            try {
                return this._createAndCache2(ctxt, factory, type);
            }
            finally {
                if (count == 0 && this._incompleteDeserializers.size() > 0) {
                    this._incompleteDeserializers.clear();
                }
            }
        }
    }
    
    protected JsonDeserializer<Object> _createAndCache2(final DeserializationContext ctxt, final DeserializerFactory factory, final JavaType type) throws JsonMappingException {
        JsonDeserializer<Object> deser;
        try {
            deser = this._createDeserializer(ctxt, factory, type);
        }
        catch (IllegalArgumentException iae) {
            throw new JsonMappingException(iae.getMessage(), null, iae);
        }
        if (deser == null) {
            return null;
        }
        final boolean isResolvable = deser instanceof ResolvableDeserializer;
        final boolean addToCache = deser.isCachable();
        if (isResolvable) {
            this._incompleteDeserializers.put(type, deser);
            ((ResolvableDeserializer)deser).resolve(ctxt);
            this._incompleteDeserializers.remove(type);
        }
        if (addToCache) {
            this._cachedDeserializers.put(type, deser);
        }
        return deser;
    }
    
    protected JsonDeserializer<Object> _createDeserializer(final DeserializationContext ctxt, final DeserializerFactory factory, JavaType type) throws JsonMappingException {
        final DeserializationConfig config = ctxt.getConfig();
        if (type.isAbstract() || type.isMapLikeType() || type.isCollectionLikeType()) {
            type = factory.mapAbstractType(config, type);
        }
        BeanDescription beanDesc = config.introspect(type);
        final JsonDeserializer<Object> deser = this.findDeserializerFromAnnotation(ctxt, beanDesc.getClassInfo());
        if (deser != null) {
            return deser;
        }
        final JavaType newType = this.modifyTypeByAnnotation(ctxt, beanDesc.getClassInfo(), type);
        if (newType != type) {
            type = newType;
            beanDesc = config.introspect(newType);
        }
        final Class<?> builder = beanDesc.findPOJOBuilder();
        if (builder != null) {
            return factory.createBuilderBasedDeserializer(ctxt, type, beanDesc, builder);
        }
        final Converter<Object, Object> conv = beanDesc.findDeserializationConverter();
        if (conv == null) {
            return (JsonDeserializer<Object>)this._createDeserializer2(ctxt, factory, type, beanDesc);
        }
        final JavaType delegateType = conv.getInputType(ctxt.getTypeFactory());
        if (!delegateType.hasRawClass(type.getRawClass())) {
            beanDesc = config.introspect(delegateType);
        }
        return new StdDelegatingDeserializer<Object>(conv, delegateType, this._createDeserializer2(ctxt, factory, delegateType, beanDesc));
    }
    
    protected JsonDeserializer<?> _createDeserializer2(final DeserializationContext ctxt, final DeserializerFactory factory, final JavaType type, final BeanDescription beanDesc) throws JsonMappingException {
        final DeserializationConfig config = ctxt.getConfig();
        if (type.isEnumType()) {
            return factory.createEnumDeserializer(ctxt, type, beanDesc);
        }
        if (type.isContainerType()) {
            if (type.isArrayType()) {
                return factory.createArrayDeserializer(ctxt, (ArrayType)type, beanDesc);
            }
            if (type.isMapLikeType()) {
                final MapLikeType mlt = (MapLikeType)type;
                if (mlt.isTrueMapType()) {
                    return factory.createMapDeserializer(ctxt, (MapType)mlt, beanDesc);
                }
                return factory.createMapLikeDeserializer(ctxt, mlt, beanDesc);
            }
            else if (type.isCollectionLikeType()) {
                final JsonFormat.Value format = beanDesc.findExpectedFormat(null);
                if (format == null || format.getShape() != JsonFormat.Shape.OBJECT) {
                    final CollectionLikeType clt = (CollectionLikeType)type;
                    if (clt.isTrueCollectionType()) {
                        return factory.createCollectionDeserializer(ctxt, (CollectionType)clt, beanDesc);
                    }
                    return factory.createCollectionLikeDeserializer(ctxt, clt, beanDesc);
                }
            }
        }
        if (JsonNode.class.isAssignableFrom(type.getRawClass())) {
            return factory.createTreeDeserializer(config, type, beanDesc);
        }
        return factory.createBeanDeserializer(ctxt, type, beanDesc);
    }
    
    protected JsonDeserializer<Object> findDeserializerFromAnnotation(final DeserializationContext ctxt, final Annotated ann) throws JsonMappingException {
        final Object deserDef = ctxt.getAnnotationIntrospector().findDeserializer(ann);
        if (deserDef == null) {
            return null;
        }
        final JsonDeserializer<Object> deser = ctxt.deserializerInstance(ann, deserDef);
        return this.findConvertingDeserializer(ctxt, ann, deser);
    }
    
    protected JsonDeserializer<Object> findConvertingDeserializer(final DeserializationContext ctxt, final Annotated a, final JsonDeserializer<Object> deser) throws JsonMappingException {
        final Converter<Object, Object> conv = this.findConverter(ctxt, a);
        if (conv == null) {
            return deser;
        }
        final JavaType delegateType = conv.getInputType(ctxt.getTypeFactory());
        return new StdDelegatingDeserializer<Object>(conv, delegateType, deser);
    }
    
    protected Converter<Object, Object> findConverter(final DeserializationContext ctxt, final Annotated a) throws JsonMappingException {
        final Object convDef = ctxt.getAnnotationIntrospector().findDeserializationConverter(a);
        if (convDef == null) {
            return null;
        }
        return ctxt.converterInstance(a, convDef);
    }
    
    private JavaType modifyTypeByAnnotation(final DeserializationContext ctxt, final Annotated a, JavaType type) throws JsonMappingException {
        final AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
        final Class<?> subclass = intr.findDeserializationType(a, type);
        if (subclass != null) {
            try {
                type = type.narrowBy(subclass);
            }
            catch (IllegalArgumentException iae) {
                throw new JsonMappingException("Failed to narrow type " + type + " with concrete-type annotation (value " + subclass.getName() + "), method '" + a.getName() + "': " + iae.getMessage(), null, iae);
            }
        }
        if (type.isContainerType()) {
            final Class<?> keyClass = intr.findDeserializationKeyType(a, type.getKeyType());
            if (keyClass != null) {
                if (!(type instanceof MapLikeType)) {
                    throw new JsonMappingException("Illegal key-type annotation: type " + type + " is not a Map(-like) type");
                }
                try {
                    type = ((MapLikeType)type).narrowKey(keyClass);
                }
                catch (IllegalArgumentException iae2) {
                    throw new JsonMappingException("Failed to narrow key type " + type + " with key-type annotation (" + keyClass.getName() + "): " + iae2.getMessage(), null, iae2);
                }
            }
            JavaType keyType = type.getKeyType();
            if (keyType != null && keyType.getValueHandler() == null) {
                final Object kdDef = intr.findKeyDeserializer(a);
                if (kdDef != null) {
                    final KeyDeserializer kd = ctxt.keyDeserializerInstance(a, kdDef);
                    if (kd != null) {
                        type = ((MapLikeType)type).withKeyValueHandler(kd);
                        keyType = type.getKeyType();
                    }
                }
            }
            final Class<?> cc = intr.findDeserializationContentType(a, type.getContentType());
            if (cc != null) {
                try {
                    type = type.narrowContentsBy(cc);
                }
                catch (IllegalArgumentException iae3) {
                    throw new JsonMappingException("Failed to narrow content type " + type + " with content-type annotation (" + cc.getName() + "): " + iae3.getMessage(), null, iae3);
                }
            }
            final JavaType contentType = type.getContentType();
            if (contentType.getValueHandler() == null) {
                Object cdDef = intr.findContentDeserializer(a);
                if (cdDef != null) {
                    JsonDeserializer<?> cd = null;
                    if (cdDef instanceof JsonDeserializer) {
                        cdDef = cdDef;
                    }
                    else {
                        final Class<?> cdClass = this._verifyAsClass(cdDef, "findContentDeserializer", JsonDeserializer.None.class);
                        if (cdClass != null) {
                            cd = ctxt.deserializerInstance(a, cdClass);
                        }
                    }
                    if (cd != null) {
                        type = type.withContentValueHandler(cd);
                    }
                }
            }
        }
        return type;
    }
    
    private Class<?> _verifyAsClass(final Object src, final String methodName, final Class<?> noneClass) {
        if (src == null) {
            return null;
        }
        if (!(src instanceof Class)) {
            throw new IllegalStateException("AnnotationIntrospector." + methodName + "() returned value of type " + src.getClass().getName() + ": expected type JsonSerializer or Class<JsonSerializer> instead");
        }
        final Class<?> cls = (Class<?>)src;
        if (cls == noneClass || ClassUtil.isBogusClass(cls)) {
            return null;
        }
        return cls;
    }
    
    protected JsonDeserializer<Object> _handleUnknownValueDeserializer(final JavaType type) throws JsonMappingException {
        final Class<?> rawClass = type.getRawClass();
        if (!ClassUtil.isConcrete(rawClass)) {
            throw new JsonMappingException("Can not find a Value deserializer for abstract type " + type);
        }
        throw new JsonMappingException("Can not find a Value deserializer for type " + type);
    }
    
    protected KeyDeserializer _handleUnknownKeyDeserializer(final JavaType type) throws JsonMappingException {
        throw new JsonMappingException("Can not find a (Map) Key deserializer for type " + type);
    }
}
