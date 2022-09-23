// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.beanutils;

import org.apache.commons.beanutils.ConvertUtilsBean;
import org.apache.commons.beanutils.BeanIntrospector;
import org.apache.commons.beanutils.FluentPropertyBeanIntrospector;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.commons.lang3.ClassUtils;
import java.util.TreeSet;
import java.util.ArrayList;
import org.apache.commons.configuration2.ex.ConfigurationRuntimeException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import org.apache.commons.beanutils.WrapDynaBean;
import org.apache.commons.beanutils.WrapDynaClass;
import org.apache.commons.beanutils.DynaBean;
import java.util.Iterator;
import java.util.List;
import java.util.Collection;
import java.util.Set;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.beanutils.BeanUtilsBean;

public final class BeanHelper
{
    public static final BeanHelper INSTANCE;
    private static final BeanUtilsBean BEAN_UTILS_BEAN;
    private final Map<String, BeanFactory> beanFactories;
    private final BeanFactory defaultBeanFactory;
    
    public BeanHelper() {
        this(null);
    }
    
    public BeanHelper(final BeanFactory defFactory) {
        this.beanFactories = Collections.synchronizedMap(new HashMap<String, BeanFactory>());
        this.defaultBeanFactory = ((defFactory != null) ? defFactory : DefaultBeanFactory.INSTANCE);
    }
    
    public void registerBeanFactory(final String name, final BeanFactory factory) {
        if (name == null) {
            throw new IllegalArgumentException("Name for bean factory must not be null!");
        }
        if (factory == null) {
            throw new IllegalArgumentException("Bean factory must not be null!");
        }
        this.beanFactories.put(name, factory);
    }
    
    public BeanFactory deregisterBeanFactory(final String name) {
        return this.beanFactories.remove(name);
    }
    
    public Set<String> registeredFactoryNames() {
        return this.beanFactories.keySet();
    }
    
    public BeanFactory getDefaultBeanFactory() {
        return this.defaultBeanFactory;
    }
    
    public void initBean(final Object bean, final BeanDeclaration data) {
        initBeanProperties(bean, data);
        final Map<String, Object> nestedBeans = data.getNestedBeanDeclarations();
        if (nestedBeans != null) {
            if (bean instanceof Collection) {
                final Collection<Object> coll = (Collection<Object>)bean;
                if (nestedBeans.size() == 1) {
                    final Map.Entry<String, Object> e = nestedBeans.entrySet().iterator().next();
                    final String propName = e.getKey();
                    final Class<?> defaultClass = getDefaultClass(bean, propName);
                    if (e.getValue() instanceof List) {
                        final List<BeanDeclaration> decls = e.getValue();
                        for (final BeanDeclaration decl : decls) {
                            coll.add(this.createBean(decl, defaultClass));
                        }
                    }
                    else {
                        final BeanDeclaration decl2 = e.getValue();
                        coll.add(this.createBean(decl2, defaultClass));
                    }
                }
            }
            else {
                for (final Map.Entry<String, Object> e : nestedBeans.entrySet()) {
                    final String propName = e.getKey();
                    final Class<?> defaultClass = getDefaultClass(bean, propName);
                    final Object prop = e.getValue();
                    if (prop instanceof Collection) {
                        final Collection<Object> beanCollection = createPropertyCollection(propName, defaultClass);
                        for (final Object elemDef : (Collection)prop) {
                            beanCollection.add(this.createBean((BeanDeclaration)elemDef));
                        }
                        initProperty(bean, propName, beanCollection);
                    }
                    else {
                        initProperty(bean, propName, this.createBean(e.getValue(), defaultClass));
                    }
                }
            }
        }
    }
    
    public static void initBeanProperties(final Object bean, final BeanDeclaration data) {
        final Map<String, Object> properties = data.getBeanProperties();
        if (properties != null) {
            for (final Map.Entry<String, Object> e : properties.entrySet()) {
                final String propName = e.getKey();
                initProperty(bean, propName, e.getValue());
            }
        }
    }
    
    public static DynaBean createWrapDynaBean(final Object bean) {
        if (bean == null) {
            throw new IllegalArgumentException("Bean must not be null!");
        }
        final WrapDynaClass dynaClass = WrapDynaClass.createDynaClass(bean.getClass(), BeanHelper.BEAN_UTILS_BEAN.getPropertyUtils());
        return new WrapDynaBean(bean, dynaClass);
    }
    
