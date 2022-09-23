// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.impl.model.parameter;

import com.sun.jersey.server.impl.inject.AbstractHttpContextInjectable;
import java.lang.annotation.Annotation;
import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.api.core.ExtendedUriInfo;
import javax.ws.rs.core.UriInfo;
import com.sun.jersey.api.core.HttpContext;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.HttpHeaders;
import java.util.HashMap;
import com.sun.jersey.spi.inject.Injectable;
import java.util.Map;
import java.lang.reflect.Type;
import javax.ws.rs.core.Context;
import com.sun.jersey.spi.inject.InjectableProvider;

public final class HttpContextInjectableProvider implements InjectableProvider<Context, Type>
{
    private final Map<Type, Injectable> injectables;
    
    public HttpContextInjectableProvider() {
        this.injectables = new HashMap<Type, Injectable>();
        final HttpContextRequestInjectable re = new HttpContextRequestInjectable();
        this.injectables.put(HttpHeaders.class, re);
        this.injectables.put(Request.class, re);
        this.injectables.put(SecurityContext.class, re);
        this.injectables.put(HttpContext.class, new HttpContextInjectable());
        this.injectables.put(UriInfo.class, new UriInfoInjectable());
        this.injectables.put(ExtendedUriInfo.class, new UriInfoInjectable());
    }
    
    @Override
    public ComponentScope getScope() {
        return ComponentScope.PerRequest;
    }
    
    @Override
    public Injectable getInjectable(final ComponentContext ic, final Context a, final Type c) {
        return this.injectables.get(c);
    }
    
    private static final class HttpContextInjectable extends AbstractHttpContextInjectable<Object>
    {
        @Override
        public Object getValue(final HttpContext context) {
            return context;
        }
    }
    
    private static final class HttpContextRequestInjectable extends AbstractHttpContextInjectable<Object>
    {
        @Override
        public Object getValue(final HttpContext context) {
            return context.getRequest();
        }
    }
    
    private static final class UriInfoInjectable extends AbstractHttpContextInjectable<UriInfo>
    {
        @Override
        public UriInfo getValue(final HttpContext context) {
            return context.getUriInfo();
        }
    }
}
