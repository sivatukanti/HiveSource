// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.deser;

import java.util.LinkedList;
import java.util.Queue;
import java.util.TreeSet;
import java.util.SortedSet;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.TreeMap;
import java.util.SortedMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.LinkedHashMap;
import java.lang.reflect.Method;
import org.apache.htrace.shaded.fasterxml.jackson.databind.DeserializationFeature;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.std.JdkDeserializers;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.std.TokenBufferDeserializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.util.TokenBuffer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.std.DateDeserializers;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.std.NumberDeserializers;
import org.apache.htrace.shaded.fasterxml.jackson.databind.type.TypeFactory;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.std.StringDeserializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.std.UntypedObjectDeserializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.util.EnumResolver;
import java.lang.reflect.Member;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.std.StdKeyDeserializers;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsontype.NamedType;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonNode;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.std.JsonNodeDeserializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.std.EnumDeserializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.type.MapLikeType;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.std.MapDeserializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.std.EnumMapDeserializer;
import java.util.EnumMap;
import org.apache.htrace.shaded.fasterxml.jackson.databind.KeyDeserializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.type.MapType;
import org.apache.htrace.shaded.fasterxml.jackson.databind.type.CollectionLikeType;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.std.CollectionDeserializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.std.StringCollectionDeserializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.std.ArrayBlockingQueueDeserializer;
import java.util.concurrent.ArrayBlockingQueue;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.std.EnumSetDeserializer;
import java.util.EnumSet;
import org.apache.htrace.shaded.fasterxml.jackson.databind.type.CollectionType;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.std.ObjectArrayDeserializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.std.StringArrayDeserializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.std.PrimitiveArrayDeserializers;
import org.apache.htrace.shaded.fasterxml.jackson.databind.type.ArrayType;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonDeserializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.BeanProperty;
import org.apache.htrace.shaded.fasterxml.jackson.databind.PropertyMetadata;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import org.apache.htrace.shaded.fasterxml.jackson.databind.util.NameTransformer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.AnnotatedMember;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.AnnotatedConstructor;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import org.apache.htrace.shaded.fasterxml.jackson.databind.cfg.HandlerInstantiator;
import org.apache.htrace.shaded.fasterxml.jackson.databind.cfg.MapperConfig;
import org.apache.htrace.shaded.fasterxml.jackson.databind.util.ClassUtil;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.VisibilityChecker;
import org.apache.htrace.shaded.fasterxml.jackson.databind.AnnotationIntrospector;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.impl.CreatorCollector;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.std.JsonLocationInstantiator;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonLocation;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.AnnotatedWithParams;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.AnnotatedParameter;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.AnnotatedClass;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.Annotated;
import org.apache.htrace.shaded.fasterxml.jackson.databind.BeanDescription;
import org.apache.htrace.shaded.fasterxml.jackson.databind.DeserializationContext;
import java.util.Iterator;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonMappingException;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JavaType;
import org.apache.htrace.shaded.fasterxml.jackson.databind.DeserializationConfig;
import org.apache.htrace.shaded.fasterxml.jackson.databind.AbstractTypeResolver;
import org.apache.htrace.shaded.fasterxml.jackson.databind.cfg.DeserializerFactoryConfig;
import java.util.Collection;
import java.util.Map;
import java.util.HashMap;
import org.apache.htrace.shaded.fasterxml.jackson.databind.PropertyName;
import java.io.Serializable;

public abstract class BasicDeserializerFactory extends DeserializerFactory implements Serializable
{
    private static final Class<?> CLASS_OBJECT;
    private static final Class<?> CLASS_STRING;
    private static final Class<?> CLASS_CHAR_BUFFER;
    private static final Class<?> CLASS_ITERABLE;
    protected static final PropertyName UNWRAPPED_CREATOR_PARAM_NAME;
    static final HashMap<String, Class<? extends Map>> _mapFallbacks;
    static final HashMap<String, Class<? extends Collection>> _collectionFallbacks;
    protected final DeserializerFactoryConfig _factoryConfig;
    
    protected BasicDeserializerFactory(final DeserializerFactoryConfig config) {
        this._factoryConfig = config;
    }
    
    public DeserializerFactoryConfig getFactoryConfig() {
        return this._factoryConfig;
    }
    
    protected abstract DeserializerFactory withConfig(final DeserializerFactoryConfig p0);
    
    @Override
    public final DeserializerFactory withAdditionalDeserializers(final Deserializers additional) {
        return this.withConfig(this._factoryConfig.withAdditionalDeserializers(additional));
    }
    
    @Override
    public final DeserializerFactory withAdditionalKeyDeserializers(final KeyDeserializers additional) {
        return this.withConfig(this._factoryConfig.withAdditionalKeyDeserializers(additional));
    }
    
    @Override
    public final DeserializerFactory withDeserializerModifier(final BeanDeserializerModifier modifier) {
        return this.withConfig(this._factoryConfig.withDeserializerModifier(modifier));
    }
    
    @Override
    public final DeserializerFactory withAbstractTypeResolver(final AbstractTypeResolver resolver) {
        return this.withConfig(this._factoryConfig.withAbstractTypeResolver(resolver));
    }
    
    @Override
    public final DeserializerFactory withValueInstantiators(final ValueInstantiators instantiators) {
        return this.withConfig(this._factoryConfig.withValueInstantiators(instantiators));
    }
    
    @Override
    public JavaType mapAbstractType(final DeserializationConfig config, JavaType type) throws JsonMappingException {
        while (true) {
            final JavaType next = this._mapAbstractType2(config, type);
            if (next == null) {
                return type;
            }
            final Class<?> prevCls = type.getRawClass();
            final Class<?> nextCls = next.getRawClass();
            if (prevCls == nextCls || !prevCls.isAssignableFrom(nextCls)) {
                throw new IllegalArgumentException("Invalid abstract type resolution from " + type + " to " + next + ": latter is not a subtype of former");
            }
            type = next;
        }
    }
    
    private JavaType _mapAbstractType2(final DeserializationConfig config, final JavaType type) throws JsonMappingException {
        final Class<?> currClass = type.getRawClass();
        if (this._factoryConfig.hasAbstractTypeResolvers()) {
            for (final AbstractTypeResolver resolver : this._factoryConfig.abstractTypeResolvers()) {
                final JavaType concrete = resolver.findTypeMapping(config, type);
                if (concrete != null && concrete.getRawClass() != currClass) {
                    return concrete;
                }
            }
        }
        return null;
    }
    
