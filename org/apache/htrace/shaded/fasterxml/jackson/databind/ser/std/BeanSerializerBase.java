// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.ser.std;

import org.apache.htrace.shaded.fasterxml.jackson.databind.jsonFormatVisitors.JsonObjectFormatVisitor;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import org.apache.htrace.shaded.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsonschema.JsonSerializableSchema;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonNode;
import org.apache.htrace.shaded.fasterxml.jackson.databind.ser.PropertyFilter;
import org.apache.htrace.shaded.fasterxml.jackson.databind.ser.PropertyWriter;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonProcessingException;
import org.apache.htrace.shaded.fasterxml.jackson.databind.ser.impl.WritableObjectId;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonGenerationException;
import java.io.IOException;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonGenerator;
import org.apache.htrace.shaded.fasterxml.jackson.databind.ser.impl.PropertyBasedObjectIdGenerator;
import org.apache.htrace.shaded.fasterxml.jackson.annotation.ObjectIdGenerators;
import java.lang.reflect.Type;
import org.apache.htrace.shaded.fasterxml.jackson.annotation.ObjectIdResolver;
import org.apache.htrace.shaded.fasterxml.jackson.annotation.ObjectIdGenerator;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.ObjectIdInfo;
import org.apache.htrace.shaded.fasterxml.jackson.databind.AnnotationIntrospector;
import org.apache.htrace.shaded.fasterxml.jackson.databind.util.Converter;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.Annotated;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonMappingException;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonSerializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.ser.ContainerSerializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsontype.TypeSerializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.BeanProperty;
import org.apache.htrace.shaded.fasterxml.jackson.databind.SerializerProvider;
import org.apache.htrace.shaded.fasterxml.jackson.databind.util.NameTransformer;
import java.util.HashSet;
import java.util.ArrayList;
import org.apache.htrace.shaded.fasterxml.jackson.databind.util.ArrayBuilders;
import org.apache.htrace.shaded.fasterxml.jackson.databind.ser.BeanSerializerBuilder;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JavaType;
import org.apache.htrace.shaded.fasterxml.jackson.annotation.JsonFormat;
import org.apache.htrace.shaded.fasterxml.jackson.databind.ser.impl.ObjectIdWriter;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.AnnotatedMember;
import org.apache.htrace.shaded.fasterxml.jackson.databind.ser.AnyGetterWriter;
import org.apache.htrace.shaded.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import org.apache.htrace.shaded.fasterxml.jackson.databind.PropertyName;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsonschema.SchemaAware;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitable;
import org.apache.htrace.shaded.fasterxml.jackson.databind.ser.ResolvableSerializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.ser.ContextualSerializer;

public abstract class BeanSerializerBase extends StdSerializer<Object> implements ContextualSerializer, ResolvableSerializer, JsonFormatVisitable, SchemaAware
{
    protected static final PropertyName NAME_FOR_OBJECT_REF;
    protected static final BeanPropertyWriter[] NO_PROPS;
    protected final BeanPropertyWriter[] _props;
    protected final BeanPropertyWriter[] _filteredProps;
    protected final AnyGetterWriter _anyGetterWriter;
    protected final Object _propertyFilterId;
    protected final AnnotatedMember _typeId;
    protected final ObjectIdWriter _objectIdWriter;
    protected final JsonFormat.Shape _serializationShape;
    
    protected BeanSerializerBase(final JavaType type, final BeanSerializerBuilder builder, final BeanPropertyWriter[] properties, final BeanPropertyWriter[] filteredProperties) {
        super(type);
        this._props = properties;
        this._filteredProps = filteredProperties;
        if (builder == null) {
            this._typeId = null;
            this._anyGetterWriter = null;
            this._propertyFilterId = null;
            this._objectIdWriter = null;
            this._serializationShape = null;
        }
        else {
            this._typeId = builder.getTypeId();
            this._anyGetterWriter = builder.getAnyGetter();
            this._propertyFilterId = builder.getFilterId();
            this._objectIdWriter = builder.getObjectIdWriter();
            final JsonFormat.Value format = builder.getBeanDescription().findExpectedFormat(null);
            this._serializationShape = ((format == null) ? null : format.getShape());
        }
    }
    
