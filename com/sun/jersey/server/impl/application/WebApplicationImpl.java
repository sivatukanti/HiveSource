// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.impl.application;

import com.sun.jersey.api.model.AbstractResourceMethod;
import com.sun.jersey.api.model.AbstractSubResourceLocator;
import com.sun.jersey.server.spi.component.ResourceComponentInjector;
import java.security.AccessController;
import java.lang.reflect.Proxy;
import java.security.PrivilegedAction;
import com.sun.jersey.api.model.AbstractResourceModelListener;
import java.util.Collections;
import java.util.HashSet;
import com.sun.jersey.api.NotFoundException;
import com.sun.jersey.spi.uri.rules.UriRuleContext;
import com.sun.jersey.spi.container.ContainerResponseFilter;
import com.sun.jersey.api.container.MappableContainerException;
import com.sun.jersey.server.impl.resource.PerRequestFactory;
import java.io.IOException;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseWriter;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ExceptionMapperContext;
import com.sun.jersey.server.impl.model.RulesMap;
import com.sun.jersey.core.spi.component.ioc.IoCComponentProcessorFactory;
import com.sun.jersey.server.impl.monitoring.MonitoringProviderFactory;
import com.sun.jersey.server.impl.uri.PathPattern;
import com.sun.jersey.spi.container.WebApplicationListener;
import com.sun.jersey.api.container.filter.UriConnegFilter;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import com.sun.jersey.spi.inject.SingletonTypeInjectableProvider;
import com.sun.jersey.server.impl.wadl.WadlApplicationContextInjectionProxy;
import com.sun.jersey.server.impl.model.parameter.FormParamInjectableProvider;
import com.sun.jersey.server.impl.model.parameter.QueryParamInjectableProvider;
import com.sun.jersey.server.impl.model.parameter.PathParamInjectableProvider;
import com.sun.jersey.server.impl.model.parameter.MatrixParamInjectableProvider;
import com.sun.jersey.server.impl.model.parameter.HttpContextInjectableProvider;
import com.sun.jersey.server.impl.model.parameter.HeaderParamInjectableProvider;
import com.sun.jersey.server.impl.model.parameter.CookieParamInjectableProvider;
import com.sun.jersey.server.impl.model.parameter.multivalued.MultivaluedParameterExtractorProvider;
import com.sun.jersey.server.impl.model.parameter.multivalued.MultivaluedParameterExtractorFactory;
import com.sun.jersey.spi.StringReaderWorkers;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.core.MediaType;
import com.sun.jersey.spi.MessageBodyWorkers;
import com.sun.jersey.core.spi.factory.ContextResolverFactory;
import com.sun.jersey.server.impl.template.TemplateFactory;
import com.sun.jersey.core.spi.factory.InjectableProviderFactory;
import com.sun.jersey.api.core.ResourceConfigurator;
import com.sun.jersey.core.reflection.ReflectionHelper;
import com.sun.jersey.api.core.ParentRef;
import com.sun.jersey.spi.container.ResourceMethodCustomInvokerDispatchFactory;
import com.sun.jersey.spi.inject.ConstrainedToType;
import com.sun.jersey.core.spi.component.ProviderServices;
import com.sun.jersey.spi.inject.ServerSide;
import javax.ws.rs.WebApplicationException;
import java.net.URI;
import com.sun.jersey.core.spi.component.ioc.IoCComponentProcessorFactoryInitializer;
import com.sun.jersey.server.impl.component.IoCResourceFactory;
import com.sun.jersey.core.spi.component.ioc.IoCProviderFactory;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Arrays;
import java.util.logging.Level;
import com.sun.jersey.spi.service.ServiceFinder;
import com.sun.jersey.server.impl.BuildId;
import com.sun.jersey.api.container.ContainerException;
import com.sun.jersey.impl.ImplMessages;
import com.sun.research.ws.wadl.Application;
import com.sun.jersey.server.wadl.WadlApplicationContext;
import javax.ws.rs.Produces;
import javax.xml.bind.JAXBContext;
import javax.ws.rs.ext.ContextResolver;
import com.sun.jersey.server.impl.modelapi.annotation.IntrospectionModeller;
import java.util.Iterator;
import com.sun.jersey.spi.monitoring.DispatchingListener;
import com.sun.jersey.api.model.ResourceModelIssue;
import com.sun.jersey.api.model.AbstractModelComponent;
import com.sun.jersey.server.impl.modelapi.validation.BasicValidator;
import com.sun.jersey.spi.inject.Errors;
import com.sun.jersey.server.impl.model.ResourceUriRules;
import com.sun.jersey.core.util.FeaturesAndProperties;
import com.sun.jersey.api.core.InjectParam;
import com.sun.jersey.spi.inject.Inject;
import java.lang.reflect.ParameterizedType;
import java.lang.annotation.Annotation;
import com.sun.jersey.spi.inject.Injectable;
import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.ComponentScope;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.Request;
import com.sun.jersey.api.core.ExtendedUriInfo;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.HttpHeaders;
import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.server.impl.inject.ServerInjectableProviderContext;
import com.sun.jersey.spi.inject.InjectableProvider;
import java.lang.reflect.Type;
import com.sun.jersey.spi.inject.InjectableProviderContext;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationHandler;
import java.util.concurrent.ConcurrentHashMap;
import java.util.HashMap;
import com.sun.jersey.core.spi.component.ioc.IoCComponentProcessor;
import com.sun.jersey.spi.monitoring.ResponseListener;
import com.sun.jersey.spi.monitoring.RequestListener;
import com.sun.jersey.server.impl.wadl.WadlFactory;
import com.sun.jersey.server.impl.container.filter.FilterFactory;
import com.sun.jersey.api.model.AbstractResourceModelContext;
import java.util.Set;
import com.sun.jersey.api.core.ResourceContext;
import com.sun.jersey.spi.container.ResourceMethodDispatchProvider;
import com.sun.jersey.spi.template.TemplateContext;
import com.sun.jersey.server.impl.model.parameter.multivalued.StringReaderFactory;
import com.sun.jersey.core.spi.factory.MessageBodyFactory;
import javax.ws.rs.ext.Providers;
import java.util.List;
import com.sun.jersey.core.spi.component.ioc.IoCComponentProviderFactory;
import com.sun.jersey.server.impl.component.ResourceFactory;
import com.sun.jersey.core.spi.component.ProviderFactory;
import com.sun.jersey.server.impl.inject.ServerInjectableProviderFactory;
import com.sun.jersey.server.impl.uri.rules.RootResourceClassesRule;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.server.impl.ThreadLocalHttpContext;
import com.sun.jersey.server.spi.component.ResourceComponentProvider;
import com.sun.jersey.spi.uri.rules.UriRule;
import com.sun.jersey.spi.uri.rules.UriRules;
import java.util.concurrent.ConcurrentMap;
import com.sun.jersey.api.model.AbstractResource;
import java.util.Map;
import java.util.logging.Logger;
import com.sun.jersey.spi.container.WebApplication;

