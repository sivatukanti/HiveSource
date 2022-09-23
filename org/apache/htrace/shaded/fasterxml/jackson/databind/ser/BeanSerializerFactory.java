// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.ser;

import java.util.HashMap;
import java.util.HashSet;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.AnnotatedClass;
import org.apache.htrace.shaded.fasterxml.jackson.databind.util.ArrayBuilders;
import org.apache.htrace.shaded.fasterxml.jackson.databind.type.TypeBindings;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.AnnotatedField;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import org.apache.htrace.shaded.fasterxml.jackson.databind.util.ClassUtil;
import org.apache.htrace.shaded.fasterxml.jackson.databind.ser.impl.FilteredBeanPropertyWriter;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.ObjectIdInfo;
import org.apache.htrace.shaded.fasterxml.jackson.annotation.ObjectIdGenerator;
import java.lang.reflect.Type;
import org.apache.htrace.shaded.fasterxml.jackson.databind.ser.impl.PropertyBasedObjectIdGenerator;
import org.apache.htrace.shaded.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.apache.htrace.shaded.fasterxml.jackson.databind.ser.impl.ObjectIdWriter;
import java.util.List;
import org.apache.htrace.shaded.fasterxml.jackson.databind.BeanProperty;
import org.apache.htrace.shaded.fasterxml.jackson.databind.PropertyMetadata;
import org.apache.htrace.shaded.fasterxml.jackson.databind.PropertyName;
import org.apache.htrace.shaded.fasterxml.jackson.databind.ser.std.MapSerializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.MapperFeature;
import java.util.ArrayList;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsontype.NamedType;
import java.util.Collection;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
import org.apache.htrace.shaded.fasterxml.jackson.databind.AnnotationIntrospector;
import org.apache.htrace.shaded.fasterxml.jackson.databind.cfg.MapperConfig;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.AnnotatedMember;
import java.util.Iterator;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsontype.TypeSerializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonMappingException;
import org.apache.htrace.shaded.fasterxml.jackson.databind.BeanDescription;
import org.apache.htrace.shaded.fasterxml.jackson.databind.SerializationConfig;
import org.apache.htrace.shaded.fasterxml.jackson.databind.util.Converter;
import org.apache.htrace.shaded.fasterxml.jackson.databind.ser.std.StdDelegatingSerializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.Annotated;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonSerializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JavaType;
import org.apache.htrace.shaded.fasterxml.jackson.databind.SerializerProvider;
import org.apache.htrace.shaded.fasterxml.jackson.databind.cfg.SerializerFactoryConfig;
import java.io.Serializable;

public class BeanSerializerFactory extends BasicSerializerFactory implements Serializable
{
    private static final long serialVersionUID = 1L;
    public static final BeanSerializerFactory instance;
    
    protected BeanSerializerFactory(final SerializerFactoryConfig config) {
        super(config);
    }
    
    @Override
    public SerializerFactory withConfig(final SerializerFactoryConfig config) {
        if (this._factoryConfig == config) {
            return this;
        }
        if (this.getClass() != BeanSerializerFactory.class) {
            throw new IllegalStateException("Subtype of BeanSerializerFactory (" + this.getClass().getName() + ") has not properly overridden method 'withAdditionalSerializers': can not instantiate subtype with " + "additional serializer definitions");
        }
        return new BeanSerializerFactory(config);
    }
    
    @Override
    protected Iterable<Serializers> customSerializers() {
        return this._factoryConfig.serializers();
    }
    
