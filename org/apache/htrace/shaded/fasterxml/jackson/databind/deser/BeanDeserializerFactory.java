// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.deser;

import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.impl.SetterlessProperty;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.impl.FieldProperty;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.AnnotatedField;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.impl.MethodProperty;
import org.apache.htrace.shaded.fasterxml.jackson.databind.BeanProperty;
import org.apache.htrace.shaded.fasterxml.jackson.databind.PropertyMetadata;
import java.util.HashMap;
import java.util.ArrayList;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;
import org.apache.htrace.shaded.fasterxml.jackson.databind.AnnotationIntrospector;
import java.util.Map;
import java.util.Collection;
import org.apache.htrace.shaded.fasterxml.jackson.databind.MapperFeature;
import org.apache.htrace.shaded.fasterxml.jackson.databind.util.ArrayBuilders;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.std.ThrowableDeserializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.AnnotatedMember;
import org.apache.htrace.shaded.fasterxml.jackson.databind.cfg.MapperConfig;
import org.apache.htrace.shaded.fasterxml.jackson.databind.util.SimpleBeanPropertyDefinition;
import org.apache.htrace.shaded.fasterxml.jackson.databind.PropertyName;
import org.apache.htrace.shaded.fasterxml.jackson.annotation.ObjectIdResolver;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.ObjectIdInfo;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.impl.ObjectIdReader;
import org.apache.htrace.shaded.fasterxml.jackson.annotation.ObjectIdGenerator;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.impl.PropertyBasedObjectIdGenerator;
import org.apache.htrace.shaded.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import org.apache.htrace.shaded.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import java.lang.reflect.Member;
import org.apache.htrace.shaded.fasterxml.jackson.databind.util.ClassUtil;
import org.apache.htrace.shaded.fasterxml.jackson.databind.AbstractTypeResolver;
import org.apache.htrace.shaded.fasterxml.jackson.databind.ext.OptionalHandlerFactory;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.std.AtomicReferenceDeserializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.Annotated;
import org.apache.htrace.shaded.fasterxml.jackson.databind.type.TypeFactory;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.htrace.shaded.fasterxml.jackson.databind.DeserializationContext;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonMappingException;
import java.util.Iterator;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonDeserializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.BeanDescription;
import org.apache.htrace.shaded.fasterxml.jackson.databind.DeserializationConfig;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JavaType;
import org.apache.htrace.shaded.fasterxml.jackson.databind.cfg.DeserializerFactoryConfig;
import java.io.Serializable;

public class BeanDeserializerFactory extends BasicDeserializerFactory implements Serializable
{
    private static final long serialVersionUID = 1L;
    private static final Class<?>[] INIT_CAUSE_PARAMS;
    private static final Class<?>[] NO_VIEWS;
    public static final BeanDeserializerFactory instance;
    
    public BeanDeserializerFactory(final DeserializerFactoryConfig config) {
        super(config);
    }
    
    public DeserializerFactory withConfig(final DeserializerFactoryConfig config) {
        if (this._factoryConfig == config) {
            return this;
        }
        if (this.getClass() != BeanDeserializerFactory.class) {
            throw new IllegalStateException("Subtype of BeanDeserializerFactory (" + this.getClass().getName() + ") has not properly overridden method 'withAdditionalDeserializers': can not instantiate subtype with " + "additional deserializer definitions");
        }
        return new BeanDeserializerFactory(config);
    }
    
    protected JsonDeserializer<Object> _findCustomBeanDeserializer(final JavaType type, final DeserializationConfig config, final BeanDescription beanDesc) throws JsonMappingException {
        for (final Deserializers d : this._factoryConfig.deserializers()) {
            final JsonDeserializer<?> deser = d.findBeanDeserializer(type, config, beanDesc);
            if (deser != null) {
                return (JsonDeserializer<Object>)deser;
            }
        }
        return null;
    }
    
    @Override
    public JsonDeserializer<Object> createBeanDeserializer(final DeserializationContext ctxt, final JavaType type, BeanDescription beanDesc) throws JsonMappingException {
        final DeserializationConfig config = ctxt.getConfig();
        final JsonDeserializer<Object> custom = this._findCustomBeanDeserializer(type, config, beanDesc);
        if (custom != null) {
            return custom;
        }
        if (type.isThrowable()) {
            return this.buildThrowableDeserializer(ctxt, type, beanDesc);
        }
        if (type.isAbstract()) {
            final JavaType concreteType = this.materializeAbstractType(ctxt, type, beanDesc);
            if (concreteType != null) {
                beanDesc = config.introspect(concreteType);
                return this.buildBeanDeserializer(ctxt, concreteType, beanDesc);
            }
        }
        final JsonDeserializer<Object> deser = (JsonDeserializer<Object>)this.findStdDeserializer(ctxt, type, beanDesc);
        if (deser != null) {
            return deser;
        }
        if (!this.isPotentialBeanType(type.getRawClass())) {
            return null;
        }
        return this.buildBeanDeserializer(ctxt, type, beanDesc);
    }
    
