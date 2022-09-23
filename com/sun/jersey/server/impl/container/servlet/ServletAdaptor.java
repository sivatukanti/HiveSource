// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.impl.container.servlet;

import java.util.Enumeration;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import com.sun.jersey.server.impl.ThreadLocalNamedInvoker;
import com.sun.jersey.api.container.ContainerException;
import javax.persistence.EntityManagerFactory;
import com.sun.jersey.spi.inject.Injectable;
import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.ComponentScope;
import java.lang.reflect.Type;
import javax.persistence.PersistenceUnit;
import com.sun.jersey.spi.inject.InjectableProvider;
import com.sun.jersey.spi.container.WebApplication;
import com.sun.jersey.api.core.ResourceConfig;
import javax.servlet.ServletConfig;
import java.util.HashMap;
import java.util.Map;
import com.sun.jersey.spi.container.servlet.ServletContainer;

public class ServletAdaptor extends ServletContainer
{
    private Map<String, String> persistenceUnits;
    
    public ServletAdaptor() {
        this.persistenceUnits = new HashMap<String, String>();
    }
    
    @Override
    protected void configure(final ServletConfig servletConfig, final ResourceConfig rc, final WebApplication wa) {
        super.configure(servletConfig, rc, wa);
        final Enumeration e = servletConfig.getInitParameterNames();
        while (e.hasMoreElements()) {
            final String key = e.nextElement();
            final String value = servletConfig.getInitParameter(key);
            if (key.startsWith("unit:")) {
                this.persistenceUnits.put(key.substring(5), "java:comp/env/" + value);
            }
        }
        rc.getSingletons().add(new InjectableProvider<PersistenceUnit, Type>() {
            @Override
            public ComponentScope getScope() {
                return ComponentScope.Singleton;
            }
            
            @Override
            public Injectable<EntityManagerFactory> getInjectable(final ComponentContext ic, final PersistenceUnit pu, final Type c) {
                if (!c.equals(EntityManagerFactory.class)) {
                    return null;
                }
                if (!ServletAdaptor.this.persistenceUnits.containsKey(pu.unitName())) {
                    throw new ContainerException("Persistence unit '" + pu.unitName() + "' is not configured as a servlet parameter in web.xml");
                }
                final String jndiName = ServletAdaptor.this.persistenceUnits.get(pu.unitName());
                final ThreadLocalNamedInvoker<EntityManagerFactory> emfHandler = new ThreadLocalNamedInvoker<EntityManagerFactory>(jndiName);
                final EntityManagerFactory emf = (EntityManagerFactory)Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[] { EntityManagerFactory.class }, emfHandler);
                return new Injectable<EntityManagerFactory>() {
                    @Override
                    public EntityManagerFactory getValue() {
                        return emf;
                    }
                };
            }
        });
    }
}
