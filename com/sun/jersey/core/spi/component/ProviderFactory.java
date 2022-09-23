// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.core.spi.component;

import java.util.Collection;
import java.util.Iterator;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.HashMap;
import com.sun.jersey.spi.inject.InjectableProviderContext;
import java.util.Map;
import java.util.logging.Logger;

public class ProviderFactory implements ComponentProviderFactory<ComponentProvider>
{
    protected static final Logger LOGGER;
    private final Map<Class, ComponentProvider> cache;
    private final InjectableProviderContext ipc;
    
    public ProviderFactory(final InjectableProviderContext ipc) {
        this.cache = new HashMap<Class, ComponentProvider>();
        this.ipc = ipc;
    }
    
    public InjectableProviderContext getInjectableProviderContext() {
        return this.ipc;
    }
    
    public final ComponentProvider getComponentProvider(final ProviderServices.ProviderClass pc) {
        if (!pc.isServiceClass) {
            return this.getComponentProvider(pc.c);
        }
        ComponentProvider cp = this.cache.get(pc.c);
        if (cp != null) {
            return cp;
        }
        cp = this.__getComponentProvider(pc.c);
        if (cp != null) {
            this.cache.put(pc.c, cp);
        }
        return cp;
    }
    
    @Override
    public final ComponentProvider getComponentProvider(final Class c) {
        ComponentProvider cp = this.cache.get(c);
        if (cp != null) {
            return cp;
        }
        cp = this._getComponentProvider(c);
        if (cp != null) {
            this.cache.put(c, cp);
        }
        return cp;
    }
    
    protected ComponentProvider _getComponentProvider(final Class c) {
        return this.__getComponentProvider(c);
    }
    
    private ComponentProvider __getComponentProvider(final Class c) {
        try {
            final ComponentInjector ci = new ComponentInjector(this.ipc, c);
            final ComponentConstructor cc = new ComponentConstructor(this.ipc, c, ci);
            final Object o = cc.getInstance();
            return new SingletonComponentProvider(ci, o);
        }
        catch (NoClassDefFoundError ex) {
            ProviderFactory.LOGGER.log(Level.CONFIG, "A dependent class, " + ex.getLocalizedMessage() + ", of the component " + c + " is not found." + " The component is ignored.");
            return null;
        }
        catch (InvocationTargetException ex2) {
            if (ex2.getCause() instanceof NoClassDefFoundError) {
                final NoClassDefFoundError ncdf = (NoClassDefFoundError)ex2.getCause();
                ProviderFactory.LOGGER.log(Level.CONFIG, "A dependent class, " + ncdf.getLocalizedMessage() + ", of the component " + c + " is not found." + " The component is ignored.");
                return null;
            }
            ProviderFactory.LOGGER.log(Level.SEVERE, "The provider class, " + c + ", could not be instantiated. Processing will continue but the class will not be utilized", ex2.getTargetException());
            return null;
        }
        catch (Exception ex3) {
            ProviderFactory.LOGGER.log(Level.SEVERE, "The provider class, " + c + ", could not be instantiated. Processing will continue but the class will not be utilized", ex3);
            return null;
        }
    }
    
    public void injectOnAllComponents() {
        for (final ComponentProvider cp : this.cache.values()) {
            if (cp instanceof SingletonComponentProvider) {
                final SingletonComponentProvider scp = (SingletonComponentProvider)cp;
                scp.inject();
            }
        }
    }
    
    public void destroy() {
        for (final ComponentProvider cp : this.cache.values()) {
            if (cp instanceof Destroyable) {
                final Destroyable d = (Destroyable)cp;
                d.destroy();
            }
        }
    }
    
    public void injectOnProviderInstances(final Collection<?> providers) {
        for (final Object o : providers) {
            this.injectOnProviderInstance(o);
        }
    }
    
    public void injectOnProviderInstance(final Object provider) {
        final Class c = provider.getClass();
        final ComponentInjector ci = new ComponentInjector(this.ipc, c);
        ci.inject(provider);
    }
    
    static {
        LOGGER = Logger.getLogger(ProviderFactory.class.getName());
    }
    
    private static final class SingletonComponentProvider implements ComponentProvider, Destroyable
    {
        private final Object o;
        private final ComponentDestructor cd;
        private final ComponentInjector ci;
        
        SingletonComponentProvider(final ComponentInjector ci, final Object o) {
            this.cd = new ComponentDestructor(o.getClass());
            this.ci = ci;
            this.o = o;
        }
        
        @Override
        public Object getInstance() {
            return this.o;
        }
        
        public void inject() {
            this.ci.inject(this.o);
        }
        
        @Override
        public void destroy() {
            try {
                this.cd.destroy(this.o);
            }
            catch (IllegalAccessException ex) {
                ProviderFactory.LOGGER.log(Level.SEVERE, "Unable to destroy resource", ex);
            }
            catch (IllegalArgumentException ex2) {
                ProviderFactory.LOGGER.log(Level.SEVERE, "Unable to destroy resource", ex2);
            }
            catch (InvocationTargetException ex3) {
                ProviderFactory.LOGGER.log(Level.SEVERE, "Unable to destroy resource", ex3);
            }
        }
    }
    
    protected interface Destroyable
    {
        void destroy();
    }
}