    @Override
    public JsonDeserializer<Object> createBuilderBasedDeserializer(final DeserializationContext ctxt, final JavaType valueType, final BeanDescription beanDesc, final Class<?> builderClass) throws JsonMappingException {
        final JavaType builderType = ctxt.constructType(builderClass);
        final BeanDescription builderDesc = ctxt.getConfig().introspectForBuilder(builderType);
        return this.buildBuilderBasedDeserializer(ctxt, valueType, builderDesc);
    }
    
    protected JsonDeserializer<?> findStdDeserializer(final DeserializationContext ctxt, final JavaType type, final BeanDescription beanDesc) throws JsonMappingException {
        JsonDeserializer<?> deser = this.findDefaultDeserializer(ctxt, type, beanDesc);
        if (deser != null) {
            return deser;
        }
        final Class<?> cls = type.getRawClass();
        if (AtomicReference.class.isAssignableFrom(cls)) {
            final TypeFactory tf = ctxt.getTypeFactory();
            final JavaType[] params = tf.findTypeParameters(type, AtomicReference.class);
            JavaType referencedType;
            if (params == null || params.length < 1) {
                referencedType = TypeFactory.unknownType();
            }
            else {
                referencedType = params[0];
            }
            final TypeDeserializer valueTypeDeser = this.findTypeDeserializer(ctxt.getConfig(), referencedType);
            final BeanDescription refdDesc = ctxt.getConfig().introspectClassAnnotations(referencedType);
            deser = this.findDeserializerFromAnnotation(ctxt, refdDesc.getClassInfo());
            return new AtomicReferenceDeserializer(referencedType, valueTypeDeser, deser);
        }
        return this.findOptionalStdDeserializer(ctxt, type, beanDesc);
    }
    
    protected JsonDeserializer<?> findOptionalStdDeserializer(final DeserializationContext ctxt, final JavaType type, final BeanDescription beanDesc) throws JsonMappingException {
        return OptionalHandlerFactory.instance.findDeserializer(type, ctxt.getConfig(), beanDesc);
    }
    
    protected JavaType materializeAbstractType(final DeserializationContext ctxt, final JavaType type, final BeanDescription beanDesc) throws JsonMappingException {
        final JavaType abstractType = beanDesc.getType();
        for (final AbstractTypeResolver r : this._factoryConfig.abstractTypeResolvers()) {
            final JavaType concrete = r.resolveAbstractType(ctxt.getConfig(), abstractType);
            if (concrete != null) {
                return concrete;
            }
        }
        return null;
    }
    
    public JsonDeserializer<Object> buildBeanDeserializer(final DeserializationContext ctxt, final JavaType type, final BeanDescription beanDesc) throws JsonMappingException {
        final ValueInstantiator valueInstantiator = this.findValueInstantiator(ctxt, beanDesc);
        BeanDeserializerBuilder builder = this.constructBeanDeserializerBuilder(ctxt, beanDesc);
        builder.setValueInstantiator(valueInstantiator);
        this.addBeanProps(ctxt, beanDesc, builder);
        this.addObjectIdReader(ctxt, beanDesc, builder);
        this.addReferenceProperties(ctxt, beanDesc, builder);
        this.addInjectables(ctxt, beanDesc, builder);
        final DeserializationConfig config = ctxt.getConfig();
        if (this._factoryConfig.hasDeserializerModifiers()) {
            for (final BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
                builder = mod.updateBuilder(config, beanDesc, builder);
            }
        }
        JsonDeserializer<?> deserializer;
        if (type.isAbstract() && !valueInstantiator.canInstantiate()) {
            deserializer = builder.buildAbstract();
        }
        else {
            deserializer = builder.build();
        }
        if (this._factoryConfig.hasDeserializerModifiers()) {
            for (final BeanDeserializerModifier mod2 : this._factoryConfig.deserializerModifiers()) {
                deserializer = mod2.modifyDeserializer(config, beanDesc, deserializer);
            }
        }
        return (JsonDeserializer<Object>)deserializer;
    }
    
