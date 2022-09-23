// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind;

import org.apache.htrace.shaded.fasterxml.jackson.core.Base64Variant;
import java.util.TimeZone;
import java.util.Locale;
import org.apache.htrace.shaded.fasterxml.jackson.annotation.JsonAutoDetect;
import org.apache.htrace.shaded.fasterxml.jackson.annotation.PropertyAccessor;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.VisibilityChecker;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
import org.apache.htrace.shaded.fasterxml.jackson.databind.type.TypeFactory;
import org.apache.htrace.shaded.fasterxml.jackson.databind.cfg.HandlerInstantiator;
import java.text.DateFormat;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.ClassIntrospector;
import org.apache.htrace.shaded.fasterxml.jackson.databind.cfg.ContextAttributes;
import org.apache.htrace.shaded.fasterxml.jackson.databind.cfg.MapperConfig;
import org.apache.htrace.shaded.fasterxml.jackson.databind.type.ClassKey;
import java.util.Map;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsontype.SubtypeResolver;
import org.apache.htrace.shaded.fasterxml.jackson.databind.cfg.BaseSettings;
import org.apache.htrace.shaded.fasterxml.jackson.databind.ser.FilterProvider;
import org.apache.htrace.shaded.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;
import org.apache.htrace.shaded.fasterxml.jackson.databind.cfg.MapperConfigBase;

public final class SerializationConfig extends MapperConfigBase<SerializationFeature, SerializationConfig> implements Serializable
{
    private static final long serialVersionUID = -1278867172535832879L;
    protected final int _serFeatures;
    protected JsonInclude.Include _serializationInclusion;
    protected final FilterProvider _filterProvider;
    
    public SerializationConfig(final BaseSettings base, final SubtypeResolver str, final Map<ClassKey, Class<?>> mixins) {
        super(base, str, mixins);
        this._serializationInclusion = null;
        this._serFeatures = MapperConfig.collectFeatureDefaults(SerializationFeature.class);
        this._filterProvider = null;
    }
    
    private SerializationConfig(final SerializationConfig src, final SubtypeResolver str) {
        super(src, str);
        this._serializationInclusion = null;
        this._serFeatures = src._serFeatures;
        this._serializationInclusion = src._serializationInclusion;
        this._filterProvider = src._filterProvider;
    }
    
    private SerializationConfig(final SerializationConfig src, final int mapperFeatures, final int serFeatures) {
        super(src, mapperFeatures);
        this._serializationInclusion = null;
        this._serFeatures = serFeatures;
        this._serializationInclusion = src._serializationInclusion;
        this._filterProvider = src._filterProvider;
    }
    
    private SerializationConfig(final SerializationConfig src, final BaseSettings base) {
        super(src, base);
        this._serializationInclusion = null;
        this._serFeatures = src._serFeatures;
        this._serializationInclusion = src._serializationInclusion;
        this._filterProvider = src._filterProvider;
    }
    
    private SerializationConfig(final SerializationConfig src, final FilterProvider filters) {
        super(src);
        this._serializationInclusion = null;
        this._serFeatures = src._serFeatures;
        this._serializationInclusion = src._serializationInclusion;
        this._filterProvider = filters;
    }
    
    private SerializationConfig(final SerializationConfig src, final Class<?> view) {
        super(src, view);
        this._serializationInclusion = null;
        this._serFeatures = src._serFeatures;
        this._serializationInclusion = src._serializationInclusion;
        this._filterProvider = src._filterProvider;
    }
    
    private SerializationConfig(final SerializationConfig src, final JsonInclude.Include incl) {
        super(src);
        this._serializationInclusion = null;
        this._serFeatures = src._serFeatures;
        this._serializationInclusion = incl;
        this._filterProvider = src._filterProvider;
    }
    
    private SerializationConfig(final SerializationConfig src, final String rootName) {
        super(src, rootName);
        this._serializationInclusion = null;
        this._serFeatures = src._serFeatures;
        this._serializationInclusion = src._serializationInclusion;
        this._filterProvider = src._filterProvider;
    }
    
    protected SerializationConfig(final SerializationConfig src, final Map<ClassKey, Class<?>> mixins) {
        super(src, mixins);
        this._serializationInclusion = null;
        this._serFeatures = src._serFeatures;
        this._serializationInclusion = src._serializationInclusion;
        this._filterProvider = src._filterProvider;
    }
    
    protected SerializationConfig(final SerializationConfig src, final ContextAttributes attrs) {
        super(src, attrs);
        this._serializationInclusion = null;
        this._serFeatures = src._serFeatures;
        this._serializationInclusion = src._serializationInclusion;
        this._filterProvider = src._filterProvider;
    }
    
