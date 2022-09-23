// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.impl.container.httpserver;

import java.util.ArrayList;
import java.io.OutputStream;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ReloadListener;
import java.util.Iterator;
import com.sun.net.httpserver.Headers;
import java.util.Map;
import com.sun.jersey.core.header.InBoundHeaders;
import java.net.InetSocketAddress;
import java.util.List;
import java.io.IOException;
import com.sun.jersey.spi.container.ContainerResponseWriter;
import com.sun.jersey.spi.container.ContainerRequest;
import java.net.URISyntaxException;
import java.net.URI;
import com.sun.net.httpserver.HttpsExchange;
import javax.ws.rs.core.UriBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.jersey.api.container.ContainerException;
import com.sun.jersey.spi.container.WebApplication;
import com.sun.jersey.spi.container.ContainerListener;
import com.sun.net.httpserver.HttpHandler;

public class HttpHandlerContainer implements HttpHandler, ContainerListener
{
    private WebApplication application;
    
    public HttpHandlerContainer(final WebApplication app) throws ContainerException {
        this.application = app;
    }
    
    @Override
    public void handle(final HttpExchange exchange) throws IOException {
        final WebApplication _application = this.application;
        URI exchangeUri = exchange.getRequestURI();
        String decodedBasePath = exchange.getHttpContext().getPath();
        if (!decodedBasePath.endsWith("/")) {
            if (decodedBasePath.equals(exchangeUri.getPath())) {
                exchangeUri = UriBuilder.fromUri(exchangeUri).path("/").build(new Object[0]);
            }
            decodedBasePath += "/";
        }
        final String scheme = (exchange instanceof HttpsExchange) ? "https" : "http";
        URI baseUri = null;
        try {
            final List<String> hostHeader = exchange.getRequestHeaders().get((Object)"Host");
            if (hostHeader != null) {
                final StringBuilder sb = new StringBuilder(scheme);
                sb.append("://").append(hostHeader.get(0)).append(decodedBasePath);
                baseUri = new URI(sb.toString());
            }
            else {
                final InetSocketAddress addr = exchange.getLocalAddress();
                baseUri = new URI(scheme, null, addr.getHostName(), addr.getPort(), decodedBasePath, null, null);
            }
        }
        catch (URISyntaxException ex) {
            throw new IllegalArgumentException(ex);
        }
        final URI requestUri = baseUri.resolve(exchangeUri);
        final ContainerRequest cRequest = new ContainerRequest(_application, exchange.getRequestMethod(), baseUri, requestUri, this.getHeaders(exchange), exchange.getRequestBody());
        try {
            _application.handleRequest(cRequest, new Writer(exchange));
        }
        catch (RuntimeException e) {
            e.printStackTrace();
            exchange.getResponseHeaders().clear();
            exchange.sendResponseHeaders(500, -1L);
        }
        catch (IOException ex2) {
            ex2.printStackTrace();
            exchange.getResponseHeaders().clear();
            exchange.sendResponseHeaders(500, -1L);
            throw ex2;
        }
        exchange.getResponseBody().flush();
        exchange.close();
    }
    
    private InBoundHeaders getHeaders(final HttpExchange exchange) {
        final InBoundHeaders rh = new InBoundHeaders();
        final Headers eh = exchange.getRequestHeaders();
        for (final Map.Entry<String, List<String>> e : eh.entrySet()) {
            rh.put(e.getKey(), (List<V>)e.getValue());
        }
        return rh;
    }
    
    @Override
    public void onReload() {
        final WebApplication oldApplication = this.application;
        this.application = this.application.clone();
        if (this.application.getFeaturesAndProperties() instanceof ReloadListener) {
            ((ReloadListener)this.application.getFeaturesAndProperties()).onReload();
        }
        oldApplication.destroy();
    }
    
    private static final class Writer implements ContainerResponseWriter
    {
        final HttpExchange exchange;
        
        Writer(final HttpExchange exchange) {
            this.exchange = exchange;
        }
        
        @Override
        public OutputStream writeStatusAndHeaders(final long contentLength, final ContainerResponse cResponse) throws IOException {
            final Headers eh = this.exchange.getResponseHeaders();
            for (final Map.Entry<String, List<Object>> e : cResponse.getHttpHeaders().entrySet()) {
                final List<String> values = new ArrayList<String>();
                for (final Object v : e.getValue()) {
                    values.add(ContainerResponse.getHeaderValue(v));
                }
                eh.put((String)e.getKey(), values);
            }
            if (cResponse.getStatus() == 204) {
                this.exchange.sendResponseHeaders(cResponse.getStatus(), -1L);
            }
            else {
                this.exchange.sendResponseHeaders(cResponse.getStatus(), this.getResponseLength(contentLength));
            }
            return this.exchange.getResponseBody();
        }
        
        @Override
        public void finish() throws IOException {
        }
        
        private long getResponseLength(final long contentLength) {
            if (contentLength == 0L) {
                return -1L;
            }
            if (contentLength < 0L) {
                return 0L;
            }
            return contentLength;
        }
    }
}
