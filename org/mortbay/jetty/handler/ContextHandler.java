// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.jetty.handler;

import javax.servlet.ServletContextAttributeEvent;
import javax.servlet.Servlet;
import java.io.InputStream;
import javax.servlet.RequestDispatcher;
import org.mortbay.io.Buffer;
import java.net.MalformedURLException;
import java.util.Locale;
import org.mortbay.util.Loader;
import org.mortbay.jetty.webapp.WebAppClassLoader;
import javax.servlet.ServletException;
import javax.servlet.ServletRequestEvent;
import org.mortbay.jetty.HttpException;
import org.mortbay.util.URIUtil;
import org.mortbay.jetty.HttpConnection;
import org.mortbay.jetty.Request;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.mortbay.util.QuotedStringTokenizer;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletRequestAttributeListener;
import javax.servlet.ServletRequestListener;
import javax.servlet.ServletContextAttributeListener;
import org.mortbay.util.LazyList;
import javax.servlet.ServletContextListener;
import java.util.Collections;
import java.net.URL;
import java.io.IOException;
import org.mortbay.log.Log;
import java.io.File;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.Collection;
import java.util.HashSet;
import java.util.Arrays;
import org.mortbay.jetty.Handler;
import org.mortbay.jetty.HandlerContainer;
import java.util.HashMap;
import org.mortbay.log.Logger;
import java.util.EventListener;
import java.util.Set;
import org.mortbay.jetty.MimeTypes;
import org.mortbay.resource.Resource;
import java.util.Map;
import org.mortbay.util.AttributesMap;
import org.mortbay.jetty.Server;
import org.mortbay.util.Attributes;

public class ContextHandler extends HandlerWrapper implements Attributes, Server.Graceful
{
    private static ThreadLocal __context;
    public static final String MANAGED_ATTRIBUTES = "org.mortbay.jetty.servlet.ManagedAttributes";
    protected SContext _scontext;
    private AttributesMap _attributes;
    private AttributesMap _contextAttributes;
    private ClassLoader _classLoader;
    private String _contextPath;
    private Map _initParams;
    private String _displayName;
    private Resource _baseResource;
    private MimeTypes _mimeTypes;
    private Map _localeEncodingMap;
    private String[] _welcomeFiles;
    private ErrorHandler _errorHandler;
    private String[] _vhosts;
    private Set _connectors;
    private EventListener[] _eventListeners;
    private Logger _logger;
    private boolean _shutdown;
    private boolean _allowNullPathInfo;
    private int _maxFormContentSize;
    private boolean _compactPath;
    private Object _contextListeners;
    private Object _contextAttributeListeners;
    private Object _requestListeners;
    private Object _requestAttributeListeners;
    private Set _managedAttributes;
    
    public static SContext getCurrentContext() {
        final SContext context = ContextHandler.__context.get();
        return context;
    }
    
    public ContextHandler() {
        this._contextPath = "/";
        this._maxFormContentSize = Integer.getInteger("org.mortbay.jetty.Request.maxFormContentSize", 200000);
        this._compactPath = false;
        this._scontext = new SContext();
        this._attributes = new AttributesMap();
        this._initParams = new HashMap();
    }
    
    protected ContextHandler(final SContext context) {
        this._contextPath = "/";
        this._maxFormContentSize = Integer.getInteger("org.mortbay.jetty.Request.maxFormContentSize", 200000);
        this._compactPath = false;
        this._scontext = context;
        this._attributes = new AttributesMap();
        this._initParams = new HashMap();
    }
    
    public ContextHandler(final String contextPath) {
        this();
        this.setContextPath(contextPath);
    }
    
    public ContextHandler(final HandlerContainer parent, final String contextPath) {
        this();
        this.setContextPath(contextPath);
        parent.addHandler(this);
    }
    
    public SContext getServletContext() {
        return this._scontext;
    }
    
    public boolean getAllowNullPathInfo() {
        return this._allowNullPathInfo;
    }
    