    @Override
    public JsonSerializer<Object> createSerializer(final SerializerProvider prov, final JavaType origType) throws JsonMappingException {
        final SerializationConfig config = prov.getConfig();
        BeanDescription beanDesc = config.introspect(origType);
        JsonSerializer<?> ser = this.findSerializerFromAnnotation(prov, beanDesc.getClassInfo());
        if (ser != null) {
            return (JsonSerializer<Object>)ser;
        }
        final JavaType type = this.modifyTypeByAnnotation(config, beanDesc.getClassInfo(), origType);
        boolean staticTyping;
        if (type == origType) {
            staticTyping = false;
        }
        else {
            staticTyping = true;
            if (!type.hasRawClass(origType.getRawClass())) {
                beanDesc = config.introspect(type);
            }
        }
        final Converter<Object, Object> conv = beanDesc.findSerializationConverter();
        if (conv == null) {
            return (JsonSerializer<Object>)this._createSerializer2(prov, type, beanDesc, staticTyping);
        }
        final JavaType delegateType = conv.getOutputType(prov.getTypeFactory());
        if (!delegateType.hasRawClass(type.getRawClass())) {
            beanDesc = config.introspect(delegateType);
            ser = this.findSerializerFromAnnotation(prov, beanDesc.getClassInfo());
        }
        if (ser == null) {
            ser = this._createSerializer2(prov, delegateType, beanDesc, true);
        }
        return new StdDelegatingSerializer(conv, delegateType, ser);
    }
    
    protected JsonSerializer<?> _createSerializer2(final SerializerProvider prov, final JavaType type, final BeanDescription beanDesc, boolean staticTyping) throws JsonMappingException {
        JsonSerializer<?> ser = this.findSerializerByAnnotations(prov, type, beanDesc);
        if (ser != null) {
            return ser;
        }
        final SerializationConfig config = prov.getConfig();
        if (type.isContainerType()) {
            if (!staticTyping) {
                staticTyping = this.usesStaticTyping(config, beanDesc, null);
            }
            ser = this.buildContainerSerializer(prov, type, beanDesc, staticTyping);
            if (ser != null) {
                return ser;
            }
        }
        else {
            for (final Serializers serializers : this.customSerializers()) {
                ser = serializers.findSerializer(config, type, beanDesc);
                if (ser != null) {
                    break;
                }
            }
        }
        if (ser == null) {
            ser = this.findSerializerByLookup(type, config, beanDesc, staticTyping);
            if (ser == null) {
                ser = this.findSerializerByPrimaryType(prov, type, beanDesc, staticTyping);
                if (ser == null) {
                    ser = this.findBeanSerializer(prov, type, beanDesc);
                    if (ser == null) {
                        ser = this.findSerializerByAddonType(config, type, beanDesc, staticTyping);
                    }
                }
            }
        }
        if (ser != null && this._factoryConfig.hasSerializerModifiers()) {
            for (final BeanSerializerModifier mod : this._factoryConfig.serializerModifiers()) {
                ser = mod.modifySerializer(config, beanDesc, ser);
            }
        }
        return ser;
    }
    
    public JsonSerializer<Object> findBeanSerializer(final SerializerProvider prov, final JavaType type, final BeanDescription beanDesc) throws JsonMappingException {
        if (!this.isPotentialBeanType(type.getRawClass()) && !type.isEnumType()) {
            return null;
        }
        return this.constructBeanSerializer(prov, beanDesc);
    }
    
    public TypeSerializer findPropertyTypeSerializer(final JavaType baseType, final SerializationConfig config, final AnnotatedMember accessor) throws JsonMappingException {
        final AnnotationIntrospector ai = config.getAnnotationIntrospector();
        final TypeResolverBuilder<?> b = ai.findPropertyTypeResolver(config, accessor, baseType);
        if (b == null) {
            return this.createTypeSerializer(config, baseType);
        }
        final Collection<NamedType> subtypes = config.getSubtypeResolver().collectAndResolveSubtypes(accessor, config, ai, baseType);
        return b.buildTypeSerializer(config, baseType, subtypes);
    }
    
    public TypeSerializer findPropertyContentTypeSerializer(final JavaType containerType, final SerializationConfig config, final AnnotatedMember accessor) throws JsonMappingException {
        final JavaType contentType = containerType.getContentType();
        final AnnotationIntrospector ai = config.getAnnotationIntrospector();
        final TypeResolverBuilder<?> b = ai.findPropertyContentTypeResolver(config, accessor, containerType);
        if (b == null) {
            return this.createTypeSerializer(config, contentType);
        }
        final Collection<NamedType> subtypes = config.getSubtypeResolver().collectAndResolveSubtypes(accessor, config, ai, contentType);
        return b.buildTypeSerializer(config, contentType, subtypes);
    }
    
