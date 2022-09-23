// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.impl.model.method.dispatch;

import java.lang.reflect.ParameterizedType;
import com.sun.jersey.core.spi.factory.ResponseBuilderImpl;
import java.lang.reflect.Type;
import java.lang.reflect.InvocationTargetException;
import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.server.impl.inject.InjectableValuesProvider;
import javax.ws.rs.core.GenericEntity;
import com.sun.jersey.api.JResponse;
import javax.ws.rs.core.Response;
import com.sun.jersey.spi.inject.Errors;
import com.sun.jersey.spi.container.JavaMethodInvoker;
import com.sun.jersey.spi.container.JavaMethodInvokerFactory;
import com.sun.jersey.spi.dispatch.RequestDispatcher;
import com.sun.jersey.api.model.AbstractResourceMethod;
import javax.ws.rs.core.Context;
import com.sun.jersey.server.impl.inject.ServerInjectableProviderContext;
import com.sun.jersey.spi.container.ResourceMethodCustomInvokerDispatchProvider;
import com.sun.jersey.spi.container.ResourceMethodDispatchProvider;

public abstract class AbstractResourceMethodDispatchProvider implements ResourceMethodDispatchProvider, ResourceMethodCustomInvokerDispatchProvider
{
    @Context
    private ServerInjectableProviderContext sipc;
    
    @Override
    public RequestDispatcher create(final AbstractResourceMethod abstractResourceMethod) {
        return this.create(abstractResourceMethod, JavaMethodInvokerFactory.getDefault());
    }
    
    @Override
    public RequestDispatcher create(final AbstractResourceMethod abstractResourceMethod, final JavaMethodInvoker invoker) {
        final InjectableValuesProvider pp = this.getInjectableValuesProvider(abstractResourceMethod);
        if (pp == null) {
            return null;
        }
        if (pp.getInjectables().contains(null)) {
            for (int i = 0; i < pp.getInjectables().size(); ++i) {
                if (pp.getInjectables().get(i) == null) {
                    Errors.missingDependency(abstractResourceMethod.getMethod(), i);
                }
            }
            return null;
        }
        final Class<?> returnType = (Class<?>)abstractResourceMethod.getReturnType();
        if (Response.class.isAssignableFrom(returnType)) {
            return new ResponseOutInvoker(abstractResourceMethod, pp, invoker);
        }
        if (JResponse.class.isAssignableFrom(returnType)) {
            return new JResponseOutInvoker(abstractResourceMethod, pp, invoker);
        }
        if (returnType == Void.TYPE) {
            return new VoidOutInvoker(abstractResourceMethod, pp, invoker);
        }
        if (returnType == Object.class || GenericEntity.class.isAssignableFrom(returnType)) {
            return new ObjectOutInvoker(abstractResourceMethod, pp, invoker);
        }
        return new TypeOutInvoker(abstractResourceMethod, pp, invoker);
    }
    
    protected ServerInjectableProviderContext getInjectableProviderContext() {
        return this.sipc;
    }
    
    protected abstract InjectableValuesProvider getInjectableValuesProvider(final AbstractResourceMethod p0);
    
    private abstract static class EntityParamInInvoker extends ResourceJavaMethodDispatcher
    {
        private final InjectableValuesProvider pp;
        
        EntityParamInInvoker(final AbstractResourceMethod abstractResourceMethod, final InjectableValuesProvider pp) {
            this(abstractResourceMethod, pp, JavaMethodInvokerFactory.getDefault());
        }
        
        EntityParamInInvoker(final AbstractResourceMethod abstractResourceMethod, final InjectableValuesProvider pp, final JavaMethodInvoker invoker) {
            super(abstractResourceMethod, invoker);
            this.pp = pp;
        }
        
        final Object[] getParams(final HttpContext context) {
            return this.pp.getInjectableValues(context);
        }
    }
    
