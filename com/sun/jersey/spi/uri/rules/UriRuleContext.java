// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.spi.uri.rules;

import com.sun.jersey.api.model.AbstractResourceMethod;
import com.sun.jersey.api.uri.UriTemplate;
import com.sun.jersey.spi.container.ContainerResponseFilter;
import java.util.List;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.api.core.HttpContext;

public interface UriRuleContext extends HttpContext, UriMatchResultContext
{
    ContainerRequest getContainerRequest();
    
    void setContainerRequest(final ContainerRequest p0);
    
    ContainerResponse getContainerResponse();
    
    void setContainerResponse(final ContainerResponse p0);
    
    void pushContainerResponseFilters(final List<ContainerResponseFilter> p0);
    
    Object getResource(final Class p0);
    
    UriRules<UriRule> getRules(final Class p0);
    
    void pushMatch(final UriTemplate p0, final List<String> p1);
    
    void pushResource(final Object p0);
    
    void pushMethod(final AbstractResourceMethod p0);
    
    void pushRightHandPathLength(final int p0);
}
