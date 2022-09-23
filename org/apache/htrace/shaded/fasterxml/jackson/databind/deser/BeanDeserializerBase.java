// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.deser;

import java.lang.reflect.InvocationTargetException;
import org.apache.htrace.shaded.fasterxml.jackson.databind.exc.IgnoredPropertyException;
import org.apache.htrace.shaded.fasterxml.jackson.databind.DeserializationFeature;
import org.apache.htrace.shaded.fasterxml.jackson.databind.util.TokenBuffer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.impl.ReadableObjectId;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonToken;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonParser;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Collection;
import java.lang.reflect.Constructor;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.impl.InnerClassProperty;
import java.lang.reflect.Member;
import org.apache.htrace.shaded.fasterxml.jackson.databind.util.ClassUtil;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.impl.ObjectIdReferenceProperty;
import org.apache.htrace.shaded.fasterxml.jackson.annotation.ObjectIdResolver;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.ObjectIdInfo;
import java.util.Set;
import org.apache.htrace.shaded.fasterxml.jackson.databind.util.ArrayBuilders;
import org.apache.htrace.shaded.fasterxml.jackson.annotation.ObjectIdGenerator;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.impl.PropertyBasedObjectIdGenerator;
import org.apache.htrace.shaded.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.apache.htrace.shaded.fasterxml.jackson.databind.util.Converter;
import org.apache.htrace.shaded.fasterxml.jackson.databind.AnnotationIntrospector;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.std.StdDelegatingDeserializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.Annotated;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonMappingException;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.AnnotatedWithParams;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import java.util.Iterator;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.AnnotatedMember;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.impl.ManagedReferenceProperty;
import org.apache.htrace.shaded.fasterxml.jackson.databind.BeanProperty;
import org.apache.htrace.shaded.fasterxml.jackson.annotation.JsonTypeInfo;
import org.apache.htrace.shaded.fasterxml.jackson.databind.DeserializationContext;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.impl.ObjectIdValueProperty;
import org.apache.htrace.shaded.fasterxml.jackson.databind.PropertyMetadata;
import org.apache.htrace.shaded.fasterxml.jackson.databind.util.NameTransformer;
import java.util.List;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.AnnotatedClass;
import org.apache.htrace.shaded.fasterxml.jackson.databind.BeanDescription;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.impl.ObjectIdReader;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.impl.ExternalTypeHandler;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.impl.UnwrappedPropertyHandler;
import org.apache.htrace.shaded.fasterxml.jackson.databind.type.ClassKey;
import java.util.HashMap;
import java.util.Map;
import java.util.HashSet;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.impl.ValueInjector;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.impl.BeanPropertyMap;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.impl.PropertyBasedCreator;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonDeserializer;
import org.apache.htrace.shaded.fasterxml.jackson.annotation.JsonFormat;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JavaType;
import org.apache.htrace.shaded.fasterxml.jackson.databind.util.Annotations;
import org.apache.htrace.shaded.fasterxml.jackson.databind.PropertyName;
import java.io.Serializable;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.std.StdDeserializer;

public abstract class BeanDeserializerBase extends StdDeserializer<Object> implements ContextualDeserializer, ResolvableDeserializer, Serializable
{
    private static final long serialVersionUID = 2960120955735322578L;
    protected static final PropertyName TEMP_PROPERTY_NAME;
    private final transient Annotations _classAnnotations;
    protected final JavaType _beanType;
    protected final JsonFormat.Shape _serializationShape;
    protected final ValueInstantiator _valueInstantiator;
    protected JsonDeserializer<Object> _delegateDeserializer;
    protected PropertyBasedCreator _propertyBasedCreator;
    protected boolean _nonStandardCreation;
    protected boolean _vanillaProcessing;
    protected final BeanPropertyMap _beanProperties;
    protected final ValueInjector[] _injectables;
    protected SettableAnyProperty _anySetter;
    protected final HashSet<String> _ignorableProps;
    protected final boolean _ignoreAllUnknown;
    protected final boolean _needViewProcesing;
    protected final Map<String, SettableBeanProperty> _backRefs;
    protected transient HashMap<ClassKey, JsonDeserializer<Object>> _subDeserializers;
    protected UnwrappedPropertyHandler _unwrappedPropertyHandler;
    protected ExternalTypeHandler _externalTypeIdHandler;
    protected final ObjectIdReader _objectIdReader;
    
