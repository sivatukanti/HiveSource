// 
// Decompiled by Procyon v0.5.36
// 

package javax.el;

import java.beans.FeatureDescriptor;
import java.util.Iterator;
import java.lang.reflect.Array;

public class ArrayELResolver extends ELResolver
{
    private boolean isReadOnly;
    
    public ArrayELResolver() {
        this.isReadOnly = false;
    }
    
    public ArrayELResolver(final boolean isReadOnly) {
        this.isReadOnly = isReadOnly;
    }
    
    @Override
    public Class<?> getType(final ELContext context, final Object base, final Object property) {
        if (context == null) {
            throw new NullPointerException();
        }
        if (base == null || !base.getClass().isArray()) {
            return null;
        }
        context.setPropertyResolved(true);
        final int index = this.toInteger(property);
        if (index < 0 || index >= Array.getLength(base)) {
            throw new PropertyNotFoundException();
        }
        return base.getClass().getComponentType();
    }
    
    @Override
    public Object getValue(final ELContext context, final Object base, final Object property) {
        if (context == null) {
            throw new NullPointerException();
        }
        if (base != null && base.getClass().isArray()) {
            context.setPropertyResolved(true);
            final int index = this.toInteger(property);
            if (index >= 0 && index < Array.getLength(base)) {
                return Array.get(base, index);
            }
        }
        return null;
    }
    
    @Override
    public void setValue(final ELContext context, final Object base, final Object property, final Object val) {
        if (context == null) {
            throw new NullPointerException();
        }
        if (base != null && base.getClass().isArray()) {
            context.setPropertyResolved(true);
            if (this.isReadOnly) {
                throw new PropertyNotWritableException();
            }
            final Class<?> type = base.getClass().getComponentType();
            if (val != null && !type.isAssignableFrom(val.getClass())) {
                throw new ClassCastException();
            }
            final int index = this.toInteger(property);
            if (index < 0 || index >= Array.getLength(base)) {
                throw new PropertyNotFoundException();
            }
            Array.set(base, index, val);
        }
    }
    
    @Override
    public boolean isReadOnly(final ELContext context, final Object base, final Object property) {
        if (context == null) {
            throw new NullPointerException();
        }
        if (base != null && base.getClass().isArray()) {
            context.setPropertyResolved(true);
            final int index = this.toInteger(property);
            if (index < 0 || index >= Array.getLength(base)) {
                throw new PropertyNotFoundException();
            }
        }
        return this.isReadOnly;
    }
    
    @Override
    public Iterator<FeatureDescriptor> getFeatureDescriptors(final ELContext context, final Object base) {
        return null;
    }
    
    @Override
    public Class<?> getCommonPropertyType(final ELContext context, final Object base) {
        if (base != null && base.getClass().isArray()) {
            return Integer.class;
        }
        return null;
    }
    
    private int toInteger(final Object p) {
        if (p instanceof Integer) {
            return (int)p;
        }
        if (p instanceof Character) {
            return (char)p;
        }
        if (p instanceof Boolean) {
            return ((boolean)p) ? 1 : 0;
        }
        if (p instanceof Number) {
            return ((Number)p).intValue();
        }
        if (p instanceof String) {
            return Integer.parseInt((String)p);
        }
        throw new IllegalArgumentException();
    }
}
