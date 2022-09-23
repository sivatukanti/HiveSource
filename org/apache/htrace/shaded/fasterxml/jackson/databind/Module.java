// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind;

import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsontype.NamedType;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.ClassIntrospector;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.ValueInstantiators;
import org.apache.htrace.shaded.fasterxml.jackson.databind.type.TypeModifier;
import org.apache.htrace.shaded.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import org.apache.htrace.shaded.fasterxml.jackson.databind.ser.Serializers;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.KeyDeserializers;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.Deserializers;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonGenerator;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonParser;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonFactory;
import org.apache.htrace.shaded.fasterxml.jackson.databind.type.TypeFactory;
import org.apache.htrace.shaded.fasterxml.jackson.core.ObjectCodec;
import org.apache.htrace.shaded.fasterxml.jackson.core.Version;
import org.apache.htrace.shaded.fasterxml.jackson.core.Versioned;

public abstract class Module implements Versioned
{
    public abstract String getModuleName();
    
    @Override
    public abstract Version version();
    
    public abstract void setupModule(final SetupContext p0);
    
    public interface SetupContext
    {
        Version getMapperVersion();
        
         <C extends ObjectCodec> C getOwner();
        
        TypeFactory getTypeFactory();
        
        boolean isEnabled(final MapperFeature p0);
        
        boolean isEnabled(final DeserializationFeature p0);
        
        boolean isEnabled(final SerializationFeature p0);
        
        boolean isEnabled(final JsonFactory.Feature p0);
        
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
        
        void setClassIntrospector(final ClassIntrospector p0);
        
        void insertAnnotationIntrospector(final AnnotationIntrospector p0);
        
        void appendAnnotationIntrospector(final AnnotationIntrospector p0);
        
        void registerSubtypes(final Class<?>... p0);
        
        void registerSubtypes(final NamedType... p0);
        
        void setMixInAnnotations(final Class<?> p0, final Class<?> p1);
        
        void addDeserializationProblemHandler(final DeserializationProblemHandler p0);
        
        void setNamingStrategy(final PropertyNamingStrategy p0);
    }
}
