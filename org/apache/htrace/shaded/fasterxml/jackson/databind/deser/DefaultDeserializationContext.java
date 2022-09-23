// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.deser;

import org.apache.htrace.shaded.fasterxml.jackson.databind.KeyDeserializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonMappingException;
import org.apache.htrace.shaded.fasterxml.jackson.databind.cfg.HandlerInstantiator;
import org.apache.htrace.shaded.fasterxml.jackson.databind.util.ClassUtil;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonDeserializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.Annotated;
import java.util.Map;
import org.apache.htrace.shaded.fasterxml.jackson.annotation.SimpleObjectIdResolver;
import java.util.Iterator;
import java.util.ArrayList;
import org.apache.htrace.shaded.fasterxml.jackson.databind.InjectableValues;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonParser;
import org.apache.htrace.shaded.fasterxml.jackson.databind.DeserializationConfig;
import org.apache.htrace.shaded.fasterxml.jackson.annotation.ObjectIdResolver;
import java.util.List;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.impl.ReadableObjectId;
import org.apache.htrace.shaded.fasterxml.jackson.annotation.ObjectIdGenerator;
import java.util.LinkedHashMap;
import java.io.Serializable;
import org.apache.htrace.shaded.fasterxml.jackson.databind.DeserializationContext;

public abstract class DefaultDeserializationContext extends DeserializationContext implements Serializable
{
    private static final long serialVersionUID = 1L;
    protected transient LinkedHashMap<ObjectIdGenerator.IdKey, ReadableObjectId> _objectIds;
    private List<ObjectIdResolver> _objectIdResolvers;
    
    protected DefaultDeserializationContext(final DeserializerFactory df, final DeserializerCache cache) {
        super(df, cache);
    }
    
    protected DefaultDeserializationContext(final DefaultDeserializationContext src, final DeserializationConfig config, final JsonParser jp, final InjectableValues values) {
        super(src, config, jp, values);
    }
    
    protected DefaultDeserializationContext(final DefaultDeserializationContext src, final DeserializerFactory factory) {
        super(src, factory);
    }
    
    @Override
    public ReadableObjectId findObjectId(final Object id, final ObjectIdGenerator<?> gen, final ObjectIdResolver resolverType) {
        final ObjectIdGenerator.IdKey key = gen.key(id);
        if (this._objectIds == null) {
            this._objectIds = new LinkedHashMap<ObjectIdGenerator.IdKey, ReadableObjectId>();
        }
        else {
            final ReadableObjectId entry = this._objectIds.get(key);
            if (entry != null) {
                return entry;
            }
        }
        ObjectIdResolver resolver = null;
        if (this._objectIdResolvers == null) {
            this._objectIdResolvers = new ArrayList<ObjectIdResolver>(8);
        }
        else {
            for (final ObjectIdResolver res : this._objectIdResolvers) {
                if (res.canUseFor(resolverType)) {
                    resolver = res;
                    break;
                }
            }
        }
        if (resolver == null) {
            resolver = resolverType.newForDeserialization(this);
            this._objectIdResolvers.add(resolver);
        }
        final ReadableObjectId entry2 = new ReadableObjectId(key);
        entry2.setResolver(resolver);
        this._objectIds.put(key, entry2);
        return entry2;
    }
    
    @Deprecated
    @Override
    public ReadableObjectId findObjectId(final Object id, final ObjectIdGenerator<?> gen) {
        return this.findObjectId(id, gen, new SimpleObjectIdResolver());
    }
    
    @Override
    public void checkUnresolvedObjectId() throws UnresolvedForwardReference {
        if (this._objectIds == null) {
            return;
        }
        UnresolvedForwardReference exception = null;
        for (final Map.Entry<ObjectIdGenerator.IdKey, ReadableObjectId> entry : this._objectIds.entrySet()) {
            final ReadableObjectId roid = entry.getValue();
            if (roid.hasReferringProperties()) {
                if (exception == null) {
                    exception = new UnresolvedForwardReference("Unresolved forward references for: ");
                }
                final Iterator<ReadableObjectId.Referring> iterator = roid.referringProperties();
                while (iterator.hasNext()) {
                    final ReadableObjectId.Referring referring = iterator.next();
                    exception.addUnresolvedId(roid.getKey().key, referring.getBeanType(), referring.getLocation());
                }
            }
        }
        if (exception != null) {
            throw exception;
        }
    }
    
