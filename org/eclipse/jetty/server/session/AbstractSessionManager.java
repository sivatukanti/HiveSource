// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.server.session;

import java.util.Enumeration;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionEvent;
import org.eclipse.jetty.util.annotation.ManagedOperation;
import javax.servlet.http.HttpServletRequest;
import org.eclipse.jetty.server.Server;
import java.util.Iterator;
import java.util.EventListener;
import org.eclipse.jetty.http.HttpCookie;
import javax.servlet.http.HttpSession;
import org.eclipse.jetty.util.annotation.ManagedAttribute;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.Collections;
import java.util.Collection;
import java.util.HashSet;
import java.util.Arrays;
import javax.servlet.SessionCookieConfig;
import org.eclipse.jetty.util.statistic.SampleStatistic;
import org.eclipse.jetty.util.statistic.CounterStatistic;
import org.eclipse.jetty.server.handler.ContextHandler;
import javax.servlet.http.HttpSessionIdListener;
import javax.servlet.http.HttpSessionListener;
import javax.servlet.http.HttpSessionAttributeListener;
import java.util.List;
import org.eclipse.jetty.server.SessionIdManager;
import javax.servlet.http.HttpSessionContext;
import java.math.BigDecimal;
import javax.servlet.SessionTrackingMode;
import java.util.Set;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.util.annotation.ManagedObject;
import org.eclipse.jetty.server.SessionManager;
import org.eclipse.jetty.util.component.ContainerLifeCycle;

@ManagedObject("Abstract Session Manager")
public abstract class AbstractSessionManager extends ContainerLifeCycle implements SessionManager
{
    static final Logger __log;
    public Set<SessionTrackingMode> __defaultSessionTrackingModes;
    public static final int __distantFuture = 628992000;
    public static final BigDecimal MAX_INACTIVE_MINUTES;
    static final HttpSessionContext __nullSessionContext;
    private boolean _usingCookies;
    protected int _dftMaxIdleSecs;
    protected SessionHandler _sessionHandler;
    protected boolean _httpOnly;
    protected SessionIdManager _sessionIdManager;
    protected boolean _secureCookies;
    protected boolean _secureRequestOnly;
    protected final List<HttpSessionAttributeListener> _sessionAttributeListeners;
    protected final List<HttpSessionListener> _sessionListeners;
    protected final List<HttpSessionIdListener> _sessionIdListeners;
    protected ClassLoader _loader;
    protected ContextHandler.Context _context;
    protected String _sessionCookie;
    protected String _sessionIdPathParameterName;
    protected String _sessionIdPathParameterNamePrefix;
    protected String _sessionDomain;
    protected String _sessionPath;
    protected int _maxCookieAge;
    protected int _refreshCookieAge;
    protected boolean _nodeIdInSessionId;
    protected boolean _checkingRemoteSessionIdEncoding;
    protected String _sessionComment;
    public Set<SessionTrackingMode> _sessionTrackingModes;
    private boolean _usingURLs;
    protected final CounterStatistic _sessionsStats;
    protected final SampleStatistic _sessionTimeStats;
    private SessionCookieConfig _cookieConfig;
    
    public AbstractSessionManager() {
        this.__defaultSessionTrackingModes = Collections.unmodifiableSet((Set<? extends SessionTrackingMode>)new HashSet<SessionTrackingMode>(Arrays.asList(SessionTrackingMode.COOKIE, SessionTrackingMode.URL)));
        this._usingCookies = true;
        this._dftMaxIdleSecs = -1;
        this._httpOnly = false;
        this._secureCookies = false;
        this._secureRequestOnly = true;
        this._sessionAttributeListeners = new CopyOnWriteArrayList<HttpSessionAttributeListener>();
        this._sessionListeners = new CopyOnWriteArrayList<HttpSessionListener>();
        this._sessionIdListeners = new CopyOnWriteArrayList<HttpSessionIdListener>();
        this._sessionCookie = "JSESSIONID";
        this._sessionIdPathParameterName = "jsessionid";
        this._sessionIdPathParameterNamePrefix = ";" + this._sessionIdPathParameterName + "=";
        this._maxCookieAge = -1;
        this._sessionsStats = new CounterStatistic();
        this._sessionTimeStats = new SampleStatistic();
        this._cookieConfig = new CookieConfig();
        this.setSessionTrackingModes(this.__defaultSessionTrackingModes);
    }
    
