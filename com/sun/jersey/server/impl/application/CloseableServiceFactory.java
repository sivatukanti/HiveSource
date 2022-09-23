// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.impl.application;

import java.lang.annotation.Annotation;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.HashSet;
import java.util.Set;
import java.io.Closeable;
import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.api.core.HttpContext;
import java.util.logging.Logger;
import com.sun.jersey.spi.CloseableService;
import com.sun.jersey.spi.inject.Injectable;
import java.lang.reflect.Type;
import javax.ws.rs.core.Context;
import com.sun.jersey.spi.inject.InjectableProvider;

public class CloseableServiceFactory implements InjectableProvider<Context, Type>, Injectable<CloseableService>, CloseableService
{
    private static final Logger LOGGER;
    private final HttpContext context;
    
    public CloseableServiceFactory(@Context final HttpContext context) {
        this.context = context;
    }
    
    @Override
    public ComponentScope getScope() {
        return ComponentScope.Singleton;
    }
    
    @Override
    public Injectable getInjectable(final ComponentContext ic, final Context a, final Type c) {
        if (c != CloseableService.class) {
            return null;
        }
        return this;
    }
    
    @Override
    public CloseableService getValue() {
        return this;
    }
    
    @Override
    public void add(final Closeable c) {
        Set<Closeable> s = this.context.getProperties().get(CloseableServiceFactory.class.getName());
        if (s == null) {
            s = new HashSet<Closeable>();
            this.context.getProperties().put(CloseableServiceFactory.class.getName(), s);
        }
        s.add(c);
    }
    
    public void close(final HttpContext context) {
        final Set<Closeable> s = context.getProperties().get(CloseableServiceFactory.class.getName());
        if (s != null) {
            for (final Closeable c : s) {
                try {
                    c.close();
                }
                catch (Exception ex) {
                    CloseableServiceFactory.LOGGER.log(Level.SEVERE, "Unable to close", ex);
                }
            }
        }
    }
    
    static {
        LOGGER = Logger.getLogger(CloseableServiceFactory.class.getName());
    }
}