    public void setAllowNullPathInfo(final boolean allowNullPathInfo) {
        this._allowNullPathInfo = allowNullPathInfo;
    }
    
    public void setServer(final Server server) {
        if (this._errorHandler != null) {
            final Server old_server = this.getServer();
            if (old_server != null && old_server != server) {
                old_server.getContainer().update(this, this._errorHandler, null, "error", true);
            }
            super.setServer(server);
            if (server != null && server != old_server) {
                server.getContainer().update(this, null, this._errorHandler, "error", true);
            }
            this._errorHandler.setServer(server);
        }
        else {
            super.setServer(server);
        }
    }
    
    public void setVirtualHosts(final String[] vhosts) {
        if (vhosts == null) {
            this._vhosts = vhosts;
        }
        else {
            this._vhosts = new String[vhosts.length];
            for (int i = 0; i < vhosts.length; ++i) {
                this._vhosts[i] = this.normalizeHostname(vhosts[i]);
            }
        }
    }
    
    public String[] getVirtualHosts() {
        return this._vhosts;
    }
    
    public void setHosts(final String[] hosts) {
        this.setConnectorNames(hosts);
    }
    
    public String[] getHosts() {
        return this.getConnectorNames();
    }
    
    public String[] getConnectorNames() {
        if (this._connectors == null || this._connectors.size() == 0) {
            return null;
        }
        return this._connectors.toArray(new String[this._connectors.size()]);
    }
    
    public void setConnectorNames(final String[] connectors) {
        if (connectors == null || connectors.length == 0) {
            this._connectors = null;
        }
        else {
            this._connectors = new HashSet(Arrays.asList(connectors));
        }
    }
    
    public Object getAttribute(final String name) {
        return this._attributes.getAttribute(name);
    }
    
    public Enumeration getAttributeNames() {
        return AttributesMap.getAttributeNamesCopy(this._attributes);
    }
    
    public Attributes getAttributes() {
        return this._attributes;
    }
    
    public ClassLoader getClassLoader() {
        return this._classLoader;
    }
    
    public String getClassPath() {
        if (this._classLoader == null || !(this._classLoader instanceof URLClassLoader)) {
            return null;
        }
        final URLClassLoader loader = (URLClassLoader)this._classLoader;
        final URL[] urls = loader.getURLs();
        final StringBuffer classpath = new StringBuffer();
        for (int i = 0; i < urls.length; ++i) {
            try {
                final Resource resource = Resource.newResource(urls[i]);
                final File file = resource.getFile();
                if (file.exists()) {
                    if (classpath.length() > 0) {
                        classpath.append(File.pathSeparatorChar);
                    }
                    classpath.append(file.getAbsolutePath());
                }
            }
            catch (IOException e) {
                Log.debug(e);
            }
        }
        if (classpath.length() == 0) {
            return null;
        }
        return classpath.toString();
    }
    
    public String getContextPath() {
        return this._contextPath;
    }
    
    public String getInitParameter(final String name) {
        return this._initParams.get(name);
    }
    
    public Enumeration getInitParameterNames() {
        return Collections.enumeration((Collection<Object>)this._initParams.keySet());
    }
    
    public Map getInitParams() {
        return this._initParams;
    }
    
    public String getDisplayName() {
        return this._displayName;
    }
    
    public EventListener[] getEventListeners() {
        return this._eventListeners;
    }
    
    public void setEventListeners(final EventListener[] eventListeners) {
        this._contextListeners = null;
        this._contextAttributeListeners = null;
        this._requestListeners = null;
        this._requestAttributeListeners = null;
        this._eventListeners = eventListeners;
        for (int i = 0; eventListeners != null && i < eventListeners.length; ++i) {
            final EventListener listener = this._eventListeners[i];
            if (listener instanceof ServletContextListener) {
                this._contextListeners = LazyList.add(this._contextListeners, listener);
            }
            if (listener instanceof ServletContextAttributeListener) {
                this._contextAttributeListeners = LazyList.add(this._contextAttributeListeners, listener);
            }
            if (listener instanceof ServletRequestListener) {
                this._requestListeners = LazyList.add(this._requestListeners, listener);
            }
            if (listener instanceof ServletRequestAttributeListener) {
                this._requestAttributeListeners = LazyList.add(this._requestAttributeListeners, listener);
            }
        }
    }
    
