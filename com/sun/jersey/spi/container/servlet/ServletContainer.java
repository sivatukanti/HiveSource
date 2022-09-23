// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.spi.container.servlet;

import javax.ws.rs.core.Context;
import com.sun.jersey.spi.inject.SingletonTypeInjectableProvider;
import javax.servlet.FilterChain;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import java.util.regex.PatternSyntaxException;
import com.sun.jersey.api.container.ContainerException;
import com.sun.jersey.api.uri.UriComponent;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.lang.reflect.Type;
import javax.servlet.ServletConfig;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.Iterator;
import com.sun.jersey.spi.service.ServiceFinder;
import java.util.List;
import com.sun.jersey.spi.container.ContainerNotifier;
import java.util.ArrayList;
import com.sun.jersey.spi.container.ReloadListener;
import com.sun.jersey.api.core.ResourceConfig;
import java.util.Map;
import com.sun.jersey.spi.container.WebApplicationFactory;
import com.sun.jersey.spi.container.WebApplication;
import javax.servlet.ServletException;
import javax.servlet.ServletContext;
import com.sun.jersey.server.impl.application.DeferredResourceConfig;
import javax.ws.rs.core.Application;
import java.util.regex.Pattern;
import javax.servlet.FilterConfig;
import javax.servlet.Filter;
import javax.servlet.http.HttpServlet;

public class ServletContainer extends HttpServlet implements Filter
{
    public static final String GLASSFISH_DEFAULT_ERROR_PAGE_RESPONSE = "org.glassfish.web.isDefaultErrorPageEnabled";
    public static final String APPLICATION_CONFIG_CLASS = "javax.ws.rs.Application";
    public static final String RESOURCE_CONFIG_CLASS = "com.sun.jersey.config.property.resourceConfigClass";
    public static final String JSP_TEMPLATES_BASE_PATH = "com.sun.jersey.config.property.JSPTemplatesBasePath";
    public static final String PROPERTY_WEB_PAGE_CONTENT_REGEX = "com.sun.jersey.config.property.WebPageContentRegex";
    public static final String FEATURE_FILTER_FORWARD_ON_404 = "com.sun.jersey.config.feature.FilterForwardOn404";
    public static final String PROPERTY_FILTER_CONTEXT_PATH = "com.sun.jersey.config.feature.FilterContextPath";
    public static final String FEATURE_ALLOW_RAW_MANAGED_BEANS = "com.sun.jersey.config.feature.AllowRawManagedBeans";
    private transient WebComponent webComponent;
    private transient FilterConfig filterConfig;
    private transient Pattern staticContentPattern;
    private transient boolean forwardOn404;
    private final transient Application app;
    private String filterContextPath;
    
    public ServletContainer() {
        this.filterContextPath = null;
        this.app = null;
    }
    
    public ServletContainer(final Class<? extends Application> appClass) {
        this.filterContextPath = null;
        this.app = new DeferredResourceConfig(appClass);
    }
    
    public ServletContainer(final Application app) {
        this.filterContextPath = null;
        this.app = app;
    }
    
    @Override
    public ServletContext getServletContext() {
        if (this.filterConfig != null) {
            return this.filterConfig.getServletContext();
        }
        return super.getServletContext();
    }
    
    protected void init(final WebConfig webConfig) throws ServletException {
        (this.webComponent = ((this.app == null) ? new InternalWebComponent() : new InternalWebComponent(this.app))).init(webConfig);
    }
    
    protected WebConfig getWebConfig() {
        return this.webComponent.getWebConfig();
    }
    
    protected WebApplication create() {
        return WebApplicationFactory.createWebApplication();
    }
    
    protected ResourceConfig getDefaultResourceConfig(final Map<String, Object> props, final WebConfig wc) throws ServletException {
        return this.webComponent.getWebAppResourceConfig(props, wc);
    }
    
    protected void configure(final WebConfig wc, final ResourceConfig rc, final WebApplication wa) {
        if (this.getServletConfig() != null) {
            this.configure(this.getServletConfig(), rc, wa);
        }
        else if (this.filterConfig != null) {
            this.configure(this.filterConfig, rc, wa);
        }
        if (rc instanceof ReloadListener) {
            final List<ContainerNotifier> notifiers = new ArrayList<ContainerNotifier>();
            final Object o = rc.getProperties().get("com.sun.jersey.spi.container.ContainerNotifier");
            if (o instanceof ContainerNotifier) {
                notifiers.add((ContainerNotifier)o);
            }
            else if (o instanceof List) {
                for (final Object elem : (List)o) {
                    if (elem instanceof ContainerNotifier) {
                        notifiers.add((ContainerNotifier)elem);
                    }
                }
            }
            for (final ContainerNotifier cn : ServiceFinder.find(ContainerNotifier.class)) {
                notifiers.add(cn);
            }
            rc.getProperties().put("com.sun.jersey.spi.container.ContainerNotifier", notifiers);
        }
    }
    