    @Override
    public SerializationConfig with(final MapperFeature... features) {
        int newMapperFlags = this._mapperFeatures;
        for (final MapperFeature f : features) {
            newMapperFlags |= f.getMask();
        }
        return (newMapperFlags == this._mapperFeatures) ? this : new SerializationConfig(this, newMapperFlags, this._serFeatures);
    }
    
    @Override
    public SerializationConfig without(final MapperFeature... features) {
        int newMapperFlags = this._mapperFeatures;
        for (final MapperFeature f : features) {
            newMapperFlags &= ~f.getMask();
        }
        return (newMapperFlags == this._mapperFeatures) ? this : new SerializationConfig(this, newMapperFlags, this._serFeatures);
    }
    
    @Override
    public SerializationConfig with(final MapperFeature feature, final boolean state) {
        int newMapperFlags;
        if (state) {
            newMapperFlags = (this._mapperFeatures | feature.getMask());
        }
        else {
            newMapperFlags = (this._mapperFeatures & ~feature.getMask());
        }
        return (newMapperFlags == this._mapperFeatures) ? this : new SerializationConfig(this, newMapperFlags, this._serFeatures);
    }
    
    @Override
    public SerializationConfig with(final AnnotationIntrospector ai) {
        return this._withBase(this._base.withAnnotationIntrospector(ai));
    }
    
    @Override
    public SerializationConfig withAppendedAnnotationIntrospector(final AnnotationIntrospector ai) {
        return this._withBase(this._base.withAppendedAnnotationIntrospector(ai));
    }
    
    @Override
    public SerializationConfig withInsertedAnnotationIntrospector(final AnnotationIntrospector ai) {
        return this._withBase(this._base.withInsertedAnnotationIntrospector(ai));
    }
    
    @Override
    public SerializationConfig with(final ClassIntrospector ci) {
        return this._withBase(this._base.withClassIntrospector(ci));
    }
    