    protected JsonDeserializer<Object> buildBuilderBasedDeserializer(final DeserializationContext ctxt, final JavaType valueType, final BeanDescription builderDesc) throws JsonMappingException {
        final ValueInstantiator valueInstantiator = this.findValueInstantiator(ctxt, builderDesc);
        final DeserializationConfig config = ctxt.getConfig();
        BeanDeserializerBuilder builder = this.constructBeanDeserializerBuilder(ctxt, builderDesc);
        builder.setValueInstantiator(valueInstantiator);
        this.addBeanProps(ctxt, builderDesc, builder);
        this.addObjectIdReader(ctxt, builderDesc, builder);
        this.addReferenceProperties(ctxt, builderDesc, builder);
        this.addInjectables(ctxt, builderDesc, builder);
        final JsonPOJOBuilder.Value builderConfig = builderDesc.findPOJOBuilderConfig();
        final String buildMethodName = (builderConfig == null) ? "build" : builderConfig.buildMethodName;
        final AnnotatedMethod buildMethod = builderDesc.findMethod(buildMethodName, null);
        if (buildMethod != null && config.canOverrideAccessModifiers()) {
            ClassUtil.checkAndFixAccess(buildMethod.getMember());
        }
        builder.setPOJOBuilder(buildMethod, builderConfig);
        if (this._factoryConfig.hasDeserializerModifiers()) {
            for (final BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
                builder = mod.updateBuilder(config, builderDesc, builder);
            }
        }
        JsonDeserializer<?> deserializer = builder.buildBuilderBased(valueType, buildMethodName);
        if (this._factoryConfig.hasDeserializerModifiers()) {
            for (final BeanDeserializerModifier mod2 : this._factoryConfig.deserializerModifiers()) {
                deserializer = mod2.modifyDeserializer(config, builderDesc, deserializer);
            }
        }
        return (JsonDeserializer<Object>)deserializer;
    }
    
    protected void addObjectIdReader(final DeserializationContext ctxt, final BeanDescription beanDesc, final BeanDeserializerBuilder builder) throws JsonMappingException {
        final ObjectIdInfo objectIdInfo = beanDesc.getObjectIdInfo();
        if (objectIdInfo == null) {
            return;
        }
        final Class<?> implClass = objectIdInfo.getGeneratorType();
        final ObjectIdResolver resolver = ctxt.objectIdResolverInstance(beanDesc.getClassInfo(), objectIdInfo);
        SettableBeanProperty idProp;
        JavaType idType;
        ObjectIdGenerator<?> gen;
        if (implClass == ObjectIdGenerators.PropertyGenerator.class) {
            final PropertyName propName = objectIdInfo.getPropertyName();
            idProp = builder.findProperty(propName);
            if (idProp == null) {
                throw new IllegalArgumentException("Invalid Object Id definition for " + beanDesc.getBeanClass().getName() + ": can not find property with name '" + propName + "'");
            }
            idType = idProp.getType();
            gen = new PropertyBasedObjectIdGenerator(objectIdInfo.getScope());
        }
        else {
            final JavaType type = ctxt.constructType(implClass);
            idType = ctxt.getTypeFactory().findTypeParameters(type, ObjectIdGenerator.class)[0];
            idProp = null;
            gen = ctxt.objectIdGeneratorInstance(beanDesc.getClassInfo(), objectIdInfo);
        }
        final JsonDeserializer<?> deser = ctxt.findRootValueDeserializer(idType);
        builder.setObjectIdReader(ObjectIdReader.construct(idType, objectIdInfo.getPropertyName(), gen, deser, idProp, resolver));
    }
    