    @Override
    public ValueInstantiator findValueInstantiator(final DeserializationContext ctxt, final BeanDescription beanDesc) throws JsonMappingException {
        final DeserializationConfig config = ctxt.getConfig();
        ValueInstantiator instantiator = null;
        final AnnotatedClass ac = beanDesc.getClassInfo();
        final Object instDef = ctxt.getAnnotationIntrospector().findValueInstantiator(ac);
        if (instDef != null) {
            instantiator = this._valueInstantiatorInstance(config, ac, instDef);
        }
        if (instantiator == null) {
            instantiator = this._findStdValueInstantiator(config, beanDesc);
            if (instantiator == null) {
                instantiator = this._constructDefaultValueInstantiator(ctxt, beanDesc);
            }
        }
        if (this._factoryConfig.hasValueInstantiators()) {
            for (final ValueInstantiators insts : this._factoryConfig.valueInstantiators()) {
                instantiator = insts.findValueInstantiator(config, beanDesc, instantiator);
                if (instantiator == null) {
                    throw new JsonMappingException("Broken registered ValueInstantiators (of type " + insts.getClass().getName() + "): returned null ValueInstantiator");
                }
            }
        }
        if (instantiator.getIncompleteParameter() != null) {
            final AnnotatedParameter nonAnnotatedParam = instantiator.getIncompleteParameter();
            final AnnotatedWithParams ctor = nonAnnotatedParam.getOwner();
            throw new IllegalArgumentException("Argument #" + nonAnnotatedParam.getIndex() + " of constructor " + ctor + " has no property name annotation; must have name when multiple-parameter constructor annotated as Creator");
        }
        return instantiator;
    }
    
    private ValueInstantiator _findStdValueInstantiator(final DeserializationConfig config, final BeanDescription beanDesc) throws JsonMappingException {
        if (beanDesc.getBeanClass() == JsonLocation.class) {
            return new JsonLocationInstantiator();
        }
        return null;
    }
    
    protected ValueInstantiator _constructDefaultValueInstantiator(final DeserializationContext ctxt, final BeanDescription beanDesc) throws JsonMappingException {
        final boolean fixAccess = ctxt.canOverrideAccessModifiers();
        final CreatorCollector creators = new CreatorCollector(beanDesc, fixAccess);
        final AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
        final DeserializationConfig config = ctxt.getConfig();
        VisibilityChecker<?> vchecker = config.getDefaultVisibilityChecker();
        vchecker = intr.findAutoDetectVisibility(beanDesc.getClassInfo(), vchecker);
        this._addDeserializerFactoryMethods(ctxt, beanDesc, vchecker, intr, creators);
        if (beanDesc.getType().isConcrete()) {
            this._addDeserializerConstructors(ctxt, beanDesc, vchecker, intr, creators);
        }
        return creators.constructValueInstantiator(config);
    }
    
    public ValueInstantiator _valueInstantiatorInstance(final DeserializationConfig config, final Annotated annotated, final Object instDef) throws JsonMappingException {
        if (instDef == null) {
            return null;
        }
        if (instDef instanceof ValueInstantiator) {
            return (ValueInstantiator)instDef;
        }
        if (!(instDef instanceof Class)) {
            throw new IllegalStateException("AnnotationIntrospector returned key deserializer definition of type " + instDef.getClass().getName() + "; expected type KeyDeserializer or Class<KeyDeserializer> instead");
        }
        final Class<?> instClass = (Class<?>)instDef;
        if (ClassUtil.isBogusClass(instClass)) {
            return null;
        }
        if (!ValueInstantiator.class.isAssignableFrom(instClass)) {
            throw new IllegalStateException("AnnotationIntrospector returned Class " + instClass.getName() + "; expected Class<ValueInstantiator>");
        }
        final HandlerInstantiator hi = config.getHandlerInstantiator();
        if (hi != null) {
            final ValueInstantiator inst = hi.valueInstantiatorInstance(config, annotated, instClass);
            if (inst != null) {
                return inst;
            }
        }
        return ClassUtil.createInstance(instClass, config.canOverrideAccessModifiers());
    }
    
    protected void _addDeserializerConstructors(final DeserializationContext ctxt, final BeanDescription beanDesc, final VisibilityChecker<?> vchecker, final AnnotationIntrospector intr, final CreatorCollector creators) throws JsonMappingException {
        final AnnotatedConstructor defaultCtor = beanDesc.findDefaultConstructor();
        if (defaultCtor != null && (!creators.hasDefaultCreator() || intr.hasCreatorAnnotation(defaultCtor))) {
            creators.setDefaultCreator(defaultCtor);
        }
        PropertyName[] ctorPropNames = null;
        AnnotatedConstructor propertyCtor = null;
        for (final BeanPropertyDefinition propDef : beanDesc.findProperties()) {
            if (propDef.getConstructorParameter() != null) {
                final AnnotatedParameter param = propDef.getConstructorParameter();
                final AnnotatedWithParams owner = param.getOwner();
                if (!(owner instanceof AnnotatedConstructor)) {
                    continue;
                }
                if (propertyCtor == null) {
                    propertyCtor = (AnnotatedConstructor)owner;
                    ctorPropNames = new PropertyName[propertyCtor.getParameterCount()];
                }
                ctorPropNames[param.getIndex()] = propDef.getFullName();
            }
        }
        for (final AnnotatedConstructor ctor : beanDesc.getConstructors()) {
            final int argCount = ctor.getParameterCount();
            final boolean isCreator = intr.hasCreatorAnnotation(ctor) || ctor == propertyCtor;
            final boolean isVisible = vchecker.isCreatorVisible(ctor);
            if (argCount == 1) {
                final PropertyName name = (ctor == propertyCtor) ? ctorPropNames[0] : null;
                this._handleSingleArgumentConstructor(ctxt, beanDesc, vchecker, intr, creators, ctor, isCreator, isVisible, name);
            }
            else {
                if (!isCreator && !isVisible) {
                    continue;
                }
                AnnotatedParameter nonAnnotatedParam = null;
                int namedCount = 0;
                int injectCount = 0;
                final CreatorProperty[] properties = new CreatorProperty[argCount];
                for (int i = 0; i < argCount; ++i) {
                    final AnnotatedParameter param2 = ctor.getParameter(i);
                    PropertyName name2 = null;
                    if (ctor == propertyCtor) {
                        name2 = ctorPropNames[i];
                    }
                    if (name2 == null) {
                        name2 = this._findParamName(param2, intr);
                    }
                    final Object injectId = intr.findInjectableValueId(param2);
                    if (name2 != null && name2.hasSimpleName()) {
                        ++namedCount;
                        properties[i] = this.constructCreatorProperty(ctxt, beanDesc, name2, i, param2, injectId);
                    }
                    else if (injectId != null) {
                        ++injectCount;
                        properties[i] = this.constructCreatorProperty(ctxt, beanDesc, name2, i, param2, injectId);
                    }
                    else {
                        final NameTransformer unwrapper = intr.findUnwrappingNameTransformer(param2);
                        if (unwrapper != null) {
                            properties[i] = this.constructCreatorProperty(ctxt, beanDesc, BasicDeserializerFactory.UNWRAPPED_CREATOR_PARAM_NAME, i, param2, null);
                            ++namedCount;
                        }
                        else if (nonAnnotatedParam == null) {
                            nonAnnotatedParam = param2;
                        }
                    }
                }
                if (!isCreator && namedCount <= 0 && injectCount <= 0) {
                    continue;
                }
                if (namedCount + injectCount == argCount) {
                    creators.addPropertyCreator(ctor, properties);
                }
                else if (namedCount == 0 && injectCount + 1 == argCount) {
                    creators.addDelegatingCreator(ctor, properties);
                }
                else {
                    creators.addIncompeteParameter(nonAnnotatedParam);
                }
            }
        }
    }
    