    public BeanSerializerBase(final BeanSerializerBase src, final BeanPropertyWriter[] properties, final BeanPropertyWriter[] filteredProperties) {
        super(src._handledType);
        this._props = properties;
        this._filteredProps = filteredProperties;
        this._typeId = src._typeId;
        this._anyGetterWriter = src._anyGetterWriter;
        this._objectIdWriter = src._objectIdWriter;
        this._propertyFilterId = src._propertyFilterId;
        this._serializationShape = src._serializationShape;
    }
    
    protected BeanSerializerBase(final BeanSerializerBase src, final ObjectIdWriter objectIdWriter) {
        this(src, objectIdWriter, src._propertyFilterId);
    }
    
    protected BeanSerializerBase(final BeanSerializerBase src, final ObjectIdWriter objectIdWriter, final Object filterId) {
        super(src._handledType);
        this._props = src._props;
        this._filteredProps = src._filteredProps;
        this._typeId = src._typeId;
        this._anyGetterWriter = src._anyGetterWriter;
        this._objectIdWriter = objectIdWriter;
        this._propertyFilterId = filterId;
        this._serializationShape = src._serializationShape;
    }
    
    protected BeanSerializerBase(final BeanSerializerBase src, final String[] toIgnore) {
        super(src._handledType);
        final HashSet<String> ignoredSet = ArrayBuilders.arrayToSet(toIgnore);
        final BeanPropertyWriter[] propsIn = src._props;
        final BeanPropertyWriter[] fpropsIn = src._filteredProps;
        final int len = propsIn.length;
        final ArrayList<BeanPropertyWriter> propsOut = new ArrayList<BeanPropertyWriter>(len);
        final ArrayList<BeanPropertyWriter> fpropsOut = (fpropsIn == null) ? null : new ArrayList<BeanPropertyWriter>(len);
        for (int i = 0; i < len; ++i) {
            final BeanPropertyWriter bpw = propsIn[i];
            if (!ignoredSet.contains(bpw.getName())) {
                propsOut.add(bpw);
                if (fpropsIn != null) {
                    fpropsOut.add(fpropsIn[i]);
                }
            }
        }
        this._props = propsOut.toArray(new BeanPropertyWriter[propsOut.size()]);
        this._filteredProps = (BeanPropertyWriter[])((fpropsOut == null) ? null : ((BeanPropertyWriter[])fpropsOut.toArray(new BeanPropertyWriter[fpropsOut.size()])));
        this._typeId = src._typeId;
        this._anyGetterWriter = src._anyGetterWriter;
        this._objectIdWriter = src._objectIdWriter;
        this._propertyFilterId = src._propertyFilterId;
        this._serializationShape = src._serializationShape;
    }
    
    public abstract BeanSerializerBase withObjectIdWriter(final ObjectIdWriter p0);
    
    protected abstract BeanSerializerBase withIgnorals(final String[] p0);
    
    protected abstract BeanSerializerBase asArraySerializer();
    
    protected abstract BeanSerializerBase withFilterId(final Object p0);
    
    protected BeanSerializerBase(final BeanSerializerBase src) {
        this(src, src._props, src._filteredProps);
    }
    
    protected BeanSerializerBase(final BeanSerializerBase src, final NameTransformer unwrapper) {
        this(src, rename(src._props, unwrapper), rename(src._filteredProps, unwrapper));
    }
    
    private static final BeanPropertyWriter[] rename(final BeanPropertyWriter[] props, final NameTransformer transformer) {
        if (props == null || props.length == 0 || transformer == null || transformer == NameTransformer.NOP) {
            return props;
        }
        final int len = props.length;
        final BeanPropertyWriter[] result = new BeanPropertyWriter[len];
        for (int i = 0; i < len; ++i) {
            final BeanPropertyWriter bpw = props[i];
            if (bpw != null) {
                result[i] = bpw.rename(transformer);
            }
        }
        return result;
    }
    
