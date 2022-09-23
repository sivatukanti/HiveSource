// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.impl.application;

import java.util.ArrayList;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.PathSegment;
import com.sun.jersey.api.uri.UriComponent;
import com.sun.jersey.spi.uri.rules.UriRule;
import com.sun.jersey.spi.uri.rules.UriRules;
import com.sun.jersey.api.core.HttpContext;
import java.util.Iterator;
import java.util.Map;
import com.sun.jersey.api.core.HttpResponseContext;
import com.sun.jersey.api.core.HttpRequestContext;
import com.sun.jersey.spi.container.ContainerResponseWriter;
import java.io.InputStream;
import com.sun.jersey.spi.container.WebApplication;
import java.io.ByteArrayInputStream;
import com.sun.jersey.core.header.InBoundHeaders;
import javax.ws.rs.core.UriBuilder;
import com.sun.jersey.api.container.ContainerException;
import java.net.URI;
import com.sun.jersey.api.core.TraceInformation;
import java.util.Collections;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import com.sun.jersey.api.model.AbstractResourceMethod;
import com.sun.jersey.api.uri.UriTemplate;
import java.util.LinkedList;
import java.util.regex.MatchResult;
import com.sun.jersey.spi.container.ContainerResponseFilter;
import java.util.List;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.api.core.ExtendedUriInfo;
import com.sun.jersey.spi.uri.rules.UriRuleContext;

public final class WebApplicationContext implements UriRuleContext, ExtendedUriInfo
{
    public static final String HTTP_METHOD_MATCH_RESOURCE = "com.sun.jersey.MATCH_RESOURCE";
    private final WebApplicationImpl app;
    private final boolean isTraceEnabled;
    private ContainerRequest request;
    private ContainerResponse response;
    private List<ContainerResponseFilter> responseFilters;
    private MatchResult matchResult;
    private final LinkedList<Object> resources;
    private final LinkedList<MatchResult> matchResults;
    private final LinkedList<String> paths;
    private final LinkedList<UriTemplate> templates;
    private AbstractResourceMethod arm;
    private MultivaluedMapImpl encodedTemplateValues;
    private MultivaluedMapImpl decodedTemplateValues;
    
    public WebApplicationContext(final WebApplicationImpl app, final ContainerRequest request, final ContainerResponse response) {
        this.resources = new LinkedList<Object>();
        this.matchResults = new LinkedList<MatchResult>();
        this.paths = new LinkedList<String>();
        this.templates = new LinkedList<UriTemplate>();
        this.app = app;
        this.isTraceEnabled = app.isTracingEnabled();
        this.request = request;
        this.response = response;
        this.responseFilters = (List<ContainerResponseFilter>)Collections.EMPTY_LIST;
        if (this.isTracingEnabled()) {
            this.getProperties().put(TraceInformation.class.getName(), new TraceInformation(this));
        }
    }
    
    public WebApplicationContext createMatchResourceContext(URI u) {
        final URI base = this.request.getBaseUri();
        if (u.isAbsolute()) {
            final URI r = base.relativize(u);
            if (r == u) {
                throw new ContainerException("The URI " + u + " is not relative to the base URI " + base);
            }
        }
        else {
            u = UriBuilder.fromUri(base).path(u.getRawPath()).replaceQuery(u.getRawQuery()).fragment(u.getRawFragment()).build(new Object[0]);
        }
        final ContainerRequest _request = new ContainerRequest(this.app, "com.sun.jersey.MATCH_RESOURCE", base, u, new InBoundHeaders(), new ByteArrayInputStream(new byte[0]));
        _request.setSecurityContext(this.request.getSecurityContext());
        final ContainerResponse _response = new ContainerResponse(this.app, _request, null);
        return new WebApplicationContext(this.app, _request, _response);
    }
    
    public List<ContainerResponseFilter> getResponseFilters() {
        return this.responseFilters;
    }
    
    @Override
    public HttpRequestContext getRequest() {
        return this.request;
    }
    
    @Override
    public HttpResponseContext getResponse() {
        return this.response;
    }
    
    @Override
    public ExtendedUriInfo getUriInfo() {
        return this;
    }
    
