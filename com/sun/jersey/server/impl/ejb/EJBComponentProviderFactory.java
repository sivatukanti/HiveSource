// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.impl.ejb;

import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.core.spi.component.ioc.IoCFullyManagedComponentProvider;
import com.sun.jersey.core.spi.component.ComponentProvider;
import com.sun.jersey.core.spi.component.ioc.IoCComponentProcessorFactory;
import javax.ejb.Singleton;
import java.lang.annotation.Annotation;
import javax.ejb.Stateless;
import javax.naming.NamingException;
import com.sun.jersey.api.container.ContainerException;
import java.util.logging.Level;
import javax.naming.InitialContext;
import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.ioc.IoCComponentProvider;
import java.util.logging.Logger;
import com.sun.jersey.core.util.Priority;
import com.sun.jersey.core.spi.component.ioc.IoCComponentProcessorFactoryInitializer;
import com.sun.jersey.core.spi.component.ioc.IoCComponentProviderFactory;

@Priority(300)
final class EJBComponentProviderFactory implements IoCComponentProviderFactory, IoCComponentProcessorFactoryInitializer
{
    private static final Logger LOGGER;
    private final EJBInjectionInterceptor interceptor;
    
    public EJBComponentProviderFactory(final EJBInjectionInterceptor interceptor) {
        this.interceptor = interceptor;
    }
    
    @Override
    public IoCComponentProvider getComponentProvider(final Class<?> c) {
        return this.getComponentProvider(null, c);
    }
    
    @Override
    public IoCComponentProvider getComponentProvider(final ComponentContext cc, final Class<?> c) {
        final String name = this.getName(c);
        if (name == null) {
            return null;
        }
        try {
            final InitialContext ic = new InitialContext();
            final Object o = this.lookup(ic, c, name);
            EJBComponentProviderFactory.LOGGER.info("Binding the EJB class " + c.getName() + " to EJBManagedComponentProvider");
            return new EJBManagedComponentProvider(o);
        }
        catch (NamingException ex) {
            final String message = "An instance of EJB class " + c.getName() + " could not be looked up using simple form name or the fully-qualified form name." + "Ensure that the EJB/JAX-RS component implements at most one interface.";
            EJBComponentProviderFactory.LOGGER.log(Level.SEVERE, message, ex);
            throw new ContainerException(message);
        }
    }
    
    private String getName(final Class<?> c) {
        String name = null;
        if (c.isAnnotationPresent((Class<? extends Annotation>)Stateless.class)) {
            name = c.getAnnotation(Stateless.class).name();
        }
        else {
            if (!c.isAnnotationPresent((Class<? extends Annotation>)Singleton.class)) {
                return null;
            }
            name = c.getAnnotation(Singleton.class).name();
        }
        if (name == null || name.length() == 0) {
            name = c.getSimpleName();
        }
        return name;
    }
    
    private Object lookup(final InitialContext ic, final Class<?> c, final String name) throws NamingException {
        try {
            return this.lookupSimpleForm(ic, c, name);
        }
        catch (NamingException ex) {
            EJBComponentProviderFactory.LOGGER.log(Level.WARNING, "An instance of EJB class " + c.getName() + " could not be looked up using simple form name. " + "Attempting to look up using the fully-qualified form name.", ex);
            return this.lookupFullyQualfiedForm(ic, c, name);
        }
    }
    
    private Object lookupSimpleForm(final InitialContext ic, final Class<?> c, final String name) throws NamingException {
        final String jndiName = "java:module/" + name;
        return ic.lookup(jndiName);
    }
    
    private Object lookupFullyQualfiedForm(final InitialContext ic, final Class<?> c, final String name) throws NamingException {
        final String jndiName = "java:module/" + name + "!" + c.getName();
        return ic.lookup(jndiName);
    }
    
    @Override
    public void init(final IoCComponentProcessorFactory cpf) {
        this.interceptor.setFactory(cpf);
    }
    
    static {
        LOGGER = Logger.getLogger(EJBComponentProviderFactory.class.getName());
    }
    
    private static class EJBManagedComponentProvider implements IoCFullyManagedComponentProvider
    {
        private final Object o;
        
        EJBManagedComponentProvider(final Object o) {
            this.o = o;
        }
        
        @Override
        public ComponentScope getScope() {
            return ComponentScope.Singleton;
        }
        
        @Override
        public Object getInstance() {
            return this.o;
        }
    }
}
