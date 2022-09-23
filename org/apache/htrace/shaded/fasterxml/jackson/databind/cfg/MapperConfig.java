// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.cfg;

import org.apache.htrace.shaded.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import org.apache.htrace.shaded.fasterxml.jackson.databind.util.ClassUtil;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.Annotated;
import org.apache.htrace.shaded.fasterxml.jackson.core.Base64Variant;
import java.util.TimeZone;
import java.util.Locale;
import java.text.DateFormat;
import org.apache.htrace.shaded.fasterxml.jackson.databind.BeanDescription;
import org.apache.htrace.shaded.fasterxml.jackson.core.type.TypeReference;
import java.lang.reflect.Type;
import org.apache.htrace.shaded.fasterxml.jackson.databind.type.TypeBindings;
import org.apache.htrace.shaded.fasterxml.jackson.databind.type.TypeFactory;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsontype.SubtypeResolver;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JavaType;
import org.apache.htrace.shaded.fasterxml.jackson.databind.PropertyNamingStrategy;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.VisibilityChecker;
import org.apache.htrace.shaded.fasterxml.jackson.databind.AnnotationIntrospector;
import org.apache.htrace.shaded.fasterxml.jackson.core.io.SerializedString;
import org.apache.htrace.shaded.fasterxml.jackson.core.SerializableString;
import org.apache.htrace.shaded.fasterxml.jackson.databind.MapperFeature;
import java.io.Serializable;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.ClassIntrospector;

public abstract class MapperConfig<T extends MapperConfig<T>> implements ClassIntrospector.MixInResolver, Serializable
{
    private static final long serialVersionUID = 8891625428805876137L;
    protected final int _mapperFeatures;
    protected final BaseSettings _base;
    
    protected MapperConfig(final BaseSettings base, final int mapperFeatures) {
        this._base = base;
        this._mapperFeatures = mapperFeatures;
    }
    
    protected MapperConfig(final MapperConfig<T> src) {
        this._base = src._base;
        this._mapperFeatures = src._mapperFeatures;
    }
    
    public static <F extends Enum> int collectFeatureDefaults(final Class<F> enumClass) {
        int flags = 0;
        for (final F value : (java.lang.Enum[])enumClass.getEnumConstants()) {
            if (((ConfigFeature)value).enabledByDefault()) {
                flags |= ((ConfigFeature)value).getMask();
            }
        }
        return flags;
    }
    
    public abstract T with(final MapperFeature... p0);
    
    public abstract T without(final MapperFeature... p0);
    
    public abstract T with(final MapperFeature p0, final boolean p1);
    
    public final boolean isEnabled(final MapperFeature f) {
        return (this._mapperFeatures & f.getMask()) != 0x0;
    }
    
    public final boolean hasMapperFeatures(final int featureMask) {
        return (this._mapperFeatures & featureMask) == featureMask;
    }
    
    public final boolean isAnnotationProcessingEnabled() {
        return this.isEnabled(MapperFeature.USE_ANNOTATIONS);
    }
    
    public final boolean canOverrideAccessModifiers() {
        return this.isEnabled(MapperFeature.CAN_OVERRIDE_ACCESS_MODIFIERS);
    }
    
    public final boolean shouldSortPropertiesAlphabetically() {
        return this.isEnabled(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY);
    }
    
    public abstract boolean useRootWrapping();
    
    public SerializableString compileString(final String src) {
        return new SerializedString(src);
    }
    
    public ClassIntrospector getClassIntrospector() {
        return this._base.getClassIntrospector();
    }
    
    public AnnotationIntrospector getAnnotationIntrospector() {
        return this._base.getAnnotationIntrospector();
    }
    
    public VisibilityChecker<?> getDefaultVisibilityChecker() {
        return this._base.getVisibilityChecker();
    }
    
    public final PropertyNamingStrategy getPropertyNamingStrategy() {
        return this._base.getPropertyNamingStrategy();
    }
    
    public final HandlerInstantiator getHandlerInstantiator() {
        return this._base.getHandlerInstantiator();
    }
    
    public final TypeResolverBuilder<?> getDefaultTyper(final JavaType baseType) {
        return this._base.getTypeResolverBuilder();
    }
    
    public abstract SubtypeResolver getSubtypeResolver();
    
    public final TypeFactory getTypeFactory() {
        return this._base.getTypeFactory();
    }
    
    public final JavaType constructType(final Class<?> cls) {
        return this.getTypeFactory().constructType(cls, (TypeBindings)null);
    }
    
    public final JavaType constructType(final TypeReference<?> valueTypeRef) {
        return this.getTypeFactory().constructType(valueTypeRef.getType(), (TypeBindings)null);
    }
    
    public JavaType constructSpecializedType(final JavaType baseType, final Class<?> subclass) {
        return this.getTypeFactory().constructSpecializedType(baseType, subclass);
    }
    
    public BeanDescription introspectClassAnnotations(final Class<?> cls) {
        return this.introspectClassAnnotations(this.constructType(cls));
    }
    
    public abstract BeanDescription introspectClassAnnotations(final JavaType p0);
    
    public BeanDescription introspectDirectClassAnnotations(final Class<?> cls) {
        return this.introspectDirectClassAnnotations(this.constructType(cls));
    }
    
    public abstract BeanDescription introspectDirectClassAnnotations(final JavaType p0);
    
    public final DateFormat getDateFormat() {
        return this._base.getDateFormat();
    }
    
    public final Locale getLocale() {
        return this._base.getLocale();
    }
    
    public final TimeZone getTimeZone() {
        return this._base.getTimeZone();
    }
    
    public abstract Class<?> getActiveView();
    
    public Base64Variant getBase64Variant() {
        return this._base.getBase64Variant();
    }
    
    public abstract ContextAttributes getAttributes();
    
    public TypeResolverBuilder<?> typeResolverBuilderInstance(final Annotated annotated, final Class<? extends TypeResolverBuilder<?>> builderClass) {
        final HandlerInstantiator hi = this.getHandlerInstantiator();
        if (hi != null) {
            final TypeResolverBuilder<?> builder = hi.typeResolverBuilderInstance(this, annotated, builderClass);
            if (builder != null) {
                return builder;
            }
        }
        return ClassUtil.createInstance(builderClass, this.canOverrideAccessModifiers());
    }
    
    public TypeIdResolver typeIdResolverInstance(final Annotated annotated, final Class<? extends TypeIdResolver> resolverClass) {
        final HandlerInstantiator hi = this.getHandlerInstantiator();
        if (hi != null) {
            final TypeIdResolver builder = hi.typeIdResolverInstance(this, annotated, resolverClass);
            if (builder != null) {
                return builder;
            }
        }
        return ClassUtil.createInstance(resolverClass, this.canOverrideAccessModifiers());
    }
}
