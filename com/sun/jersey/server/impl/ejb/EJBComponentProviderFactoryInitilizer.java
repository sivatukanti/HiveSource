// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.impl.ejb;

import java.lang.reflect.Method;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.logging.Level;
import com.sun.jersey.server.impl.InitialContextHelper;
import com.sun.jersey.api.core.ResourceConfig;
import java.util.logging.Logger;

public final class EJBComponentProviderFactoryInitilizer
{
    private static final Logger LOGGER;
    
    public static void initialize(final ResourceConfig rc) {
        try {
            final InitialContext ic = InitialContextHelper.getInitialContext();
            if (ic == null) {
                return;
            }
            final Object interceptorBinder = ic.lookup("java:org.glassfish.ejb.container.interceptor_binding_spi");
            if (interceptorBinder == null) {
                EJBComponentProviderFactoryInitilizer.LOGGER.config("The EJB interceptor binding API is not available. JAX-RS EJB support is disabled.");
                return;
            }
            final Method interceptorBinderMethod = interceptorBinder.getClass().getMethod("registerInterceptor", Object.class);
            final EJBInjectionInterceptor interceptor = new EJBInjectionInterceptor();
            try {
                interceptorBinderMethod.invoke(interceptorBinder, interceptor);
            }
            catch (Exception ex) {
                EJBComponentProviderFactoryInitilizer.LOGGER.log(Level.SEVERE, "Error when configuring to use the EJB interceptor binding API. JAX-RS EJB support is disabled.", ex);
                return;
            }
            rc.getSingletons().add(new EJBComponentProviderFactory(interceptor));
            rc.getClasses().add(EJBExceptionMapper.class);
        }
        catch (NamingException ex2) {
            EJBComponentProviderFactoryInitilizer.LOGGER.log(Level.CONFIG, "The EJB interceptor binding API is not available. JAX-RS EJB support is disabled.", ex2);
        }
        catch (NoSuchMethodException ex3) {
            EJBComponentProviderFactoryInitilizer.LOGGER.log(Level.SEVERE, "The EJB interceptor binding API does not conform to what is expected. JAX-RS EJB support is disabled.", ex3);
        }
        catch (SecurityException ex4) {
            EJBComponentProviderFactoryInitilizer.LOGGER.log(Level.SEVERE, "Security issue when configuring to use the EJB interceptor binding API. JAX-RS EJB support is disabled.", ex4);
        }
        catch (LinkageError ex5) {
            EJBComponentProviderFactoryInitilizer.LOGGER.log(Level.SEVERE, "Linkage error when configuring to use the EJB interceptor binding API. JAX-RS EJB support is disabled.", ex5);
        }
    }
    
    static {
        LOGGER = Logger.getLogger(EJBComponentProviderFactoryInitilizer.class.getName());
    }
}
