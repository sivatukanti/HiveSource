// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.ser;

import org.apache.htrace.shaded.fasterxml.jackson.databind.ser.std.BeanSerializerBase;
import org.apache.htrace.shaded.fasterxml.jackson.databind.SerializationFeature;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsonschema.JsonSchema;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsonschema.SchemaAware;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonMappingException;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsonFormatVisitors.JsonObjectFormatVisitor;
import org.apache.htrace.shaded.fasterxml.jackson.databind.SerializerProvider;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonGenerator;
import org.apache.htrace.shaded.fasterxml.jackson.databind.AnnotationIntrospector;
import java.lang.reflect.Type;
import org.apache.htrace.shaded.fasterxml.jackson.core.SerializableString;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonNode;
import org.apache.htrace.shaded.fasterxml.jackson.databind.node.ObjectNode;
import java.lang.annotation.Annotation;
import org.apache.htrace.shaded.fasterxml.jackson.databind.ser.impl.UnwrappingBeanPropertyWriter;
import org.apache.htrace.shaded.fasterxml.jackson.databind.util.NameTransformer;
import java.util.Map;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.AnnotatedField;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import org.apache.htrace.shaded.fasterxml.jackson.databind.PropertyMetadata;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsontype.TypeSerializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.ser.impl.PropertySerializerMap;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonSerializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.PropertyName;
import org.apache.htrace.shaded.fasterxml.jackson.core.io.SerializedString;
import java.util.HashMap;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JavaType;
import org.apache.htrace.shaded.fasterxml.jackson.databind.util.Annotations;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.AnnotatedMember;
import org.apache.htrace.shaded.fasterxml.jackson.databind.BeanProperty;

public class BeanPropertyWriter extends PropertyWriter implements BeanProperty
{
    public static final Object MARKER_FOR_EMPTY;
    protected final AnnotatedMember _member;
    protected final Annotations _contextAnnotations;
    protected final JavaType _declaredType;
    protected final Method _accessorMethod;
    protected final Field _field;
    protected HashMap<Object, Object> _internalSettings;
    protected final SerializedString _name;
    protected final PropertyName _wrapperName;
    protected final JavaType _cfgSerializationType;
    protected JsonSerializer<Object> _serializer;
    protected JsonSerializer<Object> _nullSerializer;
    protected transient PropertySerializerMap _dynamicSerializers;
    protected final boolean _suppressNulls;
    protected final Object _suppressableValue;
    protected final Class<?>[] _includeInViews;
    protected TypeSerializer _typeSerializer;
    protected JavaType _nonTrivialBaseType;
    protected final PropertyMetadata _metadata;
    
    public BeanPropertyWriter(final BeanPropertyDefinition propDef, final AnnotatedMember member, final Annotations contextAnnotations, final JavaType declaredType, final JsonSerializer<?> ser, final TypeSerializer typeSer, final JavaType serType, final boolean suppressNulls, final Object suppressableValue) {
        this._member = member;
        this._contextAnnotations = contextAnnotations;
        this._name = new SerializedString(propDef.getName());
        this._wrapperName = propDef.getWrapperName();
        this._declaredType = declaredType;
        this._serializer = (JsonSerializer<Object>)ser;
        this._dynamicSerializers = ((ser == null) ? PropertySerializerMap.emptyMap() : null);
        this._typeSerializer = typeSer;
        this._cfgSerializationType = serType;
        this._metadata = propDef.getMetadata();
        if (member instanceof AnnotatedField) {
            this._accessorMethod = null;
            this._field = (Field)member.getMember();
        }
        else {
            if (!(member instanceof AnnotatedMethod)) {
                throw new IllegalArgumentException("Can not pass member of type " + member.getClass().getName());
            }
            this._accessorMethod = (Method)member.getMember();
            this._field = null;
        }
        this._suppressNulls = suppressNulls;
        this._suppressableValue = suppressableValue;
        this._includeInViews = propDef.findViews();
        this._nullSerializer = null;
    }
    
