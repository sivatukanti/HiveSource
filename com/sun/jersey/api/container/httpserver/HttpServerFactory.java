// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.api.container.httpserver;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import com.sun.net.httpserver.HttpsServer;
import java.net.InetSocketAddress;
import com.sun.jersey.core.spi.component.ioc.IoCComponentProviderFactory;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.api.container.ContainerFactory;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.net.URI;
import com.sun.net.httpserver.HttpServer;

public final class HttpServerFactory
{
    private HttpServerFactory() {
    }
    
    public static HttpServer create(final String u) throws IOException, IllegalArgumentException {
        if (u == null) {
            throw new IllegalArgumentException("The URI must not be null");
        }
        return create(URI.create(u));
    }
    
    public static HttpServer create(final URI u) throws IOException, IllegalArgumentException {
        return create(u, ContainerFactory.createContainer(HttpHandler.class));
    }
    
    public static HttpServer create(final String u, final ResourceConfig rc) throws IOException, IllegalArgumentException {
        if (u == null) {
            throw new IllegalArgumentException("The URI must not be null");
        }
        return create(URI.create(u), rc);
    }
    
    public static HttpServer create(final URI u, final ResourceConfig rc) throws IOException, IllegalArgumentException {
        return create(u, ContainerFactory.createContainer(HttpHandler.class, rc));
    }
    
    public static HttpServer create(final String u, final ResourceConfig rc, final IoCComponentProviderFactory factory) throws IOException, IllegalArgumentException {
        if (u == null) {
            throw new IllegalArgumentException("The URI must not be null");
        }
        return create(URI.create(u), rc, factory);
    }
    
    public static HttpServer create(final URI u, final ResourceConfig rc, final IoCComponentProviderFactory factory) throws IOException, IllegalArgumentException {
        return create(u, ContainerFactory.createContainer(HttpHandler.class, rc, factory));
    }
    
    public static HttpServer create(final String u, final HttpHandler handler) throws IOException, IllegalArgumentException {
        if (u == null) {
            throw new IllegalArgumentException("The URI must not be null");
        }
        return create(URI.create(u), handler);
    }
    
    public static HttpServer create(final URI u, final HttpHandler handler) throws IOException, IllegalArgumentException {
        if (u == null) {
            throw new IllegalArgumentException("The URI must not be null");
        }
        final String scheme = u.getScheme();
        if (!scheme.equalsIgnoreCase("http") && !scheme.equalsIgnoreCase("https")) {
            throw new IllegalArgumentException("The URI scheme, of the URI " + u + ", must be equal (ignoring case) to 'http' or 'https'");
        }
        final String path = u.getPath();
        if (path == null) {
            throw new IllegalArgumentException("The URI path, of the URI " + u + ", must be non-null");
        }
        if (path.length() == 0) {
            throw new IllegalArgumentException("The URI path, of the URI " + u + ", must be present");
        }
        if (path.charAt(0) != '/') {
            throw new IllegalArgumentException("The URI path, of the URI " + u + ". must start with a '/'");
        }
        final int port = (u.getPort() == -1) ? 80 : u.getPort();
        final HttpServer server = scheme.equalsIgnoreCase("http") ? HttpServer.create(new InetSocketAddress(port), 0) : HttpsServer.create(new InetSocketAddress(port), 0);
        server.setExecutor(Executors.newCachedThreadPool());
        server.createContext(path, handler);
        return server;
    }
}
