// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.deser;

import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.impl.FailingDeserializer;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonLocation;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonToken;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import org.apache.htrace.shaded.fasterxml.jackson.databind.DeserializationContext;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonParser;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonMappingException;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsonFormatVisitors.JsonObjectFormatVisitor;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.AnnotatedMember;
import java.lang.annotation.Annotation;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import org.apache.htrace.shaded.fasterxml.jackson.databind.util.ViewMatcher;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.ObjectIdInfo;
import org.apache.htrace.shaded.fasterxml.jackson.databind.PropertyMetadata;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.impl.NullProvider;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.util.Annotations;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JavaType;
import org.apache.htrace.shaded.fasterxml.jackson.databind.PropertyName;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonDeserializer;
import java.io.Serializable;
import org.apache.htrace.shaded.fasterxml.jackson.databind.BeanProperty;

public abstract class SettableBeanProperty implements BeanProperty, Serializable
{
    protected static final JsonDeserializer<Object> MISSING_VALUE_DESERIALIZER;
    protected final PropertyName _propName;
    protected final JavaType _type;
    protected final PropertyName _wrapperName;
    protected final transient Annotations _contextAnnotations;
    protected final JsonDeserializer<Object> _valueDeserializer;
    protected final TypeDeserializer _valueTypeDeserializer;
    protected final NullProvider _nullProvider;
    protected final PropertyMetadata _metadata;
    protected String _managedReferenceName;
    protected ObjectIdInfo _objectIdInfo;
    protected ViewMatcher _viewMatcher;
    protected int _propertyIndex;
    
    protected SettableBeanProperty(final BeanPropertyDefinition propDef, final JavaType type, final TypeDeserializer typeDeser, final Annotations contextAnnotations) {
        this(propDef.getFullName(), type, propDef.getWrapperName(), typeDeser, contextAnnotations, propDef.getMetadata());
    }
    
    @Deprecated
    protected SettableBeanProperty(final String propName, final JavaType type, final PropertyName wrapper, final TypeDeserializer typeDeser, final Annotations contextAnnotations) {
        this(new PropertyName(propName), type, wrapper, typeDeser, contextAnnotations, PropertyMetadata.STD_OPTIONAL);
    }
    
    @Deprecated
    protected SettableBeanProperty(final String propName, final JavaType type, final PropertyName wrapper, final TypeDeserializer typeDeser, final Annotations contextAnnotations, final boolean isRequired) {
        this(new PropertyName(propName), type, wrapper, typeDeser, contextAnnotations, PropertyMetadata.construct(isRequired, null, null));
    }
    
    protected SettableBeanProperty(final PropertyName propName, final JavaType type, final PropertyName wrapper, TypeDeserializer typeDeser, final Annotations contextAnnotations, final PropertyMetadata metadata) {
        this._propertyIndex = -1;
        if (propName == null) {
            this._propName = PropertyName.NO_NAME;
        }
        else {
            this._propName = propName.internSimpleName();
        }
        this._type = type;
        this._wrapperName = wrapper;
        this._metadata = metadata;
        this._contextAnnotations = contextAnnotations;
        this._viewMatcher = null;
        this._nullProvider = null;
        if (typeDeser != null) {
            typeDeser = typeDeser.forProperty(this);
        }
        this._valueTypeDeserializer = typeDeser;
        this._valueDeserializer = SettableBeanProperty.MISSING_VALUE_DESERIALIZER;
    }
    
    protected SettableBeanProperty(final PropertyName propName, final JavaType type, final PropertyMetadata metadata, final JsonDeserializer<Object> valueDeser) {
        this._propertyIndex = -1;
        if (propName == null) {
            this._propName = PropertyName.NO_NAME;
        }
        else {
            this._propName = propName.internSimpleName();
        }
        this._type = type;
        this._wrapperName = null;
        this._metadata = metadata;
        this._contextAnnotations = null;
        this._viewMatcher = null;
        this._nullProvider = null;
        this._valueTypeDeserializer = null;
        this._valueDeserializer = valueDeser;
    }
    
