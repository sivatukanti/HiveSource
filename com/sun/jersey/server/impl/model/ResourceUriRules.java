// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.impl.model;

import com.sun.jersey.server.impl.model.method.ResourceHttpOptionsMethod;
import com.sun.jersey.server.impl.model.method.ResourceHeadWrapperMethod;
import com.sun.jersey.server.impl.template.ViewResourceMethod;
import com.sun.jersey.server.impl.model.method.ResourceMethod;
import com.sun.jersey.server.impl.uri.rules.HttpMethodRule;
import com.sun.jersey.api.model.AbstractResourceMethod;
import com.sun.jersey.server.impl.model.method.ResourceHttpMethod;
import com.sun.jersey.api.model.AbstractSubResourceMethod;
import java.util.HashMap;
import com.sun.jersey.spi.inject.Injectable;
import java.util.Iterator;
import com.sun.jersey.server.impl.uri.rules.RightHandPathRule;
import com.sun.jersey.server.impl.uri.rules.SubLocatorRule;
import java.lang.reflect.AccessibleObject;
import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.api.uri.UriTemplate;
import com.sun.jersey.server.impl.uri.PathTemplate;
import com.sun.jersey.api.model.AbstractSubResourceLocator;
import com.sun.jersey.spi.container.ResourceFilter;
import com.sun.jersey.core.header.QualitySourceMediaType;
import java.util.List;
import com.sun.jersey.server.impl.uri.rules.CombiningMatchingPatterns;
import java.util.Arrays;
import com.sun.jersey.server.impl.uri.rules.SequentialMatchingPatterns;
import com.sun.jersey.server.impl.uri.rules.TerminatingRule;
import com.sun.jersey.api.uri.UriPattern;
import com.sun.jersey.spi.inject.InjectableProviderContext;
import com.sun.jersey.core.spi.component.ComponentInjector;
import com.sun.jersey.server.impl.template.ViewableRule;
import com.sun.jersey.api.model.AbstractMethod;
import com.sun.jersey.api.model.AbstractImplicitViewMethod;
import com.sun.jersey.server.impl.uri.rules.PatternRulePair;
import java.util.ArrayList;
import java.util.Map;
import com.sun.jersey.server.impl.uri.rules.UriRulesFactory;
import com.sun.jersey.spi.inject.Errors;
import com.sun.jersey.server.impl.uri.PathPattern;
import com.sun.jersey.core.header.MediaTypes;
import com.sun.jersey.api.view.ImplicitProduces;
import com.sun.jersey.api.model.AbstractResource;
import com.sun.jersey.spi.monitoring.DispatchingListener;
import com.sun.jersey.server.impl.wadl.WadlFactory;
import com.sun.jersey.server.impl.container.filter.FilterFactory;
import com.sun.jersey.server.impl.inject.ServerInjectableProviderContext;
import com.sun.jersey.spi.container.ResourceMethodDispatchProvider;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.spi.uri.rules.UriRule;
import com.sun.jersey.spi.uri.rules.UriRules;

public final class ResourceUriRules
{
    private final UriRules<UriRule> rules;
    private final ResourceConfig resourceConfig;
    private final ResourceMethodDispatchProvider dp;
    private final ServerInjectableProviderContext injectableContext;
    private final FilterFactory ff;
    private final WadlFactory wadlFactory;
    private final DispatchingListener dispatchingListener;
    
