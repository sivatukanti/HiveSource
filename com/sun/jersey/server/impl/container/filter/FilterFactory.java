// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.impl.container.filter;

import java.util.Collections;
import java.util.Iterator;
import com.sun.jersey.spi.container.ResourceFilter;
import com.sun.jersey.api.model.AbstractMethod;
import java.util.Collection;
import com.sun.jersey.api.core.ResourceConfig;
import java.util.LinkedList;
import com.sun.jersey.spi.container.ResourceFilterFactory;
import com.sun.jersey.spi.container.ContainerResponseFilter;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import java.util.List;
import com.sun.jersey.core.spi.component.ProviderServices;
import java.util.logging.Logger;

public final class FilterFactory
{
    private static final Logger LOGGER;
    private final ProviderServices providerServices;
    private final List<ContainerRequestFilter> requestFilters;
    private final List<ContainerResponseFilter> responseFilters;
    private final List<ResourceFilterFactory> resourceFilterFactories;
    
    public FilterFactory(final ProviderServices providerServices) {
        this.requestFilters = new LinkedList<ContainerRequestFilter>();
        this.responseFilters = new LinkedList<ContainerResponseFilter>();
        this.resourceFilterFactories = new LinkedList<ResourceFilterFactory>();
        this.providerServices = providerServices;
    }
    
    public void init(final ResourceConfig resourceConfig) {
        this.requestFilters.addAll(this.getFilters(ContainerRequestFilter.class, resourceConfig.getContainerRequestFilters()));
        this.requestFilters.addAll(this.providerServices.getServices(ContainerRequestFilter.class));
        this.responseFilters.addAll(this.getFilters(ContainerResponseFilter.class, resourceConfig.getContainerResponseFilters()));
        this.responseFilters.addAll(this.providerServices.getServices(ContainerResponseFilter.class));
        this.resourceFilterFactories.addAll(this.getFilters(ResourceFilterFactory.class, resourceConfig.getResourceFilterFactories()));
        this.resourceFilterFactories.addAll(this.providerServices.getServices(ResourceFilterFactory.class));
        this.resourceFilterFactories.add(new AnnotationResourceFilterFactory(this));
    }
    
    public List<ContainerRequestFilter> getRequestFilters() {
        return this.requestFilters;
    }
    
    public List<ContainerResponseFilter> getResponseFilters() {
        return this.responseFilters;
    }
    
    public List<ResourceFilter> getResourceFilters(final AbstractMethod am) {
        final List<ResourceFilter> resourceFilters = new LinkedList<ResourceFilter>();
        for (final ResourceFilterFactory rff : this.resourceFilterFactories) {
            final List<ResourceFilter> rfs = rff.create(am);
            if (rfs != null) {
                resourceFilters.addAll(rfs);
            }
        }
        return resourceFilters;
    }
    
    public List<ResourceFilter> getResourceFilters(final Class<? extends ResourceFilter>[] classes) {
        if (classes == null || classes.length == 0) {
            return (List<ResourceFilter>)Collections.EMPTY_LIST;
        }
        return this.providerServices.getInstances(ResourceFilter.class, classes);
    }
    
    public static List<ContainerRequestFilter> getRequestFilters(final List<ResourceFilter> resourceFilters) {
        final List<ContainerRequestFilter> filters = new LinkedList<ContainerRequestFilter>();
        for (final ResourceFilter rf : resourceFilters) {
            final ContainerRequestFilter crf = rf.getRequestFilter();
            if (crf != null) {
                filters.add(crf);
            }
        }
        return filters;
    }
    
    public static List<ContainerResponseFilter> getResponseFilters(final List<ResourceFilter> resourceFilters) {
        final List<ContainerResponseFilter> filters = new LinkedList<ContainerResponseFilter>();
        for (final ResourceFilter rf : resourceFilters) {
            final ContainerResponseFilter crf = rf.getResponseFilter();
            if (crf != null) {
                filters.add(crf);
            }
        }
        return filters;
    }
    
    private <T> List<T> getFilters(final Class<T> c, final List<?> l) {
        final List<T> f = new LinkedList<T>();
        for (final Object o : l) {
            if (o instanceof String) {
                f.addAll((Collection<? extends T>)this.providerServices.getInstances(c, ResourceConfig.getElements(new String[] { (String)o }, " ,;\n")));
            }
            else if (o instanceof String[]) {
                f.addAll((Collection<? extends T>)this.providerServices.getInstances(c, ResourceConfig.getElements((String[])o, " ,;\n")));
            }
            else if (c.isInstance(o)) {
                f.add(c.cast(o));
            }
            else if (o instanceof Class) {
                final Class fc = (Class)o;
                if (c.isAssignableFrom(fc)) {
                    f.addAll((Collection<? extends T>)this.providerServices.getInstances(c, new Class[] { fc }));
                }
                else {
                    FilterFactory.LOGGER.severe("The filter, of type" + o.getClass().getName() + ", MUST be of the type Class<? extends" + c.getName() + ">" + ". The filter is ignored.");
                }
            }
            else {
                FilterFactory.LOGGER.severe("The filter, of type" + o.getClass().getName() + ", MUST be of the type String, String[], Class<? extends " + c.getName() + ">, or an instance of " + c.getName() + ". The filter is ignored.");
            }
        }
        this.providerServices.getComponentProviderFactory().injectOnProviderInstances(f);
        return f;
    }
    
    static {
        LOGGER = Logger.getLogger(FilterFactory.class.getName());
    }
}