    protected boolean _handleSingleArgumentConstructor(final DeserializationContext ctxt, final BeanDescription beanDesc, final VisibilityChecker<?> vchecker, final AnnotationIntrospector intr, final CreatorCollector creators, final AnnotatedConstructor ctor, final boolean isCreator, final boolean isVisible, PropertyName name) throws JsonMappingException {
        final AnnotatedParameter param = ctor.getParameter(0);
        if (name == null) {
            name = this._findParamName(param, intr);
        }
        final Object injectId = intr.findInjectableValueId(param);
        if (injectId != null || (name != null && name.hasSimpleName())) {
            final CreatorProperty[] properties = { this.constructCreatorProperty(ctxt, beanDesc, name, 0, param, injectId) };
            creators.addPropertyCreator(ctor, properties);
            return true;
        }
        final Class<?> type = ctor.getRawParameterType(0);
        if (type == String.class) {
            if (isCreator || isVisible) {
                creators.addStringCreator(ctor);
            }
            return true;
        }
        if (type == Integer.TYPE || type == Integer.class) {
            if (isCreator || isVisible) {
                creators.addIntCreator(ctor);
            }
            return true;
        }
        if (type == Long.TYPE || type == Long.class) {
            if (isCreator || isVisible) {
                creators.addLongCreator(ctor);
            }
            return true;
        }
        if (type == Double.TYPE || type == Double.class) {
            if (isCreator || isVisible) {
                creators.addDoubleCreator(ctor);
            }
            return true;
        }
        if (type == Boolean.TYPE || type == Boolean.class) {
            if (isCreator || isVisible) {
                creators.addBooleanCreator(ctor);
            }
            return true;
        }
        if (isCreator) {
            creators.addDelegatingCreator(ctor, null);
            return true;
        }
        return false;
    }
    
    protected void _addDeserializerFactoryMethods(final DeserializationContext ctxt, final BeanDescription beanDesc, final VisibilityChecker<?> vchecker, final AnnotationIntrospector intr, final CreatorCollector creators) throws JsonMappingException {
        final DeserializationConfig config = ctxt.getConfig();
        for (final AnnotatedMethod factory : beanDesc.getFactoryMethods()) {
            final boolean isCreator = intr.hasCreatorAnnotation(factory);
            final int argCount = factory.getParameterCount();
            if (argCount == 0) {
                if (!isCreator) {
                    continue;
                }
                creators.setDefaultCreator(factory);
            }
            else {
                if (argCount == 1) {
                    final AnnotatedParameter param = factory.getParameter(0);
                    final PropertyName pn = this._findParamName(param, intr);
                    final String name = (pn == null) ? null : pn.getSimpleName();
                    final Object injectId = intr.findInjectableValueId(param);
                    if (injectId == null && (name == null || name.length() == 0)) {
                        this._handleSingleArgumentFactory(config, beanDesc, vchecker, intr, creators, factory, isCreator);
                        continue;
                    }
                }
                else if (!intr.hasCreatorAnnotation(factory)) {
                    continue;
                }
                AnnotatedParameter nonAnnotatedParam = null;
                final CreatorProperty[] properties = new CreatorProperty[argCount];
                int namedCount = 0;
                int injectCount = 0;
                for (int i = 0; i < argCount; ++i) {
                    final AnnotatedParameter param2 = factory.getParameter(i);
                    final PropertyName name2 = this._findParamName(param2, intr);
                    final Object injectId2 = intr.findInjectableValueId(param2);
                    if (name2 != null && name2.hasSimpleName()) {
                        ++namedCount;
                        properties[i] = this.constructCreatorProperty(ctxt, beanDesc, name2, i, param2, injectId2);
                    }
                    else if (injectId2 != null) {
                        ++injectCount;
                        properties[i] = this.constructCreatorProperty(ctxt, beanDesc, name2, i, param2, injectId2);
                    }
                    else {
                        final NameTransformer unwrapper = intr.findUnwrappingNameTransformer(param2);
                        if (unwrapper != null) {
                            properties[i] = this.constructCreatorProperty(ctxt, beanDesc, BasicDeserializerFactory.UNWRAPPED_CREATOR_PARAM_NAME, i, param2, null);
                            ++namedCount;
                        }
                        else if (nonAnnotatedParam == null) {
                            nonAnnotatedParam = param2;
                        }
                    }
                }
                if (!isCreator && namedCount <= 0 && injectCount <= 0) {
                    continue;
                }
                if (namedCount + injectCount == argCount) {
                    creators.addPropertyCreator(factory, properties);
                }
                else {
                    if (namedCount != 0 || injectCount + 1 != argCount) {
                        throw new IllegalArgumentException("Argument #" + nonAnnotatedParam.getIndex() + " of factory method " + factory + " has no property name annotation; must have name when multiple-parameter constructor annotated as Creator");
                    }
                    creators.addDelegatingCreator(factory, properties);
                }
            }
        }
    }
    
