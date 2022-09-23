// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.servlet.listener;

import org.eclipse.jetty.util.log.Log;
import java.util.Iterator;
import java.util.Map;
import java.lang.reflect.Field;
import org.eclipse.jetty.util.Loader;
import javax.servlet.ServletContextEvent;
import org.eclipse.jetty.util.log.Logger;
import javax.servlet.ServletContextListener;

public class ELContextCleaner implements ServletContextListener
{
    private static final Logger LOG;
    
    @Override
    public void contextInitialized(final ServletContextEvent sce) {
    }
    
    @Override
    public void contextDestroyed(final ServletContextEvent sce) {
        try {
            final Class<?> beanELResolver = (Class<?>)Loader.loadClass(this.getClass(), "javax.el.BeanELResolver");
            final Field field = this.getField(beanELResolver);
            this.purgeEntries(field);
            if (ELContextCleaner.LOG.isDebugEnabled()) {
                ELContextCleaner.LOG.debug("javax.el.BeanELResolver purged", new Object[0]);
            }
        }
        catch (ClassNotFoundException ex2) {}
        catch (SecurityException | IllegalArgumentException | IllegalAccessException ex3) {
            final Exception ex;
            final Exception e = ex;
            ELContextCleaner.LOG.warn("Cannot purge classes from javax.el.BeanELResolver", e);
        }
        catch (NoSuchFieldException e2) {
            ELContextCleaner.LOG.debug("Not cleaning cached beans: no such field javax.el.BeanELResolver.properties", new Object[0]);
        }
    }
    
    protected Field getField(final Class<?> beanELResolver) throws SecurityException, NoSuchFieldException {
        if (beanELResolver == null) {
            return null;
        }
        return beanELResolver.getDeclaredField("properties");
    }
    
    protected void purgeEntries(final Field properties) throws IllegalArgumentException, IllegalAccessException {
        if (properties == null) {
            return;
        }
        if (!properties.isAccessible()) {
            properties.setAccessible(true);
        }
        final Map map = (Map)properties.get(null);
        if (map == null) {
            return;
        }
        final Iterator<Class<?>> itor = map.keySet().iterator();
        while (itor.hasNext()) {
            final Class<?> clazz = itor.next();
            if (ELContextCleaner.LOG.isDebugEnabled()) {
                ELContextCleaner.LOG.debug("Clazz: " + clazz + " loaded by " + clazz.getClassLoader(), new Object[0]);
            }
            if (Thread.currentThread().getContextClassLoader().equals(clazz.getClassLoader())) {
                itor.remove();
                if (!ELContextCleaner.LOG.isDebugEnabled()) {
                    continue;
                }
                ELContextCleaner.LOG.debug("removed", new Object[0]);
            }
            else {
                if (!ELContextCleaner.LOG.isDebugEnabled()) {
                    continue;
                }
                ELContextCleaner.LOG.debug("not removed: contextclassloader=" + Thread.currentThread().getContextClassLoader() + "clazz's classloader=" + clazz.getClassLoader(), new Object[0]);
            }
        }
    }
    
    static {
        LOG = Log.getLogger(ELContextCleaner.class);
    }
}
