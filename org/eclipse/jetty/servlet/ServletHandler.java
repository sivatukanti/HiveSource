// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.servlet;

import javax.servlet.http.HttpServlet;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.URIUtil;
import java.util.EnumSet;
import javax.servlet.Filter;
import java.util.Collections;
import java.util.Set;
import javax.servlet.ServletSecurityElement;
import javax.servlet.ServletRegistration;
import java.util.Arrays;
import org.eclipse.jetty.util.MultiException;
import java.util.Iterator;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import javax.servlet.UnavailableException;
import org.eclipse.jetty.http.HttpHeaderValue;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.server.QuietServletException;
import org.eclipse.jetty.io.RuntimeIOException;
import org.eclipse.jetty.io.EofException;
import org.eclipse.jetty.server.ServletResponseHttpWrapper;
import org.eclipse.jetty.server.ServletRequestHttpWrapper;
import javax.servlet.ServletException;
import java.io.IOException;
import org.eclipse.jetty.server.UserIdentity;
import javax.servlet.DispatcherType;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.util.annotation.ManagedAttribute;
import java.util.ListIterator;
import org.eclipse.jetty.util.LazyList;
import org.eclipse.jetty.util.ArrayUtil;
import java.util.ArrayList;
import org.eclipse.jetty.util.component.LifeCycle;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.Servlet;
import org.eclipse.jetty.security.SecurityHandler;
import org.eclipse.jetty.server.handler.ContextHandler;
import java.util.HashMap;
import java.util.Queue;
import javax.servlet.FilterChain;
import java.util.concurrent.ConcurrentMap;
import org.eclipse.jetty.http.PathMap;
import org.eclipse.jetty.util.MultiMap;
import java.util.List;
import java.util.Map;
import org.eclipse.jetty.security.IdentityService;
import javax.servlet.ServletContext;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.util.annotation.ManagedObject;
import org.eclipse.jetty.server.handler.ScopedHandler;

@ManagedObject("Servlet Handler")
public class ServletHandler extends ScopedHandler
{
    private static final Logger LOG;
    public static final String __DEFAULT_SERVLET = "default";
    private ServletContextHandler _contextHandler;
    private ServletContext _servletContext;
    private FilterHolder[] _filters;
    private FilterMapping[] _filterMappings;
    private int _matchBeforeIndex;
    private int _matchAfterIndex;
    private boolean _filterChainsCached;
    private int _maxFilterChainsCacheSize;
    private boolean _startWithUnavailable;
    private boolean _ensureDefaultServlet;
    private IdentityService _identityService;
    private boolean _allowDuplicateMappings;
    private ServletHolder[] _servlets;
    private ServletMapping[] _servletMappings;
    private final Map<String, FilterHolder> _filterNameMap;
    private List<FilterMapping> _filterPathMappings;
    private MultiMap<FilterMapping> _filterNameMappings;
    private final Map<String, ServletHolder> _servletNameMap;
    private PathMap<ServletHolder> _servletPathMap;
    private ListenerHolder[] _listeners;
    protected final ConcurrentMap<String, FilterChain>[] _chainCache;
    protected final Queue<String>[] _chainLRU;
    
    public ServletHandler() {
        this._filters = new FilterHolder[0];
        this._matchBeforeIndex = -1;
        this._matchAfterIndex = -1;
        this._filterChainsCached = true;
        this._maxFilterChainsCacheSize = 512;
        this._startWithUnavailable = false;
        this._ensureDefaultServlet = true;
        this._allowDuplicateMappings = false;
        this._servlets = new ServletHolder[0];
        this._filterNameMap = new HashMap<String, FilterHolder>();
        this._servletNameMap = new HashMap<String, ServletHolder>();
        this._listeners = new ListenerHolder[0];
        this._chainCache = (ConcurrentMap<String, FilterChain>[])new ConcurrentMap[31];
        this._chainLRU = (Queue<String>[])new Queue[31];
    }
    
    @Override
    protected synchronized void doStart() throws Exception {
        final ContextHandler.Context context = ContextHandler.getCurrentContext();
        this._servletContext = ((context == null) ? new ContextHandler.StaticContext() : context);
        this._contextHandler = (ServletContextHandler)((context == null) ? null : context.getContextHandler());
        if (this._contextHandler != null) {
            final SecurityHandler security_handler = this._contextHandler.getChildHandlerByClass(SecurityHandler.class);
            if (security_handler != null) {
                this._identityService = security_handler.getIdentityService();
            }
        }
        this.updateNameMappings();
        this.updateMappings();
        if (this.getServletMapping("/") == null && this._ensureDefaultServlet) {
            if (ServletHandler.LOG.isDebugEnabled()) {
                ServletHandler.LOG.debug("Adding Default404Servlet to {}", this);
            }
            this.addServletWithMapping(Default404Servlet.class, "/");
            this.updateMappings();
            this.getServletMapping("/").setDefault(true);
        }
        if (this._filterChainsCached) {
            this._chainCache[1] = new ConcurrentHashMap<String, FilterChain>();
            this._chainCache[2] = new ConcurrentHashMap<String, FilterChain>();
            this._chainCache[4] = new ConcurrentHashMap<String, FilterChain>();
            this._chainCache[8] = new ConcurrentHashMap<String, FilterChain>();
            this._chainCache[16] = new ConcurrentHashMap<String, FilterChain>();
            this._chainLRU[1] = new ConcurrentLinkedQueue<String>();
            this._chainLRU[2] = new ConcurrentLinkedQueue<String>();
            this._chainLRU[4] = new ConcurrentLinkedQueue<String>();
            this._chainLRU[8] = new ConcurrentLinkedQueue<String>();
            this._chainLRU[16] = new ConcurrentLinkedQueue<String>();
        }
        if (this._contextHandler == null) {
            this.initialize();
        }
        super.doStart();
    }
    
