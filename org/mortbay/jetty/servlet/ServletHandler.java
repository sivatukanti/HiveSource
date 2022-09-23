// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.jetty.servlet;

import java.util.LinkedHashMap;
import javax.servlet.Filter;
import javax.servlet.Servlet;
import java.util.ArrayList;
import java.util.Arrays;
import org.mortbay.util.MultiException;
import javax.servlet.FilterChain;
import org.mortbay.jetty.HttpException;
import javax.servlet.UnavailableException;
import javax.servlet.ServletException;
import java.io.IOException;
import org.mortbay.io.RuntimeIOException;
import org.mortbay.jetty.EofException;
import org.mortbay.jetty.RetryRequest;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequestListener;
import org.mortbay.util.LazyList;
import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestEvent;
import org.mortbay.jetty.HttpConnection;
import org.mortbay.jetty.Request;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletContext;
import org.mortbay.util.URIUtil;
import javax.servlet.RequestDispatcher;
import org.mortbay.log.Log;
import org.mortbay.jetty.Server;
import java.util.HashMap;
import org.mortbay.util.MultiMap;
import java.util.List;
import java.util.Map;
import org.mortbay.jetty.handler.ContextHandler;
import org.mortbay.jetty.handler.AbstractHandler;

public class ServletHandler extends AbstractHandler
{
    public static final String __DEFAULT_SERVLET = "default";
    public static final String __J_S_CONTEXT_TEMPDIR = "javax.servlet.context.tempdir";
    public static final String __J_S_ERROR_EXCEPTION = "javax.servlet.error.exception";
    public static final String __J_S_ERROR_EXCEPTION_TYPE = "javax.servlet.error.exception_type";
    public static final String __J_S_ERROR_MESSAGE = "javax.servlet.error.message";
    public static final String __J_S_ERROR_REQUEST_URI = "javax.servlet.error.request_uri";
    public static final String __J_S_ERROR_SERVLET_NAME = "javax.servlet.error.servlet_name";
    public static final String __J_S_ERROR_STATUS_CODE = "javax.servlet.error.status_code";
    private ContextHandler _contextHandler;
    private ContextHandler.SContext _servletContext;
    private FilterHolder[] _filters;
    private FilterMapping[] _filterMappings;
    private boolean _filterChainsCached;
    private int _maxFilterChainsCacheSize;
    private boolean _startWithUnavailable;
    private ServletHolder[] _servlets;
    private ServletMapping[] _servletMappings;
    private transient Map _filterNameMap;
    private transient List _filterPathMappings;
    private transient MultiMap _filterNameMappings;
    private transient Map _servletNameMap;
    private transient PathMap _servletPathMap;
    protected transient MruCache[] _chainCache;
    
    public ServletHandler() {
        this._filterChainsCached = true;
        this._maxFilterChainsCacheSize = 1000;
        this._startWithUnavailable = true;
        this._filterNameMap = new HashMap();
        this._servletNameMap = new HashMap();
    }
    
    public void setServer(final Server server) {
        if (this.getServer() != null && this.getServer() != server) {
            this.getServer().getContainer().update(this, this._filters, null, "filter", true);
            this.getServer().getContainer().update(this, this._filterMappings, null, "filterMapping", true);
            this.getServer().getContainer().update(this, this._servlets, null, "servlet", true);
            this.getServer().getContainer().update(this, this._servletMappings, null, "servletMapping", true);
        }
        if (server != null && this.getServer() != server) {
            server.getContainer().update(this, null, this._filters, "filter", true);
            server.getContainer().update(this, null, this._filterMappings, "filterMapping", true);
            server.getContainer().update(this, null, this._servlets, "servlet", true);
            server.getContainer().update(this, null, this._servletMappings, "servletMapping", true);
        }
        super.setServer(server);
        this.invalidateChainsCache();
    }
    
