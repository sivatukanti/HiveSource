// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.server.handler;

import javax.servlet.SessionCookieConfig;
import javax.servlet.SessionTrackingMode;
import javax.servlet.ServletRegistration;
import javax.servlet.FilterRegistration;
import javax.servlet.Filter;
import javax.servlet.Servlet;
import javax.servlet.descriptor.JspConfigDescriptor;
import java.lang.reflect.Method;
import java.security.Permission;
import java.security.AccessController;
import javax.servlet.ServletContextAttributeEvent;
import java.io.InputStream;
import org.eclipse.jetty.server.Dispatcher;
import org.eclipse.jetty.http.HttpURI;
import javax.servlet.RequestDispatcher;
import java.util.HashSet;
import java.util.Set;
import java.net.URI;
import java.net.MalformedURLException;
import java.util.Locale;
import org.eclipse.jetty.util.Loader;
import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletException;
import org.eclipse.jetty.util.URIUtil;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.DispatcherType;
import javax.servlet.http.HttpServletResponse;
import java.util.Iterator;
import javax.servlet.ServletContextEvent;
import org.eclipse.jetty.util.StringUtil;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.FutureCallback;
import java.util.concurrent.Future;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.ArrayList;
import java.util.Arrays;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.annotation.ManagedAttribute;
import java.io.IOException;
import org.eclipse.jetty.util.component.DumpableCollection;
import java.util.Collections;
import org.eclipse.jetty.server.ClassLoaderDump;
import java.util.Collection;
import org.eclipse.jetty.server.Handler;
import java.io.File;
import java.util.HashMap;
import org.eclipse.jetty.server.HandlerContainer;
import javax.servlet.ServletContext;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.servlet.ServletRequestAttributeListener;
import javax.servlet.ServletRequestListener;
import javax.servlet.ServletContextAttributeListener;
import javax.servlet.ServletContextListener;
import java.util.EventListener;
import java.util.List;
import org.eclipse.jetty.http.MimeTypes;
import org.eclipse.jetty.util.resource.Resource;
import java.util.Map;
import org.eclipse.jetty.util.AttributesMap;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.util.annotation.ManagedObject;
import org.eclipse.jetty.util.component.Graceful;
import org.eclipse.jetty.util.Attributes;

@ManagedObject("URI Context")
public class ContextHandler extends ScopedHandler implements Attributes, Graceful
{
    public static final int SERVLET_MAJOR_VERSION = 3;
    public static final int SERVLET_MINOR_VERSION = 1;
    public static final Class<?>[] SERVLET_LISTENER_TYPES;
    public static final int DEFAULT_LISTENER_TYPE_INDEX = 1;
    public static final int EXTENDED_LISTENER_TYPE_INDEX = 0;
    private static final String __unimplmented = "Unimplemented - use org.eclipse.jetty.servlet.ServletContextHandler";
    private static final Logger LOG;
    private static final ThreadLocal<Context> __context;
    private static String __serverInfo;
    public static final String MANAGED_ATTRIBUTES = "org.eclipse.jetty.server.context.ManagedAttributes";
    protected Context _scontext;
    private final AttributesMap _attributes;
    private final Map<String, String> _initParams;
    private ClassLoader _classLoader;
    private String _contextPath;
    private String _displayName;
    private Resource _baseResource;
    private MimeTypes _mimeTypes;
    private Map<String, String> _localeEncodingMap;
    private String[] _welcomeFiles;
    private ErrorHandler _errorHandler;
    private String[] _vhosts;
    private Logger _logger;
    private boolean _allowNullPathInfo;
    private int _maxFormKeys;
    private int _maxFormContentSize;
    private boolean _compactPath;
    private boolean _usingSecurityManager;
    private final List<EventListener> _eventListeners;
    private final List<EventListener> _programmaticListeners;
    private final List<ServletContextListener> _servletContextListeners;
    private final List<ServletContextAttributeListener> _servletContextAttributeListeners;
    private final List<ServletRequestListener> _servletRequestListeners;
    private final List<ServletRequestAttributeListener> _servletRequestAttributeListeners;
    private final List<ContextScopeListener> _contextListeners;
    private final List<EventListener> _durableListeners;
    private Map<String, Object> _managedAttributes;
    private String[] _protectedTargets;
    private final CopyOnWriteArrayList<AliasCheck> _aliasChecks;
    private volatile Availability _availability;
    
    public static Context getCurrentContext() {
        return ContextHandler.__context.get();
    }
    
    public static ContextHandler getContextHandler(final ServletContext context) {
        if (context instanceof Context) {
            return ((Context)context).getContextHandler();
        }
        final Context c = getCurrentContext();
        if (c != null) {
            return c.getContextHandler();
        }
        return null;
    }
    
    public static String getServerInfo() {
        return ContextHandler.__serverInfo;
    }
    
    public static void setServerInfo(final String serverInfo) {
        ContextHandler.__serverInfo = serverInfo;
    }
    
    public ContextHandler() {
        this(null, null, null);
    }
    
    protected ContextHandler(final Context context) {
        this(context, null, null);
    }
    
    public ContextHandler(final String contextPath) {
        this(null, null, contextPath);
    }
    
    public ContextHandler(final HandlerContainer parent, final String contextPath) {
        this(null, parent, contextPath);
    }
    
    private ContextHandler(final Context context, final HandlerContainer parent, final String contextPath) {
        this._contextPath = "/";
        this._maxFormKeys = Integer.getInteger("org.eclipse.jetty.server.Request.maxFormKeys", -1);
        this._maxFormContentSize = Integer.getInteger("org.eclipse.jetty.server.Request.maxFormContentSize", -1);
        this._compactPath = false;
        this._usingSecurityManager = (System.getSecurityManager() != null);
        this._eventListeners = new CopyOnWriteArrayList<EventListener>();
        this._programmaticListeners = new CopyOnWriteArrayList<EventListener>();
        this._servletContextListeners = new CopyOnWriteArrayList<ServletContextListener>();
        this._servletContextAttributeListeners = new CopyOnWriteArrayList<ServletContextAttributeListener>();
        this._servletRequestListeners = new CopyOnWriteArrayList<ServletRequestListener>();
        this._servletRequestAttributeListeners = new CopyOnWriteArrayList<ServletRequestAttributeListener>();
        this._contextListeners = new CopyOnWriteArrayList<ContextScopeListener>();
        this._durableListeners = new CopyOnWriteArrayList<EventListener>();
        this._aliasChecks = new CopyOnWriteArrayList<AliasCheck>();
        this._scontext = ((context == null) ? new Context() : context);
        this._attributes = new AttributesMap();
        this._initParams = new HashMap<String, String>();
        this.addAliasCheck(new ApproveNonExistentDirectoryAliases());
        if (File.separatorChar == '/') {
            this.addAliasCheck(new AllowSymLinkAliasChecker());
        }
        if (contextPath != null) {
            this.setContextPath(contextPath);
        }
        if (parent instanceof HandlerWrapper) {
            ((HandlerWrapper)parent).setHandler(this);
        }
        else if (parent instanceof HandlerCollection) {
            ((HandlerCollection)parent).addHandler(this);
        }
    }
    
    @Override
    public void dump(final Appendable out, final String indent) throws IOException {
        this.dumpBeans(out, indent, Collections.singletonList(new ClassLoaderDump(this.getClassLoader())), Collections.singletonList(new DumpableCollection("Handler attributes " + this, ((AttributesMap)this.getAttributes()).getAttributeEntrySet())), Collections.singletonList(new DumpableCollection("Context attributes " + this, this.getServletContext().getAttributeEntrySet())), Collections.singletonList(new DumpableCollection("Initparams " + this, this.getInitParams().entrySet())));
    }
    