    protected BeanDeserializerBase(final BeanDeserializerBuilder builder, final BeanDescription beanDesc, final BeanPropertyMap properties, final Map<String, SettableBeanProperty> backRefs, final HashSet<String> ignorableProps, final boolean ignoreAllUnknown, final boolean hasViews) {
        super(beanDesc.getType());
        final AnnotatedClass ac = beanDesc.getClassInfo();
        this._classAnnotations = ac.getAnnotations();
        this._beanType = beanDesc.getType();
        this._valueInstantiator = builder.getValueInstantiator();
        this._beanProperties = properties;
        this._backRefs = backRefs;
        this._ignorableProps = ignorableProps;
        this._ignoreAllUnknown = ignoreAllUnknown;
        this._anySetter = builder.getAnySetter();
        final List<ValueInjector> injectables = builder.getInjectables();
        this._injectables = (ValueInjector[])((injectables == null || injectables.isEmpty()) ? null : ((ValueInjector[])injectables.toArray(new ValueInjector[injectables.size()])));
        this._objectIdReader = builder.getObjectIdReader();
        this._nonStandardCreation = (this._unwrappedPropertyHandler != null || this._valueInstantiator.canCreateUsingDelegate() || this._valueInstantiator.canCreateFromObjectWith() || !this._valueInstantiator.canCreateUsingDefault());
        final JsonFormat.Value format = beanDesc.findExpectedFormat(null);
        this._serializationShape = ((format == null) ? null : format.getShape());
        this._needViewProcesing = hasViews;
        this._vanillaProcessing = (!this._nonStandardCreation && this._injectables == null && !this._needViewProcesing && this._objectIdReader == null);
    }
    
    protected BeanDeserializerBase(final BeanDeserializerBase src) {
        this(src, src._ignoreAllUnknown);
    }
    
    protected BeanDeserializerBase(final BeanDeserializerBase src, final boolean ignoreAllUnknown) {
        super(src._beanType);
        this._classAnnotations = src._classAnnotations;
        this._beanType = src._beanType;
        this._valueInstantiator = src._valueInstantiator;
        this._delegateDeserializer = src._delegateDeserializer;
        this._propertyBasedCreator = src._propertyBasedCreator;
        this._beanProperties = src._beanProperties;
        this._backRefs = src._backRefs;
        this._ignorableProps = src._ignorableProps;
        this._ignoreAllUnknown = ignoreAllUnknown;
        this._anySetter = src._anySetter;
        this._injectables = src._injectables;
        this._objectIdReader = src._objectIdReader;
        this._nonStandardCreation = src._nonStandardCreation;
        this._unwrappedPropertyHandler = src._unwrappedPropertyHandler;
        this._needViewProcesing = src._needViewProcesing;
        this._serializationShape = src._serializationShape;
        this._vanillaProcessing = src._vanillaProcessing;
    }
    
    protected BeanDeserializerBase(final BeanDeserializerBase src, final NameTransformer unwrapper) {
        super(src._beanType);
        this._classAnnotations = src._classAnnotations;
        this._beanType = src._beanType;
        this._valueInstantiator = src._valueInstantiator;
        this._delegateDeserializer = src._delegateDeserializer;
        this._propertyBasedCreator = src._propertyBasedCreator;
        this._backRefs = src._backRefs;
        this._ignorableProps = src._ignorableProps;
        this._ignoreAllUnknown = (unwrapper != null || src._ignoreAllUnknown);
        this._anySetter = src._anySetter;
        this._injectables = src._injectables;
        this._objectIdReader = src._objectIdReader;
        this._nonStandardCreation = src._nonStandardCreation;
        UnwrappedPropertyHandler uph = src._unwrappedPropertyHandler;
        if (unwrapper != null) {
            if (uph != null) {
                uph = uph.renameAll(unwrapper);
            }
            this._beanProperties = src._beanProperties.renameAll(unwrapper);
        }
        else {
            this._beanProperties = src._beanProperties;
        }
        this._unwrappedPropertyHandler = uph;
        this._needViewProcesing = src._needViewProcesing;
        this._serializationShape = src._serializationShape;
        this._vanillaProcessing = false;
    }
    
    public BeanDeserializerBase(final BeanDeserializerBase src, final ObjectIdReader oir) {
        super(src._beanType);
        this._classAnnotations = src._classAnnotations;
        this._beanType = src._beanType;
        this._valueInstantiator = src._valueInstantiator;
        this._delegateDeserializer = src._delegateDeserializer;
        this._propertyBasedCreator = src._propertyBasedCreator;
        this._backRefs = src._backRefs;
        this._ignorableProps = src._ignorableProps;
        this._ignoreAllUnknown = src._ignoreAllUnknown;
        this._anySetter = src._anySetter;
        this._injectables = src._injectables;
        this._nonStandardCreation = src._nonStandardCreation;
        this._unwrappedPropertyHandler = src._unwrappedPropertyHandler;
        this._needViewProcesing = src._needViewProcesing;
        this._serializationShape = src._serializationShape;
        this._objectIdReader = oir;
        if (oir == null) {
            this._beanProperties = src._beanProperties;
            this._vanillaProcessing = src._vanillaProcessing;
        }
        else {
            final ObjectIdValueProperty idProp = new ObjectIdValueProperty(oir, PropertyMetadata.STD_REQUIRED);
            this._beanProperties = src._beanProperties.withProperty(idProp);
            this._vanillaProcessing = false;
        }
    }
    
