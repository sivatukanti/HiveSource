// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.impl.model.method;

import com.sun.jersey.core.header.MediaTypes;
import com.sun.jersey.api.model.AbstractResourceMethod;
import java.util.Iterator;
import java.util.Collections;
import com.sun.jersey.spi.container.ContainerResponseFilter;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import com.sun.jersey.spi.dispatch.RequestDispatcher;
import javax.ws.rs.core.MediaType;
import java.util.List;
import com.sun.jersey.api.uri.UriTemplate;
import java.util.Comparator;

public abstract class ResourceMethod
{
    public static final Comparator<ResourceMethod> COMPARATOR;
    private final String httpMethod;
    private final UriTemplate template;
    private final List<? extends MediaType> consumeMime;
    private final List<? extends MediaType> produceMime;
    private final boolean isProducesDeclared;
    private final RequestDispatcher dispatcher;
    private final List<ContainerRequestFilter> requestFilters;
    private final List<ContainerResponseFilter> responseFilters;
    
    public ResourceMethod(final String httpMethod, final UriTemplate template, final List<? extends MediaType> consumeMime, final List<? extends MediaType> produceMime, final boolean isProducesDeclared, final RequestDispatcher dispatcher) {
        this(httpMethod, template, consumeMime, produceMime, isProducesDeclared, dispatcher, Collections.EMPTY_LIST, Collections.EMPTY_LIST);
    }
    
    public ResourceMethod(final String httpMethod, final UriTemplate template, final List<? extends MediaType> consumeMime, final List<? extends MediaType> produceMime, final boolean isProducesDeclared, final RequestDispatcher dispatcher, final List<ContainerRequestFilter> requestFilters, final List<ContainerResponseFilter> responseFilters) {
        this.httpMethod = httpMethod;
        this.template = template;
        this.consumeMime = consumeMime;
        this.produceMime = produceMime;
        this.isProducesDeclared = isProducesDeclared;
        this.dispatcher = dispatcher;
        this.requestFilters = requestFilters;
        this.responseFilters = responseFilters;
    }
    
    public final String getHttpMethod() {
        return this.httpMethod;
    }
    
    public final UriTemplate getTemplate() {
        return this.template;
    }
    
    public final List<? extends MediaType> getConsumes() {
        return this.consumeMime;
    }
    
    public final List<? extends MediaType> getProduces() {
        return this.produceMime;
    }
    
    public final boolean isProducesDeclared() {
        return this.isProducesDeclared;
    }
    
    public final RequestDispatcher getDispatcher() {
        return this.dispatcher;
    }
    
    public final List<ContainerRequestFilter> getRequestFilters() {
        return this.requestFilters;
    }
    
    public final List<ContainerResponseFilter> getResponseFilters() {
        return this.responseFilters;
    }
    
    public final boolean consumes(final MediaType contentType) {
        for (final MediaType c : this.consumeMime) {
            if (c.getType().equals("*")) {
                return true;
            }
            if (contentType.isCompatible(c)) {
                return true;
            }
        }
        return false;
    }
    
    public final boolean consumesWild() {
        for (final MediaType c : this.consumeMime) {
            if (c.getType().equals("*")) {
                return true;
            }
        }
        return false;
    }
    
    public final boolean mediaEquals(final ResourceMethod that) {
        final boolean v = this.consumeMime.equals(that.consumeMime);
        return v && this.produceMime.equals(that.produceMime);
    }
    
    public AbstractResourceMethod getAbstractResourceMethod() {
        return null;
    }
    
    static {
        COMPARATOR = new Comparator<ResourceMethod>() {
            @Override
            public int compare(final ResourceMethod o1, final ResourceMethod o2) {
                int i = MediaTypes.MEDIA_TYPE_LIST_COMPARATOR.compare(o1.consumeMime, o2.consumeMime);
                if (i == 0) {
                    i = MediaTypes.MEDIA_TYPE_LIST_COMPARATOR.compare(o1.produceMime, o2.produceMime);
                }
                return i;
            }
        };
    }
}
