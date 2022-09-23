// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.deser.impl;

import org.apache.htrace.shaded.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import org.apache.htrace.shaded.fasterxml.jackson.databind.DeserializationContext;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonParser;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.AnnotatedMember;
import java.lang.annotation.Annotation;
import org.apache.htrace.shaded.fasterxml.jackson.databind.PropertyName;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonDeserializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.util.Annotations;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JavaType;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import java.lang.reflect.Method;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.SettableBeanProperty;

public final class MethodProperty extends SettableBeanProperty
{
    private static final long serialVersionUID = 1L;
    protected final AnnotatedMethod _annotated;
    protected final transient Method _setter;
    
    public MethodProperty(final BeanPropertyDefinition propDef, final JavaType type, final TypeDeserializer typeDeser, final Annotations contextAnnotations, final AnnotatedMethod method) {
        super(propDef, type, typeDeser, contextAnnotations);
        this._annotated = method;
        this._setter = method.getAnnotated();
    }
    
    protected MethodProperty(final MethodProperty src, final JsonDeserializer<?> deser) {
        super(src, deser);
        this._annotated = src._annotated;
        this._setter = src._setter;
    }
    
    protected MethodProperty(final MethodProperty src, final PropertyName newName) {
        super(src, newName);
        this._annotated = src._annotated;
        this._setter = src._setter;
    }
    
    protected MethodProperty(final MethodProperty src, final Method m) {
        super(src);
        this._annotated = src._annotated;
        this._setter = m;
    }
    
    @Override
    public MethodProperty withName(final PropertyName newName) {
        return new MethodProperty(this, newName);
    }
    
    @Override
    public MethodProperty withValueDeserializer(final JsonDeserializer<?> deser) {
        return new MethodProperty(this, deser);
    }
    
    @Override
    public <A extends Annotation> A getAnnotation(final Class<A> acls) {
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
    public Object deserializeSetAndReturn(final JsonParser jp, final DeserializationContext ctxt, final Object instance) throws IOException, JsonProcessingException {
        return this.setAndReturn(instance, this.deserialize(jp, ctxt));
    }
    
    @Override
    public final void set(final Object instance, final Object value) throws IOException {
        try {
            this._setter.invoke(instance, value);
        }
        catch (Exception e) {
            this._throwAsIOE(e, value);
        }
    }
    
    @Override
    public Object setAndReturn(final Object instance, final Object value) throws IOException {
        try {
            final Object result = this._setter.invoke(instance, value);
            return (result == null) ? instance : result;
        }
        catch (Exception e) {
            this._throwAsIOE(e, value);
            return null;
        }
    }
    
    Object readResolve() {
        return new MethodProperty(this, this._annotated.getAnnotated());
    }
}
