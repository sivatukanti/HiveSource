// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.api.container.filter;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerResponseFilter;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import java.util.Collections;
import java.lang.annotation.Annotation;
import javax.annotation.security.DenyAll;
import com.sun.jersey.spi.container.ResourceFilter;
import java.util.List;
import com.sun.jersey.api.model.AbstractMethod;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
import com.sun.jersey.spi.container.ResourceFilterFactory;

public class RolesAllowedResourceFilterFactory implements ResourceFilterFactory
{
    @Context
    private SecurityContext sc;
    
    @Override
    public List<ResourceFilter> create(final AbstractMethod am) {
        if (am.isAnnotationPresent(DenyAll.class)) {
            return (List<ResourceFilter>)Collections.singletonList(new Filter());
        }
        RolesAllowed ra = am.getAnnotation(RolesAllowed.class);
        if (ra != null) {
            return (List<ResourceFilter>)Collections.singletonList(new Filter(ra.value()));
        }
        if (am.isAnnotationPresent(PermitAll.class)) {
            return null;
        }
        ra = am.getResource().getAnnotation(RolesAllowed.class);
        if (ra != null) {
            return (List<ResourceFilter>)Collections.singletonList(new Filter(ra.value()));
        }
        return null;
    }
    
    private class Filter implements ResourceFilter, ContainerRequestFilter
    {
        private final boolean denyAll;
        private final String[] rolesAllowed;
        
        protected Filter() {
            this.denyAll = true;
            this.rolesAllowed = null;
        }
        
        protected Filter(final String[] rolesAllowed) {
            this.denyAll = false;
            this.rolesAllowed = ((rolesAllowed != null) ? rolesAllowed : new String[0]);
        }
        
        @Override
        public ContainerRequestFilter getRequestFilter() {
            return this;
        }
        
        @Override
        public ContainerResponseFilter getResponseFilter() {
            return null;
        }
        
        @Override
        public ContainerRequest filter(final ContainerRequest request) {
            if (!this.denyAll) {
                for (final String role : this.rolesAllowed) {
                    if (RolesAllowedResourceFilterFactory.this.sc.isUserInRole(role)) {
                        return request;
                    }
                }
            }
            throw new WebApplicationException(Response.Status.FORBIDDEN);
        }
    }
}
