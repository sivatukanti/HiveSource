// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.impl.monitoring;

import java.util.Collections;
import javax.ws.rs.ext.ExceptionMapper;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.api.model.AbstractResourceMethod;
import com.sun.jersey.api.model.AbstractSubResourceLocator;
import com.sun.jersey.spi.monitoring.ResponseListenerAdapter;
import com.sun.jersey.spi.monitoring.ResponseListener;
import com.sun.jersey.spi.monitoring.DispatchingListenerAdapter;
import com.sun.jersey.spi.monitoring.DispatchingListener;
import java.util.Iterator;
import com.sun.jersey.spi.monitoring.RequestListenerAdapter;
import java.util.Set;
import com.sun.jersey.spi.monitoring.RequestListener;
import com.sun.jersey.core.spi.component.ProviderServices;

public final class MonitoringProviderFactory
{
    private static final EmptyListener EMPTY_LISTENER;
    
    private MonitoringProviderFactory() {
    }
    
    public static RequestListener createRequestListener(final ProviderServices providerServices) {
        final Set<RequestListener> listeners = providerServices.getProvidersAndServices(RequestListener.class);
        RequestListener requestListener = listeners.isEmpty() ? MonitoringProviderFactory.EMPTY_LISTENER : new AggregatedRequestListener((Set)listeners);
        for (final RequestListenerAdapter a : providerServices.getProvidersAndServices(RequestListenerAdapter.class)) {
            requestListener = a.adapt(requestListener);
        }
        return requestListener;
    }
    
    public static DispatchingListener createDispatchingListener(final ProviderServices providerServices) {
        final Set<DispatchingListener> listeners = providerServices.getProvidersAndServices(DispatchingListener.class);
        DispatchingListener dispatchingListener = listeners.isEmpty() ? MonitoringProviderFactory.EMPTY_LISTENER : new AggregatedDispatchingListener((Set)listeners);
        for (final DispatchingListenerAdapter a : providerServices.getProvidersAndServices(DispatchingListenerAdapter.class)) {
            dispatchingListener = a.adapt(dispatchingListener);
        }
        return dispatchingListener;
    }
    
    public static ResponseListener createResponseListener(final ProviderServices providerServices) {
        final Set<ResponseListener> listeners = providerServices.getProvidersAndServices(ResponseListener.class);
        ResponseListener responseListener = listeners.isEmpty() ? MonitoringProviderFactory.EMPTY_LISTENER : new AggregatedResponseListener((Set)listeners);
        for (final ResponseListenerAdapter a : providerServices.getProvidersAndServices(ResponseListenerAdapter.class)) {
            responseListener = a.adapt(responseListener);
        }
        return responseListener;
    }
    
    static {
        EMPTY_LISTENER = new EmptyListener();
    }
    
    private static class EmptyListener implements RequestListener, ResponseListener, DispatchingListener
    {
        @Override
        public void onSubResource(final long id, final Class subResource) {
        }
        
        @Override
        public void onSubResourceLocator(final long id, final AbstractSubResourceLocator locator) {
        }
        
        @Override
        public void onResourceMethod(final long id, final AbstractResourceMethod method) {
        }
        
        @Override
        public void onRequest(final long id, final ContainerRequest request) {
        }
        
        @Override
        public void onError(final long id, final Throwable ex) {
        }
        
        @Override
        public void onResponse(final long id, final ContainerResponse response) {
        }
        
        @Override
        public void onMappedException(final long id, final Throwable exception, final ExceptionMapper mapper) {
        }
    }
    
    private static class AggregatedRequestListener implements RequestListener
    {
        private final Set<RequestListener> listeners;
        
        private AggregatedRequestListener(final Set<RequestListener> listeners) {
            this.listeners = Collections.unmodifiableSet((Set<? extends RequestListener>)listeners);
        }
        
        @Override
        public void onRequest(final long id, final ContainerRequest request) {
            for (final RequestListener requestListener : this.listeners) {
                requestListener.onRequest(id, request);
            }
        }
    }
    
    private static class AggregatedResponseListener implements ResponseListener
    {
        private final Set<ResponseListener> listeners;
        
        private AggregatedResponseListener(final Set<ResponseListener> listeners) {
            this.listeners = Collections.unmodifiableSet((Set<? extends ResponseListener>)listeners);
        }
        
        @Override
        public void onError(final long id, final Throwable ex) {
            for (final ResponseListener responseListener : this.listeners) {
                responseListener.onError(id, ex);
            }
        }
        
        @Override
        public void onResponse(final long id, final ContainerResponse response) {
            for (final ResponseListener responseListener : this.listeners) {
                responseListener.onResponse(id, response);
            }
        }
        
        @Override
        public void onMappedException(final long id, final Throwable exception, final ExceptionMapper mapper) {
            for (final ResponseListener responseListener : this.listeners) {
                responseListener.onMappedException(id, exception, mapper);
            }
        }
    }
    
    private static class AggregatedDispatchingListener implements DispatchingListener
    {
        private final Set<DispatchingListener> listeners;
        
        private AggregatedDispatchingListener(final Set<DispatchingListener> listeners) {
            this.listeners = Collections.unmodifiableSet((Set<? extends DispatchingListener>)listeners);
        }
        
        @Override
        public void onSubResource(final long id, final Class subResource) {
            for (final DispatchingListener dispatchingListener : this.listeners) {
                dispatchingListener.onSubResource(id, subResource);
            }
        }
        
        @Override
        public void onSubResourceLocator(final long id, final AbstractSubResourceLocator locator) {
            for (final DispatchingListener dispatchingListener : this.listeners) {
                dispatchingListener.onSubResourceLocator(id, locator);
            }
        }
        
        @Override
        public void onResourceMethod(final long id, final AbstractResourceMethod method) {
            for (final DispatchingListener dispatchingListener : this.listeners) {
                dispatchingListener.onResourceMethod(id, method);
            }
        }
    }
}