    protected boolean _handleSingleArgumentFactory(final DeserializationConfig config, final BeanDescription beanDesc, final VisibilityChecker<?> vchecker, final AnnotationIntrospector intr, final CreatorCollector creators, final AnnotatedMethod factory, final boolean isCreator) throws JsonMappingException {
        final Class<?> type = factory.getRawParameterType(0);
        if (type == String.class) {
            if (isCreator || vchecker.isCreatorVisible(factory)) {
                creators.addStringCreator(factory);
            }
            return true;
        }
        if (type == Integer.TYPE || type == Integer.class) {
            if (isCreator || vchecker.isCreatorVisible(factory)) {
                creators.addIntCreator(factory);
            }
            return true;
        }
        if (type == Long.TYPE || type == Long.class) {
            if (isCreator || vchecker.isCreatorVisible(factory)) {
                creators.addLongCreator(factory);
            }
            return true;
        }
        if (type == Double.TYPE || type == Double.class) {
            if (isCreator || vchecker.isCreatorVisible(factory)) {
                creators.addDoubleCreator(factory);
            }
            return true;
        }
        if (type == Boolean.TYPE || type == Boolean.class) {
            if (isCreator || vchecker.isCreatorVisible(factory)) {
                creators.addBooleanCreator(factory);
            }
            return true;
        }
        if (intr.hasCreatorAnnotation(factory)) {
            creators.addDelegatingCreator(factory, null);
            return true;
        }
        return false;
    }
    
    protected CreatorProperty constructCreatorProperty(final DeserializationContext ctxt, final BeanDescription beanDesc, final PropertyName name, final int index, final AnnotatedParameter param, final Object injectableValueId) throws JsonMappingException {
        final DeserializationConfig config = ctxt.getConfig();
        final AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
        final Boolean b = (intr == null) ? null : intr.hasRequiredMarker(param);
        final boolean req = b != null && b;
        final String desc = (intr == null) ? null : intr.findPropertyDescription(param);
        final Integer idx = (intr == null) ? null : intr.findPropertyIndex(param);
        final PropertyMetadata metadata = PropertyMetadata.construct(req, desc, idx);
        final JavaType t0 = config.getTypeFactory().constructType(param.getParameterType(), beanDesc.bindingsForBeanType());
        BeanProperty.Std property = new BeanProperty.Std(name, t0, intr.findWrapperName(param), beanDesc.getClassAnnotations(), param, metadata);
        JavaType type = this.resolveType(ctxt, beanDesc, t0, param);
        if (type != t0) {
            property = property.withType(type);
        }
        JsonDeserializer<?> deser = this.findDeserializerFromAnnotation(ctxt, param);
        type = this.modifyTypeByAnnotation(ctxt, param, type);
        TypeDeserializer typeDeser = type.getTypeHandler();
        if (typeDeser == null) {
            typeDeser = this.findTypeDeserializer(config, type);
        }
        CreatorProperty prop = new CreatorProperty(name, type, property.getWrapperName(), typeDeser, beanDesc.getClassAnnotations(), param, index, injectableValueId, metadata);
        if (deser != null) {
            deser = ctxt.handlePrimaryContextualization(deser, prop);
            prop = prop.withValueDeserializer(deser);
        }
        return prop;
    }
    
    protected PropertyName _findParamName(final AnnotatedParameter param, final AnnotationIntrospector intr) {
        if (param != null && intr != null) {
            final PropertyName name = intr.findNameForDeserialization(param);
            if (name != null) {
                return name;
            }
            final String str = intr.findImplicitPropertyName(param);
            if (str != null && !str.isEmpty()) {
                return new PropertyName(str);
            }
        }
        return null;
    }
    
    @Override
    public JsonDeserializer<?> createArrayDeserializer(final DeserializationContext ctxt, final ArrayType type, final BeanDescription beanDesc) throws JsonMappingException {
        final DeserializationConfig config = ctxt.getConfig();
        final JavaType elemType = type.getContentType();
        final JsonDeserializer<Object> contentDeser = elemType.getValueHandler();
        TypeDeserializer elemTypeDeser = elemType.getTypeHandler();
        if (elemTypeDeser == null) {
            elemTypeDeser = this.findTypeDeserializer(config, elemType);
        }
        JsonDeserializer<?> deser = this._findCustomArrayDeserializer(type, config, beanDesc, elemTypeDeser, contentDeser);
        if (deser == null) {
            if (contentDeser == null) {
                final Class<?> raw = elemType.getRawClass();
                if (elemType.isPrimitive()) {
                    return PrimitiveArrayDeserializers.forType(raw);
                }
                if (raw == String.class) {
                    return StringArrayDeserializer.instance;
                }
            }
            deser = new ObjectArrayDeserializer(type, contentDeser, elemTypeDeser);
        }
        if (this._factoryConfig.hasDeserializerModifiers()) {
            for (final BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
                deser = mod.modifyArrayDeserializer(config, type, beanDesc, deser);
            }
        }
        return deser;
    }
    
    protected JsonDeserializer<?> _findCustomArrayDeserializer(final ArrayType type, final DeserializationConfig config, final BeanDescription beanDesc, final TypeDeserializer elementTypeDeserializer, final JsonDeserializer<?> elementDeserializer) throws JsonMappingException {
        for (final Deserializers d : this._factoryConfig.deserializers()) {
            final JsonDeserializer<?> deser = d.findArrayDeserializer(type, config, beanDesc, elementTypeDeserializer, elementDeserializer);
            if (deser != null) {
                return deser;
            }
        }
        return null;
    }
    