public final class WebApplicationImpl implements WebApplication
{
    private static final Logger LOGGER;
    private final Map<Class, AbstractResource> abstractResourceMap;
    private final ConcurrentMap<Class, UriRules<UriRule>> rulesMap;
    private final ConcurrentMap<Class, ResourceComponentProvider> providerMap;
    private final ConcurrentMap<Class, ResourceComponentProvider> singletonMap;
    private final ConcurrentMap<ClassAnnotationKey, ResourceComponentProvider> providerWithAnnotationKeyMap;
    private final ThreadLocalHttpContext context;
    private final CloseableServiceFactory closeableFactory;
    private boolean initiated;
    private ResourceConfig resourceConfig;
    private RootResourceClassesRule rootsRule;
    private ServerInjectableProviderFactory injectableFactory;
    private ProviderFactory cpFactory;
    private ResourceFactory rcpFactory;
    private IoCComponentProviderFactory provider;
    private List<IoCComponentProviderFactory> providerFactories;
    private Providers providers;
    private MessageBodyFactory bodyFactory;
    private StringReaderFactory stringReaderFactory;
    private TemplateContext templateContext;
    private ExceptionMapperFactory exceptionFactory;
    private ResourceMethodDispatchProvider dispatcherFactory;
    private ResourceContext resourceContext;
    private Set<AbstractResource> abstractRootResources;
    private Map<String, AbstractResource> explicitAbstractRootResources;
    private final AbstractResourceModelContext armContext;
    private FilterFactory filterFactory;
    private WadlFactory wadlFactory;
    private boolean isTraceEnabled;
    private RequestListener requestListener;
    private DispatchingListenerProxy dispatchingListener;
    private ResponseListener responseListener;
    private static final IoCComponentProcessor NULL_COMPONENT_PROCESSOR;
    
