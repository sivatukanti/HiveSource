// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.deser.impl;

import org.apache.htrace.shaded.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import org.apache.htrace.shaded.fasterxml.jackson.databind.DeserializationContext;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonParser;
import org.apache.htrace.shaded.fasterxml.jackson.annotation.SimpleObjectIdResolver;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.SettableBeanProperty;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonDeserializer;
import org.apache.htrace.shaded.fasterxml.jackson.annotation.ObjectIdResolver;
import org.apache.htrace.shaded.fasterxml.jackson.annotation.ObjectIdGenerator;
import org.apache.htrace.shaded.fasterxml.jackson.databind.PropertyName;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JavaType;
import java.io.Serializable;

public class ObjectIdReader implements Serializable
{
    private static final long serialVersionUID = 1L;
    protected final JavaType _idType;
    public final PropertyName propertyName;
    public final ObjectIdGenerator<?> generator;
    public final ObjectIdResolver resolver;
    protected final JsonDeserializer<Object> _deserializer;
    public final SettableBeanProperty idProperty;
    
    protected ObjectIdReader(final JavaType t, final PropertyName propName, final ObjectIdGenerator<?> gen, final JsonDeserializer<?> deser, final SettableBeanProperty idProp, final ObjectIdResolver resolver) {
        this._idType = t;
        this.propertyName = propName;
        this.generator = gen;
        this.resolver = resolver;
        this._deserializer = (JsonDeserializer<Object>)deser;
        this.idProperty = idProp;
    }
    
    @Deprecated
    protected ObjectIdReader(final JavaType t, final PropertyName propName, final ObjectIdGenerator<?> gen, final JsonDeserializer<?> deser, final SettableBeanProperty idProp) {
        this(t, propName, gen, deser, idProp, new SimpleObjectIdResolver());
    }
    
    @Deprecated
    protected ObjectIdReader(final JavaType t, final String propName, final ObjectIdGenerator<?> gen, final JsonDeserializer<?> deser, final SettableBeanProperty idProp) {
        this(t, new PropertyName(propName), gen, deser, idProp);
    }
    
    public static ObjectIdReader construct(final JavaType idType, final PropertyName propName, final ObjectIdGenerator<?> generator, final JsonDeserializer<?> deser, final SettableBeanProperty idProp, final ObjectIdResolver resolver) {
        return new ObjectIdReader(idType, propName, generator, deser, idProp, resolver);
    }
    
    @Deprecated
    public static ObjectIdReader construct(final JavaType idType, final PropertyName propName, final ObjectIdGenerator<?> generator, final JsonDeserializer<?> deser, final SettableBeanProperty idProp) {
        return construct(idType, propName, generator, deser, idProp, new SimpleObjectIdResolver());
    }
    
    @Deprecated
    public static ObjectIdReader construct(final JavaType idType, final String propName, final ObjectIdGenerator<?> generator, final JsonDeserializer<?> deser, final SettableBeanProperty idProp) {
        return construct(idType, new PropertyName(propName), generator, deser, idProp);
    }
    
    public JsonDeserializer<Object> getDeserializer() {
        return this._deserializer;
    }
    
    public JavaType getIdType() {
        return this._idType;
    }
    
    public Object readObjectReference(final JsonParser jp, final DeserializationContext ctxt) throws IOException, JsonProcessingException {
        return this._deserializer.deserialize(jp, ctxt);
    }
}
