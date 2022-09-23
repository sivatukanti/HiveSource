// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.spi.container.servlet;

import com.sun.jersey.spi.inject.SingletonTypeInjectableProvider;
import javax.ws.rs.core.MultivaluedMap;
import com.sun.jersey.spi.container.ContainerResponse;
import java.io.OutputStream;
import java.util.LinkedList;
import com.sun.jersey.core.header.InBoundHeaders;
import java.io.BufferedInputStream;
import com.sun.jersey.core.util.ReaderWriter;
import java.util.Arrays;
import com.sun.jersey.api.representation.Form;
import com.sun.jersey.core.header.MediaTypes;
import javax.ws.rs.core.MediaType;
import javax.naming.Context;
import javax.naming.NamingException;
import java.util.logging.Level;
import com.sun.jersey.server.impl.InitialContextHelper;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.lang.reflect.Constructor;
import java.security.PrivilegedActionException;
import com.sun.jersey.server.impl.application.DeferredResourceConfig;
import com.sun.jersey.api.core.ClasspathResourceConfig;
import java.security.AccessController;
import com.sun.jersey.core.reflection.ReflectionHelper;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.api.core.servlet.WebAppResourceConfig;
import com.sun.jersey.spi.container.ReloadListener;
import java.util.Map;
import com.sun.jersey.server.impl.monitoring.GlassFishMonitoringInitializer;
import com.sun.jersey.server.impl.managedbeans.ManagedBeanComponentProviderFactoryInitilizer;
import com.sun.jersey.server.impl.cdi.CDIComponentProviderFactoryInitializer;
import com.sun.jersey.server.impl.ejb.EJBComponentProviderFactoryInitilizer;
import com.sun.jersey.server.impl.container.servlet.JSPTemplateProcessor;
import javax.servlet.ServletContext;
import javax.ws.rs.core.GenericEntity;
import java.lang.reflect.Type;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import com.sun.jersey.spi.container.WebApplicationFactory;
import com.sun.jersey.api.core.TraceInformation;
import java.io.InputStream;
import java.io.IOException;
import javax.ws.rs.core.Response;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.api.container.ContainerException;
import com.sun.jersey.api.container.MappableContainerException;
import javax.ws.rs.WebApplicationException;
import com.sun.jersey.spi.container.ContainerResponseWriter;
import com.sun.jersey.server.probes.UriRuleProbeProvider;
import java.security.Principal;
import javax.ws.rs.core.SecurityContext;
import java.net.URI;
import javax.servlet.ServletException;
import java.util.Iterator;
import com.sun.jersey.spi.container.ContainerNotifier;
import java.util.List;
import com.sun.jersey.api.core.ApplicationAdapter;
import javax.ws.rs.core.Application;
import com.sun.jersey.spi.container.WebApplication;
import com.sun.jersey.api.core.ResourceConfig;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import com.sun.jersey.server.impl.ThreadLocalInvoker;
import java.util.logging.Logger;
import com.sun.jersey.spi.container.ContainerListener;

public class WebComponent implements ContainerListener
{
    public static final String APPLICATION_CONFIG_CLASS = "javax.ws.rs.Application";
    public static final String RESOURCE_CONFIG_CLASS = "com.sun.jersey.config.property.resourceConfigClass";
    public static final String JSP_TEMPLATES_BASE_PATH = "com.sun.jersey.config.property.JSPTemplatesBasePath";
    private static final Logger LOGGER;
    private final ThreadLocalInvoker<HttpServletRequest> requestInvoker;
    private final ThreadLocalInvoker<HttpServletResponse> responseInvoker;
    private WebConfig config;
    private ResourceConfig resourceConfig;
    private WebApplication application;
    
    public WebComponent() {
        this.requestInvoker = new ThreadLocalInvoker<HttpServletRequest>();
        this.responseInvoker = new ThreadLocalInvoker<HttpServletResponse>();
    }
    
    public WebComponent(final Application app) {
        this.requestInvoker = new ThreadLocalInvoker<HttpServletRequest>();
        this.responseInvoker = new ThreadLocalInvoker<HttpServletResponse>();
        if (app == null) {
            throw new IllegalArgumentException();
        }
        if (app instanceof ResourceConfig) {
            this.resourceConfig = (ResourceConfig)app;
        }
        else {
            this.resourceConfig = new ApplicationAdapter(app);
        }
    }
    
    public WebConfig getWebConfig() {
        return this.config;
    }
    