    @Override
    public void resolve(final SerializerProvider provider) throws JsonMappingException {
        final int filteredCount = (this._filteredProps == null) ? 0 : this._filteredProps.length;
        for (int i = 0, len = this._props.length; i < len; ++i) {
            final BeanPropertyWriter prop = this._props[i];
            if (!prop.willSuppressNulls() && !prop.hasNullSerializer()) {
                final JsonSerializer<Object> nullSer = provider.findNullValueSerializer(prop);
                if (nullSer != null) {
                    prop.assignNullSerializer(nullSer);
                    if (i < filteredCount) {
                        final BeanPropertyWriter w2 = this._filteredProps[i];
                        if (w2 != null) {
                            w2.assignNullSerializer(nullSer);
                        }
                    }
                }
            }
            if (!prop.hasSerializer()) {
                JsonSerializer<Object> ser = this.findConvertingSerializer(provider, prop);
                if (ser == null) {
                    JavaType type = prop.getSerializationType();
                    if (type == null) {
                        type = provider.constructType(prop.getGenericPropertyType());
                        if (!type.isFinal()) {
                            if (type.isContainerType() || type.containedTypeCount() > 0) {
                                prop.setNonTrivialBaseType(type);
                            }
                            continue;
                        }
                    }
                    ser = provider.findValueSerializer(type, prop);
                    if (type.isContainerType()) {
                        final TypeSerializer typeSer = type.getContentType().getTypeHandler();
                        if (typeSer != null && ser instanceof ContainerSerializer) {
                            final JsonSerializer<Object> ser2 = ser = (JsonSerializer<Object>)((ContainerSerializer)ser).withValueTypeSerializer(typeSer);
                        }
                    }
                }
                prop.assignSerializer(ser);
                if (i < filteredCount) {
                    final BeanPropertyWriter w2 = this._filteredProps[i];
                    if (w2 != null) {
                        w2.assignSerializer(ser);
                    }
                }
            }
        }
        if (this._anyGetterWriter != null) {
            this._anyGetterWriter.resolve(provider);
        }
    }
    
    protected JsonSerializer<Object> findConvertingSerializer(final SerializerProvider provider, final BeanPropertyWriter prop) throws JsonMappingException {
        final AnnotationIntrospector intr = provider.getAnnotationIntrospector();
        if (intr != null) {
            final Object convDef = intr.findSerializationConverter(prop.getMember());
            if (convDef != null) {
                final Converter<Object, Object> conv = provider.converterInstance(prop.getMember(), convDef);
                final JavaType delegateType = conv.getOutputType(provider.getTypeFactory());
                final JsonSerializer<?> ser = provider.findValueSerializer(delegateType, prop);
                return new StdDelegatingSerializer(conv, delegateType, ser);
            }
        }
        return null;
    }
    