    public void addEventListener(final EventListener listener) {
        this.setEventListeners((EventListener[])LazyList.addToArray(this.getEventListeners(), listener, EventListener.class));
    }
    
    public boolean isShutdown() {
        return !this._shutdown;
    }
    
    public void setShutdown(final boolean shutdown) {
        this._shutdown = shutdown;
    }
    
    protected void doStart() throws Exception {
        if (this._contextPath == null) {
            throw new IllegalStateException("Null contextPath");
        }
        this._logger = Log.getLogger((this.getDisplayName() == null) ? this.getContextPath() : this.getDisplayName());
        ClassLoader old_classloader = null;
        Thread current_thread = null;
        SContext old_context = null;
        this._contextAttributes = new AttributesMap();
        try {
            if (this._classLoader != null) {
                current_thread = Thread.currentThread();
                old_classloader = current_thread.getContextClassLoader();
                current_thread.setContextClassLoader(this._classLoader);
            }
            if (this._mimeTypes == null) {
                this._mimeTypes = new MimeTypes();
            }
            old_context = ContextHandler.__context.get();
            ContextHandler.__context.set(this._scontext);
            if (this._errorHandler == null) {
                this.setErrorHandler(new ErrorHandler());
            }
            this.startContext();
        }
        finally {
            ContextHandler.__context.set(old_context);
            if (this._classLoader != null) {
                current_thread.setContextClassLoader(old_classloader);
            }
        }
    }
    
    protected void startContext() throws Exception {
        super.doStart();
        if (this._errorHandler != null) {
            this._errorHandler.start();
        }
        if (this._contextListeners != null) {
            final ServletContextEvent event = new ServletContextEvent(this._scontext);
            for (int i = 0; i < LazyList.size(this._contextListeners); ++i) {
                ((ServletContextListener)LazyList.get(this._contextListeners, i)).contextInitialized(event);
            }
        }
        final String managedAttributes = this._initParams.get("org.mortbay.jetty.servlet.ManagedAttributes");
        if (managedAttributes != null) {
            this._managedAttributes = new HashSet();
            final QuotedStringTokenizer tok = new QuotedStringTokenizer(managedAttributes, ",");
            while (tok.hasMoreTokens()) {
                this._managedAttributes.add(tok.nextToken().trim());
            }
            final Enumeration e = this._scontext.getAttributeNames();
            while (e.hasMoreElements()) {
                final String name = e.nextElement();
                final Object value = this._scontext.getAttribute(name);
                this.setManagedAttribute(name, value);
            }
        }
    }
    
    protected void doStop() throws Exception {
        ClassLoader old_classloader = null;
        Thread current_thread = null;
        final SContext old_context = ContextHandler.__context.get();
        ContextHandler.__context.set(this._scontext);
        try {
            if (this._classLoader != null) {
                current_thread = Thread.currentThread();
                old_classloader = current_thread.getContextClassLoader();
                current_thread.setContextClassLoader(this._classLoader);
            }
            super.doStop();
            if (this._contextListeners != null) {
                final ServletContextEvent event = new ServletContextEvent(this._scontext);
                int i = LazyList.size(this._contextListeners);
                while (i-- > 0) {
                    ((ServletContextListener)LazyList.get(this._contextListeners, i)).contextDestroyed(event);
                }
            }
            if (this._errorHandler != null) {
                this._errorHandler.stop();
            }
            final Enumeration e = this._scontext.getAttributeNames();
            while (e.hasMoreElements()) {
                final String name = e.nextElement();
                this.setManagedAttribute(name, null);
            }
        }
        finally {
            ContextHandler.__context.set(old_context);
            if (this._classLoader != null) {
                current_thread.setContextClassLoader(old_classloader);
            }
        }
        if (this._contextAttributes != null) {
            this._contextAttributes.clearAttributes();
        }
        this._contextAttributes = null;
    }
    
