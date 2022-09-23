// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.module;

import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonNode;
import org.apache.htrace.shaded.fasterxml.jackson.databind.type.MapLikeType;
import org.apache.htrace.shaded.fasterxml.jackson.databind.KeyDeserializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.type.MapType;
import org.apache.htrace.shaded.fasterxml.jackson.databind.type.CollectionLikeType;
import org.apache.htrace.shaded.fasterxml.jackson.databind.type.CollectionType;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JavaType;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonMappingException;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.BeanDescription;
import org.apache.htrace.shaded.fasterxml.jackson.databind.DeserializationConfig;
import org.apache.htrace.shaded.fasterxml.jackson.databind.type.ArrayType;
import java.util.Iterator;
import java.util.Map;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonDeserializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.type.ClassKey;
import java.util.HashMap;
import java.io.Serializable;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.Deserializers;

public class SimpleDeserializers implements Deserializers, Serializable
{
    private static final long serialVersionUID = -3006673354353448880L;
    protected HashMap<ClassKey, JsonDeserializer<?>> _classMappings;
    protected boolean _hasEnumDeserializer;
    
    public SimpleDeserializers() {
        this._classMappings = null;
        this._hasEnumDeserializer = false;
    }
    
    public SimpleDeserializers(final Map<Class<?>, JsonDeserializer<?>> desers) {
        this._classMappings = null;
        this._hasEnumDeserializer = false;
        this.addDeserializers(desers);
    }
    
    public <T> void addDeserializer(final Class<T> forClass, final JsonDeserializer<? extends T> deser) {
        final ClassKey key = new ClassKey(forClass);
        if (this._classMappings == null) {
            this._classMappings = new HashMap<ClassKey, JsonDeserializer<?>>();
        }
        this._classMappings.put(key, deser);
        if (forClass == Enum.class) {
            this._hasEnumDeserializer = true;
        }
    }
    
    public void addDeserializers(final Map<Class<?>, JsonDeserializer<?>> desers) {
        for (final Map.Entry<Class<?>, JsonDeserializer<?>> entry : desers.entrySet()) {
            final Class<?> cls = entry.getKey();
            final JsonDeserializer<Object> deser = entry.getValue();
            this.addDeserializer(cls, deser);
        }
    }
    
    @Override
    public JsonDeserializer<?> findArrayDeserializer(final ArrayType type, final DeserializationConfig config, final BeanDescription beanDesc, final TypeDeserializer elementTypeDeserializer, final JsonDeserializer<?> elementDeserializer) throws JsonMappingException {
        return (this._classMappings == null) ? null : this._classMappings.get(new ClassKey(type.getRawClass()));
    }
    
    @Override
    public JsonDeserializer<?> findBeanDeserializer(final JavaType type, final DeserializationConfig config, final BeanDescription beanDesc) throws JsonMappingException {
        return (this._classMappings == null) ? null : this._classMappings.get(new ClassKey(type.getRawClass()));
    }
    
    @Override
    public JsonDeserializer<?> findCollectionDeserializer(final CollectionType type, final DeserializationConfig config, final BeanDescription beanDesc, final TypeDeserializer elementTypeDeserializer, final JsonDeserializer<?> elementDeserializer) throws JsonMappingException {
        return (this._classMappings == null) ? null : this._classMappings.get(new ClassKey(type.getRawClass()));
    }
    
    @Override
    public JsonDeserializer<?> findCollectionLikeDeserializer(final CollectionLikeType type, final DeserializationConfig config, final BeanDescription beanDesc, final TypeDeserializer elementTypeDeserializer, final JsonDeserializer<?> elementDeserializer) throws JsonMappingException {
        return (this._classMappings == null) ? null : this._classMappings.get(new ClassKey(type.getRawClass()));
    }
    
    @Override
    public JsonDeserializer<?> findEnumDeserializer(final Class<?> type, final DeserializationConfig config, final BeanDescription beanDesc) throws JsonMappingException {
        if (this._classMappings == null) {
            return null;
        }
        JsonDeserializer<?> deser = this._classMappings.get(new ClassKey(type));
        if (deser == null && this._hasEnumDeserializer && type.isEnum()) {
            deser = this._classMappings.get(new ClassKey(Enum.class));
        }
        return deser;
    }
    
    @Override
    public JsonDeserializer<?> findMapDeserializer(final MapType type, final DeserializationConfig config, final BeanDescription beanDesc, final KeyDeserializer keyDeserializer, final TypeDeserializer elementTypeDeserializer, final JsonDeserializer<?> elementDeserializer) throws JsonMappingException {
        return (this._classMappings == null) ? null : this._classMappings.get(new ClassKey(type.getRawClass()));
    }
    
    @Override
    public JsonDeserializer<?> findMapLikeDeserializer(final MapLikeType type, final DeserializationConfig config, final BeanDescription beanDesc, final KeyDeserializer keyDeserializer, final TypeDeserializer elementTypeDeserializer, final JsonDeserializer<?> elementDeserializer) throws JsonMappingException {
        return (this._classMappings == null) ? null : this._classMappings.get(new ClassKey(type.getRawClass()));
    }
    
    @Override
    public JsonDeserializer<?> findTreeNodeDeserializer(final Class<? extends JsonNode> nodeType, final DeserializationConfig config, final BeanDescription beanDesc) throws JsonMappingException {
        return (this._classMappings == null) ? null : this._classMappings.get(new ClassKey(nodeType));
    }
}