    protected SettableBeanProperty(final SettableBeanProperty src) {
        this._propertyIndex = -1;
        this._propName = src._propName;
        this._type = src._type;
        this._wrapperName = src._wrapperName;
        this._metadata = src._metadata;
        this._contextAnnotations = src._contextAnnotations;
        this._valueDeserializer = src._valueDeserializer;
        this._valueTypeDeserializer = src._valueTypeDeserializer;
        this._nullProvider = src._nullProvider;
        this._managedReferenceName = src._managedReferenceName;
        this._propertyIndex = src._propertyIndex;
        this._viewMatcher = src._viewMatcher;
    }
    
    protected SettableBeanProperty(final SettableBeanProperty src, final JsonDeserializer<?> deser) {
        this._propertyIndex = -1;
        this._propName = src._propName;
        this._type = src._type;
        this._wrapperName = src._wrapperName;
        this._metadata = src._metadata;
        this._contextAnnotations = src._contextAnnotations;
        this._valueTypeDeserializer = src._valueTypeDeserializer;
        this._managedReferenceName = src._managedReferenceName;
        this._propertyIndex = src._propertyIndex;
        if (deser == null) {
            this._nullProvider = null;
            this._valueDeserializer = SettableBeanProperty.MISSING_VALUE_DESERIALIZER;
        }
        else {
            final Object nvl = deser.getNullValue();
            this._nullProvider = ((nvl == null) ? null : new NullProvider(this._type, nvl));
            this._valueDeserializer = (JsonDeserializer<Object>)deser;
        }
        this._viewMatcher = src._viewMatcher;
    }
    
    @Deprecated
    protected SettableBeanProperty(final SettableBeanProperty src, final String newName) {
        this(src, new PropertyName(newName));
    }
    
    protected SettableBeanProperty(final SettableBeanProperty src, final PropertyName newName) {
        this._propertyIndex = -1;
        this._propName = newName;
        this._type = src._type;
        this._wrapperName = src._wrapperName;
        this._metadata = src._metadata;
        this._contextAnnotations = src._contextAnnotations;
        this._valueDeserializer = src._valueDeserializer;
        this._valueTypeDeserializer = src._valueTypeDeserializer;
        this._nullProvider = src._nullProvider;
        this._managedReferenceName = src._managedReferenceName;
        this._propertyIndex = src._propertyIndex;
        this._viewMatcher = src._viewMatcher;
    }
    
    public abstract SettableBeanProperty withValueDeserializer(final JsonDeserializer<?> p0);
    
    public abstract SettableBeanProperty withName(final PropertyName p0);
    
    public SettableBeanProperty withSimpleName(final String simpleName) {
        final PropertyName n = (this._propName == null) ? new PropertyName(simpleName) : this._propName.withSimpleName(simpleName);
        return (n == this._propName) ? this : this.withName(n);
    }
    
    @Deprecated
    public SettableBeanProperty withName(final String simpleName) {
        return this.withName(new PropertyName(simpleName));
    }
    
    public void setManagedReferenceName(final String n) {
        this._managedReferenceName = n;
    }
    
    public void setObjectIdInfo(final ObjectIdInfo objectIdInfo) {
        this._objectIdInfo = objectIdInfo;
    }
    
    public void setViews(final Class<?>[] views) {
        if (views == null) {
            this._viewMatcher = null;
        }
        else {
            this._viewMatcher = ViewMatcher.construct(views);
        }
    }
    
    public void assignIndex(final int index) {
        if (this._propertyIndex != -1) {
            throw new IllegalStateException("Property '" + this.getName() + "' already had index (" + this._propertyIndex + "), trying to assign " + index);
        }
        this._propertyIndex = index;
    }
    
    @Override
    public final String getName() {
        return this._propName.getSimpleName();
    }
    
