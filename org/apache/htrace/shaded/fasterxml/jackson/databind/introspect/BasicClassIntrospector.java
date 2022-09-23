// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.introspect;

import org.apache.htrace.shaded.fasterxml.jackson.databind.type.SimpleType;
import org.apache.htrace.shaded.fasterxml.jackson.databind.BeanDescription;
import org.apache.htrace.shaded.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import org.apache.htrace.shaded.fasterxml.jackson.databind.AnnotationIntrospector;
import org.apache.htrace.shaded.fasterxml.jackson.databind.DeserializationConfig;
import org.apache.htrace.shaded.fasterxml.jackson.databind.cfg.MapperConfig;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JavaType;
import org.apache.htrace.shaded.fasterxml.jackson.databind.SerializationConfig;
import java.io.Serializable;

public class BasicClassIntrospector extends ClassIntrospector implements Serializable
{
    private static final long serialVersionUID = 1L;
    protected static final BasicBeanDescription STRING_DESC;
    protected static final BasicBeanDescription BOOLEAN_DESC;
    protected static final BasicBeanDescription INT_DESC;
    protected static final BasicBeanDescription LONG_DESC;
    public static final BasicClassIntrospector instance;
    
    @Override
    public BasicBeanDescription forSerialization(final SerializationConfig cfg, final JavaType type, final MixInResolver r) {
        BasicBeanDescription desc = this._findCachedDesc(type);
        if (desc == null) {
            desc = BasicBeanDescription.forSerialization(this.collectProperties(cfg, type, r, true, "set"));
        }
        return desc;
    }
    
    @Override
    public BasicBeanDescription forDeserialization(final DeserializationConfig cfg, final JavaType type, final MixInResolver r) {
        BasicBeanDescription desc = this._findCachedDesc(type);
        if (desc == null) {
            desc = BasicBeanDescription.forDeserialization(this.collectProperties(cfg, type, r, false, "set"));
        }
        return desc;
    }
    
    @Override
    public BasicBeanDescription forDeserializationWithBuilder(final DeserializationConfig cfg, final JavaType type, final MixInResolver r) {
        return BasicBeanDescription.forDeserialization(this.collectPropertiesWithBuilder(cfg, type, r, false));
    }
    
    @Override
    public BasicBeanDescription forCreation(final DeserializationConfig cfg, final JavaType type, final MixInResolver r) {
        BasicBeanDescription desc = this._findCachedDesc(type);
        if (desc == null) {
            desc = BasicBeanDescription.forDeserialization(this.collectProperties(cfg, type, r, false, "set"));
        }
        return desc;
    }
    
    @Override
    public BasicBeanDescription forClassAnnotations(final MapperConfig<?> cfg, final JavaType type, final MixInResolver r) {
        final boolean useAnnotations = cfg.isAnnotationProcessingEnabled();
        final AnnotatedClass ac = AnnotatedClass.construct(type.getRawClass(), useAnnotations ? cfg.getAnnotationIntrospector() : null, r);
        return BasicBeanDescription.forOtherUse(cfg, type, ac);
    }
    
    @Override
    public BasicBeanDescription forDirectClassAnnotations(final MapperConfig<?> cfg, final JavaType type, final MixInResolver r) {
        final boolean useAnnotations = cfg.isAnnotationProcessingEnabled();
        final AnnotationIntrospector ai = cfg.getAnnotationIntrospector();
        final AnnotatedClass ac = AnnotatedClass.constructWithoutSuperTypes(type.getRawClass(), useAnnotations ? ai : null, r);
        return BasicBeanDescription.forOtherUse(cfg, type, ac);
    }
    
    protected POJOPropertiesCollector collectProperties(final MapperConfig<?> config, final JavaType type, final MixInResolver r, final boolean forSerialization, final String mutatorPrefix) {
        final boolean useAnnotations = config.isAnnotationProcessingEnabled();
        final AnnotatedClass ac = AnnotatedClass.construct(type.getRawClass(), useAnnotations ? config.getAnnotationIntrospector() : null, r);
        return this.constructPropertyCollector(config, ac, type, forSerialization, mutatorPrefix).collect();
    }
    
    protected POJOPropertiesCollector collectPropertiesWithBuilder(final MapperConfig<?> config, final JavaType type, final MixInResolver r, final boolean forSerialization) {
        final boolean useAnnotations = config.isAnnotationProcessingEnabled();
        final AnnotationIntrospector ai = useAnnotations ? config.getAnnotationIntrospector() : null;
        final AnnotatedClass ac = AnnotatedClass.construct(type.getRawClass(), ai, r);
        final JsonPOJOBuilder.Value builderConfig = (ai == null) ? null : ai.findPOJOBuilderConfig(ac);
        final String mutatorPrefix = (builderConfig == null) ? "with" : builderConfig.withPrefix;
        return this.constructPropertyCollector(config, ac, type, forSerialization, mutatorPrefix).collect();
    }
    
    protected POJOPropertiesCollector constructPropertyCollector(final MapperConfig<?> config, final AnnotatedClass ac, final JavaType type, final boolean forSerialization, final String mutatorPrefix) {
        return new POJOPropertiesCollector(config, forSerialization, type, ac, mutatorPrefix);
    }
    
    protected BasicBeanDescription _findCachedDesc(final JavaType type) {
        final Class<?> cls = type.getRawClass();
        if (cls == String.class) {
            return BasicClassIntrospector.STRING_DESC;
        }
        if (cls == Boolean.TYPE) {
            return BasicClassIntrospector.BOOLEAN_DESC;
        }
        if (cls == Integer.TYPE) {
            return BasicClassIntrospector.INT_DESC;
        }
        if (cls == Long.TYPE) {
            return BasicClassIntrospector.LONG_DESC;
        }
        return null;
    }
    
    static {
        AnnotatedClass ac = AnnotatedClass.constructWithoutSuperTypes(String.class, null, null);
        STRING_DESC = BasicBeanDescription.forOtherUse(null, SimpleType.constructUnsafe(String.class), ac);
        ac = AnnotatedClass.constructWithoutSuperTypes(Boolean.TYPE, null, null);
        BOOLEAN_DESC = BasicBeanDescription.forOtherUse(null, SimpleType.constructUnsafe(Boolean.TYPE), ac);
        ac = AnnotatedClass.constructWithoutSuperTypes(Integer.TYPE, null, null);
        INT_DESC = BasicBeanDescription.forOtherUse(null, SimpleType.constructUnsafe(Integer.TYPE), ac);
        ac = AnnotatedClass.constructWithoutSuperTypes(Long.TYPE, null, null);
        LONG_DESC = BasicBeanDescription.forOtherUse(null, SimpleType.constructUnsafe(Long.TYPE), ac);
        instance = new BasicClassIntrospector();
    }
}
