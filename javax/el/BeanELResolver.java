// 
// Decompiled by Procyon v0.5.36
// 

package javax.el;

import java.beans.IntrospectionException;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.lang.reflect.Modifier;
import java.beans.PropertyDescriptor;
import java.beans.BeanInfo;
import java.util.ArrayList;
import java.beans.Introspector;
import java.beans.FeatureDescriptor;
import java.util.Iterator;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public class BeanELResolver extends ELResolver
{
    private boolean isReadOnly;
    private static final int SIZE = 2000;
    private static final Map<Class, BeanProperties> properties;
    private static final Map<Class, BeanProperties> properties2;
    
    public BeanELResolver() {
        this.isReadOnly = false;
    }
    
    public BeanELResolver(final boolean isReadOnly) {
        this.isReadOnly = isReadOnly;
    }
    
    @Override
    public Class<?> getType(final ELContext context, final Object base, final Object property) {
        if (context == null) {
            throw new NullPointerException();
        }
        if (base == null || property == null) {
            return null;
        }
        final BeanProperty bp = this.getBeanProperty(context, base, property);
        context.setPropertyResolved(true);
        return (Class<?>)bp.getPropertyType();
    }
    
    @Override
    public Object getValue(final ELContext context, final Object base, final Object property) {
        if (context == null) {
            throw new NullPointerException();
        }
        if (base == null || property == null) {
            return null;
        }
        final BeanProperty bp = this.getBeanProperty(context, base, property);
        final Method method = bp.getReadMethod();
        if (method == null) {
            throw new PropertyNotFoundException(ELUtil.getExceptionMessageString(context, "propertyNotReadable", new Object[] { base.getClass().getName(), property.toString() }));
        }
        Object value;
        try {
            value = method.invoke(base, new Object[0]);
            context.setPropertyResolved(true);
        }
        catch (ELException ex) {
            throw ex;
        }
        catch (InvocationTargetException ite) {
            throw new ELException(ite.getCause());
        }
        catch (Exception ex2) {
            throw new ELException(ex2);
        }
        return value;
    }
    
    @Override
    public void setValue(final ELContext context, final Object base, final Object property, Object val) {
        if (context == null) {
            throw new NullPointerException();
        }
        if (base == null || property == null) {
            return;
        }
        if (this.isReadOnly) {
            throw new PropertyNotWritableException(ELUtil.getExceptionMessageString(context, "resolverNotwritable", new Object[] { base.getClass().getName() }));
        }
        final BeanProperty bp = this.getBeanProperty(context, base, property);
        final Method method = bp.getWriteMethod();
        if (method == null) {
            throw new PropertyNotWritableException(ELUtil.getExceptionMessageString(context, "propertyNotWritable", new Object[] { base.getClass().getName(), property.toString() }));
        }
        try {
            method.invoke(base, val);
            context.setPropertyResolved(true);
        }
        catch (ELException ex) {
            throw ex;
        }
        catch (InvocationTargetException ite) {
            throw new ELException(ite.getCause());
        }
        catch (Exception ex2) {
            if (null == val) {
                val = "null";
            }
            final String message = ELUtil.getExceptionMessageString(context, "setPropertyFailed", new Object[] { property.toString(), base.getClass().getName(), val });
            throw new ELException(message, ex2);
        }
    }
    
    @Override
    public boolean isReadOnly(final ELContext context, final Object base, final Object property) {
        if (context == null) {
            throw new NullPointerException();
        }
        if (base == null || property == null) {
            return false;
        }
        context.setPropertyResolved(true);
        if (this.isReadOnly) {
            return true;
        }
        final BeanProperty bp = this.getBeanProperty(context, base, property);
        return bp.isReadOnly();
    }
    
    @Override
    public Iterator<FeatureDescriptor> getFeatureDescriptors(final ELContext context, final Object base) {
        if (base == null) {
            return null;
        }
        BeanInfo info = null;
        try {
            info = Introspector.getBeanInfo(base.getClass());
        }
        catch (Exception ex) {}
        if (info == null) {
            return null;
        }
        final ArrayList<FeatureDescriptor> list = new ArrayList<FeatureDescriptor>(info.getPropertyDescriptors().length);
        for (final PropertyDescriptor pd : info.getPropertyDescriptors()) {
            pd.setValue("type", pd.getPropertyType());
            pd.setValue("resolvableAtDesignTime", Boolean.TRUE);
            list.add(pd);
        }
        return list.iterator();
    }
    
    @Override
    public Class<?> getCommonPropertyType(final ELContext context, final Object base) {
        if (base == null) {
            return null;
        }
        return Object.class;
    }
    
    private static Method getMethod(final Class cl, final Method method) {
        if (Modifier.isPublic(cl.getModifiers())) {
            return method;
        }
        final Class[] interfaces = cl.getInterfaces();
        for (int i = 0; i < interfaces.length; ++i) {
            Class c = interfaces[i];
            Method m = null;
            try {
                m = c.getMethod(method.getName(), (Class[])method.getParameterTypes());
                c = m.getDeclaringClass();
                if ((m = getMethod(c, m)) != null) {
                    return m;
                }
            }
            catch (NoSuchMethodException ex) {}
        }
        Class c2 = cl.getSuperclass();
        if (c2 != null) {
            Method j = null;
            try {
                j = c2.getMethod(method.getName(), (Class[])method.getParameterTypes());
                c2 = j.getDeclaringClass();
                if ((j = getMethod(c2, j)) != null) {
                    return j;
                }
            }
            catch (NoSuchMethodException ex2) {}
        }
        return null;
    }
    
    private BeanProperty getBeanProperty(final ELContext context, final Object base, final Object prop) {
        final String property = prop.toString();
        final Class baseClass = base.getClass();
        BeanProperties bps = BeanELResolver.properties.get(baseClass);
        if (bps == null && (bps = BeanELResolver.properties2.get(baseClass)) == null) {
            if (BeanELResolver.properties.size() > 2000) {
                BeanELResolver.properties2.clear();
                BeanELResolver.properties2.putAll(BeanELResolver.properties);
                BeanELResolver.properties.clear();
            }
            bps = new BeanProperties(baseClass);
            BeanELResolver.properties.put(baseClass, bps);
        }
        final BeanProperty bp = bps.getBeanProperty(property);
        if (bp == null) {
            throw new PropertyNotFoundException(ELUtil.getExceptionMessageString(context, "propertyNotFound", new Object[] { baseClass.getName(), property }));
        }
        return bp;
    }
    
    static {
        properties = new ConcurrentHashMap<Class, BeanProperties>(2000);
        properties2 = new ConcurrentHashMap<Class, BeanProperties>(2000);
    }
    
    protected static final class BeanProperty
    {
        private Method readMethod;
        private Method writeMethod;
        private Class baseClass;
        private PropertyDescriptor descriptor;
        
        public BeanProperty(final Class<?> baseClass, final PropertyDescriptor descriptor) {
            this.baseClass = baseClass;
            this.descriptor = descriptor;
        }
        
        public Class getPropertyType() {
            return this.descriptor.getPropertyType();
        }
        
        public boolean isReadOnly() {
            return this.getWriteMethod() == null;
        }
        
        public Method getReadMethod() {
            if (this.readMethod == null) {
                this.readMethod = getMethod(this.baseClass, this.descriptor.getReadMethod());
            }
            return this.readMethod;
        }
        
        public Method getWriteMethod() {
            if (this.writeMethod == null) {
                this.writeMethod = getMethod(this.baseClass, this.descriptor.getWriteMethod());
            }
            return this.writeMethod;
        }
    }
    
    protected static final class BeanProperties
    {
        private final Class baseClass;
        private final Map<String, BeanProperty> propertyMap;
        
        public BeanProperties(final Class<?> baseClass) {
            this.propertyMap = new HashMap<String, BeanProperty>();
            this.baseClass = baseClass;
            PropertyDescriptor[] descriptors;
            try {
                final BeanInfo info = Introspector.getBeanInfo(baseClass);
                descriptors = info.getPropertyDescriptors();
            }
            catch (IntrospectionException ie) {
                throw new ELException(ie);
            }
            for (final PropertyDescriptor pd : descriptors) {
                this.propertyMap.put(pd.getName(), new BeanProperty(baseClass, pd));
            }
        }
        
        public BeanProperty getBeanProperty(final String property) {
            return this.propertyMap.get(property);
        }
    }
}