    protected synchronized void doStart() throws Exception {
        this._servletContext = ContextHandler.getCurrentContext();
        this._contextHandler = ((this._servletContext == null) ? null : this._servletContext.getContextHandler());
        this.updateNameMappings();
        this.updateMappings();
        if (this._filterChainsCached) {
            this._chainCache = new MruCache[] { null, new MruCache(this._maxFilterChainsCacheSize), new MruCache(this._maxFilterChainsCacheSize), null, new MruCache(this._maxFilterChainsCacheSize), null, null, null, new MruCache(this._maxFilterChainsCacheSize) };
        }
        super.doStart();
        if (this._contextHandler == null || !(this._contextHandler instanceof Context)) {
            this.initialize();
        }
    }
    
    protected synchronized void doStop() throws Exception {
        super.doStop();
        if (this._filters != null) {
            int i = this._filters.length;
            while (i-- > 0) {
                try {
                    this._filters[i].stop();
                }
                catch (Exception e) {
                    Log.warn("EXCEPTION ", e);
                }
            }
        }
        if (this._servlets != null) {
            int i = this._servlets.length;
            while (i-- > 0) {
                try {
                    this._servlets[i].stop();
                }
                catch (Exception e) {
                    Log.warn("EXCEPTION ", e);
                }
            }
        }
        this._filterPathMappings = null;
        this._filterNameMappings = null;
        this._servletPathMap = null;
        this._chainCache = null;
    }
    
    public Object getContextLog() {
        return null;
    }
    
    public FilterMapping[] getFilterMappings() {
        return this._filterMappings;
    }
    
    public FilterHolder[] getFilters() {
        return this._filters;
    }
    
    public PathMap.Entry getHolderEntry(final String pathInContext) {
        if (this._servletPathMap == null) {
            return null;
        }
        return this._servletPathMap.getMatch(pathInContext);
    }
    
    public boolean matchesPath(final String pathInContext) {
        return this._servletPathMap.containsMatch(pathInContext);
    }
    
    public RequestDispatcher getRequestDispatcher(String uriInContext) {
        if (uriInContext == null) {
            return null;
        }
        if (!uriInContext.startsWith("/")) {
            return null;
        }
        try {
            String query = null;
            int q = 0;
            if ((q = uriInContext.indexOf(63)) > 0) {
                query = uriInContext.substring(q + 1);
                uriInContext = uriInContext.substring(0, q);
            }
            if ((q = uriInContext.indexOf(59)) > 0) {
                uriInContext = uriInContext.substring(0, q);
            }
            final String pathInContext = URIUtil.canonicalPath(URIUtil.decodePath(uriInContext));
            final String uri = URIUtil.addPaths(this._contextHandler.getContextPath(), uriInContext);
            return new Dispatcher(this._contextHandler, uri, pathInContext, query);
        }
        catch (Exception e) {
            Log.ignore(e);
            return null;
        }
    }
    
    public ServletContext getServletContext() {
        return this._servletContext;
    }
    
    public ServletMapping[] getServletMappings() {
        return this._servletMappings;
    }
    
    public ServletHolder[] getServlets() {
        return this._servlets;
    }
    
    public ServletHolder getServlet(final String name) {
        return this._servletNameMap.get(name);
    }
    
