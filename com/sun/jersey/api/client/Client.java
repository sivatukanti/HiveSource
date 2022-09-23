// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.api.client;

import com.sun.jersey.core.spi.component.ComponentInjector;
import com.sun.jersey.core.spi.component.ioc.IoCComponentProcessor;
import com.sun.jersey.spi.inject.SingletonTypeInjectableProvider;
import com.sun.jersey.client.urlconnection.URLConnectionClientHandler;
import java.util.Iterator;
import com.sun.jersey.client.proxy.ViewProxy;
import java.util.concurrent.Future;
import java.net.URI;
import java.util.Collection;
import java.lang.reflect.ParameterizedType;
import com.sun.jersey.spi.inject.Injectable;
import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.ComponentScope;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.core.MediaType;
import java.lang.annotation.Annotation;
import com.sun.jersey.spi.MessageBodyWorkers;
import com.sun.jersey.core.spi.factory.ContextResolverFactory;
import com.sun.jersey.spi.inject.InjectableProvider;
import java.lang.reflect.Type;
import com.sun.jersey.core.util.FeaturesAndProperties;
import com.sun.jersey.spi.inject.ConstrainedToType;
import com.sun.jersey.core.spi.component.ProviderServices;
import com.sun.jersey.spi.inject.ClientSide;
import com.sun.jersey.core.spi.component.ioc.IoCProviderFactory;
import com.sun.jersey.spi.inject.InjectableProviderContext;
import com.sun.jersey.core.spi.component.ioc.IoCComponentProcessorFactory;
import com.sun.jersey.core.spi.component.ioc.IoCComponentProcessorFactoryInitializer;
import java.util.Map;
import com.sun.jersey.core.spi.factory.InjectableProviderFactory;
import java.util.logging.Level;
import com.sun.jersey.spi.service.ServiceFinder;
import java.util.concurrent.Executors;
import com.sun.jersey.spi.inject.Errors;
import com.sun.jersey.core.spi.component.ioc.IoCComponentProviderFactory;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.core.spi.factory.MessageBodyFactory;
import com.sun.jersey.client.proxy.ViewProxyProvider;
import java.util.Set;
import com.sun.jersey.client.impl.CopyOnWriteHashMap;
import java.util.concurrent.ExecutorService;
import com.sun.jersey.core.util.LazyVal;
import javax.ws.rs.ext.Providers;
import com.sun.jersey.core.spi.component.ProviderFactory;
import java.util.logging.Logger;
import com.sun.jersey.api.client.filter.Filterable;

public class Client extends Filterable implements ClientHandler
{
    private static final Logger LOGGER;
    private ProviderFactory componentProviderFactory;
    private Providers providers;
    private boolean destroyed;
    private LazyVal<ExecutorService> executorService;
    private CopyOnWriteHashMap<String, Object> properties;
    private Set<ViewProxyProvider> vpps;
    private MessageBodyFactory workers;
    
    public Client() {
        this(createDefaultClientHander(), new DefaultClientConfig(), null);
    }
    
    public Client(final ClientHandler root) {
        this(root, new DefaultClientConfig(), null);
    }
    
    public Client(final ClientHandler root, final ClientConfig config) {
        this(root, config, null);
    }
    
    public Client(final ClientHandler root, final ClientConfig config, final IoCComponentProviderFactory provider) {
        super(root);
        this.destroyed = false;
        Errors.processWithErrors((Errors.Closure<Object>)new Errors.Closure<Void>() {
            @Override
            public Void f() {
                Errors.setReportMissingDependentFieldOrMethod(false);
                Client.this.init(root, config, provider);
                return null;
            }
        });
    }
    
