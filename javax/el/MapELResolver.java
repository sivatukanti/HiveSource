// 
// Decompiled by Procyon v0.5.36
// 

package javax.el;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.beans.FeatureDescriptor;
import java.util.Iterator;
import java.util.Map;

public class MapELResolver extends ELResolver
{
    private static Class<?> theUnmodifiableMapClass;
    private boolean isReadOnly;
    
    public MapELResolver() {
        this.isReadOnly = false;
    }
    
    public MapELResolver(final boolean isReadOnly) {
        this.isReadOnly = isReadOnly;
    }
    
    @Override
    public Class<?> getType(final ELContext context, final Object base, final Object property) {
        if (context == null) {
            throw new NullPointerException();
        }
        if (base != null && base instanceof Map) {
            context.setPropertyResolved(true);
            return Object.class;
        }
        return null;
    }
    
    @Override
    public Object getValue(final ELContext context, final Object base, final Object property) {
        if (context == null) {
            throw new NullPointerException();
        }
        if (base != null && base instanceof Map) {
            context.setPropertyResolved(true);
            final Map map = (Map)base;
            return map.get(property);
        }
        return null;
    }
    
    @Override
    public void setValue(final ELContext context, final Object base, final Object property, final Object val) {
        if (context == null) {
            throw new NullPointerException();
        }
        if (base != null && base instanceof Map) {
            context.setPropertyResolved(true);
            final Map map = (Map)base;
            if (this.isReadOnly || map.getClass() == MapELResolver.theUnmodifiableMapClass) {
                throw new PropertyNotWritableException();
            }
            try {
                map.put(property, val);
            }
            catch (UnsupportedOperationException ex) {
                throw new PropertyNotWritableException();
            }
        }
    }
    
    @Override
    public boolean isReadOnly(final ELContext context, final Object base, final Object property) {
        if (context == null) {
            throw new NullPointerException();
        }
        if (base != null && base instanceof Map) {
            context.setPropertyResolved(true);
            final Map map = (Map)base;
            return this.isReadOnly || map.getClass() == MapELResolver.theUnmodifiableMapClass;
        }
        return false;
    }
    
    @Override
    public Iterator<FeatureDescriptor> getFeatureDescriptors(final ELContext context, final Object base) {
        if (base != null && base instanceof Map) {
            final Map map = (Map)base;
            final Iterator iter = map.keySet().iterator();
            final List<FeatureDescriptor> list = new ArrayList<FeatureDescriptor>();
            while (iter.hasNext()) {
                final Object key = iter.next();
                final FeatureDescriptor descriptor = new FeatureDescriptor();
                final String name = (key == null) ? null : key.toString();
                descriptor.setName(name);
                descriptor.setDisplayName(name);
                descriptor.setShortDescription("");
                descriptor.setExpert(false);
                descriptor.setHidden(false);
                descriptor.setPreferred(true);
                descriptor.setValue("type", (key == null) ? null : key.getClass());
                descriptor.setValue("resolvableAtDesignTime", Boolean.TRUE);
                list.add(descriptor);
            }
            return list.iterator();
        }
        return null;
    }
    
    @Override
    public Class<?> getCommonPropertyType(final ELContext context, final Object base) {
        if (base != null && base instanceof Map) {
            return Object.class;
        }
        return null;
    }
    
    static {
        MapELResolver.theUnmodifiableMapClass = Collections.unmodifiableMap((Map<?, ?>)new HashMap<Object, Object>()).getClass();
    }
}
