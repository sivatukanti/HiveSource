// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.impl.model.method.dispatch;

import java.lang.reflect.InvocationTargetException;
import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.api.core.HttpResponseContext;
import com.sun.jersey.api.core.HttpRequestContext;
import java.util.Arrays;
import com.sun.jersey.spi.container.JavaMethodInvoker;
import com.sun.jersey.spi.container.JavaMethodInvokerFactory;
import com.sun.jersey.spi.dispatch.RequestDispatcher;
import com.sun.jersey.api.model.AbstractResourceMethod;
import com.sun.jersey.spi.container.ResourceMethodCustomInvokerDispatchProvider;
import com.sun.jersey.spi.container.ResourceMethodDispatchProvider;

public class HttpReqResDispatchProvider implements ResourceMethodDispatchProvider, ResourceMethodCustomInvokerDispatchProvider
{
    private static final Class[] EXPECTED_METHOD_PARAMS;
    
    @Override
    public RequestDispatcher create(final AbstractResourceMethod abstractResourceMethod) {
        return this.create(abstractResourceMethod, JavaMethodInvokerFactory.getDefault());
    }
    
    @Override
    public RequestDispatcher create(final AbstractResourceMethod abstractResourceMethod, final JavaMethodInvoker invoker) {
        if (abstractResourceMethod.getMethod().getReturnType() != Void.TYPE) {
            return null;
        }
        final Class<?>[] parameters = abstractResourceMethod.getMethod().getParameterTypes();
        if (!Arrays.deepEquals(parameters, HttpReqResDispatchProvider.EXPECTED_METHOD_PARAMS)) {
            return null;
        }
        return new HttpReqResDispatcher(abstractResourceMethod, invoker);
    }
    
    static {
        EXPECTED_METHOD_PARAMS = new Class[] { HttpRequestContext.class, HttpResponseContext.class };
    }
    
    static final class HttpReqResDispatcher extends ResourceJavaMethodDispatcher
    {
        HttpReqResDispatcher(final AbstractResourceMethod abstractResourceMethod) {
            this(abstractResourceMethod, JavaMethodInvokerFactory.getDefault());
        }
        
        HttpReqResDispatcher(final AbstractResourceMethod abstractResourceMethod, final JavaMethodInvoker invoker) {
            super(abstractResourceMethod, invoker);
        }
        
        public void _dispatch(final Object resource, final HttpContext context) throws InvocationTargetException, IllegalAccessException {
            this.invoker.invoke(this.method, resource, context.getRequest(), context.getResponse());
        }
    }
}
