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
import java.lang.reflect.Field;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.AnnotatedField;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.SettableBeanProperty;

public final class FieldProperty extends SettableBeanProperty
{
    private static final long serialVersionUID = 1L;
    protected final AnnotatedField _annotated;
    protected final transient Field _field;
    
    public FieldProperty(final BeanPropertyDefinition propDef, final JavaType type, final TypeDeserializer typeDeser, final Annotations contextAnnotations, final AnnotatedField field) {
        super(propDef, type, typeDeser, contextAnnotations);
        this._annotated = field;
        this._field = field.getAnnotated();
    }
    
    protected FieldProperty(final FieldProperty src, final JsonDeserializer<?> deser) {
        super(src, deser);
        this._annotated = src._annotated;
        this._field = src._field;
    }
    
    protected FieldProperty(final FieldProperty src, final PropertyName newName) {
        super(src, newName);
        this._annotated = src._annotated;
        this._field = src._field;
    }
    
    protected FieldProperty(final FieldProperty src, final Field f) {
        super(src);
        this._annotated = src._annotated;
        if (f == null) {
            throw new IllegalArgumentException("No Field passed for property '" + src.getName() + "' (class " + src.getDeclaringClass().getName() + ")");
        }
        this._field = f;
    }
    
    @Override
    public FieldProperty withName(final PropertyName newName) {
        return new FieldProperty(this, newName);
    }
    
    @Override
    public FieldProperty withValueDeserializer(final JsonDeserializer<?> deser) {
        return new FieldProperty(this, deser);
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
            this._field.set(instance, value);
        }
        catch (Exception e) {
            this._throwAsIOE(e, value);
        }
    }
    
    @Override
    public Object setAndReturn(final Object instance, final Object value) throws IOException {
        try {
            this._field.set(instance, value);
        }
        catch (Exception e) {
            this._throwAsIOE(e, value);
        }
        return instance;
    }
    
    Object readResolve() {
        return new FieldProperty(this, this._annotated.getAnnotated());
    }
}
