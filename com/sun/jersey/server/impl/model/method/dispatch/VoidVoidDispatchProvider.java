// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.impl.model.method.dispatch;

import java.lang.reflect.InvocationTargetException;
import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.spi.container.JavaMethodInvoker;
import com.sun.jersey.spi.container.JavaMethodInvokerFactory;
import com.sun.jersey.spi.dispatch.RequestDispatcher;
import com.sun.jersey.api.model.AbstractResourceMethod;
import com.sun.jersey.spi.container.ResourceMethodCustomInvokerDispatchProvider;
import com.sun.jersey.spi.container.ResourceMethodDispatchProvider;

public class VoidVoidDispatchProvider implements ResourceMethodDispatchProvider, ResourceMethodCustomInvokerDispatchProvider
{
    @Override
    public RequestDispatcher create(final AbstractResourceMethod abstractResourceMethod) {
        return this.create(abstractResourceMethod, JavaMethodInvokerFactory.getDefault());
    }
    
    @Override
    public RequestDispatcher create(final AbstractResourceMethod abstractResourceMethod, final JavaMethodInvoker invoker) {
        if (!abstractResourceMethod.getParameters().isEmpty()) {
            return null;
        }
        if (abstractResourceMethod.getReturnType() != Void.TYPE) {
            return null;
        }
        return new VoidVoidMethodInvoker(abstractResourceMethod, invoker);
    }
    
    public static final class VoidVoidMethodInvoker extends ResourceJavaMethodDispatcher
    {
        public VoidVoidMethodInvoker(final AbstractResourceMethod abstractResourceMethod) {
            this(abstractResourceMethod, JavaMethodInvokerFactory.getDefault());
        }
        
        public VoidVoidMethodInvoker(final AbstractResourceMethod abstractResourceMethod, final JavaMethodInvoker invoker) {
            super(abstractResourceMethod, invoker);
        }
        
        public void _dispatch(final Object resource, final HttpContext context) throws IllegalAccessException, InvocationTargetException {
            this.invoker.invoke(this.method, resource, new Object[0]);
        }
    }
}