    @Override
    public SerializationConfig with(final DateFormat df) {
        SerializationConfig cfg = new SerializationConfig(this, this._base.withDateFormat(df));
        if (df == null) {
            cfg = cfg.with(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        }
        else {
            cfg = cfg.without(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        }
        return cfg;
    }
    
    @Override
    public SerializationConfig with(final HandlerInstantiator hi) {
        return this._withBase(this._base.withHandlerInstantiator(hi));
    }
    
    @Override
    public SerializationConfig with(final PropertyNamingStrategy pns) {
        return this._withBase(this._base.withPropertyNamingStrategy(pns));
    }
    
    @Override
    public SerializationConfig withRootName(final String rootName) {
        if (rootName == null) {
            if (this._rootName == null) {
                return this;
            }
        }
        else if (rootName.equals(this._rootName)) {
            return this;
        }
        return new SerializationConfig(this, rootName);
    }
    
    @Override
    public SerializationConfig with(final SubtypeResolver str) {
        return (str == this._subtypeResolver) ? this : new SerializationConfig(this, str);
    }
    
    @Override
    public SerializationConfig with(final TypeFactory tf) {
        return this._withBase(this._base.withTypeFactory(tf));
    }
    
    @Override
    public SerializationConfig with(final TypeResolverBuilder<?> trb) {
        return this._withBase(this._base.withTypeResolverBuilder(trb));
    }
    
    @Override
    public SerializationConfig withView(final Class<?> view) {
        return (this._view == view) ? this : new SerializationConfig(this, view);
    }
    
    @Override
    public SerializationConfig with(final VisibilityChecker<?> vc) {
        return this._withBase(this._base.withVisibilityChecker(vc));
    }
    
    @Override
    public SerializationConfig withVisibility(final PropertyAccessor forMethod, final JsonAutoDetect.Visibility visibility) {
        return this._withBase(this._base.withVisibility(forMethod, visibility));
    }
    
    @Override
    public SerializationConfig with(final Locale l) {
        return this._withBase(this._base.with(l));
    }
    
    @Override
    public SerializationConfig with(final TimeZone tz) {
        return this._withBase(this._base.with(tz));
    }
    
    @Override
    public SerializationConfig with(final Base64Variant base64) {
        return this._withBase(this._base.with(base64));
    }
    
    @Override
    public SerializationConfig with(final ContextAttributes attrs) {
        return (attrs == this._attributes) ? this : new SerializationConfig(this, attrs);
    }
    
    private final SerializationConfig _withBase(final BaseSettings newBase) {
        return (this._base == newBase) ? this : new SerializationConfig(this, newBase);
    }
    
    public SerializationConfig with(final SerializationFeature feature) {
        final int newSerFeatures = this._serFeatures | feature.getMask();
        return (newSerFeatures == this._serFeatures) ? this : new SerializationConfig(this, this._mapperFeatures, newSerFeatures);
    }
    
    public SerializationConfig with(final SerializationFeature first, final SerializationFeature... features) {
        int newSerFeatures = this._serFeatures | first.getMask();
        for (final SerializationFeature f : features) {
            newSerFeatures |= f.getMask();
        }
        return (newSerFeatures == this._serFeatures) ? this : new SerializationConfig(this, this._mapperFeatures, newSerFeatures);
    }
    
    public SerializationConfig withFeatures(final SerializationFeature... features) {
        int newSerFeatures = this._serFeatures;
        for (final SerializationFeature f : features) {
            newSerFeatures |= f.getMask();
        }
        return (newSerFeatures == this._serFeatures) ? this : new SerializationConfig(this, this._mapperFeatures, newSerFeatures);
    }
    
    public SerializationConfig without(final SerializationFeature feature) {
        final int newSerFeatures = this._serFeatures & ~feature.getMask();
        return (newSerFeatures == this._serFeatures) ? this : new SerializationConfig(this, this._mapperFeatures, newSerFeatures);
    }
    
    public SerializationConfig without(final SerializationFeature first, final SerializationFeature... features) {
        int newSerFeatures = this._serFeatures & ~first.getMask();
        for (final SerializationFeature f : features) {
            newSerFeatures &= ~f.getMask();
        }
        return (newSerFeatures == this._serFeatures) ? this : new SerializationConfig(this, this._mapperFeatures, newSerFeatures);
    }
    
    public SerializationConfig withoutFeatures(final SerializationFeature... features) {
        int newSerFeatures = this._serFeatures;
        for (final SerializationFeature f : features) {
            newSerFeatures &= ~f.getMask();
        }
        return (newSerFeatures == this._serFeatures) ? this : new SerializationConfig(this, this._mapperFeatures, newSerFeatures);
    }
    
    public SerializationConfig withFilters(final FilterProvider filterProvider) {
        return (filterProvider == this._filterProvider) ? this : new SerializationConfig(this, filterProvider);
    }
    
    public SerializationConfig withSerializationInclusion(final JsonInclude.Include incl) {
        return (this._serializationInclusion == incl) ? this : new SerializationConfig(this, incl);
    }
    
    @Override
    public boolean useRootWrapping() {
        if (this._rootName != null) {
            return this._rootName.length() > 0;
        }
        return this.isEnabled(SerializationFeature.WRAP_ROOT_VALUE);
    }
    
    @Override
    public AnnotationIntrospector getAnnotationIntrospector() {
        if (this.isEnabled(MapperFeature.USE_ANNOTATIONS)) {
            return super.getAnnotationIntrospector();
        }
        return AnnotationIntrospector.nopInstance();
    }
    
    @Override
    public BeanDescription introspectClassAnnotations(final JavaType type) {
        return this.getClassIntrospector().forClassAnnotations(this, type, this);
    }
    
    @Override
    public BeanDescription introspectDirectClassAnnotations(final JavaType type) {
        return this.getClassIntrospector().forDirectClassAnnotations(this, type, this);
    }
    
    @Override
    public VisibilityChecker<?> getDefaultVisibilityChecker() {
        VisibilityChecker<?> vchecker = super.getDefaultVisibilityChecker();
        if (!this.isEnabled(MapperFeature.AUTO_DETECT_GETTERS)) {
            vchecker = (VisibilityChecker<?>)vchecker.withGetterVisibility(JsonAutoDetect.Visibility.NONE);
        }
        if (!this.isEnabled(MapperFeature.AUTO_DETECT_IS_GETTERS)) {
            vchecker = (VisibilityChecker<?>)vchecker.withIsGetterVisibility(JsonAutoDetect.Visibility.NONE);
        }
        if (!this.isEnabled(MapperFeature.AUTO_DETECT_FIELDS)) {
            vchecker = (VisibilityChecker<?>)vchecker.withFieldVisibility(JsonAutoDetect.Visibility.NONE);
        }
        return vchecker;
    }
    
    public final boolean isEnabled(final SerializationFeature f) {
        return (this._serFeatures & f.getMask()) != 0x0;
    }
    
    public final boolean hasSerializationFeatures(final int featureMask) {
        return (this._serFeatures & featureMask) == featureMask;
    }
    
    public final int getSerializationFeatures() {
        return this._serFeatures;
    }
    
    public JsonInclude.Include getSerializationInclusion() {
        if (this._serializationInclusion != null) {
            return this._serializationInclusion;
        }
        return JsonInclude.Include.ALWAYS;
    }
    
    public FilterProvider getFilterProvider() {
        return this._filterProvider;
    }
    
    public <T extends BeanDescription> T introspect(final JavaType type) {
        return (T)this.getClassIntrospector().forSerialization(this, type, this);
    }
    
    @Override
    public String toString() {
        return "[SerializationConfig: flags=0x" + Integer.toHexString(this._serFeatures) + "]";
    }
}