    public Context getServletContext() {
        return this._scontext;
    }
    
    @ManagedAttribute("Checks if the /context is not redirected to /context/")
    public boolean getAllowNullPathInfo() {
        return this._allowNullPathInfo;
    }
    
    public void setAllowNullPathInfo(final boolean allowNullPathInfo) {
        this._allowNullPathInfo = allowNullPathInfo;
    }
    
    @Override
    public void setServer(final Server server) {
        super.setServer(server);
        if (this._errorHandler != null) {
            this._errorHandler.setServer(server);
        }
    }
    
    public boolean isUsingSecurityManager() {
        return this._usingSecurityManager;
    }
    
    public void setUsingSecurityManager(final boolean usingSecurityManager) {
        this._usingSecurityManager = usingSecurityManager;
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
    
    public void addVirtualHosts(final String[] virtualHosts) {
        if (virtualHosts == null) {
            return;
        }
        List<String> currentVirtualHosts = null;
        if (this._vhosts != null) {
            currentVirtualHosts = new ArrayList<String>(Arrays.asList(this._vhosts));
        }
        else {
            currentVirtualHosts = new ArrayList<String>();
        }
        for (int i = 0; i < virtualHosts.length; ++i) {
            final String normVhost = this.normalizeHostname(virtualHosts[i]);
            if (!currentVirtualHosts.contains(normVhost)) {
                currentVirtualHosts.add(normVhost);
            }
        }
        this._vhosts = currentVirtualHosts.toArray(new String[0]);
    }
    
    public void removeVirtualHosts(final String[] virtualHosts) {
        if (virtualHosts == null) {
            return;
        }
        if (this._vhosts == null || this._vhosts.length == 0) {
            return;
        }
        final List<String> existingVirtualHosts = new ArrayList<String>(Arrays.asList(this._vhosts));
        for (int i = 0; i < virtualHosts.length; ++i) {
            final String toRemoveVirtualHost = this.normalizeHostname(virtualHosts[i]);
            if (existingVirtualHosts.contains(toRemoveVirtualHost)) {
                existingVirtualHosts.remove(toRemoveVirtualHost);
            }
        }
        if (existingVirtualHosts.isEmpty()) {
            this._vhosts = null;
        }
        else {
            this._vhosts = existingVirtualHosts.toArray(new String[0]);
        }
    }
    
    @ManagedAttribute(value = "Virtual hosts accepted by the context", readonly = true)
    public String[] getVirtualHosts() {
        return this._vhosts;
    }
    
    @Override
    public Object getAttribute(final String name) {
        return this._attributes.getAttribute(name);
    }
    
    @Override
    public Enumeration<String> getAttributeNames() {
        return AttributesMap.getAttributeNamesCopy(this._attributes);
    }
    
    public Attributes getAttributes() {
        return this._attributes;
    }
    
    public ClassLoader getClassLoader() {
        return this._classLoader;
    }
    
    @ManagedAttribute("The file classpath")
    public String getClassPath() {
        if (this._classLoader == null || !(this._classLoader instanceof URLClassLoader)) {
            return null;
        }
        final URLClassLoader loader = (URLClassLoader)this._classLoader;
        final URL[] urls = loader.getURLs();
        final StringBuilder classpath = new StringBuilder();
        for (int i = 0; i < urls.length; ++i) {
            try {
                final Resource resource = this.newResource(urls[i]);
                final File file = resource.getFile();
                if (file != null && file.exists()) {
                    if (classpath.length() > 0) {
                        classpath.append(File.pathSeparatorChar);
                    }
                    classpath.append(file.getAbsolutePath());
                }
            }
            catch (IOException e) {
                ContextHandler.LOG.debug(e);
            }
        }
        if (classpath.length() == 0) {
            return null;
        }
        return classpath.toString();
    }
    
    @ManagedAttribute("True if URLs are compacted to replace the multiple '/'s with a single '/'")
    public String getContextPath() {
        return this._contextPath;
    }
    
    public String getInitParameter(final String name) {
        return this._initParams.get(name);
    }
    
    public String setInitParameter(final String name, final String value) {
        return this._initParams.put(name, value);
    }
    
    public Enumeration<String> getInitParameterNames() {
        return Collections.enumeration(this._initParams.keySet());
    }
    
    @ManagedAttribute("Initial Parameter map for the context")
    public Map<String, String> getInitParams() {
        return this._initParams;
    }
    
    @ManagedAttribute(value = "Display name of the Context", readonly = true)
    public String getDisplayName() {
        return this._displayName;
    }
    
    public EventListener[] getEventListeners() {
        return this._eventListeners.toArray(new EventListener[this._eventListeners.size()]);
    }
    
    public void setEventListeners(final EventListener[] eventListeners) {
        this._contextListeners.clear();
        this._servletContextListeners.clear();
        this._servletContextAttributeListeners.clear();
        this._servletRequestListeners.clear();
        this._servletRequestAttributeListeners.clear();
        this._eventListeners.clear();
        if (eventListeners != null) {
            for (final EventListener listener : eventListeners) {
                this.addEventListener(listener);
            }
        }
    }
    
    public void addEventListener(final EventListener listener) {
        this._eventListeners.add(listener);
        if (!this.isStarted() && !this.isStarting()) {
            this._durableListeners.add(listener);
        }
        if (listener instanceof ContextScopeListener) {
            this._contextListeners.add((ContextScopeListener)listener);
        }
        if (listener instanceof ServletContextListener) {
            this._servletContextListeners.add((ServletContextListener)listener);
        }
        if (listener instanceof ServletContextAttributeListener) {
            this._servletContextAttributeListeners.add((ServletContextAttributeListener)listener);
        }
        if (listener instanceof ServletRequestListener) {
            this._servletRequestListeners.add((ServletRequestListener)listener);
        }
        if (listener instanceof ServletRequestAttributeListener) {
            this._servletRequestAttributeListeners.add((ServletRequestAttributeListener)listener);
        }
    }
    
    public void removeEventListener(final EventListener listener) {
        this._eventListeners.remove(listener);
        if (listener instanceof ContextScopeListener) {
            this._contextListeners.remove(listener);
        }
        if (listener instanceof ServletContextListener) {
            this._servletContextListeners.remove(listener);
        }
        if (listener instanceof ServletContextAttributeListener) {
            this._servletContextAttributeListeners.remove(listener);
        }
        if (listener instanceof ServletRequestListener) {
            this._servletRequestListeners.remove(listener);
        }
        if (listener instanceof ServletRequestAttributeListener) {
            this._servletRequestAttributeListeners.remove(listener);
        }
    }
    
    protected void addProgrammaticListener(final EventListener listener) {
        this._programmaticListeners.add(listener);
    }
    
    protected boolean isProgrammaticListener(final EventListener listener) {
        return this._programmaticListeners.contains(listener);
    }
    
    @ManagedAttribute("true for graceful shutdown, which allows existing requests to complete")
    public boolean isShutdown() {
        switch (this._availability) {
            case SHUTDOWN: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    @Override
    public Future<Void> shutdown() {
        this._availability = (this.isRunning() ? Availability.SHUTDOWN : Availability.UNAVAILABLE);
        return new FutureCallback(true);
    }
    
    public boolean isAvailable() {
        return this._availability == Availability.AVAILABLE;
    }
    
    public void setAvailable(final boolean available) {
        synchronized (this) {
            if (available && this.isRunning()) {
                this._availability = Availability.AVAILABLE;
            }
            else if (!available || !this.isRunning()) {
                this._availability = Availability.UNAVAILABLE;
            }
        }
    }
    
    public Logger getLogger() {
        return this._logger;
    }
    
    public void setLogger(final Logger logger) {
        this._logger = logger;
    }
    
    @Override
    protected void doStart() throws Exception {
        this._availability = Availability.STARTING;
        if (this._contextPath == null) {
            throw new IllegalStateException("Null contextPath");
        }
        if (this._logger == null) {
            this._logger = Log.getLogger((this.getDisplayName() == null) ? this.getContextPath() : this.getDisplayName());
        }
        ClassLoader old_classloader = null;
        Thread current_thread = null;
        Context old_context = null;
        this._attributes.setAttribute("org.eclipse.jetty.server.Executor", this.getServer().getThreadPool());
        if (this._mimeTypes == null) {
            this._mimeTypes = new MimeTypes();
        }
        try {
            if (this._classLoader != null) {
                current_thread = Thread.currentThread();
                old_classloader = current_thread.getContextClassLoader();
                current_thread.setContextClassLoader(this._classLoader);
            }
            old_context = ContextHandler.__context.get();
            ContextHandler.__context.set(this._scontext);
            this.enterScope(null, this.getState());
            this.startContext();
            this._availability = Availability.AVAILABLE;
            ContextHandler.LOG.info("Started {}", this);
        }
        finally {
            if (this._availability == Availability.STARTING) {
                this._availability = Availability.UNAVAILABLE;
            }
            this.exitScope(null);
            ContextHandler.__context.set(old_context);
            if (this._classLoader != null && current_thread != null) {
                current_thread.setContextClassLoader(old_classloader);
            }
        }
    }
    
    protected void startContext() throws Exception {
        final String managedAttributes = this._initParams.get("org.eclipse.jetty.server.context.ManagedAttributes");
        if (managedAttributes != null) {
            this.addEventListener(new ManagedAttributeListener(this, StringUtil.csvSplit(managedAttributes)));
        }
        super.doStart();
        if (!this._servletContextListeners.isEmpty()) {
            final ServletContextEvent event = new ServletContextEvent(this._scontext);
            for (final ServletContextListener listener : this._servletContextListeners) {
                this.callContextInitialized(listener, event);
            }
        }
    }
    
    protected void stopContext() throws Exception {
        super.doStop();
        if (!this._servletContextListeners.isEmpty()) {
            final ServletContextEvent event = new ServletContextEvent(this._scontext);
            int i = this._servletContextListeners.size();
            while (i-- > 0) {
                this.callContextDestroyed(this._servletContextListeners.get(i), event);
            }
        }
    }
    
    protected void callContextInitialized(final ServletContextListener l, final ServletContextEvent e) {
        if (ContextHandler.LOG.isDebugEnabled()) {
            ContextHandler.LOG.debug("contextInitialized: {}->{}", e, l);
        }
        l.contextInitialized(e);
    }
    
    protected void callContextDestroyed(final ServletContextListener l, final ServletContextEvent e) {
        if (ContextHandler.LOG.isDebugEnabled()) {
            ContextHandler.LOG.debug("contextDestroyed: {}->{}", e, l);
        }
        l.contextDestroyed(e);
    }
    
    @Override
    protected void doStop() throws Exception {
        this._availability = Availability.UNAVAILABLE;
        ClassLoader old_classloader = null;
        ClassLoader old_webapploader = null;
        Thread current_thread = null;
        final Context old_context = ContextHandler.__context.get();
        this.enterScope(null, "doStop");
        ContextHandler.__context.set(this._scontext);
        try {
            if (this._classLoader != null) {
                old_webapploader = this._classLoader;
                current_thread = Thread.currentThread();
                old_classloader = current_thread.getContextClassLoader();
                current_thread.setContextClassLoader(this._classLoader);
            }
            this.stopContext();
            this.setEventListeners(this._durableListeners.toArray(new EventListener[this._durableListeners.size()]));
            this._durableListeners.clear();
            if (this._errorHandler != null) {
                this._errorHandler.stop();
            }
            for (final EventListener l : this._programmaticListeners) {
                this.removeEventListener(l);
                if (l instanceof ContextScopeListener) {
                    try {
                        ((ContextScopeListener)l).exitScope(this._scontext, null);
                    }
                    catch (Throwable e) {
                        ContextHandler.LOG.warn(e);
                    }
                }
            }
            this._programmaticListeners.clear();
        }
        finally {
            ContextHandler.__context.set(old_context);
            this.exitScope(null);
            ContextHandler.LOG.info("Stopped {}", this);
            if ((old_classloader == null || old_classloader != old_webapploader) && current_thread != null) {
                current_thread.setContextClassLoader(old_classloader);
            }
        }
        this._scontext.clearAttributes();
    }
    
    public boolean checkVirtualHost(final Request baseRequest) {
        if (this._vhosts != null && this._vhosts.length > 0) {
            final String vhost = this.normalizeHostname(baseRequest.getServerName());
            boolean match = false;
            boolean connectorName = false;
            boolean connectorMatch = false;
            for (final String contextVhost : this._vhosts) {
                if (contextVhost != null) {
                    if (contextVhost.length() != 0) {
                        final char c = contextVhost.charAt(0);
                        switch (c) {
                            case '*': {
                                if (contextVhost.startsWith("*.")) {
                                    match = (match || contextVhost.regionMatches(true, 2, vhost, vhost.indexOf(".") + 1, contextVhost.length() - 2));
                                    break;
                                }
                                break;
                            }
                            case '@': {
                                connectorName = true;
                                final String name = baseRequest.getHttpChannel().getConnector().getName();
                                final boolean m = name != null && contextVhost.length() == name.length() + 1 && contextVhost.endsWith(name);
                                match = (match || m);
                                connectorMatch = (connectorMatch || m);
                                break;
                            }
                            default: {
                                match = (match || contextVhost.equalsIgnoreCase(vhost));
                                break;
                            }
                        }
                    }
                }
            }
            if (!match || (connectorName && !connectorMatch)) {
                return false;
            }
        }
        return true;
    }
    
    public boolean checkContextPath(final String uri) {
        if (this._contextPath.length() > 1) {
            if (!uri.startsWith(this._contextPath)) {
                return false;
            }
            if (uri.length() > this._contextPath.length() && uri.charAt(this._contextPath.length()) != '/') {
                return false;
            }
        }
        return true;
    }
    
    public boolean checkContext(final String target, final Request baseRequest, final HttpServletResponse response) throws IOException {
        final DispatcherType dispatch = baseRequest.getDispatcherType();
        if (!this.checkVirtualHost(baseRequest)) {
            return false;
        }
        if (!this.checkContextPath(target)) {
            return false;
        }
        if (!this._allowNullPathInfo && this._contextPath.length() == target.length() && this._contextPath.length() > 1) {
            baseRequest.setHandled(true);
            if (baseRequest.getQueryString() != null) {
                response.sendRedirect(baseRequest.getRequestURI() + "/?" + baseRequest.getQueryString());
            }
            else {
                response.sendRedirect(baseRequest.getRequestURI() + "/");
            }
            return false;
        }
        switch (this._availability) {
            case SHUTDOWN:
            case UNAVAILABLE: {
                baseRequest.setHandled(true);
                response.sendError(503);
                return false;
            }
            default: {
                return !DispatcherType.REQUEST.equals(dispatch) || !baseRequest.isHandled();
            }
        }
    }
    
    @Override
    public void doScope(String target, final Request baseRequest, final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
        if (ContextHandler.LOG.isDebugEnabled()) {
            ContextHandler.LOG.debug("scope {}|{}|{} @ {}", baseRequest.getContextPath(), baseRequest.getServletPath(), baseRequest.getPathInfo(), this);
        }
        Context old_context = null;
        String old_context_path = null;
        String old_servlet_path = null;
        String old_path_info = null;
        ClassLoader old_classloader = null;
        Thread current_thread = null;
        String pathInfo = target;
        final DispatcherType dispatch = baseRequest.getDispatcherType();
        old_context = baseRequest.getContext();
        if (old_context != this._scontext) {
            if (DispatcherType.REQUEST.equals(dispatch) || DispatcherType.ASYNC.equals(dispatch) || (DispatcherType.ERROR.equals(dispatch) && baseRequest.getHttpChannelState().isAsync())) {
                if (this._compactPath) {
                    target = URIUtil.compactPath(target);
                }
                if (!this.checkContext(target, baseRequest, response)) {
                    return;
                }
                if (target.length() > this._contextPath.length()) {
                    if (this._contextPath.length() > 1) {
                        target = target.substring(this._contextPath.length());
                    }
                    pathInfo = target;
                }
                else if (this._contextPath.length() == 1) {
                    target = "/";
                    pathInfo = "/";
                }
                else {
                    target = "/";
                    pathInfo = null;
                }
            }
            if (this._classLoader != null) {
                current_thread = Thread.currentThread();
                old_classloader = current_thread.getContextClassLoader();
                current_thread.setContextClassLoader(this._classLoader);
            }
        }
        try {
            old_context_path = baseRequest.getContextPath();
            old_servlet_path = baseRequest.getServletPath();
            old_path_info = baseRequest.getPathInfo();
            baseRequest.setContext(this._scontext);
            ContextHandler.__context.set(this._scontext);
            if (!DispatcherType.INCLUDE.equals(dispatch) && target.startsWith("/")) {
                if (this._contextPath.length() == 1) {
                    baseRequest.setContextPath("");
                }
                else {
                    baseRequest.setContextPath(this._contextPath);
                }
                baseRequest.setServletPath(null);
                baseRequest.setPathInfo(pathInfo);
            }
            if (old_context != this._scontext) {
                this.enterScope(baseRequest, dispatch);
            }
            if (ContextHandler.LOG.isDebugEnabled()) {
                ContextHandler.LOG.debug("context={}|{}|{} @ {}", baseRequest.getContextPath(), baseRequest.getServletPath(), baseRequest.getPathInfo(), this);
            }
            if (this.never()) {
                this.nextScope(target, baseRequest, request, response);
            }
            else if (this._nextScope != null) {
                this._nextScope.doScope(target, baseRequest, request, response);
            }
            else if (this._outerScope != null) {
                this._outerScope.doHandle(target, baseRequest, request, response);
            }
            else {
                this.doHandle(target, baseRequest, request, response);
            }
        }
        finally {
            if (old_context != this._scontext) {
                this.exitScope(baseRequest);
                if (this._classLoader != null && current_thread != null) {
                    current_thread.setContextClassLoader(old_classloader);
                }
                baseRequest.setContext(old_context);
                ContextHandler.__context.set(old_context);
                baseRequest.setContextPath(old_context_path);
                baseRequest.setServletPath(old_servlet_path);
                baseRequest.setPathInfo(old_path_info);
            }
        }
    }
    
    @Override
    public void doHandle(final String target, final Request baseRequest, final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
        final DispatcherType dispatch = baseRequest.getDispatcherType();
        final boolean new_context = baseRequest.takeNewContext();
        try {
            if (new_context) {
                if (!this._servletRequestAttributeListeners.isEmpty()) {
                    for (final ServletRequestAttributeListener l : this._servletRequestAttributeListeners) {
                        baseRequest.addEventListener(l);
                    }
                }
                if (!this._servletRequestListeners.isEmpty()) {
                    final ServletRequestEvent sre = new ServletRequestEvent(this._scontext, request);
                    for (final ServletRequestListener i : this._servletRequestListeners) {
                        i.requestInitialized(sre);
                    }
                }
            }
            if (DispatcherType.REQUEST.equals(dispatch) && this.isProtectedTarget(target)) {
                response.sendError(404);
                baseRequest.setHandled(true);
                return;
            }
            if (this.never()) {
                this.nextHandle(target, baseRequest, request, response);
            }
            else if (this._nextScope != null && this._nextScope == this._handler) {
                this._nextScope.doHandle(target, baseRequest, request, response);
            }
            else if (this._handler != null) {
                this._handler.handle(target, baseRequest, request, response);
            }
        }
        finally {
            if (new_context) {
                if (!this._servletRequestListeners.isEmpty()) {
                    final ServletRequestEvent sre2 = new ServletRequestEvent(this._scontext, request);
                    int j = this._servletRequestListeners.size();
                    while (j-- > 0) {
                        this._servletRequestListeners.get(j).requestDestroyed(sre2);
                    }
                }
                if (!this._servletRequestAttributeListeners.isEmpty()) {
                    int k = this._servletRequestAttributeListeners.size();
                    while (k-- > 0) {
                        baseRequest.removeEventListener(this._servletRequestAttributeListeners.get(k));
                    }
                }
            }
        }
    }
    
    protected void enterScope(final Request request, final Object reason) {
        if (!this._contextListeners.isEmpty()) {
            for (final ContextScopeListener listener : this._contextListeners) {
                try {
                    listener.enterScope(this._scontext, request, reason);
                }
                catch (Throwable e) {
                    ContextHandler.LOG.warn(e);
                }
            }
        }
    }
    
    protected void exitScope(final Request request) {
        if (!this._contextListeners.isEmpty()) {
            int i = this._contextListeners.size();
            while (i-- > 0) {
                try {
                    this._contextListeners.get(i).exitScope(this._scontext, request);
                }
                catch (Throwable e) {
                    ContextHandler.LOG.warn(e);
                }
            }
        }
    }
    
    public void handle(final Request request, final Runnable runnable) {
        ClassLoader old_classloader = null;
        Thread current_thread = null;
        final Context old_context = ContextHandler.__context.get();
        if (old_context == this._scontext) {
            runnable.run();
            return;
        }
        try {
            ContextHandler.__context.set(this._scontext);
            if (this._classLoader != null) {
                current_thread = Thread.currentThread();
                old_classloader = current_thread.getContextClassLoader();
                current_thread.setContextClassLoader(this._classLoader);
            }
            this.enterScope(request, runnable);
            runnable.run();
        }
        finally {
            this.exitScope(request);
            ContextHandler.__context.set(old_context);
            if (old_classloader != null) {
                current_thread.setContextClassLoader(old_classloader);
            }
        }
    }
    
    public void handle(final Runnable runnable) {
        this.handle(null, runnable);
    }
    
    public boolean isProtectedTarget(String target) {
        if (target == null || this._protectedTargets == null) {
            return false;
        }
        while (target.startsWith("//")) {
            target = URIUtil.compactPath(target);
        }
        for (int i = 0; i < this._protectedTargets.length; ++i) {
            final String t = this._protectedTargets[i];
            if (StringUtil.startsWithIgnoreCase(target, t)) {
                if (target.length() == t.length()) {
                    return true;
                }
                final char c = target.charAt(t.length());
                if (c == '/' || c == '?' || c == '#' || c == ';') {
                    return true;
                }
            }
        }
        return false;
    }
    
    public void setProtectedTargets(final String[] targets) {
        if (targets == null) {
            this._protectedTargets = null;
            return;
        }
        this._protectedTargets = Arrays.copyOf(targets, targets.length);
    }
    
    public String[] getProtectedTargets() {
        if (this._protectedTargets == null) {
            return null;
        }
        return Arrays.copyOf(this._protectedTargets, this._protectedTargets.length);
    }
    
    @Override
    public void removeAttribute(final String name) {
        this._attributes.removeAttribute(name);
    }
    
    @Override
    public void setAttribute(final String name, final Object value) {
        this._attributes.setAttribute(name, value);
    }
    
    public void setAttributes(final Attributes attributes) {
        this._attributes.clearAttributes();
        this._attributes.addAll(attributes);
    }
    
    @Override
    public void clearAttributes() {
        this._attributes.clearAttributes();
    }
    
    public void setManagedAttribute(final String name, final Object value) {
        final Object old = this._managedAttributes.put(name, value);
        this.updateBean(old, value);
    }
    
    public void setClassLoader(final ClassLoader classLoader) {
        this._classLoader = classLoader;
    }
    
    public void setContextPath(String contextPath) {
        if (contextPath == null) {
            throw new IllegalArgumentException("null contextPath");
        }
        if (contextPath.endsWith("/*")) {
            ContextHandler.LOG.warn(this + " contextPath ends with /*", new Object[0]);
            contextPath = contextPath.substring(0, contextPath.length() - 2);
        }
        else if (contextPath.length() > 1 && contextPath.endsWith("/")) {
            ContextHandler.LOG.warn(this + " contextPath ends with /", new Object[0]);
            contextPath = contextPath.substring(0, contextPath.length() - 1);
        }
        if (contextPath.length() == 0) {
            ContextHandler.LOG.warn("Empty contextPath", new Object[0]);
            contextPath = "/";
        }
        this._contextPath = contextPath;
        if (this.getServer() != null && (this.getServer().isStarting() || this.getServer().isStarted())) {
            final Handler[] contextCollections = this.getServer().getChildHandlersByClass(ContextHandlerCollection.class);
            for (int h = 0; contextCollections != null && h < contextCollections.length; ++h) {
                ((ContextHandlerCollection)contextCollections[h]).mapContexts();
            }
        }
    }
    
    public void setDisplayName(final String servletContextName) {
        this._displayName = servletContextName;
    }
    
    public Resource getBaseResource() {
        if (this._baseResource == null) {
            return null;
        }
        return this._baseResource;
    }
    
    @ManagedAttribute("document root for context")
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
            this.setBaseResource(this.newResource(resourceBase));
        }
        catch (Exception e) {
            ContextHandler.LOG.warn(e.toString(), new Object[0]);
            ContextHandler.LOG.debug(e);
            throw new IllegalArgumentException(resourceBase);
        }
    }
    
    public MimeTypes getMimeTypes() {
        if (this._mimeTypes == null) {
            this._mimeTypes = new MimeTypes();
        }
        return this._mimeTypes;
    }
    
    public void setMimeTypes(final MimeTypes mimeTypes) {
        this._mimeTypes = mimeTypes;
    }
    
    public void setWelcomeFiles(final String[] files) {
        this._welcomeFiles = files;
    }
    
    @ManagedAttribute(value = "Partial URIs of directory welcome files", readonly = true)
    public String[] getWelcomeFiles() {
        return this._welcomeFiles;
    }
    
    @ManagedAttribute("The error handler to use for the context")
    public ErrorHandler getErrorHandler() {
        return this._errorHandler;
    }
    
    public void setErrorHandler(final ErrorHandler errorHandler) {
        if (errorHandler != null) {
            errorHandler.setServer(this.getServer());
        }
        this.updateBean(this._errorHandler, errorHandler, true);
        this._errorHandler = errorHandler;
    }
    
    @ManagedAttribute("The maximum content size")
    public int getMaxFormContentSize() {
        return this._maxFormContentSize;
    }
    
    public void setMaxFormContentSize(final int maxSize) {
        this._maxFormContentSize = maxSize;
    }
    
    public int getMaxFormKeys() {
        return this._maxFormKeys;
    }
    
    public void setMaxFormKeys(final int max) {
        this._maxFormKeys = max;
    }
    
    public boolean isCompactPath() {
        return this._compactPath;
    }
    
    public void setCompactPath(final boolean compactPath) {
        this._compactPath = compactPath;
    }
    
    @Override
    public String toString() {
        final String[] vhosts = this.getVirtualHosts();
        final StringBuilder b = new StringBuilder();
        final Package pkg = this.getClass().getPackage();
        if (pkg != null) {
            final String p = pkg.getName();
            if (p != null && p.length() > 0) {
                final String[] split;
                final String[] ss = split = p.split("\\.");
                for (final String s : split) {
                    b.append(s.charAt(0)).append('.');
                }
            }
        }
        b.append(this.getClass().getSimpleName()).append('@').append(Integer.toString(this.hashCode(), 16));
        b.append('{').append(this.getContextPath()).append(',').append(this.getBaseResource()).append(',').append(this._availability);
        if (vhosts != null && vhosts.length > 0) {
            b.append(',').append(vhosts[0]);
        }
        b.append('}');
        return b.toString();
    }
    
    public synchronized Class<?> loadClass(final String className) throws ClassNotFoundException {
        if (className == null) {
            return null;
        }
        if (this._classLoader == null) {
            return (Class<?>)Loader.loadClass(this.getClass(), className);
        }
        return this._classLoader.loadClass(className);
    }
    
    public void addLocaleEncoding(final String locale, final String encoding) {
        if (this._localeEncodingMap == null) {
            this._localeEncodingMap = new HashMap<String, String>();
        }
        this._localeEncodingMap.put(locale, encoding);
    }
    
    public String getLocaleEncoding(final String locale) {
        if (this._localeEncodingMap == null) {
            return null;
        }
        final String encoding = this._localeEncodingMap.get(locale);
        return encoding;
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
    
    public Map<String, String> getLocaleEncodings() {
        if (this._localeEncodingMap == null) {
            return null;
        }
        return Collections.unmodifiableMap((Map<? extends String, ? extends String>)this._localeEncodingMap);
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
            if (this.checkAlias(path, resource)) {
                return resource;
            }
            return null;
        }
        catch (Exception e) {
            ContextHandler.LOG.ignore(e);
            return null;
        }
    }
    
    public boolean checkAlias(final String path, final Resource resource) {
        if (resource.isAlias()) {
            if (ContextHandler.LOG.isDebugEnabled()) {
                ContextHandler.LOG.debug("Aliased resource: " + resource + "~=" + resource.getAlias(), new Object[0]);
            }
            for (final AliasCheck check : this._aliasChecks) {
                if (check.check(path, resource)) {
                    if (ContextHandler.LOG.isDebugEnabled()) {
                        ContextHandler.LOG.debug("Aliased resource: " + resource + " approved by " + check, new Object[0]);
                    }
                    return true;
                }
            }
            return false;
        }
        return true;
    }
    
    public Resource newResource(final URL url) throws IOException {
        return Resource.newResource(url);
    }
    
    public Resource newResource(final URI uri) throws IOException {
        return Resource.newResource(uri);
    }
    
    public Resource newResource(final String urlOrPath) throws IOException {
        return Resource.newResource(urlOrPath);
    }
    
    public Set<String> getResourcePaths(String path) {
        try {
            path = URIUtil.canonicalPath(path);
            final Resource resource = this.getResource(path);
            if (resource != null && resource.exists()) {
                if (!path.endsWith("/")) {
                    path += "/";
                }
                final String[] l = resource.list();
                if (l != null) {
                    final HashSet<String> set = new HashSet<String>();
                    for (int i = 0; i < l.length; ++i) {
                        set.add(path + l[i]);
                    }
                    return set;
                }
            }
        }
        catch (Exception e) {
            ContextHandler.LOG.ignore(e);
        }
        return Collections.emptySet();
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
    
    public void addAliasCheck(final AliasCheck check) {
        this._aliasChecks.add(check);
    }
    
    public List<AliasCheck> getAliasChecks() {
        return this._aliasChecks;
    }
    
    public void setAliasChecks(final List<AliasCheck> checks) {
        this._aliasChecks.clear();
        this._aliasChecks.addAll(checks);
    }
    
    public void clearAliasChecks() {
        this._aliasChecks.clear();
    }
    
    static {
        SERVLET_LISTENER_TYPES = new Class[] { ServletContextListener.class, ServletContextAttributeListener.class, ServletRequestListener.class, ServletRequestAttributeListener.class };
        LOG = Log.getLogger(ContextHandler.class);
        __context = new ThreadLocal<Context>();
        ContextHandler.__serverInfo = "jetty/" + Server.getVersion();
    }
    
    public enum Availability
    {
        UNAVAILABLE, 
        STARTING, 
        AVAILABLE, 
        SHUTDOWN;
    }
    
    public class Context extends StaticContext
    {
        protected boolean _enabled;
        protected boolean _extendedListenerTypes;
        
        protected Context() {
            this._enabled = true;
            this._extendedListenerTypes = false;
        }
        
        public ContextHandler getContextHandler() {
            return ContextHandler.this;
        }
        
        @Override
        public ServletContext getContext(final String uripath) {
            final List<ContextHandler> contexts = new ArrayList<ContextHandler>();
            final Handler[] handlers = ContextHandler.this.getServer().getChildHandlersByClass(ContextHandler.class);
            String matched_path = null;
            for (final Handler handler : handlers) {
                if (handler != null) {
                    final ContextHandler ch = (ContextHandler)handler;
                    final String context_path = ch.getContextPath();
                    if (uripath.equals(context_path) || (uripath.startsWith(context_path) && uripath.charAt(context_path.length()) == '/') || "/".equals(context_path)) {
                        if (ContextHandler.this.getVirtualHosts() != null && ContextHandler.this.getVirtualHosts().length > 0) {
                            if (ch.getVirtualHosts() != null && ch.getVirtualHosts().length > 0) {
                                for (final String h1 : ContextHandler.this.getVirtualHosts()) {
                                    for (final String h2 : ch.getVirtualHosts()) {
                                        if (h1.equals(h2)) {
                                            if (matched_path == null || context_path.length() > matched_path.length()) {
                                                contexts.clear();
                                                matched_path = context_path;
                                            }
                                            if (matched_path.equals(context_path)) {
                                                contexts.add(ch);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        else {
                            if (matched_path == null || context_path.length() > matched_path.length()) {
                                contexts.clear();
                                matched_path = context_path;
                            }
                            if (matched_path.equals(context_path)) {
                                contexts.add(ch);
                            }
                        }
                    }
                }
            }
            if (contexts.size() > 0) {
                return contexts.get(0)._scontext;
            }
            matched_path = null;
            for (final Handler handler : handlers) {
                if (handler != null) {
                    final ContextHandler ch = (ContextHandler)handler;
                    final String context_path = ch.getContextPath();
                    if (uripath.equals(context_path) || (uripath.startsWith(context_path) && uripath.charAt(context_path.length()) == '/') || "/".equals(context_path)) {
                        if (matched_path == null || context_path.length() > matched_path.length()) {
                            contexts.clear();
                            matched_path = context_path;
                        }
                        if (matched_path != null && matched_path.equals(context_path)) {
                            contexts.add(ch);
                        }
                    }
                }
            }
            if (contexts.size() > 0) {
                return contexts.get(0)._scontext;
            }
            return null;
        }
        
        @Override
        public String getMimeType(final String file) {
            if (ContextHandler.this._mimeTypes == null) {
                return null;
            }
            return ContextHandler.this._mimeTypes.getMimeByExtension(file);
        }
        
        @Override
        public RequestDispatcher getRequestDispatcher(final String uriInContext) {
            if (uriInContext == null) {
                return null;
            }
            if (!uriInContext.startsWith("/")) {
                return null;
            }
            try {
                final HttpURI uri = new HttpURI(null, null, 0, uriInContext);
                final String pathInfo = URIUtil.canonicalPath(uri.getDecodedPath());
                if (pathInfo == null) {
                    return null;
                }
                final String contextPath = this.getContextPath();
                if (contextPath != null && contextPath.length() > 0) {
                    uri.setPath(URIUtil.addPaths(contextPath, uri.getPath()));
                }
                return new Dispatcher(ContextHandler.this, uri, pathInfo);
            }
            catch (Exception e) {
                ContextHandler.LOG.ignore(e);
                return null;
            }
        }
        
        @Override
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
                ContextHandler.LOG.ignore(e);
            }
            return null;
        }
        
        @Override
        public URL getResource(final String path) throws MalformedURLException {
            final Resource resource = ContextHandler.this.getResource(path);
            if (resource != null && resource.exists()) {
                return resource.getURI().toURL();
            }
            return null;
        }
        
        @Override
        public InputStream getResourceAsStream(final String path) {
            try {
                final URL url = this.getResource(path);
                if (url == null) {
                    return null;
                }
                final Resource r = Resource.newResource(url);
                if (r.isDirectory()) {
                    return null;
                }
                return r.getInputStream();
            }
            catch (Exception e) {
                ContextHandler.LOG.ignore(e);
                return null;
            }
        }
        
        @Override
        public Set<String> getResourcePaths(final String path) {
            return ContextHandler.this.getResourcePaths(path);
        }
        
        @Override
        public void log(final Exception exception, final String msg) {
            ContextHandler.this._logger.warn(msg, exception);
        }
        
        @Override
        public void log(final String msg) {
            ContextHandler.this._logger.info(msg, new Object[0]);
        }
        
        @Override
        public void log(final String message, final Throwable throwable) {
            ContextHandler.this._logger.warn(message, throwable);
        }
        
        @Override
        public String getInitParameter(final String name) {
            return ContextHandler.this.getInitParameter(name);
        }
        
        @Override
        public Enumeration<String> getInitParameterNames() {
            return ContextHandler.this.getInitParameterNames();
        }
        
        @Override
        public synchronized Object getAttribute(final String name) {
            Object o = ContextHandler.this.getAttribute(name);
            if (o == null) {
                o = super.getAttribute(name);
            }
            return o;
        }
        
        @Override
        public synchronized Enumeration<String> getAttributeNames() {
            final HashSet<String> set = new HashSet<String>();
            Enumeration<String> e = super.getAttributeNames();
            while (e.hasMoreElements()) {
                set.add(e.nextElement());
            }
            e = ContextHandler.this._attributes.getAttributeNames();
            while (e.hasMoreElements()) {
                set.add(e.nextElement());
            }
            return Collections.enumeration(set);
        }
        
        @Override
        public synchronized void setAttribute(final String name, final Object value) {
            final Object old_value = super.getAttribute(name);
            if (value == null) {
                super.removeAttribute(name);
            }
            else {
                super.setAttribute(name, value);
            }
            if (!ContextHandler.this._servletContextAttributeListeners.isEmpty()) {
                final ServletContextAttributeEvent event = new ServletContextAttributeEvent(ContextHandler.this._scontext, name, (old_value == null) ? value : old_value);
                for (final ServletContextAttributeListener l : ContextHandler.this._servletContextAttributeListeners) {
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
        
        @Override
        public synchronized void removeAttribute(final String name) {
            final Object old_value = super.getAttribute(name);
            super.removeAttribute(name);
            if (old_value != null && !ContextHandler.this._servletContextAttributeListeners.isEmpty()) {
                final ServletContextAttributeEvent event = new ServletContextAttributeEvent(ContextHandler.this._scontext, name, old_value);
                for (final ServletContextAttributeListener l : ContextHandler.this._servletContextAttributeListeners) {
                    l.attributeRemoved(event);
                }
            }
        }
        
        @Override
        public String getServletContextName() {
            String name = ContextHandler.this.getDisplayName();
            if (name == null) {
                name = ContextHandler.this.getContextPath();
            }
            return name;
        }
        
        @Override
        public String getContextPath() {
            if (ContextHandler.this._contextPath != null && ContextHandler.this._contextPath.equals("/")) {
                return "";
            }
            return ContextHandler.this._contextPath;
        }
        
        @Override
        public String toString() {
            return "ServletContext@" + ContextHandler.this.toString();
        }
        
        @Override
        public boolean setInitParameter(final String name, final String value) {
            if (ContextHandler.this.getInitParameter(name) != null) {
                return false;
            }
            ContextHandler.this.getInitParams().put(name, value);
            return true;
        }
        
        @Override
        public void addListener(final String className) {
            if (!this._enabled) {
                throw new UnsupportedOperationException();
            }
            try {
                final Class<? extends EventListener> clazz = (Class<? extends EventListener>)((ContextHandler.this._classLoader == null) ? Loader.loadClass(ContextHandler.class, className) : ContextHandler.this._classLoader.loadClass(className));
                this.addListener(clazz);
            }
            catch (ClassNotFoundException e) {
                throw new IllegalArgumentException(e);
            }
        }
        
        @Override
        public <T extends EventListener> void addListener(final T t) {
            if (!this._enabled) {
                throw new UnsupportedOperationException();
            }
            this.checkListener(t.getClass());
            ContextHandler.this.addEventListener(t);
            ContextHandler.this.addProgrammaticListener(t);
        }
        
        @Override
        public void addListener(final Class<? extends EventListener> listenerClass) {
            if (!this._enabled) {
                throw new UnsupportedOperationException();
            }
            try {
                final EventListener e = this.createListener(listenerClass);
                this.addListener(e);
            }
            catch (ServletException e2) {
                throw new IllegalArgumentException(e2);
            }
        }
        
        @Override
        public <T extends EventListener> T createListener(final Class<T> clazz) throws ServletException {
            try {
                return this.createInstance(clazz);
            }
            catch (Exception e) {
                throw new ServletException(e);
            }
        }
        
        public void checkListener(final Class<? extends EventListener> listener) throws IllegalStateException {
            boolean ok = false;
            int i;
            for (int startIndex = i = (this.isExtendedListenerTypes() ? 0 : 1); i < ContextHandler.SERVLET_LISTENER_TYPES.length; ++i) {
                if (ContextHandler.SERVLET_LISTENER_TYPES[i].isAssignableFrom(listener)) {
                    ok = true;
                    break;
                }
            }
            if (!ok) {
                throw new IllegalArgumentException("Inappropriate listener class " + listener.getName());
            }
        }
        
        public void setExtendedListenerTypes(final boolean extended) {
            this._extendedListenerTypes = extended;
        }
        
        public boolean isExtendedListenerTypes() {
            return this._extendedListenerTypes;
        }
        
        @Override
        public ClassLoader getClassLoader() {
            if (!this._enabled) {
                throw new UnsupportedOperationException();
            }
            if (!ContextHandler.this._usingSecurityManager) {
                return ContextHandler.this._classLoader;
            }
            try {
                final Class<?> reflect = (Class<?>)Loader.loadClass(this.getClass(), "sun.reflect.Reflection");
                final Method getCallerClass = reflect.getMethod("getCallerClass", Integer.TYPE);
                final Class<?> caller = (Class<?>)getCallerClass.invoke(null, 2);
                boolean ok = false;
                ClassLoader callerLoader = caller.getClassLoader();
                while (!ok && callerLoader != null) {
                    if (callerLoader == ContextHandler.this._classLoader) {
                        ok = true;
                    }
                    else {
                        callerLoader = callerLoader.getParent();
                    }
                }
                if (ok) {
                    return ContextHandler.this._classLoader;
                }
            }
            catch (Exception e) {
                ContextHandler.LOG.warn("Unable to check classloader of caller", e);
            }
            AccessController.checkPermission(new RuntimePermission("getClassLoader"));
            return ContextHandler.this._classLoader;
        }
        
        @Override
        public JspConfigDescriptor getJspConfigDescriptor() {
            ContextHandler.LOG.warn("Unimplemented - use org.eclipse.jetty.servlet.ServletContextHandler", new Object[0]);
            return null;
        }
        
        public void setJspConfigDescriptor(final JspConfigDescriptor d) {
        }
        
        @Override
        public void declareRoles(final String... roleNames) {
            if (!ContextHandler.this.isStarting()) {
                throw new IllegalStateException();
            }
            if (!this._enabled) {
                throw new UnsupportedOperationException();
            }
        }
        
        public void setEnabled(final boolean enabled) {
            this._enabled = enabled;
        }
        
        public boolean isEnabled() {
            return this._enabled;
        }
        
        public <T> T createInstance(final Class<T> clazz) throws Exception {
            final T o = clazz.newInstance();
            return o;
        }
        
        @Override
        public String getVirtualServerName() {
            final String[] hosts = ContextHandler.this.getVirtualHosts();
            if (hosts != null && hosts.length > 0) {
                return hosts[0];
            }
            return null;
        }
    }
    
    public static class StaticContext extends AttributesMap implements ServletContext
    {
        private int _effectiveMajorVersion;
        private int _effectiveMinorVersion;
        
        public StaticContext() {
            this._effectiveMajorVersion = 3;
            this._effectiveMinorVersion = 1;
        }
        
        @Override
        public ServletContext getContext(final String uripath) {
            return null;
        }
        
        @Override
        public int getMajorVersion() {
            return 3;
        }
        
        @Override
        public String getMimeType(final String file) {
            return null;
        }
        
        @Override
        public int getMinorVersion() {
            return 1;
        }
        
        @Override
        public RequestDispatcher getNamedDispatcher(final String name) {
            return null;
        }
        
        @Override
        public RequestDispatcher getRequestDispatcher(final String uriInContext) {
            return null;
        }
        
        @Override
        public String getRealPath(final String path) {
            return null;
        }
        
        @Override
        public URL getResource(final String path) throws MalformedURLException {
            return null;
        }
        
        @Override
        public InputStream getResourceAsStream(final String path) {
            return null;
        }
        
        @Override
        public Set<String> getResourcePaths(final String path) {
            return null;
        }
        
        @Override
        public String getServerInfo() {
            return ContextHandler.__serverInfo;
        }
        
        @Deprecated
        @Override
        public Servlet getServlet(final String name) throws ServletException {
            return null;
        }
        
        @Deprecated
        @Override
        public Enumeration<String> getServletNames() {
            return Collections.enumeration((Collection<String>)Collections.EMPTY_LIST);
        }
        
        @Deprecated
        @Override
        public Enumeration<Servlet> getServlets() {
            return Collections.enumeration((Collection<Servlet>)Collections.EMPTY_LIST);
        }
        
        @Override
        public void log(final Exception exception, final String msg) {
            ContextHandler.LOG.warn(msg, exception);
        }
        
        @Override
        public void log(final String msg) {
            ContextHandler.LOG.info(msg, new Object[0]);
        }
        
        @Override
        public void log(final String message, final Throwable throwable) {
            ContextHandler.LOG.warn(message, throwable);
        }
        
        @Override
        public String getInitParameter(final String name) {
            return null;
        }
        
        @Override
        public Enumeration<String> getInitParameterNames() {
            return Collections.enumeration((Collection<String>)Collections.EMPTY_LIST);
        }
        
        @Override
        public String getServletContextName() {
            return "No Context";
        }
        
        @Override
        public String getContextPath() {
            return null;
        }
        
        @Override
        public boolean setInitParameter(final String name, final String value) {
            return false;
        }
        
        @Override
        public FilterRegistration.Dynamic addFilter(final String filterName, final Class<? extends Filter> filterClass) {
            ContextHandler.LOG.warn("Unimplemented - use org.eclipse.jetty.servlet.ServletContextHandler", new Object[0]);
            return null;
        }
        
        @Override
        public FilterRegistration.Dynamic addFilter(final String filterName, final Filter filter) {
            ContextHandler.LOG.warn("Unimplemented - use org.eclipse.jetty.servlet.ServletContextHandler", new Object[0]);
            return null;
        }
        
        @Override
        public FilterRegistration.Dynamic addFilter(final String filterName, final String className) {
            ContextHandler.LOG.warn("Unimplemented - use org.eclipse.jetty.servlet.ServletContextHandler", new Object[0]);
            return null;
        }
        
        @Override
        public ServletRegistration.Dynamic addServlet(final String servletName, final Class<? extends Servlet> servletClass) {
            ContextHandler.LOG.warn("Unimplemented - use org.eclipse.jetty.servlet.ServletContextHandler", new Object[0]);
            return null;
        }
        
        @Override
        public ServletRegistration.Dynamic addServlet(final String servletName, final Servlet servlet) {
            ContextHandler.LOG.warn("Unimplemented - use org.eclipse.jetty.servlet.ServletContextHandler", new Object[0]);
            return null;
        }
        
        @Override
        public ServletRegistration.Dynamic addServlet(final String servletName, final String className) {
            ContextHandler.LOG.warn("Unimplemented - use org.eclipse.jetty.servlet.ServletContextHandler", new Object[0]);
            return null;
        }
        
        @Override
        public <T extends Filter> T createFilter(final Class<T> c) throws ServletException {
            ContextHandler.LOG.warn("Unimplemented - use org.eclipse.jetty.servlet.ServletContextHandler", new Object[0]);
            return null;
        }
        
        @Override
        public <T extends Servlet> T createServlet(final Class<T> c) throws ServletException {
            ContextHandler.LOG.warn("Unimplemented - use org.eclipse.jetty.servlet.ServletContextHandler", new Object[0]);
            return null;
        }
        
        @Override
        public Set<SessionTrackingMode> getDefaultSessionTrackingModes() {
            ContextHandler.LOG.warn("Unimplemented - use org.eclipse.jetty.servlet.ServletContextHandler", new Object[0]);
            return null;
        }
        
        @Override
        public Set<SessionTrackingMode> getEffectiveSessionTrackingModes() {
            ContextHandler.LOG.warn("Unimplemented - use org.eclipse.jetty.servlet.ServletContextHandler", new Object[0]);
            return null;
        }
        
        @Override
        public FilterRegistration getFilterRegistration(final String filterName) {
            ContextHandler.LOG.warn("Unimplemented - use org.eclipse.jetty.servlet.ServletContextHandler", new Object[0]);
            return null;
        }
        
        @Override
        public Map<String, ? extends FilterRegistration> getFilterRegistrations() {
            ContextHandler.LOG.warn("Unimplemented - use org.eclipse.jetty.servlet.ServletContextHandler", new Object[0]);
            return null;
        }
        
        @Override
        public ServletRegistration getServletRegistration(final String servletName) {
            ContextHandler.LOG.warn("Unimplemented - use org.eclipse.jetty.servlet.ServletContextHandler", new Object[0]);
            return null;
        }
        
        @Override
        public Map<String, ? extends ServletRegistration> getServletRegistrations() {
            ContextHandler.LOG.warn("Unimplemented - use org.eclipse.jetty.servlet.ServletContextHandler", new Object[0]);
            return null;
        }
        
        @Override
        public SessionCookieConfig getSessionCookieConfig() {
            ContextHandler.LOG.warn("Unimplemented - use org.eclipse.jetty.servlet.ServletContextHandler", new Object[0]);
            return null;
        }
        
        @Override
        public void setSessionTrackingModes(final Set<SessionTrackingMode> sessionTrackingModes) {
            ContextHandler.LOG.warn("Unimplemented - use org.eclipse.jetty.servlet.ServletContextHandler", new Object[0]);
        }
        
        @Override
        public void addListener(final String className) {
            ContextHandler.LOG.warn("Unimplemented - use org.eclipse.jetty.servlet.ServletContextHandler", new Object[0]);
        }
        
        @Override
        public <T extends EventListener> void addListener(final T t) {
            ContextHandler.LOG.warn("Unimplemented - use org.eclipse.jetty.servlet.ServletContextHandler", new Object[0]);
        }
        
        @Override
        public void addListener(final Class<? extends EventListener> listenerClass) {
            ContextHandler.LOG.warn("Unimplemented - use org.eclipse.jetty.servlet.ServletContextHandler", new Object[0]);
        }
        
        @Override
        public <T extends EventListener> T createListener(final Class<T> clazz) throws ServletException {
            try {
                return clazz.newInstance();
            }
            catch (InstantiationException e) {
                throw new ServletException(e);
            }
            catch (IllegalAccessException e2) {
                throw new ServletException(e2);
            }
        }
        
        @Override
        public ClassLoader getClassLoader() {
            return ContextHandler.class.getClassLoader();
        }
        
        @Override
        public int getEffectiveMajorVersion() {
            return this._effectiveMajorVersion;
        }
        
        @Override
        public int getEffectiveMinorVersion() {
            return this._effectiveMinorVersion;
        }
        
        public void setEffectiveMajorVersion(final int v) {
            this._effectiveMajorVersion = v;
        }
        
        public void setEffectiveMinorVersion(final int v) {
            this._effectiveMinorVersion = v;
        }
        
        @Override
        public JspConfigDescriptor getJspConfigDescriptor() {
            ContextHandler.LOG.warn("Unimplemented - use org.eclipse.jetty.servlet.ServletContextHandler", new Object[0]);
            return null;
        }
        
        @Override
        public void declareRoles(final String... roleNames) {
            ContextHandler.LOG.warn("Unimplemented - use org.eclipse.jetty.servlet.ServletContextHandler", new Object[0]);
        }
        
        @Override
        public String getVirtualServerName() {
            return null;
        }
    }
    
    public static class ApproveAliases implements AliasCheck
    {
        @Override
        public boolean check(final String path, final Resource resource) {
            return true;
        }
    }
    
    public static class ApproveNonExistentDirectoryAliases implements AliasCheck
    {
        @Override
        public boolean check(final String path, final Resource resource) {
            if (resource.exists()) {
                return false;
            }
            final String a = resource.getAlias().toString();
            final String r = resource.getURI().toString();
            if (a.length() > r.length()) {
                return a.startsWith(r) && a.length() == r.length() + 1 && a.endsWith("/");
            }
            if (a.length() < r.length()) {
                return r.startsWith(a) && r.length() == a.length() + 1 && r.endsWith("/");
            }
            return a.equals(r);
        }
    }
    
    public interface ContextScopeListener extends EventListener
    {
        void enterScope(final Context p0, final Request p1, final Object p2);
        
        void exitScope(final Context p0, final Request p1);
    }
    
    public interface AliasCheck
    {
        boolean check(final String p0, final Resource p1);
    }
}
