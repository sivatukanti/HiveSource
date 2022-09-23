// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.ser.impl;

import org.apache.htrace.shaded.fasterxml.jackson.annotation.ObjectIdGenerator;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.ObjectIdInfo;
import org.apache.htrace.shaded.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import org.apache.htrace.shaded.fasterxml.jackson.annotation.ObjectIdGenerators;

public class PropertyBasedObjectIdGenerator extends ObjectIdGenerators.PropertyGenerator
{
    private static final long serialVersionUID = 1L;
    protected final BeanPropertyWriter _property;
    
    public PropertyBasedObjectIdGenerator(final ObjectIdInfo oid, final BeanPropertyWriter prop) {
        this(oid.getScope(), prop);
    }
    
    protected PropertyBasedObjectIdGenerator(final Class<?> scope, final BeanPropertyWriter prop) {
        super(scope);
        this._property = prop;
    }
    
    @Override
    public boolean canUseFor(final ObjectIdGenerator<?> gen) {
        if (gen.getClass() == this.getClass()) {
            final PropertyBasedObjectIdGenerator other = (PropertyBasedObjectIdGenerator)gen;
            if (other.getScope() == this._scope) {
                return other._property == this._property;
            }
        }
        return false;
    }
    
    @Override
    public Object generateId(final Object forPojo) {
        try {
            return this._property.get(forPojo);
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e2) {
            throw new IllegalStateException("Problem accessing property '" + this._property.getName() + "': " + e2.getMessage(), e2);
        }
    }
    
    @Override
    public ObjectIdGenerator<Object> forScope(final Class<?> scope) {
        return (scope == this._scope) ? this : new PropertyBasedObjectIdGenerator(scope, this._property);
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