    public ContextHandler.Context getContext() {
        return this._context;
    }
    
    public ContextHandler getContextHandler() {
        return this._context.getContextHandler();
    }
    
    @ManagedAttribute("path of the session cookie, or null for default")
    public String getSessionPath() {
        return this._sessionPath;
    }
    
    @ManagedAttribute("if greater the zero, the time in seconds a session cookie will last for")
    public int getMaxCookieAge() {
        return this._maxCookieAge;
    }
    
    @Override
    public HttpCookie access(final HttpSession session, final boolean secure) {
        final long now = System.currentTimeMillis();
        final AbstractSession s = ((SessionIf)session).getSession();
        if (s.access(now) && this.isUsingCookies() && (s.isIdChanged() || (this.getSessionCookieConfig().getMaxAge() > 0 && this.getRefreshCookieAge() > 0 && (now - s.getCookieSetTime()) / 1000L > this.getRefreshCookieAge()))) {
            final HttpCookie cookie = this.getSessionCookie(session, (this._context == null) ? "/" : this._context.getContextPath(), secure);
            s.cookieSet();
            s.setIdChanged(false);
            return cookie;
        }
        return null;
    }
    
    @Override
    public void addEventListener(final EventListener listener) {
        if (listener instanceof HttpSessionAttributeListener) {
            this._sessionAttributeListeners.add((HttpSessionAttributeListener)listener);
        }
        if (listener instanceof HttpSessionListener) {
            this._sessionListeners.add((HttpSessionListener)listener);
        }
        if (listener instanceof HttpSessionIdListener) {
            this._sessionIdListeners.add((HttpSessionIdListener)listener);
        }
        this.addBean(listener, false);
    }
    
    @Override
    public void clearEventListeners() {
        for (final EventListener e : this.getBeans(EventListener.class)) {
            this.removeBean(e);
        }
        this._sessionAttributeListeners.clear();
        this._sessionListeners.clear();
        this._sessionIdListeners.clear();
    }
    
    @Override
    public void complete(final HttpSession session) {
        final AbstractSession s = ((SessionIf)session).getSession();
        s.complete();
    }
    
    public void doStart() throws Exception {
        this._context = ContextHandler.getCurrentContext();
        this._loader = Thread.currentThread().getContextClassLoader();
        final Server server = this.getSessionHandler().getServer();
        synchronized (server) {
            if (this._sessionIdManager == null) {
                this._sessionIdManager = server.getSessionIdManager();
                if (this._sessionIdManager == null) {
                    final ClassLoader serverLoader = server.getClass().getClassLoader();
                    try {
                        Thread.currentThread().setContextClassLoader(serverLoader);
                        server.setSessionIdManager(this._sessionIdManager = new HashSessionIdManager());
                        server.manage(this._sessionIdManager);
                        this._sessionIdManager.start();
                    }
                    finally {
                        Thread.currentThread().setContextClassLoader(this._loader);
                    }
                }
                this.addBean(this._sessionIdManager, false);
            }
        }
        if (this._context != null) {
            String tmp = this._context.getInitParameter("org.eclipse.jetty.servlet.SessionCookie");
            if (tmp != null) {
                this._sessionCookie = tmp;
            }
            tmp = this._context.getInitParameter("org.eclipse.jetty.servlet.SessionIdPathParameterName");
            if (tmp != null) {
                this.setSessionIdPathParameterName(tmp);
            }
            if (this._maxCookieAge == -1) {
                tmp = this._context.getInitParameter("org.eclipse.jetty.servlet.MaxAge");
                if (tmp != null) {
                    this._maxCookieAge = Integer.parseInt(tmp.trim());
                }
            }
            if (this._sessionDomain == null) {
                this._sessionDomain = this._context.getInitParameter("org.eclipse.jetty.servlet.SessionDomain");
            }
            if (this._sessionPath == null) {
                this._sessionPath = this._context.getInitParameter("org.eclipse.jetty.servlet.SessionPath");
            }
            tmp = this._context.getInitParameter("org.eclipse.jetty.servlet.CheckingRemoteSessionIdEncoding");
            if (tmp != null) {
                this._checkingRemoteSessionIdEncoding = Boolean.parseBoolean(tmp);
            }
        }
        super.doStart();
    }
    