    protected void initiate(final ResourceConfig rc, final WebApplication wa) {
        wa.initiate(rc);
    }
    
    public void load() {
        this.webComponent.load();
    }
    
    public void reload() {
        this.webComponent.onReload();
    }
    
    public int service(final URI baseUri, final URI requestUri, final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        return this.webComponent.service(baseUri, requestUri, request, response);
    }
    
    @Override
    public void destroy() {
        if (this.webComponent != null) {
            this.webComponent.destroy();
        }
    }
    
    @Override
    public void init() throws ServletException {
        this.init(new WebServletConfig(this));
    }
    
    @Deprecated
    protected ResourceConfig getDefaultResourceConfig(final Map<String, Object> props, final ServletConfig servletConfig) throws ServletException {
        return this.getDefaultResourceConfig(props, this.getWebConfig());
    }
    
    protected void configure(final ServletConfig sc, final ResourceConfig rc, final WebApplication wa) {
        rc.getSingletons().add(new ContextInjectableProvider(ServletConfig.class, sc));
    }
    
    public void service(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        final String servletPath = request.getServletPath();
        final String pathInfo = request.getPathInfo();
        final StringBuffer requestURL = request.getRequestURL();
        String requestURI = request.getRequestURI();
        final boolean checkPathInfo = pathInfo == null || pathInfo.isEmpty() || pathInfo.equals("/");
        UriBuilder absoluteUriBuilder;
        try {
            absoluteUriBuilder = UriBuilder.fromUri(requestURL.toString());
        }
        catch (IllegalArgumentException iae) {
            final Response.Status badRequest = Response.Status.BAD_REQUEST;
            response.sendError(badRequest.getStatusCode(), badRequest.getReasonPhrase());
            return;
        }
        if (checkPathInfo && !request.getRequestURI().endsWith("/")) {
            final int i = servletPath.lastIndexOf("/");
            if (servletPath.substring(i + 1).indexOf(46) < 0) {
                if (this.webComponent.getResourceConfig().getFeature("com.sun.jersey.config.feature.Redirect")) {
                    final URI l = absoluteUriBuilder.path("/").replaceQuery(request.getQueryString()).build(new Object[0]);
                    response.setStatus(307);
                    response.setHeader("Location", l.toASCIIString());
                    return;
                }
                requestURL.append("/");
                requestURI += "/";
            }
        }
        final String decodedBasePath = request.getContextPath() + servletPath + "/";
        final String encodedBasePath = UriComponent.encode(decodedBasePath, UriComponent.Type.PATH);
        if (!decodedBasePath.equals(encodedBasePath)) {
            throw new ContainerException("The servlet context path and/or the servlet path contain characters that are percent encoded");
        }
        final URI baseUri = absoluteUriBuilder.replacePath(encodedBasePath).build(new Object[0]);
        String queryParameters = request.getQueryString();
        if (queryParameters == null) {
            queryParameters = "";
        }
        final URI requestUri = absoluteUriBuilder.replacePath(requestURI).replaceQuery(queryParameters).build(new Object[0]);
        this.service(baseUri, requestUri, request, response);
    }
    
    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
        this.init(new WebFilterConfig(filterConfig));
    }
    
    public Pattern getStaticContentPattern() {
        return this.staticContentPattern;
    }
    
    protected void configure(final FilterConfig fc, final ResourceConfig rc, final WebApplication wa) {
        rc.getSingletons().add(new ContextInjectableProvider(FilterConfig.class, fc));
        final String regex = (String)rc.getProperty("com.sun.jersey.config.property.WebPageContentRegex");
        if (regex != null && regex.length() > 0) {
            try {
                this.staticContentPattern = Pattern.compile(regex);
            }
            catch (PatternSyntaxException ex) {
                throw new ContainerException("The syntax is invalid for the regular expression, " + regex + ", associated with the initialization parameter " + "com.sun.jersey.config.property.WebPageContentRegex", ex);
            }
        }
        this.forwardOn404 = rc.getFeature("com.sun.jersey.config.feature.FilterForwardOn404");
        this.filterContextPath = this.filterConfig.getInitParameter("com.sun.jersey.config.feature.FilterContextPath");
        if (this.filterContextPath != null) {
            if (this.filterContextPath.isEmpty()) {
                this.filterContextPath = null;
            }
            else {
                if (!this.filterContextPath.startsWith("/")) {
                    this.filterContextPath = '/' + this.filterContextPath;
                }
                if (this.filterContextPath.endsWith("/")) {
                    this.filterContextPath = this.filterContextPath.substring(0, this.filterContextPath.length() - 1);
                }
            }
        }
    }
    
    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
        try {
            this.doFilter((HttpServletRequest)request, (HttpServletResponse)response, chain);
        }
        catch (ClassCastException e) {
            throw new ServletException("non-HTTP request or response");
        }
    }
    
    public void doFilter(final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain) throws IOException, ServletException {
        if (request.getAttribute("javax.servlet.include.request_uri") != null) {
            final String includeRequestURI = (String)request.getAttribute("javax.servlet.include.request_uri");
            if (!includeRequestURI.equals(request.getRequestURI())) {
                this.doFilter(request, response, chain, includeRequestURI, (String)request.getAttribute("javax.servlet.include.servlet_path"), (String)request.getAttribute("javax.servlet.include.query_string"));
                return;
            }
        }
        final String servletPath = request.getServletPath() + ((request.getPathInfo() == null) ? "" : request.getPathInfo());
        this.doFilter(request, response, chain, request.getRequestURI(), servletPath, request.getQueryString());
    }
    
    private void doFilter(final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain, String requestURI, final String servletPath, final String queryString) throws IOException, ServletException {
        final Pattern p = this.getStaticContentPattern();
        if (p != null && p.matcher(servletPath).matches()) {
            chain.doFilter(request, response);
            return;
        }
        if (this.filterContextPath != null) {
            if (!servletPath.startsWith(this.filterContextPath)) {
                throw new ContainerException("The servlet path, \"" + servletPath + "\", does not start with the filter context path, \"" + this.filterContextPath + "\"");
            }
            if (servletPath.length() == this.filterContextPath.length()) {
                if (this.webComponent.getResourceConfig().getFeature("com.sun.jersey.config.feature.Redirect")) {
                    final URI l = UriBuilder.fromUri(request.getRequestURL().toString()).path("/").replaceQuery(queryString).build(new Object[0]);
                    response.setStatus(307);
                    response.setHeader("Location", l.toASCIIString());
                    return;
                }
                requestURI += "/";
            }
        }
        final UriBuilder absoluteUriBuilder = UriBuilder.fromUri(request.getRequestURL().toString());
        final URI baseUri = (this.filterContextPath == null) ? absoluteUriBuilder.replacePath(request.getContextPath()).path("/").build(new Object[0]) : absoluteUriBuilder.replacePath(request.getContextPath()).path(this.filterContextPath).path("/").build(new Object[0]);
        final URI requestUri = absoluteUriBuilder.replacePath(requestURI).replaceQuery(queryString).build(new Object[0]);
        final int status = this.service(baseUri, requestUri, request, response);
        if (this.forwardOn404 && status == 404 && !response.isCommitted()) {
            response.setStatus(200);
            chain.doFilter(request, response);
        }
    }
    
    protected static class ContextInjectableProvider<T> extends SingletonTypeInjectableProvider<Context, T>
    {
        protected ContextInjectableProvider(final Type type, final T instance) {
            super(type, instance);
        }
    }
    
    private class InternalWebComponent extends WebComponent
    {
        InternalWebComponent() {
        }
        
        InternalWebComponent(final Application app) {
            super(app);
        }
        
        @Override
        protected WebApplication create() {
            return ServletContainer.this.create();
        }
        
        @Override
        protected void configure(final WebConfig wc, final ResourceConfig rc, final WebApplication wa) {
            super.configure(wc, rc, wa);
            ServletContainer.this.configure(wc, rc, wa);
        }
        
        @Override
        protected void initiate(final ResourceConfig rc, final WebApplication wa) {
            ServletContainer.this.initiate(rc, wa);
        }
        
        @Override
        protected ResourceConfig getDefaultResourceConfig(final Map<String, Object> props, final WebConfig wc) throws ServletException {
            return ServletContainer.this.getDefaultResourceConfig(props, wc);
        }
    }
}
