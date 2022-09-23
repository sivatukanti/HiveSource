// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind;

import org.apache.htrace.shaded.fasterxml.jackson.databind.jsontype.NamedType;
import java.util.Collection;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.AnnotatedClass;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.NopAnnotationIntrospector;
import org.apache.htrace.shaded.fasterxml.jackson.core.Base64Variant;
import java.util.TimeZone;
import java.util.Locale;
import org.apache.htrace.shaded.fasterxml.jackson.databind.cfg.HandlerInstantiator;
import java.text.DateFormat;
import org.apache.htrace.shaded.fasterxml.jackson.databind.type.TypeFactory;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
import org.apache.htrace.shaded.fasterxml.jackson.annotation.JsonAutoDetect;
import org.apache.htrace.shaded.fasterxml.jackson.annotation.PropertyAccessor;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.VisibilityChecker;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.ClassIntrospector;
import org.apache.htrace.shaded.fasterxml.jackson.databind.cfg.ContextAttributes;
import org.apache.htrace.shaded.fasterxml.jackson.databind.cfg.MapperConfig;
import org.apache.htrace.shaded.fasterxml.jackson.databind.type.ClassKey;
import java.util.Map;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsontype.SubtypeResolver;
import org.apache.htrace.shaded.fasterxml.jackson.databind.cfg.BaseSettings;
import org.apache.htrace.shaded.fasterxml.jackson.databind.node.JsonNodeFactory;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
import org.apache.htrace.shaded.fasterxml.jackson.databind.util.LinkedNode;
import java.io.Serializable;
import org.apache.htrace.shaded.fasterxml.jackson.databind.cfg.MapperConfigBase;

public final class DeserializationConfig extends MapperConfigBase<DeserializationFeature, DeserializationConfig> implements Serializable
{
    private static final long serialVersionUID = -4227480407273773599L;
    protected final int _deserFeatures;
    protected final LinkedNode<DeserializationProblemHandler> _problemHandlers;
    protected final JsonNodeFactory _nodeFactory;
    
    public DeserializationConfig(final BaseSettings base, final SubtypeResolver str, final Map<ClassKey, Class<?>> mixins) {
        super(base, str, mixins);
        this._deserFeatures = MapperConfig.collectFeatureDefaults(DeserializationFeature.class);
        this._nodeFactory = JsonNodeFactory.instance;
        this._problemHandlers = null;
    }
    
    private DeserializationConfig(final DeserializationConfig src, final SubtypeResolver str) {
        super(src, str);
        this._deserFeatures = src._deserFeatures;
        this._nodeFactory = src._nodeFactory;
        this._problemHandlers = src._problemHandlers;
    }
    
    private DeserializationConfig(final DeserializationConfig src, final int mapperFeatures, final int deserFeatures) {
        super(src, mapperFeatures);
        this._deserFeatures = deserFeatures;
        this._nodeFactory = src._nodeFactory;
        this._problemHandlers = src._problemHandlers;
    }
    
    private DeserializationConfig(final DeserializationConfig src, final BaseSettings base) {
        super(src, base);
        this._deserFeatures = src._deserFeatures;
        this._nodeFactory = src._nodeFactory;
        this._problemHandlers = src._problemHandlers;
    }
    
    private DeserializationConfig(final DeserializationConfig src, final JsonNodeFactory f) {
        super(src);
        this._deserFeatures = src._deserFeatures;
        this._problemHandlers = src._problemHandlers;
        this._nodeFactory = f;
    }
    
    private DeserializationConfig(final DeserializationConfig src, final LinkedNode<DeserializationProblemHandler> problemHandlers) {
        super(src);
        this._deserFeatures = src._deserFeatures;
        this._problemHandlers = problemHandlers;
        this._nodeFactory = src._nodeFactory;
    }
    
    private DeserializationConfig(final DeserializationConfig src, final String rootName) {
        super(src, rootName);
        this._deserFeatures = src._deserFeatures;
        this._problemHandlers = src._problemHandlers;
        this._nodeFactory = src._nodeFactory;
    }
    
    private DeserializationConfig(final DeserializationConfig src, final Class<?> view) {
        super(src, view);
        this._deserFeatures = src._deserFeatures;
        this._problemHandlers = src._problemHandlers;
        this._nodeFactory = src._nodeFactory;
    }
    
    protected DeserializationConfig(final DeserializationConfig src, final Map<ClassKey, Class<?>> mixins) {
        super(src, mixins);
        this._deserFeatures = src._deserFeatures;
        this._problemHandlers = src._problemHandlers;
        this._nodeFactory = src._nodeFactory;
    }
    
    protected DeserializationConfig(final DeserializationConfig src, final ContextAttributes attrs) {
        super(src, attrs);
        this._deserFeatures = src._deserFeatures;
        this._problemHandlers = src._problemHandlers;
        this._nodeFactory = src._nodeFactory;
    }
    
    protected BaseSettings getBaseSettings() {
        return this._base;
    }
    