    public void handle(final String target, final HttpServletRequest request, final HttpServletResponse response, final int type) throws IOException, ServletException {
        if (!this.isStarted()) {
            return;
        }
        final Request base_request = (Request)((request instanceof Request) ? request : HttpConnection.getCurrentConnection().getRequest());
        final String old_servlet_name = base_request.getServletName();
        final String old_servlet_path = base_request.getServletPath();
        final String old_path_info = base_request.getPathInfo();
        final Map old_role_map = base_request.getRoleMap();
        Object request_listeners = null;
        ServletRequestEvent request_event = null;
        try {
            ServletHolder servlet_holder = null;
            FilterChain chain = null;
            if (target.startsWith("/")) {
                final PathMap.Entry entry = this.getHolderEntry(target);
                if (entry != null) {
                    servlet_holder = (ServletHolder)entry.getValue();
                    base_request.setServletName(servlet_holder.getName());
                    base_request.setRoleMap(servlet_holder.getRoleMap());
                    if (Log.isDebugEnabled()) {
                        Log.debug("servlet=" + servlet_holder);
                    }
                    final String servlet_path_spec = (String)entry.getKey();
                    final String servlet_path = (entry.getMapped() != null) ? entry.getMapped() : PathMap.pathMatch(servlet_path_spec, target);
                    final String path_info = PathMap.pathInfo(servlet_path_spec, target);
                    if (type == 4) {
                        base_request.setAttribute("javax.servlet.include.servlet_path", servlet_path);
                        base_request.setAttribute("javax.servlet.include.path_info", path_info);
                    }
                    else {
                        base_request.setServletPath(servlet_path);
                        base_request.setPathInfo(path_info);
                    }
                    if (servlet_holder != null && this._filterMappings != null && this._filterMappings.length > 0) {
                        chain = this.getFilterChain(type, target, servlet_holder);
                    }
                }
            }
            else {
                servlet_holder = this._servletNameMap.get(target);
                if (servlet_holder != null && this._filterMappings != null && this._filterMappings.length > 0) {
                    base_request.setServletName(servlet_holder.getName());
                    chain = this.getFilterChain(type, null, servlet_holder);
                }
            }
            if (Log.isDebugEnabled()) {
                Log.debug("chain=" + chain);
                Log.debug("servlet holder=" + servlet_holder);
            }
            request_listeners = base_request.takeRequestListeners();
            if (request_listeners != null) {
                request_event = new ServletRequestEvent(this.getServletContext(), request);
                for (int s = LazyList.size(request_listeners), i = 0; i < s; ++i) {
                    final ServletRequestListener listener = (ServletRequestListener)LazyList.get(request_listeners, i);
                    listener.requestInitialized(request_event);
                }
            }
            if (servlet_holder != null) {
                base_request.setHandled(true);
                if (chain != null) {
                    chain.doFilter(request, response);
                }
                else {
                    servlet_holder.handle(request, response);
                }
            }
            else {
                this.notFound(request, response);
            }
        }
        catch (RetryRequest e) {
            base_request.setHandled(false);
            throw e;
        }
        catch (EofException e2) {
            throw e2;
        }
        catch (RuntimeIOException e3) {
            throw e3;
        }
        catch (Exception e4) {
            if (type != 1) {
                if (e4 instanceof IOException) {
                    throw (IOException)e4;
                }
                if (e4 instanceof RuntimeException) {
                    throw (RuntimeException)e4;
                }
                if (e4 instanceof ServletException) {
                    throw (ServletException)e4;
                }
            }
            Throwable th = e4;
            if (th instanceof UnavailableException) {
                Log.debug(th);
            }
            else if (th instanceof ServletException) {
                Log.debug(th);
                final Throwable cause = ((ServletException)th).getRootCause();
                if (cause != th && cause != null) {
                    th = cause;
                }
            }
            if (th instanceof RetryRequest) {
                base_request.setHandled(false);
                throw (RetryRequest)th;
            }
            if (th instanceof HttpException) {
                throw (HttpException)th;
            }
            if (th instanceof RuntimeIOException) {
                throw (RuntimeIOException)th;
            }
            if (th instanceof EofException) {
                throw (EofException)th;
            }
            if (Log.isDebugEnabled()) {
                Log.warn(request.getRequestURI(), th);
                Log.debug(request.toString());
            }
            else if (th instanceof IOException || th instanceof UnavailableException) {
                Log.warn(request.getRequestURI() + ": " + th);
            }
            else {
                Log.warn(request.getRequestURI(), th);
            }
            if (!response.isCommitted()) {
                request.setAttribute("javax.servlet.error.exception_type", th.getClass());
                request.setAttribute("javax.servlet.error.exception", th);
                if (th instanceof UnavailableException) {
                    final UnavailableException ue = (UnavailableException)th;
                    if (ue.isPermanent()) {
                        response.sendError(404, th.getMessage());
                    }
                    else {
                        response.sendError(503, th.getMessage());
                    }
                }
                else {
                    response.sendError(500, th.getMessage());
                }
            }
            else if (Log.isDebugEnabled()) {
                Log.debug("Response already committed for handling " + th);
            }
        }
        catch (Error e5) {
            if (type != 1) {
                throw e5;
            }
            Log.warn("Error for " + request.getRequestURI(), e5);
            if (Log.isDebugEnabled()) {
                Log.debug(request.toString());
            }
            if (!response.isCommitted()) {
                request.setAttribute("javax.servlet.error.exception_type", e5.getClass());
                request.setAttribute("javax.servlet.error.exception", e5);
                response.sendError(500, e5.getMessage());
            }
            else if (Log.isDebugEnabled()) {
                Log.debug("Response already committed for handling ", e5);
            }
        }
        finally {
            if (request_listeners != null) {
                int j = LazyList.size(request_listeners);
                while (j-- > 0) {
                    final ServletRequestListener listener2 = (ServletRequestListener)LazyList.get(request_listeners, j);
                    listener2.requestDestroyed(request_event);
                }
            }
            base_request.setServletName(old_servlet_name);
            base_request.setRoleMap(old_role_map);
            if (type != 4) {
                base_request.setServletPath(old_servlet_path);
                base_request.setPathInfo(old_path_info);
            }
        }
    }
    
