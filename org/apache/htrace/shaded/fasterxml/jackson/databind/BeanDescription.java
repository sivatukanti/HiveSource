// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind;

import org.apache.htrace.shaded.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import org.apache.htrace.shaded.fasterxml.jackson.databind.util.Converter;
import org.apache.htrace.shaded.fasterxml.jackson.annotation.JsonFormat;
import org.apache.htrace.shaded.fasterxml.jackson.annotation.JsonInclude;
import java.lang.reflect.Method;
import java.lang.reflect.Constructor;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.AnnotatedConstructor;
import java.util.Set;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.AnnotatedMember;
import java.util.Map;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import java.util.List;
import org.apache.htrace.shaded.fasterxml.jackson.databind.util.Annotations;
import java.lang.reflect.Type;
import org.apache.htrace.shaded.fasterxml.jackson.databind.type.TypeBindings;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.ObjectIdInfo;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.AnnotatedClass;

public abstract class BeanDescription
{
    protected final JavaType _type;
    
    protected BeanDescription(final JavaType type) {
        this._type = type;
    }
    
    public JavaType getType() {
        return this._type;
    }
    
    public Class<?> getBeanClass() {
        return this._type.getRawClass();
    }
    
    public abstract AnnotatedClass getClassInfo();
    
    public abstract ObjectIdInfo getObjectIdInfo();
    
    public abstract boolean hasKnownClassAnnotations();
    
    public abstract TypeBindings bindingsForBeanType();
    
    public abstract JavaType resolveType(final Type p0);
    
    public abstract Annotations getClassAnnotations();
    
    public abstract List<BeanPropertyDefinition> findProperties();
    
    public abstract Map<String, AnnotatedMember> findBackReferenceProperties();
    
    public abstract Set<String> getIgnoredPropertyNames();
    
    public abstract List<AnnotatedConstructor> getConstructors();
    
    public abstract List<AnnotatedMethod> getFactoryMethods();
    
    public abstract AnnotatedConstructor findDefaultConstructor();
    
    public abstract Constructor<?> findSingleArgConstructor(final Class<?>... p0);
    
    public abstract Method findFactoryMethod(final Class<?>... p0);
    
    public abstract AnnotatedMember findAnyGetter();
    
    public abstract AnnotatedMethod findAnySetter();
    
    public abstract AnnotatedMethod findJsonValueMethod();
    
    public abstract AnnotatedMethod findMethod(final String p0, final Class<?>[] p1);
    
    public abstract JsonInclude.Include findSerializationInclusion(final JsonInclude.Include p0);
    
    public abstract JsonFormat.Value findExpectedFormat(final JsonFormat.Value p0);
    
    public abstract Converter<Object, Object> findSerializationConverter();
    
    public abstract Converter<Object, Object> findDeserializationConverter();
    
    public abstract Map<Object, AnnotatedMember> findInjectables();
    
    public abstract Class<?> findPOJOBuilder();
    
    public abstract JsonPOJOBuilder.Value findPOJOBuilderConfig();
    
    public abstract Object instantiateBean(final boolean p0);
}
