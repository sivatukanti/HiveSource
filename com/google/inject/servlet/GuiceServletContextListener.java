// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.servlet;

import com.google.inject.Injector;
import javax.servlet.ServletContext;
import java.lang.ref.WeakReference;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public abstract class GuiceServletContextListener implements ServletContextListener
{
    static final String INJECTOR_NAME;
    
    public void contextInitialized(final ServletContextEvent servletContextEvent) {
        final ServletContext servletContext = servletContextEvent.getServletContext();
        GuiceFilter.servletContext = new WeakReference<ServletContext>(servletContext);
        final Injector injector = this.getInjector();
        injector.getInstance(InternalServletModule.BackwardsCompatibleServletContextProvider.class).set(servletContext);
        servletContext.setAttribute(GuiceServletContextListener.INJECTOR_NAME, injector);
    }
    
    public void contextDestroyed(final ServletContextEvent servletContextEvent) {
        final ServletContext servletContext = servletContextEvent.getServletContext();
        servletContext.removeAttribute(GuiceServletContextListener.INJECTOR_NAME);
    }
    
    protected abstract Injector getInjector();
    
    static {
        INJECTOR_NAME = Injector.class.getName();
    }
}