    private FilterChain getFilterChain(final int requestType, final String pathInContext, final ServletHolder servletHolder) {
        final String key = (pathInContext == null) ? servletHolder.getName() : pathInContext;
        if (this._filterChainsCached && this._chainCache != null) {
            synchronized (this) {
                if (this._chainCache[requestType].containsKey(key)) {
                    return this._chainCache[requestType].get(key);
                }
            }
        }
        Object filters = null;
        if (pathInContext != null && this._filterPathMappings != null) {
            for (int i = 0; i < this._filterPathMappings.size(); ++i) {
                final FilterMapping mapping = this._filterPathMappings.get(i);
                if (mapping.appliesTo(pathInContext, requestType)) {
                    filters = LazyList.add(filters, mapping.getFilterHolder());
                }
            }
        }
        if (servletHolder != null && this._filterNameMappings != null && this._filterNameMappings.size() > 0 && this._filterNameMappings.size() > 0) {
            Object o = this._filterNameMappings.get(servletHolder.getName());
            for (int j = 0; j < LazyList.size(o); ++j) {
                final FilterMapping mapping2 = (FilterMapping)LazyList.get(o, j);
                if (mapping2.appliesTo(requestType)) {
                    filters = LazyList.add(filters, mapping2.getFilterHolder());
                }
            }
            o = this._filterNameMappings.get("*");
            for (int j = 0; j < LazyList.size(o); ++j) {
                final FilterMapping mapping2 = (FilterMapping)LazyList.get(o, j);
                if (mapping2.appliesTo(requestType)) {
                    filters = LazyList.add(filters, mapping2.getFilterHolder());
                }
            }
        }
        if (filters == null) {
            return null;
        }
        FilterChain chain = null;
        if (this._filterChainsCached) {
            if (LazyList.size(filters) > 0) {
                chain = new CachedChain(filters, servletHolder);
            }
            synchronized (this) {
                this._chainCache[requestType].put(key, chain);
            }
        }
        else if (LazyList.size(filters) > 0) {
            chain = new Chain(filters, servletHolder);
        }
        return chain;
    }
    
    private void invalidateChainsCache() {
        this._chainCache = new MruCache[] { null, new MruCache(this._maxFilterChainsCacheSize), new MruCache(this._maxFilterChainsCacheSize), null, new MruCache(this._maxFilterChainsCacheSize), null, null, null, new MruCache(this._maxFilterChainsCacheSize) };
    }
    