    protected BeanPropertyWriter(final BeanPropertyWriter base) {
        this(base, base._name);
    }
    
    protected BeanPropertyWriter(final BeanPropertyWriter base, final SerializedString name) {
        this._name = name;
        this._wrapperName = base._wrapperName;
        this._member = base._member;
        this._contextAnnotations = base._contextAnnotations;
        this._declaredType = base._declaredType;
        this._accessorMethod = base._accessorMethod;
        this._field = base._field;
        this._serializer = base._serializer;
        this._nullSerializer = base._nullSerializer;
        if (base._internalSettings != null) {
            this._internalSettings = new HashMap<Object, Object>(base._internalSettings);
        }
        this._cfgSerializationType = base._cfgSerializationType;
        this._dynamicSerializers = base._dynamicSerializers;
        this._suppressNulls = base._suppressNulls;
        this._suppressableValue = base._suppressableValue;
        this._includeInViews = base._includeInViews;
        this._typeSerializer = base._typeSerializer;
        this._nonTrivialBaseType = base._nonTrivialBaseType;
        this._metadata = base._metadata;
    }
    
    public BeanPropertyWriter rename(final NameTransformer transformer) {
        final String newName = transformer.transform(this._name.getValue());
        if (newName.equals(this._name.toString())) {
            return this;
        }
        return new BeanPropertyWriter(this, new SerializedString(newName));
    }
    
    public void assignSerializer(final JsonSerializer<Object> ser) {
        if (this._serializer != null && this._serializer != ser) {
            throw new IllegalStateException("Can not override serializer");
        }
        this._serializer = ser;
    }
    
    public void assignNullSerializer(final JsonSerializer<Object> nullSer) {
        if (this._nullSerializer != null && this._nullSerializer != nullSer) {
            throw new IllegalStateException("Can not override null serializer");
        }
        this._nullSerializer = nullSer;
    }
    
    public BeanPropertyWriter unwrappingWriter(final NameTransformer unwrapper) {
        return new UnwrappingBeanPropertyWriter(this, unwrapper);
    }
    
    public void setNonTrivialBaseType(final JavaType t) {
        this._nonTrivialBaseType = t;
    }
    
    @Override
    public String getName() {
        return this._name.getValue();
    }
    
    @Override
    public PropertyName getFullName() {
        return new PropertyName(this._name.getValue());
    }
    
    @Override
    public JavaType getType() {
        return this._declaredType;
    }
    
    @Override
    public PropertyName getWrapperName() {
        return this._wrapperName;
    }
    
    @Override
    public boolean isRequired() {
        return this._metadata.isRequired();
    }
    
    @Override
    public PropertyMetadata getMetadata() {
        return this._metadata;
    }
    
    @Override
    public <A extends Annotation> A getAnnotation(final Class<A> acls) {
        return this._member.getAnnotation(acls);
    }
    
    @Override
    public <A extends Annotation> A getContextAnnotation(final Class<A> acls) {
        return this._contextAnnotations.get(acls);
    }
    
    @Override
    public AnnotatedMember getMember() {
        return this._member;
    }
    
    protected void _depositSchemaProperty(final ObjectNode propertiesNode, final JsonNode schemaNode) {
        propertiesNode.set(this.getName(), schemaNode);
    }
    
    public Object getInternalSetting(final Object key) {
        return (this._internalSettings == null) ? null : this._internalSettings.get(key);
    }
    
    public Object setInternalSetting(final Object key, final Object value) {
        if (this._internalSettings == null) {
            this._internalSettings = new HashMap<Object, Object>();
        }
        return this._internalSettings.put(key, value);
    }
    
    public Object removeInternalSetting(final Object key) {
        Object removed = null;
        if (this._internalSettings != null) {
            removed = this._internalSettings.remove(key);
            if (this._internalSettings.size() == 0) {
                this._internalSettings = null;
            }
        }
        return removed;
    }
    
    public SerializableString getSerializedName() {
        return this._name;
    }
    
