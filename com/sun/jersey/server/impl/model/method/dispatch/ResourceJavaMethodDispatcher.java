// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.impl.model.method.dispatch;

import com.sun.jersey.api.container.ContainerException;
import java.lang.reflect.InvocationTargetException;
import com.sun.jersey.api.container.MappableContainerException;
import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.api.model.AbstractResourceMethod;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import com.sun.jersey.spi.container.JavaMethodInvoker;
import com.sun.jersey.spi.dispatch.RequestDispatcher;

public abstract class ResourceJavaMethodDispatcher implements RequestDispatcher
{
    protected final JavaMethodInvoker invoker;
    protected final Method method;
    private final Annotation[] annotations;
    
    public ResourceJavaMethodDispatcher(final AbstractResourceMethod abstractResourceMethod, final JavaMethodInvoker invoker) {
        this.method = abstractResourceMethod.getMethod();
        this.annotations = abstractResourceMethod.getAnnotations();
        this.invoker = invoker;
    }
    
    @Override
    public final void dispatch(final Object resource, final HttpContext context) {
        try {
            this._dispatch(resource, context);
            if (context.getResponse().getEntity() != null) {
                context.getResponse().setAnnotations(this.annotations);
            }
        }
        catch (InvocationTargetException e) {
            throw new MappableContainerException(e.getTargetException());
        }
        catch (IllegalAccessException e2) {
            throw new ContainerException(e2);
        }
    }
    
    protected abstract void _dispatch(final Object p0, final HttpContext p1) throws InvocationTargetException, IllegalAccessException;
    
    @Override
    public String toString() {
        return this.method.toString();
    }
}