    public BeanDeserializerBase(final BeanDeserializerBase src, final HashSet<String> ignorableProps) {
        super(src._beanType);
        this._classAnnotations = src._classAnnotations;
        this._beanType = src._beanType;
        this._valueInstantiator = src._valueInstantiator;
        this._delegateDeserializer = src._delegateDeserializer;
        this._propertyBasedCreator = src._propertyBasedCreator;
        this._backRefs = src._backRefs;
        this._ignorableProps = ignorableProps;
        this._ignoreAllUnknown = src._ignoreAllUnknown;
        this._anySetter = src._anySetter;
        this._injectables = src._injectables;
        this._nonStandardCreation = src._nonStandardCreation;
        this._unwrappedPropertyHandler = src._unwrappedPropertyHandler;
        this._needViewProcesing = src._needViewProcesing;
        this._serializationShape = src._serializationShape;
        this._vanillaProcessing = src._vanillaProcessing;
        this._objectIdReader = src._objectIdReader;
        this._beanProperties = src._beanProperties;
    }
    
    @Override
    public abstract JsonDeserializer<Object> unwrappingDeserializer(final NameTransformer p0);
    
    public abstract BeanDeserializerBase withObjectIdReader(final ObjectIdReader p0);
    
    public abstract BeanDeserializerBase withIgnorableProperties(final HashSet<String> p0);
    
    protected abstract BeanDeserializerBase asArrayDeserializer();
    
    @Override
    public void resolve(final DeserializationContext ctxt) throws JsonMappingException {
        ExternalTypeHandler.Builder extTypes = null;
        if (this._valueInstantiator.canCreateFromObjectWith()) {
            final SettableBeanProperty[] creatorProps = this._valueInstantiator.getFromObjectArguments(ctxt.getConfig());
            this._propertyBasedCreator = PropertyBasedCreator.construct(ctxt, this._valueInstantiator, creatorProps);
            for (final SettableBeanProperty prop : this._propertyBasedCreator.properties()) {
                if (prop.hasValueTypeDeserializer()) {
                    final TypeDeserializer typeDeser = prop.getValueTypeDeserializer();
                    if (typeDeser.getTypeInclusion() != JsonTypeInfo.As.EXTERNAL_PROPERTY) {
                        continue;
                    }
                    if (extTypes == null) {
                        extTypes = new ExternalTypeHandler.Builder();
                    }
                    extTypes.addExternal(prop, typeDeser);
                }
            }
        }
        UnwrappedPropertyHandler unwrapped = null;
        for (SettableBeanProperty prop2 : this._beanProperties) {
            final SettableBeanProperty origProp = prop2;
            if (!prop2.hasValueDeserializer()) {
                JsonDeserializer<?> deser = this.findConvertingDeserializer(ctxt, prop2);
                if (deser == null) {
                    deser = this.findDeserializer(ctxt, prop2.getType(), prop2);
                }
                prop2 = prop2.withValueDeserializer(deser);
            }
            else {
                final JsonDeserializer<Object> deser2 = prop2.getValueDeserializer();
                final JsonDeserializer<?> cd = ctxt.handlePrimaryContextualization(deser2, prop2);
                if (cd != deser2) {
                    prop2 = prop2.withValueDeserializer(cd);
                }
            }
            prop2 = this._resolveManagedReferenceProperty(ctxt, prop2);
            if (!(prop2 instanceof ManagedReferenceProperty)) {
                prop2 = this._resolvedObjectIdProperty(ctxt, prop2);
            }
            final SettableBeanProperty u = this._resolveUnwrappedProperty(ctxt, prop2);
            if (u != null) {
                prop2 = u;
                if (unwrapped == null) {
                    unwrapped = new UnwrappedPropertyHandler();
                }
                unwrapped.addProperty(prop2);
                if (prop2 == origProp) {
                    continue;
                }
                this._beanProperties.replace(prop2);
            }
            else {
                prop2 = this._resolveInnerClassValuedProperty(ctxt, prop2);
                if (prop2 != origProp) {
                    this._beanProperties.replace(prop2);
                }
                if (!prop2.hasValueTypeDeserializer()) {
                    continue;
                }
                final TypeDeserializer typeDeser2 = prop2.getValueTypeDeserializer();
                if (typeDeser2.getTypeInclusion() != JsonTypeInfo.As.EXTERNAL_PROPERTY) {
                    continue;
                }
                if (extTypes == null) {
                    extTypes = new ExternalTypeHandler.Builder();
                }
                extTypes.addExternal(prop2, typeDeser2);
                this._beanProperties.remove(prop2);
            }
        }
        if (this._anySetter != null && !this._anySetter.hasValueDeserializer()) {
            this._anySetter = this._anySetter.withValueDeserializer(this.findDeserializer(ctxt, this._anySetter.getType(), this._anySetter.getProperty()));
        }
        if (this._valueInstantiator.canCreateUsingDelegate()) {
            final JavaType delegateType = this._valueInstantiator.getDelegateType(ctxt.getConfig());
            if (delegateType == null) {
                throw new IllegalArgumentException("Invalid delegate-creator definition for " + this._beanType + ": value instantiator (" + this._valueInstantiator.getClass().getName() + ") returned true for 'canCreateUsingDelegate()', but null for 'getDelegateType()'");
            }
            final AnnotatedWithParams delegateCreator = this._valueInstantiator.getDelegateCreator();
            final BeanProperty.Std property = new BeanProperty.Std(BeanDeserializerBase.TEMP_PROPERTY_NAME, delegateType, null, this._classAnnotations, delegateCreator, PropertyMetadata.STD_OPTIONAL);
            this._delegateDeserializer = this.findDeserializer(ctxt, delegateType, property);
        }
        if (extTypes != null) {
            this._externalTypeIdHandler = extTypes.build();
            this._nonStandardCreation = true;
        }
        if ((this._unwrappedPropertyHandler = unwrapped) != null) {
            this._nonStandardCreation = true;
        }
        this._vanillaProcessing = (this._vanillaProcessing && !this._nonStandardCreation);
    }
    