    public boolean isEnsureDefaultServlet() {
        return this._ensureDefaultServlet;
    }
    
    public void setEnsureDefaultServlet(final boolean ensureDefaultServlet) {
        this._ensureDefaultServlet = ensureDefaultServlet;
    }
    
    @Override
    protected void start(final LifeCycle l) throws Exception {
        if (!(l instanceof Holder)) {
            super.start(l);
        }
    }
    
    @Override
    protected synchronized void doStop() throws Exception {
        super.doStop();
        final List<FilterHolder> filterHolders = new ArrayList<FilterHolder>();
        final List<FilterMapping> filterMappings = ArrayUtil.asMutableList(this._filterMappings);
        if (this._filters != null) {
            int i = this._filters.length;
            while (i-- > 0) {
                try {
                    this._filters[i].stop();
                }
                catch (Exception e) {
                    ServletHandler.LOG.warn("EXCEPTION ", e);
                }
                if (this._filters[i].getSource() != BaseHolder.Source.EMBEDDED) {
                    this._filterNameMap.remove(this._filters[i].getName());
                    final ListIterator<FilterMapping> fmitor = filterMappings.listIterator();
                    while (fmitor.hasNext()) {
                        final FilterMapping fm = fmitor.next();
                        if (fm.getFilterName().equals(this._filters[i].getName())) {
                            fmitor.remove();
                        }
                    }
                }
                else {
                    filterHolders.add(this._filters[i]);
                }
            }
        }
        final FilterHolder[] fhs = (FilterHolder[])LazyList.toArray(filterHolders, FilterHolder.class);
        this.updateBeans(this._filters, fhs);
        this._filters = fhs;
        final FilterMapping[] fms = (FilterMapping[])LazyList.toArray(filterMappings, FilterMapping.class);
        this.updateBeans(this._filterMappings, fms);
        this._filterMappings = fms;
        this._matchAfterIndex = ((this._filterMappings == null || this._filterMappings.length == 0) ? -1 : (this._filterMappings.length - 1));
        this._matchBeforeIndex = -1;
        final List<ServletHolder> servletHolders = new ArrayList<ServletHolder>();
        final List<ServletMapping> servletMappings = ArrayUtil.asMutableList(this._servletMappings);
        if (this._servlets != null) {
            int j = this._servlets.length;
            while (j-- > 0) {
                try {
                    this._servlets[j].stop();
                }
                catch (Exception e2) {
                    ServletHandler.LOG.warn("EXCEPTION ", e2);
                }
                if (this._servlets[j].getSource() != BaseHolder.Source.EMBEDDED) {
                    this._servletNameMap.remove(this._servlets[j].getName());
                    final ListIterator<ServletMapping> smitor = servletMappings.listIterator();
                    while (smitor.hasNext()) {
                        final ServletMapping sm = smitor.next();
                        if (sm.getServletName().equals(this._servlets[j].getName())) {
                            smitor.remove();
                        }
                    }
                }
                else {
                    servletHolders.add(this._servlets[j]);
                }
            }
        }
        final ServletHolder[] shs = (ServletHolder[])LazyList.toArray(servletHolders, ServletHolder.class);
        this.updateBeans(this._servlets, shs);
        this._servlets = shs;
        final ServletMapping[] sms = (ServletMapping[])LazyList.toArray(servletMappings, ServletMapping.class);
        this.updateBeans(this._servletMappings, sms);
        this._servletMappings = sms;
        final List<ListenerHolder> listenerHolders = new ArrayList<ListenerHolder>();
        if (this._listeners != null) {
            int k = this._listeners.length;
            while (k-- > 0) {
                try {
                    this._listeners[k].stop();
                }
                catch (Exception e3) {
                    ServletHandler.LOG.warn("EXCEPTION ", e3);
                }
                if (this._listeners[k].getSource() == BaseHolder.Source.EMBEDDED) {
                    listenerHolders.add(this._listeners[k]);
                }
            }
        }
        final ListenerHolder[] listeners = (ListenerHolder[])LazyList.toArray(listenerHolders, ListenerHolder.class);
        this.updateBeans(this._listeners, listeners);
        this._listeners = listeners;
        this._filterPathMappings = null;
        this._filterNameMappings = null;
        this._servletPathMap = null;
    }
    
    protected IdentityService getIdentityService() {
        return this._identityService;
    }
    
    public Object getContextLog() {
        return null;
    }
    
    @ManagedAttribute(value = "filters", readonly = true)
    public FilterMapping[] getFilterMappings() {
        return this._filterMappings;
    }
    
    @ManagedAttribute(value = "filters", readonly = true)
    public FilterHolder[] getFilters() {
        return this._filters;
    }
    
    public PathMap.MappedEntry<ServletHolder> getHolderEntry(final String pathInContext) {
        if (this._servletPathMap == null) {
            return null;
        }
        return this._servletPathMap.getMatch(pathInContext);
    }
    
    public ServletContext getServletContext() {
        return this._servletContext;
    }
    
    @ManagedAttribute(value = "mappings of servlets", readonly = true)
    public ServletMapping[] getServletMappings() {
        return this._servletMappings;
    }
    
    public ServletMapping getServletMapping(final String pathSpec) {
        if (pathSpec == null || this._servletMappings == null) {
            return null;
        }
        ServletMapping mapping = null;
        for (int i = 0; i < this._servletMappings.length && mapping == null; ++i) {
            final ServletMapping m = this._servletMappings[i];
            if (m.getPathSpecs() != null) {
                for (final String p : m.getPathSpecs()) {
                    if (pathSpec.equals(p)) {
                        mapping = m;
                        break;
                    }
                }
            }
        }
        return mapping;
    }
    