    @Override
    public Map<String, Object> getProperties() {
        return this.request.getProperties();
    }
    
    @Override
    public boolean isTracingEnabled() {
        return this.isTraceEnabled;
    }
    
    @Override
    public void trace(final String message) {
        if (!this.isTracingEnabled()) {
            return;
        }
        this.request.trace(message);
    }
    
    @Override
    public MatchResult getMatchResult() {
        return this.matchResult;
    }
    
    @Override
    public void setMatchResult(final MatchResult matchResult) {
        this.matchResult = matchResult;
    }
    
    @Override
    public ContainerRequest getContainerRequest() {
        return this.request;
    }
    
    @Override
    public void setContainerRequest(final ContainerRequest request) {
        this.request = request;
        this.response.setContainerRequest(request);
    }
    
    @Override
    public ContainerResponse getContainerResponse() {
        return this.response;
    }
    
    @Override
    public void setContainerResponse(final ContainerResponse response) {
        this.response = response;
    }
    
    @Override
    public void pushContainerResponseFilters(final List<ContainerResponseFilter> filters) {
        if (filters.isEmpty()) {
            return;
        }
        if (this.responseFilters == Collections.EMPTY_LIST) {
            this.responseFilters = new LinkedList<ContainerResponseFilter>();
        }
        for (final ContainerResponseFilter f : filters) {
            this.responseFilters.add(0, f);
        }
    }
    
    @Override
    public Object getResource(final Class resourceClass) {
        return this.app.getResourceComponentProvider(resourceClass).getInstance(this);
    }
    
    @Override
    public UriRules<UriRule> getRules(final Class resourceClass) {
        return this.app.getUriRules(resourceClass);
    }
    
    @Override
    public void pushMatch(final UriTemplate template, final List<String> names) {
        this.matchResults.addFirst(this.matchResult);
        this.templates.addFirst(template);
        if (this.encodedTemplateValues == null) {
            this.encodedTemplateValues = new MultivaluedMapImpl();
        }
        int i = 1;
        for (final String name : names) {
            final String value = this.matchResult.group(i++);
            this.encodedTemplateValues.addFirst(name, value);
            if (this.decodedTemplateValues != null) {
                this.decodedTemplateValues.addFirst(UriComponent.decode(name, UriComponent.Type.PATH_SEGMENT), UriComponent.decode(value, UriComponent.Type.PATH));
            }
        }
    }
    
    @Override
    public void pushResource(final Object resource) {
        this.resources.addFirst(resource);
    }
    
    @Override
    public void pushMethod(final AbstractResourceMethod arm) {
        this.arm = arm;
    }
    
    @Override
    public void pushRightHandPathLength(final int rhpathlen) {
        final String ep = this.request.getPath(false);
        this.paths.addFirst(ep.substring(0, ep.length() - rhpathlen));
    }
    
    @Override
    public URI getBaseUri() {
        return this.request.getBaseUri();
    }
    
    @Override
    public UriBuilder getBaseUriBuilder() {
        return this.request.getBaseUriBuilder();
    }
    
    @Override
    public URI getAbsolutePath() {
        return this.request.getAbsolutePath();
    }
    
    @Override
    public UriBuilder getAbsolutePathBuilder() {
        return this.request.getAbsolutePathBuilder();
    }
    
    @Override
    public URI getRequestUri() {
        return this.request.getRequestUri();
    }
    
    @Override
    public UriBuilder getRequestUriBuilder() {
        return this.request.getRequestUriBuilder();
    }
    
    @Override
    public String getPath() {
        return this.request.getPath(true);
    }
    
    @Override
    public String getPath(final boolean decode) {
        return this.request.getPath(decode);
    }
    
    @Override
    public List<PathSegment> getPathSegments() {
        return this.request.getPathSegments(true);
    }
    
    @Override
    public List<PathSegment> getPathSegments(final boolean decode) {
        return this.request.getPathSegments(decode);
    }
    
    @Override
    public MultivaluedMap<String, String> getQueryParameters() {
        return this.request.getQueryParameters(true);
    }
    