    public void doStop() throws Exception {
        super.doStop();
        this.shutdownSessions();
        this._loader = null;
    }
    
    @ManagedAttribute("true if cookies use the http only flag")
    @Override
    public boolean getHttpOnly() {
        return this._httpOnly;
    }
    
    @Override
    public HttpSession getHttpSession(final String nodeId) {
        final String cluster_id = this.getSessionIdManager().getClusterId(nodeId);
        final AbstractSession session = this.getSession(cluster_id);
        if (session != null && !session.getNodeId().equals(nodeId)) {
            session.setIdChanged(true);
        }
        return session;
    }
    
    @ManagedAttribute("Session ID Manager")
    @Override
    public SessionIdManager getSessionIdManager() {
        return this._sessionIdManager;
    }
    
    @ManagedAttribute("defailt maximum time a session may be idle for (in s)")
    @Override
    public int getMaxInactiveInterval() {
        return this._dftMaxIdleSecs;
    }
    
    @ManagedAttribute("maximum number of simultaneous sessions")
    public int getSessionsMax() {
        return (int)this._sessionsStats.getMax();
    }
    
    @ManagedAttribute("total number of sessions")
    public int getSessionsTotal() {
        return (int)this._sessionsStats.getTotal();
    }
    
    @ManagedAttribute("time before a session cookie is re-set (in s)")
    public int getRefreshCookieAge() {
        return this._refreshCookieAge;
    }
    
    @ManagedAttribute("if true, secure cookie flag is set on session cookies")
    public boolean getSecureCookies() {
        return this._secureCookies;
    }
    
    public boolean isSecureRequestOnly() {
        return this._secureRequestOnly;
    }
    
    public void setSecureRequestOnly(final boolean secureRequestOnly) {
        this._secureRequestOnly = secureRequestOnly;
    }
    
    @ManagedAttribute("the set session cookie")
    public String getSessionCookie() {
        return this._sessionCookie;
    }
    
    @Override
    public HttpCookie getSessionCookie(final HttpSession session, final String contextPath, final boolean requestIsSecure) {
        if (this.isUsingCookies()) {
            String sessionPath = (this._cookieConfig.getPath() == null) ? contextPath : this._cookieConfig.getPath();
            sessionPath = ((sessionPath == null || sessionPath.length() == 0) ? "/" : sessionPath);
            final String id = this.getNodeId(session);
            HttpCookie cookie = null;
            if (this._sessionComment == null) {
                cookie = new HttpCookie(this._cookieConfig.getName(), id, this._cookieConfig.getDomain(), sessionPath, this._cookieConfig.getMaxAge(), this._cookieConfig.isHttpOnly(), this._cookieConfig.isSecure() || (this.isSecureRequestOnly() && requestIsSecure));
            }
            else {
                cookie = new HttpCookie(this._cookieConfig.getName(), id, this._cookieConfig.getDomain(), sessionPath, this._cookieConfig.getMaxAge(), this._cookieConfig.isHttpOnly(), this._cookieConfig.isSecure() || (this.isSecureRequestOnly() && requestIsSecure), this._sessionComment, 1);
            }
            return cookie;
        }
        return null;
    }
    
    @ManagedAttribute("domain of the session cookie, or null for the default")
    public String getSessionDomain() {
        return this._sessionDomain;
    }
    
    public SessionHandler getSessionHandler() {
        return this._sessionHandler;
    }
    
    @ManagedAttribute("number of currently active sessions")
    public int getSessions() {
        return (int)this._sessionsStats.getCurrent();
    }
    
    @ManagedAttribute("name of use for URL session tracking")
    @Override
    public String getSessionIdPathParameterName() {
        return this._sessionIdPathParameterName;
    }
    