    @ManagedAttribute(value = "servlets", readonly = true)
    public ServletHolder[] getServlets() {
        return this._servlets;
    }
    
    public ServletHolder getServlet(final String name) {
        return this._servletNameMap.get(name);
    }
    
    @Override
    public void doScope(final String target, final Request baseRequest, final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
        final String old_servlet_path = baseRequest.getServletPath();
        final String old_path_info = baseRequest.getPathInfo();
        final DispatcherType type = baseRequest.getDispatcherType();
        ServletHolder servlet_holder = null;
        UserIdentity.Scope old_scope = null;
        if (target.startsWith("/")) {
            final PathMap.MappedEntry<ServletHolder> entry = this.getHolderEntry(target);
            if (entry != null) {
                servlet_holder = entry.getValue();
                final String servlet_path_spec = entry.getKey();
                final String servlet_path = (entry.getMapped() != null) ? entry.getMapped() : PathMap.pathMatch(servlet_path_spec, target);
                final String path_info = PathMap.pathInfo(servlet_path_spec, target);
                if (DispatcherType.INCLUDE.equals(type)) {
                    baseRequest.setAttribute("javax.servlet.include.servlet_path", servlet_path);
                    baseRequest.setAttribute("javax.servlet.include.path_info", path_info);
                }
                else {
                    baseRequest.setServletPath(servlet_path);
                    baseRequest.setPathInfo(path_info);
                }
            }
        }
        else {
            servlet_holder = this._servletNameMap.get(target);
        }
        if (ServletHandler.LOG.isDebugEnabled()) {
            ServletHandler.LOG.debug("servlet {}|{}|{} -> {}", baseRequest.getContextPath(), baseRequest.getServletPath(), baseRequest.getPathInfo(), servlet_holder);
        }
        try {
            old_scope = baseRequest.getUserIdentityScope();
            baseRequest.setUserIdentityScope(servlet_holder);
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
            if (old_scope != null) {
                baseRequest.setUserIdentityScope(old_scope);
            }
            if (!DispatcherType.INCLUDE.equals(type)) {
                baseRequest.setServletPath(old_servlet_path);
                baseRequest.setPathInfo(old_path_info);
            }
        }
    }
    
    @Override
    public void doHandle(final String target, final Request baseRequest, final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
        final DispatcherType type = baseRequest.getDispatcherType();
        final ServletHolder servlet_holder = (ServletHolder)baseRequest.getUserIdentityScope();
        FilterChain chain = null;
        if (target.startsWith("/")) {
            if (servlet_holder != null && this._filterMappings != null && this._filterMappings.length > 0) {
                chain = this.getFilterChain(baseRequest, target, servlet_holder);
            }
        }
        else if (servlet_holder != null && this._filterMappings != null && this._filterMappings.length > 0) {
            chain = this.getFilterChain(baseRequest, null, servlet_holder);
        }
        if (ServletHandler.LOG.isDebugEnabled()) {
            ServletHandler.LOG.debug("chain={}", chain);
        }
        Throwable th = null;
        try {
            if (servlet_holder == null) {
                this.notFound(baseRequest, request, response);
            }
            else {
                ServletRequest req = request;
                if (req instanceof ServletRequestHttpWrapper) {
                    req = ((ServletRequestHttpWrapper)req).getRequest();
                }
                ServletResponse res = response;
                if (res instanceof ServletResponseHttpWrapper) {
                    res = ((ServletResponseHttpWrapper)res).getResponse();
                }
                servlet_holder.prepare(baseRequest, req, res);
                if (chain != null) {
                    chain.doFilter(req, res);
                }
                else {
                    servlet_holder.handle(baseRequest, req, res);
                }
            }
        }
        catch (EofException e) {
            throw e;
        }
        catch (RuntimeIOException e2) {
            if (e2.getCause() instanceof IOException) {
                ServletHandler.LOG.debug(e2);
                throw (IOException)e2.getCause();
            }
            throw e2;
        }
        catch (Exception e3) {
            if (baseRequest.isAsyncStarted() || (!DispatcherType.REQUEST.equals(type) && !DispatcherType.ASYNC.equals(type))) {
                if (e3 instanceof IOException) {
                    throw (IOException)e3;
                }
                if (e3 instanceof RuntimeException) {
                    throw (RuntimeException)e3;
                }
                if (e3 instanceof ServletException) {
                    throw (ServletException)e3;
                }
            }
            th = e3;
            if (th instanceof ServletException) {
                if (th instanceof QuietServletException) {
                    ServletHandler.LOG.warn(th.toString(), new Object[0]);
                    ServletHandler.LOG.debug(th);
                }
                else {
                    ServletHandler.LOG.warn(th);
                }
            }
            else {
                if (th instanceof EofException) {
                    throw (EofException)th;
                }
                ServletHandler.LOG.warn(request.getRequestURI(), th);
                if (ServletHandler.LOG.isDebugEnabled()) {
                    ServletHandler.LOG.debug(request.toString(), new Object[0]);
                }
            }
            request.setAttribute("javax.servlet.error.exception_type", th.getClass());
            request.setAttribute("javax.servlet.error.exception", th);
            if (!response.isCommitted()) {
                baseRequest.getResponse().getHttpFields().put(HttpHeader.CONNECTION, HttpHeaderValue.CLOSE);
                if (th instanceof UnavailableException) {
                    final UnavailableException ue = (UnavailableException)th;
                    if (ue.isPermanent()) {
                        response.sendError(404);
                    }
                    else {
                        response.sendError(503);
                    }
                }
                else {
                    response.sendError(500);
                }
                return;
            }
            if (th instanceof IOException) {
                throw (IOException)th;
            }
            if (th instanceof RuntimeException) {
                throw (RuntimeException)th;
            }
            if (th instanceof ServletException) {
                throw (ServletException)th;
            }
            throw new IllegalStateException("response already committed", th);
        }
        catch (Error e4) {
            if ("ContinuationThrowable".equals(e4.getClass().getSimpleName())) {
                throw e4;
            }
            th = e4;
            if (!DispatcherType.REQUEST.equals(type) && !DispatcherType.ASYNC.equals(type)) {
                throw e4;
            }
            ServletHandler.LOG.warn("Error for " + request.getRequestURI(), e4);
            if (ServletHandler.LOG.isDebugEnabled()) {
                ServletHandler.LOG.debug(request.toString(), new Object[0]);
            }
            request.setAttribute("javax.servlet.error.exception_type", e4.getClass());
            request.setAttribute("javax.servlet.error.exception", e4);
            if (!response.isCommitted()) {
                baseRequest.getResponse().getHttpFields().put(HttpHeader.CONNECTION, HttpHeaderValue.CLOSE);
                response.sendError(500);
            }
            else {
                ServletHandler.LOG.debug("Response already committed for handling ", e4);
            }
        }
        finally {
            if (th != null && request.isAsyncStarted()) {
                baseRequest.getHttpChannelState().errorComplete();
            }
            if (servlet_holder != null) {
                baseRequest.setHandled(true);
            }
        }
    }
    
