// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.impl.application;

import com.sun.jersey.server.impl.uri.rules.ResourceObjectRule;
import com.sun.jersey.server.impl.uri.rules.RightHandPathRule;
import com.sun.jersey.server.impl.uri.rules.ResourceClassRule;
import com.sun.jersey.server.impl.wadl.WadlResource;
import com.sun.jersey.api.uri.UriTemplate;
import com.sun.jersey.server.impl.uri.PathTemplate;
import java.util.Iterator;
import java.util.Set;
import com.sun.jersey.spi.inject.Errors;
import com.sun.jersey.server.impl.uri.PathPattern;
import com.sun.jersey.api.model.AbstractResource;
import java.util.Map;
import com.sun.jersey.spi.inject.InjectableProviderContext;
import com.sun.jersey.core.spi.component.ComponentInjector;
import com.sun.jersey.api.container.ContainerException;
import com.sun.jersey.impl.ImplMessages;
import com.sun.jersey.core.spi.factory.InjectableProviderFactory;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.server.impl.wadl.WadlFactory;
import com.sun.jersey.spi.uri.rules.UriRule;
import com.sun.jersey.server.impl.model.RulesMap;
import java.util.logging.Logger;

public class RootResourceUriRules
{
    private static final Logger LOGGER;
    private final RulesMap<UriRule> rules;
    private final WebApplicationImpl wa;
    private final WadlFactory wadlFactory;
    private final ResourceConfig resourceConfig;
    private final InjectableProviderFactory injectableFactory;
    
    public RootResourceUriRules(final WebApplicationImpl wa, final ResourceConfig resourceConfig, final WadlFactory wadlFactory, final InjectableProviderFactory injectableFactory) {
        this.rules = new RulesMap<UriRule>();
        this.wa = wa;
        this.resourceConfig = resourceConfig;
        this.wadlFactory = wadlFactory;
        this.injectableFactory = injectableFactory;
        final Set<Class<?>> classes = resourceConfig.getRootResourceClasses();
        final Set<Object> singletons = resourceConfig.getRootResourceSingletons();
        if (classes.isEmpty() && singletons.isEmpty() && resourceConfig.getExplicitRootResources().isEmpty()) {
            RootResourceUriRules.LOGGER.severe(ImplMessages.NO_ROOT_RES_IN_RES_CFG());
            throw new ContainerException(ImplMessages.NO_ROOT_RES_IN_RES_CFG());
        }
        final Set<AbstractResource> rootResourcesSet = wa.getAbstractRootResources();
        final Map<String, AbstractResource> explicitRootResources = wa.getExplicitAbstractRootResources();
        this.initWadl(rootResourcesSet);
        for (final Object o : singletons) {
            final AbstractResource ar = wa.getAbstractResource(o);
            wa.initiateResource(ar, o);
            final ComponentInjector ci = new ComponentInjector(injectableFactory, (Class<T>)o.getClass());
            ci.inject(o);
            this.addRule(ar.getPath().getValue(), o);
        }
        for (final Class<?> c : classes) {
            final AbstractResource ar = wa.getAbstractResource(c);
            wa.initiateResource(ar);
            this.addRule(ar.getPath().getValue(), c);
        }
        for (final Map.Entry<String, Object> e : resourceConfig.getExplicitRootResources().entrySet()) {
            final String path = e.getKey();
            final Object o2 = e.getValue();
            if (o2 instanceof Class) {
                final Class c2 = (Class)o2;
                wa.initiateResource(explicitRootResources.get(path));
                this.addRule(path, c2);
            }
            else {
                wa.initiateResource(explicitRootResources.get(path), o2);
                final ComponentInjector ci2 = new ComponentInjector(injectableFactory, (Class<T>)o2.getClass());
                ci2.inject(o2);
                this.addRule(path, o2);
            }
        }
        this.rules.processConflicts(new RulesMap.ConflictClosure() {
            @Override
            public void onConflict(final PathPattern p1, final PathPattern p2) {
                Errors.error(String.format("Conflicting URI templates. The URI templates %s and %s for root resource classes transform to the same regular expression %s", p1.getTemplate().getTemplate(), p2.getTemplate().getTemplate(), p1));
            }
        });
        this.initWadlResource();
    }
    
    private void initWadl(final Set<AbstractResource> rootResources) {
        if (!this.wadlFactory.isSupported()) {
            return;
        }
        this.wadlFactory.init(this.injectableFactory, rootResources);
    }
    
    private void initWadlResource() {
        if (!this.wadlFactory.isSupported()) {
            return;
        }
        final PathPattern p = new PathPattern(new PathTemplate("application.wadl"));
        if (this.rules.containsKey(p)) {
            return;
        }
        this.wa.initiateResource(WadlResource.class);
        this.rules.put(p, new RightHandPathRule(this.resourceConfig.getFeature("com.sun.jersey.config.feature.Redirect"), p.getTemplate().endsWithSlash(), new ResourceClassRule(p.getTemplate(), WadlResource.class)));
    }
    
    private void addRule(final String path, final Class c) {
        final PathPattern p = this.getPattern(path, c);
        if (this.isPatternValid(p, c)) {
            this.rules.put(p, new RightHandPathRule(this.resourceConfig.getFeature("com.sun.jersey.config.feature.Redirect"), p.getTemplate().endsWithSlash(), new ResourceClassRule(p.getTemplate(), c)));
        }
    }
    
    private void addRule(final String path, final Object o) {
        final PathPattern p = this.getPattern(path, o.getClass());
        if (this.isPatternValid(p, o.getClass())) {
            this.rules.put(p, new RightHandPathRule(this.resourceConfig.getFeature("com.sun.jersey.config.feature.Redirect"), p.getTemplate().endsWithSlash(), new ResourceObjectRule(p.getTemplate(), o)));
        }
    }
    
    private PathPattern getPattern(final String path, final Class c) {
        PathPattern p = null;
        try {
            p = new PathPattern(new PathTemplate(path));
        }
        catch (IllegalArgumentException ex) {
            Errors.error("Illegal URI template for root resource class " + c.getName() + ": " + ex.getMessage());
        }
        return p;
    }
    
    private boolean isPatternValid(final PathPattern p, final Class c) {
        if (p == null) {
            return false;
        }
        final PathPattern conflict = this.rules.hasConflict(p);
        if (conflict != null) {
            Errors.error(String.format("Conflicting URI templates. The URI template %s for root resource class %s and the URI template %s transform to the same regular expression %s", p.getTemplate().getTemplate(), c.getName(), conflict.getTemplate().getTemplate(), p));
            return false;
        }
        return true;
    }
    
    public RulesMap<UriRule> getRules() {
        return this.rules;
    }
    
    static {
        LOGGER = Logger.getLogger(RootResourceUriRules.class.getName());
    }
}