    private void init(final ClientHandler root, ClientConfig config, final IoCComponentProviderFactory provider) {
        final Object threadpoolSize = config.getProperties().get("com.sun.jersey.client.property.threadpoolSize");
        this.executorService = new LazyVal<ExecutorService>() {
            @Override
            protected ExecutorService instance() {
                if (threadpoolSize != null && threadpoolSize instanceof Integer && (int)threadpoolSize > 0) {
                    return Executors.newFixedThreadPool((int)threadpoolSize);
                }
                return Executors.newCachedThreadPool();
            }
        };
        final Class<?>[] components = ServiceFinder.find("jersey-client-components").toClassArray();
        if (components.length > 0) {
            if (Client.LOGGER.isLoggable(Level.INFO)) {
                final StringBuilder b = new StringBuilder();
                b.append("Adding the following classes declared in META-INF/services/jersey-client-components to the client configuration:");
                for (final Class c : components) {
                    b.append('\n').append("  ").append(c);
                }
                Client.LOGGER.log(Level.INFO, b.toString());
            }
            config = new ComponentsClientConfig(config, components);
        }
        final InjectableProviderFactory injectableFactory = new InjectableProviderFactory();
        this.getProperties().putAll(config.getProperties());
        if (provider != null && provider instanceof IoCComponentProcessorFactoryInitializer) {
            final IoCComponentProcessorFactoryInitializer i = (IoCComponentProcessorFactoryInitializer)provider;
            i.init(new ComponentProcessorFactoryImpl(injectableFactory));
        }
        this.componentProviderFactory = ((provider == null) ? new ProviderFactory(injectableFactory) : new IoCProviderFactory(injectableFactory, provider));
        final ProviderServices providerServices = new ProviderServices(ClientSide.class, this.componentProviderFactory, config.getClasses(), config.getSingletons());
        this.vpps = providerServices.getServices(ViewProxyProvider.class);
        injectableFactory.add(new ContextInjectableProvider(FeaturesAndProperties.class, config));
        injectableFactory.add(new ContextInjectableProvider(ClientConfig.class, config));
        injectableFactory.add(new ContextInjectableProvider(Client.class, this));
        injectableFactory.configure(providerServices);
        final ContextResolverFactory crf = new ContextResolverFactory();
        final MessageBodyFactory bodyContext = new MessageBodyFactory(providerServices, config.getFeature("com.sun.jersey.config.feature.Pre14ProviderPrecedence"));
        this.workers = bodyContext;
        injectableFactory.add(new ContextInjectableProvider(MessageBodyWorkers.class, bodyContext));
        this.providers = new Providers() {
            @Override
            public <T> MessageBodyReader<T> getMessageBodyReader(final Class<T> c, final Type t, final Annotation[] as, final MediaType m) {
                return bodyContext.getMessageBodyReader(c, t, as, m);
            }
            
            @Override
            public <T> MessageBodyWriter<T> getMessageBodyWriter(final Class<T> c, final Type t, final Annotation[] as, final MediaType m) {
                return bodyContext.getMessageBodyWriter(c, t, as, m);
            }
            
            @Override
            public <T extends Throwable> ExceptionMapper<T> getExceptionMapper(final Class<T> c) {
                throw new IllegalArgumentException("This method is not supported on the client side");
            }
            
            @Override
            public <T> ContextResolver<T> getContextResolver(final Class<T> ct, final MediaType m) {
                return crf.resolve(ct, m);
            }
        };
        injectableFactory.add(new ContextInjectableProvider(Providers.class, this.providers));
        injectableFactory.add(new InjectableProvider<Context, Type>() {
            @Override
            public ComponentScope getScope() {
                return ComponentScope.Singleton;
            }
            
            @Override
            public Injectable<Injectable> getInjectable(final ComponentContext ic, final Context a, final Type c) {
                if (c instanceof ParameterizedType) {
                    final ParameterizedType pt = (ParameterizedType)c;
                    if (pt.getRawType() == Injectable.class && pt.getActualTypeArguments().length == 1) {
                        final Injectable<?> i = (Injectable<?>)injectableFactory.getInjectable(a.annotationType(), ic, a, pt.getActualTypeArguments()[0], ComponentScope.PERREQUEST_UNDEFINED_SINGLETON);
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
        crf.init(providerServices, injectableFactory);
        bodyContext.init();
        Errors.setReportMissingDependentFieldOrMethod(true);
        this.componentProviderFactory.injectOnAllComponents();
        this.componentProviderFactory.injectOnProviderInstances(config.getSingletons());
        this.componentProviderFactory.injectOnProviderInstance(root);
    }
    
    public void destroy() {
        if (!this.destroyed) {
            this.componentProviderFactory.destroy();
            this.destroyed = true;
        }
    }
    
    @Override
    protected void finalize() throws Throwable {
        this.destroy();
        super.finalize();
    }
    
    public Providers getProviders() {
        return this.providers;
    }
    
    public MessageBodyWorkers getMessageBodyWorkers() {
        return this.workers;
    }
    
    public WebResource resource(final String u) {
        return this.resource(URI.create(u));
    }
    
    public WebResource resource(final URI u) {
        return new WebResource(this, this.properties, u);
    }
    
    public AsyncWebResource asyncResource(final String u) {
        return this.asyncResource(URI.create(u));
    }
    
    public AsyncWebResource asyncResource(final URI u) {
        return new AsyncWebResource(this, this.properties, u);
    }
    
    public ViewResource viewResource(final String u) {
        return this.viewResource(URI.create(u));
    }
    
    public ViewResource viewResource(final URI u) {
        return new ViewResource(this, u);
    }
    
    public AsyncViewResource asyncViewResource(final String u) {
        return this.asyncViewResource(URI.create(u));
    }
    
    public AsyncViewResource asyncViewResource(final URI u) {
        return new AsyncViewResource(this, u);
    }
    
    public <T> T view(final String u, final Class<T> type) {
        final ViewResource vr = this.viewResource(u);
        return vr.get(type);
    }
    
    public <T> T view(final URI uri, final Class<T> type) {
        final ViewResource vr = this.viewResource(uri);
        return vr.get(type);
    }
    
    public <T> T view(final String u, final T t) {
        final ViewResource vr = this.viewResource(u);
        return vr.get(t);
    }
    
    public <T> T view(final URI uri, final T t) {
        final ViewResource vr = this.viewResource(uri);
        return vr.get(t);
    }
    
    public <T> Future<T> asyncView(final String u, final Class<T> type) {
        final AsyncViewResource vr = this.asyncViewResource(u);
        return vr.get(type);
    }
    
    public <T> Future<T> asyncView(final URI uri, final Class<T> type) {
        final AsyncViewResource vr = this.asyncViewResource(uri);
        return vr.get(type);
    }
    
    public <T> Future<T> asyncView(final String u, final T t) {
        final AsyncViewResource vr = this.asyncViewResource(u);
        return vr.get(t);
    }
    
    public <T> Future<T> asyncView(final URI uri, final T t) {
        final AsyncViewResource vr = this.asyncViewResource(uri);
        return vr.get(t);
    }
    
    public <T> T view(final Class<T> c, final ClientResponse response) {
        return this.getViewProxy(c).view(c, response);
    }
    
    public <T> T view(final T t, final ClientResponse response) {
        return (T)this.getViewProxy(t.getClass()).view(t, response);
    }
    
    public <T> ViewProxy<T> getViewProxy(final Class<T> c) {
        for (final ViewProxyProvider vpp : this.vpps) {
            final ViewProxy<T> vp = vpp.proxy(this, c);
            if (vp != null) {
                return vp;
            }
        }
        throw new IllegalArgumentException("A view proxy is not available for the class '" + c.getName() + "'");
    }
    
    public void setExecutorService(final ExecutorService es) {
        if (es == null) {
            throw new IllegalArgumentException("ExecutorService service MUST not be null");
        }
        this.executorService.set(es);
    }
    
    public ExecutorService getExecutorService() {
        return this.executorService.get();
    }
    
    public Map<String, Object> getProperties() {
        if (this.properties == null) {
            this.properties = new CopyOnWriteHashMap<String, Object>();
        }
        return this.properties;
    }
    
    public void setFollowRedirects(final Boolean redirect) {
        this.getProperties().put("com.sun.jersey.client.property.followRedirects", redirect);
    }
    
    public void setReadTimeout(final Integer interval) {
        this.getProperties().put("com.sun.jersey.client.property.readTimeout", interval);
    }
    
    public void setConnectTimeout(final Integer interval) {
        this.getProperties().put("com.sun.jersey.client.property.connectTimeout", interval);
    }
    
    public void setChunkedEncodingSize(final Integer chunkSize) {
        this.getProperties().put("com.sun.jersey.client.property.chunkedEncodingSize", chunkSize);
    }
    
    @Override
    public ClientResponse handle(final ClientRequest request) throws ClientHandlerException {
        request.getProperties().putAll(this.properties);
        request.getProperties().put(Client.class.getName(), this);
        final ClientResponse response = this.getHeadHandler().handle(request);
        response.getProperties().put(Client.class.getName(), this);
        return response;
    }
    
    public void inject(final Object o) {
        this.componentProviderFactory.injectOnProviderInstance(o);
    }
    
    public static Client create() {
        return new Client(createDefaultClientHander());
    }
    
    public static Client create(final ClientConfig cc) {
        return new Client(createDefaultClientHander(), cc);
    }
    
    public static Client create(final ClientConfig cc, final IoCComponentProviderFactory provider) {
        return new Client(createDefaultClientHander(), cc, provider);
    }
    
    private static ClientHandler createDefaultClientHander() {
        return new URLConnectionClientHandler();
    }
    
    static {
        LOGGER = Logger.getLogger(Client.class.getName());
    }
    
    private static class ContextInjectableProvider<T> extends SingletonTypeInjectableProvider<Context, T>
    {
        ContextInjectableProvider(final Type type, final T instance) {
            super(type, instance);
        }
    }
    
    private class ComponentProcessorFactoryImpl implements IoCComponentProcessorFactory
    {
        private final InjectableProviderFactory injectableFactory;
        
        ComponentProcessorFactoryImpl(final InjectableProviderFactory injectableFactory) {
            this.injectableFactory = injectableFactory;
        }
        
        @Override
        public ComponentScope getScope(final Class c) {
            return ComponentScope.Singleton;
        }
        
        @Override
        public IoCComponentProcessor get(final Class c, final ComponentScope scope) {
            final ComponentInjector ci = new ComponentInjector(this.injectableFactory, c);
            return new IoCComponentProcessor() {
                @Override
                public void preConstruct() {
                }
                
                @Override
                public void postConstruct(final Object o) {
                    ci.inject(o);
                }
            };
        }
    }
}