    protected FilterChain getFilterChain(final Request baseRequest, final String pathInContext, final ServletHolder servletHolder) {
        final String key = (pathInContext == null) ? servletHolder.getName() : pathInContext;
        final int dispatch = FilterMapping.dispatch(baseRequest.getDispatcherType());
        if (this._filterChainsCached && this._chainCache != null) {
            final FilterChain chain = this._chainCache[dispatch].get(key);
            if (chain != null) {
                return chain;
            }
        }
        final List<FilterHolder> filters = new ArrayList<FilterHolder>();
        if (pathInContext != null && this._filterPathMappings != null) {
            for (final FilterMapping filterPathMapping : this._filterPathMappings) {
                if (filterPathMapping.appliesTo(pathInContext, dispatch)) {
                    filters.add(filterPathMapping.getFilterHolder());
                }
            }
        }
        if (servletHolder != null && this._filterNameMappings != null && this._filterNameMappings.size() > 0 && this._filterNameMappings.size() > 0) {
            Object o = this._filterNameMappings.get(servletHolder.getName());
            for (int i = 0; i < LazyList.size(o); ++i) {
                final FilterMapping mapping = LazyList.get(o, i);
                if (mapping.appliesTo(dispatch)) {
                    filters.add(mapping.getFilterHolder());
                }
            }
            o = this._filterNameMappings.get("*");
            for (int i = 0; i < LazyList.size(o); ++i) {
                final FilterMapping mapping = LazyList.get(o, i);
                if (mapping.appliesTo(dispatch)) {
                    filters.add(mapping.getFilterHolder());
                }
            }
        }
        if (filters.isEmpty()) {
            return null;
        }
        FilterChain chain2 = null;
        if (this._filterChainsCached) {
            if (filters.size() > 0) {
                chain2 = new CachedChain(filters, servletHolder);
            }
            final Map<String, FilterChain> cache = this._chainCache[dispatch];
            final Queue<String> lru = this._chainLRU[dispatch];
            while (this._maxFilterChainsCacheSize > 0 && cache.size() >= this._maxFilterChainsCacheSize) {
                final String k = lru.poll();
                if (k == null) {
                    cache.clear();
                    break;
                }
                cache.remove(k);
            }
            cache.put(key, chain2);
            lru.add(key);
        }
        else if (filters.size() > 0) {
            chain2 = new Chain(baseRequest, filters, servletHolder);
        }
        return chain2;
    }
    
    protected void invalidateChainsCache() {
        if (this._chainLRU[1] != null) {
            this._chainLRU[1].clear();
            this._chainLRU[2].clear();
            this._chainLRU[4].clear();
            this._chainLRU[8].clear();
            this._chainLRU[16].clear();
            this._chainCache[1].clear();
            this._chainCache[2].clear();
            this._chainCache[4].clear();
            this._chainCache[8].clear();
            this._chainCache[16].clear();
        }
    }
    
    public boolean isAvailable() {
        if (!this.isStarted()) {
            return false;
        }
        final ServletHolder[] servlets;
        final ServletHolder[] holders = servlets = this.getServlets();
        for (final ServletHolder holder : servlets) {
            if (holder != null && !holder.isAvailable()) {
                return false;
            }
        }
        return true;
    }
    
    public void setStartWithUnavailable(final boolean start) {
        this._startWithUnavailable = start;
    }
    
    public boolean isAllowDuplicateMappings() {
        return this._allowDuplicateMappings;
    }
    
    public void setAllowDuplicateMappings(final boolean allowDuplicateMappings) {
        this._allowDuplicateMappings = allowDuplicateMappings;
    }
    
    public boolean isStartWithUnavailable() {
        return this._startWithUnavailable;
    }
    
    public void initialize() throws Exception {
        final MultiException mx = new MultiException();
        if (this._filters != null) {
            for (final FilterHolder f : this._filters) {
                try {
                    f.start();
                    f.initialize();
                }
                catch (Exception e) {
                    mx.add(e);
                }
            }
        }
        if (this._servlets != null) {
            final ServletHolder[] servlets = this._servlets.clone();
            Arrays.sort(servlets);
            for (final ServletHolder servlet : servlets) {
                try {
                    servlet.start();
                    servlet.initialize();
                }
                catch (Throwable e2) {
                    ServletHandler.LOG.debug("EXCEPTION ", e2);
                    mx.add(e2);
                }
            }
        }
        for (final Holder<?> h : this.getBeans((Class<Holder<?>>)Holder.class)) {
            try {
                if (h.isStarted()) {
                    continue;
                }
                h.start();
                h.initialize();
            }
            catch (Exception e3) {
                mx.add(e3);
            }
        }
        mx.ifExceptionThrow();
    }
    