    @Override
    public JsonDeserializer<?> createCollectionDeserializer(final DeserializationContext ctxt, CollectionType type, BeanDescription beanDesc) throws JsonMappingException {
        final JavaType contentType = type.getContentType();
        final JsonDeserializer<Object> contentDeser = contentType.getValueHandler();
        final DeserializationConfig config = ctxt.getConfig();
        TypeDeserializer contentTypeDeser = contentType.getTypeHandler();
        if (contentTypeDeser == null) {
            contentTypeDeser = this.findTypeDeserializer(config, contentType);
        }
        JsonDeserializer<?> deser = this._findCustomCollectionDeserializer(type, config, beanDesc, contentTypeDeser, contentDeser);
        if (deser == null) {
            final Class<?> collectionClass = type.getRawClass();
            if (contentDeser == null && EnumSet.class.isAssignableFrom(collectionClass)) {
                deser = new EnumSetDeserializer(contentType, null);
            }
        }
        if (deser == null) {
            if (type.isInterface() || type.isAbstract()) {
                final CollectionType implType = this._mapAbstractCollectionType(type, config);
                if (implType == null) {
                    if (type.getTypeHandler() == null) {
                        throw new IllegalArgumentException("Can not find a deserializer for non-concrete Collection type " + type);
                    }
                    deser = AbstractDeserializer.constructForNonPOJO(beanDesc);
                }
                else {
                    type = implType;
                    beanDesc = config.introspectForCreation(type);
                }
            }
            if (deser == null) {
                final ValueInstantiator inst = this.findValueInstantiator(ctxt, beanDesc);
                if (!inst.canCreateUsingDefault() && type.getRawClass() == ArrayBlockingQueue.class) {
                    return new ArrayBlockingQueueDeserializer(type, contentDeser, contentTypeDeser, inst, null);
                }
                if (contentType.getRawClass() == String.class) {
                    deser = new StringCollectionDeserializer(type, contentDeser, inst);
                }
                else {
                    deser = new CollectionDeserializer(type, contentDeser, contentTypeDeser, inst);
                }
            }
        }
        if (this._factoryConfig.hasDeserializerModifiers()) {
            for (final BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
                deser = mod.modifyCollectionDeserializer(config, type, beanDesc, deser);
            }
        }
        return deser;
    }
    
    protected CollectionType _mapAbstractCollectionType(final JavaType type, final DeserializationConfig config) {
        Class<?> collectionClass = type.getRawClass();
        collectionClass = BasicDeserializerFactory._collectionFallbacks.get(collectionClass.getName());
        if (collectionClass == null) {
            return null;
        }
        return (CollectionType)config.constructSpecializedType(type, collectionClass);
    }
    
    protected JsonDeserializer<?> _findCustomCollectionDeserializer(final CollectionType type, final DeserializationConfig config, final BeanDescription beanDesc, final TypeDeserializer elementTypeDeserializer, final JsonDeserializer<?> elementDeserializer) throws JsonMappingException {
        for (final Deserializers d : this._factoryConfig.deserializers()) {
            final JsonDeserializer<?> deser = d.findCollectionDeserializer(type, config, beanDesc, elementTypeDeserializer, elementDeserializer);
            if (deser != null) {
                return deser;
            }
        }
        return null;
    }
    
    @Override
    public JsonDeserializer<?> createCollectionLikeDeserializer(final DeserializationContext ctxt, final CollectionLikeType type, final BeanDescription beanDesc) throws JsonMappingException {
        final JavaType contentType = type.getContentType();
        final JsonDeserializer<Object> contentDeser = contentType.getValueHandler();
        final DeserializationConfig config = ctxt.getConfig();
        TypeDeserializer contentTypeDeser = contentType.getTypeHandler();
        if (contentTypeDeser == null) {
            contentTypeDeser = this.findTypeDeserializer(config, contentType);
        }
        JsonDeserializer<?> deser = this._findCustomCollectionLikeDeserializer(type, config, beanDesc, contentTypeDeser, contentDeser);
        if (deser != null && this._factoryConfig.hasDeserializerModifiers()) {
            for (final BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
                deser = mod.modifyCollectionLikeDeserializer(config, type, beanDesc, deser);
            }
        }
        return deser;
    }
    
    protected JsonDeserializer<?> _findCustomCollectionLikeDeserializer(final CollectionLikeType type, final DeserializationConfig config, final BeanDescription beanDesc, final TypeDeserializer elementTypeDeserializer, final JsonDeserializer<?> elementDeserializer) throws JsonMappingException {
        for (final Deserializers d : this._factoryConfig.deserializers()) {
            final JsonDeserializer<?> deser = d.findCollectionLikeDeserializer(type, config, beanDesc, elementTypeDeserializer, elementDeserializer);
            if (deser != null) {
                return deser;
            }
        }
        return null;
    }
    
    @Override
    public JsonDeserializer<?> createMapDeserializer(final DeserializationContext ctxt, MapType type, BeanDescription beanDesc) throws JsonMappingException {
        final DeserializationConfig config = ctxt.getConfig();
        final JavaType keyType = type.getKeyType();
        final JavaType contentType = type.getContentType();
        final JsonDeserializer<Object> contentDeser = contentType.getValueHandler();
        final KeyDeserializer keyDes = keyType.getValueHandler();
        TypeDeserializer contentTypeDeser = contentType.getTypeHandler();
        if (contentTypeDeser == null) {
            contentTypeDeser = this.findTypeDeserializer(config, contentType);
        }
        JsonDeserializer<?> deser = this._findCustomMapDeserializer(type, config, beanDesc, keyDes, contentTypeDeser, contentDeser);
        if (deser == null) {
            Class<?> mapClass = type.getRawClass();
            if (EnumMap.class.isAssignableFrom(mapClass)) {
                final Class<?> kt = keyType.getRawClass();
                if (kt == null || !kt.isEnum()) {
                    throw new IllegalArgumentException("Can not construct EnumMap; generic (key) type not available");
                }
                deser = new EnumMapDeserializer(type, null, contentDeser, contentTypeDeser);
            }
            if (deser == null) {
                if (type.isInterface() || type.isAbstract()) {
                    final Class<? extends Map> fallback = BasicDeserializerFactory._mapFallbacks.get(mapClass.getName());
                    if (fallback != null) {
                        mapClass = fallback;
                        type = (MapType)config.constructSpecializedType(type, mapClass);
                        beanDesc = config.introspectForCreation(type);
                    }
                    else {
                        if (type.getTypeHandler() == null) {
                            throw new IllegalArgumentException("Can not find a deserializer for non-concrete Map type " + type);
                        }
                        deser = AbstractDeserializer.constructForNonPOJO(beanDesc);
                    }
                }
                if (deser == null) {
                    final ValueInstantiator inst = this.findValueInstantiator(ctxt, beanDesc);
                    final MapDeserializer md = new MapDeserializer(type, inst, keyDes, contentDeser, contentTypeDeser);
                    md.setIgnorableProperties(config.getAnnotationIntrospector().findPropertiesToIgnore(beanDesc.getClassInfo()));
                    deser = md;
                }
            }
        }
        if (this._factoryConfig.hasDeserializerModifiers()) {
            for (final BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
                deser = mod.modifyMapDeserializer(config, type, beanDesc, deser);
            }
        }
        return deser;
    }
    