    public WebApplicationImpl() {
        this.abstractResourceMap = new HashMap<Class, AbstractResource>();
        this.rulesMap = new ConcurrentHashMap<Class, UriRules<UriRule>>();
        this.providerMap = new ConcurrentHashMap<Class, ResourceComponentProvider>();
        this.singletonMap = new ConcurrentHashMap<Class, ResourceComponentProvider>();
        this.providerWithAnnotationKeyMap = new ConcurrentHashMap<ClassAnnotationKey, ResourceComponentProvider>();
        this.armContext = new AbstractResourceModelContext() {
            @Override
            public Set<AbstractResource> getAbstractRootResources() {
                return WebApplicationImpl.this.abstractRootResources;
            }
        };
        this.context = new ThreadLocalHttpContext();
        final InvocationHandler requestHandler = new InvocationHandler() {
            @Override
            public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
                try {
                    return method.invoke(WebApplicationImpl.this.context.getRequest(), args);
                }
                catch (IllegalAccessException ex) {
                    throw new IllegalStateException(ex);
                }
                catch (InvocationTargetException ex2) {
                    throw ex2.getTargetException();
                }
            }
        };
        final InvocationHandler uriInfoHandler = new InvocationHandler() {
            @Override
            public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
                try {
                    return method.invoke(WebApplicationImpl.this.context.getUriInfo(), args);
                }
                catch (IllegalAccessException ex) {
                    throw new IllegalStateException(ex);
                }
                catch (InvocationTargetException ex2) {
                    throw ex2.getTargetException();
                }
            }
        };
        (this.injectableFactory = new ServerInjectableProviderFactory()).add(new ContextInjectableProvider(InjectableProviderContext.class, this.injectableFactory));
        this.injectableFactory.add(new ContextInjectableProvider(ServerInjectableProviderContext.class, this.injectableFactory));
        final Map<Type, Object> m = new HashMap<Type, Object>();
        m.put(HttpContext.class, this.context);
        m.put(HttpHeaders.class, this.createProxy(HttpHeaders.class, requestHandler));
        m.put(UriInfo.class, this.createProxy(UriInfo.class, uriInfoHandler));
        m.put(ExtendedUriInfo.class, this.createProxy(ExtendedUriInfo.class, uriInfoHandler));
        m.put(Request.class, this.createProxy(Request.class, requestHandler));
        m.put(SecurityContext.class, this.createProxy(SecurityContext.class, requestHandler));
        this.injectableFactory.add(new InjectableProvider<Context, Type>() {
            @Override
            public ComponentScope getScope() {
                return ComponentScope.Singleton;
            }
            
            @Override
            public Injectable getInjectable(final ComponentContext ic, final Context a, final Type c) {
                final Object o = m.get(c);
                if (o != null) {
                    return new Injectable() {
                        @Override
                        public Object getValue() {
                            return o;
                        }
                    };
                }
                return null;
            }
        });
        this.injectableFactory.add(new InjectableProvider<Context, Type>() {
            @Override
            public ComponentScope getScope() {
                return ComponentScope.Singleton;
            }
            
            @Override
            public Injectable<Injectable> getInjectable(final ComponentContext ic, final Context a, final Type c) {
                if (c instanceof ParameterizedType) {
                    final ParameterizedType pt = (ParameterizedType)c;
                    if (pt.getRawType() == Injectable.class && pt.getActualTypeArguments().length == 1) {
                        final Injectable<?> i = (Injectable<?>)WebApplicationImpl.this.injectableFactory.getInjectable(a.annotationType(), ic, a, pt.getActualTypeArguments()[0], ComponentScope.PERREQUEST_UNDEFINED_SINGLETON);
                        if (i == null) {
                            return null;
                        }
                        return new Injectable<Injectable>() {
                            @Override
                            public Injectable getValue() {
                                return i;
                            }
                        };
                    }
                }
                return null;
            }
        });
        this.injectableFactory.add(new InjectableProvider<Inject, Type>() {
            @Override
            public ComponentScope getScope() {
                return ComponentScope.Singleton;
            }
            
            @Override
            public Injectable<Injectable> getInjectable(final ComponentContext ic, final Inject a, final Type c) {
                if (c instanceof ParameterizedType) {
                    final ParameterizedType pt = (ParameterizedType)c;
                    if (pt.getRawType() == Injectable.class && pt.getActualTypeArguments().length == 1) {
                        final Injectable<?> i = (Injectable<?>)WebApplicationImpl.this.injectableFactory.getInjectable(a.annotationType(), ic, a, pt.getActualTypeArguments()[0], ComponentScope.PERREQUEST_UNDEFINED_SINGLETON);
                        if (i == null) {
                            return null;
                        }
                        return new Injectable<Injectable>() {
                            @Override
                            public Injectable getValue() {
                                return i;
                            }
                        };
                    }
                }
                return null;
            }
        });
        this.injectableFactory.add(new InjectableProvider<InjectParam, Type>() {
            @Override
            public ComponentScope getScope() {
                return ComponentScope.Singleton;
            }
            
            @Override
            public Injectable<Injectable> getInjectable(final ComponentContext ic, final InjectParam a, final Type c) {
                if (c instanceof ParameterizedType) {
                    final ParameterizedType pt = (ParameterizedType)c;
                    if (pt.getRawType() == Injectable.class && pt.getActualTypeArguments().length == 1) {
                        final Injectable<?> i = (Injectable<?>)WebApplicationImpl.this.injectableFactory.getInjectable(a.annotationType(), ic, a, pt.getActualTypeArguments()[0], ComponentScope.PERREQUEST_UNDEFINED_SINGLETON);
                        if (i == null) {
                            return null;
                        }
                        return new Injectable<Injectable>() {
                            @Override
                            public Injectable getValue() {
                                return i;
                            }
                        };
                    }
                }
                return null;
            }
        });
        this.closeableFactory = new CloseableServiceFactory(this.context);
        this.injectableFactory.add(this.closeableFactory);
    }
    
    @Override
    public FeaturesAndProperties getFeaturesAndProperties() {
        return this.resourceConfig;
    }
    
    @Override
    public WebApplication clone() {
        final WebApplicationImpl wa = new WebApplicationImpl();
        wa.initiate(this.resourceConfig, this.provider);
        return wa;
    }
    
    UriRules<UriRule> getUriRules(final Class c) {
        assert c != null;
        UriRules<UriRule> r = this.rulesMap.get(c);
        if (r != null) {
            return r;
        }
        synchronized (this.abstractResourceMap) {
            r = this.rulesMap.get(c);
            if (r != null) {
                return r;
            }
            r = Errors.processWithErrors((Errors.Closure<ResourceUriRules>)new Errors.Closure<ResourceUriRules>() {
                @Override
                public ResourceUriRules f() {
                    return WebApplicationImpl.this.newResourceUriRules(WebApplicationImpl.this.getAbstractResource(c));
                }
            }).getRules();
            this.rulesMap.put(c, r);
        }
        return r;
    }
    
    ResourceComponentProvider getResourceComponentProvider(final Class c) {
        return this.getOrCreateResourceComponentProvider(c, false);
    }
    
    ResourceComponentProvider getOrCreateResourceComponentProvider(final Class c, final boolean create) {
        assert c != null;
        ResourceComponentProvider rcp = this.providerMap.get(c);
        if (rcp != null) {
            return rcp;
        }
        if (!create && this.singletonMap.containsKey(c)) {
            return this.singletonMap.get(c);
        }
        synchronized (this.abstractResourceMap) {
            rcp = this.providerMap.get(c);
            if (rcp != null) {
                return rcp;
            }
            final ResourceComponentProvider _rcp;
            rcp = (_rcp = this.rcpFactory.getComponentProvider(null, c));
            Errors.processWithErrors((Errors.Closure<Object>)new Errors.Closure<Void>() {
                @Override
                public Void f() {
                    _rcp.init(WebApplicationImpl.this.getAbstractResource(c));
                    return null;
                }
            });
            this.providerMap.put(c, rcp);
        }
        return rcp;
    }
    
    ResourceComponentProvider getResourceComponentProvider(final ComponentContext cc, final Class c) {
        assert c != null;
        if (cc == null || cc.getAnnotations().length == 0) {
            return this.getOrCreateResourceComponentProvider(c, true);
        }
        if (cc.getAnnotations().length == 1) {
            final Annotation a = cc.getAnnotations()[0];
            if (a.annotationType() == Inject.class) {
                final Inject i = Inject.class.cast(a);
                final String value = (i.value() != null) ? i.value().trim() : "";
                if (value.isEmpty()) {
                    return this.getOrCreateResourceComponentProvider(c, true);
                }
            }
            else if (a.annotationType() == InjectParam.class) {
                final InjectParam j = InjectParam.class.cast(a);
                final String value = (j.value() != null) ? j.value().trim() : "";
                if (value.isEmpty()) {
                    return this.getOrCreateResourceComponentProvider(c, true);
                }
            }
        }
        final ClassAnnotationKey cak = new ClassAnnotationKey(c, cc.getAnnotations());
        ResourceComponentProvider rcp = this.providerWithAnnotationKeyMap.get(cak);
        if (rcp != null) {
            return rcp;
        }
        synchronized (this.abstractResourceMap) {
            rcp = this.providerWithAnnotationKeyMap.get(cak);
            if (rcp != null) {
                return rcp;
            }
            final ResourceComponentProvider _rcp;
            rcp = (_rcp = this.rcpFactory.getComponentProvider(cc, c));
            Errors.processWithErrors((Errors.Closure<Object>)new Errors.Closure<Void>() {
                @Override
                public Void f() {
                    _rcp.init(WebApplicationImpl.this.getAbstractResource(c));
                    return null;
                }
            });
            this.providerWithAnnotationKeyMap.put(cak, rcp);
        }
        return rcp;
    }
    
    void initiateResource(final AbstractResource ar) {
        this.initiateResource(ar.getResourceClass());
    }
    
    void initiateResource(final Class c) {
        this.getUriRules(c);
        this.getOrCreateResourceComponentProvider(c, true);
    }
    
    void initiateResource(final AbstractResource ar, final Object resource) {
        final Class c = ar.getResourceClass();
        this.getUriRules(c);
        if (!this.singletonMap.containsKey(c)) {
            this.singletonMap.put(c, new ResourceComponentProvider() {
                @Override
                public void init(final AbstractResource abstractResource) {
                }
                
                @Override
                public ComponentScope getScope() {
                    return ComponentScope.Singleton;
                }
                
                @Override
                public Object getInstance(final HttpContext hc) {
                    return this.getInstance();
                }
                
                @Override
                public void destroy() {
                }
                
                @Override
                public Object getInstance() {
                    return resource;
                }
            });
        }
    }
    
    Set<AbstractResource> getAbstractRootResources() {
        return this.abstractRootResources;
    }
    
    Map<String, AbstractResource> getExplicitAbstractRootResources() {
        return this.explicitAbstractRootResources;
    }
    
    private ResourceUriRules newResourceUriRules(final AbstractResource ar) {
        assert null != ar;
        final BasicValidator validator = new BasicValidator();
        validator.validate(ar);
        for (final ResourceModelIssue issue : validator.getIssueList()) {
            Errors.error(issue.getMessage(), issue.isFatal());
        }
        return new ResourceUriRules(this.resourceConfig, this.getDispatchProvider(), this.injectableFactory, this.filterFactory, this.wadlFactory, this.dispatchingListener, ar);
    }
    
    protected ResourceMethodDispatchProvider getDispatchProvider() {
        return this.dispatcherFactory;
    }
    
    @Override
    public RequestListener getRequestListener() {
        return this.requestListener;
    }
    
    @Override
    public DispatchingListener getDispatchingListener() {
        return this.dispatchingListener;
    }
    
    @Override
    public ResponseListener getResponseListener() {
        return this.responseListener;
    }
    
    AbstractResource getAbstractResource(final Object o) {
        return this.getAbstractResource(o.getClass());
    }
    
    AbstractResource getAbstractResource(final Class c) {
        AbstractResource ar = this.abstractResourceMap.get(c);
        if (ar == null) {
            ar = IntrospectionModeller.createResource(c);
            this.abstractResourceMap.put(c, ar);
        }
        return ar;
    }
    
    @Override
    public boolean isInitiated() {
        return this.initiated;
    }
    
    @Override
    public void initiate(final ResourceConfig resourceConfig) {
        this.initiate(resourceConfig, null);
    }
    
    @Override
    public void initiate(final ResourceConfig rc, final IoCComponentProviderFactory _provider) {
        Errors.processWithErrors((Errors.Closure<Object>)new Errors.Closure<Void>() {
            @Override
            public Void f() {
                Errors.setReportMissingDependentFieldOrMethod(false);
                WebApplicationImpl.this._initiate(rc, _provider);
                return null;
            }
        });
    }
    
    private void _initiate(final ResourceConfig rc, final IoCComponentProviderFactory _provider) {
        if (rc == null) {
            throw new IllegalArgumentException("ResourceConfig instance MUST NOT be null");
        }
        if (this.initiated) {
            throw new ContainerException(ImplMessages.WEB_APP_ALREADY_INITIATED());
        }
        this.initiated = true;
        WebApplicationImpl.LOGGER.info("Initiating Jersey application, version '" + BuildId.getBuildId() + "'");
        final Class<?>[] components = ServiceFinder.find("jersey-server-components").toClassArray();
        if (components.length > 0) {
            if (WebApplicationImpl.LOGGER.isLoggable(Level.INFO)) {
                final StringBuilder b = new StringBuilder();
                b.append("Adding the following classes declared in META-INF/services/jersey-server-components to the resource configuration:");
                for (final Class c : components) {
                    b.append('\n').append("  ").append(c);
                }
                WebApplicationImpl.LOGGER.log(Level.INFO, b.toString());
            }
            this.resourceConfig = rc.clone();
            this.resourceConfig.getClasses().addAll(Arrays.asList(components));
        }
        else {
            this.resourceConfig = rc;
        }
        this.provider = _provider;
        this.providerFactories = new ArrayList<IoCComponentProviderFactory>(2);
        for (final Object o : this.resourceConfig.getProviderSingletons()) {
            if (o instanceof IoCComponentProviderFactory) {
                this.providerFactories.add((IoCComponentProviderFactory)o);
            }
        }
        if (_provider != null) {
            this.providerFactories.add(_provider);
        }
        this.cpFactory = (this.providerFactories.isEmpty() ? new ProviderFactory(this.injectableFactory) : new IoCProviderFactory(this.injectableFactory, this.providerFactories));
        this.rcpFactory = (this.providerFactories.isEmpty() ? new ResourceFactory(this.resourceConfig, this.injectableFactory) : new IoCResourceFactory(this.resourceConfig, this.injectableFactory, this.providerFactories));
        for (final IoCComponentProviderFactory f : this.providerFactories) {
            if (f instanceof IoCComponentProcessorFactoryInitializer) {
                final IoCComponentProcessorFactory cpf = new ComponentProcessorFactoryImpl();
                final IoCComponentProcessorFactoryInitializer i = (IoCComponentProcessorFactoryInitializer)f;
                i.init(cpf);
            }
        }
        this.resourceContext = new ResourceContext() {
            @Override
            public ExtendedUriInfo matchUriInfo(final URI u) throws ContainerException {
                try {
                    return WebApplicationImpl.this.handleMatchResourceRequest(u);
                }
                catch (ContainerException ex) {
                    throw ex;
                }
                catch (WebApplicationException ex2) {
                    if (ex2.getResponse().getStatus() == 404) {
                        return null;
                    }
                    throw new ContainerException(ex2);
                }
                catch (RuntimeException ex3) {
                    throw new ContainerException(ex3);
                }
            }
            
            @Override
            public Object matchResource(final URI u) throws ContainerException {
                final ExtendedUriInfo ui = this.matchUriInfo(u);
                return (ui != null) ? ui.getMatchedResources().get(0) : null;
            }
            
            @Override
            public <T> T matchResource(final URI u, final Class<T> c) throws ContainerException, ClassCastException {
                return c.cast(this.matchResource(u));
            }
            
            @Override
            public <T> T getResource(final Class<T> c) {
                return c.cast(WebApplicationImpl.this.getResourceComponentProvider(c).getInstance(WebApplicationImpl.this.context));
            }
        };
        final ProviderServices providerServices = new ProviderServices(ServerSide.class, this.cpFactory, this.resourceConfig.getProviderClasses(), this.resourceConfig.getProviderSingletons());
        this.injectableFactory.add(new ContextInjectableProvider(ProviderServices.class, providerServices));
        this.injectableFactory.add(new ContextInjectableProvider(ResourceMethodCustomInvokerDispatchFactory.class, new ResourceMethodCustomInvokerDispatchFactory(providerServices)));
        this.injectableFactory.add(new InjectableProvider<ParentRef, Type>() {
            @Override
            public ComponentScope getScope() {
                return ComponentScope.PerRequest;
            }
            
            @Override
            public Injectable<Object> getInjectable(final ComponentContext cc, final ParentRef a, final Type t) {
                if (!(t instanceof Class)) {
                    return null;
                }
                final Class target = ReflectionHelper.getDeclaringClass(cc.getAccesibleObject());
                final Class inject = (Class)t;
                return new Injectable<Object>() {
                    @Override
                    public Object getValue() {
                        final UriInfo ui = WebApplicationImpl.this.context.getUriInfo();
                        final List l = ui.getMatchedResources();
                        final Object parent = this.getParent(l, target);
                        if (parent == null) {
                            return null;
                        }
                        try {
                            return inject.cast(parent);
                        }
                        catch (ClassCastException ex) {
                            throw new ContainerException("The parent resource is expected to be of class " + inject.getName() + " but is of class " + parent.getClass().getName(), ex);
                        }
                    }
                    
                    private Object getParent(final List l, final Class target) {
                        if (l.isEmpty()) {
                            return null;
                        }
                        if (l.size() == 1) {
                            return (l.get(0).getClass() == target) ? null : l.get(0);
                        }
                        return (l.get(0).getClass() == target) ? l.get(1) : l.get(0);
                    }
                };
            }
        });
        this.injectableFactory.add(new InjectableProvider<Inject, Type>() {
            @Override
            public ComponentScope getScope() {
                return ComponentScope.PerRequest;
            }
            
            @Override
            public Injectable<Object> getInjectable(final ComponentContext cc, final Inject a, final Type t) {
                if (!(t instanceof Class)) {
                    return null;
                }
                final ResourceComponentProvider rcp = WebApplicationImpl.this.getResourceComponentProvider(cc, (Class)t);
                return new Injectable<Object>() {
                    @Override
                    public Object getValue() {
                        return rcp.getInstance(WebApplicationImpl.this.context);
                    }
                };
            }
        });
        this.injectableFactory.add(new InjectableProvider<Inject, Type>() {
            @Override
            public ComponentScope getScope() {
                return ComponentScope.Undefined;
            }
            
            @Override
            public Injectable<Object> getInjectable(final ComponentContext cc, final Inject a, final Type t) {
                if (!(t instanceof Class)) {
                    return null;
                }
                final ResourceComponentProvider rcp = WebApplicationImpl.this.getResourceComponentProvider(cc, (Class)t);
                if (rcp.getScope() == ComponentScope.PerRequest) {
                    return null;
                }
                return new Injectable<Object>() {
                    @Override
                    public Object getValue() {
                        return rcp.getInstance(WebApplicationImpl.this.context);
                    }
                };
            }
        });
        this.injectableFactory.add(new InjectableProvider<Inject, Type>() {
            @Override
            public ComponentScope getScope() {
                return ComponentScope.Singleton;
            }
            
            @Override
            public Injectable<Object> getInjectable(final ComponentContext cc, final Inject a, final Type t) {
                if (!(t instanceof Class)) {
                    return null;
                }
                final ResourceComponentProvider rcp = WebApplicationImpl.this.getResourceComponentProvider(cc, (Class)t);
                if (rcp.getScope() != ComponentScope.Singleton) {
                    return null;
                }
                return new Injectable<Object>() {
                    @Override
                    public Object getValue() {
                        return rcp.getInstance(WebApplicationImpl.this.context);
                    }
                };
            }
        });
        this.injectableFactory.add(new InjectableProvider<InjectParam, Type>() {
            @Override
            public ComponentScope getScope() {
                return ComponentScope.PerRequest;
            }
            
            @Override
            public Injectable<Object> getInjectable(final ComponentContext cc, final InjectParam a, final Type t) {
                if (!(t instanceof Class)) {
                    return null;
                }
                final ResourceComponentProvider rcp = WebApplicationImpl.this.getResourceComponentProvider(cc, (Class)t);
                return new Injectable<Object>() {
                    @Override
                    public Object getValue() {
                        return rcp.getInstance(WebApplicationImpl.this.context);
                    }
                };
            }
        });
        this.injectableFactory.add(new InjectableProvider<InjectParam, Type>() {
            @Override
            public ComponentScope getScope() {
                return ComponentScope.Undefined;
            }
            
            @Override
            public Injectable<Object> getInjectable(final ComponentContext cc, final InjectParam a, final Type t) {
                if (!(t instanceof Class)) {
                    return null;
                }
                final ResourceComponentProvider rcp = WebApplicationImpl.this.getResourceComponentProvider(cc, (Class)t);
                if (rcp.getScope() == ComponentScope.PerRequest) {
                    return null;
                }
                return new Injectable<Object>() {
                    @Override
                    public Object getValue() {
                        return rcp.getInstance(WebApplicationImpl.this.context);
                    }
                };
            }
        });
        this.injectableFactory.add(new InjectableProvider<InjectParam, Type>() {
            @Override
            public ComponentScope getScope() {
                return ComponentScope.Singleton;
            }
            
            @Override
            public Injectable<Object> getInjectable(final ComponentContext cc, final InjectParam a, final Type t) {
                if (!(t instanceof Class)) {
                    return null;
                }
                final ResourceComponentProvider rcp = WebApplicationImpl.this.getResourceComponentProvider(cc, (Class)t);
                if (rcp.getScope() != ComponentScope.Singleton) {
                    return null;
                }
                return new Injectable<Object>() {
                    @Override
                    public Object getValue() {
                        return rcp.getInstance(WebApplicationImpl.this.context);
                    }
                };
            }
        });
        this.injectableFactory.add(new ContextInjectableProvider(FeaturesAndProperties.class, this.resourceConfig));
        this.injectableFactory.add(new InjectableProvider<Context, Type>() {
            @Override
            public ComponentScope getScope() {
                return ComponentScope.Singleton;
            }
            
            @Override
            public Injectable<ResourceConfig> getInjectable(final ComponentContext cc, final Context a, final Type t) {
                if (t != ResourceConfig.class) {
                    return null;
                }
                return new Injectable<ResourceConfig>() {
                    @Override
                    public ResourceConfig getValue() {
                        return WebApplicationImpl.this.resourceConfig;
                    }
                };
            }
        });
        this.injectableFactory.add(new ContextInjectableProvider(ResourceContext.class, this.resourceContext));
        this.injectableFactory.configure(providerServices);
        boolean updateRequired = false;
        if (rc instanceof DeferredResourceConfig) {
            final DeferredResourceConfig drc = (DeferredResourceConfig)rc;
            if (this.resourceConfig == drc) {
                this.resourceConfig = drc.clone();
            }
            final DeferredResourceConfig.ApplicationHolder da = drc.getApplication(this.cpFactory);
            this.resourceConfig.add(da.getApplication());
            updateRequired = true;
            this.injectableFactory.add(new ContextInjectableProvider(javax.ws.rs.core.Application.class, da.getOriginalApplication()));
        }
        else {
            this.injectableFactory.add(new ContextInjectableProvider(javax.ws.rs.core.Application.class, this.resourceConfig));
        }
        for (final ResourceConfigurator configurator : providerServices.getProviders(ResourceConfigurator.class)) {
            configurator.configure(this.resourceConfig);
            updateRequired = true;
        }
        this.resourceConfig.validate();
        if (updateRequired) {
            providerServices.update(this.resourceConfig.getProviderClasses(), this.resourceConfig.getProviderSingletons(), this.injectableFactory);
        }
        this.templateContext = new TemplateFactory(providerServices);
        this.injectableFactory.add(new ContextInjectableProvider(TemplateContext.class, this.templateContext));
        final ContextResolverFactory crf = new ContextResolverFactory();
        this.exceptionFactory = new ExceptionMapperFactory();
        this.bodyFactory = new MessageBodyFactory(providerServices, this.getFeaturesAndProperties().getFeature("com.sun.jersey.config.feature.Pre14ProviderPrecedence"));
        this.injectableFactory.add(new ContextInjectableProvider(MessageBodyWorkers.class, this.bodyFactory));
        this.providers = new Providers() {
            @Override
            public <T> MessageBodyReader<T> getMessageBodyReader(final Class<T> c, final Type t, final Annotation[] as, final MediaType m) {
                return WebApplicationImpl.this.bodyFactory.getMessageBodyReader(c, t, as, m);
            }
            
            @Override
            public <T> MessageBodyWriter<T> getMessageBodyWriter(final Class<T> c, final Type t, final Annotation[] as, final MediaType m) {
                return WebApplicationImpl.this.bodyFactory.getMessageBodyWriter(c, t, as, m);
            }
            
            @Override
            public <T extends Throwable> ExceptionMapper<T> getExceptionMapper(final Class<T> c) {
                if (Throwable.class.isAssignableFrom(c)) {
                    return (ExceptionMapper<T>)WebApplicationImpl.this.exceptionFactory.find(c);
                }
                return null;
            }
            
            @Override
            public <T> ContextResolver<T> getContextResolver(final Class<T> ct, final MediaType m) {
                return crf.resolve(ct, m);
            }
        };
        this.injectableFactory.add(new ContextInjectableProvider(Providers.class, this.providers));
        this.stringReaderFactory = new StringReaderFactory();
        this.injectableFactory.add(new ContextInjectableProvider(StringReaderWorkers.class, this.stringReaderFactory));
        final MultivaluedParameterExtractorProvider mpep = new MultivaluedParameterExtractorFactory(this.stringReaderFactory);
        this.injectableFactory.add(new ContextInjectableProvider(MultivaluedParameterExtractorProvider.class, mpep));
        this.injectableFactory.add(new CookieParamInjectableProvider(mpep));
        this.injectableFactory.add(new HeaderParamInjectableProvider(mpep));
        this.injectableFactory.add(new HttpContextInjectableProvider());
        this.injectableFactory.add(new MatrixParamInjectableProvider(mpep));
        this.injectableFactory.add(new PathParamInjectableProvider(mpep));
        this.injectableFactory.add(new QueryParamInjectableProvider(mpep));
        this.injectableFactory.add(new FormParamInjectableProvider(mpep));
        this.filterFactory = new FilterFactory(providerServices);
        this.dispatcherFactory = ResourceMethodDispatcherFactory.create(providerServices);
        this.dispatchingListener = new DispatchingListenerProxy();
        this.wadlFactory = new WadlFactory(this.resourceConfig, this.providers);
        WadlApplicationContextInjectionProxy wadlApplicationContextInjectionProxy = null;
        if (!this.resourceConfig.getFeature("com.sun.jersey.config.feature.DisableWADL")) {
            wadlApplicationContextInjectionProxy = new WadlApplicationContextInjectionProxy();
            this.injectableFactory.add(new SingletonTypeInjectableProvider<Context, WadlApplicationContext>(WadlApplicationContext.class, wadlApplicationContextInjectionProxy) {});
            final WadlApplicationContext wac = wadlApplicationContextInjectionProxy;
            @Produces({ "application/vnd.sun.wadl+xml", "application/vnd.sun.wadl+json", "application/xml" })
            class WadlContextResolver implements ContextResolver<JAXBContext>
            {
                @Override
                public JAXBContext getContext(final Class<?> type) {
                    if (Application.class.isAssignableFrom(type)) {
                        return wac.getJAXBContext();
                    }
                    return null;
                }
            }
            this.resourceConfig.getSingletons().add(new WadlContextResolver());
            providerServices.update(this.resourceConfig.getProviderClasses(), this.resourceConfig.getProviderSingletons(), this.injectableFactory);
        }
        else {
            this.injectableFactory.add(new SingletonTypeInjectableProvider<Context, WadlApplicationContext>(WadlApplicationContext.class, wadlApplicationContextInjectionProxy) {});
        }
        this.filterFactory.init(this.resourceConfig);
        if (!this.resourceConfig.getMediaTypeMappings().isEmpty() || !this.resourceConfig.getLanguageMappings().isEmpty()) {
            boolean present = false;
            for (final ContainerRequestFilter f2 : this.filterFactory.getRequestFilters()) {
                present |= (f2 instanceof UriConnegFilter);
            }
            if (!present) {
                this.filterFactory.getRequestFilters().add(new UriConnegFilter(this.resourceConfig.getMediaTypeMappings(), this.resourceConfig.getLanguageMappings()));
            }
            else {
                WebApplicationImpl.LOGGER.warning("The media type and language mappings declared in the ResourceConfig are ignored because there is an instance of " + UriConnegFilter.class.getName() + "present in the list of request filters.");
            }
        }
        crf.init(providerServices, this.injectableFactory);
        this.exceptionFactory.init(providerServices);
        this.bodyFactory.init();
        this.stringReaderFactory.init(providerServices);
        Errors.setReportMissingDependentFieldOrMethod(true);
        this.cpFactory.injectOnAllComponents();
        this.cpFactory.injectOnProviderInstances(this.resourceConfig.getProviderSingletons());
        for (final IoCComponentProviderFactory providerFactory : this.providerFactories) {
            if (providerFactory instanceof WebApplicationListener) {
                final WebApplicationListener listener = (WebApplicationListener)providerFactory;
                listener.onWebApplicationReady();
            }
        }
        this.createAbstractResourceModelStructures();
        final RulesMap<UriRule> rootRules = new RootResourceUriRules(this, this.resourceConfig, this.wadlFactory, this.injectableFactory).getRules();
        this.rootsRule = new RootResourceClassesRule((Map<PathPattern, UriRule>)rootRules);
        if (!this.resourceConfig.getFeature("com.sun.jersey.config.feature.DisableWADL")) {
            wadlApplicationContextInjectionProxy.init(this.wadlFactory);
        }
        this.requestListener = MonitoringProviderFactory.createRequestListener(providerServices);
        this.responseListener = MonitoringProviderFactory.createResponseListener(providerServices);
        this.dispatchingListener.init(providerServices);
        this.callAbstractResourceModelListenersOnLoaded(providerServices);
        this.isTraceEnabled = (this.resourceConfig.getFeature("com.sun.jersey.config.feature.Trace") | this.resourceConfig.getFeature("com.sun.jersey.config.feature.TracePerRequest"));
    }
    
    @Override
    public Providers getProviders() {
        return this.providers;
    }
    
    @Override
    public ResourceContext getResourceContext() {
        return this.resourceContext;
    }
    
    @Override
    public MessageBodyWorkers getMessageBodyWorkers() {
        return this.bodyFactory;
    }
    
    @Override
    public ExceptionMapperContext getExceptionMapperContext() {
        return this.exceptionFactory;
    }
    
    @Override
    public ServerInjectableProviderFactory getServerInjectableProviderFactory() {
        return this.injectableFactory;
    }
    
    @Override
    public void handleRequest(final ContainerRequest request, final ContainerResponseWriter responseWriter) throws IOException {
        final ContainerResponse response = new ContainerResponse(this, request, responseWriter);
        this.handleRequest(request, response);
    }
    
    @Override
    public void handleRequest(final ContainerRequest request, final ContainerResponse response) throws IOException {
        final WebApplicationContext localContext = new WebApplicationContext(this, request, response);
        this.context.set(localContext);
        try {
            this._handleRequest(localContext, request, response);
        }
        finally {
            PerRequestFactory.destroy(localContext);
            this.closeableFactory.close(localContext);
            this.context.set(null);
        }
    }
    
    private WebApplicationContext handleMatchResourceRequest(final URI u) {
        final WebApplicationContext oldContext = (WebApplicationContext)this.context.get();
        final WebApplicationContext newContext = oldContext.createMatchResourceContext(u);
        this.context.set(newContext);
        try {
            this._handleRequest(newContext, newContext.getContainerRequest());
            return newContext;
        }
        finally {
            this.context.set(oldContext);
        }
    }
    
    @Override
    public void destroy() {
        for (final ResourceComponentProvider rcp : this.providerMap.values()) {
            rcp.destroy();
        }
        for (final ResourceComponentProvider rcp : this.singletonMap.values()) {
            rcp.destroy();
        }
        for (final ResourceComponentProvider rcp : this.providerWithAnnotationKeyMap.values()) {
            rcp.destroy();
        }
        this.cpFactory.destroy();
    }
    
    @Override
    public boolean isTracingEnabled() {
        return this.isTraceEnabled;
    }
    
    @Override
    public void trace(final String message) {
        this.context.get().trace(message);
    }
    
    private void _handleRequest(final WebApplicationContext localContext, final ContainerRequest request, ContainerResponse response) throws IOException {
        try {
            this.requestListener.onRequest(Thread.currentThread().getId(), request);
            this._handleRequest(localContext, request);
        }
        catch (WebApplicationException e) {
            response.mapWebApplicationException(e);
        }
        catch (MappableContainerException e2) {
            response.mapMappableContainerException(e2);
        }
        catch (RuntimeException e3) {
            if (!response.mapException(e3)) {
                WebApplicationImpl.LOGGER.log(Level.SEVERE, "The RuntimeException could not be mapped to a response, re-throwing to the HTTP container", e3);
                throw e3;
            }
        }
        try {
            for (final ContainerResponseFilter f : localContext.getResponseFilters()) {
                response = f.filter(request, response);
                localContext.setContainerResponse(response);
            }
            for (final ContainerResponseFilter f : this.filterFactory.getResponseFilters()) {
                response = f.filter(request, response);
                localContext.setContainerResponse(response);
            }
        }
        catch (WebApplicationException e) {
            response.mapWebApplicationException(e);
        }
        catch (MappableContainerException e2) {
            response.mapMappableContainerException(e2);
        }
        catch (RuntimeException e3) {
            if (!response.mapException(e3)) {
                WebApplicationImpl.LOGGER.log(Level.SEVERE, "The RuntimeException could not be mapped to a response, re-throwing to the HTTP container", e3);
                throw e3;
            }
        }
        try {
            response.write();
            this.responseListener.onResponse(Thread.currentThread().getId(), response);
        }
        catch (WebApplicationException e) {
            if (response.isCommitted()) {
                WebApplicationImpl.LOGGER.log(Level.SEVERE, "The response of the WebApplicationException cannot be utilized as the response is already committed. Re-throwing to the HTTP container", e);
                throw e;
            }
            response.mapWebApplicationException(e);
            response.write();
        }
    }
    
    private void _handleRequest(final WebApplicationContext localContext, ContainerRequest request) {
        for (final ContainerRequestFilter f : this.filterFactory.getRequestFilters()) {
            request = f.filter(request);
            localContext.setContainerRequest(request);
        }
        StringBuilder path = new StringBuilder();
        path.append("/").append(request.getPath(false));
        if (!this.resourceConfig.getFeature("com.sun.jersey.config.feature.IgnoreMatrixParams")) {
            path = this.stripMatrixParams(path);
        }
        if (!this.rootsRule.accept(path, null, localContext)) {
            throw new NotFoundException(request.getRequestUri());
        }
    }
    
    @Override
    public HttpContext getThreadLocalHttpContext() {
        return this.context;
    }
    
    private StringBuilder stripMatrixParams(final StringBuilder path) {
        int e = path.indexOf(";");
        if (e == -1) {
            return path;
        }
        int s = 0;
        final StringBuilder sb = new StringBuilder();
        do {
            sb.append(path, s, e);
            s = path.indexOf("/", e + 1);
            if (s == -1) {
                break;
            }
            e = path.indexOf(";", s);
        } while (e != -1);
        if (s != -1) {
            sb.append(path, s, path.length());
        }
        return sb;
    }
    
    private void createAbstractResourceModelStructures() {
        final Set<AbstractResource> rootARs = new HashSet<AbstractResource>();
        for (final Object o : this.resourceConfig.getRootResourceSingletons()) {
            rootARs.add(this.getAbstractResource(o));
        }
        for (final Class<?> c : this.resourceConfig.getRootResourceClasses()) {
            rootARs.add(this.getAbstractResource(c));
        }
        final Map<String, AbstractResource> explicitRootARs = new HashMap<String, AbstractResource>();
        for (final Map.Entry<String, Object> e : this.resourceConfig.getExplicitRootResources().entrySet()) {
            final Object o2 = e.getValue();
            final Class c2 = (o2 instanceof Class) ? ((Class)o2) : o2.getClass();
            final AbstractResource ar = new AbstractResource(e.getKey(), this.getAbstractResource(c2));
            rootARs.add(ar);
            explicitRootARs.put(e.getKey(), ar);
        }
        this.abstractRootResources = Collections.unmodifiableSet((Set<? extends AbstractResource>)rootARs);
        this.explicitAbstractRootResources = Collections.unmodifiableMap((Map<? extends String, ? extends AbstractResource>)explicitRootARs);
    }
    
    private void callAbstractResourceModelListenersOnLoaded(final ProviderServices providerServices) {
        for (final AbstractResourceModelListener aml : providerServices.getProviders(AbstractResourceModelListener.class)) {
            aml.onLoaded(this.armContext);
        }
    }
    
    private <T> T createProxy(final Class<T> c, final InvocationHandler i) {
        return AccessController.doPrivileged((PrivilegedAction<T>)new PrivilegedAction<T>() {
            @Override
            public T run() {
                return c.cast(Proxy.newProxyInstance(WebApplicationImpl.this.getClass().getClassLoader(), new Class[] { c }, i));
            }
        });
    }
    
    static {
        LOGGER = Logger.getLogger(WebApplicationImpl.class.getName());
        NULL_COMPONENT_PROCESSOR = new IoCComponentProcessor() {
            @Override
            public void preConstruct() {
            }
            
            @Override
            public void postConstruct(final Object o) {
            }
        };
    }
    
    private static class ClassAnnotationKey
    {
        private final Class c;
        private final Set<Annotation> as;
        
        public ClassAnnotationKey(final Class c, final Annotation[] as) {
            this.c = c;
            this.as = new HashSet<Annotation>(Arrays.asList(as));
        }
        
        @Override
        public int hashCode() {
            int hash = 5;
            hash = 67 * hash + ((this.c != null) ? this.c.hashCode() : 0);
            hash = 67 * hash + ((this.as != null) ? this.as.hashCode() : 0);
            return hash;
        }
        
        @Override
        public boolean equals(final Object obj) {
            if (obj == null) {
                return false;
            }
            if (this.getClass() != obj.getClass()) {
                return false;
            }
            final ClassAnnotationKey other = (ClassAnnotationKey)obj;
            return (this.c == other.c || (this.c != null && this.c.equals(other.c))) && (this.as == other.as || (this.as != null && this.as.equals(other.as)));
        }
    }
    
    private class ComponentProcessorImpl implements IoCComponentProcessor
    {
        private final ResourceComponentInjector rci;
        
        ComponentProcessorImpl(final ResourceComponentInjector rci) {
            this.rci = rci;
        }
        
        @Override
        public void preConstruct() {
        }
        
        @Override
        public void postConstruct(final Object o) {
            this.rci.inject(WebApplicationImpl.this.context.get(), o);
        }
    }
    
    private class ComponentProcessorFactoryImpl implements IoCComponentProcessorFactory
    {
        private final ConcurrentMap<Class, IoCComponentProcessor> componentProcessorMap;
        
        private ComponentProcessorFactoryImpl() {
            this.componentProcessorMap = new ConcurrentHashMap<Class, IoCComponentProcessor>();
        }
        
        @Override
        public ComponentScope getScope(final Class c) {
            return WebApplicationImpl.this.rcpFactory.getScope(c);
        }
        
        @Override
        public IoCComponentProcessor get(final Class c, final ComponentScope scope) {
            IoCComponentProcessor cp = this.componentProcessorMap.get(c);
            if (cp != null) {
                return (cp == WebApplicationImpl.NULL_COMPONENT_PROCESSOR) ? null : cp;
            }
            synchronized (WebApplicationImpl.this.abstractResourceMap) {
                cp = this.componentProcessorMap.get(c);
                if (cp != null) {
                    return (cp == WebApplicationImpl.NULL_COMPONENT_PROCESSOR) ? null : cp;
                }
                final ResourceComponentInjector rci = Errors.processWithErrors((Errors.Closure<ResourceComponentInjector>)new Errors.Closure<ResourceComponentInjector>() {
                    @Override
                    public ResourceComponentInjector f() {
                        return new ResourceComponentInjector(WebApplicationImpl.this.injectableFactory, scope, WebApplicationImpl.this.getAbstractResource(c));
                    }
                });
                if (rci.hasInjectableArtifacts()) {
                    cp = new ComponentProcessorImpl(rci);
                    this.componentProcessorMap.put(c, cp);
                }
                else {
                    cp = null;
                    this.componentProcessorMap.put(c, WebApplicationImpl.NULL_COMPONENT_PROCESSOR);
                }
            }
            return cp;
        }
    }
    
    private static class ContextInjectableProvider<T> extends SingletonTypeInjectableProvider<Context, T>
    {
        ContextInjectableProvider(final Type type, final T instance) {
            super(type, instance);
        }
    }
    
    private class DispatchingListenerProxy implements DispatchingListener
    {
        private DispatchingListener dispatchingListener;
        
        @Override
        public void onSubResource(final long id, final Class subResource) {
            this.dispatchingListener.onSubResource(id, subResource);
        }
        
        @Override
        public void onSubResourceLocator(final long id, final AbstractSubResourceLocator locator) {
            this.dispatchingListener.onSubResourceLocator(id, locator);
        }
        
        @Override
        public void onResourceMethod(final long id, final AbstractResourceMethod method) {
            this.dispatchingListener.onResourceMethod(id, method);
        }
        
        public void init(final ProviderServices providerServices) {
            this.dispatchingListener = MonitoringProviderFactory.createDispatchingListener(providerServices);
        }
    }
}
