// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.cfg;

import org.apache.htrace.shaded.fasterxml.jackson.databind.MapperFeature;
import org.apache.htrace.shaded.fasterxml.jackson.core.Base64Variant;
import java.util.TimeZone;
import java.util.Locale;
import org.apache.htrace.shaded.fasterxml.jackson.annotation.JsonAutoDetect;
import org.apache.htrace.shaded.fasterxml.jackson.annotation.PropertyAccessor;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.VisibilityChecker;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
import org.apache.htrace.shaded.fasterxml.jackson.databind.type.TypeFactory;
import org.apache.htrace.shaded.fasterxml.jackson.databind.PropertyNamingStrategy;
import java.text.DateFormat;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.ClassIntrospector;
import org.apache.htrace.shaded.fasterxml.jackson.databind.AnnotationIntrospector;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsontype.SubtypeResolver;
import org.apache.htrace.shaded.fasterxml.jackson.databind.type.ClassKey;
import java.util.Map;
import java.io.Serializable;

public abstract class MapperConfigBase<CFG extends ConfigFeature, T extends MapperConfigBase<CFG, T>> extends MapperConfig<T> implements Serializable
{
    private static final long serialVersionUID = 6062961959359172474L;
    private static final int DEFAULT_MAPPER_FEATURES;
    protected final Map<ClassKey, Class<?>> _mixInAnnotations;
    protected final SubtypeResolver _subtypeResolver;
    protected final String _rootName;
    protected final Class<?> _view;
    protected final ContextAttributes _attributes;
    
    protected MapperConfigBase(final BaseSettings base, final SubtypeResolver str, final Map<ClassKey, Class<?>> mixins) {
        super(base, MapperConfigBase.DEFAULT_MAPPER_FEATURES);
        this._mixInAnnotations = mixins;
        this._subtypeResolver = str;
        this._rootName = null;
        this._view = null;
        this._attributes = ContextAttributes.getEmpty();
    }
    
    protected MapperConfigBase(final MapperConfigBase<CFG, T> src) {
        super(src);
        this._mixInAnnotations = src._mixInAnnotations;
        this._subtypeResolver = src._subtypeResolver;
        this._rootName = src._rootName;
        this._view = src._view;
        this._attributes = src._attributes;
    }
    
    protected MapperConfigBase(final MapperConfigBase<CFG, T> src, final BaseSettings base) {
        super(base, src._mapperFeatures);
        this._mixInAnnotations = src._mixInAnnotations;
        this._subtypeResolver = src._subtypeResolver;
        this._rootName = src._rootName;
        this._view = src._view;
        this._attributes = src._attributes;
    }
    
    protected MapperConfigBase(final MapperConfigBase<CFG, T> src, final int mapperFeatures) {
        super(src._base, mapperFeatures);
        this._mixInAnnotations = src._mixInAnnotations;
        this._subtypeResolver = src._subtypeResolver;
        this._rootName = src._rootName;
        this._view = src._view;
        this._attributes = src._attributes;
    }
    
    protected MapperConfigBase(final MapperConfigBase<CFG, T> src, final SubtypeResolver str) {
        super(src);
        this._mixInAnnotations = src._mixInAnnotations;
        this._subtypeResolver = str;
        this._rootName = src._rootName;
        this._view = src._view;
        this._attributes = src._attributes;
    }
    
    protected MapperConfigBase(final MapperConfigBase<CFG, T> src, final String rootName) {
        super(src);
        this._mixInAnnotations = src._mixInAnnotations;
        this._subtypeResolver = src._subtypeResolver;
        this._rootName = rootName;
        this._view = src._view;
        this._attributes = src._attributes;
    }
    
    protected MapperConfigBase(final MapperConfigBase<CFG, T> src, final Class<?> view) {
        super(src);
        this._mixInAnnotations = src._mixInAnnotations;
        this._subtypeResolver = src._subtypeResolver;
        this._rootName = src._rootName;
        this._view = view;
        this._attributes = src._attributes;
    }
    
    protected MapperConfigBase(final MapperConfigBase<CFG, T> src, final Map<ClassKey, Class<?>> mixins) {
        super(src);
        this._mixInAnnotations = mixins;
        this._subtypeResolver = src._subtypeResolver;
        this._rootName = src._rootName;
        this._view = src._view;
        this._attributes = src._attributes;
    }
    
    protected MapperConfigBase(final MapperConfigBase<CFG, T> src, final ContextAttributes attr) {
        super(src);
        this._mixInAnnotations = src._mixInAnnotations;
        this._subtypeResolver = src._subtypeResolver;
        this._rootName = src._rootName;
        this._view = src._view;
        this._attributes = attr;
    }
    
    public abstract T with(final AnnotationIntrospector p0);
    
    public abstract T withAppendedAnnotationIntrospector(final AnnotationIntrospector p0);
    
    public abstract T withInsertedAnnotationIntrospector(final AnnotationIntrospector p0);
    
    public abstract T with(final ClassIntrospector p0);
    
    public abstract T with(final DateFormat p0);
    
    public abstract T with(final HandlerInstantiator p0);
    
    public abstract T with(final PropertyNamingStrategy p0);
    
    public abstract T withRootName(final String p0);
    
    public abstract T with(final SubtypeResolver p0);
    
    public abstract T with(final TypeFactory p0);
    
    public abstract T with(final TypeResolverBuilder<?> p0);
    
    public abstract T withView(final Class<?> p0);
    
    public abstract T with(final VisibilityChecker<?> p0);
    
    public abstract T withVisibility(final PropertyAccessor p0, final JsonAutoDetect.Visibility p1);
    
    public abstract T with(final Locale p0);
    
    public abstract T with(final TimeZone p0);
    
    public abstract T with(final Base64Variant p0);
    
    public abstract T with(final ContextAttributes p0);
    
    public T withAttributes(final Map<Object, Object> attributes) {
        return this.with(this.getAttributes().withSharedAttributes(attributes));
    }
    
    public T withAttribute(final Object key, final Object value) {
        return this.with(this.getAttributes().withSharedAttribute(key, value));
    }
    
    public T withoutAttribute(final Object key) {
        return this.with(this.getAttributes().withoutSharedAttribute(key));
    }
    
    @Override
    public final SubtypeResolver getSubtypeResolver() {
        return this._subtypeResolver;
    }
    
    public final String getRootName() {
        return this._rootName;
    }
    
    @Override
    public final Class<?> getActiveView() {
        return this._view;
    }
    
    @Override
    public final ContextAttributes getAttributes() {
        return this._attributes;
    }
    
    @Override
    public final Class<?> findMixInClassFor(final Class<?> cls) {
        return (this._mixInAnnotations == null) ? null : this._mixInAnnotations.get(new ClassKey(cls));
    }
    
    public final int mixInCount() {
        return (this._mixInAnnotations == null) ? 0 : this._mixInAnnotations.size();
    }
    
    static {
        DEFAULT_MAPPER_FEATURES = MapperConfig.collectFeatureDefaults(MapperFeature.class);
    }
}