    protected JsonSerializer<Object> constructBeanSerializer(final SerializerProvider prov, final BeanDescription beanDesc) throws JsonMappingException {
        if (beanDesc.getBeanClass() == Object.class) {
            return prov.getUnknownTypeSerializer(Object.class);
        }
        final SerializationConfig config = prov.getConfig();
        BeanSerializerBuilder builder = this.constructBeanSerializerBuilder(beanDesc);
        builder.setConfig(config);
        List<BeanPropertyWriter> props = this.findBeanProperties(prov, beanDesc, builder);
        if (props == null) {
            props = new ArrayList<BeanPropertyWriter>();
        }
        if (this._factoryConfig.hasSerializerModifiers()) {
            for (final BeanSerializerModifier mod : this._factoryConfig.serializerModifiers()) {
                props = mod.changeProperties(config, beanDesc, props);
            }
        }
        props = this.filterBeanProperties(config, beanDesc, props);
        if (this._factoryConfig.hasSerializerModifiers()) {
            for (final BeanSerializerModifier mod : this._factoryConfig.serializerModifiers()) {
                props = mod.orderProperties(config, beanDesc, props);
            }
        }
        builder.setObjectIdWriter(this.constructObjectIdHandler(prov, beanDesc, props));
        builder.setProperties(props);
        builder.setFilterId(this.findFilterId(config, beanDesc));
        final AnnotatedMember anyGetter = beanDesc.findAnyGetter();
        if (anyGetter != null) {
            if (config.canOverrideAccessModifiers()) {
                anyGetter.fixAccess();
            }
            final JavaType type = anyGetter.getType(beanDesc.bindingsForBeanType());
            final boolean staticTyping = config.isEnabled(MapperFeature.USE_STATIC_TYPING);
            final JavaType valueType = type.getContentType();
            final TypeSerializer typeSer = this.createTypeSerializer(config, valueType);
            final MapSerializer mapSer = MapSerializer.construct(null, type, staticTyping, typeSer, null, null, null);
            final PropertyName name = new PropertyName(anyGetter.getName());
            final BeanProperty.Std anyProp = new BeanProperty.Std(name, valueType, null, beanDesc.getClassAnnotations(), anyGetter, PropertyMetadata.STD_OPTIONAL);
            builder.setAnyGetter(new AnyGetterWriter(anyProp, anyGetter, mapSer));
        }
        this.processViews(config, builder);
        if (this._factoryConfig.hasSerializerModifiers()) {
            for (final BeanSerializerModifier mod2 : this._factoryConfig.serializerModifiers()) {
                builder = mod2.updateBuilder(config, beanDesc, builder);
            }
        }
        final JsonSerializer<Object> ser = (JsonSerializer<Object>)builder.build();
        if (ser == null && beanDesc.hasKnownClassAnnotations()) {
            return builder.createDummy();
        }
        return ser;
    }
    
    protected ObjectIdWriter constructObjectIdHandler(final SerializerProvider prov, final BeanDescription beanDesc, final List<BeanPropertyWriter> props) throws JsonMappingException {
        final ObjectIdInfo objectIdInfo = beanDesc.getObjectIdInfo();
        if (objectIdInfo == null) {
            return null;
        }
        final Class<?> implClass = objectIdInfo.getGeneratorType();
        if (implClass == ObjectIdGenerators.PropertyGenerator.class) {
            final String propName = objectIdInfo.getPropertyName().getSimpleName();
            BeanPropertyWriter idProp = null;
            for (int i = 0, len = props.size(); i != len; ++i) {
                final BeanPropertyWriter prop = props.get(i);
                if (propName.equals(prop.getName())) {
                    idProp = prop;
                    if (i > 0) {
                        props.remove(i);
                        props.add(0, idProp);
                    }
                    final JavaType idType = idProp.getType();
                    final ObjectIdGenerator<?> gen = new PropertyBasedObjectIdGenerator(objectIdInfo, idProp);
                    return ObjectIdWriter.construct(idType, (PropertyName)null, gen, objectIdInfo.getAlwaysAsId());
                }
            }
            throw new IllegalArgumentException("Invalid Object Id definition for " + beanDesc.getBeanClass().getName() + ": can not find property with name '" + propName + "'");
        }
        final JavaType type = prov.constructType(implClass);
        final JavaType idType2 = prov.getTypeFactory().findTypeParameters(type, ObjectIdGenerator.class)[0];
        final ObjectIdGenerator<?> gen = prov.objectIdGeneratorInstance(beanDesc.getClassInfo(), objectIdInfo);
        return ObjectIdWriter.construct(idType2, objectIdInfo.getPropertyName(), gen, objectIdInfo.getAlwaysAsId());
    }
    
