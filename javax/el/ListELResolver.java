// 
// Decompiled by Procyon v0.5.36
// 

package javax.el;

import java.util.Collections;
import java.util.ArrayList;
import java.beans.FeatureDescriptor;
import java.util.Iterator;
import java.util.List;

public class ListELResolver extends ELResolver
{
    private static Class<?> theUnmodifiableListClass;
    private boolean isReadOnly;
    
    public ListELResolver() {
        this.isReadOnly = false;
    }
    
    public ListELResolver(final boolean isReadOnly) {
        this.isReadOnly = isReadOnly;
    }
    
    @Override
    public Class<?> getType(final ELContext context, final Object base, final Object property) {
        if (context == null) {
            throw new NullPointerException();
        }
        if (base == null || !(base instanceof List)) {
            return null;
        }
        context.setPropertyResolved(true);
        final List list = (List)base;
        final int index = this.toInteger(property);
        if (index < 0 || index >= list.size()) {
            throw new PropertyNotFoundException();
        }
        return Object.class;
    }
    
    @Override
    public Object getValue(final ELContext context, final Object base, final Object property) {
        if (context == null) {
            throw new NullPointerException();
        }
        if (base == null || !(base instanceof List)) {
            return null;
        }
        context.setPropertyResolved(true);
        final List list = (List)base;
        final int index = this.toInteger(property);
        if (index < 0 || index >= list.size()) {
            return null;
        }
        return list.get(index);
    }
    
    @Override
    public void setValue(final ELContext context, final Object base, final Object property, final Object val) {
        if (context == null) {
            throw new NullPointerException();
        }
        if (base != null && base instanceof List) {
            context.setPropertyResolved(true);
            final List list = (List)base;
            final int index = this.toInteger(property);
            if (this.isReadOnly) {
                throw new PropertyNotWritableException();
            }
            try {
                list.set(index, val);
            }
            catch (UnsupportedOperationException ex4) {
                throw new PropertyNotWritableException();
            }
            catch (IndexOutOfBoundsException ex5) {
                throw new PropertyNotFoundException();
            }
            catch (ClassCastException ex) {
                throw ex;
            }
            catch (NullPointerException ex2) {
                throw ex2;
            }
            catch (IllegalArgumentException ex3) {
                throw ex3;
            }
        }
    }
    
    @Override
    public boolean isReadOnly(final ELContext context, final Object base, final Object property) {
        if (context == null) {
            throw new NullPointerException();
        }
        if (base == null || !(base instanceof List)) {
            return false;
        }
        context.setPropertyResolved(true);
        final List list = (List)base;
        final int index = this.toInteger(property);
        if (index < 0 || index >= list.size()) {
            throw new PropertyNotFoundException();
        }
        return list.getClass() == ListELResolver.theUnmodifiableListClass || this.isReadOnly;
    }
    
    @Override
    public Iterator<FeatureDescriptor> getFeatureDescriptors(final ELContext context, final Object base) {
        return null;
    }
    
    @Override
    public Class<?> getCommonPropertyType(final ELContext context, final Object base) {
        if (base != null && base instanceof List) {
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
    
    static {
        ListELResolver.theUnmodifiableListClass = Collections.unmodifiableList((List<?>)new ArrayList<Object>()).getClass();
    }
}