    @Override
    public String getSessionIdPathParameterNamePrefix() {
        return this._sessionIdPathParameterNamePrefix;
    }
    
    @Override
    public boolean isUsingCookies() {
        return this._usingCookies;
    }
    
    @Override
    public boolean isValid(final HttpSession session) {
        final AbstractSession s = ((SessionIf)session).getSession();
        return s.isValid();
    }
    
    @Override
    public String getClusterId(final HttpSession session) {
        final AbstractSession s = ((SessionIf)session).getSession();
        return s.getClusterId();
    }
    
    @Override
    public String getNodeId(final HttpSession session) {
        final AbstractSession s = ((SessionIf)session).getSession();
        return s.getNodeId();
    }
    
    @Override
    public HttpSession newHttpSession(final HttpServletRequest request) {
        final AbstractSession session = this.newSession(request);
        session.setMaxInactiveInterval(this._dftMaxIdleSecs);
        if (request.isSecure()) {
            session.setAttribute("org.eclipse.jetty.security.sessionCreatedSecure", Boolean.TRUE);
        }
        this.addSession(session, true);
        return session;
    }
    
    @Override
    public void removeEventListener(final EventListener listener) {
        if (listener instanceof HttpSessionAttributeListener) {
            this._sessionAttributeListeners.remove(listener);
        }
        if (listener instanceof HttpSessionListener) {
            this._sessionListeners.remove(listener);
        }
        if (listener instanceof HttpSessionIdListener) {
            this._sessionIdListeners.remove(listener);
        }
        this.removeBean(listener);
    }
    
    @ManagedOperation(value = "reset statistics", impact = "ACTION")
    public void statsReset() {
        this._sessionsStats.reset(this.getSessions());
        this._sessionTimeStats.reset();
    }
    
    public void setHttpOnly(final boolean httpOnly) {
        this._httpOnly = httpOnly;
    }
    
    @Override
    public void setSessionIdManager(final SessionIdManager metaManager) {
        this.updateBean(this._sessionIdManager, metaManager);
        this._sessionIdManager = metaManager;
    }
    
    @Override
    public void setMaxInactiveInterval(final int seconds) {
        this._dftMaxIdleSecs = seconds;
        if (AbstractSessionManager.__log.isDebugEnabled()) {
            if (this._dftMaxIdleSecs <= 0) {
                AbstractSessionManager.__log.debug("Sessions created by this manager are immortal (default maxInactiveInterval={})", this._dftMaxIdleSecs);
            }
            else {
                AbstractSessionManager.__log.debug("SessionManager default maxInactiveInterval={}", this._dftMaxIdleSecs);
            }
        }
    }
    
    public void setRefreshCookieAge(final int ageInSeconds) {
        this._refreshCookieAge = ageInSeconds;
    }
    
    public void setSessionCookie(final String cookieName) {
        this._sessionCookie = cookieName;
    }
    
    @Override
    public void setSessionHandler(final SessionHandler sessionHandler) {
        this._sessionHandler = sessionHandler;
    }
    
    @Override
    public void setSessionIdPathParameterName(final String param) {
        this._sessionIdPathParameterName = ((param == null || "none".equals(param)) ? null : param);
        this._sessionIdPathParameterNamePrefix = ((param == null || "none".equals(param)) ? null : (";" + this._sessionIdPathParameterName + "="));
    }
    
    public void setUsingCookies(final boolean usingCookies) {
        this._usingCookies = usingCookies;
    }
    
    protected abstract void addSession(final AbstractSession p0);
    
    protected void addSession(final AbstractSession session, final boolean created) {
        synchronized (this._sessionIdManager) {
            this._sessionIdManager.addSession(session);
            this.addSession(session);
        }
        if (created) {
            this._sessionsStats.increment();
            if (this._sessionListeners != null) {
                final HttpSessionEvent event = new HttpSessionEvent(session);
                for (final HttpSessionListener listener : this._sessionListeners) {
                    listener.sessionCreated(event);
                }
            }
        }
    }
    
    public abstract AbstractSession getSession(final String p0);
    
