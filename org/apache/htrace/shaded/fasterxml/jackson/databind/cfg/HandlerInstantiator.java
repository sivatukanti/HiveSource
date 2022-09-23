// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.cfg;

import org.apache.htrace.shaded.fasterxml.jackson.databind.util.Converter;
import org.apache.htrace.shaded.fasterxml.jackson.databind.PropertyNamingStrategy;
import org.apache.htrace.shaded.fasterxml.jackson.annotation.ObjectIdResolver;
import org.apache.htrace.shaded.fasterxml.jackson.annotation.ObjectIdGenerator;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.ValueInstantiator;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonSerializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.SerializationConfig;
import org.apache.htrace.shaded.fasterxml.jackson.databind.KeyDeserializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonDeserializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.Annotated;
import org.apache.htrace.shaded.fasterxml.jackson.databind.DeserializationConfig;

public abstract class HandlerInstantiator
{
    public abstract JsonDeserializer<?> deserializerInstance(final DeserializationConfig p0, final Annotated p1, final Class<?> p2);
    
    public abstract KeyDeserializer keyDeserializerInstance(final DeserializationConfig p0, final Annotated p1, final Class<?> p2);
    
    public abstract JsonSerializer<?> serializerInstance(final SerializationConfig p0, final Annotated p1, final Class<?> p2);
    
    public abstract TypeResolverBuilder<?> typeResolverBuilderInstance(final MapperConfig<?> p0, final Annotated p1, final Class<?> p2);
    
    public abstract TypeIdResolver typeIdResolverInstance(final MapperConfig<?> p0, final Annotated p1, final Class<?> p2);
    
    public ValueInstantiator valueInstantiatorInstance(final MapperConfig<?> config, final Annotated annotated, final Class<?> resolverClass) {
        return null;
    }
    
    public ObjectIdGenerator<?> objectIdGeneratorInstance(final MapperConfig<?> config, final Annotated annotated, final Class<?> implClass) {
        return null;
    }
    
    public ObjectIdResolver resolverIdGeneratorInstance(final MapperConfig<?> config, final Annotated annotated, final Class<?> implClass) {
        return null;
    }
    
    public PropertyNamingStrategy namingStrategyInstance(final MapperConfig<?> config, final Annotated annotated, final Class<?> implClass) {
        return null;
    }
    
    public Converter<?, ?> converterInstance(final MapperConfig<?> config, final Annotated annotated, final Class<?> implClass) {
        return null;
    }
}
