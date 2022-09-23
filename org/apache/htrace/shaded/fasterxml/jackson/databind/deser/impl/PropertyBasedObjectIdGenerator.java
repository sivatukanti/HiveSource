// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.deser.impl;

import org.apache.htrace.shaded.fasterxml.jackson.annotation.ObjectIdGenerator;
import org.apache.htrace.shaded.fasterxml.jackson.annotation.ObjectIdGenerators;

public class PropertyBasedObjectIdGenerator extends ObjectIdGenerators.PropertyGenerator
{
    private static final long serialVersionUID = 1L;
    
    public PropertyBasedObjectIdGenerator(final Class<?> scope) {
        super(scope);
    }
    
    @Override
    public Object generateId(final Object forPojo) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public ObjectIdGenerator<Object> forScope(final Class<?> scope) {
        return (scope == this._scope) ? this : new PropertyBasedObjectIdGenerator(scope);
    }
    
    @Override
    public ObjectIdGenerator<Object> newForSerialization(final Object context) {
        return this;
    }
    
    @Override
    public IdKey key(final Object key) {
        return new IdKey(this.getClass(), this._scope, key);
    }
}
