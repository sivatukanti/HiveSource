// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.impl.cdi;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.logging.Level;
import com.sun.jersey.server.impl.InitialContextHelper;
import javax.servlet.ServletContext;
import com.sun.jersey.spi.container.WebApplication;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.spi.container.servlet.WebConfig;
import java.util.logging.Logger;

public class CDIComponentProviderFactoryInitializer
{
    private static final Logger LOGGER;
    private static final String BEAN_MANAGER_CLASS = "javax.enterprise.inject.spi.BeanManager";
    private static final String WELD_SERVLET_PACKAGE = "org.jboss.weld.environment.servlet";
    
    public static void initialize(final WebConfig wc, final ResourceConfig rc, final WebApplication wa) {
        final ServletContext sc = wc.getServletContext();
        final Object beanManager = lookup(sc);
        if (beanManager == null) {
            CDIComponentProviderFactoryInitializer.LOGGER.config("The CDI BeanManager is not available. JAX-RS CDI support is disabled.");
            return;
        }
        rc.getSingletons().add(new CDIComponentProviderFactory(beanManager, rc, wa));
        CDIComponentProviderFactoryInitializer.LOGGER.info("CDI support is enabled");
    }
    
    private static Object lookup(final ServletContext sc) {
        Object beanManager = null;
        beanManager = lookupInJndi("java:comp/BeanManager");
        if (beanManager != null) {
            return beanManager;
        }
        beanManager = lookupInServletContext(sc, "javax.enterprise.inject.spi.BeanManager");
        if (beanManager != null) {
            return beanManager;
        }
        beanManager = lookupInServletContext(sc, "org.jboss.weld.environment.servlet.javax.enterprise.inject.spi.BeanManager");
        if (beanManager != null) {
            return beanManager;
        }
        return null;
    }
    
    private static Object lookupInJndi(final String name) {
        try {
            final InitialContext ic = InitialContextHelper.getInitialContext();
            if (ic == null) {
                return null;
            }
            final Object beanManager = ic.lookup(name);
            if (beanManager == null) {
                CDIComponentProviderFactoryInitializer.LOGGER.config("The CDI BeanManager is not available at " + name);
                return null;
            }
            CDIComponentProviderFactoryInitializer.LOGGER.config("The CDI BeanManager is at " + name);
            return beanManager;
        }
        catch (NamingException ex) {
            CDIComponentProviderFactoryInitializer.LOGGER.log(Level.CONFIG, "The CDI BeanManager is not available at " + name, ex);
            return null;
        }
    }
    
    private static Object lookupInServletContext(final ServletContext sc, final String name) {
        final Object beanManager = sc.getAttribute(name);
        if (beanManager == null) {
            CDIComponentProviderFactoryInitializer.LOGGER.config("The CDI BeanManager is not available at " + name);
            return null;
        }
        CDIComponentProviderFactoryInitializer.LOGGER.config("The CDI BeanManager is at " + name);
        return beanManager;
    }
    
    static {
        LOGGER = Logger.getLogger(CDIComponentProviderFactoryInitializer.class.getName());
    }
}