    public ResourceUriRules(final ResourceConfig resourceConfig, final ResourceMethodDispatchProvider dp, final ServerInjectableProviderContext injectableContext, final FilterFactory ff, final WadlFactory wadlFactory, final DispatchingListener dispatchingListener, final AbstractResource resource) {
        this.resourceConfig = resourceConfig;
        this.dp = dp;
        this.injectableContext = injectableContext;
        this.ff = ff;
        this.wadlFactory = wadlFactory;
        this.dispatchingListener = dispatchingListener;
        final boolean implicitViewables = resourceConfig.getFeature("com.sun.jersey.config.feature.ImplicitViewables");
        List<QualitySourceMediaType> implictProduces = null;
        if (implicitViewables) {
            final ImplicitProduces ip = resource.getAnnotation(ImplicitProduces.class);
            if (ip != null && ip.value() != null && ip.value().length > 0) {
                implictProduces = MediaTypes.createQualitySourceMediaTypes(ip.value());
            }
        }
        final RulesMap<UriRule> rulesMap = new RulesMap<UriRule>();
        this.processSubResourceLocators(resource, rulesMap);
        this.processSubResourceMethods(resource, implictProduces, rulesMap);
        this.processMethods(resource, implictProduces, rulesMap);
        rulesMap.processConflicts(new RulesMap.ConflictClosure() {
            @Override
            public void onConflict(final PathPattern p1, final PathPattern p2) {
                Errors.error(String.format("Conflicting URI templates. The URI templates %s and %s for sub-resource methods and/or sub-resource locators of resource class %s transform to the same regular expression %s", p1.getTemplate().getTemplate(), p2.getTemplate().getTemplate(), resource.getResourceClass().getName(), p1));
            }
        });
        final UriRules<UriRule> atomicRules = UriRulesFactory.create((Map<PathPattern, UriRule>)rulesMap);
        final List<PatternRulePair<UriRule>> patterns = new ArrayList<PatternRulePair<UriRule>>();
        if (resourceConfig.getFeature("com.sun.jersey.config.feature.ImplicitViewables")) {
            final AbstractImplicitViewMethod method = new AbstractImplicitViewMethod(resource);
            final List<ResourceFilter> resourceFilters = ff.getResourceFilters(method);
            final ViewableRule r = new ViewableRule(implictProduces, FilterFactory.getRequestFilters(resourceFilters), FilterFactory.getResponseFilters(resourceFilters));
            final ComponentInjector<ViewableRule> ci = new ComponentInjector<ViewableRule>(injectableContext, ViewableRule.class);
            ci.inject(r);
            patterns.add(new PatternRulePair<UriRule>(new UriPattern("/([^/]+)"), r));
            patterns.add(new PatternRulePair<UriRule>(UriPattern.EMPTY, r));
        }
        patterns.add(new PatternRulePair<UriRule>(new UriPattern(".*"), new TerminatingRule()));
        patterns.add(new PatternRulePair<UriRule>(UriPattern.EMPTY, new TerminatingRule()));
        final UriRules<UriRule> sequentialRules = new SequentialMatchingPatterns<UriRule>(patterns);
        final UriRules<UriRule> combiningRules = new CombiningMatchingPatterns<UriRule>((List<UriRules<UriRule>>)Arrays.asList(atomicRules, sequentialRules));
        this.rules = combiningRules;
    }
    
    public UriRules<UriRule> getRules() {
        return this.rules;
    }
    
    private void processSubResourceLocators(final AbstractResource resource, final RulesMap<UriRule> rulesMap) {
        for (final AbstractSubResourceLocator locator : resource.getSubResourceLocators()) {
            PathPattern p = null;
            try {
                p = new PathPattern(new PathTemplate(locator.getPath().getValue()));
            }
            catch (IllegalArgumentException ex) {
                Errors.error(String.format("Illegal URI template for sub-resource locator %s: %s", locator.getMethod(), ex.getMessage()));
                continue;
            }
            final PathPattern conflict = rulesMap.hasConflict(p);
            if (conflict != null) {
                Errors.error(String.format("Conflicting URI templates. The URI template %s for sub-resource locator %s and the URI template %s transform to the same regular expression %s", p.getTemplate().getTemplate(), locator.getMethod(), conflict.getTemplate().getTemplate(), p));
            }
            else {
                final List<Injectable> is = this.injectableContext.getInjectable(locator.getMethod(), locator.getParameters(), ComponentScope.PerRequest);
                if (is.contains(null)) {
                    for (int i = 0; i < is.size(); ++i) {
                        if (is.get(i) == null) {
                            Errors.missingDependency(locator.getMethod(), i);
                        }
                    }
                }
                final List<ResourceFilter> resourceFilters = this.ff.getResourceFilters(locator);
                final UriRule r = new SubLocatorRule(p.getTemplate(), is, FilterFactory.getRequestFilters(resourceFilters), FilterFactory.getResponseFilters(resourceFilters), this.dispatchingListener, locator);
                rulesMap.put(p, new RightHandPathRule(this.resourceConfig.getFeature("com.sun.jersey.config.feature.Redirect"), p.getTemplate().endsWithSlash(), r));
            }
        }
    }
    
    private void processSubResourceMethods(final AbstractResource resource, final List<QualitySourceMediaType> implictProduces, final RulesMap<UriRule> rulesMap) {
        final Map<PathPattern, ResourceMethodMap> patternMethodMap = new HashMap<PathPattern, ResourceMethodMap>();
        for (final AbstractSubResourceMethod method : resource.getSubResourceMethods()) {
            PathPattern p;
            try {
                p = new PathPattern(new PathTemplate(method.getPath().getValue()), "(/)?");
            }
            catch (IllegalArgumentException ex) {
                Errors.error(String.format("Illegal URI template for sub-resource method %s: %s", method.getMethod(), ex.getMessage()));
                continue;
            }
            final ResourceMethod rm = new ResourceHttpMethod(this.dp, this.ff, p.getTemplate(), method);
            ResourceMethodMap rmm = patternMethodMap.get(p);
            if (rmm == null) {
                rmm = new ResourceMethodMap();
                patternMethodMap.put(p, rmm);
            }
            if (this.isValidResourceMethod(rm, rmm)) {
                rmm.put(rm);
            }
            rmm.put(rm);
        }
        for (final Map.Entry<PathPattern, ResourceMethodMap> e : patternMethodMap.entrySet()) {
            this.addImplicitMethod(implictProduces, e.getValue());
            final PathPattern p = e.getKey();
            final ResourceMethodMap rmm2 = e.getValue();
            this.processHead(rmm2);
            this.processOptions(rmm2, resource, p);
            rmm2.sort();
            rulesMap.put(p, new RightHandPathRule(this.resourceConfig.getFeature("com.sun.jersey.config.feature.Redirect"), p.getTemplate().endsWithSlash(), new HttpMethodRule(rmm2, true, this.dispatchingListener)));
        }
    }
    
