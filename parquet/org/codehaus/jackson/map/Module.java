// 
// Decompiled by Procyon v0.5.36
// 

package parquet.org.codehaus.jackson.map;

import parquet.org.codehaus.jackson.map.deser.ValueInstantiators;
import parquet.org.codehaus.jackson.map.type.TypeModifier;
import parquet.org.codehaus.jackson.map.ser.BeanSerializerModifier;
import parquet.org.codehaus.jackson.map.deser.BeanDeserializerModifier;
import parquet.org.codehaus.jackson.JsonGenerator;
import parquet.org.codehaus.jackson.JsonParser;
import parquet.org.codehaus.jackson.Version;
import parquet.org.codehaus.jackson.Versioned;

public abstract class Module implements Versioned
{
    public abstract String getModuleName();
    
    public abstract Version version();
    
    public abstract void setupModule(final SetupContext p0);
    
    public interface SetupContext
    {
        Version getMapperVersion();
        
        DeserializationConfig getDeserializationConfig();
        
        SerializationConfig getSerializationConfig();
        
        boolean isEnabled(final DeserializationConfig.Feature p0);
        
        boolean isEnabled(final SerializationConfig.Feature p0);
        
        boolean isEnabled(final JsonParser.Feature p0);
        
        boolean isEnabled(final JsonGenerator.Feature p0);
        
        void addDeserializers(final Deserializers p0);
        
        void addKeyDeserializers(final KeyDeserializers p0);
        
        void addSerializers(final Serializers p0);
        
        void addKeySerializers(final Serializers p0);
        
        void addBeanDeserializerModifier(final BeanDeserializerModifier p0);
        
        void addBeanSerializerModifier(final BeanSerializerModifier p0);
        
        void addAbstractTypeResolver(final AbstractTypeResolver p0);
        
        void addTypeModifier(final TypeModifier p0);
        
        void addValueInstantiators(final ValueInstantiators p0);
        
        void insertAnnotationIntrospector(final AnnotationIntrospector p0);
        
        void appendAnnotationIntrospector(final AnnotationIntrospector p0);
        
        void setMixInAnnotations(final Class<?> p0, final Class<?> p1);
    }
}