    protected BeanPropertyWriter constructFilteredBeanWriter(final BeanPropertyWriter writer, final Class<?>[] inViews) {
        return FilteredBeanPropertyWriter.constructViewBased(writer, inViews);
    }
    
    protected PropertyBuilder constructPropertyBuilder(final SerializationConfig config, final BeanDescription beanDesc) {
        return new PropertyBuilder(config, beanDesc);
    }
    
    protected BeanSerializerBuilder constructBeanSerializerBuilder(final BeanDescription beanDesc) {
        return new BeanSerializerBuilder(beanDesc);
    }
    
    protected boolean isPotentialBeanType(final Class<?> type) {
        return ClassUtil.canBeABeanType(type) == null && !ClassUtil.isProxyType(type);
    }
    
    protected List<BeanPropertyWriter> findBeanProperties(final SerializerProvider prov, final BeanDescription beanDesc, final BeanSerializerBuilder builder) throws JsonMappingException {
        final List<BeanPropertyDefinition> properties = beanDesc.findProperties();
        final SerializationConfig config = prov.getConfig();
        this.removeIgnorableTypes(config, beanDesc, properties);
        if (config.isEnabled(MapperFeature.REQUIRE_SETTERS_FOR_GETTERS)) {
            this.removeSetterlessGetters(config, beanDesc, properties);
        }
        if (properties.isEmpty()) {
            return null;
        }
        final boolean staticTyping = this.usesStaticTyping(config, beanDesc, null);
        final PropertyBuilder pb = this.constructPropertyBuilder(config, beanDesc);
        final ArrayList<BeanPropertyWriter> result = new ArrayList<BeanPropertyWriter>(properties.size());
        final TypeBindings typeBind = beanDesc.bindingsForBeanType();
        for (final BeanPropertyDefinition property : properties) {
            final AnnotatedMember accessor = property.getAccessor();
            if (property.isTypeId()) {
                if (accessor == null) {
                    continue;
                }
                if (config.canOverrideAccessModifiers()) {
                    accessor.fixAccess();
                }
                builder.setTypeId(accessor);
            }
            else {
                final AnnotationIntrospector.ReferenceProperty refType = property.findReferenceType();
                if (refType != null && refType.isBackReference()) {
                    continue;
                }
                if (accessor instanceof AnnotatedMethod) {
                    result.add(this._constructWriter(prov, property, typeBind, pb, staticTyping, accessor));
                }
                else {
                    result.add(this._constructWriter(prov, property, typeBind, pb, staticTyping, accessor));
                }
            }
        }
        return result;
    }
    
    protected List<BeanPropertyWriter> filterBeanProperties(final SerializationConfig config, final BeanDescription beanDesc, final List<BeanPropertyWriter> props) {
        final AnnotationIntrospector intr = config.getAnnotationIntrospector();
        final AnnotatedClass ac = beanDesc.getClassInfo();
        final String[] ignored = intr.findPropertiesToIgnore(ac);
        if (ignored != null && ignored.length > 0) {
            final HashSet<String> ignoredSet = ArrayBuilders.arrayToSet(ignored);
            final Iterator<BeanPropertyWriter> it = props.iterator();
            while (it.hasNext()) {
                if (ignoredSet.contains(it.next().getName())) {
                    it.remove();
                }
            }
        }
        return props;
    }
    
