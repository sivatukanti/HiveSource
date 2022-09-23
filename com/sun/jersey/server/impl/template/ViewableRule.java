// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.impl.template;

import com.sun.jersey.server.impl.uri.rules.HttpMethodRule;
import javax.ws.rs.core.MediaType;
import com.sun.jersey.api.core.HttpResponseContext;
import java.util.Iterator;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.template.ResolvedViewable;
import com.sun.jersey.api.core.HttpRequestContext;
import com.sun.jersey.core.reflection.ReflectionHelper;
import com.sun.jersey.api.view.Viewable;
import com.sun.jersey.server.probes.UriRuleProbeProvider;
import com.sun.jersey.spi.uri.rules.UriRuleContext;
import javax.ws.rs.core.Context;
import com.sun.jersey.spi.template.TemplateContext;
import com.sun.jersey.spi.container.ContainerResponseFilter;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import com.sun.jersey.core.header.QualitySourceMediaType;
import java.util.List;
import com.sun.jersey.spi.uri.rules.UriRule;

public class ViewableRule implements UriRule
{
    private final List<QualitySourceMediaType> priorityMediaTypes;
    private final List<ContainerRequestFilter> requestFilters;
    private final List<ContainerResponseFilter> responseFilters;
    @Context
    TemplateContext tc;
    
    public ViewableRule(final List<QualitySourceMediaType> priorityMediaTypes, final List<ContainerRequestFilter> requestFilters, final List<ContainerResponseFilter> responseFilters) {
        this.priorityMediaTypes = priorityMediaTypes;
        this.requestFilters = requestFilters;
        this.responseFilters = responseFilters;
    }
    
    @Override
    public final boolean accept(final CharSequence path, final Object resource, final UriRuleContext context) {
        UriRuleProbeProvider.ruleAccept(ViewableRule.class.getSimpleName(), path, resource);
        final HttpRequestContext request = context.getRequest();
        if (!request.getMethod().equals("GET") && !request.getMethod().equals("com.sun.jersey.MATCH_RESOURCE")) {
            return false;
        }
        final String templatePath = (path.length() > 0) ? context.getMatchResult().group(1) : "";
        final Viewable v = new Viewable(templatePath, resource);
        final ResolvedViewable rv = this.tc.resolveViewable(v);
        if (rv == null) {
            return false;
        }
        if (request.getMethod().equals("com.sun.jersey.MATCH_RESOURCE")) {
            return true;
        }
        if (context.isTracingEnabled()) {
            context.trace(String.format("accept implicit view: \"%s\" -> %s, %s", templatePath, ReflectionHelper.objectToString(resource), rv.getTemplateName()));
        }
        context.pushContainerResponseFilters(this.responseFilters);
        if (!this.requestFilters.isEmpty()) {
            ContainerRequest containerRequest = context.getContainerRequest();
            for (final ContainerRequestFilter f : this.requestFilters) {
                containerRequest = f.filter(containerRequest);
                context.setContainerRequest(containerRequest);
            }
        }
        final HttpResponseContext response = context.getResponse();
        response.setStatus(200);
        response.setEntity(rv);
        if (!response.getHttpHeaders().containsKey("Content-Type")) {
            final MediaType contentType = this.getContentType(request, response);
            response.getHttpHeaders().putSingle("Content-Type", contentType);
        }
        return true;
    }
    
    private MediaType getContentType(final HttpRequestContext request, final HttpResponseContext response) {
        final List<MediaType> accept = (this.priorityMediaTypes == null) ? request.getAcceptableMediaTypes() : HttpMethodRule.getSpecificAcceptableMediaTypes(request.getAcceptableMediaTypes(), this.priorityMediaTypes);
        if (!accept.isEmpty()) {
            final MediaType contentType = accept.get(0);
            if (!contentType.isWildcardType() && !contentType.isWildcardSubtype()) {
                return contentType;
            }
        }
        return null;
    }
}