    @Override
    public JsonDeserializer<Object> deserializerInstance(final Annotated ann, final Object deserDef) throws JsonMappingException {
        if (deserDef == null) {
            return null;
        }
        JsonDeserializer<?> deser;
        if (deserDef instanceof JsonDeserializer) {
            deser = (JsonDeserializer<?>)deserDef;
        }
        else {
            if (!(deserDef instanceof Class)) {
                throw new IllegalStateException("AnnotationIntrospector returned deserializer definition of type " + deserDef.getClass().getName() + "; expected type JsonDeserializer or Class<JsonDeserializer> instead");
            }
            final Class<?> deserClass = (Class<?>)deserDef;
            if (deserClass == JsonDeserializer.None.class || ClassUtil.isBogusClass(deserClass)) {
                return null;
            }
            if (!JsonDeserializer.class.isAssignableFrom(deserClass)) {
                throw new IllegalStateException("AnnotationIntrospector returned Class " + deserClass.getName() + "; expected Class<JsonDeserializer>");
            }
            final HandlerInstantiator hi = this._config.getHandlerInstantiator();
            deser = ((hi == null) ? null : hi.deserializerInstance(this._config, ann, deserClass));
            if (deser == null) {
                deser = ClassUtil.createInstance(deserClass, this._config.canOverrideAccessModifiers());
            }
        }
        if (deser instanceof ResolvableDeserializer) {
            ((ResolvableDeserializer)deser).resolve(this);
        }
        return (JsonDeserializer<Object>)deser;
    }
    
    @Override
    public final KeyDeserializer keyDeserializerInstance(final Annotated ann, final Object deserDef) throws JsonMappingException {
        if (deserDef == null) {
            return null;
        }
        KeyDeserializer deser;
        if (deserDef instanceof KeyDeserializer) {
            deser = (KeyDeserializer)deserDef;
        }
        else {
            if (!(deserDef instanceof Class)) {
                throw new IllegalStateException("AnnotationIntrospector returned key deserializer definition of type " + deserDef.getClass().getName() + "; expected type KeyDeserializer or Class<KeyDeserializer> instead");
            }
            final Class<?> deserClass = (Class<?>)deserDef;
            if (deserClass == KeyDeserializer.None.class || ClassUtil.isBogusClass(deserClass)) {
                return null;
            }
            if (!KeyDeserializer.class.isAssignableFrom(deserClass)) {
                throw new IllegalStateException("AnnotationIntrospector returned Class " + deserClass.getName() + "; expected Class<KeyDeserializer>");
            }
            final HandlerInstantiator hi = this._config.getHandlerInstantiator();
            deser = ((hi == null) ? null : hi.keyDeserializerInstance(this._config, ann, deserClass));
            if (deser == null) {
                deser = ClassUtil.createInstance(deserClass, this._config.canOverrideAccessModifiers());
            }
        }
        if (deser instanceof ResolvableDeserializer) {
            ((ResolvableDeserializer)deser).resolve(this);
        }
        return deser;
    }
    
    public abstract DefaultDeserializationContext with(final DeserializerFactory p0);
    
    public abstract DefaultDeserializationContext createInstance(final DeserializationConfig p0, final JsonParser p1, final InjectableValues p2);
    
    public static final class Impl extends DefaultDeserializationContext
    {
        private static final long serialVersionUID = 1L;
        
        public Impl(final DeserializerFactory df) {
            super(df, null);
        }
        
        protected Impl(final Impl src, final DeserializationConfig config, final JsonParser jp, final InjectableValues values) {
            super(src, config, jp, values);
        }
        
        protected Impl(final Impl src, final DeserializerFactory factory) {
            super(src, factory);
        }
        
        @Override
        public DefaultDeserializationContext createInstance(final DeserializationConfig config, final JsonParser jp, final InjectableValues values) {
            return new Impl(this, config, jp, values);
        }
        
        @Override
        public DefaultDeserializationContext with(final DeserializerFactory factory) {
            return new Impl(this, factory);
        }
    }
}