    private void processMethods(final AbstractResource resource, final List<QualitySourceMediaType> implictProduces, final RulesMap<UriRule> rulesMap) {
        final ResourceMethodMap rmm = new ResourceMethodMap();
        for (final AbstractResourceMethod resourceMethod : resource.getResourceMethods()) {
            final ResourceMethod rm = new ResourceHttpMethod(this.dp, this.ff, resourceMethod);
            if (this.isValidResourceMethod(rm, rmm)) {
                rmm.put(rm);
            }
        }
        this.addImplicitMethod(implictProduces, rmm);
        this.processHead(rmm);
        this.processOptions(rmm, resource, null);
        rmm.sort();
        if (!rmm.isEmpty()) {
            rulesMap.put(PathPattern.EMPTY_PATH, new HttpMethodRule(rmm, this.dispatchingListener));
        }
    }
    
    private void addImplicitMethod(final List<QualitySourceMediaType> implictProduces, final ResourceMethodMap rmm) {
        if (implictProduces != null) {
            final List<ResourceMethod> getList = ((HashMap<K, List<ResourceMethod>>)rmm).get("GET");
            if (getList != null && !getList.isEmpty()) {
                rmm.put(new ViewResourceMethod(implictProduces));
            }
        }
    }
    
    private boolean isValidResourceMethod(final ResourceMethod rm, final ResourceMethodMap rmm) {
        final List<ResourceMethod> rml = ((HashMap<K, List<ResourceMethod>>)rmm).get(rm.getHttpMethod());
        if (rml != null) {
            boolean conflict = false;
            ResourceMethod erm = null;
            for (int i = 0; i < rml.size() && !conflict; conflict = (MediaTypes.intersects(rm.getConsumes(), erm.getConsumes()) && MediaTypes.intersects(rm.getProduces(), erm.getProduces())), ++i) {
                erm = rml.get(i);
            }
            if (conflict) {
                if (rm.getAbstractResourceMethod().hasEntity()) {
                    Errors.error(String.format("Consuming media type conflict. The resource methods %s and %s can consume the same media type", rm.getAbstractResourceMethod().getMethod(), erm.getAbstractResourceMethod().getMethod()));
                }
                else {
                    Errors.error(String.format("Producing media type conflict. The resource methods %s and %s can produce the same media type", rm.getAbstractResourceMethod().getMethod(), erm.getAbstractResourceMethod().getMethod()));
                }
            }
            if (conflict) {
                return false;
            }
        }
        return true;
    }
    
    private void processHead(final ResourceMethodMap methodMap) {
        final List<ResourceMethod> getList = ((HashMap<K, List<ResourceMethod>>)methodMap).get("GET");
        if (getList == null || getList.isEmpty()) {
            return;
        }
        List<ResourceMethod> headList = ((HashMap<K, List<ResourceMethod>>)methodMap).get("HEAD");
        if (headList == null) {
            headList = new ArrayList<ResourceMethod>();
        }
        for (final ResourceMethod getMethod : getList) {
            if (!this.containsMediaOfMethod(headList, getMethod)) {
                final ResourceMethod headMethod = new ResourceHeadWrapperMethod(getMethod);
                methodMap.put(headMethod);
                headList = ((HashMap<K, List<ResourceMethod>>)methodMap).get("HEAD");
            }
        }
    }
    
    private boolean containsMediaOfMethod(final List<ResourceMethod> methods, final ResourceMethod method) {
        for (final ResourceMethod m : methods) {
            if (method.mediaEquals(m)) {
                return true;
            }
        }
        return false;
    }
    
    private void processOptions(final ResourceMethodMap methodMap, final AbstractResource resource, final PathPattern p) {
        final List<ResourceMethod> l = ((HashMap<K, List<ResourceMethod>>)methodMap).get("OPTIONS");
        if (l != null) {
            return;
        }
        ResourceMethod optionsMethod = this.wadlFactory.createWadlOptionsMethod(methodMap, resource, p);
        if (optionsMethod == null) {
            optionsMethod = new ResourceHttpOptionsMethod(methodMap);
        }
        methodMap.put(optionsMethod);
    }
}