    @Override
    public DeserializationConfig with(final MapperFeature... features) {
        int newMapperFlags = this._mapperFeatures;
        for (final MapperFeature f : features) {
            newMapperFlags |= f.getMask();
        }
        return (newMapperFlags == this._mapperFeatures) ? this : new DeserializationConfig(this, newMapperFlags, this._deserFeatures);
    }
    
    @Override
    public DeserializationConfig without(final MapperFeature... features) {
        int newMapperFlags = this._mapperFeatures;
        for (final MapperFeature f : features) {
            newMapperFlags &= ~f.getMask();
        }
        return (newMapperFlags == this._mapperFeatures) ? this : new DeserializationConfig(this, newMapperFlags, this._deserFeatures);
    }
    
    @Override
    public DeserializationConfig with(final MapperFeature feature, final boolean state) {
        int newMapperFlags;
        if (state) {
            newMapperFlags = (this._mapperFeatures | feature.getMask());
        }
        else {
            newMapperFlags = (this._mapperFeatures & ~feature.getMask());
        }
        return (newMapperFlags == this._mapperFeatures) ? this : new DeserializationConfig(this, newMapperFlags, this._deserFeatures);
    }
    
    @Override
    public DeserializationConfig with(final ClassIntrospector ci) {
        return this._withBase(this._base.withClassIntrospector(ci));
    }
    
    @Override
    public DeserializationConfig with(final AnnotationIntrospector ai) {
        return this._withBase(this._base.withAnnotationIntrospector(ai));
    }
    
    @Override
    public DeserializationConfig with(final VisibilityChecker<?> vc) {
        return this._withBase(this._base.withVisibilityChecker(vc));
    }
    
    @Override
    public DeserializationConfig withVisibility(final PropertyAccessor forMethod, final JsonAutoDetect.Visibility visibility) {
        return this._withBase(this._base.withVisibility(forMethod, visibility));
    }
    
    @Override
    public DeserializationConfig with(final TypeResolverBuilder<?> trb) {
        return this._withBase(this._base.withTypeResolverBuilder(trb));
    }
    
    @Override
    public DeserializationConfig with(final SubtypeResolver str) {
        return (this._subtypeResolver == str) ? this : new DeserializationConfig(this, str);
    }
    
    @Override
    public DeserializationConfig with(final PropertyNamingStrategy pns) {
        return this._withBase(this._base.withPropertyNamingStrategy(pns));
    }
    
    @Override
    public DeserializationConfig withRootName(final String rootName) {
        if (rootName == null) {
            if (this._rootName == null) {
                return this;
            }
        }
        else if (rootName.equals(this._rootName)) {
            return this;
        }
        return new DeserializationConfig(this, rootName);
    }
    
    @Override
    public DeserializationConfig with(final TypeFactory tf) {
        return this._withBase(this._base.withTypeFactory(tf));
    }
    
    @Override
    public DeserializationConfig with(final DateFormat df) {
        return this._withBase(this._base.withDateFormat(df));
    }
    
    @Override
    public DeserializationConfig with(final HandlerInstantiator hi) {
        return this._withBase(this._base.withHandlerInstantiator(hi));
    }
    
    @Override
    public DeserializationConfig withInsertedAnnotationIntrospector(final AnnotationIntrospector ai) {
        return this._withBase(this._base.withInsertedAnnotationIntrospector(ai));
    }
    
    @Override
    public DeserializationConfig withAppendedAnnotationIntrospector(final AnnotationIntrospector ai) {
        return this._withBase(this._base.withAppendedAnnotationIntrospector(ai));
    }
    
    @Override
    public DeserializationConfig withView(final Class<?> view) {
        return (this._view == view) ? this : new DeserializationConfig(this, view);
    }
    
    @Override
    public DeserializationConfig with(final Locale l) {
        return this._withBase(this._base.with(l));
    }
    
    @Override
    public DeserializationConfig with(final TimeZone tz) {
        return this._withBase(this._base.with(tz));
    }
    
    @Override
    public DeserializationConfig with(final Base64Variant base64) {
        return this._withBase(this._base.with(base64));
    }
    
    @Override
    public DeserializationConfig with(final ContextAttributes attrs) {
        return (attrs == this._attributes) ? this : new DeserializationConfig(this, attrs);
    }
    
    private final DeserializationConfig _withBase(final BaseSettings newBase) {
        return (this._base == newBase) ? this : new DeserializationConfig(this, newBase);
    }
    
    public DeserializationConfig with(final JsonNodeFactory f) {
        if (this._nodeFactory == f) {
            return this;
        }
        return new DeserializationConfig(this, f);
    }
    
    public DeserializationConfig withHandler(final DeserializationProblemHandler h) {
        if (LinkedNode.contains(this._problemHandlers, h)) {
            return this;
        }
        return new DeserializationConfig(this, new LinkedNode<DeserializationProblemHandler>(h, this._problemHandlers));
    }
    
    public DeserializationConfig withNoProblemHandlers() {
        if (this._problemHandlers == null) {
            return this;
        }
        return new DeserializationConfig(this, (LinkedNode<DeserializationProblemHandler>)null);
    }
    
    public DeserializationConfig with(final DeserializationFeature feature) {
        final int newDeserFeatures = this._deserFeatures | feature.getMask();
        return (newDeserFeatures == this._deserFeatures) ? this : new DeserializationConfig(this, this._mapperFeatures, newDeserFeatures);
    }
    
