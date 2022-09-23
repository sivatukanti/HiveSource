// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.deser;

import org.apache.htrace.shaded.fasterxml.jackson.databind.JavaType;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonNode;
import org.apache.htrace.shaded.fasterxml.jackson.databind.type.MapLikeType;
import org.apache.htrace.shaded.fasterxml.jackson.databind.KeyDeserializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.type.MapType;
import org.apache.htrace.shaded.fasterxml.jackson.databind.type.CollectionLikeType;
import org.apache.htrace.shaded.fasterxml.jackson.databind.type.CollectionType;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonMappingException;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonDeserializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.BeanDescription;
import org.apache.htrace.shaded.fasterxml.jackson.databind.DeserializationConfig;
import org.apache.htrace.shaded.fasterxml.jackson.databind.type.ArrayType;

public interface Deserializers
{
    JsonDeserializer<?> findArrayDeserializer(final ArrayType p0, final DeserializationConfig p1, final BeanDescription p2, final TypeDeserializer p3, final JsonDeserializer<?> p4) throws JsonMappingException;
    
    JsonDeserializer<?> findCollectionDeserializer(final CollectionType p0, final DeserializationConfig p1, final BeanDescription p2, final TypeDeserializer p3, final JsonDeserializer<?> p4) throws JsonMappingException;
    
    JsonDeserializer<?> findCollectionLikeDeserializer(final CollectionLikeType p0, final DeserializationConfig p1, final BeanDescription p2, final TypeDeserializer p3, final JsonDeserializer<?> p4) throws JsonMappingException;
    
    JsonDeserializer<?> findEnumDeserializer(final Class<?> p0, final DeserializationConfig p1, final BeanDescription p2) throws JsonMappingException;
    
    JsonDeserializer<?> findMapDeserializer(final MapType p0, final DeserializationConfig p1, final BeanDescription p2, final KeyDeserializer p3, final TypeDeserializer p4, final JsonDeserializer<?> p5) throws JsonMappingException;
    
    JsonDeserializer<?> findMapLikeDeserializer(final MapLikeType p0, final DeserializationConfig p1, final BeanDescription p2, final KeyDeserializer p3, final TypeDeserializer p4, final JsonDeserializer<?> p5) throws JsonMappingException;
    
    JsonDeserializer<?> findTreeNodeDeserializer(final Class<? extends JsonNode> p0, final DeserializationConfig p1, final BeanDescription p2) throws JsonMappingException;
    
    JsonDeserializer<?> findBeanDeserializer(final JavaType p0, final DeserializationConfig p1, final BeanDescription p2) throws JsonMappingException;
    
    public static class Base implements Deserializers
    {
        @Override
        public JsonDeserializer<?> findArrayDeserializer(final ArrayType type, final DeserializationConfig config, final BeanDescription beanDesc, final TypeDeserializer elementTypeDeserializer, final JsonDeserializer<?> elementDeserializer) throws JsonMappingException {
            return null;
        }
        
        @Override
        public JsonDeserializer<?> findCollectionDeserializer(final CollectionType type, final DeserializationConfig config, final BeanDescription beanDesc, final TypeDeserializer elementTypeDeserializer, final JsonDeserializer<?> elementDeserializer) throws JsonMappingException {
            return null;
        }
        
        @Override
        public JsonDeserializer<?> findCollectionLikeDeserializer(final CollectionLikeType type, final DeserializationConfig config, final BeanDescription beanDesc, final TypeDeserializer elementTypeDeserializer, final JsonDeserializer<?> elementDeserializer) throws JsonMappingException {
            return null;
        }
        
        @Override
        public JsonDeserializer<?> findMapDeserializer(final MapType type, final DeserializationConfig config, final BeanDescription beanDesc, final KeyDeserializer keyDeserializer, final TypeDeserializer elementTypeDeserializer, final JsonDeserializer<?> elementDeserializer) throws JsonMappingException {
            return null;
        }
        
        @Override
        public JsonDeserializer<?> findMapLikeDeserializer(final MapLikeType type, final DeserializationConfig config, final BeanDescription beanDesc, final KeyDeserializer keyDeserializer, final TypeDeserializer elementTypeDeserializer, final JsonDeserializer<?> elementDeserializer) throws JsonMappingException {
            return null;
        }
        
        @Override
        public JsonDeserializer<?> findEnumDeserializer(final Class<?> type, final DeserializationConfig config, final BeanDescription beanDesc) throws JsonMappingException {
            return null;
        }
        
        @Override
        public JsonDeserializer<?> findTreeNodeDeserializer(final Class<? extends JsonNode> nodeType, final DeserializationConfig config, final BeanDescription beanDesc) throws JsonMappingException {
            return null;
        }
        
        @Override
        public JsonDeserializer<?> findBeanDeserializer(final JavaType type, final DeserializationConfig config, final BeanDescription beanDesc) throws JsonMappingException {
            return null;
        }
    }
}
