// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.deser;

import org.apache.htrace.shaded.fasterxml.jackson.core.JsonProcessingException;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonParser;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.AnnotatedMember;
import java.lang.annotation.Annotation;
import java.io.IOException;
import org.apache.htrace.shaded.fasterxml.jackson.databind.BeanProperty;
import org.apache.htrace.shaded.fasterxml.jackson.databind.DeserializationContext;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonDeserializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.PropertyMetadata;
import org.apache.htrace.shaded.fasterxml.jackson.databind.util.Annotations;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JavaType;
import org.apache.htrace.shaded.fasterxml.jackson.databind.PropertyName;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.AnnotatedParameter;

public class CreatorProperty extends SettableBeanProperty
{
    private static final long serialVersionUID = 1L;
    protected final AnnotatedParameter _annotated;
    protected final Object _injectableValueId;
    protected final int _creatorIndex;
    protected final SettableBeanProperty _fallbackSetter;
    
    public CreatorProperty(final PropertyName name, final JavaType type, final PropertyName wrapperName, final TypeDeserializer typeDeser, final Annotations contextAnnotations, final AnnotatedParameter param, final int index, final Object injectableValueId, final PropertyMetadata metadata) {
        super(name, type, wrapperName, typeDeser, contextAnnotations, metadata);
        this._annotated = param;
        this._creatorIndex = index;
        this._injectableValueId = injectableValueId;
        this._fallbackSetter = null;
    }
    
    @Deprecated
    public CreatorProperty(final String name, final JavaType type, final PropertyName wrapperName, final TypeDeserializer typeDeser, final Annotations contextAnnotations, final AnnotatedParameter param, final int index, final Object injectableValueId, final boolean isRequired) {
        this(new PropertyName(name), type, wrapperName, typeDeser, contextAnnotations, param, index, injectableValueId, PropertyMetadata.construct(isRequired, null, null));
    }
    
    protected CreatorProperty(final CreatorProperty src, final PropertyName newName) {
        super(src, newName);
        this._annotated = src._annotated;
        this._creatorIndex = src._creatorIndex;
        this._injectableValueId = src._injectableValueId;
        this._fallbackSetter = src._fallbackSetter;
    }
    
    @Deprecated
    protected CreatorProperty(final CreatorProperty src, final String newName) {
        this(src, new PropertyName(newName));
    }
    
    protected CreatorProperty(final CreatorProperty src, final JsonDeserializer<?> deser) {
        super(src, deser);
        this._annotated = src._annotated;
        this._creatorIndex = src._creatorIndex;
        this._injectableValueId = src._injectableValueId;
        this._fallbackSetter = src._fallbackSetter;
    }
    
    protected CreatorProperty(final CreatorProperty src, final SettableBeanProperty fallbackSetter) {
        super(src);
        this._annotated = src._annotated;
        this._creatorIndex = src._creatorIndex;
        this._injectableValueId = src._injectableValueId;
        this._fallbackSetter = fallbackSetter;
    }
    
    @Override
    public CreatorProperty withName(final PropertyName newName) {
        return new CreatorProperty(this, newName);
    }
    
    @Override
    public CreatorProperty withValueDeserializer(final JsonDeserializer<?> deser) {
        return new CreatorProperty(this, deser);
    }
    
    public CreatorProperty withFallbackSetter(final SettableBeanProperty fallbackSetter) {
        return new CreatorProperty(this, fallbackSetter);
    }
    
    public Object findInjectableValue(final DeserializationContext context, final Object beanInstance) {
        if (this._injectableValueId == null) {
            throw new IllegalStateException("Property '" + this.getName() + "' (type " + this.getClass().getName() + ") has no injectable value id configured");
        }
        return context.findInjectableValue(this._injectableValueId, this, beanInstance);
    }
    
    public void inject(final DeserializationContext context, final Object beanInstance) throws IOException {
        this.set(beanInstance, this.findInjectableValue(context, beanInstance));
    }
    
    @Override
    public <A extends Annotation> A getAnnotation(final Class<A> acls) {
        if (this._annotated == null) {
            return null;
        }
        return this._annotated.getAnnotation(acls);
    }
    
    @Override
    public AnnotatedMember getMember() {
        return this._annotated;
    }
    
    @Override
    public int getCreatorIndex() {
        return this._creatorIndex;
    }
    
    @Override
    public void deserializeAndSet(final JsonParser jp, final DeserializationContext ctxt, final Object instance) throws IOException, JsonProcessingException {
        this.set(instance, this.deserialize(jp, ctxt));
    }
    
    @Override
    public Object deserializeSetAndReturn(final JsonParser jp, final DeserializationContext ctxt, final Object instance) throws IOException, JsonProcessingException {
        return this.setAndReturn(instance, this.deserialize(jp, ctxt));
    }
    
    @Override
    public void set(final Object instance, final Object value) throws IOException {
        if (this._fallbackSetter == null) {
            throw new IllegalStateException("No fallback setter/field defined: can not use creator property for " + this.getClass().getName());
        }
        this._fallbackSetter.set(instance, value);
    }
    
    @Override
    public Object setAndReturn(final Object instance, final Object value) throws IOException {
        if (this._fallbackSetter == null) {
            throw new IllegalStateException("No fallback setter/field defined: can not use creator property for " + this.getClass().getName());
        }
        return this._fallbackSetter.setAndReturn(instance, value);
    }
    
    @Override
    public Object getInjectableValueId() {
        return this._injectableValueId;
    }
    
    @Override
    public String toString() {
        return "[creator property, name '" + this.getName() + "'; inject id '" + this._injectableValueId + "']";
    }
}