    public JsonDeserializer<Object> buildThrowableDeserializer(final DeserializationContext ctxt, final JavaType type, final BeanDescription beanDesc) throws JsonMappingException {
        final DeserializationConfig config = ctxt.getConfig();
        BeanDeserializerBuilder builder = this.constructBeanDeserializerBuilder(ctxt, beanDesc);
        builder.setValueInstantiator(this.findValueInstantiator(ctxt, beanDesc));
        this.addBeanProps(ctxt, beanDesc, builder);
        final AnnotatedMethod am = beanDesc.findMethod("initCause", BeanDeserializerFactory.INIT_CAUSE_PARAMS);
        if (am != null) {
            final SimpleBeanPropertyDefinition propDef = SimpleBeanPropertyDefinition.construct(ctxt.getConfig(), am, "cause");
            final SettableBeanProperty prop = this.constructSettableProperty(ctxt, beanDesc, propDef, am.getGenericParameterType(0));
            if (prop != null) {
                builder.addOrReplaceProperty(prop, true);
            }
        }
        builder.addIgnorable("localizedMessage");
        builder.addIgnorable("suppressed");
        builder.addIgnorable("message");
        if (this._factoryConfig.hasDeserializerModifiers()) {
            for (final BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
                builder = mod.updateBuilder(config, beanDesc, builder);
            }
        }
        JsonDeserializer<?> deserializer = builder.build();
        if (deserializer instanceof BeanDeserializer) {
            deserializer = new ThrowableDeserializer((BeanDeserializer)deserializer);
        }
        if (this._factoryConfig.hasDeserializerModifiers()) {
            for (final BeanDeserializerModifier mod2 : this._factoryConfig.deserializerModifiers()) {
                deserializer = mod2.modifyDeserializer(config, beanDesc, deserializer);
            }
        }
        return (JsonDeserializer<Object>)deserializer;
    }
    
    protected BeanDeserializerBuilder constructBeanDeserializerBuilder(final DeserializationContext ctxt, final BeanDescription beanDesc) {
        return new BeanDeserializerBuilder(beanDesc, ctxt.getConfig());
    }
    
    protected void addBeanProps(final DeserializationContext ctxt, final BeanDescription beanDesc, final BeanDeserializerBuilder builder) throws JsonMappingException {
        final SettableBeanProperty[] creatorProps = builder.getValueInstantiator().getFromObjectArguments(ctxt.getConfig());
        final AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
        boolean ignoreAny = false;
        final Boolean B = intr.findIgnoreUnknownProperties(beanDesc.getClassInfo());
        if (B != null) {
            ignoreAny = B;
            builder.setIgnoreUnknownProperties(ignoreAny);
        }
        final Set<String> ignored = ArrayBuilders.arrayToSet(intr.findPropertiesToIgnore(beanDesc.getClassInfo()));
        for (final String propName : ignored) {
            builder.addIgnorable(propName);
        }
        final AnnotatedMethod anySetter = beanDesc.findAnySetter();
        if (anySetter != null) {
            builder.setAnySetter(this.constructAnySetter(ctxt, beanDesc, anySetter));
        }
        if (anySetter == null) {
            final Collection<String> ignored2 = beanDesc.getIgnoredPropertyNames();
            if (ignored2 != null) {
                for (final String propName2 : ignored2) {
                    builder.addIgnorable(propName2);
                }
            }
        }
        final boolean useGettersAsSetters = ctxt.isEnabled(MapperFeature.USE_GETTERS_AS_SETTERS) && ctxt.isEnabled(MapperFeature.AUTO_DETECT_GETTERS);
        List<BeanPropertyDefinition> propDefs = this.filterBeanProps(ctxt, beanDesc, builder, beanDesc.findProperties(), ignored);
        if (this._factoryConfig.hasDeserializerModifiers()) {
            for (final BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
                propDefs = mod.updateProperties(ctxt.getConfig(), beanDesc, propDefs);
            }
        }
        for (final BeanPropertyDefinition propDef : propDefs) {
            SettableBeanProperty prop = null;
            if (propDef.hasSetter()) {
                final Type propertyType = propDef.getSetter().getGenericParameterType(0);
                prop = this.constructSettableProperty(ctxt, beanDesc, propDef, propertyType);
            }
            else if (propDef.hasField()) {
                final Type propertyType = propDef.getField().getGenericType();
                prop = this.constructSettableProperty(ctxt, beanDesc, propDef, propertyType);
            }
            else if (useGettersAsSetters && propDef.hasGetter()) {
                final AnnotatedMethod getter = propDef.getGetter();
                final Class<?> rawPropertyType = getter.getRawType();
                if (Collection.class.isAssignableFrom(rawPropertyType) || Map.class.isAssignableFrom(rawPropertyType)) {
                    prop = this.constructSetterlessProperty(ctxt, beanDesc, propDef);
                }
            }
            if (propDef.hasConstructorParameter()) {
                final String name = propDef.getName();
                CreatorProperty cprop = null;
                if (creatorProps != null) {
                    for (final SettableBeanProperty cp : creatorProps) {
                        if (name.equals(cp.getName())) {
                            cprop = (CreatorProperty)cp;
                            break;
                        }
                    }
                }
                if (cprop == null) {
                    throw ctxt.mappingException("Could not find creator property with name '" + name + "' (in class " + beanDesc.getBeanClass().getName() + ")");
                }
                if (prop != null) {
                    cprop = cprop.withFallbackSetter(prop);
                }
                prop = cprop;
                builder.addCreatorProperty(cprop);
            }
            else {
                if (prop == null) {
                    continue;
                }
                Class<?>[] views = propDef.findViews();
                if (views == null && !ctxt.isEnabled(MapperFeature.DEFAULT_VIEW_INCLUSION)) {
                    views = BeanDeserializerFactory.NO_VIEWS;
                }
                prop.setViews(views);
                builder.addProperty(prop);
            }
        }
    }
    