    protected JsonDeserializer<Object> findConvertingDeserializer(final DeserializationContext ctxt, final SettableBeanProperty prop) throws JsonMappingException {
        final AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
        if (intr != null) {
            final Object convDef = intr.findDeserializationConverter(prop.getMember());
            if (convDef != null) {
                final Converter<Object, Object> conv = ctxt.converterInstance(prop.getMember(), convDef);
                final JavaType delegateType = conv.getInputType(ctxt.getTypeFactory());
                final JsonDeserializer<?> ser = ctxt.findContextualValueDeserializer(delegateType, prop);
                return new StdDelegatingDeserializer<Object>(conv, delegateType, ser);
            }
        }
        return null;
    }
    
    @Override
    public JsonDeserializer<?> createContextual(final DeserializationContext ctxt, final BeanProperty property) throws JsonMappingException {
        ObjectIdReader oir = this._objectIdReader;
        final AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
        final AnnotatedMember accessor = (property == null || intr == null) ? null : property.getMember();
        if (accessor != null && intr != null) {
            ObjectIdInfo objectIdInfo = intr.findObjectIdInfo(accessor);
            if (objectIdInfo != null) {
                objectIdInfo = intr.findObjectReferenceInfo(accessor, objectIdInfo);
                final Class<?> implClass = objectIdInfo.getGeneratorType();
                final ObjectIdResolver resolver = ctxt.objectIdResolverInstance(accessor, objectIdInfo);
                SettableBeanProperty idProp;
                JavaType idType;
                ObjectIdGenerator<?> idGen;
                if (implClass == ObjectIdGenerators.PropertyGenerator.class) {
                    final PropertyName propName = objectIdInfo.getPropertyName();
                    idProp = this.findProperty(propName);
                    if (idProp == null) {
                        throw new IllegalArgumentException("Invalid Object Id definition for " + this.handledType().getName() + ": can not find property with name '" + propName + "'");
                    }
                    idType = idProp.getType();
                    idGen = new PropertyBasedObjectIdGenerator(objectIdInfo.getScope());
                }
                else {
                    final JavaType type = ctxt.constructType(implClass);
                    idType = ctxt.getTypeFactory().findTypeParameters(type, ObjectIdGenerator.class)[0];
                    idProp = null;
                    idGen = ctxt.objectIdGeneratorInstance(accessor, objectIdInfo);
                }
                final JsonDeserializer<?> deser = ctxt.findRootValueDeserializer(idType);
                oir = ObjectIdReader.construct(idType, objectIdInfo.getPropertyName(), idGen, deser, idProp, resolver);
            }
        }
        BeanDeserializerBase contextual = this;
        if (oir != null && oir != this._objectIdReader) {
            contextual = contextual.withObjectIdReader(oir);
        }
        if (accessor != null) {
            final String[] ignorals = intr.findPropertiesToIgnore(accessor);
            if (ignorals != null && ignorals.length != 0) {
                final HashSet<String> newIgnored = ArrayBuilders.setAndArray(contextual._ignorableProps, ignorals);
                contextual = contextual.withIgnorableProperties(newIgnored);
            }
        }
        JsonFormat.Shape shape = null;
        if (accessor != null) {
            final JsonFormat.Value format = intr.findFormat(accessor);
            if (format != null) {
                shape = format.getShape();
            }
        }
        if (shape == null) {
            shape = this._serializationShape;
        }
        if (shape == JsonFormat.Shape.ARRAY) {
            contextual = contextual.asArrayDeserializer();
        }
        return contextual;
    }
    
    protected SettableBeanProperty _resolveManagedReferenceProperty(final DeserializationContext ctxt, final SettableBeanProperty prop) {
        final String refName = prop.getManagedReferenceName();
        if (refName == null) {
            return prop;
        }
        final JsonDeserializer<?> valueDeser = prop.getValueDeserializer();
        final SettableBeanProperty backProp = valueDeser.findBackReference(refName);
        if (backProp == null) {
            throw new IllegalArgumentException("Can not handle managed/back reference '" + refName + "': no back reference property found from type " + prop.getType());
        }
        final JavaType referredType = this._beanType;
        final JavaType backRefType = backProp.getType();
        final boolean isContainer = prop.getType().isContainerType();
        if (!backRefType.getRawClass().isAssignableFrom(referredType.getRawClass())) {
            throw new IllegalArgumentException("Can not handle managed/back reference '" + refName + "': back reference type (" + backRefType.getRawClass().getName() + ") not compatible with managed type (" + referredType.getRawClass().getName() + ")");
        }
        return new ManagedReferenceProperty(prop, refName, backProp, this._classAnnotations, isContainer);
    }
    
