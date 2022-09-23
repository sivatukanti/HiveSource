// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.impl.application;

import com.sun.jersey.core.spi.component.ComponentProvider;
import javax.ws.rs.core.MediaType;
import java.util.Map;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.api.container.ContainerException;
import com.sun.jersey.core.spi.component.ProviderFactory;
import java.util.Collections;
import java.util.Set;
import javax.ws.rs.core.Application;
import java.util.logging.Logger;
import com.sun.jersey.api.core.DefaultResourceConfig;

public class DeferredResourceConfig extends DefaultResourceConfig
{
    private static final Logger LOGGER;
    private final Class<? extends Application> appClass;
    private final Set<Class<?>> defaultClasses;
    
    public DeferredResourceConfig(final Class<? extends Application> appClass) {
        this(appClass, Collections.emptySet());
    }
    
    public DeferredResourceConfig(final Class<? extends Application> appClass, final Set<Class<?>> defaultClasses) {
        this.appClass = appClass;
        this.defaultClasses = defaultClasses;
    }
    
    public ApplicationHolder getApplication(final ProviderFactory pf) {
        return new ApplicationHolder(pf);
    }
    
    static {
        LOGGER = Logger.getLogger(DeferredResourceConfig.class.getName());
    }
    
    public class ApplicationHolder
    {
        private final Application originalApp;
        private final DefaultResourceConfig adaptedApp;
        
        private ApplicationHolder(final ProviderFactory pf) {
            final ComponentProvider cp = pf.getComponentProvider(DeferredResourceConfig.this.appClass);
            if (cp == null) {
                throw new ContainerException("The Application class " + DeferredResourceConfig.this.appClass.getName() + " could not be instantiated");
            }
            this.originalApp = (Application)cp.getInstance();
            if ((this.originalApp.getClasses() == null || this.originalApp.getClasses().isEmpty()) && (this.originalApp.getSingletons() == null || this.originalApp.getSingletons().isEmpty())) {
                DeferredResourceConfig.LOGGER.info("Instantiated the Application class " + DeferredResourceConfig.this.appClass.getName() + ". The following root resource and provider classes are registered: " + DeferredResourceConfig.this.defaultClasses);
                (this.adaptedApp = new DefaultResourceConfig(DeferredResourceConfig.this.defaultClasses)).add(this.originalApp);
            }
            else {
                DeferredResourceConfig.LOGGER.info("Instantiated the Application class " + DeferredResourceConfig.this.appClass.getName());
                this.adaptedApp = null;
            }
            if (this.originalApp instanceof ResourceConfig) {
                final ResourceConfig rc = (ResourceConfig)this.originalApp;
                DeferredResourceConfig.this.getFeatures().putAll(rc.getFeatures());
                DeferredResourceConfig.this.getProperties().putAll(rc.getProperties());
                DeferredResourceConfig.this.getExplicitRootResources().putAll(rc.getExplicitRootResources());
                DeferredResourceConfig.this.getMediaTypeMappings().putAll(rc.getMediaTypeMappings());
                DeferredResourceConfig.this.getLanguageMappings().putAll(rc.getLanguageMappings());
            }
        }
        
        public Application getOriginalApplication() {
            return this.originalApp;
        }
        
        public Application getApplication() {
            return (this.adaptedApp != null) ? this.adaptedApp : this.originalApp;
        }
    }
}
