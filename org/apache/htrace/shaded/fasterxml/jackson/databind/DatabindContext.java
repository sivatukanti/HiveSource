// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind;

import org.apache.htrace.shaded.fasterxml.jackson.databind.util.Converter;
import org.apache.htrace.shaded.fasterxml.jackson.annotation.ObjectIdResolver;
import org.apache.htrace.shaded.fasterxml.jackson.databind.cfg.HandlerInstantiator;
import org.apache.htrace.shaded.fasterxml.jackson.databind.util.ClassUtil;
import org.apache.htrace.shaded.fasterxml.jackson.annotation.ObjectIdGenerator;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.ObjectIdInfo;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.Annotated;
import org.apache.htrace.shaded.fasterxml.jackson.databind.type.TypeFactory;
import java.lang.reflect.Type;
import org.apache.htrace.shaded.fasterxml.jackson.databind.cfg.MapperConfig;

public abstract class DatabindContext
{
    public abstract MapperConfig<?> getConfig();
    
    public abstract AnnotationIntrospector getAnnotationIntrospector();
    
    public final boolean isEnabled(final MapperFeature feature) {
        return this.getConfig().isEnabled(feature);
    }
    
    public final boolean canOverrideAccessModifiers() {
        return this.getConfig().canOverrideAccessModifiers();
    }
    
    public abstract Class<?> getActiveView();
    
    public abstract Object getAttribute(final Object p0);
    
    public abstract DatabindContext setAttribute(final Object p0, final Object p1);
    
    public JavaType constructType(final Type type) {
        return this.getTypeFactory().constructType(type);
    }
    
    public JavaType constructSpecializedType(final JavaType baseType, final Class<?> subclass) {
        if (baseType.getRawClass() == subclass) {
            return baseType;
        }
        return this.getConfig().constructSpecializedType(baseType, subclass);
    }
    
    public abstract TypeFactory getTypeFactory();
    
    public ObjectIdGenerator<?> objectIdGeneratorInstance(final Annotated annotated, final ObjectIdInfo objectIdInfo) throws JsonMappingException {
        final Class<?> implClass = objectIdInfo.getGeneratorType();
        final MapperConfig<?> config = this.getConfig();
        final HandlerInstantiator hi = config.getHandlerInstantiator();
        ObjectIdGenerator<?> gen = (hi == null) ? null : hi.objectIdGeneratorInstance(config, annotated, implClass);
        if (gen == null) {
            gen = ClassUtil.createInstance(implClass, config.canOverrideAccessModifiers());
        }
        return gen.forScope(objectIdInfo.getScope());
    }
    
    public ObjectIdResolver objectIdResolverInstance(final Annotated annotated, final ObjectIdInfo objectIdInfo) {
        final Class<? extends ObjectIdResolver> implClass = objectIdInfo.getResolverType();
        final MapperConfig<?> config = this.getConfig();
        final HandlerInstantiator hi = config.getHandlerInstantiator();
        ObjectIdResolver resolver = (hi == null) ? null : hi.resolverIdGeneratorInstance(config, annotated, implClass);
        if (resolver == null) {
            resolver = ClassUtil.createInstance(implClass, config.canOverrideAccessModifiers());
        }
        return resolver;
    }
    
    public Converter<Object, Object> converterInstance(final Annotated annotated, final Object converterDef) throws JsonMappingException {
        if (converterDef == null) {
            return null;
        }
        if (converterDef instanceof Converter) {
            return (Converter<Object, Object>)converterDef;
        }
        if (!(converterDef instanceof Class)) {
            throw new IllegalStateException("AnnotationIntrospector returned Converter definition of type " + converterDef.getClass().getName() + "; expected type Converter or Class<Converter> instead");
        }
        final Class<?> converterClass = (Class<?>)converterDef;
        if (converterClass == Converter.None.class || ClassUtil.isBogusClass(converterClass)) {
            return null;
        }
        if (!Converter.class.isAssignableFrom(converterClass)) {
            throw new IllegalStateException("AnnotationIntrospector returned Class " + converterClass.getName() + "; expected Class<Converter>");
        }
        final MapperConfig<?> config = this.getConfig();
        final HandlerInstantiator hi = config.getHandlerInstantiator();
        Converter<?, ?> conv = (hi == null) ? null : hi.converterInstance(config, annotated, converterClass);
        if (conv == null) {
            conv = ClassUtil.createInstance(converterClass, config.canOverrideAccessModifiers());
        }
        return (Converter<Object, Object>)conv;
    }
}