    protected SettableBeanProperty _resolvedObjectIdProperty(final DeserializationContext ctxt, final SettableBeanProperty prop) {
        final ObjectIdInfo objectIdInfo = prop.getObjectIdInfo();
        final JsonDeserializer<Object> valueDeser = prop.getValueDeserializer();
        final ObjectIdReader objectIdReader = valueDeser.getObjectIdReader();
        if (objectIdInfo == null && objectIdReader == null) {
            return prop;
        }
        return new ObjectIdReferenceProperty(prop, objectIdInfo);
    }
    
    protected SettableBeanProperty _resolveUnwrappedProperty(final DeserializationContext ctxt, final SettableBeanProperty prop) {
        final AnnotatedMember am = prop.getMember();
        if (am != null) {
            final NameTransformer unwrapper = ctxt.getAnnotationIntrospector().findUnwrappingNameTransformer(am);
            if (unwrapper != null) {
                final JsonDeserializer<Object> orig = prop.getValueDeserializer();
                final JsonDeserializer<Object> unwrapping = orig.unwrappingDeserializer(unwrapper);
                if (unwrapping != orig && unwrapping != null) {
                    return prop.withValueDeserializer(unwrapping);
                }
            }
        }
        return null;
    }
    
    protected SettableBeanProperty _resolveInnerClassValuedProperty(final DeserializationContext ctxt, final SettableBeanProperty prop) {
        final JsonDeserializer<Object> deser = prop.getValueDeserializer();
        if (deser instanceof BeanDeserializerBase) {
            final BeanDeserializerBase bd = (BeanDeserializerBase)deser;
            final ValueInstantiator vi = bd.getValueInstantiator();
            if (!vi.canCreateUsingDefault()) {
                final Class<?> valueClass = prop.getType().getRawClass();
                final Class<?> enclosing = ClassUtil.getOuterClass(valueClass);
                if (enclosing != null && enclosing == this._beanType.getRawClass()) {
                    for (final Constructor<?> ctor : valueClass.getConstructors()) {
                        final Class<?>[] paramTypes = ctor.getParameterTypes();
                        if (paramTypes.length == 1 && paramTypes[0] == enclosing) {
                            if (ctxt.getConfig().canOverrideAccessModifiers()) {
                                ClassUtil.checkAndFixAccess(ctor);
                            }
                            return new InnerClassProperty(prop, ctor);
                        }
                    }
                }
            }
        }
        return prop;
    }
    
    @Override
    public boolean isCachable() {
        return true;
    }
    
    @Override
    public Class<?> handledType() {
        return this._beanType.getRawClass();
    }
    
    @Override
    public ObjectIdReader getObjectIdReader() {
        return this._objectIdReader;
    }
    
    public boolean hasProperty(final String propertyName) {
        return this._beanProperties.find(propertyName) != null;
    }
    
    public boolean hasViews() {
        return this._needViewProcesing;
    }
    
    public int getPropertyCount() {
        return this._beanProperties.size();
    }
    
    @Override
    public Collection<Object> getKnownPropertyNames() {
        final ArrayList<Object> names = new ArrayList<Object>();
        for (final SettableBeanProperty prop : this._beanProperties) {
            names.add(prop.getName());
        }
        return names;
    }
    
    @Deprecated
    public final Class<?> getBeanClass() {
        return this._beanType.getRawClass();
    }
    
    @Override
    public JavaType getValueType() {
        return this._beanType;
    }
    
    public Iterator<SettableBeanProperty> properties() {
        if (this._beanProperties == null) {
            throw new IllegalStateException("Can only call after BeanDeserializer has been resolved");
        }
        return this._beanProperties.iterator();
    }
    
    public Iterator<SettableBeanProperty> creatorProperties() {
        if (this._propertyBasedCreator == null) {
            return Collections.emptyList().iterator();
        }
        return this._propertyBasedCreator.properties().iterator();
    }
    
    public SettableBeanProperty findProperty(final PropertyName propertyName) {
        return this.findProperty(propertyName.getSimpleName());
    }
    
    public SettableBeanProperty findProperty(final String propertyName) {
        SettableBeanProperty prop = (this._beanProperties == null) ? null : this._beanProperties.find(propertyName);
        if (prop == null && this._propertyBasedCreator != null) {
            prop = this._propertyBasedCreator.findCreatorProperty(propertyName);
        }
        return prop;
    }
    
    public SettableBeanProperty findProperty(final int propertyIndex) {
        SettableBeanProperty prop = (this._beanProperties == null) ? null : this._beanProperties.find(propertyIndex);
        if (prop == null && this._propertyBasedCreator != null) {
            prop = this._propertyBasedCreator.findCreatorProperty(propertyIndex);
        }
        return prop;
    }
    
    @Override
    public SettableBeanProperty findBackReference(final String logicalName) {
        if (this._backRefs == null) {
            return null;
        }
        return this._backRefs.get(logicalName);
    }
    
    public ValueInstantiator getValueInstantiator() {
        return this._valueInstantiator;
    }
    
    public void replaceProperty(final SettableBeanProperty original, final SettableBeanProperty replacement) {
        this._beanProperties.replace(replacement);
    }
    