    @Override
    public JsonDeserializer<?> createMapLikeDeserializer(final DeserializationContext ctxt, final MapLikeType type, final BeanDescription beanDesc) throws JsonMappingException {
        final JavaType keyType = type.getKeyType();
        final JavaType contentType = type.getContentType();
        final DeserializationConfig config = ctxt.getConfig();
        final JsonDeserializer<Object> contentDeser = contentType.getValueHandler();
        final KeyDeserializer keyDes = keyType.getValueHandler();
        TypeDeserializer contentTypeDeser = contentType.getTypeHandler();
        if (contentTypeDeser == null) {
            contentTypeDeser = this.findTypeDeserializer(config, contentType);
        }
        JsonDeserializer<?> deser = this._findCustomMapLikeDeserializer(type, config, beanDesc, keyDes, contentTypeDeser, contentDeser);
        if (deser != null && this._factoryConfig.hasDeserializerModifiers()) {
            for (final BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
                deser = mod.modifyMapLikeDeserializer(config, type, beanDesc, deser);
            }
        }
        return deser;
    }
    
    protected JsonDeserializer<?> _findCustomMapDeserializer(final MapType type, final DeserializationConfig config, final BeanDescription beanDesc, final KeyDeserializer keyDeserializer, final TypeDeserializer elementTypeDeserializer, final JsonDeserializer<?> elementDeserializer) throws JsonMappingException {
        for (final Deserializers d : this._factoryConfig.deserializers()) {
            final JsonDeserializer<?> deser = d.findMapDeserializer(type, config, beanDesc, keyDeserializer, elementTypeDeserializer, elementDeserializer);
            if (deser != null) {
                return deser;
            }
        }
        return null;
    }
    
    protected JsonDeserializer<?> _findCustomMapLikeDeserializer(final MapLikeType type, final DeserializationConfig config, final BeanDescription beanDesc, final KeyDeserializer keyDeserializer, final TypeDeserializer elementTypeDeserializer, final JsonDeserializer<?> elementDeserializer) throws JsonMappingException {
        for (final Deserializers d : this._factoryConfig.deserializers()) {
            final JsonDeserializer<?> deser = d.findMapLikeDeserializer(type, config, beanDesc, keyDeserializer, elementTypeDeserializer, elementDeserializer);
            if (deser != null) {
                return deser;
            }
        }
        return null;
    }
    
    @Override
    public JsonDeserializer<?> createEnumDeserializer(final DeserializationContext ctxt, final JavaType type, final BeanDescription beanDesc) throws JsonMappingException {
        final DeserializationConfig config = ctxt.getConfig();
        final Class<?> enumClass = type.getRawClass();
        JsonDeserializer<?> deser = this._findCustomEnumDeserializer(enumClass, config, beanDesc);
        if (deser == null) {
            for (final AnnotatedMethod factory : beanDesc.getFactoryMethods()) {
                if (ctxt.getAnnotationIntrospector().hasCreatorAnnotation(factory)) {
                    final int argCount = factory.getParameterCount();
                    if (argCount == 1) {
                        final Class<?> returnType = factory.getRawReturnType();
                        if (returnType.isAssignableFrom(enumClass)) {
                            deser = EnumDeserializer.deserializerForCreator(config, enumClass, factory);
                            break;
                        }
                    }
                    throw new IllegalArgumentException("Unsuitable method (" + factory + ") decorated with @JsonCreator (for Enum type " + enumClass.getName() + ")");
                }
            }
            if (deser == null) {
                deser = new EnumDeserializer(this.constructEnumResolver(enumClass, config, beanDesc.findJsonValueMethod()));
            }
        }
        if (this._factoryConfig.hasDeserializerModifiers()) {
            for (final BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
                deser = mod.modifyEnumDeserializer(config, type, beanDesc, deser);
            }
        }
        return deser;
    }
    
    protected JsonDeserializer<?> _findCustomEnumDeserializer(final Class<?> type, final DeserializationConfig config, final BeanDescription beanDesc) throws JsonMappingException {
        for (final Deserializers d : this._factoryConfig.deserializers()) {
            final JsonDeserializer<?> deser = d.findEnumDeserializer(type, config, beanDesc);
            if (deser != null) {
                return deser;
            }
        }
        return null;
    }
    
    @Override
    public JsonDeserializer<?> createTreeDeserializer(final DeserializationConfig config, final JavaType nodeType, final BeanDescription beanDesc) throws JsonMappingException {
        final Class<? extends JsonNode> nodeClass = (Class<? extends JsonNode>)nodeType.getRawClass();
        final JsonDeserializer<?> custom = this._findCustomTreeNodeDeserializer(nodeClass, config, beanDesc);
        if (custom != null) {
            return custom;
        }
        return JsonNodeDeserializer.getDeserializer(nodeClass);
    }
    
    protected JsonDeserializer<?> _findCustomTreeNodeDeserializer(final Class<? extends JsonNode> type, final DeserializationConfig config, final BeanDescription beanDesc) throws JsonMappingException {
        for (final Deserializers d : this._factoryConfig.deserializers()) {
            final JsonDeserializer<?> deser = d.findTreeNodeDeserializer(type, config, beanDesc);
            if (deser != null) {
                return deser;
            }
        }
        return null;
    }
    
    @Override
    public TypeDeserializer findTypeDeserializer(final DeserializationConfig config, final JavaType baseType) throws JsonMappingException {
        final BeanDescription bean = config.introspectClassAnnotations(baseType.getRawClass());
        final AnnotatedClass ac = bean.getClassInfo();
        final AnnotationIntrospector ai = config.getAnnotationIntrospector();
        TypeResolverBuilder<?> b = ai.findTypeResolver(config, ac, baseType);
        Collection<NamedType> subtypes = null;
        if (b == null) {
            b = config.getDefaultTyper(baseType);
            if (b == null) {
                return null;
            }
        }
        else {
            subtypes = config.getSubtypeResolver().collectAndResolveSubtypes(ac, config, ai);
        }
        if (b.getDefaultImpl() == null && baseType.isAbstract()) {
            final JavaType defaultType = this.mapAbstractType(config, baseType);
            if (defaultType != null && defaultType.getRawClass() != baseType.getRawClass()) {
                b = (TypeResolverBuilder<?>)b.defaultImpl(defaultType.getRawClass());
            }
        }
        return b.buildTypeDeserializer(config, baseType, subtypes);
    }
    