    public void handle(String target, final HttpServletRequest request, final HttpServletResponse response, final int dispatch) throws IOException, ServletException {
        boolean new_context = false;
        SContext old_context = null;
        String old_context_path = null;
        String old_servlet_path = null;
        String old_path_info = null;
        ClassLoader old_classloader = null;
        Thread current_thread = null;
        final Request base_request = (Request)((request instanceof Request) ? request : HttpConnection.getCurrentConnection().getRequest());
        if (!this.isStarted() || this._shutdown || (dispatch == 1 && base_request.isHandled())) {
            return;
        }
        old_context = base_request.getContext();
        if (old_context != this._scontext) {
            new_context = true;
            if (this._vhosts != null && this._vhosts.length > 0) {
                final String vhost = this.normalizeHostname(request.getServerName());
                boolean match = false;
                for (int i = 0; !match && i < this._vhosts.length; ++i) {
                    final String contextVhost = this._vhosts[i];
                    if (contextVhost != null) {
                        if (contextVhost.startsWith("*.")) {
                            match = contextVhost.regionMatches(true, 2, vhost, vhost.indexOf(".") + 1, contextVhost.length() - 2);
                        }
                        else {
                            match = contextVhost.equalsIgnoreCase(vhost);
                        }
                    }
                }
                if (!match) {
                    return;
                }
            }
            if (this._connectors != null && this._connectors.size() > 0) {
                final String connector = HttpConnection.getCurrentConnection().getConnector().getName();
                if (connector == null || !this._connectors.contains(connector)) {
                    return;
                }
            }
            if (dispatch == 1) {
                if (this._compactPath) {
                    target = URIUtil.compactPath(target);
                }
                if (target.equals(this._contextPath)) {
                    if (!this._allowNullPathInfo && !target.endsWith("/")) {
                        base_request.setHandled(true);
                        if (request.getQueryString() != null) {
                            response.sendRedirect(response.encodeRedirectURL(URIUtil.addPaths(request.getRequestURI(), "/") + "?" + request.getQueryString()));
                        }
                        else {
                            response.sendRedirect(response.encodeRedirectURL(URIUtil.addPaths(request.getRequestURI(), "/")));
                        }
                        return;
                    }
                    if (this._contextPath.length() > 1) {
                        target = "/";
                        request.setAttribute("org.mortbay.jetty.nullPathInfo", target);
                    }
                }
                else {
                    if (!target.startsWith(this._contextPath) || (this._contextPath.length() != 1 && target.charAt(this._contextPath.length()) != '/')) {
                        return;
                    }
                    if (this._contextPath.length() > 1) {
                        target = target.substring(this._contextPath.length());
                    }
                }
            }
        }
        try {
            old_context_path = base_request.getContextPath();
            old_servlet_path = base_request.getServletPath();
            old_path_info = base_request.getPathInfo();
            base_request.setContext(this._scontext);
            if (dispatch != 4 && target.startsWith("/")) {
                if (this._contextPath.length() == 1) {
                    base_request.setContextPath("");
                }
                else {
                    base_request.setContextPath(this._contextPath);
                }
                base_request.setServletPath(null);
                base_request.setPathInfo(target);
            }
            final ServletRequestEvent event = null;
            if (new_context) {
                if (this._classLoader != null) {
                    current_thread = Thread.currentThread();
                    old_classloader = current_thread.getContextClassLoader();
                    current_thread.setContextClassLoader(this._classLoader);
                }
                base_request.setRequestListeners(this._requestListeners);
                if (this._requestAttributeListeners != null) {
                    for (int s = LazyList.size(this._requestAttributeListeners), i = 0; i < s; ++i) {
                        base_request.addEventListener((EventListener)LazyList.get(this._requestAttributeListeners, i));
                    }
                }
            }
            try {
                if (dispatch == 1 && this.isProtectedTarget(target)) {
                    throw new HttpException(404);
                }
                final Handler handler = this.getHandler();
                if (handler != null) {
                    handler.handle(target, request, response, dispatch);
                }
            }
            catch (HttpException e) {
                Log.debug(e);
                response.sendError(e.getStatus(), e.getReason());
            }
            finally {
                if (new_context) {
                    base_request.takeRequestListeners();
                    if (this._requestAttributeListeners != null) {
                        int j = LazyList.size(this._requestAttributeListeners);
                        while (j-- > 0) {
                            base_request.removeEventListener((EventListener)LazyList.get(this._requestAttributeListeners, j));
                        }
                    }
                }
            }
        }
        finally {
            if (old_context != this._scontext) {
                if (this._classLoader != null) {
                    current_thread.setContextClassLoader(old_classloader);
                }
                base_request.setContext(old_context);
                base_request.setContextPath(old_context_path);
                base_request.setServletPath(old_servlet_path);
                base_request.setPathInfo(old_path_info);
            }
        }
    }
    
