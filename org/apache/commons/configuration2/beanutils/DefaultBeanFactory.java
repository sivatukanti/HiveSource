// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.beanutils;

import org.apache.commons.configuration2.ex.ConfigurationRuntimeException;
import java.util.LinkedList;
import java.util.Collections;
import java.util.Collection;
import java.util.Iterator;
import org.apache.commons.configuration2.interpol.ConfigurationInterpolator;
import java.util.List;
import java.lang.reflect.Constructor;
import org.apache.commons.configuration2.convert.DefaultConversionHandler;
import org.apache.commons.configuration2.convert.ConversionHandler;

public class DefaultBeanFactory implements BeanFactory
{
    public static final DefaultBeanFactory INSTANCE;
    private static final String FMT_CTOR_ERROR = "%s! Bean class = %s, constructor arguments = %s";
    private final ConversionHandler conversionHandler;
    
    public DefaultBeanFactory() {
        this(null);
    }
    
    public DefaultBeanFactory(final ConversionHandler convHandler) {
        this.conversionHandler = ((convHandler != null) ? convHandler : DefaultConversionHandler.INSTANCE);
    }
    
    public ConversionHandler getConversionHandler() {
        return this.conversionHandler;
    }
    
    @Override
    public Object createBean(final BeanCreationContext bcc) throws Exception {
        final Object result = this.createBeanInstance(bcc);
        this.initBeanInstance(result, bcc);
        return result;
    }
    
    @Override
    public Class<?> getDefaultBeanClass() {
        return null;
    }
    
    protected Object createBeanInstance(final BeanCreationContext bcc) throws Exception {
        final Constructor<?> ctor = findMatchingConstructor(bcc.getBeanClass(), bcc.getBeanDeclaration());
        final Object[] args = this.fetchConstructorArgs(ctor, bcc);
        return ctor.newInstance(args);
    }
    
    protected void initBeanInstance(final Object bean, final BeanCreationContext bcc) throws Exception {
        bcc.initBean(bean, bcc.getBeanDeclaration());
    }
    
    protected static <T> Constructor<T> findMatchingConstructor(final Class<T> beanClass, final BeanDeclaration data) {
        final List<Constructor<T>> matchingConstructors = findMatchingConstructors(beanClass, data);
        checkSingleMatchingConstructor(beanClass, data, matchingConstructors);
        return matchingConstructors.get(0);
    }
    
    private Object[] fetchConstructorArgs(final Constructor<?> ctor, final BeanCreationContext bcc) {
        final Class<?>[] types = ctor.getParameterTypes();
        assert types.length == nullSafeConstructorArgs(bcc.getBeanDeclaration()).size() : "Wrong number of constructor arguments!";
        final Object[] args = new Object[types.length];
        int idx = 0;
        for (final ConstructorArg arg : nullSafeConstructorArgs(bcc.getBeanDeclaration())) {
            final Object val = arg.isNestedBeanDeclaration() ? bcc.createBean(arg.getBeanDeclaration()) : arg.getValue();
            args[idx] = this.getConversionHandler().to(val, types[idx], null);
            ++idx;
        }
        return args;
    }
    
    private static Collection<ConstructorArg> nullSafeConstructorArgs(final BeanDeclaration data) {
        Collection<ConstructorArg> args = data.getConstructorArgs();
        if (args == null) {
            args = (Collection<ConstructorArg>)Collections.emptySet();
        }
        return args;
    }
    
    private static <T> List<Constructor<T>> findMatchingConstructors(final Class<T> beanClass, final BeanDeclaration data) {
        final List<Constructor<T>> result = new LinkedList<Constructor<T>>();
        final Collection<ConstructorArg> args = getConstructorArgs(data);
        for (final Constructor<?> ctor : beanClass.getConstructors()) {
            if (matchesConstructor(ctor, args)) {
                final Constructor<T> match = (Constructor<T>)ctor;
                result.add(match);
            }
        }
        return result;
    }
    
    private static boolean matchesConstructor(final Constructor<?> ctor, final Collection<ConstructorArg> args) {
        final Class<?>[] types = ctor.getParameterTypes();
        if (types.length != args.size()) {
            return false;
        }
        int idx = 0;
        for (final ConstructorArg arg : args) {
            if (!arg.matches(types[idx++])) {
                return false;
            }
        }
        return true;
    }
    
    private static Collection<ConstructorArg> getConstructorArgs(final BeanDeclaration data) {
        Collection<ConstructorArg> args = data.getConstructorArgs();
        if (args == null) {
            args = (Collection<ConstructorArg>)Collections.emptySet();
        }
        return args;
    }
    
    private static <T> void checkSingleMatchingConstructor(final Class<T> beanClass, final BeanDeclaration data, final List<Constructor<T>> matchingConstructors) {
        if (matchingConstructors.isEmpty()) {
            throw constructorMatchingException(beanClass, data, "No matching constructor found");
        }
        if (matchingConstructors.size() > 1) {
            throw constructorMatchingException(beanClass, data, "Multiple matching constructors found");
        }
    }
    
    private static ConfigurationRuntimeException constructorMatchingException(final Class<?> beanClass, final BeanDeclaration data, final String msg) {
        return new ConfigurationRuntimeException(String.format("%s! Bean class = %s, constructor arguments = %s", msg, beanClass.getName(), getConstructorArgs(data).toString()));
    }
    
    static {
        INSTANCE = new DefaultBeanFactory();
    }
}