    public DeserializationConfig with(final DeserializationFeature first, final DeserializationFeature... features) {
        int newDeserFeatures = this._deserFeatures | first.getMask();
        for (final DeserializationFeature f : features) {
            newDeserFeatures |= f.getMask();
        }
        return (newDeserFeatures == this._deserFeatures) ? this : new DeserializationConfig(this, this._mapperFeatures, newDeserFeatures);
    }
    
    public DeserializationConfig withFeatures(final DeserializationFeature... features) {
        int newDeserFeatures = this._deserFeatures;
        for (final DeserializationFeature f : features) {
            newDeserFeatures |= f.getMask();
        }
        return (newDeserFeatures == this._deserFeatures) ? this : new DeserializationConfig(this, this._mapperFeatures, newDeserFeatures);
    }
    
    public DeserializationConfig without(final DeserializationFeature feature) {
        final int newDeserFeatures = this._deserFeatures & ~feature.getMask();
        return (newDeserFeatures == this._deserFeatures) ? this : new DeserializationConfig(this, this._mapperFeatures, newDeserFeatures);
    }
    
    public DeserializationConfig without(final DeserializationFeature first, final DeserializationFeature... features) {
        int newDeserFeatures = this._deserFeatures & ~first.getMask();
        for (final DeserializationFeature f : features) {
            newDeserFeatures &= ~f.getMask();
        }
        return (newDeserFeatures == this._deserFeatures) ? this : new DeserializationConfig(this, this._mapperFeatures, newDeserFeatures);
    }
    
    public DeserializationConfig withoutFeatures(final DeserializationFeature... features) {
        int newDeserFeatures = this._deserFeatures;
        for (final DeserializationFeature f : features) {
            newDeserFeatures &= ~f.getMask();
        }
        return (newDeserFeatures == this._deserFeatures) ? this : new DeserializationConfig(this, this._mapperFeatures, newDeserFeatures);
    }
    
    @Override
    public AnnotationIntrospector getAnnotationIntrospector() {
        if (this.isEnabled(MapperFeature.USE_ANNOTATIONS)) {
            return super.getAnnotationIntrospector();
        }
        return NopAnnotationIntrospector.instance;
    }
    
    @Override
    public boolean useRootWrapping() {
        if (this._rootName != null) {
            return this._rootName.length() > 0;
        }
        return this.isEnabled(DeserializationFeature.UNWRAP_ROOT_VALUE);
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
        if (!this.isEnabled(MapperFeature.AUTO_DETECT_SETTERS)) {
            vchecker = (VisibilityChecker<?>)vchecker.withSetterVisibility(JsonAutoDetect.Visibility.NONE);
        }
        if (!this.isEnabled(MapperFeature.AUTO_DETECT_CREATORS)) {
            vchecker = (VisibilityChecker<?>)vchecker.withCreatorVisibility(JsonAutoDetect.Visibility.NONE);
        }
        if (!this.isEnabled(MapperFeature.AUTO_DETECT_FIELDS)) {
            vchecker = (VisibilityChecker<?>)vchecker.withFieldVisibility(JsonAutoDetect.Visibility.NONE);
        }
        return vchecker;
    }
    
    public final boolean isEnabled(final DeserializationFeature f) {
        return (this._deserFeatures & f.getMask()) != 0x0;
    }
    
    public final boolean hasDeserializationFeatures(final int featureMask) {
        return (this._deserFeatures & featureMask) == featureMask;
    }
    
    public final int getDeserializationFeatures() {
        return this._deserFeatures;
    }
    
    public LinkedNode<DeserializationProblemHandler> getProblemHandlers() {
        return this._problemHandlers;
    }
    
    public final JsonNodeFactory getNodeFactory() {
        return this._nodeFactory;
    }
    
    public <T extends BeanDescription> T introspect(final JavaType type) {
        return (T)this.getClassIntrospector().forDeserialization(this, type, this);
    }
    
    public <T extends BeanDescription> T introspectForCreation(final JavaType type) {
        return (T)this.getClassIntrospector().forCreation(this, type, this);
    }
    
    public <T extends BeanDescription> T introspectForBuilder(final JavaType type) {
        return (T)this.getClassIntrospector().forDeserializationWithBuilder(this, type, this);
    }
    
    public TypeDeserializer findTypeDeserializer(final JavaType baseType) throws JsonMappingException {
        final BeanDescription bean = this.introspectClassAnnotations(baseType.getRawClass());
        final AnnotatedClass ac = bean.getClassInfo();
        TypeResolverBuilder<?> b = this.getAnnotationIntrospector().findTypeResolver(this, ac, baseType);
        Collection<NamedType> subtypes = null;
        if (b == null) {
            b = this.getDefaultTyper(baseType);
            if (b == null) {
                return null;
            }
        }
        else {
            subtypes = this.getSubtypeResolver().collectAndResolveSubtypes(ac, this, this.getAnnotationIntrospector());
        }
        return b.buildTypeDeserializer(this, baseType, subtypes);
    }
}