    protected List<BeanPropertyDefinition> filterBeanProps(final DeserializationContext ctxt, final BeanDescription beanDesc, final BeanDeserializerBuilder builder, final List<BeanPropertyDefinition> propDefsIn, final Set<String> ignored) throws JsonMappingException {
        final ArrayList<BeanPropertyDefinition> result = new ArrayList<BeanPropertyDefinition>(Math.max(4, propDefsIn.size()));
        final HashMap<Class<?>, Boolean> ignoredTypes = new HashMap<Class<?>, Boolean>();
        for (final BeanPropertyDefinition property : propDefsIn) {
            final String name = property.getName();
            if (ignored.contains(name)) {
                continue;
            }
            if (!property.hasConstructorParameter()) {
                Class<?> rawPropertyType = null;
                if (property.hasSetter()) {
                    rawPropertyType = property.getSetter().getRawParameterType(0);
                }
                else if (property.hasField()) {
                    rawPropertyType = property.getField().getRawType();
                }
                if (rawPropertyType != null && this.isIgnorableType(ctxt.getConfig(), beanDesc, rawPropertyType, ignoredTypes)) {
                    builder.addIgnorable(name);
                    continue;
                }
            }
            result.add(property);
        }
        return result;
    }
    
    protected void addReferenceProperties(final DeserializationContext ctxt, final BeanDescription beanDesc, final BeanDeserializerBuilder builder) throws JsonMappingException {
        final Map<String, AnnotatedMember> refs = beanDesc.findBackReferenceProperties();
        if (refs != null) {
            for (final Map.Entry<String, AnnotatedMember> en : refs.entrySet()) {
                final String name = en.getKey();
                final AnnotatedMember m = en.getValue();
                Type genericType;
                if (m instanceof AnnotatedMethod) {
                    genericType = ((AnnotatedMethod)m).getGenericParameterType(0);
                }
                else {
                    genericType = m.getRawType();
                }
                final SimpleBeanPropertyDefinition propDef = SimpleBeanPropertyDefinition.construct(ctxt.getConfig(), m);
                builder.addBackReferenceProperty(name, this.constructSettableProperty(ctxt, beanDesc, propDef, genericType));
            }
        }
    }
    
    protected void addInjectables(final DeserializationContext ctxt, final BeanDescription beanDesc, final BeanDeserializerBuilder builder) throws JsonMappingException {
        final Map<Object, AnnotatedMember> raw = beanDesc.findInjectables();
        if (raw != null) {
            final boolean fixAccess = ctxt.canOverrideAccessModifiers();
            for (final Map.Entry<Object, AnnotatedMember> entry : raw.entrySet()) {
                final AnnotatedMember m = entry.getValue();
                if (fixAccess) {
                    m.fixAccess();
                }
                builder.addInjectable(new PropertyName(m.getName()), beanDesc.resolveType(m.getGenericType()), beanDesc.getClassAnnotations(), m, entry.getKey());
            }
        }
    }
    
    protected SettableAnyProperty constructAnySetter(final DeserializationContext ctxt, final BeanDescription beanDesc, final AnnotatedMethod setter) throws JsonMappingException {
        if (ctxt.canOverrideAccessModifiers()) {
            setter.fixAccess();
        }
        JavaType type = beanDesc.bindingsForBeanType().resolveType(setter.getGenericParameterType(1));
        final BeanProperty.Std property = new BeanProperty.Std(new PropertyName(setter.getName()), type, null, beanDesc.getClassAnnotations(), setter, PropertyMetadata.STD_OPTIONAL);
        type = this.resolveType(ctxt, beanDesc, type, setter);
        JsonDeserializer<Object> deser = this.findDeserializerFromAnnotation(ctxt, setter);
        type = this.modifyTypeByAnnotation(ctxt, setter, type);
        if (deser == null) {
            deser = type.getValueHandler();
        }
        final TypeDeserializer typeDeser = type.getTypeHandler();
        return new SettableAnyProperty(property, setter, type, deser, typeDeser);
    }
    