    public boolean isInitializeAtStart() {
        return false;
    }
    
    public void setInitializeAtStart(final boolean initializeAtStart) {
    }
    
    public boolean isAvailable() {
        if (!this.isStarted()) {
            return false;
        }
        final ServletHolder[] holders = this.getServlets();
        for (int i = 0; i < holders.length; ++i) {
            final ServletHolder holder = holders[i];
            if (holder != null && !holder.isAvailable()) {
                return false;
            }
        }
        return true;
    }
    
    public void setStartWithUnavailable(final boolean start) {
        this._startWithUnavailable = start;
    }
    
    public boolean isStartWithUnavailable() {
        return this._startWithUnavailable;
    }
    
    public void initialize() throws Exception {
        final MultiException mx = new MultiException();
        if (this._filters != null) {
            for (int i = 0; i < this._filters.length; ++i) {
                this._filters[i].start();
            }
        }
        if (this._servlets != null) {
            final ServletHolder[] servlets = this._servlets.clone();
            Arrays.sort(servlets);
            for (int j = 0; j < servlets.length; ++j) {
                try {
                    if (servlets[j].getClassName() == null && servlets[j].getForcedPath() != null) {
                        final ServletHolder forced_holder = (ServletHolder)this._servletPathMap.match(servlets[j].getForcedPath());
                        if (forced_holder == null || forced_holder.getClassName() == null) {
                            mx.add(new IllegalStateException("No forced path servlet for " + servlets[j].getForcedPath()));
                            continue;
                        }
                        servlets[j].setClassName(forced_holder.getClassName());
                    }
                    servlets[j].start();
                }
                catch (Throwable e) {
                    Log.debug("EXCEPTION ", e);
                    mx.add(e);
                }
            }
            mx.ifExceptionThrow();
        }
    }
    
    public boolean isFilterChainsCached() {
        return this._filterChainsCached;
    }
    
    public ServletHolder newServletHolder() {
        return new ServletHolder();
    }
    
    public ServletHolder newServletHolder(final Class servlet) {
        return new ServletHolder(servlet);
    }
    
    public ServletHolder addServletWithMapping(final String className, final String pathSpec) {
        final ServletHolder holder = this.newServletHolder(null);
        holder.setName(className + "-" + holder.hashCode());
        holder.setClassName(className);
        this.addServletWithMapping(holder, pathSpec);
        return holder;
    }
    
    public ServletHolder addServletWithMapping(final Class servlet, final String pathSpec) {
        final ServletHolder holder = this.newServletHolder(servlet);
        this.setServlets((ServletHolder[])LazyList.addToArray(this.getServlets(), holder, ServletHolder.class));
        this.addServletWithMapping(holder, pathSpec);
        return holder;
    }
    
    public void addServletWithMapping(final ServletHolder servlet, final String pathSpec) {
        ServletHolder[] holders = this.getServlets();
        if (holders != null) {
            holders = holders.clone();
        }
        try {
            this.setServlets((ServletHolder[])LazyList.addToArray(holders, servlet, ServletHolder.class));
            final ServletMapping mapping = new ServletMapping();
            mapping.setServletName(servlet.getName());
            mapping.setPathSpec(pathSpec);
            this.setServletMappings((ServletMapping[])LazyList.addToArray(this.getServletMappings(), mapping, ServletMapping.class));
        }
        catch (Exception e) {
            this.setServlets(holders);
            if (e instanceof RuntimeException) {
                throw (RuntimeException)e;
            }
            throw new RuntimeException(e);
        }
    }
    
    public ServletHolder addServlet(final String className, final String pathSpec) {
        return this.addServletWithMapping(className, pathSpec);
    }
    
    public void addServlet(final ServletHolder holder) {
        this.setServlets((ServletHolder[])LazyList.addToArray(this.getServlets(), holder, ServletHolder.class));
    }
    
    public void addServletMapping(final ServletMapping mapping) {
        this.setServletMappings((ServletMapping[])LazyList.addToArray(this.getServletMappings(), mapping, ServletMapping.class));
    }
    
