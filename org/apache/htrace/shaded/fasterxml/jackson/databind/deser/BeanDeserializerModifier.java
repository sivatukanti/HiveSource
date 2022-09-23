// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.deser;

import org.apache.htrace.shaded.fasterxml.jackson.databind.KeyDeserializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JavaType;
import org.apache.htrace.shaded.fasterxml.jackson.databind.type.MapLikeType;
import org.apache.htrace.shaded.fasterxml.jackson.databind.type.MapType;
import org.apache.htrace.shaded.fasterxml.jackson.databind.type.CollectionLikeType;
import org.apache.htrace.shaded.fasterxml.jackson.databind.type.CollectionType;
import org.apache.htrace.shaded.fasterxml.jackson.databind.type.ArrayType;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonDeserializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import java.util.List;
import org.apache.htrace.shaded.fasterxml.jackson.databind.BeanDescription;
import org.apache.htrace.shaded.fasterxml.jackson.databind.DeserializationConfig;

public abstract class BeanDeserializerModifier
{
    public List<BeanPropertyDefinition> updateProperties(final DeserializationConfig config, final BeanDescription beanDesc, final List<BeanPropertyDefinition> propDefs) {
        return propDefs;
    }
    
    public BeanDeserializerBuilder updateBuilder(final DeserializationConfig config, final BeanDescription beanDesc, final BeanDeserializerBuilder builder) {
        return builder;
    }
    
    public JsonDeserializer<?> modifyDeserializer(final DeserializationConfig config, final BeanDescription beanDesc, final JsonDeserializer<?> deserializer) {
        return deserializer;
    }
    
    public JsonDeserializer<?> modifyArrayDeserializer(final DeserializationConfig config, final ArrayType valueType, final BeanDescription beanDesc, final JsonDeserializer<?> deserializer) {
        return deserializer;
    }
    
    public JsonDeserializer<?> modifyCollectionDeserializer(final DeserializationConfig config, final CollectionType type, final BeanDescription beanDesc, final JsonDeserializer<?> deserializer) {
        return deserializer;
    }
    
    public JsonDeserializer<?> modifyCollectionLikeDeserializer(final DeserializationConfig config, final CollectionLikeType type, final BeanDescription beanDesc, final JsonDeserializer<?> deserializer) {
        return deserializer;
    }
    
    public JsonDeserializer<?> modifyMapDeserializer(final DeserializationConfig config, final MapType type, final BeanDescription beanDesc, final JsonDeserializer<?> deserializer) {
        return deserializer;
    }
    
    public JsonDeserializer<?> modifyMapLikeDeserializer(final DeserializationConfig config, final MapLikeType type, final BeanDescription beanDesc, final JsonDeserializer<?> deserializer) {
        return deserializer;
    }
    
    public JsonDeserializer<?> modifyEnumDeserializer(final DeserializationConfig config, final JavaType type, final BeanDescription beanDesc, final JsonDeserializer<?> deserializer) {
        return deserializer;
    }
    
    public KeyDeserializer modifyKeyDeserializer(final DeserializationConfig config, final JavaType type, final KeyDeserializer deserializer) {
        return deserializer;
    }
}