    public boolean hasSerializer() {
        return this._serializer != null;
    }
    
    public boolean hasNullSerializer() {
        return this._nullSerializer != null;
    }
    
    public boolean isUnwrapping() {
        return false;
    }
    
    public boolean willSuppressNulls() {
        return this._suppressNulls;
    }
    
    public JsonSerializer<Object> getSerializer() {
        return this._serializer;
    }
    
    public JavaType getSerializationType() {
        return this._cfgSerializationType;
    }
    
    public Class<?> getRawSerializationType() {
        return (this._cfgSerializationType == null) ? null : this._cfgSerializationType.getRawClass();
    }
    
    public Class<?> getPropertyType() {
        return (this._accessorMethod != null) ? this._accessorMethod.getReturnType() : this._field.getType();
    }
    
    public Type getGenericPropertyType() {
        if (this._accessorMethod != null) {
            return this._accessorMethod.getGenericReturnType();
        }
        return this._field.getGenericType();
    }
    
    public Class<?>[] getViews() {
        return this._includeInViews;
    }
    
    @Deprecated
    protected boolean isRequired(final AnnotationIntrospector intr) {
        return this._metadata.isRequired();
    }
    
    @Override
    public void serializeAsField(final Object bean, final JsonGenerator jgen, final SerializerProvider prov) throws Exception {
        final Object value = this.get(bean);
        if (value == null) {
            if (this._nullSerializer != null) {
                jgen.writeFieldName(this._name);
                this._nullSerializer.serialize(null, jgen, prov);
            }
            return;
        }
        JsonSerializer<Object> ser = this._serializer;
        if (ser == null) {
            final Class<?> cls = value.getClass();
            final PropertySerializerMap m = this._dynamicSerializers;
            ser = m.serializerFor(cls);
            if (ser == null) {
                ser = this._findAndAddDynamic(m, cls, prov);
            }
        }
        if (this._suppressableValue != null) {
            if (BeanPropertyWriter.MARKER_FOR_EMPTY == this._suppressableValue) {
                if (ser.isEmpty(value)) {
                    return;
                }
            }
            else if (this._suppressableValue.equals(value)) {
                return;
            }
        }
        if (value == bean && this._handleSelfReference(bean, jgen, prov, ser)) {
            return;
        }
        jgen.writeFieldName(this._name);
        if (this._typeSerializer == null) {
            ser.serialize(value, jgen, prov);
        }
        else {
            ser.serializeWithType(value, jgen, prov, this._typeSerializer);
        }
    }
    
    @Override
    public void serializeAsOmittedField(final Object bean, final JsonGenerator jgen, final SerializerProvider prov) throws Exception {
        if (!jgen.canOmitFields()) {
            jgen.writeOmittedField(this._name.getValue());
        }
    }
    
    @Override
    public void serializeAsElement(final Object bean, final JsonGenerator jgen, final SerializerProvider prov) throws Exception {
        final Object value = this.get(bean);
        if (value == null) {
            if (this._nullSerializer != null) {
                this._nullSerializer.serialize(null, jgen, prov);
            }
            else {
                jgen.writeNull();
            }
            return;
        }
        JsonSerializer<Object> ser = this._serializer;
        if (ser == null) {
            final Class<?> cls = value.getClass();
            final PropertySerializerMap map = this._dynamicSerializers;
            ser = map.serializerFor(cls);
            if (ser == null) {
                ser = this._findAndAddDynamic(map, cls, prov);
            }
        }
        if (this._suppressableValue != null) {
            if (BeanPropertyWriter.MARKER_FOR_EMPTY == this._suppressableValue) {
                if (ser.isEmpty(value)) {
                    this.serializeAsPlaceholder(bean, jgen, prov);
                    return;
                }
            }
            else if (this._suppressableValue.equals(value)) {
                this.serializeAsPlaceholder(bean, jgen, prov);
                return;
            }
        }
        if (value == bean && this._handleSelfReference(bean, jgen, prov, ser)) {
            return;
        }
        if (this._typeSerializer == null) {
            ser.serialize(value, jgen, prov);
        }
        else {
            ser.serializeWithType(value, jgen, prov, this._typeSerializer);
        }
    }
    