    public FilterHolder newFilterHolder(final Class filter) {
        return new FilterHolder(filter);
    }
    
    public FilterHolder newFilterHolder() {
        return new FilterHolder();
    }
    
    public FilterHolder getFilter(final String name) {
        return this._filterNameMap.get(name);
    }
    
    public FilterHolder addFilterWithMapping(final Class filter, final String pathSpec, final int dispatches) {
        final FilterHolder holder = this.newFilterHolder(filter);
        this.addFilterWithMapping(holder, pathSpec, dispatches);
        return holder;
    }
    
    public FilterHolder addFilterWithMapping(final String className, final String pathSpec, final int dispatches) {
        final FilterHolder holder = this.newFilterHolder(null);
        holder.setName(className + "-" + holder.hashCode());
        holder.setClassName(className);
        this.addFilterWithMapping(holder, pathSpec, dispatches);
        return holder;
    }
    
    public void addFilterWithMapping(final FilterHolder holder, final String pathSpec, final int dispatches) {
        FilterHolder[] holders = this.getFilters();
        if (holders != null) {
            holders = holders.clone();
        }
        try {
            this.setFilters((FilterHolder[])LazyList.addToArray(holders, holder, FilterHolder.class));
            final FilterMapping mapping = new FilterMapping();
            mapping.setFilterName(holder.getName());
            mapping.setPathSpec(pathSpec);
            mapping.setDispatches(dispatches);
            this.setFilterMappings((FilterMapping[])LazyList.addToArray(this.getFilterMappings(), mapping, FilterMapping.class));
        }
        catch (RuntimeException e) {
            this.setFilters(holders);
            throw e;
        }
        catch (Error e2) {
            this.setFilters(holders);
            throw e2;
        }
    }
    
    public FilterHolder addFilter(final String className, final String pathSpec, final int dispatches) {
        return this.addFilterWithMapping(className, pathSpec, dispatches);
    }
    
    public void addFilter(final FilterHolder filter, final FilterMapping filterMapping) {
        if (filter != null) {
            this.setFilters((FilterHolder[])LazyList.addToArray(this.getFilters(), filter, FilterHolder.class));
        }
        if (filterMapping != null) {
            this.setFilterMappings((FilterMapping[])LazyList.addToArray(this.getFilterMappings(), filterMapping, FilterMapping.class));
        }
    }
    
    public void addFilter(final FilterHolder filter) {
        if (filter != null) {
            this.setFilters((FilterHolder[])LazyList.addToArray(this.getFilters(), filter, FilterHolder.class));
        }
    }
    
    public void addFilterMapping(final FilterMapping mapping) {
        if (mapping != null) {
            this.setFilterMappings((FilterMapping[])LazyList.addToArray(this.getFilterMappings(), mapping, FilterMapping.class));
        }
    }
    
    protected synchronized void updateNameMappings() {
        this._filterNameMap.clear();
        if (this._filters != null) {
            for (int i = 0; i < this._filters.length; ++i) {
                this._filterNameMap.put(this._filters[i].getName(), this._filters[i]);
                this._filters[i].setServletHandler(this);
            }
        }
        this._servletNameMap.clear();
        if (this._servlets != null) {
            for (int i = 0; i < this._servlets.length; ++i) {
                this._servletNameMap.put(this._servlets[i].getName(), this._servlets[i]);
                this._servlets[i].setServletHandler(this);
            }
        }
    }
    