    protected abstract void shutdownSessions() throws Exception;
    
    protected abstract AbstractSession newSession(final HttpServletRequest p0);
    
    public boolean isNodeIdInSessionId() {
        return this._nodeIdInSessionId;
    }
    
    public void setNodeIdInSessionId(final boolean nodeIdInSessionId) {
        this._nodeIdInSessionId = nodeIdInSessionId;
    }
    
    public void removeSession(final HttpSession session, final boolean invalidate) {
        final AbstractSession s = ((SessionIf)session).getSession();
        this.removeSession(s, invalidate);
    }
    
    public boolean removeSession(final AbstractSession session, final boolean invalidate) {
        final boolean removed = this.removeSession(session.getClusterId());
        if (removed) {
            this._sessionsStats.decrement();
            this._sessionTimeStats.set(Math.round((System.currentTimeMillis() - session.getCreationTime()) / 1000.0));
            this._sessionIdManager.removeSession(session);
            if (invalidate) {
                this._sessionIdManager.invalidateAll(session.getClusterId());
            }
            if (invalidate && this._sessionListeners != null) {
                final HttpSessionEvent event = new HttpSessionEvent(session);
                for (int i = this._sessionListeners.size() - 1; i >= 0; --i) {
                    this._sessionListeners.get(i).sessionDestroyed(event);
                }
            }
        }
        return removed;
    }
    
    protected abstract boolean removeSession(final String p0);
    
    @ManagedAttribute("maximum amount of time sessions have remained active (in s)")
    public long getSessionTimeMax() {
        return this._sessionTimeStats.getMax();
    }
    
    @Override
    public Set<SessionTrackingMode> getDefaultSessionTrackingModes() {
        return this.__defaultSessionTrackingModes;
    }
    
    @Override
    public Set<SessionTrackingMode> getEffectiveSessionTrackingModes() {
        return Collections.unmodifiableSet((Set<? extends SessionTrackingMode>)this._sessionTrackingModes);
    }
    
    @Override
    public void setSessionTrackingModes(final Set<SessionTrackingMode> sessionTrackingModes) {
        this._sessionTrackingModes = new HashSet<SessionTrackingMode>(sessionTrackingModes);
        this._usingCookies = this._sessionTrackingModes.contains(SessionTrackingMode.COOKIE);
        this._usingURLs = this._sessionTrackingModes.contains(SessionTrackingMode.URL);
    }
    
    @Override
    public boolean isUsingURLs() {
        return this._usingURLs;
    }
    
    @Override
    public SessionCookieConfig getSessionCookieConfig() {
        return this._cookieConfig;
    }
    
    @ManagedAttribute("total time sessions have remained valid")
    public long getSessionTimeTotal() {
        return this._sessionTimeStats.getTotal();
    }
    
    @ManagedAttribute("mean time sessions remain valid (in s)")
    public double getSessionTimeMean() {
        return this._sessionTimeStats.getMean();
    }
    
    @ManagedAttribute("standard deviation a session remained valid (in s)")
    public double getSessionTimeStdDev() {
        return this._sessionTimeStats.getStdDev();
    }
    
    @ManagedAttribute("check remote session id encoding")
    @Override
    public boolean isCheckingRemoteSessionIdEncoding() {
        return this._checkingRemoteSessionIdEncoding;
    }
    
    @Override
    public void setCheckingRemoteSessionIdEncoding(final boolean remote) {
        this._checkingRemoteSessionIdEncoding = remote;
    }
    
    @Override
    public void renewSessionId(final String oldClusterId, final String oldNodeId, final String newClusterId, final String newNodeId) {
        if (!this._sessionIdListeners.isEmpty()) {
            final AbstractSession session = this.getSession(newClusterId);
            final HttpSessionEvent event = new HttpSessionEvent(session);
            for (final HttpSessionIdListener l : this._sessionIdListeners) {
                l.sessionIdChanged(event, oldClusterId);
            }
        }
    }
    