    private static final class VoidOutInvoker extends EntityParamInInvoker
    {
        VoidOutInvoker(final AbstractResourceMethod abstractResourceMethod, final InjectableValuesProvider pp, final JavaMethodInvoker invoker) {
            super(abstractResourceMethod, pp, invoker);
        }
        
        public void _dispatch(final Object resource, final HttpContext context) throws IllegalAccessException, InvocationTargetException {
            final Object[] params = this.getParams(context);
            this.invoker.invoke(this.method, resource, params);
        }
    }
    
    private static final class TypeOutInvoker extends EntityParamInInvoker
    {
        private final Type t;
        
        TypeOutInvoker(final AbstractResourceMethod abstractResourceMethod, final InjectableValuesProvider pp, final JavaMethodInvoker invoker) {
            super(abstractResourceMethod, pp, invoker);
            this.t = abstractResourceMethod.getGenericReturnType();
        }
        
        public void _dispatch(final Object resource, final HttpContext context) throws IllegalAccessException, InvocationTargetException {
            final Object[] params = this.getParams(context);
            final Object o = this.invoker.invoke(this.method, resource, params);
            if (o != null) {
                final Response r = new ResponseBuilderImpl().entityWithType(o, this.t).status(200).build();
                context.getResponse().setResponse(r);
            }
        }
    }
    
    private static final class ResponseOutInvoker extends EntityParamInInvoker
    {
        ResponseOutInvoker(final AbstractResourceMethod abstractResourceMethod, final InjectableValuesProvider pp, final JavaMethodInvoker invoker) {
            super(abstractResourceMethod, pp, invoker);
        }
        
        public void _dispatch(final Object resource, final HttpContext context) throws IllegalAccessException, InvocationTargetException {
            final Object[] params = this.getParams(context);
            final Response r = (Response)this.invoker.invoke(this.method, resource, params);
            if (r != null) {
                context.getResponse().setResponse(r);
            }
        }
    }
    
    private static final class JResponseOutInvoker extends EntityParamInInvoker
    {
        private final Type t;
        
        JResponseOutInvoker(final AbstractResourceMethod abstractResourceMethod, final InjectableValuesProvider pp, final JavaMethodInvoker invoker) {
            super(abstractResourceMethod, pp);
            final Type jResponseType = abstractResourceMethod.getGenericReturnType();
            if (jResponseType instanceof ParameterizedType) {
                final ParameterizedType pt = (ParameterizedType)jResponseType;
                if (pt.getRawType().equals(JResponse.class)) {
                    this.t = ((ParameterizedType)jResponseType).getActualTypeArguments()[0];
                }
                else {
                    this.t = null;
                }
            }
            else {
                this.t = null;
            }
        }
        
        public void _dispatch(final Object resource, final HttpContext context) throws IllegalAccessException, InvocationTargetException {
            final Object[] params = this.getParams(context);
            final JResponse<?> r = (JResponse<?>)this.invoker.invoke(this.method, resource, params);
            if (r != null) {
                if (this.t == null) {
                    context.getResponse().setResponse(r.toResponse());
                }
                else {
                    context.getResponse().setResponse(r.toResponse(this.t));
                }
            }
        }
    }
    
    private static final class ObjectOutInvoker extends EntityParamInInvoker
    {
        ObjectOutInvoker(final AbstractResourceMethod abstractResourceMethod, final InjectableValuesProvider pp, final JavaMethodInvoker invoker) {
            super(abstractResourceMethod, pp, invoker);
        }
        
        public void _dispatch(final Object resource, final HttpContext context) throws IllegalAccessException, InvocationTargetException {
            final Object[] params = this.getParams(context);
            final Object o = this.invoker.invoke(this.method, resource, params);
            if (o instanceof Response) {
                context.getResponse().setResponse((Response)o);
            }
            else if (o instanceof JResponse) {
                context.getResponse().setResponse(((JResponse)o).toResponse());
            }
            else if (o != null) {
                final Response r = new ResponseBuilderImpl().status(200).entity(o).build();
                context.getResponse().setResponse(r);
            }
        }
    }
}
