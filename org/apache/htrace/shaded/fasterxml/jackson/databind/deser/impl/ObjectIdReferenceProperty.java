// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.deser.impl;

import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.UnresolvedForwardReference;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonMappingException;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import org.apache.htrace.shaded.fasterxml.jackson.databind.DeserializationContext;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonParser;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.AnnotatedMember;
import java.lang.annotation.Annotation;
import org.apache.htrace.shaded.fasterxml.jackson.databind.PropertyName;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonDeserializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.ObjectIdInfo;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.SettableBeanProperty;

public class ObjectIdReferenceProperty extends SettableBeanProperty
{
    private static final long serialVersionUID = 8465266677345565407L;
    private SettableBeanProperty _forward;
    
    public ObjectIdReferenceProperty(final SettableBeanProperty forward, final ObjectIdInfo objectIdInfo) {
        super(forward);
        this._forward = forward;
        this._objectIdInfo = objectIdInfo;
    }
    
    public ObjectIdReferenceProperty(final ObjectIdReferenceProperty src, final JsonDeserializer<?> deser) {
        super(src, deser);
        this._forward = src._forward;
        this._objectIdInfo = src._objectIdInfo;
    }
    
    public ObjectIdReferenceProperty(final ObjectIdReferenceProperty src, final PropertyName newName) {
        super(src, newName);
        this._forward = src._forward;
        this._objectIdInfo = src._objectIdInfo;
    }
    
    @Override
    public SettableBeanProperty withValueDeserializer(final JsonDeserializer<?> deser) {
        return new ObjectIdReferenceProperty(this, deser);
    }
    
    @Override
    public SettableBeanProperty withName(final PropertyName newName) {
        return new ObjectIdReferenceProperty(this, newName);
    }
    
    @Override
    public <A extends Annotation> A getAnnotation(final Class<A> acls) {
        return this._forward.getAnnotation(acls);
    }
    
    @Override
    public AnnotatedMember getMember() {
        return this._forward.getMember();
    }
    
    @Override
    public void deserializeAndSet(final JsonParser jp, final DeserializationContext ctxt, final Object instance) throws IOException, JsonProcessingException {
        this.deserializeSetAndReturn(jp, ctxt, instance);
    }
    
    @Override
    public Object deserializeSetAndReturn(final JsonParser jp, final DeserializationContext ctxt, final Object instance) throws IOException, JsonProcessingException {
        final boolean usingIdentityInfo = this._objectIdInfo != null || this._valueDeserializer.getObjectIdReader() != null;
        try {
            return this.setAndReturn(instance, this.deserialize(jp, ctxt));
        }
        catch (UnresolvedForwardReference reference) {
            if (!usingIdentityInfo) {
                throw JsonMappingException.from(jp, "Unresolved forward reference but no identity info.", reference);
            }
            reference.getRoid().appendReferring(new PropertyReferring(this, reference, this._type.getRawClass(), instance));
            return null;
        }
    }
    
    @Override
    public void set(final Object instance, final Object value) throws IOException {
        this._forward.set(instance, value);
    }
    
    @Override
    public Object setAndReturn(final Object instance, final Object value) throws IOException {
        return this._forward.setAndReturn(instance, value);
    }
    
    public static final class PropertyReferring extends ReadableObjectId.Referring
    {
        private final ObjectIdReferenceProperty _parent;
        public final Object _pojo;
        
        public PropertyReferring(final ObjectIdReferenceProperty parent, final UnresolvedForwardReference ref, final Class<?> type, final Object ob) {
            super(ref, type);
            this._parent = parent;
            this._pojo = ob;
        }
        
        @Override
        public void handleResolvedForwardReference(final Object id, final Object value) throws IOException {
            if (!this.hasId(id)) {
                throw new IllegalArgumentException("Trying to resolve a forward reference with id [" + id + "] that wasn't previously seen as unresolved.");
            }
            this._parent.set(this._pojo, value);
        }
    }
}