    public ResourceConfig getResourceConfig() {
        return this.resourceConfig;
    }
    
    public void init(final WebConfig webConfig) throws ServletException {
        this.config = webConfig;
        if (this.resourceConfig == null) {
            this.resourceConfig = this.createResourceConfig(this.config);
        }
        this.load();
        final Object o = this.resourceConfig.getProperties().get("com.sun.jersey.spi.container.ContainerNotifier");
        if (o instanceof List) {
            final List list = (List)o;
            for (final Object elem : list) {
                if (elem instanceof ContainerNotifier) {
                    final ContainerNotifier crf = (ContainerNotifier)elem;
                    crf.addListener(this);
                }
            }
        }
        else if (o instanceof ContainerNotifier) {
            final ContainerNotifier crf2 = (ContainerNotifier)o;
            crf2.addListener(this);
        }
    }
    
    public void destroy() {
        if (this.application != null) {
            this.application.destroy();
        }
    }
    
    public int service(final URI baseUri, final URI requestUri, final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        final WebApplication _application = this.application;
        final ContainerRequest cRequest = this.createRequest(_application, request, baseUri, requestUri);
        cRequest.setSecurityContext(new SecurityContext() {
            @Override
            public Principal getUserPrincipal() {
                return request.getUserPrincipal();
            }
            
            @Override
            public boolean isUserInRole(final String role) {
                return request.isUserInRole(role);
            }
            
            @Override
            public boolean isSecure() {
                return request.isSecure();
            }
            
            @Override
            public String getAuthenticationScheme() {
                return request.getAuthType();
            }
        });
        try {
            this.filterFormParameters(request, cRequest);
            UriRuleProbeProvider.requestStart(requestUri);
            this.requestInvoker.set(request);
            this.responseInvoker.set(response);
            final Writer w = new Writer(response);
            _application.handleRequest(cRequest, w);
            return w.cResponse.getStatus();
        }
        catch (WebApplicationException ex) {
            final Response exResponse = ex.getResponse();
            final String entity = (exResponse.getEntity() != null) ? exResponse.getEntity().toString() : null;
            response.sendError(exResponse.getStatus(), entity);
            return exResponse.getStatus();
        }
        catch (MappableContainerException ex2) {
            this.traceOnException(cRequest, response);
            throw new ServletException(ex2.getCause());
        }
        catch (ContainerException ex3) {
            this.traceOnException(cRequest, response);
            throw new ServletException(ex3);
        }
        catch (RuntimeException ex4) {
            this.traceOnException(cRequest, response);
            throw ex4;
        }
        finally {
            UriRuleProbeProvider.requestEnd();
            this.requestInvoker.set(null);
            this.responseInvoker.set(null);
        }
    }
    
    protected ContainerRequest createRequest(final WebApplication app, final HttpServletRequest request, final URI baseUri, final URI requestUri) throws IOException {
        return new ContainerRequest(app, request.getMethod(), baseUri, requestUri, this.getHeaders(request), request.getInputStream());
    }
    
    private void traceOnException(final ContainerRequest cRequest, final HttpServletResponse response) {
        if (cRequest.isTracingEnabled()) {
            final TraceInformation ti = cRequest.getProperties().get(TraceInformation.class.getName());
            ti.addTraceHeaders(new TraceInformation.TraceHeaderListener() {
                @Override
                public void onHeader(final String name, final String value) {
                    response.addHeader(name, value);
                }
            });
        }
    }
    
    protected WebApplication create() {
        return WebApplicationFactory.createWebApplication();
    }
    