    @Override
    public MultivaluedMap<String, String> getQueryParameters(final boolean decode) {
        return this.request.getQueryParameters(decode);
    }
    
    @Override
    public MultivaluedMap<String, String> getPathParameters() {
        return this.getPathParameters(true);
    }
    
    @Override
    public MultivaluedMap<String, String> getPathParameters(final boolean decode) {
        if (!decode) {
            return this.encodedTemplateValues;
        }
        if (this.decodedTemplateValues != null) {
            return this.decodedTemplateValues;
        }
        this.decodedTemplateValues = new MultivaluedMapImpl();
        for (final Map.Entry<String, List<String>> e : this.encodedTemplateValues.entrySet()) {
            final List<String> l = new ArrayList<String>();
            for (final String v : e.getValue()) {
                l.add(UriComponent.decode(v, UriComponent.Type.PATH));
            }
            this.decodedTemplateValues.put(UriComponent.decode(e.getKey(), UriComponent.Type.PATH_SEGMENT), l);
        }
        return this.decodedTemplateValues;
    }
    
    @Override
    public List<String> getMatchedURIs() {
        return this.getMatchedURIs(true);
    }
    
    @Override
    public List<String> getMatchedURIs(final boolean decode) {
        List<String> result;
        if (decode) {
            result = new ArrayList<String>(this.paths.size());
            for (final String path : this.paths) {
                result.add(UriComponent.decode(path, UriComponent.Type.PATH));
            }
        }
        else {
            result = this.paths;
        }
        return Collections.unmodifiableList((List<? extends String>)result);
    }
    
    @Override
    public List<Object> getMatchedResources() {
        return this.resources;
    }
    
    @Override
    public AbstractResourceMethod getMatchedMethod() {
        return this.arm;
    }
    
    @Override
    public Throwable getMappedThrowable() {
        return this.response.getMappedThrowable();
    }
    
    @Override
    public List<MatchResult> getMatchedResults() {
        return this.matchResults;
    }
    
    @Override
    public List<UriTemplate> getMatchedTemplates() {
        return this.templates;
    }
    
    @Override
    public List<PathSegment> getPathSegments(final String name) {
        return this.getPathSegments(name, true);
    }
    
    @Override
    public List<PathSegment> getPathSegments(final String name, final boolean decode) {
        final int[] bounds = this.getPathParameterBounds(name);
        if (bounds != null) {
            final String path = this.matchResults.getLast().group();
            int segmentsStart = 0;
            for (int x = 0; x < bounds[0]; ++x) {
                if (path.charAt(x) == '/') {
                    ++segmentsStart;
                }
            }
            int segmentsEnd = segmentsStart;
            for (int x2 = bounds[0]; x2 < bounds[1]; ++x2) {
                if (path.charAt(x2) == '/') {
                    ++segmentsEnd;
                }
            }
            return this.getPathSegments(decode).subList(segmentsStart - 1, segmentsEnd);
        }
        return Collections.emptyList();
    }
    
    private int[] getPathParameterBounds(final String name) {
        final Iterator<UriTemplate> iTemplate = this.templates.iterator();
        final Iterator<MatchResult> iMatchResult = this.matchResults.iterator();
        while (iTemplate.hasNext()) {
            MatchResult mr = iMatchResult.next();
            final int pIndex = this.getLastPathParameterIndex(name, iTemplate.next());
            if (pIndex != -1) {
                int pathLength = mr.group().length();
                int segmentIndex = mr.end(pIndex + 1);
                final int groupLength = segmentIndex - mr.start(pIndex + 1);
                while (iMatchResult.hasNext()) {
                    mr = iMatchResult.next();
                    segmentIndex += mr.group().length() - pathLength;
                    pathLength = mr.group().length();
                }
                final int[] bounds = { segmentIndex - groupLength, segmentIndex };
                return bounds;
            }
        }
        return null;
    }
    
    private int getLastPathParameterIndex(final String name, final UriTemplate t) {
        int i = 0;
        int pIndex = -1;
        for (final String parameterName : t.getTemplateVariables()) {
            if (parameterName.equals(name)) {
                pIndex = i;
            }
            ++i;
        }
        return pIndex;
    }
}
