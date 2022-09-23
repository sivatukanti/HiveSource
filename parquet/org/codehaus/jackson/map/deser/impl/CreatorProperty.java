// 
// Decompiled by Procyon v0.5.36
// 

package parquet.org.codehaus.jackson.map.deser.impl;

import parquet.org.codehaus.jackson.JsonProcessingException;
import parquet.org.codehaus.jackson.JsonParser;
import parquet.org.codehaus.jackson.map.introspect.AnnotatedMember;
import java.lang.annotation.Annotation;
import java.io.IOException;
import parquet.org.codehaus.jackson.map.BeanProperty;
import parquet.org.codehaus.jackson.map.DeserializationContext;
import parquet.org.codehaus.jackson.map.JsonDeserializer;
import parquet.org.codehaus.jackson.map.util.Annotations;
import parquet.org.codehaus.jackson.map.TypeDeserializer;
import parquet.org.codehaus.jackson.type.JavaType;
import parquet.org.codehaus.jackson.map.introspect.AnnotatedParameter;
import parquet.org.codehaus.jackson.map.deser.SettableBeanProperty;

public class CreatorProperty extends SettableBeanProperty
{
    protected final AnnotatedParameter _annotated;
    protected final Object _injectableValueId;
    
    public CreatorProperty(final String name, final JavaType type, final TypeDeserializer typeDeser, final Annotations contextAnnotations, final AnnotatedParameter param, final int index, final Object injectableValueId) {
        super(name, type, typeDeser, contextAnnotations);
        this._annotated = param;
        this._propertyIndex = index;
        this._injectableValueId = injectableValueId;
    }
    
    protected CreatorProperty(final CreatorProperty src, final JsonDeserializer<Object> deser) {
        super(src, deser);
        this._annotated = src._annotated;
        this._injectableValueId = src._injectableValueId;
    }
    
    @Override
    public CreatorProperty withValueDeserializer(final JsonDeserializer<Object> deser) {
        return new CreatorProperty(this, deser);
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
    public void deserializeAndSet(final JsonParser jp, final DeserializationContext ctxt, final Object instance) throws IOException, JsonProcessingException {
        this.set(instance, this.deserialize(jp, ctxt));
    }
    
    @Override
    public void set(final Object instance, final Object value) throws IOException {
    }
    
    @Override
    public Object getInjectableValueId() {
        return this._injectableValueId;
    }
}
