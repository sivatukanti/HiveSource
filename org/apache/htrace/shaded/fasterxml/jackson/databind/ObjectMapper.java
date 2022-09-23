// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind;

import org.apache.htrace.shaded.fasterxml.jackson.databind.jsontype.TypeSerializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import java.util.Collection;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsontype.impl.StdTypeResolverBuilder;
import org.apache.htrace.shaded.fasterxml.jackson.core.Base64Variants;
import org.apache.htrace.shaded.fasterxml.jackson.databind.util.StdDateFormat;
import org.apache.htrace.shaded.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.BasicClassIntrospector;
import org.apache.htrace.shaded.fasterxml.jackson.databind.type.SimpleType;
import org.apache.htrace.shaded.fasterxml.jackson.databind.cfg.MapperConfig;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsonschema.JsonSchema;
import org.apache.htrace.shaded.fasterxml.jackson.databind.cfg.ContextAttributes;
import org.apache.htrace.shaded.fasterxml.jackson.core.io.CharacterEscapes;
import org.apache.htrace.shaded.fasterxml.jackson.core.FormatSchema;
import org.apache.htrace.shaded.fasterxml.jackson.core.util.ByteArrayBuilder;
import org.apache.htrace.shaded.fasterxml.jackson.core.io.SegmentedStringWriter;
import java.io.Writer;
import java.io.OutputStream;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonEncoding;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.htrace.shaded.fasterxml.jackson.databind.util.TokenBuffer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.node.TreeTraversingParser;
import org.apache.htrace.shaded.fasterxml.jackson.databind.node.ArrayNode;
import org.apache.htrace.shaded.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonGenerationException;
import java.io.Closeable;
import java.net.URL;
import java.io.File;
import java.io.Reader;
import org.apache.htrace.shaded.fasterxml.jackson.databind.node.NullNode;
import java.io.InputStream;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonProcessingException;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonToken;
import org.apache.htrace.shaded.fasterxml.jackson.core.TreeNode;
import org.apache.htrace.shaded.fasterxml.jackson.core.type.ResolvedType;
import org.apache.htrace.shaded.fasterxml.jackson.core.type.TypeReference;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonParseException;
import java.io.IOException;
import java.util.TimeZone;
import java.util.Locale;
import org.apache.htrace.shaded.fasterxml.jackson.databind.cfg.HandlerInstantiator;
import java.text.DateFormat;
import org.apache.htrace.shaded.fasterxml.jackson.core.Base64Variant;
import org.apache.htrace.shaded.fasterxml.jackson.databind.ser.FilterProvider;
import org.apache.htrace.shaded.fasterxml.jackson.databind.node.JsonNodeFactory;
import java.lang.reflect.Type;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import org.apache.htrace.shaded.fasterxml.jackson.annotation.JsonTypeInfo;
import org.apache.htrace.shaded.fasterxml.jackson.annotation.JsonInclude;
import org.apache.htrace.shaded.fasterxml.jackson.annotation.JsonAutoDetect;
import org.apache.htrace.shaded.fasterxml.jackson.annotation.PropertyAccessor;
import java.util.ServiceLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsontype.NamedType;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.ValueInstantiators;
import org.apache.htrace.shaded.fasterxml.jackson.databind.type.TypeModifier;
import org.apache.htrace.shaded.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import org.apache.htrace.shaded.fasterxml.jackson.databind.ser.Serializers;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.KeyDeserializers;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.Deserializers;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonGenerator;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonParser;
import org.apache.htrace.shaded.fasterxml.jackson.databind.cfg.PackageVersion;
import org.apache.htrace.shaded.fasterxml.jackson.core.Version;
import org.apache.htrace.shaded.fasterxml.jackson.databind.ser.BeanSerializerFactory;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.DeserializerFactory;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.BeanDeserializerFactory;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsontype.impl.StdSubtypeResolver;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.DefaultDeserializationContext;
import org.apache.htrace.shaded.fasterxml.jackson.databind.ser.SerializerFactory;
import org.apache.htrace.shaded.fasterxml.jackson.databind.ser.DefaultSerializerProvider;
import org.apache.htrace.shaded.fasterxml.jackson.databind.type.ClassKey;
import java.util.HashMap;
import org.apache.htrace.shaded.fasterxml.jackson.databind.util.RootNameLookup;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsontype.SubtypeResolver;
import org.apache.htrace.shaded.fasterxml.jackson.databind.type.TypeFactory;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonFactory;
import org.apache.htrace.shaded.fasterxml.jackson.databind.cfg.BaseSettings;
import org.apache.htrace.shaded.fasterxml.jackson.core.PrettyPrinter;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.VisibilityChecker;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.ClassIntrospector;
import java.io.Serializable;
import org.apache.htrace.shaded.fasterxml.jackson.core.Versioned;
import org.apache.htrace.shaded.fasterxml.jackson.core.ObjectCodec;

public class ObjectMapper extends ObjectCodec implements Versioned, Serializable
{
    private static final long serialVersionUID = 1L;
    private static final JavaType JSON_NODE_TYPE;
    protected static final ClassIntrospector DEFAULT_INTROSPECTOR;
    protected static final AnnotationIntrospector DEFAULT_ANNOTATION_INTROSPECTOR;
    protected static final VisibilityChecker<?> STD_VISIBILITY_CHECKER;
    protected static final PrettyPrinter _defaultPrettyPrinter;
    protected static final BaseSettings DEFAULT_BASE;
    protected final JsonFactory _jsonFactory;
    protected TypeFactory _typeFactory;
    protected InjectableValues _injectableValues;
    protected SubtypeResolver _subtypeResolver;
    protected final RootNameLookup _rootNames;
    protected final HashMap<ClassKey, Class<?>> _mixInAnnotations;
    protected SerializationConfig _serializationConfig;
    protected DefaultSerializerProvider _serializerProvider;
    protected SerializerFactory _serializerFactory;
    protected DeserializationConfig _deserializationConfig;
    protected DefaultDeserializationContext _deserializationContext;
    protected final ConcurrentHashMap<JavaType, JsonDeserializer<Object>> _rootDeserializers;
    
    public ObjectMapper() {
        this(null, null, null);
    }
    
    public ObjectMapper(final JsonFactory jf) {
        this(jf, null, null);
    }
    
    protected ObjectMapper(final ObjectMapper src) {
        this._rootDeserializers = new ConcurrentHashMap<JavaType, JsonDeserializer<Object>>(64, 0.6f, 2);
        (this._jsonFactory = src._jsonFactory.copy()).setCodec(this);
        this._subtypeResolver = src._subtypeResolver;
        this._rootNames = new RootNameLookup();
        this._typeFactory = src._typeFactory;
        this._serializationConfig = src._serializationConfig;
        final HashMap<ClassKey, Class<?>> mixins = new HashMap<ClassKey, Class<?>>(src._mixInAnnotations);
        this._mixInAnnotations = mixins;
        this._serializationConfig = new SerializationConfig(src._serializationConfig, mixins);
        this._deserializationConfig = new DeserializationConfig(src._deserializationConfig, mixins);
        this._serializerProvider = src._serializerProvider;
        this._deserializationContext = src._deserializationContext;
        this._serializerFactory = src._serializerFactory;
    }
    
