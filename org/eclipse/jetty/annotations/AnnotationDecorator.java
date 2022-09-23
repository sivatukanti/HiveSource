// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.annotations;

import javax.servlet.Servlet;
import org.eclipse.jetty.servlet.ServletHolder;
import java.util.EventListener;
import javax.servlet.Filter;
import javax.servlet.ServletException;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.servlet.ServletContextHandler;

public class AnnotationDecorator implements ServletContextHandler.Decorator
{
    AnnotationIntrospector _introspector;
    
    public AnnotationDecorator(final WebAppContext context) {
        (this._introspector = new AnnotationIntrospector()).registerHandler(new ResourceAnnotationHandler(context));
        this._introspector.registerHandler(new ResourcesAnnotationHandler(context));
        this._introspector.registerHandler(new RunAsAnnotationHandler(context));
        this._introspector.registerHandler(new PostConstructAnnotationHandler(context));
        this._introspector.registerHandler(new PreDestroyAnnotationHandler(context));
        this._introspector.registerHandler(new DeclareRolesAnnotationHandler(context));
    }
    
    public void decorateFilterHolder(final FilterHolder filter) throws ServletException {
    }
    
    public <T extends Filter> T decorateFilterInstance(final T filter) throws ServletException {
        this.introspect(filter);
        return filter;
    }
    
    public <T extends EventListener> T decorateListenerInstance(final T listener) throws ServletException {
        this.introspect(listener);
        return listener;
    }
    
    public void decorateServletHolder(final ServletHolder servlet) throws ServletException {
    }
    
    public <T extends Servlet> T decorateServletInstance(final T servlet) throws ServletException {
        this.introspect(servlet);
        return servlet;
    }
    
    public void destroyFilterInstance(final Filter f) {
    }
    
    public void destroyServletInstance(final Servlet s) {
    }
    
    public void destroyListenerInstance(final EventListener f) {
    }
    
    protected void introspect(final Object o) {
        this._introspector.introspect(o.getClass());
    }
}