    @Override
    public KeyDeserializer createKeyDeserializer(final DeserializationContext ctxt, final JavaType type) throws JsonMappingException {
        final DeserializationConfig config = ctxt.getConfig();
        KeyDeserializer deser = null;
        if (this._factoryConfig.hasKeyDeserializers()) {
            final BeanDescription beanDesc = config.introspectClassAnnotations(type.getRawClass());
            for (final KeyDeserializers d : this._factoryConfig.keyDeserializers()) {
                deser = d.findKeyDeserializer(type, config, beanDesc);
                if (deser != null) {
                    break;
                }
            }
        }
        if (deser == null) {
            if (type.isEnumType()) {
                return this._createEnumKeyDeserializer(ctxt, type);
            }
            deser = StdKeyDeserializers.findStringBasedKeyDeserializer(config, type);
        }
        if (deser != null && this._factoryConfig.hasDeserializerModifiers()) {
            for (final BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
                deser = mod.modifyKeyDeserializer(config, type, deser);
            }
        }
        return deser;
    }
    
    private KeyDeserializer _createEnumKeyDeserializer(final DeserializationContext ctxt, final JavaType type) throws JsonMappingException {
        final DeserializationConfig config = ctxt.getConfig();
        final BeanDescription beanDesc = config.introspect(type);
        final JsonDeserializer<?> des = this.findDeserializerFromAnnotation(ctxt, beanDesc.getClassInfo());
        if (des != null) {
            return StdKeyDeserializers.constructDelegatingKeyDeserializer(config, type, des);
        }
        final Class<?> enumClass = type.getRawClass();
        final JsonDeserializer<?> custom = this._findCustomEnumDeserializer(enumClass, config, beanDesc);
        if (custom != null) {
            return StdKeyDeserializers.constructDelegatingKeyDeserializer(config, type, custom);
        }
        final EnumResolver<?> enumRes = this.constructEnumResolver(enumClass, config, beanDesc.findJsonValueMethod());
        for (final AnnotatedMethod factory : beanDesc.getFactoryMethods()) {
            if (config.getAnnotationIntrospector().hasCreatorAnnotation(factory)) {
                final int argCount = factory.getParameterCount();
                if (argCount == 1) {
                    final Class<?> returnType = factory.getRawReturnType();
                    if (returnType.isAssignableFrom(enumClass)) {
                        if (factory.getGenericParameterType(0) != String.class) {
                            throw new IllegalArgumentException("Parameter #0 type for factory method (" + factory + ") not suitable, must be java.lang.String");
                        }
                        if (config.canOverrideAccessModifiers()) {
                            ClassUtil.checkAndFixAccess(factory.getMember());
                        }
                        return StdKeyDeserializers.constructEnumKeyDeserializer(enumRes, factory);
                    }
                }
                throw new IllegalArgumentException("Unsuitable method (" + factory + ") decorated with @JsonCreator (for Enum type " + enumClass.getName() + ")");
            }
        }
        return StdKeyDeserializers.constructEnumKeyDeserializer(enumRes);
    }
    
    public TypeDeserializer findPropertyTypeDeserializer(final DeserializationConfig config, final JavaType baseType, final AnnotatedMember annotated) throws JsonMappingException {
        final AnnotationIntrospector ai = config.getAnnotationIntrospector();
        final TypeResolverBuilder<?> b = ai.findPropertyTypeResolver(config, annotated, baseType);
        if (b == null) {
            return this.findTypeDeserializer(config, baseType);
        }
        final Collection<NamedType> subtypes = config.getSubtypeResolver().collectAndResolveSubtypes(annotated, config, ai, baseType);
        return b.buildTypeDeserializer(config, baseType, subtypes);
    }
    
    public TypeDeserializer findPropertyContentTypeDeserializer(final DeserializationConfig config, final JavaType containerType, final AnnotatedMember propertyEntity) throws JsonMappingException {
        final AnnotationIntrospector ai = config.getAnnotationIntrospector();
        final TypeResolverBuilder<?> b = ai.findPropertyContentTypeResolver(config, propertyEntity, containerType);
        final JavaType contentType = containerType.getContentType();
        if (b == null) {
            return this.findTypeDeserializer(config, contentType);
        }
        final Collection<NamedType> subtypes = config.getSubtypeResolver().collectAndResolveSubtypes(propertyEntity, config, ai, contentType);
        return b.buildTypeDeserializer(config, contentType, subtypes);
    }
    
    public JsonDeserializer<?> findDefaultDeserializer(final DeserializationContext ctxt, final JavaType type, final BeanDescription beanDesc) throws JsonMappingException {
        final Class<?> rawType = type.getRawClass();
        if (rawType == BasicDeserializerFactory.CLASS_OBJECT) {
            return new UntypedObjectDeserializer();
        }
        if (rawType == BasicDeserializerFactory.CLASS_STRING || rawType == BasicDeserializerFactory.CLASS_CHAR_BUFFER) {
            return StringDeserializer.instance;
        }
        if (rawType == BasicDeserializerFactory.CLASS_ITERABLE) {
            final TypeFactory tf = ctxt.getTypeFactory();
            final JavaType elemType = (type.containedTypeCount() > 0) ? type.containedType(0) : TypeFactory.unknownType();
            final CollectionType ct = tf.constructCollectionType(Collection.class, elemType);
            return this.createCollectionDeserializer(ctxt, ct, beanDesc);
        }
        final String clsName = rawType.getName();
        if (rawType.isPrimitive() || clsName.startsWith("java.")) {
            JsonDeserializer<?> deser = NumberDeserializers.find(rawType, clsName);
            if (deser == null) {
                deser = DateDeserializers.find(rawType, clsName);
            }
            if (deser != null) {
                return deser;
            }
        }
        if (rawType == TokenBuffer.class) {
            return new TokenBufferDeserializer();
        }
        return JdkDeserializers.find(rawType, clsName);
    }
    
    protected JsonDeserializer<Object> findDeserializerFromAnnotation(final DeserializationContext ctxt, final Annotated ann) throws JsonMappingException {
        final Object deserDef = ctxt.getAnnotationIntrospector().findDeserializer(ann);
        if (deserDef == null) {
            return null;
        }
        return ctxt.deserializerInstance(ann, deserDef);
    }
    