    public boolean isFilterChainsCached() {
        return this._filterChainsCached;
    }
    
    public void addListener(final ListenerHolder listener) {
        if (listener != null) {
            this.setListeners(ArrayUtil.addToArray(this.getListeners(), listener, ListenerHolder.class));
        }
    }
    
    public ListenerHolder[] getListeners() {
        return this._listeners;
    }
    
    public void setListeners(final ListenerHolder[] listeners) {
        if (listeners != null) {
            for (final ListenerHolder holder : listeners) {
                holder.setServletHandler(this);
            }
        }
        this.updateBeans(this._listeners, listeners);
        this._listeners = listeners;
    }
    
    public ListenerHolder newListenerHolder(final BaseHolder.Source source) {
        return new ListenerHolder(source);
    }
    
    public ServletHolder newServletHolder(final BaseHolder.Source source) {
        return new ServletHolder(source);
    }
    
    public ServletHolder addServletWithMapping(final String className, final String pathSpec) {
        final ServletHolder holder = this.newServletHolder(BaseHolder.Source.EMBEDDED);
        holder.setClassName(className);
        this.addServletWithMapping(holder, pathSpec);
        return holder;
    }
    
    public ServletHolder addServletWithMapping(final Class<? extends Servlet> servlet, final String pathSpec) {
        final ServletHolder holder = this.newServletHolder(BaseHolder.Source.EMBEDDED);
        holder.setHeldClass(servlet);
        this.addServletWithMapping(holder, pathSpec);
        return holder;
    }
    
    public void addServletWithMapping(final ServletHolder servlet, final String pathSpec) {
        ServletHolder[] holders = this.getServlets();
        if (holders != null) {
            holders = holders.clone();
        }
        try {
            synchronized (this) {
                if (servlet != null && !this.containsServletHolder(servlet)) {
                    this.setServlets(ArrayUtil.addToArray(holders, servlet, ServletHolder.class));
                }
            }
            final ServletMapping mapping = new ServletMapping();
            mapping.setServletName(servlet.getName());
            mapping.setPathSpec(pathSpec);
            this.setServletMappings(ArrayUtil.addToArray(this.getServletMappings(), mapping, ServletMapping.class));
        }
        catch (Exception e) {
            this.setServlets(holders);
            if (e instanceof RuntimeException) {
                throw (RuntimeException)e;
            }
            throw new RuntimeException(e);
        }
    }
    
    public void addServlet(final ServletHolder holder) {
        if (holder == null) {
            return;
        }
        synchronized (this) {
            if (!this.containsServletHolder(holder)) {
                this.setServlets(ArrayUtil.addToArray(this.getServlets(), holder, ServletHolder.class));
            }
        }
    }
    
    public void addServletMapping(final ServletMapping mapping) {
        this.setServletMappings(ArrayUtil.addToArray(this.getServletMappings(), mapping, ServletMapping.class));
    }
    
    public Set<String> setServletSecurity(final ServletRegistration.Dynamic registration, final ServletSecurityElement servletSecurityElement) {
        if (this._contextHandler != null) {
            return this._contextHandler.setServletSecurity(registration, servletSecurityElement);
        }
        return Collections.emptySet();
    }
    
    public FilterHolder newFilterHolder(final BaseHolder.Source source) {
        return new FilterHolder(source);
    }
    
    public FilterHolder getFilter(final String name) {
        return this._filterNameMap.get(name);
    }
    
    public FilterHolder addFilterWithMapping(final Class<? extends Filter> filter, final String pathSpec, final EnumSet<DispatcherType> dispatches) {
        final FilterHolder holder = this.newFilterHolder(BaseHolder.Source.EMBEDDED);
        holder.setHeldClass(filter);
        this.addFilterWithMapping(holder, pathSpec, dispatches);
        return holder;
    }
    
    public FilterHolder addFilterWithMapping(final String className, final String pathSpec, final EnumSet<DispatcherType> dispatches) {
        final FilterHolder holder = this.newFilterHolder(BaseHolder.Source.EMBEDDED);
        holder.setClassName(className);
        this.addFilterWithMapping(holder, pathSpec, dispatches);
        return holder;
    }
    
