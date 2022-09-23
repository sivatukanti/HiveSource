// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.deser.impl;

import org.apache.htrace.shaded.fasterxml.jackson.core.JsonLocation;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.UnresolvedForwardReference;
import java.util.Collections;
import java.io.IOException;
import java.util.Iterator;
import org.apache.htrace.shaded.fasterxml.jackson.annotation.ObjectIdResolver;
import java.util.LinkedList;
import org.apache.htrace.shaded.fasterxml.jackson.annotation.ObjectIdGenerator;

public class ReadableObjectId
{
    @Deprecated
    public Object item;
    @Deprecated
    public final Object id;
    private final ObjectIdGenerator.IdKey _key;
    private LinkedList<Referring> _referringProperties;
    private ObjectIdResolver _resolver;
    
    @Deprecated
    public ReadableObjectId(final Object id) {
        this.id = id;
        this._key = null;
    }
    
    public ReadableObjectId(final ObjectIdGenerator.IdKey key) {
        this._key = key;
        this.id = key.key;
    }
    
    public void setResolver(final ObjectIdResolver resolver) {
        this._resolver = resolver;
    }
    
    public ObjectIdGenerator.IdKey getKey() {
        return this._key;
    }
    
    public void appendReferring(final Referring currentReferring) {
        if (this._referringProperties == null) {
            this._referringProperties = new LinkedList<Referring>();
        }
        this._referringProperties.add(currentReferring);
    }
    
    public void bindItem(final Object ob) throws IOException {
        this._resolver.bindItem(this._key, ob);
        this.item = ob;
        if (this._referringProperties != null) {
            final Iterator<Referring> it = this._referringProperties.iterator();
            this._referringProperties = null;
            while (it.hasNext()) {
                it.next().handleResolvedForwardReference(this.id, ob);
            }
        }
    }
    
    public Object resolve() {
        return this.item = this._resolver.resolveId(this._key);
    }
    
    public boolean hasReferringProperties() {
        return this._referringProperties != null && !this._referringProperties.isEmpty();
    }
    
    public Iterator<Referring> referringProperties() {
        if (this._referringProperties == null) {
            return Collections.emptyList().iterator();
        }
        return this._referringProperties.iterator();
    }
    
    public abstract static class Referring
    {
        private final UnresolvedForwardReference _reference;
        private final Class<?> _beanType;
        
        public Referring(final UnresolvedForwardReference ref, final Class<?> beanType) {
            this._reference = ref;
            this._beanType = beanType;
        }
        
        public JsonLocation getLocation() {
            return this._reference.getLocation();
        }
        
        public Class<?> getBeanType() {
            return this._beanType;
        }
        
        public abstract void handleResolvedForwardReference(final Object p0, final Object p1) throws IOException;
        
        public boolean hasId(final Object id) {
            return id.equals(this._reference.getUnresolvedId());
        }
    }
}