    public static void copyProperties(final Object dest, final Object orig) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        BeanHelper.BEAN_UTILS_BEAN.getPropertyUtils().copyProperties(dest, orig);
    }
    
    private static Class<?> getDefaultClass(final Object bean, final String propName) {
        try {
            final PropertyDescriptor desc = BeanHelper.BEAN_UTILS_BEAN.getPropertyUtils().getPropertyDescriptor(bean, propName);
            if (desc == null) {
                return null;
            }
            return desc.getPropertyType();
        }
        catch (Exception ex) {
            return null;
        }
    }
    
    private static void initProperty(final Object bean, final String propName, final Object value) {
        if (!isPropertyWriteable(bean, propName)) {
            throw new ConfigurationRuntimeException("Property " + propName + " cannot be set on " + bean.getClass().getName());
        }
        try {
            BeanHelper.BEAN_UTILS_BEAN.setProperty(bean, propName, value);
        }
        catch (IllegalAccessException iaex) {
            throw new ConfigurationRuntimeException(iaex);
        }
        catch (InvocationTargetException itex) {
            throw new ConfigurationRuntimeException(itex);
        }
    }
    
    private static Collection<Object> createPropertyCollection(final String propName, final Class<?> propertyClass) {
        Collection<Object> beanCollection;
        if (List.class.isAssignableFrom(propertyClass)) {
            beanCollection = new ArrayList<Object>();
        }
        else {
            if (!Set.class.isAssignableFrom(propertyClass)) {
                throw new UnsupportedOperationException("Unable to handle collection of type : " + propertyClass.getName() + " for property " + propName);
            }
            beanCollection = new TreeSet<Object>();
        }
        return beanCollection;
    }
    
    public static void setProperty(final Object bean, final String propName, final Object value) {
        if (isPropertyWriteable(bean, propName)) {
            initProperty(bean, propName, value);
        }
    }
    
    public Object createBean(final BeanDeclaration data, final Class<?> defaultClass, final Object param) {
        if (data == null) {
            throw new IllegalArgumentException("Bean declaration must not be null!");
        }
        final BeanFactory factory = this.fetchBeanFactory(data);
        final BeanCreationContext bcc = this.createBeanCreationContext(data, defaultClass, param, factory);
        try {
            return factory.createBean(bcc);
        }
        catch (Exception ex) {
            throw new ConfigurationRuntimeException(ex);
        }
    }
    
    public Object createBean(final BeanDeclaration data, final Class<?> defaultClass) {
        return this.createBean(data, defaultClass, null);
    }
    
    public Object createBean(final BeanDeclaration data) {
        return this.createBean(data, null);
    }
    
    static Class<?> loadClass(final String name) throws ClassNotFoundException {
        return ClassUtils.getClass(name);
    }
    
    private static boolean isPropertyWriteable(final Object bean, final String propName) {
        return BeanHelper.BEAN_UTILS_BEAN.getPropertyUtils().isWriteable(bean, propName);
    }
    
    private static Class<?> fetchBeanClass(final BeanDeclaration data, final Class<?> defaultClass, final BeanFactory factory) {
        final String clsName = data.getBeanClassName();
        if (clsName != null) {
            try {
                return loadClass(clsName);
            }
            catch (ClassNotFoundException cex) {
                throw new ConfigurationRuntimeException(cex);
            }
        }
        if (defaultClass != null) {
            return defaultClass;
        }
        final Class<?> clazz = factory.getDefaultBeanClass();
        if (clazz == null) {
            throw new ConfigurationRuntimeException("Bean class is not specified!");
        }
        return clazz;
    }
    
    private BeanFactory fetchBeanFactory(final BeanDeclaration data) {
        final String factoryName = data.getBeanFactoryName();
        if (factoryName == null) {
            return this.getDefaultBeanFactory();
        }
        final BeanFactory factory = this.beanFactories.get(factoryName);
        if (factory == null) {
            throw new ConfigurationRuntimeException("Unknown bean factory: " + factoryName);
        }
        return factory;
    }
    
    private BeanCreationContext createBeanCreationContext(final BeanDeclaration data, final Class<?> defaultClass, final Object param, final BeanFactory factory) {
        final Class<?> beanClass = fetchBeanClass(data, defaultClass, factory);
        return new BeanCreationContextImpl(this, (Class)beanClass, data, param);
    }
    
    private static BeanUtilsBean initBeanUtilsBean() {
        final PropertyUtilsBean propUtilsBean = new PropertyUtilsBean();
        propUtilsBean.addBeanIntrospector(new FluentPropertyBeanIntrospector());
        return new BeanUtilsBean(new ConvertUtilsBean(), propUtilsBean);
    }
    
    static {
        INSTANCE = new BeanHelper();
        BEAN_UTILS_BEAN = initBeanUtilsBean();
    }
    
    private static final class BeanCreationContextImpl implements BeanCreationContext
    {
        private final BeanHelper beanHelper;
        private final Class<?> beanClass;
        private final BeanDeclaration data;
        private final Object param;
        
        private BeanCreationContextImpl(final BeanHelper helper, final Class<?> beanClass, final BeanDeclaration data, final Object param) {
            this.beanHelper = helper;
            this.beanClass = beanClass;
            this.param = param;
            this.data = data;
        }
        
        @Override
        public void initBean(final Object bean, final BeanDeclaration data) {
            this.beanHelper.initBean(bean, data);
        }
        
        @Override
        public Object getParameter() {
            return this.param;
        }
        
        @Override
        public BeanDeclaration getBeanDeclaration() {
            return this.data;
        }
        
        @Override
        public Class<?> getBeanClass() {
            return this.beanClass;
        }
        
        @Override
        public Object createBean(final BeanDeclaration data) {
            return this.beanHelper.createBean(data);
        }
    }
}