    protected boolean isProtectedTarget(final String target) {
        return false;
    }
    
    public void removeAttribute(final String name) {
        this.setManagedAttribute(name, null);
        this._attributes.removeAttribute(name);
    }
    
    public void setAttribute(final String name, final Object value) {
        this.setManagedAttribute(name, value);
        this._attributes.setAttribute(name, value);
    }
    
    public void setAttributes(final Attributes attributes) {
        if (attributes instanceof AttributesMap) {
            this._attributes = (AttributesMap)attributes;
            final Enumeration e = this._attributes.getAttributeNames();
            while (e.hasMoreElements()) {
                final String name = e.nextElement();
                this.setManagedAttribute(name, attributes.getAttribute(name));
            }
        }
        else {
            this._attributes = new AttributesMap();
            final Enumeration e = attributes.getAttributeNames();
            while (e.hasMoreElements()) {
                final String name = e.nextElement();
                final Object value = attributes.getAttribute(name);
                this.setManagedAttribute(name, value);
                this._attributes.setAttribute(name, value);
            }
        }
    }
    
    public void clearAttributes() {
        final Enumeration e = this._attributes.getAttributeNames();
        while (e.hasMoreElements()) {
            final String name = e.nextElement();
            this.setManagedAttribute(name, null);
        }
        this._attributes.clearAttributes();
    }
    
    private void setManagedAttribute(final String name, final Object value) {
        if (this._managedAttributes != null && this._managedAttributes.contains(name)) {
            final Object o = this._scontext.getAttribute(name);
            if (o != null) {
                this.getServer().getContainer().removeBean(o);
            }
            if (value != null) {
                this.getServer().getContainer().addBean(value);
            }
        }
    }
    
    public void setClassLoader(final ClassLoader classLoader) {
        this._classLoader = classLoader;
    }
    
    public void setContextPath(final String contextPath) {
        if (contextPath != null && contextPath.length() > 1 && contextPath.endsWith("/")) {
            throw new IllegalArgumentException("ends with /");
        }
        this._contextPath = contextPath;
        if (this.getServer() != null && (this.getServer().isStarting() || this.getServer().isStarted())) {
            final Handler[] contextCollections = this.getServer().getChildHandlersByClass(ContextHandlerCollection.class);
            for (int h = 0; contextCollections != null && h < contextCollections.length; ++h) {
                ((ContextHandlerCollection)contextCollections[h]).mapContexts();
            }
        }
    }
    
    public void setInitParams(final Map initParams) {
        if (initParams == null) {
            return;
        }
        this._initParams = new HashMap(initParams);
    }
    