    public abstract Object deserializeFromObject(final JsonParser p0, final DeserializationContext p1) throws IOException, JsonProcessingException;
    
    @Override
    public Object deserializeWithType(final JsonParser jp, final DeserializationContext ctxt, final TypeDeserializer typeDeserializer) throws IOException, JsonProcessingException {
        if (this._objectIdReader != null) {
            if (jp.canReadObjectId()) {
                final Object id = jp.getObjectId();
                if (id != null) {
                    final Object ob = typeDeserializer.deserializeTypedFromObject(jp, ctxt);
                    return this._handleTypedObjectId(jp, ctxt, ob, id);
                }
            }
            final JsonToken t = jp.getCurrentToken();
            if (t != null && t.isScalarValue()) {
                return this.deserializeFromObjectId(jp, ctxt);
            }
        }
        return typeDeserializer.deserializeTypedFromObject(jp, ctxt);
    }
    
    protected Object _handleTypedObjectId(final JsonParser jp, final DeserializationContext ctxt, final Object pojo, final Object rawId) throws IOException, JsonProcessingException {
        final JsonDeserializer<Object> idDeser = this._objectIdReader.getDeserializer();
        Object id;
        if (idDeser.handledType() == rawId.getClass()) {
            id = rawId;
        }
        else {
            id = this._convertObjectId(jp, ctxt, rawId, idDeser);
        }
        final ReadableObjectId roid = ctxt.findObjectId(id, this._objectIdReader.generator, this._objectIdReader.resolver);
        roid.bindItem(pojo);
        final SettableBeanProperty idProp = this._objectIdReader.idProperty;
        if (idProp != null) {
            return idProp.setAndReturn(pojo, id);
        }
        return pojo;
    }
    
    protected Object _convertObjectId(final JsonParser jp, final DeserializationContext ctxt, final Object rawId, final JsonDeserializer<Object> idDeser) throws IOException, JsonProcessingException {
        final TokenBuffer buf = new TokenBuffer(jp);
        if (rawId instanceof String) {
            buf.writeString((String)rawId);
        }
        else if (rawId instanceof Long) {
            buf.writeNumber((long)rawId);
        }
        else if (rawId instanceof Integer) {
            buf.writeNumber((int)rawId);
        }
        else {
            buf.writeObject(rawId);
        }
        final JsonParser bufParser = buf.asParser();
        bufParser.nextToken();
        return idDeser.deserialize(bufParser, ctxt);
    }
    
    protected Object deserializeWithObjectId(final JsonParser jp, final DeserializationContext ctxt) throws IOException, JsonProcessingException {
        return this.deserializeFromObject(jp, ctxt);
    }
    
    protected Object deserializeFromObjectId(final JsonParser jp, final DeserializationContext ctxt) throws IOException, JsonProcessingException {
        final Object id = this._objectIdReader.readObjectReference(jp, ctxt);
        final ReadableObjectId roid = ctxt.findObjectId(id, this._objectIdReader.generator, this._objectIdReader.resolver);
        final Object pojo = roid.resolve();
        if (pojo == null) {
            throw new UnresolvedForwardReference("Could not resolve Object Id [" + id + "] (for " + this._beanType + ").", jp.getCurrentLocation(), roid);
        }
        return pojo;
    }
    
    protected Object deserializeFromObjectUsingNonDefault(final JsonParser jp, final DeserializationContext ctxt) throws IOException, JsonProcessingException {
        if (this._delegateDeserializer != null) {
            return this._valueInstantiator.createUsingDelegate(ctxt, this._delegateDeserializer.deserialize(jp, ctxt));
        }
        if (this._propertyBasedCreator != null) {
            return this._deserializeUsingPropertyBased(jp, ctxt);
        }
        if (this._beanType.isAbstract()) {
            throw JsonMappingException.from(jp, "Can not instantiate abstract type " + this._beanType + " (need to add/enable type information?)");
        }
        throw JsonMappingException.from(jp, "No suitable constructor found for type " + this._beanType + ": can not instantiate from JSON object (need to add/enable type information?)");
    }
    
    protected abstract Object _deserializeUsingPropertyBased(final JsonParser p0, final DeserializationContext p1) throws IOException, JsonProcessingException;
    
    public Object deserializeFromNumber(final JsonParser jp, final DeserializationContext ctxt) throws IOException, JsonProcessingException {
        if (this._objectIdReader != null) {
            return this.deserializeFromObjectId(jp, ctxt);
        }
        switch (jp.getNumberType()) {
            case INT: {
                if (this._delegateDeserializer != null && !this._valueInstantiator.canCreateFromInt()) {
                    final Object bean = this._valueInstantiator.createUsingDelegate(ctxt, this._delegateDeserializer.deserialize(jp, ctxt));
                    if (this._injectables != null) {
                        this.injectValues(ctxt, bean);
                    }
                    return bean;
                }
                return this._valueInstantiator.createFromInt(ctxt, jp.getIntValue());
            }
            case LONG: {
                if (this._delegateDeserializer != null && !this._valueInstantiator.canCreateFromInt()) {
                    final Object bean = this._valueInstantiator.createUsingDelegate(ctxt, this._delegateDeserializer.deserialize(jp, ctxt));
                    if (this._injectables != null) {
                        this.injectValues(ctxt, bean);
                    }
                    return bean;
                }
                return this._valueInstantiator.createFromLong(ctxt, jp.getLongValue());
            }
            default: {
                if (this._delegateDeserializer != null) {
                    final Object bean = this._valueInstantiator.createUsingDelegate(ctxt, this._delegateDeserializer.deserialize(jp, ctxt));
                    if (this._injectables != null) {
                        this.injectValues(ctxt, bean);
                    }
                    return bean;
                }
                throw ctxt.instantiationException(this.getBeanClass(), "no suitable creator method found to deserialize from JSON integer number");
            }
        }
    }
    
