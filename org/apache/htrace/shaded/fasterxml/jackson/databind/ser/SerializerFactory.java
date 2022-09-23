// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.ser;

import org.apache.htrace.shaded.fasterxml.jackson.databind.jsontype.TypeSerializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.SerializationConfig;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonMappingException;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonSerializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JavaType;
import org.apache.htrace.shaded.fasterxml.jackson.databind.SerializerProvider;

public abstract class SerializerFactory
{
    public abstract SerializerFactory withAdditionalSerializers(final Serializers p0);
    
    public abstract SerializerFactory withAdditionalKeySerializers(final Serializers p0);
    
    public abstract SerializerFactory withSerializerModifier(final BeanSerializerModifier p0);
    
    public abstract JsonSerializer<Object> createSerializer(final SerializerProvider p0, final JavaType p1) throws JsonMappingException;
    
    public abstract TypeSerializer createTypeSerializer(final SerializationConfig p0, final JavaType p1) throws JsonMappingException;
    
    public abstract JsonSerializer<Object> createKeySerializer(final SerializationConfig p0, final JavaType p1, final JsonSerializer<Object> p2) throws JsonMappingException;
}