    protected <T extends JavaType> T modifyTypeByAnnotation(final DeserializationContext ctxt, final Annotated a, T type) throws JsonMappingException {
        final AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
        final Class<?> subclass = intr.findDeserializationType(a, type);
        if (subclass != null) {
            try {
                type = (T)type.narrowBy(subclass);
            }
            catch (IllegalArgumentException iae) {
                throw new JsonMappingException("Failed to narrow type " + type + " with concrete-type annotation (value " + subclass.getName() + "), method '" + a.getName() + "': " + iae.getMessage(), null, iae);
            }
        }
        if (type.isContainerType()) {
            final Class<?> keyClass = intr.findDeserializationKeyType(a, type.getKeyType());
            if (keyClass != null) {
                if (!(type instanceof MapLikeType)) {
                    throw new JsonMappingException("Illegal key-type annotation: type " + type + " is not a Map(-like) type");
                }
                try {
                    type = (T)((MapLikeType)type).narrowKey(keyClass);
                }
                catch (IllegalArgumentException iae2) {
                    throw new JsonMappingException("Failed to narrow key type " + type + " with key-type annotation (" + keyClass.getName() + "): " + iae2.getMessage(), null, iae2);
                }
            }
            JavaType keyType = type.getKeyType();
            if (keyType != null && keyType.getValueHandler() == null) {
                final Object kdDef = intr.findKeyDeserializer(a);
                final KeyDeserializer kd = ctxt.keyDeserializerInstance(a, kdDef);
                if (kd != null) {
                    type = (T)((MapLikeType)type).withKeyValueHandler(kd);
                    keyType = type.getKeyType();
                }
            }
            final Class<?> cc = intr.findDeserializationContentType(a, type.getContentType());
            if (cc != null) {
                try {
                    type = (T)type.narrowContentsBy(cc);
                }
                catch (IllegalArgumentException iae3) {
                    throw new JsonMappingException("Failed to narrow content type " + type + " with content-type annotation (" + cc.getName() + "): " + iae3.getMessage(), null, iae3);
                }
            }
            final JavaType contentType = type.getContentType();
            if (contentType.getValueHandler() == null) {
                final Object cdDef = intr.findContentDeserializer(a);
                final JsonDeserializer<?> cd = ctxt.deserializerInstance(a, cdDef);
                if (cd != null) {
                    type = (T)type.withContentValueHandler(cd);
                }
            }
        }
        return type;
    }
    
    protected JavaType resolveType(final DeserializationContext ctxt, final BeanDescription beanDesc, JavaType type, final AnnotatedMember member) throws JsonMappingException {
        if (type.isContainerType()) {
            final AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
            JavaType keyType = type.getKeyType();
            if (keyType != null) {
                final Object kdDef = intr.findKeyDeserializer(member);
                final KeyDeserializer kd = ctxt.keyDeserializerInstance(member, kdDef);
                if (kd != null) {
                    type = ((MapLikeType)type).withKeyValueHandler(kd);
                    keyType = type.getKeyType();
                }
            }
            final Object cdDef = intr.findContentDeserializer(member);
            final JsonDeserializer<?> cd = ctxt.deserializerInstance(member, cdDef);
            if (cd != null) {
                type = type.withContentValueHandler(cd);
            }
            if (member instanceof AnnotatedMember) {
                final TypeDeserializer contentTypeDeser = this.findPropertyContentTypeDeserializer(ctxt.getConfig(), type, member);
                if (contentTypeDeser != null) {
                    type = type.withContentTypeHandler(contentTypeDeser);
                }
            }
        }
        TypeDeserializer valueTypeDeser;
        if (member instanceof AnnotatedMember) {
            valueTypeDeser = this.findPropertyTypeDeserializer(ctxt.getConfig(), type, member);
        }
        else {
            valueTypeDeser = this.findTypeDeserializer(ctxt.getConfig(), type);
        }
        if (valueTypeDeser != null) {
            type = type.withTypeHandler(valueTypeDeser);
        }
        return type;
    }
    
    protected EnumResolver<?> constructEnumResolver(final Class<?> enumClass, final DeserializationConfig config, final AnnotatedMethod jsonValueMethod) {
        if (jsonValueMethod != null) {
            final Method accessor = jsonValueMethod.getAnnotated();
            if (config.canOverrideAccessModifiers()) {
                ClassUtil.checkAndFixAccess(accessor);
            }
            return EnumResolver.constructUnsafeUsingMethod(enumClass, accessor);
        }
        if (config.isEnabled(DeserializationFeature.READ_ENUMS_USING_TO_STRING)) {
            return EnumResolver.constructUnsafeUsingToString(enumClass);
        }
        return EnumResolver.constructUnsafe(enumClass, config.getAnnotationIntrospector());
    }
    
    protected AnnotatedMethod _findJsonValueFor(final DeserializationConfig config, final JavaType enumType) {
        if (enumType == null) {
            return null;
        }
        final BeanDescription beanDesc = config.introspect(enumType);
        return beanDesc.findJsonValueMethod();
    }
    
    static {
        CLASS_OBJECT = Object.class;
        CLASS_STRING = String.class;
        CLASS_CHAR_BUFFER = CharSequence.class;
        CLASS_ITERABLE = Iterable.class;
        UNWRAPPED_CREATOR_PARAM_NAME = new PropertyName("@JsonUnwrapped");
        (_mapFallbacks = new HashMap<String, Class<? extends Map>>()).put(Map.class.getName(), LinkedHashMap.class);
        BasicDeserializerFactory._mapFallbacks.put(ConcurrentMap.class.getName(), ConcurrentHashMap.class);
        BasicDeserializerFactory._mapFallbacks.put(SortedMap.class.getName(), TreeMap.class);
        BasicDeserializerFactory._mapFallbacks.put("java.util.NavigableMap", TreeMap.class);
        try {
            final Class<?> key = ConcurrentNavigableMap.class;
            final Class<? extends Map<?, ?>> mapValue;
            final Class<?> value = mapValue = (Class<? extends Map<?, ?>>)ConcurrentSkipListMap.class;
            BasicDeserializerFactory._mapFallbacks.put(key.getName(), mapValue);
        }
        catch (Throwable e) {
            System.err.println("Problems with (optional) types: " + e);
        }
        (_collectionFallbacks = new HashMap<String, Class<? extends Collection>>()).put(Collection.class.getName(), ArrayList.class);
        BasicDeserializerFactory._collectionFallbacks.put(List.class.getName(), ArrayList.class);
        BasicDeserializerFactory._collectionFallbacks.put(Set.class.getName(), HashSet.class);
        BasicDeserializerFactory._collectionFallbacks.put(SortedSet.class.getName(), TreeSet.class);
        BasicDeserializerFactory._collectionFallbacks.put(Queue.class.getName(), LinkedList.class);
        BasicDeserializerFactory._collectionFallbacks.put("java.util.Deque", LinkedList.class);
        BasicDeserializerFactory._collectionFallbacks.put("java.util.NavigableSet", TreeSet.class);
    }
}
