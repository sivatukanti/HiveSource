// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.impl.uri.rules;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.ArrayList;
import com.sun.jersey.core.header.QualitySourceMediaType;
import java.util.Collections;
import com.sun.jersey.core.header.MediaTypes;
import javax.ws.rs.core.SecurityContext;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.api.core.HttpResponseContext;
import com.sun.jersey.api.core.HttpRequestContext;
import com.sun.jersey.api.core.HttpContext;
import java.security.PrivilegedAction;
import com.sun.jersey.spi.container.SubjectSecurityContext;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import com.sun.jersey.server.impl.template.ViewResourceMethod;
import javax.ws.rs.core.MediaType;
import com.sun.jersey.api.Responses;
import com.sun.jersey.core.reflection.ReflectionHelper;
import com.sun.jersey.server.probes.UriRuleProbeProvider;
import com.sun.jersey.spi.uri.rules.UriRuleContext;
import java.util.Iterator;
import java.util.HashMap;
import com.sun.jersey.server.impl.model.method.ResourceMethod;
import java.util.List;
import com.sun.jersey.spi.monitoring.DispatchingListener;
import java.util.Map;
import com.sun.jersey.spi.uri.rules.UriRule;

public final class HttpMethodRule implements UriRule
{
    public static final String CONTENT_TYPE_PROPERTY = "com.sun.jersey.server.impl.uri.rules.HttpMethodRule.Content-Type";
    private final Map<String, ResourceMethodListPair> map;
    private final String allow;
    private final boolean isSubResource;
    private final DispatchingListener dispatchingListener;
    
    public HttpMethodRule(final Map<String, List<ResourceMethod>> methods, final DispatchingListener dispatchingListener) {
        this(methods, false, dispatchingListener);
    }
    
    public HttpMethodRule(final Map<String, List<ResourceMethod>> methods, final boolean isSubResource, final DispatchingListener dispatchingListener) {
        this.map = new HashMap<String, ResourceMethodListPair>();
        for (final Map.Entry<String, List<ResourceMethod>> e : methods.entrySet()) {
            this.map.put(e.getKey(), new ResourceMethodListPair(e.getValue()));
        }
        this.isSubResource = isSubResource;
        this.allow = this.getAllow(methods);
        this.dispatchingListener = dispatchingListener;
    }
    
    private String getAllow(final Map<String, List<ResourceMethod>> methods) {
        final StringBuilder s = new StringBuilder();
        for (final String method : methods.keySet()) {
            if (s.length() > 0) {
                s.append(",");
            }
            s.append(method);
        }
        return s.toString();
    }
    
    @Override
    public boolean accept(final CharSequence path, final Object resource, final UriRuleContext context) {
        UriRuleProbeProvider.ruleAccept(HttpMethodRule.class.getSimpleName(), path, resource);
        if (path.length() > 0) {
            return false;
        }
        final HttpRequestContext request = context.getRequest();
        if (request.getMethod().equals("com.sun.jersey.MATCH_RESOURCE")) {
            return true;
        }
        if (context.isTracingEnabled()) {
            final String currentPath = context.getUriInfo().getMatchedURIs().get(0);
            if (this.isSubResource) {
                final String prevPath = context.getUriInfo().getMatchedURIs().get(1);
                context.trace(String.format("accept sub-resource methods: \"%s\" : \"%s\", %s -> %s", prevPath, currentPath.substring(prevPath.length()), context.getRequest().getMethod(), ReflectionHelper.objectToString(resource)));
            }
            else {
                context.trace(String.format("accept resource methods: \"%s\", %s -> %s", currentPath, context.getRequest().getMethod(), ReflectionHelper.objectToString(resource)));
            }
        }
        final HttpResponseContext response = context.getResponse();
        final ResourceMethodListPair methods = this.map.get(request.getMethod());
        if (methods == null) {
            response.setResponse(Responses.methodNotAllowed().header("Allow", this.allow).build());
            return false;
        }
        final List<MediaType> accept = getSpecificAcceptableMediaTypes(request.getAcceptableMediaTypes(), methods.priorityMediaTypes);
        final Matcher m = new Matcher();
        final MatchStatus s = m.match(methods, request.getMediaType(), accept);
        if (s == MatchStatus.MATCH) {
            final ResourceMethod method = m.rmSelected;
            if (method instanceof ViewResourceMethod) {
                if (!m.mSelected.isWildcardType() && !m.mSelected.isWildcardSubtype()) {
                    response.getHttpHeaders().putSingle("Content-Type", m.mSelected);
                }
                return false;
            }
            if (this.isSubResource) {
                context.pushResource(resource);
                context.pushMatch(method.getTemplate(), method.getTemplate().getTemplateVariables());
            }
            if (context.isTracingEnabled()) {
                if (this.isSubResource) {
                    context.trace(String.format("matched sub-resource method: @Path(\"%s\") %s", method.getTemplate(), method.getDispatcher()));
                }
                else {
                    context.trace(String.format("matched resource method: %s", method.getDispatcher()));
                }
            }
            context.pushContainerResponseFilters(method.getResponseFilters());
            ContainerRequest containerRequest = context.getContainerRequest();
            if (!method.getRequestFilters().isEmpty()) {
                for (final ContainerRequestFilter f : method.getRequestFilters()) {
                    containerRequest = f.filter(containerRequest);
                    context.setContainerRequest(containerRequest);
                }
            }
            context.pushMethod(method.getAbstractResourceMethod());
            try {
                this.dispatchingListener.onResourceMethod(Thread.currentThread().getId(), method.getAbstractResourceMethod());
                final SecurityContext sc = containerRequest.getSecurityContext();
                if (sc instanceof SubjectSecurityContext) {
                    ((SubjectSecurityContext)sc).doAsSubject(new PrivilegedAction() {
                        @Override
                        public Object run() {
                            method.getDispatcher().dispatch(resource, context);
                            return null;
                        }
                    });
                }
                else {
                    method.getDispatcher().dispatch(resource, context);
                }
            }
            catch (RuntimeException e) {
                if (m.rmSelected.isProducesDeclared() && !m.mSelected.isWildcardType() && !m.mSelected.isWildcardSubtype()) {
                    context.getProperties().put("com.sun.jersey.server.impl.uri.rules.HttpMethodRule.Content-Type", m.mSelected);
                }
                throw e;
            }
            final Object contentType = response.getHttpHeaders().getFirst("Content-Type");
            if (contentType == null && m.rmSelected.isProducesDeclared() && !m.mSelected.isWildcardType() && !m.mSelected.isWildcardSubtype()) {
                response.getHttpHeaders().putSingle("Content-Type", m.mSelected);
            }
            return true;
        }
        else {
            if (s == MatchStatus.NO_MATCH_FOR_CONSUME) {
                response.setResponse(Responses.unsupportedMediaType().build());
                return false;
            }
            if (s == MatchStatus.NO_MATCH_FOR_PRODUCE) {
                response.setResponse(Responses.notAcceptable().build());
                return false;
            }
            return true;
        }
    }
    
