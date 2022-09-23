// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.impl.managedbeans;

import java.lang.reflect.Method;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.logging.Level;
import com.sun.jersey.server.impl.InitialContextHelper;
import com.sun.jersey.api.core.ResourceConfig;
import java.util.logging.Logger;

public final class ManagedBeanComponentProviderFactoryInitilizer
{
    private static final Logger LOGGER;
    
    public static void initialize(final ResourceConfig rc) {
        try {
            final InitialContext ic = InitialContextHelper.getInitialContext();
            if (ic == null) {
                return;
            }
            final Object injectionMgr = ic.lookup("com.sun.enterprise.container.common.spi.util.InjectionManager");
            if (injectionMgr == null) {
                ManagedBeanComponentProviderFactoryInitilizer.LOGGER.config("The managed beans injection manager API is not available. JAX-RS managed beans support is disabled.");
                return;
            }
            final Method createManagedObjectMethod = injectionMgr.getClass().getMethod("createManagedObject", Class.class);
            final Method destroyManagedObjectMethod = injectionMgr.getClass().getMethod("destroyManagedObject", Object.class);
            rc.getSingletons().add(new ManagedBeanComponentProviderFactory(injectionMgr, createManagedObjectMethod, destroyManagedObjectMethod));
        }
        catch (NamingException ex) {
            ManagedBeanComponentProviderFactoryInitilizer.LOGGER.log(Level.CONFIG, "The managed beans injection manager API is not available. JAX-RS managed beans support is disabled.", ex);
        }
        catch (NoSuchMethodException ex2) {
            ManagedBeanComponentProviderFactoryInitilizer.LOGGER.log(Level.SEVERE, "The managed beans injection manager API does not conform to what is expected. JAX-RS managed beans support is disabled.", ex2);
        }
        catch (SecurityException ex3) {
            ManagedBeanComponentProviderFactoryInitilizer.LOGGER.log(Level.SEVERE, "Security issue when configuring to use the managed beans injection manager API. JAX-RS managed beans support is disabled.", ex3);
        }
        catch (LinkageError ex4) {
            ManagedBeanComponentProviderFactoryInitilizer.LOGGER.log(Level.SEVERE, "Linkage error when configuring to use the managed beans injection manager API. JAX-RS managed beans support is disabled.", ex4);
        }
    }
    
    static {
        LOGGER = Logger.getLogger(ManagedBeanComponentProviderFactoryInitilizer.class.getName());
    }
}