    @Override
    public JsonSerializer<?> createContextual(final SerializerProvider provider, final BeanProperty property) throws JsonMappingException {
        ObjectIdWriter oiw = this._objectIdWriter;
        String[] ignorals = null;
        Object newFilterId = null;
        final AnnotationIntrospector intr = provider.getAnnotationIntrospector();
        final AnnotatedMember accessor = (property == null || intr == null) ? null : property.getMember();
        if (accessor != null) {
            ignorals = intr.findPropertiesToIgnore(accessor);
            ObjectIdInfo objectIdInfo = intr.findObjectIdInfo(accessor);
            Label_0408: {
                if (objectIdInfo == null) {
                    if (oiw != null) {
                        objectIdInfo = intr.findObjectReferenceInfo(accessor, new ObjectIdInfo(BeanSerializerBase.NAME_FOR_OBJECT_REF, null, null, null));
                        oiw = this._objectIdWriter.withAlwaysAsId(objectIdInfo.getAlwaysAsId());
                    }
                }
                else {
                    objectIdInfo = intr.findObjectReferenceInfo(accessor, objectIdInfo);
                    final Class<?> implClass = objectIdInfo.getGeneratorType();
                    final JavaType type = provider.constructType(implClass);
                    JavaType idType = provider.getTypeFactory().findTypeParameters(type, ObjectIdGenerator.class)[0];
                    if (implClass == ObjectIdGenerators.PropertyGenerator.class) {
                        final String propName = objectIdInfo.getPropertyName().getSimpleName();
                        BeanPropertyWriter idProp = null;
                        for (int i = 0, len = this._props.length; i != len; ++i) {
                            final BeanPropertyWriter prop = this._props[i];
                            if (propName.equals(prop.getName())) {
                                idProp = prop;
                                if (i > 0) {
                                    System.arraycopy(this._props, 0, this._props, 1, i);
                                    this._props[0] = idProp;
                                    if (this._filteredProps != null) {
                                        final BeanPropertyWriter fp = this._filteredProps[i];
                                        System.arraycopy(this._filteredProps, 0, this._filteredProps, 1, i);
                                        this._filteredProps[0] = fp;
                                    }
                                }
                                idType = idProp.getType();
                                final ObjectIdGenerator<?> gen = new PropertyBasedObjectIdGenerator(objectIdInfo, idProp);
                                oiw = ObjectIdWriter.construct(idType, (PropertyName)null, gen, objectIdInfo.getAlwaysAsId());
                                break Label_0408;
                            }
                        }
                        throw new IllegalArgumentException("Invalid Object Id definition for " + this._handledType.getName() + ": can not find property with name '" + propName + "'");
                    }
                    final ObjectIdGenerator<?> gen = provider.objectIdGeneratorInstance(accessor, objectIdInfo);
                    oiw = ObjectIdWriter.construct(idType, objectIdInfo.getPropertyName(), gen, objectIdInfo.getAlwaysAsId());
                }
            }
            final Object filterId = intr.findFilterId(accessor);
            if (filterId != null && (this._propertyFilterId == null || !filterId.equals(this._propertyFilterId))) {
                newFilterId = filterId;
            }
        }
        BeanSerializerBase contextual = this;
        if (oiw != null) {
            final JsonSerializer<?> ser = provider.findValueSerializer(oiw.idType, property);
            oiw = oiw.withSerializer(ser);
            if (oiw != this._objectIdWriter) {
                contextual = contextual.withObjectIdWriter(oiw);
            }
        }
        if (ignorals != null && ignorals.length != 0) {
            contextual = contextual.withIgnorals(ignorals);
        }
        if (newFilterId != null) {
            contextual = contextual.withFilterId(newFilterId);
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
            contextual = contextual.asArraySerializer();
        }
        return contextual;
    }
    
    @Override
    public boolean usesObjectId() {
        return this._objectIdWriter != null;
    }
    
    @Override
    public abstract void serialize(final Object p0, final JsonGenerator p1, final SerializerProvider p2) throws IOException, JsonGenerationException;
    
    @Override
    public void serializeWithType(final Object bean, final JsonGenerator jgen, final SerializerProvider provider, final TypeSerializer typeSer) throws IOException, JsonGenerationException {
        if (this._objectIdWriter != null) {
            this._serializeWithObjectId(bean, jgen, provider, typeSer);
            return;
        }
        final String typeStr = (this._typeId == null) ? null : this._customTypeId(bean);
        if (typeStr == null) {
            typeSer.writeTypePrefixForObject(bean, jgen);
        }
        else {
            typeSer.writeCustomTypePrefixForObject(bean, jgen, typeStr);
        }
        if (this._propertyFilterId != null) {
            this.serializeFieldsFiltered(bean, jgen, provider);
        }
        else {
            this.serializeFields(bean, jgen, provider);
        }
        if (typeStr == null) {
            typeSer.writeTypeSuffixForObject(bean, jgen);
        }
        else {
            typeSer.writeCustomTypeSuffixForObject(bean, jgen, typeStr);
        }
    }
    
