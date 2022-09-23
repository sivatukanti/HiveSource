// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.impl.cdi;

import java.util.Collections;
import java.util.Iterator;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.context.spi.Contextual;
import javax.enterprise.inject.AmbiguousResolutionException;
import java.util.Set;
import java.util.logging.Level;
import java.lang.reflect.Type;
import java.lang.annotation.Annotation;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import java.util.logging.Logger;

public class Utils
{
    private static final Logger LOGGER;
    
    private Utils() {
    }
    
    public static Bean<?> getBean(final BeanManager beanManager, final Class<?> clazz) {
        final Set<Bean<?>> beans = (Set<Bean<?>>)beanManager.getBeans((Type)clazz, new Annotation[0]);
        if (beans.isEmpty()) {
            if (Utils.LOGGER.isLoggable(Level.FINE)) {
                Utils.LOGGER.fine(String.format("No CDI beans found in bean manager, %s, for type %s", beanManager, clazz));
            }
            return null;
        }
        try {
            return (Bean<?>)beanManager.resolve((Set)beans);
        }
        catch (AmbiguousResolutionException ex) {
            if (isSharedBaseClass(clazz, beans)) {
                try {
                    if (Utils.LOGGER.isLoggable(Level.FINE)) {
                        Utils.LOGGER.fine(String.format("Ambiguous resolution exception caught when resolving bean %s. Trying to resolve by the type %s", beans, clazz));
                    }
                    return (Bean<?>)beanManager.resolve((Set)getBaseClassSubSet(clazz, beans));
                }
                catch (AmbiguousResolutionException ex2) {
                    return null;
                }
            }
            if (Utils.LOGGER.isLoggable(Level.FINE)) {
                Utils.LOGGER.fine(String.format("Failed to resolve bean %s.", beans));
            }
            return null;
        }
    }
    
    public static <T> T getInstance(final BeanManager beanManager, final Class<T> c) {
        final Bean<?> bean = getBean(beanManager, c);
        if (bean == null) {
            return null;
        }
        final CreationalContext<?> creationalContext = (CreationalContext<?>)beanManager.createCreationalContext((Contextual)bean);
        final Object result = beanManager.getReference((Bean)bean, (Type)c, (CreationalContext)creationalContext);
        return c.cast(result);
    }
    
    public static CDIExtension getCdiExtensionInstance(final BeanManager beanManager) {
        final Set<Bean<?>> beans = (Set<Bean<?>>)beanManager.getBeans((Type)CDIExtension.class, new Annotation[0]);
        if (beans.isEmpty()) {
            return null;
        }
        try {
            return getCdiExtensionReference(beanManager.resolve((Set)beans), beanManager);
        }
        catch (AmbiguousResolutionException ex) {
            for (final Bean<?> b : beans) {
                final CDIExtension cdiExtension = getCdiExtensionReference(b, beanManager);
                if (cdiExtension.toBeInitializedLater != null) {
                    return cdiExtension;
                }
            }
            return null;
        }
    }
    
    private static CDIExtension getCdiExtensionReference(final Bean extensionBean, final BeanManager beanManager) {
        final CreationalContext<?> creationalContext = (CreationalContext<?>)beanManager.createCreationalContext((Contextual)extensionBean);
        final Object result = beanManager.getReference(extensionBean, (Type)CDIExtension.class, (CreationalContext)creationalContext);
        return (CDIExtension)result;
    }
    
    private static boolean isSharedBaseClass(final Class<?> clazz, final Set<Bean<?>> beans) {
        for (final Bean<?> bean : beans) {
            if (!clazz.isAssignableFrom(bean.getBeanClass())) {
                return false;
            }
        }
        return true;
    }
    
    private static Set<Bean<?>> getBaseClassSubSet(final Class<?> clazz, final Set<Bean<?>> beans) {
        for (final Bean<?> bean : beans) {
            if (clazz == bean.getBeanClass()) {
                return Collections.singleton(bean);
            }
        }
        return beans;
    }
    
    static {
        LOGGER = Logger.getLogger(Utils.class.getName());
    }
}