    @Override
    public PropertyName getFullName() {
        return this._propName;
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
    public JavaType getType() {
        return this._type;
    }
    
    @Override
    public PropertyName getWrapperName() {
        return this._wrapperName;
    }
    
    @Override
    public abstract <A extends Annotation> A getAnnotation(final Class<A> p0);
    
    @Override
    public abstract AnnotatedMember getMember();
    
    @Override
    public <A extends Annotation> A getContextAnnotation(final Class<A> acls) {
        return this._contextAnnotations.get(acls);
    }
    
    @Override
    public void depositSchemaProperty(final JsonObjectFormatVisitor objectVisitor) throws JsonMappingException {
        if (this.isRequired()) {
            objectVisitor.property(this);
        }
        else {
            objectVisitor.optionalProperty(this);
        }
    }
    
    protected final Class<?> getDeclaringClass() {
        return this.getMember().getDeclaringClass();
    }
    
    public String getManagedReferenceName() {
        return this._managedReferenceName;
    }
    
    public ObjectIdInfo getObjectIdInfo() {
        return this._objectIdInfo;
    }
    
    public boolean hasValueDeserializer() {
        return this._valueDeserializer != null && this._valueDeserializer != SettableBeanProperty.MISSING_VALUE_DESERIALIZER;
    }
    
    public boolean hasValueTypeDeserializer() {
        return this._valueTypeDeserializer != null;
    }
    
    public JsonDeserializer<Object> getValueDeserializer() {
        final JsonDeserializer<Object> deser = this._valueDeserializer;
        if (deser == SettableBeanProperty.MISSING_VALUE_DESERIALIZER) {
            return null;
        }
        return deser;
    }
    
    public TypeDeserializer getValueTypeDeserializer() {
        return this._valueTypeDeserializer;
    }
    
    public boolean visibleInView(final Class<?> activeView) {
        return this._viewMatcher == null || this._viewMatcher.isVisibleForView(activeView);
    }
    
    public boolean hasViews() {
        return this._viewMatcher != null;
    }
    
    public int getPropertyIndex() {
        return this._propertyIndex;
    }
    
    public int getCreatorIndex() {
        return -1;
    }
    
    public Object getInjectableValueId() {
        return null;
    }
    
    public abstract void deserializeAndSet(final JsonParser p0, final DeserializationContext p1, final Object p2) throws IOException, JsonProcessingException;
    
    public abstract Object deserializeSetAndReturn(final JsonParser p0, final DeserializationContext p1, final Object p2) throws IOException, JsonProcessingException;
    
    public abstract void set(final Object p0, final Object p1) throws IOException;
    
    public abstract Object setAndReturn(final Object p0, final Object p1) throws IOException;
    
    public final Object deserialize(final JsonParser jp, final DeserializationContext ctxt) throws IOException, JsonProcessingException {
        final JsonToken t = jp.getCurrentToken();
        if (t == JsonToken.VALUE_NULL) {
            return (this._nullProvider == null) ? null : this._nullProvider.nullValue(ctxt);
        }
        if (this._valueTypeDeserializer != null) {
            return this._valueDeserializer.deserializeWithType(jp, ctxt, this._valueTypeDeserializer);
        }
        return this._valueDeserializer.deserialize(jp, ctxt);
    }
    
    protected void _throwAsIOE(final Exception e, final Object value) throws IOException {
        if (e instanceof IllegalArgumentException) {
            final String actType = (value == null) ? "[NULL]" : value.getClass().getName();
            final StringBuilder msg = new StringBuilder("Problem deserializing property '").append(this.getName());
            msg.append("' (expected type: ").append(this.getType());
            msg.append("; actual type: ").append(actType).append(")");
            final String origMsg = e.getMessage();
            if (origMsg != null) {
                msg.append(", problem: ").append(origMsg);
            }
            else {
                msg.append(" (no error message provided)");
            }
            throw new JsonMappingException(msg.toString(), null, e);
        }
        this._throwAsIOE(e);
    }
    
    protected IOException _throwAsIOE(final Exception e) throws IOException {
        if (e instanceof IOException) {
            throw (IOException)e;
        }
        if (e instanceof RuntimeException) {
            throw (RuntimeException)e;
        }
        Throwable th;
        for (th = e; th.getCause() != null; th = th.getCause()) {}
        throw new JsonMappingException(th.getMessage(), null, th);
    }
    
    @Override
    public String toString() {
        return "[property '" + this.getName() + "']";
    }
    
    static {
        MISSING_VALUE_DESERIALIZER = new FailingDeserializer("No _valueDeserializer assigned");
    }
}
