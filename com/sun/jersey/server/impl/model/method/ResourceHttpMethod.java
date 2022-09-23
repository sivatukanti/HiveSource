// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.impl.model.method;

import java.lang.reflect.Method;
import com.sun.jersey.spi.inject.Errors;
import com.sun.jersey.impl.ImplMessages;
import javax.ws.rs.core.MediaType;
import com.sun.jersey.spi.container.ResourceFilter;
import java.util.List;
import com.sun.jersey.api.model.AbstractMethod;
import com.sun.jersey.api.uri.UriTemplate;
import com.sun.jersey.server.impl.container.filter.FilterFactory;
import com.sun.jersey.spi.container.ResourceMethodDispatchProvider;
import com.sun.jersey.api.model.AbstractResourceMethod;

public final class ResourceHttpMethod extends ResourceMethod
{
    private final AbstractResourceMethod arm;
    
    public ResourceHttpMethod(final ResourceMethodDispatchProvider dp, final FilterFactory ff, final AbstractResourceMethod arm) {
        this(dp, ff, UriTemplate.EMPTY, arm);
    }
    
    public ResourceHttpMethod(final ResourceMethodDispatchProvider dp, final FilterFactory ff, final UriTemplate template, final AbstractResourceMethod arm) {
        this(dp, ff, ff.getResourceFilters(arm), template, arm);
    }
    
    public ResourceHttpMethod(final ResourceMethodDispatchProvider dp, final FilterFactory ff, final List<ResourceFilter> resourceFilters, final UriTemplate template, final AbstractResourceMethod arm) {
        super(arm.getHttpMethod(), template, arm.getSupportedInputTypes(), arm.getSupportedOutputTypes(), arm.areOutputTypesDeclared(), dp.create(arm), FilterFactory.getRequestFilters(resourceFilters), FilterFactory.getResponseFilters(resourceFilters));
        this.arm = arm;
        if (this.getDispatcher() == null) {
            final Method m = arm.getMethod();
            final String msg = ImplMessages.NOT_VALID_HTTPMETHOD(m, arm.getHttpMethod(), m.getDeclaringClass());
            Errors.error(msg);
        }
    }
    
    @Override
    public AbstractResourceMethod getAbstractResourceMethod() {
        return this.arm;
    }
    
    @Override
    public String toString() {
        final Method m = this.arm.getMethod();
        return ImplMessages.RESOURCE_METHOD(m.getDeclaringClass(), m.getName());
    }
}