    protected void configure(final WebConfig wc, final ResourceConfig rc, final WebApplication wa) {
        this.configureJndiResources(rc);
        rc.getSingletons().add(new ContextInjectableProvider(HttpServletRequest.class, Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[] { HttpServletRequest.class }, this.requestInvoker)));
        rc.getSingletons().add(new ContextInjectableProvider(HttpServletResponse.class, Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[] { HttpServletResponse.class }, this.responseInvoker)));
        final GenericEntity<ThreadLocal<HttpServletRequest>> requestThreadLocal = new GenericEntity<ThreadLocal<HttpServletRequest>>(this.requestInvoker.getImmutableThreadLocal()) {};
        rc.getSingletons().add(new ContextInjectableProvider(requestThreadLocal.getType(), requestThreadLocal.getEntity()));
        final GenericEntity<ThreadLocal<HttpServletResponse>> responseThreadLocal = new GenericEntity<ThreadLocal<HttpServletResponse>>(this.responseInvoker.getImmutableThreadLocal()) {};
        rc.getSingletons().add(new ContextInjectableProvider(responseThreadLocal.getType(), responseThreadLocal.getEntity()));
        rc.getSingletons().add(new ContextInjectableProvider(ServletContext.class, wc.getServletContext()));
        rc.getSingletons().add(new ContextInjectableProvider(WebConfig.class, wc));
        rc.getClasses().add(JSPTemplateProcessor.class);
        EJBComponentProviderFactoryInitilizer.initialize(rc);
        CDIComponentProviderFactoryInitializer.initialize(wc, rc, wa);
        ManagedBeanComponentProviderFactoryInitilizer.initialize(rc);
        GlassFishMonitoringInitializer.initialize();
    }
    
    protected void initiate(final ResourceConfig rc, final WebApplication wa) {
        wa.initiate(rc);
    }
    
    public void load() {
        final WebApplication _application = this.create();
        this.configure(this.config, this.resourceConfig, _application);
        this.initiate(this.resourceConfig, _application);
        this.application = _application;
    }
    
    protected ResourceConfig getDefaultResourceConfig(final Map<String, Object> props, final WebConfig wc) throws ServletException {
        return this.getWebAppResourceConfig(props, wc);
    }
    
    @Override
    public void onReload() {
        final WebApplication oldApplication = this.application;
        final WebApplication newApplication = this.create();
        this.initiate(this.resourceConfig, newApplication);
        this.application = newApplication;
        if (this.resourceConfig instanceof ReloadListener) {
            ((ReloadListener)this.resourceConfig).onReload();
        }
        oldApplication.destroy();
    }
    
    ResourceConfig getWebAppResourceConfig(final Map<String, Object> props, final WebConfig webConfig) throws ServletException {
        return new WebAppResourceConfig(props, webConfig.getServletContext());
    }
    
    private ResourceConfig createResourceConfig(final WebConfig webConfig) throws ServletException {
        final Map<String, Object> props = this.getInitParams(webConfig);
        final ResourceConfig rc = this.createResourceConfig(webConfig, props);
        rc.setPropertiesAndFeatures(props);
        return rc;
    }
    
    private ResourceConfig createResourceConfig(final WebConfig webConfig, final Map<String, Object> props) throws ServletException {
        String resourceConfigClassName = webConfig.getInitParameter("com.sun.jersey.config.property.resourceConfigClass");
        if (resourceConfigClassName == null) {
            resourceConfigClassName = webConfig.getInitParameter("javax.ws.rs.Application");
        }
        if (resourceConfigClassName == null) {
            final String packages = webConfig.getInitParameter("com.sun.jersey.config.property.packages");
            if (packages != null) {
                props.put("com.sun.jersey.config.property.packages", packages);
                return new PackagesResourceConfig(props);
            }
            final ResourceConfig defaultConfig = webConfig.getDefaultResourceConfig(props);
            if (defaultConfig != null) {
                return defaultConfig;
            }
            return this.getDefaultResourceConfig(props, webConfig);
        }
        else {
            try {
                final Class<?> resourceConfigClass = AccessController.doPrivileged(ReflectionHelper.classForNameWithExceptionPEA(resourceConfigClassName));
                if (resourceConfigClass == ClasspathResourceConfig.class) {
                    final String[] paths = this.getPaths(webConfig.getInitParameter("com.sun.jersey.config.property.classpath"));
                    props.put("com.sun.jersey.config.property.classpath", paths);
                    return new ClasspathResourceConfig(props);
                }
                if (ResourceConfig.class.isAssignableFrom(resourceConfigClass)) {
                    try {
                        final Constructor constructor = resourceConfigClass.getConstructor(Map.class);
                        if (ClasspathResourceConfig.class.isAssignableFrom(resourceConfigClass)) {
                            final String[] paths2 = this.getPaths(webConfig.getInitParameter("com.sun.jersey.config.property.classpath"));
                            props.put("com.sun.jersey.config.property.classpath", paths2);
                        }
                        return constructor.newInstance(props);
                    }
                    catch (NoSuchMethodException ex) {
                        return new DeferredResourceConfig(resourceConfigClass.asSubclass(ResourceConfig.class));
                    }
                    catch (Exception ex2) {}
                }
                if (Application.class.isAssignableFrom(resourceConfigClass)) {
                    return new DeferredResourceConfig(resourceConfigClass.asSubclass(Application.class));
                }
                final String message = "Resource configuration class, " + resourceConfigClassName + ", is not a super class of " + Application.class;
                throw new ServletException(message);
            }
            catch (ClassNotFoundException e) {
                final String message = "Resource configuration class, " + resourceConfigClassName + ", could not be loaded";
                throw new ServletException(message, e);
            }
            catch (PrivilegedActionException e2) {
                final String message = "Resource configuration class, " + resourceConfigClassName + ", could not be loaded";
                throw new ServletException(message, e2.getCause());
            }
        }
    }
    
    private Map<String, Object> getInitParams(final WebConfig webConfig) {
        final Map<String, Object> props = new HashMap<String, Object>();
        final Enumeration names = webConfig.getInitParameterNames();
        while (names.hasMoreElements()) {
            final String name = names.nextElement();
            props.put(name, webConfig.getInitParameter(name));
        }
        return props;
    }
    
    private String[] getPaths(final String classpath) throws ServletException {
        final ServletContext context = this.config.getServletContext();
        if (classpath == null) {
            final String[] paths = { context.getRealPath("/WEB-INF/lib"), context.getRealPath("/WEB-INF/classes") };
            if (paths[0] == null && paths[1] == null) {
                final String message = "The default deployment configuration that scans for classes in /WEB-INF/lib and /WEB-INF/classes is not supported for the application server.Try using the package scanning configuration, see the JavaDoc for " + PackagesResourceConfig.class.getName() + " and the property " + "com.sun.jersey.config.property.packages" + ".";
                throw new ServletException(message);
            }
            return paths;
        }
        else {
            final String[] virtualPaths = classpath.split(";");
            final List<String> resourcePaths = new ArrayList<String>();
            for (String virtualPath : virtualPaths) {
                virtualPath = virtualPath.trim();
                if (virtualPath.length() != 0) {
                    final String path = context.getRealPath(virtualPath);
                    if (path != null) {
                        resourcePaths.add(path);
                    }
                }
            }
            if (resourcePaths.isEmpty()) {
                final String message2 = "None of the declared classpath locations, " + classpath + ", could be resolved. " + "This could be because the default deployment configuration that scans for " + "classes in classpath locations is not supported. " + "Try using the package scanning configuration, see the JavaDoc for " + PackagesResourceConfig.class.getName() + " and the property " + "com.sun.jersey.config.property.packages" + ".";
                throw new ServletException(message2);
            }
            return resourcePaths.toArray(new String[resourcePaths.size()]);
        }
    }
    
    private void configureJndiResources(final ResourceConfig rc) {
        final Context x = InitialContextHelper.getInitialContext();
        if (x != null) {
            final Iterator<Class<?>> i = rc.getClasses().iterator();
            while (i.hasNext()) {
                final Class<?> c = i.next();
                if (!c.isInterface()) {
                    continue;
                }
                try {
                    final Object o = x.lookup(c.getName());
                    if (o == null) {
                        continue;
                    }
                    i.remove();
                    rc.getSingletons().add(o);
                    WebComponent.LOGGER.log(Level.INFO, "An instance of the class " + c.getName() + " is found by JNDI look up using the class name as the JNDI name. " + "The instance will be registered as a singleton.");
                }
                catch (NamingException ex) {
                    WebComponent.LOGGER.log(Level.CONFIG, "JNDI lookup failed for Jersey application resource " + c.getName(), ex);
                }
            }
        }
    }
    
    private void filterFormParameters(final HttpServletRequest servletRequest, final ContainerRequest containerRequet) throws IOException {
        if (MediaTypes.typeEquals(MediaType.APPLICATION_FORM_URLENCODED_TYPE, containerRequet.getMediaType()) && !this.isEntityPresent(containerRequet)) {
            final Form f = new Form();
            final Enumeration e = servletRequest.getParameterNames();
            while (e.hasMoreElements()) {
                final String name = e.nextElement();
                final String[] values = servletRequest.getParameterValues(name);
                f.put(name, Arrays.asList(values));
            }
            if (!f.isEmpty()) {
                containerRequet.getProperties().put("com.sun.jersey.api.representation.form", f);
                if (WebComponent.LOGGER.isLoggable(Level.WARNING)) {
                    WebComponent.LOGGER.log(Level.WARNING, "A servlet request, to the URI " + containerRequet.getRequestUri() + ", " + "contains form parameters in " + "the request body but the request body has been consumed " + "by the servlet or a servlet filter accessing the request " + "parameters. Only resource methods using @FormParam " + "will work as expected. Resource methods consuming the " + "request body by other means will not work as expected.");
                }
            }
        }
    }
    
    private boolean isEntityPresent(final ContainerRequest cr) throws IOException {
        InputStream in = cr.getEntityInputStream();
        if (!in.markSupported()) {
            in = new BufferedInputStream(in, ReaderWriter.BUFFER_SIZE);
            cr.setEntityInputStream(in);
        }
        in.mark(1);
        if (in.read() == -1) {
            return false;
        }
        in.reset();
        return true;
    }
    
    private InBoundHeaders getHeaders(final HttpServletRequest request) {
        final InBoundHeaders rh = new InBoundHeaders();
        final Enumeration<String> names = request.getHeaderNames();
        while (names.hasMoreElements()) {
            final String name = names.nextElement();
            final List<String> valueList = new LinkedList<String>();
            final Enumeration<String> values = request.getHeaders(name);
            while (values.hasMoreElements()) {
                valueList.add(values.nextElement());
            }
            rh.put(name, (List<V>)valueList);
        }
        return rh;
    }
    
    static {
        LOGGER = Logger.getLogger(WebComponent.class.getName());
    }
    
    private static final class Writer extends OutputStream implements ContainerResponseWriter
    {
        final HttpServletResponse response;
        ContainerResponse cResponse;
        long contentLength;
        OutputStream out;
        boolean statusAndHeadersWritten;
        
        Writer(final HttpServletResponse response) {
            this.statusAndHeadersWritten = false;
            this.response = response;
        }
        
        @Override
        public OutputStream writeStatusAndHeaders(final long contentLength, final ContainerResponse cResponse) throws IOException {
            this.contentLength = contentLength;
            this.cResponse = cResponse;
            this.statusAndHeadersWritten = false;
            return this;
        }
        
        @Override
        public void finish() throws IOException {
            if (this.statusAndHeadersWritten) {
                return;
            }
            this.writeHeaders();
            this.writeStatus();
        }
        
        private void writeStatus() {
            final Response.StatusType statusType = this.cResponse.getStatusType();
            final String reasonPhrase = statusType.getReasonPhrase();
            if (reasonPhrase != null) {
                this.response.setStatus(statusType.getStatusCode(), reasonPhrase);
            }
            else {
                this.response.setStatus(statusType.getStatusCode());
            }
        }
        
        @Override
        public void write(final int b) throws IOException {
            this.initiate();
            this.out.write(b);
        }
        
        @Override
        public void write(final byte[] b) throws IOException {
            if (b.length > 0) {
                this.initiate();
                this.out.write(b);
            }
        }
        
        @Override
        public void write(final byte[] b, final int off, final int len) throws IOException {
            if (len > 0) {
                this.initiate();
                this.out.write(b, off, len);
            }
        }
        
        @Override
        public void flush() throws IOException {
            this.writeStatusAndHeaders();
            if (this.out != null) {
                this.out.flush();
            }
        }
        
        @Override
        public void close() throws IOException {
            this.initiate();
            this.out.close();
        }
        
        void initiate() throws IOException {
            if (this.out == null) {
                this.writeStatusAndHeaders();
                this.out = this.response.getOutputStream();
            }
        }
        
        void writeStatusAndHeaders() {
            if (this.statusAndHeadersWritten) {
                return;
            }
            this.writeHeaders();
            this.writeStatus();
            this.statusAndHeadersWritten = true;
        }
        
        void writeHeaders() {
            if (this.contentLength != -1L && this.contentLength < 2147483647L) {
                this.response.setContentLength((int)this.contentLength);
            }
            final MultivaluedMap<String, Object> headers = this.cResponse.getHttpHeaders();
            for (final Map.Entry<String, List<Object>> e : headers.entrySet()) {
                for (final Object v : e.getValue()) {
                    this.response.addHeader(e.getKey(), ContainerResponse.getHeaderValue(v));
                }
            }
        }
    }
    
    protected static class ContextInjectableProvider<T> extends SingletonTypeInjectableProvider<javax.ws.rs.core.Context, T>
    {
        protected ContextInjectableProvider(final Type type, final T instance) {
            super(type, instance);
        }
    }
}