    public ObjectMapper(final JsonFactory jf, final DefaultSerializerProvider sp, final DefaultDeserializationContext dc) {
        this._rootDeserializers = new ConcurrentHashMap<JavaType, JsonDeserializer<Object>>(64, 0.6f, 2);
        if (jf == null) {
            this._jsonFactory = new MappingJsonFactory(this);
        }
        else {
            this._jsonFactory = jf;
            if (jf.getCodec() == null) {
                this._jsonFactory.setCodec(this);
            }
        }
        this._subtypeResolver = new StdSubtypeResolver();
        this._rootNames = new RootNameLookup();
        this._typeFactory = TypeFactory.defaultInstance();
        final HashMap<ClassKey, Class<?>> mixins = new HashMap<ClassKey, Class<?>>();
        this._mixInAnnotations = mixins;
        this._serializationConfig = new SerializationConfig(ObjectMapper.DEFAULT_BASE, this._subtypeResolver, mixins);
        this._deserializationConfig = new DeserializationConfig(ObjectMapper.DEFAULT_BASE, this._subtypeResolver, mixins);
        final boolean needOrder = this._jsonFactory.requiresPropertyOrdering();
        if (needOrder ^ this._serializationConfig.isEnabled(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY)) {
            this.configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, needOrder);
        }
        this._serializerProvider = ((sp == null) ? new DefaultSerializerProvider.Impl() : sp);
        this._deserializationContext = ((dc == null) ? new DefaultDeserializationContext.Impl(BeanDeserializerFactory.instance) : dc);
        this._serializerFactory = BeanSerializerFactory.instance;
    }
    
    public ObjectMapper copy() {
        this._checkInvalidCopy(ObjectMapper.class);
        return new ObjectMapper(this);
    }
    
    protected void _checkInvalidCopy(final Class<?> exp) {
        if (this.getClass() != exp) {
            throw new IllegalStateException("Failed copy(): " + this.getClass().getName() + " (version: " + this.version() + ") does not override copy(); it has to");
        }
    }
    
    @Override
    public Version version() {
        return PackageVersion.VERSION;
    }
    
    public ObjectMapper registerModule(final Module module) {
        final String name = module.getModuleName();
        if (name == null) {
            throw new IllegalArgumentException("Module without defined name");
        }
        final Version version = module.version();
        if (version == null) {
            throw new IllegalArgumentException("Module without defined version");
        }
        final ObjectMapper mapper = this;
        module.setupModule(new Module.SetupContext() {
            @Override
            public Version getMapperVersion() {
                return ObjectMapper.this.version();
            }
            
            @Override
            public <C extends ObjectCodec> C getOwner() {
                return (C)mapper;
            }
            
            @Override
            public TypeFactory getTypeFactory() {
                return ObjectMapper.this._typeFactory;
            }
            
            @Override
            public boolean isEnabled(final MapperFeature f) {
                return mapper.isEnabled(f);
            }
            
            @Override
            public boolean isEnabled(final DeserializationFeature f) {
                return mapper.isEnabled(f);
            }
            
            @Override
            public boolean isEnabled(final SerializationFeature f) {
                return mapper.isEnabled(f);
            }
            
            @Override
            public boolean isEnabled(final JsonFactory.Feature f) {
                return mapper.isEnabled(f);
            }
            
            @Override
            public boolean isEnabled(final JsonParser.Feature f) {
                return mapper.isEnabled(f);
            }
            
            @Override
            public boolean isEnabled(final JsonGenerator.Feature f) {
                return mapper.isEnabled(f);
            }
            
            @Override
            public void addDeserializers(final Deserializers d) {
                final DeserializerFactory df = mapper._deserializationContext._factory.withAdditionalDeserializers(d);
                mapper._deserializationContext = mapper._deserializationContext.with(df);
            }
            
            @Override
            public void addKeyDeserializers(final KeyDeserializers d) {
                final DeserializerFactory df = mapper._deserializationContext._factory.withAdditionalKeyDeserializers(d);
                mapper._deserializationContext = mapper._deserializationContext.with(df);
            }
            
            @Override
            public void addBeanDeserializerModifier(final BeanDeserializerModifier modifier) {
                final DeserializerFactory df = mapper._deserializationContext._factory.withDeserializerModifier(modifier);
                mapper._deserializationContext = mapper._deserializationContext.with(df);
            }
            
            @Override
            public void addSerializers(final Serializers s) {
                mapper._serializerFactory = mapper._serializerFactory.withAdditionalSerializers(s);
            }
            
            @Override
            public void addKeySerializers(final Serializers s) {
                mapper._serializerFactory = mapper._serializerFactory.withAdditionalKeySerializers(s);
            }
            
            @Override
            public void addBeanSerializerModifier(final BeanSerializerModifier modifier) {
                mapper._serializerFactory = mapper._serializerFactory.withSerializerModifier(modifier);
            }
            
            @Override
            public void addAbstractTypeResolver(final AbstractTypeResolver resolver) {
                final DeserializerFactory df = mapper._deserializationContext._factory.withAbstractTypeResolver(resolver);
                mapper._deserializationContext = mapper._deserializationContext.with(df);
            }
            
            @Override
            public void addTypeModifier(final TypeModifier modifier) {
                TypeFactory f = mapper._typeFactory;
                f = f.withModifier(modifier);
                mapper.setTypeFactory(f);
            }
            
            @Override
            public void addValueInstantiators(final ValueInstantiators instantiators) {
                final DeserializerFactory df = mapper._deserializationContext._factory.withValueInstantiators(instantiators);
                mapper._deserializationContext = mapper._deserializationContext.with(df);
            }
            
            @Override
            public void setClassIntrospector(final ClassIntrospector ci) {
                mapper._deserializationConfig = mapper._deserializationConfig.with(ci);
                mapper._serializationConfig = mapper._serializationConfig.with(ci);
            }
            
            @Override
            public void insertAnnotationIntrospector(final AnnotationIntrospector ai) {
                mapper._deserializationConfig = mapper._deserializationConfig.withInsertedAnnotationIntrospector(ai);
                mapper._serializationConfig = mapper._serializationConfig.withInsertedAnnotationIntrospector(ai);
            }
            
            @Override
            public void appendAnnotationIntrospector(final AnnotationIntrospector ai) {
                mapper._deserializationConfig = mapper._deserializationConfig.withAppendedAnnotationIntrospector(ai);
                mapper._serializationConfig = mapper._serializationConfig.withAppendedAnnotationIntrospector(ai);
            }
            
            @Override
            public void registerSubtypes(final Class<?>... subtypes) {
                mapper.registerSubtypes(subtypes);
            }
            
            @Override
            public void registerSubtypes(final NamedType... subtypes) {
                mapper.registerSubtypes(subtypes);
            }
            
            @Override
            public void setMixInAnnotations(final Class<?> target, final Class<?> mixinSource) {
                mapper.addMixInAnnotations(target, mixinSource);
            }
            
            @Override
            public void addDeserializationProblemHandler(final DeserializationProblemHandler handler) {
                mapper.addHandler(handler);
            }
            
            @Override
            public void setNamingStrategy(final PropertyNamingStrategy naming) {
                mapper.setPropertyNamingStrategy(naming);
            }
        });
        return this;
    }
    
    public ObjectMapper registerModules(final Module... modules) {
        for (final Module module : modules) {
            this.registerModule(module);
        }
        return this;
    }
    
    public ObjectMapper registerModules(final Iterable<Module> modules) {
        for (final Module module : modules) {
            this.registerModule(module);
        }
        return this;
    }
    
    public static List<Module> findModules() {
        return findModules(null);
    }
    
    public static List<Module> findModules(final ClassLoader classLoader) {
        final ArrayList<Module> modules = new ArrayList<Module>();
        final ServiceLoader<Module> loader = (classLoader == null) ? ServiceLoader.load(Module.class) : ServiceLoader.load(Module.class, classLoader);
        for (final Module module : loader) {
            modules.add(module);
        }
        return modules;
    }
    
    public ObjectMapper findAndRegisterModules() {
        return this.registerModules(findModules());
    }
    
    public SerializationConfig getSerializationConfig() {
        return this._serializationConfig;
    }
    
    public DeserializationConfig getDeserializationConfig() {
        return this._deserializationConfig;
    }
    
    public DeserializationContext getDeserializationContext() {
        return this._deserializationContext;
    }
    
    public ObjectMapper setSerializerFactory(final SerializerFactory f) {
        this._serializerFactory = f;
        return this;
    }
    
    public SerializerFactory getSerializerFactory() {
        return this._serializerFactory;
    }
    
    public ObjectMapper setSerializerProvider(final DefaultSerializerProvider p) {
        this._serializerProvider = p;
        return this;
    }
    
    public SerializerProvider getSerializerProvider() {
        return this._serializerProvider;
    }
    
    public final void setMixInAnnotations(final Map<Class<?>, Class<?>> sourceMixins) {
        this._mixInAnnotations.clear();
        if (sourceMixins != null && sourceMixins.size() > 0) {
            for (final Map.Entry<Class<?>, Class<?>> en : sourceMixins.entrySet()) {
                this._mixInAnnotations.put(new ClassKey(en.getKey()), en.getValue());
            }
        }
    }
    
    public final void addMixInAnnotations(final Class<?> target, final Class<?> mixinSource) {
        this._mixInAnnotations.put(new ClassKey(target), mixinSource);
    }
    
    public final Class<?> findMixInClassFor(final Class<?> cls) {
        return (this._mixInAnnotations == null) ? null : this._mixInAnnotations.get(new ClassKey(cls));
    }
    
    public final int mixInCount() {
        return (this._mixInAnnotations == null) ? 0 : this._mixInAnnotations.size();
    }
    
    public VisibilityChecker<?> getVisibilityChecker() {
        return this._serializationConfig.getDefaultVisibilityChecker();
    }
    
    public void setVisibilityChecker(final VisibilityChecker<?> vc) {
        this._deserializationConfig = this._deserializationConfig.with(vc);
        this._serializationConfig = this._serializationConfig.with(vc);
    }
    
    public ObjectMapper setVisibility(final PropertyAccessor forMethod, final JsonAutoDetect.Visibility visibility) {
        this._deserializationConfig = this._deserializationConfig.withVisibility(forMethod, visibility);
        this._serializationConfig = this._serializationConfig.withVisibility(forMethod, visibility);
        return this;
    }
    
    public SubtypeResolver getSubtypeResolver() {
        return this._subtypeResolver;
    }
    
    public ObjectMapper setSubtypeResolver(final SubtypeResolver str) {
        this._subtypeResolver = str;
        this._deserializationConfig = this._deserializationConfig.with(str);
        this._serializationConfig = this._serializationConfig.with(str);
        return this;
    }
    
    public ObjectMapper setAnnotationIntrospector(final AnnotationIntrospector ai) {
        this._serializationConfig = this._serializationConfig.with(ai);
        this._deserializationConfig = this._deserializationConfig.with(ai);
        return this;
    }
    
    public ObjectMapper setAnnotationIntrospectors(final AnnotationIntrospector serializerAI, final AnnotationIntrospector deserializerAI) {
        this._serializationConfig = this._serializationConfig.with(serializerAI);
        this._deserializationConfig = this._deserializationConfig.with(deserializerAI);
        return this;
    }
    
    public ObjectMapper setPropertyNamingStrategy(final PropertyNamingStrategy s) {
        this._serializationConfig = this._serializationConfig.with(s);
        this._deserializationConfig = this._deserializationConfig.with(s);
        return this;
    }
    
    public ObjectMapper setSerializationInclusion(final JsonInclude.Include incl) {
        this._serializationConfig = this._serializationConfig.withSerializationInclusion(incl);
        return this;
    }
    
    public ObjectMapper enableDefaultTyping() {
        return this.enableDefaultTyping(DefaultTyping.OBJECT_AND_NON_CONCRETE);
    }
    
    public ObjectMapper enableDefaultTyping(final DefaultTyping dti) {
        return this.enableDefaultTyping(dti, JsonTypeInfo.As.WRAPPER_ARRAY);
    }
    
    public ObjectMapper enableDefaultTyping(final DefaultTyping applicability, final JsonTypeInfo.As includeAs) {
        TypeResolverBuilder<?> typer = new DefaultTypeResolverBuilder(applicability);
        typer = (TypeResolverBuilder<?>)typer.init(JsonTypeInfo.Id.CLASS, null);
        typer = (TypeResolverBuilder<?>)typer.inclusion(includeAs);
        return this.setDefaultTyping(typer);
    }
    
    public ObjectMapper enableDefaultTypingAsProperty(final DefaultTyping applicability, final String propertyName) {
        TypeResolverBuilder<?> typer = new DefaultTypeResolverBuilder(applicability);
        typer = (TypeResolverBuilder<?>)typer.init(JsonTypeInfo.Id.CLASS, null);
        typer = (TypeResolverBuilder<?>)typer.inclusion(JsonTypeInfo.As.PROPERTY);
        typer = (TypeResolverBuilder<?>)typer.typeProperty(propertyName);
        return this.setDefaultTyping(typer);
    }
    
    public ObjectMapper disableDefaultTyping() {
        return this.setDefaultTyping(null);
    }
    
    public ObjectMapper setDefaultTyping(final TypeResolverBuilder<?> typer) {
        this._deserializationConfig = this._deserializationConfig.with(typer);
        this._serializationConfig = this._serializationConfig.with(typer);
        return this;
    }
    
    public void registerSubtypes(final Class<?>... classes) {
        this.getSubtypeResolver().registerSubtypes(classes);
    }
    
    public void registerSubtypes(final NamedType... types) {
        this.getSubtypeResolver().registerSubtypes(types);
    }
    
    public TypeFactory getTypeFactory() {
        return this._typeFactory;
    }
    
    public ObjectMapper setTypeFactory(final TypeFactory f) {
        this._typeFactory = f;
        this._deserializationConfig = this._deserializationConfig.with(f);
        this._serializationConfig = this._serializationConfig.with(f);
        return this;
    }
    
    public JavaType constructType(final Type t) {
        return this._typeFactory.constructType(t);
    }
    
    public ObjectMapper setNodeFactory(final JsonNodeFactory f) {
        this._deserializationConfig = this._deserializationConfig.with(f);
        return this;
    }
    
    public ObjectMapper addHandler(final DeserializationProblemHandler h) {
        this._deserializationConfig = this._deserializationConfig.withHandler(h);
        return this;
    }
    
    public ObjectMapper clearProblemHandlers() {
        this._deserializationConfig = this._deserializationConfig.withNoProblemHandlers();
        return this;
    }
    
    public ObjectMapper setConfig(final DeserializationConfig config) {
        this._deserializationConfig = config;
        return this;
    }
    
    public void setFilters(final FilterProvider filterProvider) {
        this._serializationConfig = this._serializationConfig.withFilters(filterProvider);
    }
    
    public ObjectMapper setBase64Variant(final Base64Variant v) {
        this._serializationConfig = this._serializationConfig.with(v);
        this._deserializationConfig = this._deserializationConfig.with(v);
        return this;
    }
    
    public ObjectMapper setConfig(final SerializationConfig config) {
        this._serializationConfig = config;
        return this;
    }
    
    @Override
    public JsonFactory getFactory() {
        return this._jsonFactory;
    }
    
    @Deprecated
    @Override
    public JsonFactory getJsonFactory() {
        return this.getFactory();
    }
    
    public ObjectMapper setDateFormat(final DateFormat dateFormat) {
        this._deserializationConfig = this._deserializationConfig.with(dateFormat);
        this._serializationConfig = this._serializationConfig.with(dateFormat);
        return this;
    }
    
    public Object setHandlerInstantiator(final HandlerInstantiator hi) {
        this._deserializationConfig = this._deserializationConfig.with(hi);
        this._serializationConfig = this._serializationConfig.with(hi);
        return this;
    }
    
    public ObjectMapper setInjectableValues(final InjectableValues injectableValues) {
        this._injectableValues = injectableValues;
        return this;
    }
    
    public ObjectMapper setLocale(final Locale l) {
        this._deserializationConfig = this._deserializationConfig.with(l);
        this._serializationConfig = this._serializationConfig.with(l);
        return this;
    }
    
    public ObjectMapper setTimeZone(final TimeZone tz) {
        this._deserializationConfig = this._deserializationConfig.with(tz);
        this._serializationConfig = this._serializationConfig.with(tz);
        return this;
    }
    
    public ObjectMapper configure(final MapperFeature f, final boolean state) {
        this._serializationConfig = (state ? this._serializationConfig.with(f) : this._serializationConfig.without(f));
        this._deserializationConfig = (state ? this._deserializationConfig.with(f) : this._deserializationConfig.without(f));
        return this;
    }
    
    public ObjectMapper configure(final SerializationFeature f, final boolean state) {
        this._serializationConfig = (state ? this._serializationConfig.with(f) : this._serializationConfig.without(f));
        return this;
    }
    
    public ObjectMapper configure(final DeserializationFeature f, final boolean state) {
        this._deserializationConfig = (state ? this._deserializationConfig.with(f) : this._deserializationConfig.without(f));
        return this;
    }
    
    public ObjectMapper configure(final JsonParser.Feature f, final boolean state) {
        this._jsonFactory.configure(f, state);
        return this;
    }
    
    public ObjectMapper configure(final JsonGenerator.Feature f, final boolean state) {
        this._jsonFactory.configure(f, state);
        return this;
    }
    
    public ObjectMapper enable(final MapperFeature... f) {
        this._deserializationConfig = this._deserializationConfig.with(f);
        this._serializationConfig = this._serializationConfig.with(f);
        return this;
    }
    
    public ObjectMapper disable(final MapperFeature... f) {
        this._deserializationConfig = this._deserializationConfig.without(f);
        this._serializationConfig = this._serializationConfig.without(f);
        return this;
    }
    
    public ObjectMapper enable(final DeserializationFeature feature) {
        this._deserializationConfig = this._deserializationConfig.with(feature);
        return this;
    }
    
    public ObjectMapper enable(final DeserializationFeature first, final DeserializationFeature... f) {
        this._deserializationConfig = this._deserializationConfig.with(first, f);
        return this;
    }
    
    public ObjectMapper disable(final DeserializationFeature feature) {
        this._deserializationConfig = this._deserializationConfig.without(feature);
        return this;
    }
    
    public ObjectMapper disable(final DeserializationFeature first, final DeserializationFeature... f) {
        this._deserializationConfig = this._deserializationConfig.without(first, f);
        return this;
    }
    
    public ObjectMapper enable(final SerializationFeature f) {
        this._serializationConfig = this._serializationConfig.with(f);
        return this;
    }
    
    public ObjectMapper enable(final SerializationFeature first, final SerializationFeature... f) {
        this._serializationConfig = this._serializationConfig.with(first, f);
        return this;
    }
    
    public ObjectMapper disable(final SerializationFeature f) {
        this._serializationConfig = this._serializationConfig.without(f);
        return this;
    }
    
    public ObjectMapper disable(final SerializationFeature first, final SerializationFeature... f) {
        this._serializationConfig = this._serializationConfig.without(first, f);
        return this;
    }
    
    public boolean isEnabled(final MapperFeature f) {
        return this._serializationConfig.isEnabled(f);
    }
    
    public boolean isEnabled(final SerializationFeature f) {
        return this._serializationConfig.isEnabled(f);
    }
    
    public boolean isEnabled(final DeserializationFeature f) {
        return this._deserializationConfig.isEnabled(f);
    }
    
    public boolean isEnabled(final JsonFactory.Feature f) {
        return this._jsonFactory.isEnabled(f);
    }
    
    public boolean isEnabled(final JsonParser.Feature f) {
        return this._jsonFactory.isEnabled(f);
    }
    
    public boolean isEnabled(final JsonGenerator.Feature f) {
        return this._jsonFactory.isEnabled(f);
    }
    
    public JsonNodeFactory getNodeFactory() {
        return this._deserializationConfig.getNodeFactory();
    }
    
    @Override
    public <T> T readValue(final JsonParser jp, final Class<T> valueType) throws IOException, JsonParseException, JsonMappingException {
        return (T)this._readValue(this.getDeserializationConfig(), jp, this._typeFactory.constructType(valueType));
    }
    
    @Override
    public <T> T readValue(final JsonParser jp, final TypeReference<?> valueTypeRef) throws IOException, JsonParseException, JsonMappingException {
        return (T)this._readValue(this.getDeserializationConfig(), jp, this._typeFactory.constructType(valueTypeRef));
    }
    
    @Override
    public final <T> T readValue(final JsonParser jp, final ResolvedType valueType) throws IOException, JsonParseException, JsonMappingException {
        return (T)this._readValue(this.getDeserializationConfig(), jp, (JavaType)valueType);
    }
    
    public <T> T readValue(final JsonParser jp, final JavaType valueType) throws IOException, JsonParseException, JsonMappingException {
        return (T)this._readValue(this.getDeserializationConfig(), jp, valueType);
    }
    
    @Override
    public <T extends TreeNode> T readTree(final JsonParser jp) throws IOException, JsonProcessingException {
        final DeserializationConfig cfg = this.getDeserializationConfig();
        JsonToken t = jp.getCurrentToken();
        if (t == null) {
            t = jp.nextToken();
            if (t == null) {
                return null;
            }
        }
        JsonNode n = (JsonNode)this._readValue(cfg, jp, ObjectMapper.JSON_NODE_TYPE);
        if (n == null) {
            n = this.getNodeFactory().nullNode();
        }
        final T result = (T)n;
        return result;
    }
    
    @Override
    public <T> MappingIterator<T> readValues(final JsonParser jp, final ResolvedType valueType) throws IOException, JsonProcessingException {
        return this.readValues(jp, (JavaType)valueType);
    }
    
    public <T> MappingIterator<T> readValues(final JsonParser jp, final JavaType valueType) throws IOException, JsonProcessingException {
        final DeserializationConfig config = this.getDeserializationConfig();
        final DeserializationContext ctxt = this.createDeserializationContext(jp, config);
        final JsonDeserializer<?> deser = this._findRootDeserializer(ctxt, valueType);
        return new MappingIterator<T>(valueType, jp, ctxt, deser, false, null);
    }
    
    @Override
    public <T> MappingIterator<T> readValues(final JsonParser jp, final Class<T> valueType) throws IOException, JsonProcessingException {
        return this.readValues(jp, this._typeFactory.constructType(valueType));
    }
    
    @Override
    public <T> MappingIterator<T> readValues(final JsonParser jp, final TypeReference<?> valueTypeRef) throws IOException, JsonProcessingException {
        return this.readValues(jp, this._typeFactory.constructType(valueTypeRef));
    }
    
    public JsonNode readTree(final InputStream in) throws IOException, JsonProcessingException {
        final JsonNode n = (JsonNode)this._readMapAndClose(this._jsonFactory.createParser(in), ObjectMapper.JSON_NODE_TYPE);
        return (n == null) ? NullNode.instance : n;
    }
    
    public JsonNode readTree(final Reader r) throws IOException, JsonProcessingException {
        final JsonNode n = (JsonNode)this._readMapAndClose(this._jsonFactory.createParser(r), ObjectMapper.JSON_NODE_TYPE);
        return (n == null) ? NullNode.instance : n;
    }
    
    public JsonNode readTree(final String content) throws IOException, JsonProcessingException {
        final JsonNode n = (JsonNode)this._readMapAndClose(this._jsonFactory.createParser(content), ObjectMapper.JSON_NODE_TYPE);
        return (n == null) ? NullNode.instance : n;
    }
    
    public JsonNode readTree(final byte[] content) throws IOException, JsonProcessingException {
        final JsonNode n = (JsonNode)this._readMapAndClose(this._jsonFactory.createParser(content), ObjectMapper.JSON_NODE_TYPE);
        return (n == null) ? NullNode.instance : n;
    }
    
    public JsonNode readTree(final File file) throws IOException, JsonProcessingException {
        final JsonNode n = (JsonNode)this._readMapAndClose(this._jsonFactory.createParser(file), ObjectMapper.JSON_NODE_TYPE);
        return (n == null) ? NullNode.instance : n;
    }
    
    public JsonNode readTree(final URL source) throws IOException, JsonProcessingException {
        final JsonNode n = (JsonNode)this._readMapAndClose(this._jsonFactory.createParser(source), ObjectMapper.JSON_NODE_TYPE);
        return (n == null) ? NullNode.instance : n;
    }
    
    @Override
    public void writeValue(final JsonGenerator jgen, final Object value) throws IOException, JsonGenerationException, JsonMappingException {
        final SerializationConfig config = this.getSerializationConfig();
        if (config.isEnabled(SerializationFeature.INDENT_OUTPUT)) {
            jgen.useDefaultPrettyPrinter();
        }
        if (config.isEnabled(SerializationFeature.CLOSE_CLOSEABLE) && value instanceof Closeable) {
            this._writeCloseableValue(jgen, value, config);
        }
        else {
            this._serializerProvider(config).serializeValue(jgen, value);
            if (config.isEnabled(SerializationFeature.FLUSH_AFTER_WRITE_VALUE)) {
                jgen.flush();
            }
        }
    }
    
    @Override
    public void writeTree(final JsonGenerator jgen, final TreeNode rootNode) throws IOException, JsonProcessingException {
        final SerializationConfig config = this.getSerializationConfig();
        this._serializerProvider(config).serializeValue(jgen, rootNode);
        if (config.isEnabled(SerializationFeature.FLUSH_AFTER_WRITE_VALUE)) {
            jgen.flush();
        }
    }
    
    public void writeTree(final JsonGenerator jgen, final JsonNode rootNode) throws IOException, JsonProcessingException {
        final SerializationConfig config = this.getSerializationConfig();
        this._serializerProvider(config).serializeValue(jgen, rootNode);
        if (config.isEnabled(SerializationFeature.FLUSH_AFTER_WRITE_VALUE)) {
            jgen.flush();
        }
    }
    
    @Override
    public ObjectNode createObjectNode() {
        return this._deserializationConfig.getNodeFactory().objectNode();
    }
    
    @Override
    public ArrayNode createArrayNode() {
        return this._deserializationConfig.getNodeFactory().arrayNode();
    }
    
    @Override
    public JsonParser treeAsTokens(final TreeNode n) {
        return new TreeTraversingParser((JsonNode)n, this);
    }
    
    @Override
    public <T> T treeToValue(final TreeNode n, final Class<T> valueType) throws JsonProcessingException {
        try {
            if (valueType != Object.class && valueType.isAssignableFrom(n.getClass())) {
                return (T)n;
            }
            return this.readValue(this.treeAsTokens(n), valueType);
        }
        catch (JsonProcessingException e) {
            throw e;
        }
        catch (IOException e2) {
            throw new IllegalArgumentException(e2.getMessage(), e2);
        }
    }
    
    public <T extends JsonNode> T valueToTree(final Object fromValue) throws IllegalArgumentException {
        if (fromValue == null) {
            return null;
        }
        final TokenBuffer buf = new TokenBuffer(this, false);
        JsonNode result;
        try {
            this.writeValue(buf, fromValue);
            final JsonParser jp = buf.asParser();
            result = this.readTree(jp);
            jp.close();
        }
        catch (IOException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
        return (T)result;
    }
    
    public boolean canSerialize(final Class<?> type) {
        return this._serializerProvider(this.getSerializationConfig()).hasSerializerFor(type, null);
    }
    
    public boolean canSerialize(final Class<?> type, final AtomicReference<Throwable> cause) {
        return this._serializerProvider(this.getSerializationConfig()).hasSerializerFor(type, cause);
    }
    
    public boolean canDeserialize(final JavaType type) {
        return this.createDeserializationContext(null, this.getDeserializationConfig()).hasValueDeserializerFor(type, null);
    }
    
    public boolean canDeserialize(final JavaType type, final AtomicReference<Throwable> cause) {
        return this.createDeserializationContext(null, this.getDeserializationConfig()).hasValueDeserializerFor(type, cause);
    }
    
    public <T> T readValue(final File src, final Class<T> valueType) throws IOException, JsonParseException, JsonMappingException {
        return (T)this._readMapAndClose(this._jsonFactory.createParser(src), this._typeFactory.constructType(valueType));
    }
    
    public <T> T readValue(final File src, final TypeReference valueTypeRef) throws IOException, JsonParseException, JsonMappingException {
        return (T)this._readMapAndClose(this._jsonFactory.createParser(src), this._typeFactory.constructType(valueTypeRef));
    }
    
    public <T> T readValue(final File src, final JavaType valueType) throws IOException, JsonParseException, JsonMappingException {
        return (T)this._readMapAndClose(this._jsonFactory.createParser(src), valueType);
    }
    
    public <T> T readValue(final URL src, final Class<T> valueType) throws IOException, JsonParseException, JsonMappingException {
        return (T)this._readMapAndClose(this._jsonFactory.createParser(src), this._typeFactory.constructType(valueType));
    }
    
    public <T> T readValue(final URL src, final TypeReference valueTypeRef) throws IOException, JsonParseException, JsonMappingException {
        return (T)this._readMapAndClose(this._jsonFactory.createParser(src), this._typeFactory.constructType(valueTypeRef));
    }
    
    public <T> T readValue(final URL src, final JavaType valueType) throws IOException, JsonParseException, JsonMappingException {
        return (T)this._readMapAndClose(this._jsonFactory.createParser(src), valueType);
    }
    
    public <T> T readValue(final String content, final Class<T> valueType) throws IOException, JsonParseException, JsonMappingException {
        return (T)this._readMapAndClose(this._jsonFactory.createParser(content), this._typeFactory.constructType(valueType));
    }
    
    public <T> T readValue(final String content, final TypeReference valueTypeRef) throws IOException, JsonParseException, JsonMappingException {
        return (T)this._readMapAndClose(this._jsonFactory.createParser(content), this._typeFactory.constructType(valueTypeRef));
    }
    
    public <T> T readValue(final String content, final JavaType valueType) throws IOException, JsonParseException, JsonMappingException {
        return (T)this._readMapAndClose(this._jsonFactory.createParser(content), valueType);
    }
    
    public <T> T readValue(final Reader src, final Class<T> valueType) throws IOException, JsonParseException, JsonMappingException {
        return (T)this._readMapAndClose(this._jsonFactory.createParser(src), this._typeFactory.constructType(valueType));
    }
    
    public <T> T readValue(final Reader src, final TypeReference valueTypeRef) throws IOException, JsonParseException, JsonMappingException {
        return (T)this._readMapAndClose(this._jsonFactory.createParser(src), this._typeFactory.constructType(valueTypeRef));
    }
    
    public <T> T readValue(final Reader src, final JavaType valueType) throws IOException, JsonParseException, JsonMappingException {
        return (T)this._readMapAndClose(this._jsonFactory.createParser(src), valueType);
    }
    
    public <T> T readValue(final InputStream src, final Class<T> valueType) throws IOException, JsonParseException, JsonMappingException {
        return (T)this._readMapAndClose(this._jsonFactory.createParser(src), this._typeFactory.constructType(valueType));
    }
    
    public <T> T readValue(final InputStream src, final TypeReference valueTypeRef) throws IOException, JsonParseException, JsonMappingException {
        return (T)this._readMapAndClose(this._jsonFactory.createParser(src), this._typeFactory.constructType(valueTypeRef));
    }
    
    public <T> T readValue(final InputStream src, final JavaType valueType) throws IOException, JsonParseException, JsonMappingException {
        return (T)this._readMapAndClose(this._jsonFactory.createParser(src), valueType);
    }
    
    public <T> T readValue(final byte[] src, final Class<T> valueType) throws IOException, JsonParseException, JsonMappingException {
        return (T)this._readMapAndClose(this._jsonFactory.createParser(src), this._typeFactory.constructType(valueType));
    }
    
    public <T> T readValue(final byte[] src, final int offset, final int len, final Class<T> valueType) throws IOException, JsonParseException, JsonMappingException {
        return (T)this._readMapAndClose(this._jsonFactory.createParser(src, offset, len), this._typeFactory.constructType(valueType));
    }
    
    public <T> T readValue(final byte[] src, final TypeReference valueTypeRef) throws IOException, JsonParseException, JsonMappingException {
        return (T)this._readMapAndClose(this._jsonFactory.createParser(src), this._typeFactory.constructType(valueTypeRef));
    }
    
    public <T> T readValue(final byte[] src, final int offset, final int len, final TypeReference valueTypeRef) throws IOException, JsonParseException, JsonMappingException {
        return (T)this._readMapAndClose(this._jsonFactory.createParser(src, offset, len), this._typeFactory.constructType(valueTypeRef));
    }
    
    public <T> T readValue(final byte[] src, final JavaType valueType) throws IOException, JsonParseException, JsonMappingException {
        return (T)this._readMapAndClose(this._jsonFactory.createParser(src), valueType);
    }
    
    public <T> T readValue(final byte[] src, final int offset, final int len, final JavaType valueType) throws IOException, JsonParseException, JsonMappingException {
        return (T)this._readMapAndClose(this._jsonFactory.createParser(src, offset, len), valueType);
    }
    
    public void writeValue(final File resultFile, final Object value) throws IOException, JsonGenerationException, JsonMappingException {
        this._configAndWriteValue(this._jsonFactory.createGenerator(resultFile, JsonEncoding.UTF8), value);
    }
    
    public void writeValue(final OutputStream out, final Object value) throws IOException, JsonGenerationException, JsonMappingException {
        this._configAndWriteValue(this._jsonFactory.createGenerator(out, JsonEncoding.UTF8), value);
    }
    
    public void writeValue(final Writer w, final Object value) throws IOException, JsonGenerationException, JsonMappingException {
        this._configAndWriteValue(this._jsonFactory.createGenerator(w), value);
    }
    
    public String writeValueAsString(final Object value) throws JsonProcessingException {
        final SegmentedStringWriter sw = new SegmentedStringWriter(this._jsonFactory._getBufferRecycler());
        try {
            this._configAndWriteValue(this._jsonFactory.createGenerator(sw), value);
        }
        catch (JsonProcessingException e) {
            throw e;
        }
        catch (IOException e2) {
            throw JsonMappingException.fromUnexpectedIOE(e2);
        }
        return sw.getAndClear();
    }
    
    public byte[] writeValueAsBytes(final Object value) throws JsonProcessingException {
        final ByteArrayBuilder bb = new ByteArrayBuilder(this._jsonFactory._getBufferRecycler());
        try {
            this._configAndWriteValue(this._jsonFactory.createGenerator(bb, JsonEncoding.UTF8), value);
        }
        catch (JsonProcessingException e) {
            throw e;
        }
        catch (IOException e2) {
            throw JsonMappingException.fromUnexpectedIOE(e2);
        }
        final byte[] result = bb.toByteArray();
        bb.release();
        return result;
    }
    
    public ObjectWriter writer() {
        return new ObjectWriter(this, this.getSerializationConfig());
    }
    
    public ObjectWriter writer(final SerializationFeature feature) {
        return new ObjectWriter(this, this.getSerializationConfig().with(feature));
    }
    
    public ObjectWriter writer(final SerializationFeature first, final SerializationFeature... other) {
        return new ObjectWriter(this, this.getSerializationConfig().with(first, other));
    }
    
    public ObjectWriter writer(final DateFormat df) {
        return new ObjectWriter(this, this.getSerializationConfig().with(df));
    }
    
    public ObjectWriter writerWithView(final Class<?> serializationView) {
        return new ObjectWriter(this, this.getSerializationConfig().withView(serializationView));
    }
    
    public ObjectWriter writerWithType(final Class<?> rootType) {
        return new ObjectWriter(this, this.getSerializationConfig(), (rootType == null) ? null : this._typeFactory.constructType(rootType), null);
    }
    
    public ObjectWriter writerWithType(final TypeReference<?> rootType) {
        return new ObjectWriter(this, this.getSerializationConfig(), (rootType == null) ? null : this._typeFactory.constructType(rootType), null);
    }
    
    public ObjectWriter writerWithType(final JavaType rootType) {
        return new ObjectWriter(this, this.getSerializationConfig(), rootType, null);
    }
    
    public ObjectWriter writer(PrettyPrinter pp) {
        if (pp == null) {
            pp = ObjectWriter.NULL_PRETTY_PRINTER;
        }
        return new ObjectWriter(this, this.getSerializationConfig(), null, pp);
    }
    
    public ObjectWriter writerWithDefaultPrettyPrinter() {
        return new ObjectWriter(this, this.getSerializationConfig(), null, this._defaultPrettyPrinter());
    }
    
    public ObjectWriter writer(final FilterProvider filterProvider) {
        return new ObjectWriter(this, this.getSerializationConfig().withFilters(filterProvider));
    }
    
    public ObjectWriter writer(final FormatSchema schema) {
        this._verifySchemaType(schema);
        return new ObjectWriter(this, this.getSerializationConfig(), schema);
    }
    
    public ObjectWriter writer(final Base64Variant defaultBase64) {
        return new ObjectWriter(this, this.getSerializationConfig().with(defaultBase64));
    }
    
    public ObjectWriter writer(final CharacterEscapes escapes) {
        return this.writer().with(escapes);
    }
    
    public ObjectWriter writer(final ContextAttributes attrs) {
        return new ObjectWriter(this, this.getSerializationConfig().with(attrs));
    }
    
    public ObjectReader reader() {
        return new ObjectReader(this, this.getDeserializationConfig()).with(this._injectableValues);
    }
    
    public ObjectReader reader(final DeserializationFeature feature) {
        return new ObjectReader(this, this.getDeserializationConfig().with(feature));
    }
    
    public ObjectReader reader(final DeserializationFeature first, final DeserializationFeature... other) {
        return new ObjectReader(this, this.getDeserializationConfig().with(first, other));
    }
    
    public ObjectReader readerForUpdating(final Object valueToUpdate) {
        final JavaType t = this._typeFactory.constructType(valueToUpdate.getClass());
        return new ObjectReader(this, this.getDeserializationConfig(), t, valueToUpdate, null, this._injectableValues);
    }
    
    public ObjectReader reader(final JavaType type) {
        return new ObjectReader(this, this.getDeserializationConfig(), type, null, null, this._injectableValues);
    }
    
    public ObjectReader reader(final Class<?> type) {
        return this.reader(this._typeFactory.constructType(type));
    }
    
    public ObjectReader reader(final TypeReference<?> type) {
        return this.reader(this._typeFactory.constructType(type));
    }
    
    public ObjectReader reader(final JsonNodeFactory f) {
        return new ObjectReader(this, this.getDeserializationConfig()).with(f);
    }
    
    public ObjectReader reader(final FormatSchema schema) {
        this._verifySchemaType(schema);
        return new ObjectReader(this, this.getDeserializationConfig(), null, null, schema, this._injectableValues);
    }
    
    public ObjectReader reader(final InjectableValues injectableValues) {
        return new ObjectReader(this, this.getDeserializationConfig(), null, null, null, injectableValues);
    }
    
    public ObjectReader readerWithView(final Class<?> view) {
        return new ObjectReader(this, this.getDeserializationConfig().withView(view));
    }
    
    public ObjectReader reader(final Base64Variant defaultBase64) {
        return new ObjectReader(this, this.getDeserializationConfig().with(defaultBase64));
    }
    
    public ObjectReader reader(final ContextAttributes attrs) {
        return new ObjectReader(this, this.getDeserializationConfig().with(attrs));
    }
    
    public <T> T convertValue(final Object fromValue, final Class<T> toValueType) throws IllegalArgumentException {
        if (fromValue == null) {
            return null;
        }
        return (T)this._convert(fromValue, this._typeFactory.constructType(toValueType));
    }
    
    public <T> T convertValue(final Object fromValue, final TypeReference<?> toValueTypeRef) throws IllegalArgumentException {
        return this.convertValue(fromValue, this._typeFactory.constructType(toValueTypeRef));
    }
    
    public <T> T convertValue(final Object fromValue, final JavaType toValueType) throws IllegalArgumentException {
        if (fromValue == null) {
            return null;
        }
        return (T)this._convert(fromValue, toValueType);
    }
    
    protected Object _convert(final Object fromValue, final JavaType toValueType) throws IllegalArgumentException {
        final Class<?> targetType = toValueType.getRawClass();
        if (targetType != Object.class && !toValueType.hasGenericTypes() && targetType.isAssignableFrom(fromValue.getClass())) {
            return fromValue;
        }
        final TokenBuffer buf = new TokenBuffer(this, false);
        try {
            final SerializationConfig config = this.getSerializationConfig().without(SerializationFeature.WRAP_ROOT_VALUE);
            this._serializerProvider(config).serializeValue(buf, fromValue);
            final JsonParser jp = buf.asParser();
            final DeserializationConfig deserConfig = this.getDeserializationConfig();
            final JsonToken t = this._initForReading(jp);
            Object result;
            if (t == JsonToken.VALUE_NULL) {
                final DeserializationContext ctxt = this.createDeserializationContext(jp, deserConfig);
                result = this._findRootDeserializer(ctxt, toValueType).getNullValue();
            }
            else if (t == JsonToken.END_ARRAY || t == JsonToken.END_OBJECT) {
                result = null;
            }
            else {
                final DeserializationContext ctxt = this.createDeserializationContext(jp, deserConfig);
                final JsonDeserializer<Object> deser = this._findRootDeserializer(ctxt, toValueType);
                result = deser.deserialize(jp, ctxt);
            }
            jp.close();
            return result;
        }
        catch (IOException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }
    
    public JsonSchema generateJsonSchema(final Class<?> t) throws JsonMappingException {
        return this._serializerProvider(this.getSerializationConfig()).generateJsonSchema(t);
    }
    
    public void acceptJsonFormatVisitor(final Class<?> type, final JsonFormatVisitorWrapper visitor) throws JsonMappingException {
        this.acceptJsonFormatVisitor(this._typeFactory.constructType(type), visitor);
    }
    
    public void acceptJsonFormatVisitor(final JavaType type, final JsonFormatVisitorWrapper visitor) throws JsonMappingException {
        if (type == null) {
            throw new IllegalArgumentException("type must be provided");
        }
        this._serializerProvider(this.getSerializationConfig()).acceptJsonFormatVisitor(type, visitor);
    }
    
    protected DefaultSerializerProvider _serializerProvider(final SerializationConfig config) {
        return this._serializerProvider.createInstance(config, this._serializerFactory);
    }
    
    protected PrettyPrinter _defaultPrettyPrinter() {
        return ObjectMapper._defaultPrettyPrinter;
    }
    
    protected final void _configAndWriteValue(final JsonGenerator jgen, final Object value) throws IOException, JsonGenerationException, JsonMappingException {
        final SerializationConfig cfg = this.getSerializationConfig();
        if (cfg.isEnabled(SerializationFeature.INDENT_OUTPUT)) {
            jgen.useDefaultPrettyPrinter();
        }
        if (cfg.isEnabled(SerializationFeature.WRITE_BIGDECIMAL_AS_PLAIN)) {
            jgen.enable(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN);
        }
        if (cfg.isEnabled(SerializationFeature.CLOSE_CLOSEABLE) && value instanceof Closeable) {
            this._configAndWriteCloseable(jgen, value, cfg);
            return;
        }
        boolean closed = false;
        try {
            this._serializerProvider(cfg).serializeValue(jgen, value);
            closed = true;
            jgen.close();
        }
        finally {
            if (!closed) {
                jgen.disable(JsonGenerator.Feature.AUTO_CLOSE_JSON_CONTENT);
                try {
                    jgen.close();
                }
                catch (IOException ex) {}
            }
        }
    }
    
    protected final void _configAndWriteValue(final JsonGenerator jgen, final Object value, final Class<?> viewClass) throws IOException, JsonGenerationException, JsonMappingException {
        final SerializationConfig cfg = this.getSerializationConfig().withView(viewClass);
        if (cfg.isEnabled(SerializationFeature.INDENT_OUTPUT)) {
            jgen.useDefaultPrettyPrinter();
        }
        if (cfg.isEnabled(SerializationFeature.WRITE_BIGDECIMAL_AS_PLAIN)) {
            jgen.enable(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN);
        }
        if (cfg.isEnabled(SerializationFeature.CLOSE_CLOSEABLE) && value instanceof Closeable) {
            this._configAndWriteCloseable(jgen, value, cfg);
            return;
        }
        boolean closed = false;
        try {
            this._serializerProvider(cfg).serializeValue(jgen, value);
            closed = true;
            jgen.close();
        }
        finally {
            if (!closed) {
                jgen.disable(JsonGenerator.Feature.AUTO_CLOSE_JSON_CONTENT);
                try {
                    jgen.close();
                }
                catch (IOException ex) {}
            }
        }
    }
    
    private final void _configAndWriteCloseable(JsonGenerator jgen, final Object value, final SerializationConfig cfg) throws IOException, JsonGenerationException, JsonMappingException {
        Closeable toClose = (Closeable)value;
        try {
            this._serializerProvider(cfg).serializeValue(jgen, value);
            final JsonGenerator tmpJgen = jgen;
            jgen = null;
            tmpJgen.close();
            final Closeable tmpToClose = toClose;
            toClose = null;
            tmpToClose.close();
        }
        finally {
            if (jgen != null) {
                jgen.disable(JsonGenerator.Feature.AUTO_CLOSE_JSON_CONTENT);
                try {
                    jgen.close();
                }
                catch (IOException ex) {}
            }
            if (toClose != null) {
                try {
                    toClose.close();
                }
                catch (IOException ex2) {}
            }
        }
    }
    
    private final void _writeCloseableValue(final JsonGenerator jgen, final Object value, final SerializationConfig cfg) throws IOException, JsonGenerationException, JsonMappingException {
        Closeable toClose = (Closeable)value;
        try {
            this._serializerProvider(cfg).serializeValue(jgen, value);
            if (cfg.isEnabled(SerializationFeature.FLUSH_AFTER_WRITE_VALUE)) {
                jgen.flush();
            }
            final Closeable tmpToClose = toClose;
            toClose = null;
            tmpToClose.close();
        }
        finally {
            if (toClose != null) {
                try {
                    toClose.close();
                }
                catch (IOException ex) {}
            }
        }
    }
    
    protected DefaultDeserializationContext createDeserializationContext(final JsonParser jp, final DeserializationConfig cfg) {
        return this._deserializationContext.createInstance(cfg, jp, this._injectableValues);
    }
    
    protected Object _readValue(final DeserializationConfig cfg, final JsonParser jp, final JavaType valueType) throws IOException, JsonParseException, JsonMappingException {
        final JsonToken t = this._initForReading(jp);
        Object result;
        if (t == JsonToken.VALUE_NULL) {
            final DeserializationContext ctxt = this.createDeserializationContext(jp, cfg);
            result = this._findRootDeserializer(ctxt, valueType).getNullValue();
        }
        else if (t == JsonToken.END_ARRAY || t == JsonToken.END_OBJECT) {
            result = null;
        }
        else {
            final DeserializationContext ctxt = this.createDeserializationContext(jp, cfg);
            final JsonDeserializer<Object> deser = this._findRootDeserializer(ctxt, valueType);
            if (cfg.useRootWrapping()) {
                result = this._unwrapAndDeserialize(jp, ctxt, cfg, valueType, deser);
            }
            else {
                result = deser.deserialize(jp, ctxt);
            }
        }
        jp.clearCurrentToken();
        return result;
    }
    
    protected Object _readMapAndClose(final JsonParser jp, final JavaType valueType) throws IOException, JsonParseException, JsonMappingException {
        try {
            final JsonToken t = this._initForReading(jp);
            Object result;
            if (t == JsonToken.VALUE_NULL) {
                final DeserializationContext ctxt = this.createDeserializationContext(jp, this.getDeserializationConfig());
                result = this._findRootDeserializer(ctxt, valueType).getNullValue();
            }
            else if (t == JsonToken.END_ARRAY || t == JsonToken.END_OBJECT) {
                result = null;
            }
            else {
                final DeserializationConfig cfg = this.getDeserializationConfig();
                final DeserializationContext ctxt2 = this.createDeserializationContext(jp, cfg);
                final JsonDeserializer<Object> deser = this._findRootDeserializer(ctxt2, valueType);
                if (cfg.useRootWrapping()) {
                    result = this._unwrapAndDeserialize(jp, ctxt2, cfg, valueType, deser);
                }
                else {
                    result = deser.deserialize(jp, ctxt2);
                }
                ctxt2.checkUnresolvedObjectId();
            }
            jp.clearCurrentToken();
            return result;
        }
        finally {
            try {
                jp.close();
            }
            catch (IOException ex) {}
        }
    }
    
    protected JsonToken _initForReading(final JsonParser jp) throws IOException, JsonParseException, JsonMappingException {
        JsonToken t = jp.getCurrentToken();
        if (t == null) {
            t = jp.nextToken();
            if (t == null) {
                throw JsonMappingException.from(jp, "No content to map due to end-of-input");
            }
        }
        return t;
    }
    
    protected Object _unwrapAndDeserialize(final JsonParser jp, final DeserializationContext ctxt, final DeserializationConfig config, final JavaType rootType, final JsonDeserializer<Object> deser) throws IOException, JsonParseException, JsonMappingException {
        String expName = config.getRootName();
        if (expName == null) {
            final PropertyName pname = this._rootNames.findRootName(rootType, config);
            expName = pname.getSimpleName();
        }
        if (jp.getCurrentToken() != JsonToken.START_OBJECT) {
            throw JsonMappingException.from(jp, "Current token not START_OBJECT (needed to unwrap root name '" + expName + "'), but " + jp.getCurrentToken());
        }
        if (jp.nextToken() != JsonToken.FIELD_NAME) {
            throw JsonMappingException.from(jp, "Current token not FIELD_NAME (to contain expected root name '" + expName + "'), but " + jp.getCurrentToken());
        }
        final String actualName = jp.getCurrentName();
        if (!expName.equals(actualName)) {
            throw JsonMappingException.from(jp, "Root name '" + actualName + "' does not match expected ('" + expName + "') for type " + rootType);
        }
        jp.nextToken();
        final Object result = deser.deserialize(jp, ctxt);
        if (jp.nextToken() != JsonToken.END_OBJECT) {
            throw JsonMappingException.from(jp, "Current token not END_OBJECT (to match wrapper object with root name '" + expName + "'), but " + jp.getCurrentToken());
        }
        return result;
    }
    
    protected JsonDeserializer<Object> _findRootDeserializer(final DeserializationContext ctxt, final JavaType valueType) throws JsonMappingException {
        JsonDeserializer<Object> deser = this._rootDeserializers.get(valueType);
        if (deser != null) {
            return deser;
        }
        deser = ctxt.findRootValueDeserializer(valueType);
        if (deser == null) {
            throw new JsonMappingException("Can not find a deserializer for type " + valueType);
        }
        this._rootDeserializers.put(valueType, deser);
        return deser;
    }
    
    protected void _verifySchemaType(final FormatSchema schema) {
        if (schema != null && !this._jsonFactory.canUseSchema(schema)) {
            throw new IllegalArgumentException("Can not use FormatSchema of type " + schema.getClass().getName() + " for format " + this._jsonFactory.getFormatName());
        }
    }
    
    static {
        JSON_NODE_TYPE = SimpleType.constructUnsafe(JsonNode.class);
        DEFAULT_INTROSPECTOR = BasicClassIntrospector.instance;
        DEFAULT_ANNOTATION_INTROSPECTOR = new JacksonAnnotationIntrospector();
        STD_VISIBILITY_CHECKER = VisibilityChecker.Std.defaultInstance();
        _defaultPrettyPrinter = new DefaultPrettyPrinter();
        DEFAULT_BASE = new BaseSettings(ObjectMapper.DEFAULT_INTROSPECTOR, ObjectMapper.DEFAULT_ANNOTATION_INTROSPECTOR, ObjectMapper.STD_VISIBILITY_CHECKER, null, TypeFactory.defaultInstance(), null, StdDateFormat.instance, null, Locale.getDefault(), TimeZone.getTimeZone("GMT"), Base64Variants.getDefaultVariant());
    }
    
    public enum DefaultTyping
    {
        JAVA_LANG_OBJECT, 
        OBJECT_AND_NON_CONCRETE, 
        NON_CONCRETE_AND_ARRAYS, 
        NON_FINAL;
    }
    
    public static class DefaultTypeResolverBuilder extends StdTypeResolverBuilder implements Serializable
    {
        private static final long serialVersionUID = 1L;
        protected final DefaultTyping _appliesFor;
        
        public DefaultTypeResolverBuilder(final DefaultTyping t) {
            this._appliesFor = t;
        }
        
        @Override
        public TypeDeserializer buildTypeDeserializer(final DeserializationConfig config, final JavaType baseType, final Collection<NamedType> subtypes) {
            return this.useForType(baseType) ? super.buildTypeDeserializer(config, baseType, subtypes) : null;
        }
        
        @Override
        public TypeSerializer buildTypeSerializer(final SerializationConfig config, final JavaType baseType, final Collection<NamedType> subtypes) {
            return this.useForType(baseType) ? super.buildTypeSerializer(config, baseType, subtypes) : null;
        }
        
        public boolean useForType(JavaType t) {
            switch (this._appliesFor) {
                case NON_CONCRETE_AND_ARRAYS: {
                    while (t.isArrayType()) {
                        t = t.getContentType();
                    }
                }
                case OBJECT_AND_NON_CONCRETE: {
                    return t.getRawClass() == Object.class || !t.isConcrete() || TreeNode.class.isAssignableFrom(t.getRawClass());
                }
                case NON_FINAL: {
                    while (t.isArrayType()) {
                        t = t.getContentType();
                    }
                    return !t.isFinal() && !TreeNode.class.isAssignableFrom(t.getRawClass());
                }
                default: {
                    return t.getRawClass() == Object.class;
                }
            }
        }
    }
}
