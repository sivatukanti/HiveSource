// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.api.client.filter;

import java.io.IOException;
import java.io.OutputStream;
import com.sun.jersey.api.client.AbstractClientRequestAdapter;
import com.sun.jersey.api.client.ClientHandlerException;
import java.io.InputStream;
import com.sun.jersey.api.client.ClientRequestAdapter;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientRequest;

public class ConnectionListenerFilter extends ClientFilter
{
    private final OnStartConnectionListener listenerFactory;
    
    public ConnectionListenerFilter(final OnStartConnectionListener listenerFactory) {
        if (listenerFactory == null) {
            throw new IllegalArgumentException("ConnectionListenerFilter can't be initiated without OnStartConnectionListener");
        }
        this.listenerFactory = listenerFactory;
    }
    
    @Override
    public ClientResponse handle(final ClientRequest request) throws ClientHandlerException {
        final ContainerListener listener = this.listenerFactory.onStart(new ClientRequestContainer(request));
        request.setAdapter(new Adapter(request.getAdapter(), listener));
        final ClientResponse response = this.getNext().handle(request);
        if (response.hasEntity()) {
            final InputStream entityInputStream = response.getEntityInputStream();
            listener.onReceiveStart(response.getLength());
            response.setEntityInputStream(new ReportingInputStream(entityInputStream, listener));
        }
        else {
            listener.onFinish();
        }
        return response;
    }
    
    private static final class Adapter extends AbstractClientRequestAdapter
    {
        private final ContainerListener listener;
        
        Adapter(final ClientRequestAdapter cra, final ContainerListener listener) {
            super(cra);
            this.listener = listener;
        }
        
        @Override
        public OutputStream adapt(final ClientRequest request, final OutputStream out) throws IOException {
            return new ReportingOutputStream(this.getAdapter().adapt(request, out), this.listener);
        }
    }
}