    public Object deserializeFromString(final JsonParser jp, final DeserializationContext ctxt) throws IOException, JsonProcessingException {
        if (this._objectIdReader != null) {
            return this.deserializeFromObjectId(jp, ctxt);
        }
        if (this._delegateDeserializer != null && !this._valueInstantiator.canCreateFromString()) {
            final Object bean = this._valueInstantiator.createUsingDelegate(ctxt, this._delegateDeserializer.deserialize(jp, ctxt));
            if (this._injectables != null) {
                this.injectValues(ctxt, bean);
            }
            return bean;
        }
        return this._valueInstantiator.createFromString(ctxt, jp.getText());
    }
    
    public Object deserializeFromDouble(final JsonParser jp, final DeserializationContext ctxt) throws IOException, JsonProcessingException {
        switch (jp.getNumberType()) {
            case FLOAT:
            case DOUBLE: {
                if (this._delegateDeserializer != null && !this._valueInstantiator.canCreateFromDouble()) {
                    final Object bean = this._valueInstantiator.createUsingDelegate(ctxt, this._delegateDeserializer.deserialize(jp, ctxt));
                    if (this._injectables != null) {
                        this.injectValues(ctxt, bean);
                    }
                    return bean;
                }
                return this._valueInstantiator.createFromDouble(ctxt, jp.getDoubleValue());
            }
            default: {
                if (this._delegateDeserializer != null) {
                    return this._valueInstantiator.createUsingDelegate(ctxt, this._delegateDeserializer.deserialize(jp, ctxt));
                }
                throw ctxt.instantiationException(this.getBeanClass(), "no suitable creator method found to deserialize from JSON floating-point number");
            }
        }
    }
    
    public Object deserializeFromBoolean(final JsonParser jp, final DeserializationContext ctxt) throws IOException, JsonProcessingException {
        if (this._delegateDeserializer != null && !this._valueInstantiator.canCreateFromBoolean()) {
            final Object bean = this._valueInstantiator.createUsingDelegate(ctxt, this._delegateDeserializer.deserialize(jp, ctxt));
            if (this._injectables != null) {
                this.injectValues(ctxt, bean);
            }
            return bean;
        }
        final boolean value = jp.getCurrentToken() == JsonToken.VALUE_TRUE;
        return this._valueInstantiator.createFromBoolean(ctxt, value);
    }
    
    public Object deserializeFromArray(final JsonParser jp, final DeserializationContext ctxt) throws IOException, JsonProcessingException {
        if (this._delegateDeserializer != null) {
            try {
                final Object bean = this._valueInstantiator.createUsingDelegate(ctxt, this._delegateDeserializer.deserialize(jp, ctxt));
                if (this._injectables != null) {
                    this.injectValues(ctxt, bean);
                }
                return bean;
            }
            catch (Exception e) {
                this.wrapInstantiationProblem(e, ctxt);
                throw ctxt.mappingException(this.getBeanClass());
            }
        }
        if (ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
            jp.nextToken();
            final Object value = this.deserialize(jp, ctxt);
            if (jp.nextToken() != JsonToken.END_ARRAY) {
                throw ctxt.wrongTokenException(jp, JsonToken.END_ARRAY, "Attempted to unwrap single value array for single '" + this._valueClass.getName() + "' value but there was more than a single value in the array");
            }
            return value;
        }
        throw ctxt.mappingException(this.getBeanClass());
    }
    
    protected void injectValues(final DeserializationContext ctxt, final Object bean) throws IOException, JsonProcessingException {
        for (final ValueInjector injector : this._injectables) {
            injector.inject(ctxt, bean);
        }
    }
    
    protected Object handleUnknownProperties(final DeserializationContext ctxt, final Object bean, final TokenBuffer unknownTokens) throws IOException, JsonProcessingException {
        unknownTokens.writeEndObject();
        final JsonParser bufferParser = unknownTokens.asParser();
        while (bufferParser.nextToken() != JsonToken.END_OBJECT) {
            final String propName = bufferParser.getCurrentName();
            bufferParser.nextToken();
            this.handleUnknownProperty(bufferParser, ctxt, bean, propName);
        }
        return bean;
    }
    