    public void setDisplayName(final String servletContextName) {
        this._displayName = servletContextName;
        if (this._classLoader != null && this._classLoader instanceof WebAppClassLoader) {
            ((WebAppClassLoader)this._classLoader).setName(servletContextName);
        }
    }
    
    public Resource getBaseResource() {
        if (this._baseResource == null) {
            return null;
        }
        return this._baseResource;
    }
    
    public String getResourceBase() {
        if (this._baseResource == null) {
            return null;
        }
        return this._baseResource.toString();
    }
    
    public void setBaseResource(final Resource base) {
        this._baseResource = base;
    }
    
    public void setResourceBase(final String resourceBase) {
        try {
            this.setBaseResource(Resource.newResource(resourceBase));
        }
        catch (Exception e) {
            Log.warn(e.toString());
            Log.debug(e);
            throw new IllegalArgumentException(resourceBase);
        }
    }
    
    public MimeTypes getMimeTypes() {
        return this._mimeTypes;
    }
    
    public void setMimeTypes(final MimeTypes mimeTypes) {
        this._mimeTypes = mimeTypes;
    }
    
    public void setWelcomeFiles(final String[] files) {
        this._welcomeFiles = files;
    }
    
    public String[] getWelcomeFiles() {
        return this._welcomeFiles;
    }
    
    public ErrorHandler getErrorHandler() {
        return this._errorHandler;
    }
    
    public void setErrorHandler(final ErrorHandler errorHandler) {
        if (errorHandler != null) {
            errorHandler.setServer(this.getServer());
        }
        if (this.getServer() != null) {
            this.getServer().getContainer().update(this, this._errorHandler, errorHandler, "errorHandler", true);
        }
        this._errorHandler = errorHandler;
    }
    
    public int getMaxFormContentSize() {
        return this._maxFormContentSize;
    }
    
    public void setMaxFormContentSize(final int maxSize) {
        this._maxFormContentSize = maxSize;
    }
    
    public boolean isCompactPath() {
        return this._compactPath;
    }
    
    public void setCompactPath(final boolean compactPath) {
        this._compactPath = compactPath;
    }
    
    public String toString() {
        return this.getClass().getName() + "@" + Integer.toHexString(this.hashCode()) + "{" + this.getContextPath() + "," + this.getBaseResource() + "}";
    }
    
    public synchronized Class loadClass(final String className) throws ClassNotFoundException {
        if (className == null) {
            return null;
        }
        if (this._classLoader == null) {
            return Loader.loadClass(this.getClass(), className);
        }
        return this._classLoader.loadClass(className);
    }
    
    public void addLocaleEncoding(final String locale, final String encoding) {
        if (this._localeEncodingMap == null) {
            this._localeEncodingMap = new HashMap();
        }
        this._localeEncodingMap.put(locale, encoding);
    }
    
    public String getLocaleEncoding(final Locale locale) {
        if (this._localeEncodingMap == null) {
            return null;
        }
        String encoding = this._localeEncodingMap.get(locale.toString());
        if (encoding == null) {
            encoding = this._localeEncodingMap.get(locale.getLanguage());
        }
        return encoding;
    }
    
    public Resource getResource(String path) throws MalformedURLException {
        if (path == null || !path.startsWith("/")) {
            throw new MalformedURLException(path);
        }
        if (this._baseResource == null) {
            return null;
        }
        try {
            path = URIUtil.canonicalPath(path);
            final Resource resource = this._baseResource.addPath(path);
            return resource;
        }
        catch (Exception e) {
            Log.ignore(e);
            return null;
        }
    }
    
    public Set getResourcePaths(String path) {
        try {
            path = URIUtil.canonicalPath(path);
            final Resource resource = this.getResource(path);
            if (resource != null && resource.exists()) {
                if (!path.endsWith("/")) {
                    path += "/";
                }
                final String[] l = resource.list();
                if (l != null) {
                    final HashSet set = new HashSet();
                    for (int i = 0; i < l.length; ++i) {
                        set.add(path + l[i]);
                    }
                    return set;
                }
            }
        }
        catch (Exception e) {
            Log.ignore(e);
        }
        return Collections.EMPTY_SET;
    }
    
