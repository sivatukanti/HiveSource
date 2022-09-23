// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.plus.webapp;

import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.plus.annotation.LifeCycleCallbackCollection;
import org.eclipse.jetty.plus.annotation.InjectionCollection;
import org.eclipse.jetty.plus.annotation.RunAsCollection;
import javax.servlet.Servlet;
import org.eclipse.jetty.servlet.ServletHolder;
import java.util.EventListener;
import javax.servlet.Filter;
import javax.servlet.ServletException;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.servlet.ServletContextHandler;

public class PlusDecorator implements ServletContextHandler.Decorator
{
    private static final Logger LOG;
    protected WebAppContext _context;
    
    public PlusDecorator(final WebAppContext context) {
        this._context = context;
    }
    
    public void decorateFilterHolder(final FilterHolder filter) throws ServletException {
    }
    
    public <T extends Filter> T decorateFilterInstance(final T filter) throws ServletException {
        this.decorate((Object)filter);
        return filter;
    }
    
    public <T extends EventListener> T decorateListenerInstance(final T listener) throws ServletException {
        this.decorate((Object)listener);
        return listener;
    }
    
    public void decorateServletHolder(final ServletHolder holder) throws ServletException {
        this.decorate((Object)holder);
    }
    
    public <T extends Servlet> T decorateServletInstance(final T servlet) throws ServletException {
        this.decorate((Object)servlet);
        return servlet;
    }
    
    public void destroyFilterInstance(final Filter f) {
        this.destroy(f);
    }
    
    public void destroyServletInstance(final Servlet s) {
        this.destroy(s);
    }
    
    public void destroyListenerInstance(final EventListener l) {
        this.destroy(l);
    }
    
    protected void decorate(final Object o) throws ServletException {
        final RunAsCollection runAses = (RunAsCollection)this._context.getAttribute("org.eclipse.jetty.runAsCollection");
        if (runAses != null) {
            runAses.setRunAs(o);
        }
        final InjectionCollection injections = (InjectionCollection)this._context.getAttribute("org.eclipse.jetty.injectionCollection");
        if (injections != null) {
            injections.inject(o);
        }
        final LifeCycleCallbackCollection callbacks = (LifeCycleCallbackCollection)this._context.getAttribute("org.eclipse.jetty.lifecyleCallbackCollection");
        if (callbacks != null) {
            try {
                callbacks.callPostConstructCallback(o);
            }
            catch (Exception e) {
                throw new ServletException(e);
            }
        }
    }
    
    protected void destroy(final Object o) {
        final LifeCycleCallbackCollection callbacks = (LifeCycleCallbackCollection)this._context.getAttribute("org.eclipse.jetty.lifecyleCallbackCollection");
        if (callbacks != null) {
            try {
                callbacks.callPreDestroyCallback(o);
            }
            catch (Exception e) {
                PlusDecorator.LOG.warn("Destroying instance of " + o.getClass(), e);
            }
        }
    }
    
    static {
        LOG = Log.getLogger(PlusDecorator.class);
    }
}