    public void doSessionAttributeListeners(final AbstractSession session, final String name, final Object old, final Object value) {
        if (!this._sessionAttributeListeners.isEmpty()) {
            final HttpSessionBindingEvent event = new HttpSessionBindingEvent(session, name, (old == null) ? value : old);
            for (final HttpSessionAttributeListener l : this._sessionAttributeListeners) {
                if (old == null) {
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
    
    @Deprecated
    @Override
    public SessionIdManager getMetaManager() {
        throw new UnsupportedOperationException();
    }
    
    static {
        __log = SessionHandler.LOG;
        MAX_INACTIVE_MINUTES = new BigDecimal(35791394);
        __nullSessionContext = new HttpSessionContext() {
            @Override
            public HttpSession getSession(final String sessionId) {
                return null;
            }
            
            @Override
            public Enumeration getIds() {
                return Collections.enumeration((Collection<Object>)Collections.EMPTY_LIST);
            }
        };
    }
    
    public final class CookieConfig implements SessionCookieConfig
    {
        @Override
        public String getComment() {
            return AbstractSessionManager.this._sessionComment;
        }
        
        @Override
        public String getDomain() {
            return AbstractSessionManager.this._sessionDomain;
        }
        
        @Override
        public int getMaxAge() {
            return AbstractSessionManager.this._maxCookieAge;
        }
        
        @Override
        public String getName() {
            return AbstractSessionManager.this._sessionCookie;
        }
        
        @Override
        public String getPath() {
            return AbstractSessionManager.this._sessionPath;
        }
        
        @Override
        public boolean isHttpOnly() {
            return AbstractSessionManager.this._httpOnly;
        }
        
        @Override
        public boolean isSecure() {
            return AbstractSessionManager.this._secureCookies;
        }
        
        @Override
        public void setComment(final String comment) {
            if (AbstractSessionManager.this._context != null && AbstractSessionManager.this._context.getContextHandler().isAvailable()) {
                throw new IllegalStateException("CookieConfig cannot be set after ServletContext is started");
            }
            AbstractSessionManager.this._sessionComment = comment;
        }
        
        @Override
        public void setDomain(final String domain) {
            if (AbstractSessionManager.this._context != null && AbstractSessionManager.this._context.getContextHandler().isAvailable()) {
                throw new IllegalStateException("CookieConfig cannot be set after ServletContext is started");
            }
            AbstractSessionManager.this._sessionDomain = domain;
        }
        
        @Override
        public void setHttpOnly(final boolean httpOnly) {
            if (AbstractSessionManager.this._context != null && AbstractSessionManager.this._context.getContextHandler().isAvailable()) {
                throw new IllegalStateException("CookieConfig cannot be set after ServletContext is started");
            }
            AbstractSessionManager.this._httpOnly = httpOnly;
        }
        
        @Override
        public void setMaxAge(final int maxAge) {
            if (AbstractSessionManager.this._context != null && AbstractSessionManager.this._context.getContextHandler().isAvailable()) {
                throw new IllegalStateException("CookieConfig cannot be set after ServletContext is started");
            }
            AbstractSessionManager.this._maxCookieAge = maxAge;
        }
        
        @Override
        public void setName(final String name) {
            if (AbstractSessionManager.this._context != null && AbstractSessionManager.this._context.getContextHandler().isAvailable()) {
                throw new IllegalStateException("CookieConfig cannot be set after ServletContext is started");
            }
            AbstractSessionManager.this._sessionCookie = name;
        }
        
        @Override
        public void setPath(final String path) {
            if (AbstractSessionManager.this._context != null && AbstractSessionManager.this._context.getContextHandler().isAvailable()) {
                throw new IllegalStateException("CookieConfig cannot be set after ServletContext is started");
            }
            AbstractSessionManager.this._sessionPath = path;
        }
        
        @Override
        public void setSecure(final boolean secure) {
            if (AbstractSessionManager.this._context != null && AbstractSessionManager.this._context.getContextHandler().isAvailable()) {
                throw new IllegalStateException("CookieConfig cannot be set after ServletContext is started");
            }
            AbstractSessionManager.this._secureCookies = secure;
        }
    }
    
    public interface SessionIf extends HttpSession
    {
        AbstractSession getSession();
    }
}