    private String normalizeHostname(final String host) {
        if (host == null) {
            return null;
        }
        if (host.endsWith(".")) {
            return host.substring(0, host.length() - 1);
        }
        return host;
    }
    
    static {
        ContextHandler.__context = new ThreadLocal();
    }
    
    public class SContext implements ServletContext
    {
        protected SContext() {
        }
        
        public ContextHandler getContextHandler() {
            return ContextHandler.this;
        }
        
        public ServletContext getContext(final String uripath) {
            ContextHandler context = null;
            final Handler[] handlers = ContextHandler.this.getServer().getChildHandlersByClass(ContextHandler.class);
            for (int i = 0; i < handlers.length; ++i) {
                if (handlers[i] != null) {
                    if (handlers[i].isStarted()) {
                        final ContextHandler ch = (ContextHandler)handlers[i];
                        final String context_path = ch.getContextPath();
                        if ((uripath.equals(context_path) || (uripath.startsWith(context_path) && uripath.charAt(context_path.length()) == '/')) && (context == null || context_path.length() > context.getContextPath().length())) {
                            context = ch;
                        }
                    }
                }
            }
            if (context != null) {
                return context._scontext;
            }
            return null;
        }
        
        public int getMajorVersion() {
            return 2;
        }
        
        public String getMimeType(final String file) {
            if (ContextHandler.this._mimeTypes == null) {
                return null;
            }
            final Buffer mime = ContextHandler.this._mimeTypes.getMimeByExtension(file);
            if (mime != null) {
                return mime.toString();
            }
            return null;
        }
        
        public int getMinorVersion() {
            return 5;
        }
        
        public RequestDispatcher getNamedDispatcher(final String name) {
            return null;
        }
        
        public String getRealPath(String path) {
            if (path == null) {
                return null;
            }
            if (path.length() == 0) {
                path = "/";
            }
            else if (path.charAt(0) != '/') {
                path = "/" + path;
            }
            try {
                final Resource resource = ContextHandler.this.getResource(path);
                if (resource != null) {
                    final File file = resource.getFile();
                    if (file != null) {
                        return file.getCanonicalPath();
                    }
                }
            }
            catch (Exception e) {
                Log.ignore(e);
            }
            return null;
        }
        
        public RequestDispatcher getRequestDispatcher(final String uriInContext) {
            return null;
        }
        
        public URL getResource(final String path) throws MalformedURLException {
            final Resource resource = ContextHandler.this.getResource(path);
            if (resource != null && resource.exists()) {
                return resource.getURL();
            }
            return null;
        }
        
        public InputStream getResourceAsStream(final String path) {
            try {
                final URL url = this.getResource(path);
                if (url == null) {
                    return null;
                }
                return url.openStream();
            }
            catch (Exception e) {
                Log.ignore(e);
                return null;
            }
        }
        
        public Set getResourcePaths(final String path) {
            return ContextHandler.this.getResourcePaths(path);
        }
        
        public String getServerInfo() {
            return "jetty/" + Server.getVersion();
        }
        
        public Servlet getServlet(final String name) throws ServletException {
            return null;
        }
        
        public Enumeration getServletNames() {
            return Collections.enumeration((Collection<Object>)Collections.EMPTY_LIST);
        }
        
        public Enumeration getServlets() {
            return Collections.enumeration((Collection<Object>)Collections.EMPTY_LIST);
        }
        
        public void log(final Exception exception, final String msg) {
            ContextHandler.this._logger.warn(msg, exception);
        }
        
        public void log(final String msg) {
            ContextHandler.this._logger.info(msg, null, null);
        }
        