    protected synchronized void updateMappings() {
        if (this._filterMappings == null) {
            this._filterPathMappings = null;
            this._filterNameMappings = null;
        }
        else {
            this._filterPathMappings = new ArrayList();
            this._filterNameMappings = new MultiMap();
            for (int i = 0; i < this._filterMappings.length; ++i) {
                final FilterHolder filter_holder = this._filterNameMap.get(this._filterMappings[i].getFilterName());
                if (filter_holder == null) {
                    throw new IllegalStateException("No filter named " + this._filterMappings[i].getFilterName());
                }
                this._filterMappings[i].setFilterHolder(filter_holder);
                if (this._filterMappings[i].getPathSpecs() != null) {
                    this._filterPathMappings.add(this._filterMappings[i]);
                }
                if (this._filterMappings[i].getServletNames() != null) {
                    final String[] names = this._filterMappings[i].getServletNames();
                    for (int j = 0; j < names.length; ++j) {
                        if (names[j] != null) {
                            this._filterNameMappings.add(names[j], this._filterMappings[i]);
                        }
                    }
                }
            }
        }
        if (this._servletMappings == null || this._servletNameMap == null) {
            this._servletPathMap = null;
        }
        else {
            final PathMap pm = new PathMap();
            for (int k = 0; k < this._servletMappings.length; ++k) {
                final ServletHolder servlet_holder = this._servletNameMap.get(this._servletMappings[k].getServletName());
                if (servlet_holder == null) {
                    throw new IllegalStateException("No such servlet: " + this._servletMappings[k].getServletName());
                }
                if (this._servletMappings[k].getPathSpecs() != null) {
                    final String[] pathSpecs = this._servletMappings[k].getPathSpecs();
                    for (int l = 0; l < pathSpecs.length; ++l) {
                        if (pathSpecs[l] != null) {
                            pm.put(pathSpecs[l], servlet_holder);
                        }
                    }
                }
            }
            this._servletPathMap = pm;
        }
        if (Log.isDebugEnabled()) {
            Log.debug("filterNameMap=" + this._filterNameMap);
            Log.debug("pathFilters=" + this._filterPathMappings);
            Log.debug("servletFilterMap=" + this._filterNameMappings);
            Log.debug("servletPathMap=" + this._servletPathMap);
            Log.debug("servletNameMap=" + this._servletNameMap);
        }
        try {
            if (this.isStarted()) {
                this.initialize();
            }
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    protected void notFound(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        if (Log.isDebugEnabled()) {
            Log.debug("Not Found " + request.getRequestURI());
        }
        response.sendError(404);
    }
    
    public void setFilterChainsCached(final boolean filterChainsCached) {
        this._filterChainsCached = filterChainsCached;
    }
    
    public void setFilterMappings(final FilterMapping[] filterMappings) {
        if (this.getServer() != null) {
            this.getServer().getContainer().update(this, this._filterMappings, filterMappings, "filterMapping", true);
        }
        this._filterMappings = filterMappings;
        this.updateMappings();
        this.invalidateChainsCache();
    }
    
    public synchronized void setFilters(final FilterHolder[] holders) {
        if (this.getServer() != null) {
            this.getServer().getContainer().update(this, this._filters, holders, "filter", true);
        }
        this._filters = holders;
        this.updateNameMappings();
        this.invalidateChainsCache();
    }
    
    public void setServletMappings(final ServletMapping[] servletMappings) {
        if (this.getServer() != null) {
            this.getServer().getContainer().update(this, this._servletMappings, servletMappings, "servletMapping", true);
        }
        this._servletMappings = servletMappings;
        this.updateMappings();
        this.invalidateChainsCache();
    }
    
    public synchronized void setServlets(final ServletHolder[] holders) {
        if (this.getServer() != null) {
            this.getServer().getContainer().update(this, this._servlets, holders, "servlet", true);
        }
        this._servlets = holders;
        this.updateNameMappings();
        this.invalidateChainsCache();
    }
    
    public int getMaxFilterChainsCacheSize() {
        return this._maxFilterChainsCacheSize;
    }
    
    public void setMaxFilterChainsCacheSize(final int maxFilterChainsCacheSize) {
        this._maxFilterChainsCacheSize = maxFilterChainsCacheSize;
        for (int i = 0; i < this._chainCache.length; ++i) {
            if (this._chainCache[i] != null && this._chainCache[i] instanceof MruCache) {
                this._chainCache[i].setMaxEntries(maxFilterChainsCacheSize);
            }
        }
    }
    
    public Servlet customizeServlet(final Servlet servlet) throws Exception {
        return servlet;
    }
    
    public Servlet customizeServletDestroy(final Servlet servlet) throws Exception {
        return servlet;
    }
    
    public Filter customizeFilter(final Filter filter) throws Exception {
        return filter;
    }
    
    public Filter customizeFilterDestroy(final Filter filter) throws Exception {
        return filter;
    }
    
    private class MruCache extends LinkedHashMap
    {
        private int maxEntries;
        
        public MruCache() {
            this.maxEntries = 1000;
        }
        
        public MruCache(final int maxSize) {
            this.maxEntries = 1000;
            this.setMaxEntries(maxSize);
        }
        
        protected boolean removeEldestEntry(final Map.Entry eldest) {
            return this.size() > this.maxEntries;
        }
        
        public void setMaxEntries(final int maxEntries) {
            this.maxEntries = maxEntries;
        }
    }
    
    private class CachedChain implements FilterChain
    {
        FilterHolder _filterHolder;
        CachedChain _next;
        ServletHolder _servletHolder;
        
        CachedChain(Object filters, final ServletHolder servletHolder) {
            if (LazyList.size(filters) > 0) {
                this._filterHolder = (FilterHolder)LazyList.get(filters, 0);
                filters = LazyList.remove(filters, 0);
                this._next = new CachedChain(filters, servletHolder);
            }
            else {
                this._servletHolder = servletHolder;
            }
        }
        
        public void doFilter(final ServletRequest request, final ServletResponse response) throws IOException, ServletException {
            if (this._filterHolder != null) {
                if (Log.isDebugEnabled()) {
                    Log.debug("call filter " + this._filterHolder);
                }
                final Filter filter = this._filterHolder.getFilter();
                filter.doFilter(request, response, this._next);
                return;
            }
            if (this._servletHolder != null) {
                if (Log.isDebugEnabled()) {
                    Log.debug("call servlet " + this._servletHolder);
                }
                this._servletHolder.handle(request, response);
            }
            else {
                ServletHandler.this.notFound((HttpServletRequest)request, (HttpServletResponse)response);
            }
        }
        
        public String toString() {
            if (this._filterHolder != null) {
                return this._filterHolder + "->" + this._next.toString();
            }
            if (this._servletHolder != null) {
                return this._servletHolder.toString();
            }
            return "null";
        }
    }
    
    private class Chain implements FilterChain
    {
        int _filter;
        Object _chain;
        ServletHolder _servletHolder;
        
        Chain(final Object filters, final ServletHolder servletHolder) {
            this._filter = 0;
            this._chain = filters;
            this._servletHolder = servletHolder;
        }
        
        public void doFilter(final ServletRequest request, final ServletResponse response) throws IOException, ServletException {
            if (Log.isDebugEnabled()) {
                Log.debug("doFilter " + this._filter);
            }
            if (this._filter < LazyList.size(this._chain)) {
                final FilterHolder holder = (FilterHolder)LazyList.get(this._chain, this._filter++);
                if (Log.isDebugEnabled()) {
                    Log.debug("call filter " + holder);
                }
                final Filter filter = holder.getFilter();
                filter.doFilter(request, response, this);
                return;
            }
            if (this._servletHolder != null) {
                if (Log.isDebugEnabled()) {
                    Log.debug("call servlet " + this._servletHolder);
                }
                this._servletHolder.handle(request, response);
            }
            else {
                ServletHandler.this.notFound((HttpServletRequest)request, (HttpServletResponse)response);
            }
        }
        
        public String toString() {
            final StringBuffer b = new StringBuffer();
            for (int i = 0; i < LazyList.size(this._chain); ++i) {
                b.append(LazyList.get(this._chain, i).toString());
                b.append("->");
            }
            b.append(this._servletHolder);
            return b.toString();
        }
    }
}