    public static List<MediaType> getSpecificAcceptableMediaTypes(final List<MediaType> acceptableMediaType, final List<? extends MediaType> priorityMediaTypes) {
        if (priorityMediaTypes != null) {
            for (final MediaType pmt : priorityMediaTypes) {
                for (final MediaType amt : acceptableMediaType) {
                    if (amt.isCompatible(pmt)) {
                        return Collections.singletonList(MediaTypes.mostSpecific(amt, pmt));
                    }
                }
            }
        }
        return acceptableMediaType;
    }
    
    private static final class ResourceMethodListPair
    {
        final List<ResourceMethod> normal;
        final List<ResourceMethod> wildPriority;
        final List<QualitySourceMediaType> priorityMediaTypes;
        
        ResourceMethodListPair(final List<ResourceMethod> normal) {
            this.normal = normal;
            if (this.correctOrder(normal)) {
                this.wildPriority = normal;
            }
            else {
                this.wildPriority = new ArrayList<ResourceMethod>(normal.size());
                int i = 0;
                for (final ResourceMethod method : normal) {
                    if (method.consumesWild()) {
                        this.wildPriority.add(i++, method);
                    }
                    else {
                        this.wildPriority.add(method);
                    }
                }
            }
            final List<QualitySourceMediaType> pmts = new LinkedList<QualitySourceMediaType>();
            for (final ResourceMethod m : normal) {
                for (final MediaType mt : m.getProduces()) {
                    pmts.add(this.get(mt));
                }
            }
            Collections.sort(pmts, MediaTypes.QUALITY_SOURCE_MEDIA_TYPE_COMPARATOR);
            this.priorityMediaTypes = (this.retain(pmts) ? pmts : null);
        }
        
        QualitySourceMediaType get(final MediaType mt) {
            if (mt instanceof QualitySourceMediaType) {
                return (QualitySourceMediaType)mt;
            }
            return new QualitySourceMediaType(mt);
        }
        
        boolean retain(final List<QualitySourceMediaType> pmts) {
            for (final QualitySourceMediaType mt : pmts) {
                if (mt.getQualitySource() != 1000) {
                    return true;
                }
            }
            return false;
        }
        
        boolean correctOrder(final List<ResourceMethod> normal) {
            boolean consumesNonWild = false;
            for (final ResourceMethod method : normal) {
                if (method.consumesWild()) {
                    if (consumesNonWild) {
                        return false;
                    }
                    continue;
                }
                else {
                    consumesNonWild = true;
                }
            }
            return true;
        }
    }
    
    private enum MatchStatus
    {
        MATCH, 
        NO_MATCH_FOR_CONSUME, 
        NO_MATCH_FOR_PRODUCE;
    }
    
    private static class Matcher extends LinkedList<ResourceMethod>
    {
        private MediaType mSelected;
        private ResourceMethod rmSelected;
        
        private Matcher() {
            this.mSelected = null;
            this.rmSelected = null;
        }
        
        private MatchStatus match(final ResourceMethodListPair methods, final MediaType contentType, final List<MediaType> acceptableMediaTypes) {
            List<ResourceMethod> selected;
            if (contentType != null) {
                for (final ResourceMethod method : methods.normal) {
                    if (method.consumes(contentType)) {
                        this.add(method);
                    }
                }
                if (this.isEmpty()) {
                    return MatchStatus.NO_MATCH_FOR_CONSUME;
                }
                selected = this;
            }
            else {
                selected = methods.wildPriority;
            }
            for (final MediaType amt : acceptableMediaTypes) {
                for (final ResourceMethod rm : selected) {
                    for (final MediaType p : rm.getProduces()) {
                        if (p.isCompatible(amt)) {
                            this.mSelected = MediaTypes.mostSpecific(p, amt);
                            this.rmSelected = rm;
                            return MatchStatus.MATCH;
                        }
                    }
                }
            }
            return MatchStatus.NO_MATCH_FOR_PRODUCE;
        }
    }
}
