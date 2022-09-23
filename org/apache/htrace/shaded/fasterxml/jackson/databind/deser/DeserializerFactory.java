// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.deser;

import org.apache.htrace.shaded.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.KeyDeserializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.type.MapLikeType;
import org.apache.htrace.shaded.fasterxml.jackson.databind.type.MapType;
import org.apache.htrace.shaded.fasterxml.jackson.databind.type.CollectionLikeType;
import org.apache.htrace.shaded.fasterxml.jackson.databind.type.CollectionType;
import org.apache.htrace.shaded.fasterxml.jackson.databind.type.ArrayType;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonDeserializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.BeanDescription;
import org.apache.htrace.shaded.fasterxml.jackson.databind.DeserializationContext;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonMappingException;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JavaType;
import org.apache.htrace.shaded.fasterxml.jackson.databind.DeserializationConfig;
import org.apache.htrace.shaded.fasterxml.jackson.databind.AbstractTypeResolver;

public abstract class DeserializerFactory
{
    protected static final Deserializers[] NO_DESERIALIZERS;
    
    public abstract DeserializerFactory withAdditionalDeserializers(final Deserializers p0);
    
    public abstract DeserializerFactory withAdditionalKeyDeserializers(final KeyDeserializers p0);
    
    public abstract DeserializerFactory withDeserializerModifier(final BeanDeserializerModifier p0);
    
    public abstract DeserializerFactory withAbstractTypeResolver(final AbstractTypeResolver p0);
    
    public abstract DeserializerFactory withValueInstantiators(final ValueInstantiators p0);
    
    public abstract JavaType mapAbstractType(final DeserializationConfig p0, final JavaType p1) throws JsonMappingException;
    
    public abstract ValueInstantiator findValueInstantiator(final DeserializationContext p0, final BeanDescription p1) throws JsonMappingException;
    
    public abstract JsonDeserializer<Object> createBeanDeserializer(final DeserializationContext p0, final JavaType p1, final BeanDescription p2) throws JsonMappingException;
    
    public abstract JsonDeserializer<Object> createBuilderBasedDeserializer(final DeserializationContext p0, final JavaType p1, final BeanDescription p2, final Class<?> p3) throws JsonMappingException;
    
    public abstract JsonDeserializer<?> createArrayDeserializer(final DeserializationContext p0, final ArrayType p1, final BeanDescription p2) throws JsonMappingException;
    
    public abstract JsonDeserializer<?> createCollectionDeserializer(final DeserializationContext p0, final CollectionType p1, final BeanDescription p2) throws JsonMappingException;
    
    public abstract JsonDeserializer<?> createCollectionLikeDeserializer(final DeserializationContext p0, final CollectionLikeType p1, final BeanDescription p2) throws JsonMappingException;
    
    public abstract JsonDeserializer<?> createEnumDeserializer(final DeserializationContext p0, final JavaType p1, final BeanDescription p2) throws JsonMappingException;
    
    public abstract JsonDeserializer<?> createMapDeserializer(final DeserializationContext p0, final MapType p1, final BeanDescription p2) throws JsonMappingException;
    
    public abstract JsonDeserializer<?> createMapLikeDeserializer(final DeserializationContext p0, final MapLikeType p1, final BeanDescription p2) throws JsonMappingException;
    
    public abstract JsonDeserializer<?> createTreeDeserializer(final DeserializationConfig p0, final JavaType p1, final BeanDescription p2) throws JsonMappingException;
    
    public abstract KeyDeserializer createKeyDeserializer(final DeserializationContext p0, final JavaType p1) throws JsonMappingException;
    
    public abstract TypeDeserializer findTypeDeserializer(final DeserializationConfig p0, final JavaType p1) throws JsonMappingException;
    
    static {
        NO_DESERIALIZERS = new Deserializers[0];
    }
}