    protected final void _serializeWithObjectId(final Object bean, final JsonGenerator jgen, final SerializerProvider provider, final boolean startEndObject) throws IOException, JsonGenerationException {
        final ObjectIdWriter w = this._objectIdWriter;
        final WritableObjectId objectId = provider.findObjectId(bean, w.generator);
        if (objectId.writeAsId(jgen, provider, w)) {
            return;
        }
        final Object id = objectId.generateId(bean);
        if (w.alwaysAsId) {
            w.serializer.serialize(id, jgen, provider);
            return;
        }
        if (startEndObject) {
            jgen.writeStartObject();
        }
        objectId.writeAsField(jgen, provider, w);
        if (this._propertyFilterId != null) {
            this.serializeFieldsFiltered(bean, jgen, provider);
        }
        else {
            this.serializeFields(bean, jgen, provider);
        }
        if (startEndObject) {
            jgen.writeEndObject();
        }
    }
    
    protected final void _serializeWithObjectId(final Object bean, final JsonGenerator jgen, final SerializerProvider provider, final TypeSerializer typeSer) throws IOException, JsonGenerationException {
        final ObjectIdWriter w = this._objectIdWriter;
        final WritableObjectId objectId = provider.findObjectId(bean, w.generator);
        if (objectId.writeAsId(jgen, provider, w)) {
            return;
        }
        final Object id = objectId.generateId(bean);
        if (w.alwaysAsId) {
            w.serializer.serialize(id, jgen, provider);
            return;
        }
        this._serializeObjectId(bean, jgen, provider, typeSer, objectId);
    }
    
    protected void _serializeObjectId(final Object bean, final JsonGenerator jgen, final SerializerProvider provider, final TypeSerializer typeSer, final WritableObjectId objectId) throws IOException, JsonProcessingException, JsonGenerationException {
        final ObjectIdWriter w = this._objectIdWriter;
        final String typeStr = (this._typeId == null) ? null : this._customTypeId(bean);
        if (typeStr == null) {
            typeSer.writeTypePrefixForObject(bean, jgen);
        }
        else {
            typeSer.writeCustomTypePrefixForObject(bean, jgen, typeStr);
        }
        objectId.writeAsField(jgen, provider, w);
        if (this._propertyFilterId != null) {
            this.serializeFieldsFiltered(bean, jgen, provider);
        }
        else {
            this.serializeFields(bean, jgen, provider);
        }
        if (typeStr == null) {
            typeSer.writeTypeSuffixForObject(bean, jgen);
        }
        else {
            typeSer.writeCustomTypeSuffixForObject(bean, jgen, typeStr);
        }
    }
    
    private final String _customTypeId(final Object bean) {
        final Object typeId = this._typeId.getValue(bean);
        if (typeId == null) {
            return "";
        }
        return (String)((typeId instanceof String) ? typeId : typeId.toString());
    }
    
    protected void serializeFields(final Object bean, final JsonGenerator jgen, final SerializerProvider provider) throws IOException, JsonGenerationException {
        BeanPropertyWriter[] props;
        if (this._filteredProps != null && provider.getActiveView() != null) {
            props = this._filteredProps;
        }
        else {
            props = this._props;
        }
        int i = 0;
        try {
            for (int len = props.length; i < len; ++i) {
                final BeanPropertyWriter prop = props[i];
                if (prop != null) {
                    prop.serializeAsField(bean, jgen, provider);
                }
            }
            if (this._anyGetterWriter != null) {
                this._anyGetterWriter.getAndSerialize(bean, jgen, provider);
            }
        }
        catch (Exception e) {
            final String name = (i == props.length) ? "[anySetter]" : props[i].getName();
            this.wrapAndThrow(provider, e, bean, name);
        }
        catch (StackOverflowError e2) {
            final JsonMappingException mapE = new JsonMappingException("Infinite recursion (StackOverflowError)", e2);
            final String name2 = (i == props.length) ? "[anySetter]" : props[i].getName();
            mapE.prependPath(new JsonMappingException.Reference(bean, name2));
            throw mapE;
        }
    }
    