    protected void handleUnknownVanilla(final JsonParser jp, final DeserializationContext ctxt, final Object bean, final String propName) throws IOException, JsonProcessingException {
        if (this._ignorableProps != null && this._ignorableProps.contains(propName)) {
            this.handleIgnoredProperty(jp, ctxt, bean, propName);
        }
        else if (this._anySetter != null) {
            try {
                this._anySetter.deserializeAndSet(jp, ctxt, bean, propName);
            }
            catch (Exception e) {
                this.wrapAndThrow(e, bean, propName, ctxt);
            }
        }
        else {
            this.handleUnknownProperty(jp, ctxt, bean, propName);
        }
    }
    
    @Override
    protected void handleUnknownProperty(final JsonParser jp, final DeserializationContext ctxt, final Object beanOrClass, final String propName) throws IOException, JsonProcessingException {
        if (this._ignoreAllUnknown) {
            jp.skipChildren();
            return;
        }
        if (this._ignorableProps != null && this._ignorableProps.contains(propName)) {
            this.handleIgnoredProperty(jp, ctxt, beanOrClass, propName);
        }
        super.handleUnknownProperty(jp, ctxt, beanOrClass, propName);
    }
    
    protected void handleIgnoredProperty(final JsonParser jp, final DeserializationContext ctxt, final Object beanOrClass, final String propName) throws IOException, JsonProcessingException {
        if (ctxt.isEnabled(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES)) {
            throw IgnoredPropertyException.from(jp, beanOrClass, propName, this.getKnownPropertyNames());
        }
        jp.skipChildren();
    }
    
    protected Object handlePolymorphic(final JsonParser jp, final DeserializationContext ctxt, Object bean, final TokenBuffer unknownTokens) throws IOException, JsonProcessingException {
        final JsonDeserializer<Object> subDeser = this._findSubclassDeserializer(ctxt, bean, unknownTokens);
        if (subDeser != null) {
            if (unknownTokens != null) {
                unknownTokens.writeEndObject();
                final JsonParser p2 = unknownTokens.asParser();
                p2.nextToken();
                bean = subDeser.deserialize(p2, ctxt, bean);
            }
            if (jp != null) {
                bean = subDeser.deserialize(jp, ctxt, bean);
            }
            return bean;
        }
        if (unknownTokens != null) {
            bean = this.handleUnknownProperties(ctxt, bean, unknownTokens);
        }
        if (jp != null) {
            bean = this.deserialize(jp, ctxt, bean);
        }
        return bean;
    }
    
    protected JsonDeserializer<Object> _findSubclassDeserializer(final DeserializationContext ctxt, final Object bean, final TokenBuffer unknownTokens) throws IOException, JsonProcessingException {
        JsonDeserializer<Object> subDeser;
        synchronized (this) {
            subDeser = ((this._subDeserializers == null) ? null : this._subDeserializers.get(new ClassKey(bean.getClass())));
        }
        if (subDeser != null) {
            return subDeser;
        }
        final JavaType type = ctxt.constructType(bean.getClass());
        subDeser = ctxt.findRootValueDeserializer(type);
        if (subDeser != null) {
            synchronized (this) {
                if (this._subDeserializers == null) {
                    this._subDeserializers = new HashMap<ClassKey, JsonDeserializer<Object>>();
                }
                this._subDeserializers.put(new ClassKey(bean.getClass()), subDeser);
            }
        }
        return subDeser;
    }
    
    public void wrapAndThrow(final Throwable t, final Object bean, final String fieldName, final DeserializationContext ctxt) throws IOException {
        throw JsonMappingException.wrapWithPath(this.throwOrReturnThrowable(t, ctxt), bean, fieldName);
    }
    
    public void wrapAndThrow(final Throwable t, final Object bean, final int index, final DeserializationContext ctxt) throws IOException {
        throw JsonMappingException.wrapWithPath(this.throwOrReturnThrowable(t, ctxt), bean, index);
    }
    
    private Throwable throwOrReturnThrowable(Throwable t, final DeserializationContext ctxt) throws IOException {
        while (t instanceof InvocationTargetException && t.getCause() != null) {
            t = t.getCause();
        }
        if (t instanceof Error) {
            throw (Error)t;
        }
        final boolean wrap = ctxt == null || ctxt.isEnabled(DeserializationFeature.WRAP_EXCEPTIONS);
        if (t instanceof IOException) {
            if (!wrap || !(t instanceof JsonProcessingException)) {
                throw (IOException)t;
            }
        }
        else if (!wrap && t instanceof RuntimeException) {
            throw (RuntimeException)t;
        }
        return t;
    }
    
    protected void wrapInstantiationProblem(Throwable t, final DeserializationContext ctxt) throws IOException {
        while (t instanceof InvocationTargetException && t.getCause() != null) {
            t = t.getCause();
        }
        if (t instanceof Error) {
            throw (Error)t;
        }
        final boolean wrap = ctxt == null || ctxt.isEnabled(DeserializationFeature.WRAP_EXCEPTIONS);
        if (t instanceof IOException) {
            throw (IOException)t;
        }
        if (!wrap && t instanceof RuntimeException) {
            throw (RuntimeException)t;
        }
        throw ctxt.instantiationException(this._beanType.getRawClass(), t);
    }
    
    static {
        TEMP_PROPERTY_NAME = new PropertyName("#temporary-name");
    }
}