    protected void processViews(final SerializationConfig config, final BeanSerializerBuilder builder) {
        final List<BeanPropertyWriter> props = builder.getProperties();
        final boolean includeByDefault = config.isEnabled(MapperFeature.DEFAULT_VIEW_INCLUSION);
        final int propCount = props.size();
        int viewsFound = 0;
        final BeanPropertyWriter[] filtered = new BeanPropertyWriter[propCount];
        for (int i = 0; i < propCount; ++i) {
            final BeanPropertyWriter bpw = props.get(i);
            final Class<?>[] views = bpw.getViews();
            if (views == null) {
                if (includeByDefault) {
                    filtered[i] = bpw;
                }
            }
            else {
                ++viewsFound;
                filtered[i] = this.constructFilteredBeanWriter(bpw, views);
            }
        }
        if (includeByDefault && viewsFound == 0) {
            return;
        }
        builder.setFilteredProperties(filtered);
    }
    
    protected void removeIgnorableTypes(final SerializationConfig config, final BeanDescription beanDesc, final List<BeanPropertyDefinition> properties) {
        final AnnotationIntrospector intr = config.getAnnotationIntrospector();
        final HashMap<Class<?>, Boolean> ignores = new HashMap<Class<?>, Boolean>();
        final Iterator<BeanPropertyDefinition> it = properties.iterator();
        while (it.hasNext()) {
            final BeanPropertyDefinition property = it.next();
            final AnnotatedMember accessor = property.getAccessor();
            if (accessor == null) {
                it.remove();
            }
            else {
                final Class<?> type = accessor.getRawType();
                Boolean result = ignores.get(type);
                if (result == null) {
                    final BeanDescription desc = config.introspectClassAnnotations(type);
                    final AnnotatedClass ac = desc.getClassInfo();
                    result = intr.isIgnorableType(ac);
                    if (result == null) {
                        result = Boolean.FALSE;
                    }
                    ignores.put(type, result);
                }
                if (!result) {
                    continue;
                }
                it.remove();
            }
        }
    }
    
    protected void removeSetterlessGetters(final SerializationConfig config, final BeanDescription beanDesc, final List<BeanPropertyDefinition> properties) {
        final Iterator<BeanPropertyDefinition> it = properties.iterator();
        while (it.hasNext()) {
            final BeanPropertyDefinition property = it.next();
            if (!property.couldDeserialize() && !property.isExplicitlyIncluded()) {
                it.remove();
            }
        }
    }
    
    protected BeanPropertyWriter _constructWriter(final SerializerProvider prov, final BeanPropertyDefinition propDef, final TypeBindings typeContext, final PropertyBuilder pb, final boolean staticTyping, final AnnotatedMember accessor) throws JsonMappingException {
        final PropertyName name = propDef.getFullName();
        if (prov.canOverrideAccessModifiers()) {
            accessor.fixAccess();
        }
        final JavaType type = accessor.getType(typeContext);
        final BeanProperty.Std property = new BeanProperty.Std(name, type, propDef.getWrapperName(), pb.getClassAnnotations(), accessor, propDef.getMetadata());
        JsonSerializer<?> annotatedSerializer = this.findSerializerFromAnnotation(prov, accessor);
        if (annotatedSerializer instanceof ResolvableSerializer) {
            ((ResolvableSerializer)annotatedSerializer).resolve(prov);
        }
        annotatedSerializer = prov.handlePrimaryContextualization(annotatedSerializer, property);
        TypeSerializer contentTypeSer = null;
        if (ClassUtil.isCollectionMapOrArray(type.getRawClass()) || type.isCollectionLikeType() || type.isMapLikeType()) {
            contentTypeSer = this.findPropertyContentTypeSerializer(type, prov.getConfig(), accessor);
        }
        final TypeSerializer typeSer = this.findPropertyTypeSerializer(type, prov.getConfig(), accessor);
        final BeanPropertyWriter pbw = pb.buildWriter(prov, propDef, type, annotatedSerializer, typeSer, contentTypeSer, accessor, staticTyping);
        return pbw;
    }
    
    static {
        instance = new BeanSerializerFactory(null);
    }
}
