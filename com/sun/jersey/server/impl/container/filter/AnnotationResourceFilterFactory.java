// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.impl.container.filter;

import com.sun.jersey.spi.container.ResourceFilters;
import com.sun.jersey.spi.container.ResourceFilter;
import java.util.List;
import com.sun.jersey.api.model.AbstractMethod;
import com.sun.jersey.spi.container.ResourceFilterFactory;

public final class AnnotationResourceFilterFactory implements ResourceFilterFactory
{
    private FilterFactory ff;
    
    public AnnotationResourceFilterFactory(final FilterFactory ff) {
        this.ff = ff;
    }
    
    @Override
    public List<ResourceFilter> create(final AbstractMethod am) {
        ResourceFilters rfs = am.getAnnotation(ResourceFilters.class);
        if (rfs == null) {
            rfs = am.getResource().getAnnotation(ResourceFilters.class);
        }
        if (rfs == null) {
            return null;
        }
        return this.getResourceFilters(rfs.value());
    }
    
    private List<ResourceFilter> getResourceFilters(final Class<? extends ResourceFilter>[] classes) {
        if (classes == null || classes.length == 0) {
            return null;
        }
        return this.ff.getResourceFilters(classes);
    }
}