    protected SettableBeanProperty constructSettableProperty(final DeserializationContext ctxt, final BeanDescription beanDesc, final BeanPropertyDefinition propDef, final Type jdkType) throws JsonMappingException {
        final AnnotatedMember mutator = propDef.getNonConstructorMutator();
        if (ctxt.canOverrideAccessModifiers()) {
            mutator.fixAccess();
        }
        final JavaType t0 = beanDesc.resolveType(jdkType);
        BeanProperty.Std property = new BeanProperty.Std(propDef.getFullName(), t0, propDef.getWrapperName(), beanDesc.getClassAnnotations(), mutator, propDef.getMetadata());
        JavaType type = this.resolveType(ctxt, beanDesc, t0, mutator);
        if (type != t0) {
            property = property.withType(type);
        }
        final JsonDeserializer<Object> propDeser = this.findDeserializerFromAnnotation(ctxt, mutator);
        type = this.modifyTypeByAnnotation(ctxt, mutator, type);
        final TypeDeserializer typeDeser = type.getTypeHandler();
        SettableBeanProperty prop;
        if (mutator instanceof AnnotatedMethod) {
            prop = new MethodProperty(propDef, type, typeDeser, beanDesc.getClassAnnotations(), (AnnotatedMethod)mutator);
        }
        else {
            prop = new FieldProperty(propDef, type, typeDeser, beanDesc.getClassAnnotations(), (AnnotatedField)mutator);
        }
        if (propDeser != null) {
            prop = prop.withValueDeserializer(propDeser);
        }
        final AnnotationIntrospector.ReferenceProperty ref = propDef.findReferenceType();
        if (ref != null && ref.isManagedReference()) {
            prop.setManagedReferenceName(ref.getName());
        }
        final ObjectIdInfo objectIdInfo = propDef.findObjectIdInfo();
        if (objectIdInfo != null) {
            prop.setObjectIdInfo(objectIdInfo);
        }
        return prop;
    }
    
    protected SettableBeanProperty constructSetterlessProperty(final DeserializationContext ctxt, final BeanDescription beanDesc, final BeanPropertyDefinition propDef) throws JsonMappingException {
        final AnnotatedMethod getter = propDef.getGetter();
        if (ctxt.canOverrideAccessModifiers()) {
            getter.fixAccess();
        }
        JavaType type = getter.getType(beanDesc.bindingsForBeanType());
        final JsonDeserializer<Object> propDeser = this.findDeserializerFromAnnotation(ctxt, getter);
        type = this.modifyTypeByAnnotation(ctxt, getter, type);
        final TypeDeserializer typeDeser = type.getTypeHandler();
        SettableBeanProperty prop = new SetterlessProperty(propDef, type, typeDeser, beanDesc.getClassAnnotations(), getter);
        if (propDeser != null) {
            prop = prop.withValueDeserializer(propDeser);
        }
        return prop;
    }
    
    protected boolean isPotentialBeanType(final Class<?> type) {
        String typeStr = ClassUtil.canBeABeanType(type);
        if (typeStr != null) {
            throw new IllegalArgumentException("Can not deserialize Class " + type.getName() + " (of type " + typeStr + ") as a Bean");
        }
        if (ClassUtil.isProxyType(type)) {
            throw new IllegalArgumentException("Can not deserialize Proxy class " + type.getName() + " as a Bean");
        }
        typeStr = ClassUtil.isLocalType(type, true);
        if (typeStr != null) {
            throw new IllegalArgumentException("Can not deserialize Class " + type.getName() + " (of type " + typeStr + ") as a Bean");
        }
        return true;
    }
    
    protected boolean isIgnorableType(final DeserializationConfig config, final BeanDescription beanDesc, final Class<?> type, final Map<Class<?>, Boolean> ignoredTypes) {
        Boolean status = ignoredTypes.get(type);
        if (status == null) {
            final BeanDescription desc = config.introspectClassAnnotations(type);
            status = config.getAnnotationIntrospector().isIgnorableType(desc.getClassInfo());
            if (status == null) {
                status = Boolean.FALSE;
            }
        }
        return status;
    }
    
    static {
        INIT_CAUSE_PARAMS = new Class[] { Throwable.class };
        NO_VIEWS = new Class[0];
        instance = new BeanDeserializerFactory(new DeserializerFactoryConfig());
    }
}