        public void log(final String message, final Throwable throwable) {
            ContextHandler.this._logger.warn(message, throwable);
        }
        
        public String getInitParameter(final String name) {
            return ContextHandler.this.getInitParameter(name);
        }
        
        public Enumeration getInitParameterNames() {
            return ContextHandler.this.getInitParameterNames();
        }
        
        public synchronized Object getAttribute(final String name) {
            Object o = ContextHandler.this.getAttribute(name);
            if (o == null && ContextHandler.this._contextAttributes != null) {
                o = ContextHandler.this._contextAttributes.getAttribute(name);
            }
            return o;
        }
        
        public synchronized Enumeration getAttributeNames() {
            final HashSet set = new HashSet();
            if (ContextHandler.this._contextAttributes != null) {
                final Enumeration e = ContextHandler.this._contextAttributes.getAttributeNames();
                while (e.hasMoreElements()) {
                    set.add(e.nextElement());
                }
            }
            final Enumeration e = ContextHandler.this._attributes.getAttributeNames();
            while (e.hasMoreElements()) {
                set.add(e.nextElement());
            }
            return Collections.enumeration((Collection<Object>)set);
        }
        
        public synchronized void setAttribute(final String name, final Object value) {
            if (ContextHandler.this._contextAttributes == null) {
                ContextHandler.this.setAttribute(name, value);
                return;
            }
            ContextHandler.this.setManagedAttribute(name, value);
            final Object old_value = ContextHandler.this._contextAttributes.getAttribute(name);
            if (value == null) {
                ContextHandler.this._contextAttributes.removeAttribute(name);
            }
            else {
                ContextHandler.this._contextAttributes.setAttribute(name, value);
            }
            if (ContextHandler.this._contextAttributeListeners != null) {
                final ServletContextAttributeEvent event = new ServletContextAttributeEvent(ContextHandler.this._scontext, name, (old_value == null) ? value : old_value);
                for (int i = 0; i < LazyList.size(ContextHandler.this._contextAttributeListeners); ++i) {
                    final ServletContextAttributeListener l = (ServletContextAttributeListener)LazyList.get(ContextHandler.this._contextAttributeListeners, i);
                    if (old_value == null) {
                        l.attributeAdded(event);
                    }
                    else if (value == null) {
                        l.attributeRemoved(event);
                    }
                    else {
                        l.attributeReplaced(event);
                    }
                }
            }
        }
        
        public synchronized void removeAttribute(final String name) {
            ContextHandler.this.setManagedAttribute(name, null);
            if (ContextHandler.this._contextAttributes == null) {
                ContextHandler.this._attributes.removeAttribute(name);
                return;
            }
            final Object old_value = ContextHandler.this._contextAttributes.getAttribute(name);
            ContextHandler.this._contextAttributes.removeAttribute(name);
            if (old_value != null && ContextHandler.this._contextAttributeListeners != null) {
                final ServletContextAttributeEvent event = new ServletContextAttributeEvent(ContextHandler.this._scontext, name, old_value);
                for (int i = 0; i < LazyList.size(ContextHandler.this._contextAttributeListeners); ++i) {
                    ((ServletContextAttributeListener)LazyList.get(ContextHandler.this._contextAttributeListeners, i)).attributeRemoved(event);
                }
            }
        }
        
        public String getServletContextName() {
            String name = ContextHandler.this.getDisplayName();
            if (name == null) {
                name = ContextHandler.this.getContextPath();
            }
            return name;
        }
        
        public String getContextPath() {
            if (ContextHandler.this._contextPath != null && ContextHandler.this._contextPath.equals("/")) {
                return "";
            }
            return ContextHandler.this._contextPath;
        }
        
        public String toString() {
            return "ServletContext@" + Integer.toHexString(this.hashCode()) + "{" + (this.getContextPath().equals("") ? "/" : this.getContextPath()) + "," + ContextHandler.this.getBaseResource() + "}";
        }
    }
}