    public void addFilterWithMapping(final FilterHolder holder, final String pathSpec, final EnumSet<DispatcherType> dispatches) {
        FilterHolder[] holders = this.getFilters();
        if (holders != null) {
            holders = holders.clone();
        }
        try {
            synchronized (this) {
                if (holder != null && !this.containsFilterHolder(holder)) {
                    this.setFilters(ArrayUtil.addToArray(holders, holder, FilterHolder.class));
                }
            }
            final FilterMapping mapping = new FilterMapping();
            mapping.setFilterName(holder.getName());
            mapping.setPathSpec(pathSpec);
            mapping.setDispatcherTypes(dispatches);
            this.addFilterMapping(mapping);
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
    
    public FilterHolder addFilterWithMapping(final Class<? extends Filter> filter, final String pathSpec, final int dispatches) {
        final FilterHolder holder = this.newFilterHolder(BaseHolder.Source.EMBEDDED);
        holder.setHeldClass(filter);
        this.addFilterWithMapping(holder, pathSpec, dispatches);
        return holder;
    }
    
    public FilterHolder addFilterWithMapping(final String className, final String pathSpec, final int dispatches) {
        final FilterHolder holder = this.newFilterHolder(BaseHolder.Source.EMBEDDED);
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
            synchronized (this) {
                if (holder != null && !this.containsFilterHolder(holder)) {
                    this.setFilters(ArrayUtil.addToArray(holders, holder, FilterHolder.class));
                }
            }
            final FilterMapping mapping = new FilterMapping();
            mapping.setFilterName(holder.getName());
            mapping.setPathSpec(pathSpec);
            mapping.setDispatches(dispatches);
            this.addFilterMapping(mapping);
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
    
    @Deprecated
    public FilterHolder addFilter(final String className, final String pathSpec, final EnumSet<DispatcherType> dispatches) {
        return this.addFilterWithMapping(className, pathSpec, dispatches);
    }
    
    public void addFilter(final FilterHolder filter, final FilterMapping filterMapping) {
        if (filter != null) {
            synchronized (this) {
                if (!this.containsFilterHolder(filter)) {
                    this.setFilters(ArrayUtil.addToArray(this.getFilters(), filter, FilterHolder.class));
                }
            }
        }
        if (filterMapping != null) {
            this.addFilterMapping(filterMapping);
        }
    }
    
    public void addFilter(final FilterHolder filter) {
        if (filter == null) {
            return;
        }
        synchronized (this) {
            if (!this.containsFilterHolder(filter)) {
                this.setFilters(ArrayUtil.addToArray(this.getFilters(), filter, FilterHolder.class));
            }
        }
    }
    
    public void addFilterMapping(final FilterMapping mapping) {
        if (mapping != null) {
            final BaseHolder.Source source = (mapping.getFilterHolder() == null) ? null : mapping.getFilterHolder().getSource();
            final FilterMapping[] mappings = this.getFilterMappings();
            if (mappings == null || mappings.length == 0) {
                this.setFilterMappings(this.insertFilterMapping(mapping, 0, false));
                if (source != null && source == BaseHolder.Source.JAVAX_API) {
                    this._matchAfterIndex = 0;
                }
            }
            else if (source != null && BaseHolder.Source.JAVAX_API == source) {
                this.setFilterMappings(this.insertFilterMapping(mapping, mappings.length - 1, false));
                if (this._matchAfterIndex < 0) {
                    this._matchAfterIndex = this.getFilterMappings().length - 1;
                }
            }
            else if (this._matchAfterIndex < 0) {
                this.setFilterMappings(this.insertFilterMapping(mapping, mappings.length - 1, false));
            }
            else {
                final FilterMapping[] new_mappings = this.insertFilterMapping(mapping, this._matchAfterIndex, true);
                ++this._matchAfterIndex;
                this.setFilterMappings(new_mappings);
            }
        }
    }
    
    public void prependFilterMapping(final FilterMapping mapping) {
        if (mapping != null) {
            final BaseHolder.Source source = (mapping.getFilterHolder() == null) ? null : mapping.getFilterHolder().getSource();
            final FilterMapping[] mappings = this.getFilterMappings();
            if (mappings == null || mappings.length == 0) {
                this.setFilterMappings(this.insertFilterMapping(mapping, 0, false));
                if (source != null && BaseHolder.Source.JAVAX_API == source) {
                    this._matchBeforeIndex = 0;
                }
            }
            else {
                if (source != null && BaseHolder.Source.JAVAX_API == source) {
                    if (this._matchBeforeIndex < 0) {
                        this._matchBeforeIndex = 0;
                        final FilterMapping[] new_mappings = this.insertFilterMapping(mapping, 0, true);
                        this.setFilterMappings(new_mappings);
                    }
                    else {
                        final FilterMapping[] new_mappings = this.insertFilterMapping(mapping, this._matchBeforeIndex, false);
                        ++this._matchBeforeIndex;
                        this.setFilterMappings(new_mappings);
                    }
                }
                else {
                    final FilterMapping[] new_mappings = this.insertFilterMapping(mapping, 0, true);
                    this.setFilterMappings(new_mappings);
                }
                if (this._matchAfterIndex >= 0) {
                    ++this._matchAfterIndex;
                }
            }
        }
    }
    
    protected FilterMapping[] insertFilterMapping(final FilterMapping mapping, final int pos, final boolean before) {
        if (pos < 0) {
            throw new IllegalArgumentException("FilterMapping insertion pos < 0");
        }
        final FilterMapping[] mappings = this.getFilterMappings();
        if (mappings == null || mappings.length == 0) {
            return new FilterMapping[] { mapping };
        }
        final FilterMapping[] new_mappings = new FilterMapping[mappings.length + 1];
        if (before) {
            System.arraycopy(mappings, 0, new_mappings, 0, pos);
            new_mappings[pos] = mapping;
            System.arraycopy(mappings, pos, new_mappings, pos + 1, mappings.length - pos);
        }
        else {
            System.arraycopy(mappings, 0, new_mappings, 0, pos + 1);
            new_mappings[pos + 1] = mapping;
            if (mappings.length > pos + 1) {
                System.arraycopy(mappings, pos + 1, new_mappings, pos + 2, mappings.length - (pos + 1));
            }
        }
        return new_mappings;
    }
    
    protected synchronized void updateNameMappings() {
        this._filterNameMap.clear();
        if (this._filters != null) {
            for (final FilterHolder filter : this._filters) {
                this._filterNameMap.put(filter.getName(), filter);
                filter.setServletHandler(this);
            }
        }
        this._servletNameMap.clear();
        if (this._servlets != null) {
            for (final ServletHolder servlet : this._servlets) {
                this._servletNameMap.put(servlet.getName(), servlet);
                servlet.setServletHandler(this);
            }
        }
    }
    
    protected synchronized void updateMappings() {
        if (this._filterMappings == null) {
            this._filterPathMappings = null;
            this._filterNameMappings = null;
        }
        else {
            this._filterPathMappings = new ArrayList<FilterMapping>();
            this._filterNameMappings = new MultiMap<FilterMapping>();
            for (final FilterMapping filtermapping : this._filterMappings) {
                final FilterHolder filter_holder = this._filterNameMap.get(filtermapping.getFilterName());
                if (filter_holder == null) {
                    throw new IllegalStateException("No filter named " + filtermapping.getFilterName());
                }
                filtermapping.setFilterHolder(filter_holder);
                if (filtermapping.getPathSpecs() != null) {
                    this._filterPathMappings.add(filtermapping);
                }
                if (filtermapping.getServletNames() != null) {
                    final String[] servletNames;
                    final String[] names = servletNames = filtermapping.getServletNames();
                    for (final String name : servletNames) {
                        if (name != null) {
                            this._filterNameMappings.add(name, filtermapping);
                        }
                    }
                }
            }
        }
        if (this._servletMappings == null || this._servletNameMap == null) {
            this._servletPathMap = null;
        }
        else {
            final PathMap<ServletHolder> pm = new PathMap<ServletHolder>();
            final Map<String, ServletMapping> servletPathMappings = new HashMap<String, ServletMapping>();
            final HashMap<String, List<ServletMapping>> sms = new HashMap<String, List<ServletMapping>>();
            for (final ServletMapping servletMapping : this._servletMappings) {
                final String[] pathSpecs = servletMapping.getPathSpecs();
                if (pathSpecs != null) {
                    for (final String pathSpec : pathSpecs) {
                        List<ServletMapping> mappings = sms.get(pathSpec);
                        if (mappings == null) {
                            mappings = new ArrayList<ServletMapping>();
                            sms.put(pathSpec, mappings);
                        }
                        mappings.add(servletMapping);
                    }
                }
            }
            for (final String pathSpec2 : sms.keySet()) {
                final List<ServletMapping> mappings2 = sms.get(pathSpec2);
                ServletMapping finalMapping = null;
                for (final ServletMapping mapping : mappings2) {
                    final ServletHolder servlet_holder = this._servletNameMap.get(mapping.getServletName());
                    if (servlet_holder == null) {
                        throw new IllegalStateException("No such servlet: " + mapping.getServletName());
                    }
                    if (!servlet_holder.isEnabled()) {
                        continue;
                    }
                    if (finalMapping == null) {
                        finalMapping = mapping;
                    }
                    else if (finalMapping.isDefault()) {
                        finalMapping = mapping;
                    }
                    else if (this.isAllowDuplicateMappings()) {
                        ServletHandler.LOG.warn("Multiple servlets map to path {}: {} and {}, choosing {}", pathSpec2, finalMapping.getServletName(), mapping.getServletName(), mapping);
                        finalMapping = mapping;
                    }
                    else {
                        if (!mapping.isDefault()) {
                            throw new IllegalStateException("Multiple servlets map to path: " + pathSpec2 + ": " + finalMapping.getServletName() + "," + mapping.getServletName());
                        }
                        continue;
                    }
                }
                if (finalMapping == null) {
                    throw new IllegalStateException("No acceptable servlet mappings for " + pathSpec2);
                }
                if (ServletHandler.LOG.isDebugEnabled()) {
                    ServletHandler.LOG.debug("Chose path={} mapped to servlet={} from default={}", pathSpec2, finalMapping.getServletName(), finalMapping.isDefault());
                }
                servletPathMappings.put(pathSpec2, finalMapping);
                pm.put(pathSpec2, this._servletNameMap.get(finalMapping.getServletName()));
            }
            this._servletPathMap = pm;
        }
        if (this._chainCache != null) {
            int i = this._chainCache.length;
            while (i-- > 0) {
                if (this._chainCache[i] != null) {
                    this._chainCache[i].clear();
                }
            }
        }
        if (ServletHandler.LOG.isDebugEnabled()) {
            ServletHandler.LOG.debug("filterNameMap=" + this._filterNameMap, new Object[0]);
            ServletHandler.LOG.debug("pathFilters=" + this._filterPathMappings, new Object[0]);
            ServletHandler.LOG.debug("servletFilterMap=" + this._filterNameMappings, new Object[0]);
            ServletHandler.LOG.debug("servletPathMap=" + this._servletPathMap, new Object[0]);
            ServletHandler.LOG.debug("servletNameMap=" + this._servletNameMap, new Object[0]);
        }
        try {
            if ((this._contextHandler != null && this._contextHandler.isStarted()) || (this._contextHandler == null && this.isStarted())) {
                this.initialize();
            }
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    protected void notFound(final Request baseRequest, final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
        if (ServletHandler.LOG.isDebugEnabled()) {
            ServletHandler.LOG.debug("Not Found {}", request.getRequestURI());
        }
        if (this.getHandler() != null) {
            this.nextHandle(URIUtil.addPaths(request.getServletPath(), request.getPathInfo()), baseRequest, request, response);
        }
    }
    
    protected synchronized boolean containsFilterHolder(final FilterHolder holder) {
        if (this._filters == null) {
            return false;
        }
        boolean found = false;
        for (final FilterHolder f : this._filters) {
            if (f == holder) {
                found = true;
            }
        }
        return found;
    }
    
    protected synchronized boolean containsServletHolder(final ServletHolder holder) {
        if (this._servlets == null) {
            return false;
        }
        boolean found = false;
        for (final ServletHolder s : this._servlets) {
            if (s == holder) {
                found = true;
            }
        }
        return found;
    }
    
    public void setFilterChainsCached(final boolean filterChainsCached) {
        this._filterChainsCached = filterChainsCached;
    }
    
    public void setFilterMappings(final FilterMapping[] filterMappings) {
        this.updateBeans(this._filterMappings, filterMappings);
        this._filterMappings = filterMappings;
        if (this.isStarted()) {
            this.updateMappings();
        }
        this.invalidateChainsCache();
    }
    
    public synchronized void setFilters(final FilterHolder[] holders) {
        if (holders != null) {
            for (final FilterHolder holder : holders) {
                holder.setServletHandler(this);
            }
        }
        this.updateBeans(this._filters, holders);
        this._filters = holders;
        this.updateNameMappings();
        this.invalidateChainsCache();
    }
    
    public void setServletMappings(final ServletMapping[] servletMappings) {
        this.updateBeans(this._servletMappings, servletMappings);
        this._servletMappings = servletMappings;
        if (this.isStarted()) {
            this.updateMappings();
        }
        this.invalidateChainsCache();
    }
    
    public synchronized void setServlets(final ServletHolder[] holders) {
        if (holders != null) {
            for (final ServletHolder holder : holders) {
                holder.setServletHandler(this);
            }
        }
        this.updateBeans(this._servlets, holders);
        this._servlets = holders;
        this.updateNameMappings();
        this.invalidateChainsCache();
    }
    
    public int getMaxFilterChainsCacheSize() {
        return this._maxFilterChainsCacheSize;
    }
    
    public void setMaxFilterChainsCacheSize(final int maxFilterChainsCacheSize) {
        this._maxFilterChainsCacheSize = maxFilterChainsCacheSize;
    }
    
    void destroyServlet(final Servlet servlet) {
        if (this._contextHandler != null) {
            this._contextHandler.destroyServlet(servlet);
        }
    }
    
    void destroyFilter(final Filter filter) {
        if (this._contextHandler != null) {
            this._contextHandler.destroyFilter(filter);
        }
    }
    
    static {
        LOG = Log.getLogger(ServletHandler.class);
    }
    
    private class CachedChain implements FilterChain
    {
        FilterHolder _filterHolder;
        CachedChain _next;
        ServletHolder _servletHolder;
        
        CachedChain(final List<FilterHolder> filters, final ServletHolder servletHolder) {
            if (filters.size() > 0) {
                this._filterHolder = filters.get(0);
                filters.remove(0);
                this._next = new CachedChain(filters, servletHolder);
            }
            else {
                this._servletHolder = servletHolder;
            }
        }
        
        @Override
        public void doFilter(final ServletRequest request, final ServletResponse response) throws IOException, ServletException {
            final Request baseRequest = Request.getBaseRequest(request);
            if (this._filterHolder != null) {
                if (ServletHandler.LOG.isDebugEnabled()) {
                    ServletHandler.LOG.debug("call filter {}", this._filterHolder);
                }
                final Filter filter = this._filterHolder.getFilter();
                if (baseRequest.isAsyncSupported() && !this._filterHolder.isAsyncSupported()) {
                    try {
                        baseRequest.setAsyncSupported(false, this._filterHolder.toString());
                        filter.doFilter(request, response, this._next);
                    }
                    finally {
                        baseRequest.setAsyncSupported(true, null);
                    }
                }
                else {
                    filter.doFilter(request, response, this._next);
                }
                return;
            }
            final HttpServletRequest srequest = (HttpServletRequest)request;
            if (this._servletHolder == null) {
                ServletHandler.this.notFound(baseRequest, srequest, (HttpServletResponse)response);
            }
            else {
                if (ServletHandler.LOG.isDebugEnabled()) {
                    ServletHandler.LOG.debug("call servlet " + this._servletHolder, new Object[0]);
                }
                this._servletHolder.handle(baseRequest, request, response);
            }
        }
        
        @Override
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
        final Request _baseRequest;
        final List<FilterHolder> _chain;
        final ServletHolder _servletHolder;
        int _filter;
        
        Chain(final Request baseRequest, final List<FilterHolder> filters, final ServletHolder servletHolder) {
            this._filter = 0;
            this._baseRequest = baseRequest;
            this._chain = filters;
            this._servletHolder = servletHolder;
        }
        
        @Override
        public void doFilter(final ServletRequest request, final ServletResponse response) throws IOException, ServletException {
            if (ServletHandler.LOG.isDebugEnabled()) {
                ServletHandler.LOG.debug("doFilter " + this._filter, new Object[0]);
            }
            if (this._filter < this._chain.size()) {
                final FilterHolder holder = this._chain.get(this._filter++);
                if (ServletHandler.LOG.isDebugEnabled()) {
                    ServletHandler.LOG.debug("call filter " + holder, new Object[0]);
                }
                final Filter filter = holder.getFilter();
                if (!holder.isAsyncSupported() && this._baseRequest.isAsyncSupported()) {
                    try {
                        this._baseRequest.setAsyncSupported(false, holder.toString());
                        filter.doFilter(request, response, this);
                    }
                    finally {
                        this._baseRequest.setAsyncSupported(true, null);
                    }
                }
                else {
                    filter.doFilter(request, response, this);
                }
                return;
            }
            final HttpServletRequest srequest = (HttpServletRequest)request;
            if (this._servletHolder == null) {
                ServletHandler.this.notFound(Request.getBaseRequest(request), srequest, (HttpServletResponse)response);
            }
            else {
                if (ServletHandler.LOG.isDebugEnabled()) {
                    ServletHandler.LOG.debug("call servlet {}", this._servletHolder);
                }
                this._servletHolder.handle(this._baseRequest, request, response);
            }
        }
        
        @Override
        public String toString() {
            final StringBuilder b = new StringBuilder();
            for (final FilterHolder f : this._chain) {
                b.append(f.toString());
                b.append("->");
            }
            b.append(this._servletHolder);
            return b.toString();
        }
    }
    
    public static class Default404Servlet extends HttpServlet
    {
        @Override
        protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
            resp.sendError(404);
        }
    }
}