    protected void serializeFieldsFiltered(final Object bean, final JsonGenerator jgen, final SerializerProvider provider) throws IOException, JsonGenerationException {
        BeanPropertyWriter[] props;
        if (this._filteredProps != null && provider.getActiveView() != null) {
            props = this._filteredProps;
        }
        else {
            props = this._props;
        }
        final PropertyFilter filter = this.findPropertyFilter(provider, this._propertyFilterId, bean);
        if (filter == null) {
            this.serializeFields(bean, jgen, provider);
            return;
        }
        int i = 0;
        try {
            for (int len = props.length; i < len; ++i) {
                final BeanPropertyWriter prop = props[i];
                if (prop != null) {
                    filter.serializeAsField(bean, jgen, provider, prop);
                }
            }
            if (this._anyGetterWriter != null) {
                this._anyGetterWriter.getAndFilter(bean, jgen, provider, filter);
            }
        }
        catch (Exception e) {
            final String name = (i == props.length) ? "[anySetter]" : props[i].getName();
            this.wrapAndThrow(provider, e, bean, name);
        }
        catch (StackOverflowError e2) {
            final JsonMappingException mapE = new JsonMappingException("Infinite recursion (StackOverflowError)", e2);
            final String name2 = (i == props.length) ? "[anySetter]" : props[i].getName();
            mapE.prependPath(new JsonMappingException.Reference(bean, name2));
            throw mapE;
        }
    }
    
    @Deprecated
    @Override
    public JsonNode getSchema(final SerializerProvider provider, final Type typeHint) throws JsonMappingException {
        final ObjectNode o = this.createSchemaNode("object", true);
        final JsonSerializableSchema ann = this._handledType.getAnnotation(JsonSerializableSchema.class);
        if (ann != null) {
            final String id = ann.id();
            if (id != null && id.length() > 0) {
                o.put("id", id);
            }
        }
        final ObjectNode propertiesNode = o.objectNode();
        PropertyFilter filter;
        if (this._propertyFilterId != null) {
            filter = this.findPropertyFilter(provider, this._propertyFilterId, null);
        }
        else {
            filter = null;
        }
        for (int i = 0; i < this._props.length; ++i) {
            final BeanPropertyWriter prop = this._props[i];
            if (filter == null) {
                prop.depositSchemaProperty(propertiesNode, provider);
            }
            else {
                filter.depositSchemaProperty(prop, propertiesNode, provider);
            }
        }
        o.put("properties", propertiesNode);
        return o;
    }
    
    @Override
    public void acceptJsonFormatVisitor(final JsonFormatVisitorWrapper visitor, final JavaType typeHint) throws JsonMappingException {
        if (visitor == null) {
            return;
        }
        final JsonObjectFormatVisitor objectVisitor = visitor.expectObjectFormat(typeHint);
        if (objectVisitor == null) {
            return;
        }
        if (this._propertyFilterId != null) {
            final PropertyFilter filter = this.findPropertyFilter(visitor.getProvider(), this._propertyFilterId, null);
            for (int i = 0; i < this._props.length; ++i) {
                filter.depositSchemaProperty(this._props[i], objectVisitor, visitor.getProvider());
            }
        }
        else {
            for (int j = 0; j < this._props.length; ++j) {
                this._props[j].depositSchemaProperty(objectVisitor);
            }
        }
    }
    
    static {
        NAME_FOR_OBJECT_REF = new PropertyName("#object-ref");
        NO_PROPS = new BeanPropertyWriter[0];
    }
}