    @Override
    public void serializeAsPlaceholder(final Object bean, final JsonGenerator jgen, final SerializerProvider prov) throws Exception {
        if (this._nullSerializer != null) {
            this._nullSerializer.serialize(null, jgen, prov);
        }
        else {
            jgen.writeNull();
        }
    }
    
    @Override
    public void depositSchemaProperty(final JsonObjectFormatVisitor objectVisitor) throws JsonMappingException {
        if (objectVisitor != null) {
            if (this.isRequired()) {
                objectVisitor.property(this);
            }
            else {
                objectVisitor.optionalProperty(this);
            }
        }
    }
    
    @Deprecated
    @Override
    public void depositSchemaProperty(final ObjectNode propertiesNode, final SerializerProvider provider) throws JsonMappingException {
        final JavaType propType = this.getSerializationType();
        final Type hint = (propType == null) ? this.getGenericPropertyType() : propType.getRawClass();
        JsonSerializer<Object> ser = this.getSerializer();
        if (ser == null) {
            Class<?> serType = this.getRawSerializationType();
            if (serType == null) {
                serType = this.getPropertyType();
            }
            ser = provider.findValueSerializer(serType, this);
        }
        final boolean isOptional = !this.isRequired();
        JsonNode schemaNode;
        if (ser instanceof SchemaAware) {
            schemaNode = ((SchemaAware)ser).getSchema(provider, hint, isOptional);
        }
        else {
            schemaNode = JsonSchema.getDefaultSchemaNode();
        }
        this._depositSchemaProperty(propertiesNode, schemaNode);
    }
    
    protected JsonSerializer<Object> _findAndAddDynamic(final PropertySerializerMap map, final Class<?> type, final SerializerProvider provider) throws JsonMappingException {
        PropertySerializerMap.SerializerAndMapResult result;
        if (this._nonTrivialBaseType != null) {
            final JavaType t = provider.constructSpecializedType(this._nonTrivialBaseType, type);
            result = map.findAndAddPrimarySerializer(t, provider, this);
        }
        else {
            result = map.findAndAddPrimarySerializer(type, provider, this);
        }
        if (map != result.map) {
            this._dynamicSerializers = result.map;
        }
        return result.serializer;
    }
    
    public final Object get(final Object bean) throws Exception {
        if (this._accessorMethod != null) {
            return this._accessorMethod.invoke(bean, new Object[0]);
        }
        return this._field.get(bean);
    }
    
    @Deprecated
    protected void _handleSelfReference(final Object bean, final JsonSerializer<?> ser) throws JsonMappingException {
        this._handleSelfReference(bean, null, null, ser);
    }
    
    protected boolean _handleSelfReference(final Object bean, final JsonGenerator jgen, final SerializerProvider prov, final JsonSerializer<?> ser) throws JsonMappingException {
        if (prov.isEnabled(SerializationFeature.FAIL_ON_SELF_REFERENCES) && !ser.usesObjectId() && ser instanceof BeanSerializerBase) {
            throw new JsonMappingException("Direct self-reference leading to cycle");
        }
        return false;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(40);
        sb.append("property '").append(this.getName()).append("' (");
        if (this._accessorMethod != null) {
            sb.append("via method ").append(this._accessorMethod.getDeclaringClass().getName()).append("#").append(this._accessorMethod.getName());
        }
        else {
            sb.append("field \"").append(this._field.getDeclaringClass().getName()).append("#").append(this._field.getName());
        }
        if (this._serializer == null) {
            sb.append(", no static serializer");
        }
        else {
            sb.append(", static serializer of type " + this._serializer.getClass().getName());
        }
        sb.append(')');
        return sb.toString();
    }
    
    static {
        MARKER_FOR_EMPTY = new Object();
    }
}
