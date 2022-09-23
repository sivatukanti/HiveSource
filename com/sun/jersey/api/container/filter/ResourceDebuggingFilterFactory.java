// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.api.container.filter;

import java.util.logging.Level;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerResponseFilter;
import java.util.logging.Logger;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import com.sun.jersey.api.model.AbstractSubResourceLocator;
import com.sun.jersey.api.model.AbstractResourceMethod;
import java.util.Collections;
import com.sun.jersey.api.model.AbstractSubResourceMethod;
import com.sun.jersey.spi.container.ResourceFilter;
import java.util.List;
import com.sun.jersey.api.model.AbstractMethod;
import javax.ws.rs.core.Context;
import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.spi.container.ResourceFilterFactory;

public class ResourceDebuggingFilterFactory implements ResourceFilterFactory
{
    private final HttpContext context;
    
    public ResourceDebuggingFilterFactory(@Context final HttpContext hc) {
        this.context = hc;
    }
    
    @Override
    public List<ResourceFilter> create(final AbstractMethod am) {
        if (am instanceof AbstractSubResourceMethod) {
            return (List<ResourceFilter>)Collections.singletonList(new SubResourceMethodFilter((AbstractSubResourceMethod)am));
        }
        if (am instanceof AbstractResourceMethod) {
            return (List<ResourceFilter>)Collections.singletonList(new ResourceMethodFilter((AbstractResourceMethod)am));
        }
        if (am instanceof AbstractSubResourceLocator) {
            return (List<ResourceFilter>)Collections.singletonList(new SubResourceLocatorFilter((AbstractSubResourceLocator)am));
        }
        return null;
    }
    
    abstract class AbstractRequestFilter implements ResourceFilter, ContainerRequestFilter
    {
        protected Logger LOGGER;
        
        AbstractRequestFilter() {
            this.LOGGER = Logger.getLogger(AbstractRequestFilter.class.getCanonicalName());
        }
        
        @Override
        public ContainerRequestFilter getRequestFilter() {
            return this;
        }
        
        @Override
        public ContainerResponseFilter getResponseFilter() {
            return null;
        }
    }
    
    private class ResourceMethodFilter extends AbstractRequestFilter
    {
        private final AbstractResourceMethod arm;
        
        public ResourceMethodFilter(final AbstractResourceMethod arm) {
            this.arm = arm;
        }
        
        @Override
        public ContainerRequest filter(final ContainerRequest request) {
            this.LOGGER.log(Level.INFO, "Resource Method matched.\n HttpMethod: " + this.arm.getHttpMethod() + "\n Resource: " + this.arm.getDeclaringResource().getResourceClass().getName() + "\n Method: " + this.arm.getMethod().toGenericString());
            return request;
        }
    }
    
    private class SubResourceMethodFilter extends AbstractRequestFilter
    {
        private final AbstractSubResourceMethod asrm;
        
        public SubResourceMethodFilter(final AbstractSubResourceMethod asrm) {
            this.asrm = asrm;
        }
        
        @Override
        public ContainerRequest filter(final ContainerRequest request) {
            this.LOGGER.log(Level.INFO, "Sub-Resource Method matched.\n Path: " + this.asrm.getPath().getValue() + ((ResourceDebuggingFilterFactory.this.context != null) ? ("\n Matched Result: " + ResourceDebuggingFilterFactory.this.context.getUriInfo().getMatchedResults().get(0)) : "") + "\n HttpMethod: " + this.asrm.getHttpMethod() + "\n Resource: " + this.asrm.getDeclaringResource().getResourceClass().getName() + "\n Method: " + this.asrm.getMethod().toGenericString());
            return request;
        }
    }
    
    private class SubResourceLocatorFilter extends AbstractRequestFilter
    {
        private final AbstractSubResourceLocator asrl;
        
        public SubResourceLocatorFilter(final AbstractSubResourceLocator asrl) {
            this.asrl = asrl;
        }
        
        @Override
        public ContainerRequest filter(final ContainerRequest request) {
            this.LOGGER.log(Level.INFO, "Sub-Resource Locator matched. \n Path: " + this.asrl.getPath().getValue() + ((ResourceDebuggingFilterFactory.this.context != null) ? ("\n Matched Result: " + ResourceDebuggingFilterFactory.this.context.getUriInfo().getMatchedResults().get(0)) : "") + "\n Resource: " + this.asrl.getResource().getResourceClass().getName() + "\n Method: " + this.asrl.getMethod().toGenericString());
            return request;
        }
    }
}
