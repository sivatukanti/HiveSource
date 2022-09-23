// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.impl.uri.rules;

import javax.ws.rs.WebApplicationException;
import com.sun.jersey.api.container.ContainerException;
import java.lang.reflect.InvocationTargetException;
import com.sun.jersey.api.container.MappableContainerException;
import com.sun.jersey.api.core.HttpContext;
import javax.ws.rs.core.SecurityContext;
import com.sun.jersey.spi.container.ContainerRequest;
import java.security.PrivilegedAction;
import com.sun.jersey.spi.container.SubjectSecurityContext;
import com.sun.jersey.core.reflection.ReflectionHelper;
import java.util.Iterator;
import com.sun.jersey.spi.uri.rules.UriRule;
import com.sun.jersey.spi.uri.rules.UriMatchResultContext;
import com.sun.jersey.server.probes.UriRuleProbeProvider;
import com.sun.jersey.spi.uri.rules.UriRuleContext;
import com.sun.jersey.spi.inject.Injectable;
import com.sun.jersey.api.uri.UriTemplate;
import com.sun.jersey.api.model.AbstractSubResourceLocator;
import com.sun.jersey.spi.monitoring.DispatchingListener;
import com.sun.jersey.spi.container.ContainerResponseFilter;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import java.lang.reflect.Method;
import com.sun.jersey.server.impl.inject.AbstractHttpContextInjectable;
import java.util.List;

public final class SubLocatorRule extends BaseRule
{
    private final List<AbstractHttpContextInjectable> is;
    private final Method m;
    private final List<ContainerRequestFilter> requestFilters;
    private final List<ContainerResponseFilter> responseFilters;
    private final DispatchingListener dispatchingListener;
    private final AbstractSubResourceLocator locator;
    
    public SubLocatorRule(final UriTemplate template, final List<Injectable> is, final List<ContainerRequestFilter> requestFilters, final List<ContainerResponseFilter> responseFilters, final DispatchingListener dispatchingListener, final AbstractSubResourceLocator locator) {
        super(template);
        this.is = (List<AbstractHttpContextInjectable>)AbstractHttpContextInjectable.transform(is);
        this.requestFilters = requestFilters;
        this.responseFilters = responseFilters;
        this.dispatchingListener = dispatchingListener;
        this.locator = locator;
        this.m = locator.getMethod();
    }
    
    @Override
    public boolean accept(final CharSequence path, final Object resource, final UriRuleContext context) {
        UriRuleProbeProvider.ruleAccept(SubLocatorRule.class.getSimpleName(), path, resource);
        this.pushMatch(context);
        Object subResource = this.invokeSubLocator(resource, context);
        if (subResource == null) {
            if (context.isTracingEnabled()) {
                this.trace(resource, subResource, context);
            }
            return false;
        }
        if (subResource instanceof Class) {
            subResource = context.getResource((Class)subResource);
        }
        this.dispatchingListener.onSubResource(Thread.currentThread().getId(), subResource.getClass());
        context.pushResource(subResource);
        if (context.isTracingEnabled()) {
            this.trace(resource, subResource, context);
        }
        final Iterator<UriRule> matches = context.getRules(subResource.getClass()).match(path, context);
        while (matches.hasNext()) {
            if (matches.next().accept(path, subResource, context)) {
                return true;
            }
        }
        return false;
    }
    
    private void trace(final Object resource, final Object subResource, final UriRuleContext context) {
        final String prevPath = context.getUriInfo().getMatchedURIs().get(1);
        final String currentPath = context.getUriInfo().getMatchedURIs().get(0);
        context.trace(String.format("accept sub-resource locator: \"%s\" : \"%s\" -> @Path(\"%s\") %s = %s", prevPath, currentPath.substring(prevPath.length()), this.getTemplate().getTemplate(), ReflectionHelper.methodInstanceToString(resource, this.m), subResource));
    }
    
    private Object invokeSubLocator(final Object resource, final UriRuleContext context) {
        context.pushContainerResponseFilters(this.responseFilters);
        ContainerRequest containerRequest = context.getContainerRequest();
        if (!this.requestFilters.isEmpty()) {
            for (final ContainerRequestFilter f : this.requestFilters) {
                containerRequest = f.filter(containerRequest);
                context.setContainerRequest(containerRequest);
            }
        }
        this.dispatchingListener.onSubResourceLocator(Thread.currentThread().getId(), this.locator);
        final SecurityContext sc = containerRequest.getSecurityContext();
        if (sc instanceof SubjectSecurityContext) {
            return ((SubjectSecurityContext)sc).doAsSubject(new PrivilegedAction() {
                @Override
                public Object run() {
                    return SubLocatorRule.this.dispatch(resource, context);
                }
            });
        }
        return this.dispatch(resource, context);
    }
    
    private Object dispatch(final Object resource, final UriRuleContext context) {
        try {
            if (this.is.isEmpty()) {
                return this.m.invoke(resource, new Object[0]);
            }
            final Object[] params = new Object[this.is.size()];
            int index = 0;
            for (final AbstractHttpContextInjectable i : this.is) {
                params[index++] = i.getValue(context);
            }
            return this.m.invoke(resource, params);
        }
        catch (InvocationTargetException e) {
            throw new MappableContainerException(e.getTargetException());
        }
        catch (IllegalAccessException e2) {
            throw new ContainerException(e2);
        }
        catch (WebApplicationException e3) {
            throw e3;
        }
        catch (RuntimeException e4) {
            throw new ContainerException("Exception injecting parameters for sub-locator method: " + this.m, e4);
        }
    }
}
