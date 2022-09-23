// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.jetty.servlet;

import javax.servlet.http.HttpSessionActivationListener;
import javax.servlet.http.HttpSessionBindingListener;
import java.util.Iterator;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.ServletContext;
import java.util.List;
import java.util.ArrayList;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import org.mortbay.jetty.HttpOnlyCookie;
import org.mortbay.jetty.Server;
import javax.servlet.http.HttpSessionListener;
import org.mortbay.util.LazyList;
import javax.servlet.http.HttpSessionAttributeListener;
import java.util.EventListener;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;
import org.mortbay.jetty.handler.ContextHandler;
import org.mortbay.jetty.SessionIdManager;
import javax.servlet.http.HttpSessionContext;
import org.mortbay.jetty.SessionManager;
import org.mortbay.component.AbstractLifeCycle;

public abstract class AbstractSessionManager extends AbstractLifeCycle implements SessionManager
{
    public static final int __distantFuture = 628992000;
    private static final HttpSessionContext __nullSessionContext;
    private boolean _usingCookies;
    protected int _dftMaxIdleSecs;
    protected SessionHandler _sessionHandler;
    protected boolean _httpOnly;
    protected int _maxSessions;
    protected int _minSessions;
    protected SessionIdManager _sessionIdManager;
    protected boolean _secureCookies;
    protected Object _sessionAttributeListeners;
    protected Object _sessionListeners;
    protected ClassLoader _loader;
    protected ContextHandler.SContext _context;
    protected String _sessionCookie;
    protected String _sessionURL;
    protected String _sessionURLPrefix;
    protected String _sessionDomain;
    protected String _sessionPath;
    protected int _maxCookieAge;
    protected int _refreshCookieAge;
    protected boolean _nodeIdInSessionId;
    
    public AbstractSessionManager() {
        this._usingCookies = true;
        this._dftMaxIdleSecs = -1;
        this._httpOnly = false;
        this._maxSessions = 0;
        this._minSessions = 0;
        this._secureCookies = false;
        this._sessionCookie = "JSESSIONID";
        this._sessionURL = "jsessionid";
        this._sessionURLPrefix = ";" + this._sessionURL + "=";
        this._maxCookieAge = -1;
    }
    
    public Cookie access(final HttpSession session, final boolean secure) {
        final long now = System.currentTimeMillis();
        final Session s = ((SessionIf)session).getSession();
        s.access(now);
        if (this.isUsingCookies() && (s.isIdChanged() || (this.getMaxCookieAge() > 0 && this.getRefreshCookieAge() > 0 && (now - s.getCookieSetTime()) / 1000L > this.getRefreshCookieAge()))) {
            final Cookie cookie = this.getSessionCookie(session, this._context.getContextPath(), secure);
            s.cookieSet();
            s.setIdChanged(false);
            return cookie;
        }
        return null;
    }
    
    public void addEventListener(final EventListener listener) {
        if (listener instanceof HttpSessionAttributeListener) {
            this._sessionAttributeListeners = LazyList.add(this._sessionAttributeListeners, listener);
        }
        if (listener instanceof HttpSessionListener) {
            this._sessionListeners = LazyList.add(this._sessionListeners, listener);
        }
    }
    
    public void clearEventListeners() {
        this._sessionAttributeListeners = null;
        this._sessionListeners = null;
    }
    
    public void complete(final HttpSession session) {
        final Session s = ((SessionIf)session).getSession();
        s.complete();
    }
    
    public void doStart() throws Exception {
        this._context = ContextHandler.getCurrentContext();
        this._loader = Thread.currentThread().getContextClassLoader();
        if (this._sessionIdManager == null) {
            final Server server = this.getSessionHandler().getServer();
            synchronized (server) {
                this._sessionIdManager = server.getSessionIdManager();
                if (this._sessionIdManager == null) {
                    server.setSessionIdManager(this._sessionIdManager = new HashSessionIdManager());
                }
            }
        }
        if (!this._sessionIdManager.isStarted()) {
            this._sessionIdManager.start();
        }
        if (this._context != null) {
            String tmp = this._context.getInitParameter("org.mortbay.jetty.servlet.SessionCookie");
            if (tmp != null) {
                this._sessionCookie = tmp;
            }
            tmp = this._context.getInitParameter("org.mortbay.jetty.servlet.SessionURL");
            if (tmp != null) {
                this._sessionURL = ((tmp == null || "none".equals(tmp)) ? null : tmp);
                this._sessionURLPrefix = ((tmp == null || "none".equals(tmp)) ? null : (";" + this._sessionURL + "="));
            }
            if (this._maxCookieAge == -1) {
                tmp = this._context.getInitParameter("org.mortbay.jetty.servlet.MaxAge");
                if (tmp != null) {
                    this._maxCookieAge = Integer.parseInt(tmp.trim());
                }
            }
            if (this._sessionDomain == null) {
                this._sessionDomain = this._context.getInitParameter("org.mortbay.jetty.servlet.SessionDomain");
            }
            if (this._sessionPath == null) {
                this._sessionPath = this._context.getInitParameter("org.mortbay.jetty.servlet.SessionPath");
            }
        }
        super.doStart();
    }
    
    public void doStop() throws Exception {
        super.doStop();
        this.invalidateSessions();
        this._loader = null;
    }
    
    public boolean getHttpOnly() {
        return this._httpOnly;
    }
    
    public HttpSession getHttpSession(final String nodeId) {
        final String cluster_id = this.getIdManager().getClusterId(nodeId);
        synchronized (this) {
            final Session session = this.getSession(cluster_id);
            if (session != null && !session.getNodeId().equals(nodeId)) {
                session.setIdChanged(true);
            }
            return session;
        }
    }
    
    public SessionIdManager getIdManager() {
        return this._sessionIdManager;
    }
    
    public int getMaxCookieAge() {
        return this._maxCookieAge;
    }
    
    public int getMaxInactiveInterval() {
        return this._dftMaxIdleSecs;
    }
    
    public int getMaxSessions() {
        return this._maxSessions;
    }
    
    public SessionIdManager getMetaManager() {
        return this.getIdManager();
    }
    
    public int getMinSessions() {
        return this._minSessions;
    }
    
    public int getRefreshCookieAge() {
        return this._refreshCookieAge;
    }
    
    public boolean getSecureCookies() {
        return this._secureCookies;
    }
    
    public String getSessionCookie() {
        return this._sessionCookie;
    }
    
    public Cookie getSessionCookie(final HttpSession session, final String contextPath, final boolean requestIsSecure) {
        if (this.isUsingCookies()) {
            final String id = this.getNodeId(session);
            final Cookie cookie = this.getHttpOnly() ? new HttpOnlyCookie(this._sessionCookie, id) : new Cookie(this._sessionCookie, id);
            cookie.setPath((contextPath == null || contextPath.length() == 0) ? "/" : contextPath);
            cookie.setMaxAge(this.getMaxCookieAge());
            cookie.setSecure(requestIsSecure && this.getSecureCookies());
            if (this._sessionDomain != null) {
                cookie.setDomain(this._sessionDomain);
            }
            if (this._sessionPath != null) {
                cookie.setPath(this._sessionPath);
            }
            return cookie;
        }
        return null;
    }
    
    public String getSessionDomain() {
        return this._sessionDomain;
    }
    
    public SessionHandler getSessionHandler() {
        return this._sessionHandler;
    }
    
    public abstract Map getSessionMap();
    
    public String getSessionPath() {
        return this._sessionPath;
    }
    
    public abstract int getSessions();
    
    public String getSessionURL() {
        return this._sessionURL;
    }
    
    public String getSessionURLPrefix() {
        return this._sessionURLPrefix;
    }
    
    public boolean isUsingCookies() {
        return this._usingCookies;
    }
    
    public boolean isValid(final HttpSession session) {
        final Session s = ((SessionIf)session).getSession();
        return s.isValid();
    }
    
    public String getClusterId(final HttpSession session) {
        final Session s = ((SessionIf)session).getSession();
        return s.getClusterId();
    }
    
    public String getNodeId(final HttpSession session) {
        final Session s = ((SessionIf)session).getSession();
        return s.getNodeId();
    }
    
    public HttpSession newHttpSession(final HttpServletRequest request) {
        final Session session = this.newSession(request);
        session.setMaxInactiveInterval(this._dftMaxIdleSecs);
        this.addSession(session, true);
        return session;
    }
    
    public void removeEventListener(final EventListener listener) {
        if (listener instanceof HttpSessionAttributeListener) {
            this._sessionAttributeListeners = LazyList.remove(this._sessionAttributeListeners, listener);
        }
        if (listener instanceof HttpSessionListener) {
            this._sessionListeners = LazyList.remove(this._sessionListeners, listener);
        }
    }
    
    public void resetStats() {
        this._minSessions = this.getSessions();
        this._maxSessions = this.getSessions();
    }
    
    public void setHttpOnly(final boolean httpOnly) {
        this._httpOnly = httpOnly;
    }
    
    public void setIdManager(final SessionIdManager metaManager) {
        this._sessionIdManager = metaManager;
    }
    
    public void setMaxCookieAge(final int maxCookieAgeInSeconds) {
        this._maxCookieAge = maxCookieAgeInSeconds;
        if (this._maxCookieAge > 0 && this._refreshCookieAge == 0) {
            this._refreshCookieAge = this._maxCookieAge / 3;
        }
    }
    
    public void setMaxInactiveInterval(final int seconds) {
        this._dftMaxIdleSecs = seconds;
    }
    
    public void setMetaManager(final SessionIdManager metaManager) {
        this.setIdManager(metaManager);
    }
    
    public void setRefreshCookieAge(final int ageInSeconds) {
        this._refreshCookieAge = ageInSeconds;
    }
    
    public void setSecureCookies(final boolean secureCookies) {
        this._secureCookies = secureCookies;
    }
    
    public void setSessionCookie(final String cookieName) {
        this._sessionCookie = cookieName;
    }
    
    public void setSessionDomain(final String domain) {
        this._sessionDomain = domain;
    }
    
    public void setSessionHandler(final SessionHandler sessionHandler) {
        this._sessionHandler = sessionHandler;
    }
    
    public void setSessionPath(final String path) {
        this._sessionPath = path;
    }
    
    public void setSessionURL(final String param) {
        this._sessionURL = ((param == null || "none".equals(param)) ? null : param);
        this._sessionURLPrefix = ((param == null || "none".equals(param)) ? null : (";" + this._sessionURL + "="));
    }
    
    public void setUsingCookies(final boolean usingCookies) {
        this._usingCookies = usingCookies;
    }
    
    protected abstract void addSession(final Session p0);
    
    protected void addSession(final Session session, final boolean created) {
        synchronized (this._sessionIdManager) {
            this._sessionIdManager.addSession(session);
            synchronized (this) {
                this.addSession(session);
                if (this.getSessions() > this._maxSessions) {
                    this._maxSessions = this.getSessions();
                }
            }
        }
        if (!created) {
            session.didActivate();
        }
        else if (this._sessionListeners != null) {
            final HttpSessionEvent event = new HttpSessionEvent(session);
            for (int i = 0; i < LazyList.size(this._sessionListeners); ++i) {
                ((HttpSessionListener)LazyList.get(this._sessionListeners, i)).sessionCreated(event);
            }
        }
    }
    
    public abstract Session getSession(final String p0);
    
    protected abstract void invalidateSessions();
    
    protected abstract Session newSession(final HttpServletRequest p0);
    
    public boolean isNodeIdInSessionId() {
        return this._nodeIdInSessionId;
    }
    
    public void setNodeIdInSessionId(final boolean nodeIdInSessionId) {
        this._nodeIdInSessionId = nodeIdInSessionId;
    }
    
    public void removeSession(final HttpSession session, final boolean invalidate) {
        final Session s = ((SessionIf)session).getSession();
        this.removeSession(s, invalidate);
    }
    
    public void removeSession(final Session session, final boolean invalidate) {
        boolean removed = false;
        synchronized (this) {
            if (this.getSession(session.getClusterId()) != null) {
                removed = true;
                this.removeSession(session.getClusterId());
            }
        }
        if (removed && invalidate) {
            this._sessionIdManager.removeSession(session);
            this._sessionIdManager.invalidateAll(session.getClusterId());
        }
        if (invalidate && this._sessionListeners != null) {
            final HttpSessionEvent event = new HttpSessionEvent(session);
            int i = LazyList.size(this._sessionListeners);
            while (i-- > 0) {
                ((HttpSessionListener)LazyList.get(this._sessionListeners, i)).sessionDestroyed(event);
            }
        }
        if (!invalidate) {
            session.willPassivate();
        }
    }
    
    protected abstract void removeSession(final String p0);
    
    static {
        __nullSessionContext = new NullSessionContext();
    }
    
    public static class NullSessionContext implements HttpSessionContext
    {
        private NullSessionContext() {
        }
        
        public Enumeration getIds() {
            return Collections.enumeration((Collection<Object>)Collections.EMPTY_LIST);
        }
        
        public HttpSession getSession(final String id) {
            return null;
        }
    }
    
    public abstract class Session implements SessionIf, Serializable
    {
        protected final String _clusterId;
        protected final String _nodeId;
        protected boolean _idChanged;
        protected final long _created;
        protected long _cookieSet;
        protected long _accessed;
        protected long _lastAccessed;
        protected boolean _invalid;
        protected boolean _doInvalidate;
        protected long _maxIdleMs;
        protected boolean _newSession;
        protected Map _values;
        protected int _requests;
        
        protected Session(final HttpServletRequest request) {
            this._maxIdleMs = AbstractSessionManager.this._dftMaxIdleSecs * 1000;
            this._newSession = true;
            this._created = System.currentTimeMillis();
            this._clusterId = AbstractSessionManager.this._sessionIdManager.newSessionId(request, this._created);
            this._nodeId = AbstractSessionManager.this._sessionIdManager.getNodeId(this._clusterId, request);
            this._accessed = this._created;
            this._requests = 1;
        }
        
        protected Session(final long created, final String clusterId) {
            this._maxIdleMs = AbstractSessionManager.this._dftMaxIdleSecs * 1000;
            this._created = created;
            this._clusterId = clusterId;
            this._nodeId = AbstractSessionManager.this._sessionIdManager.getNodeId(this._clusterId, null);
            this._accessed = this._created;
        }
        
        public Session getSession() {
            return this;
        }
        
        protected void initValues() {
            this._values = this.newAttributeMap();
        }
        
        public synchronized Object getAttribute(final String name) {
            if (this._invalid) {
                throw new IllegalStateException();
            }
            if (null == this._values) {
                return null;
            }
            return this._values.get(name);
        }
        
        public synchronized Enumeration getAttributeNames() {
            if (this._invalid) {
                throw new IllegalStateException();
            }
            final List names = (this._values == null) ? Collections.EMPTY_LIST : new ArrayList(this._values.keySet());
            return Collections.enumeration((Collection<Object>)names);
        }
        
        public long getCookieSetTime() {
            return this._cookieSet;
        }
        
        public long getCreationTime() throws IllegalStateException {
            if (this._invalid) {
                throw new IllegalStateException();
            }
            return this._created;
        }
        
        public String getId() throws IllegalStateException {
            return AbstractSessionManager.this._nodeIdInSessionId ? this._nodeId : this._clusterId;
        }
        
        protected String getNodeId() {
            return this._nodeId;
        }
        
        protected String getClusterId() {
            return this._clusterId;
        }
        
        public long getLastAccessedTime() throws IllegalStateException {
            if (this._invalid) {
                throw new IllegalStateException();
            }
            return this._lastAccessed;
        }
        
        public int getMaxInactiveInterval() {
            if (this._invalid) {
                throw new IllegalStateException();
            }
            return (int)(this._maxIdleMs / 1000L);
        }
        
        public ServletContext getServletContext() {
            return AbstractSessionManager.this._context;
        }
        
        public HttpSessionContext getSessionContext() throws IllegalStateException {
            if (this._invalid) {
                throw new IllegalStateException();
            }
            return AbstractSessionManager.__nullSessionContext;
        }
        
        public Object getValue(final String name) throws IllegalStateException {
            return this.getAttribute(name);
        }
        
        public synchronized String[] getValueNames() throws IllegalStateException {
            if (this._invalid) {
                throw new IllegalStateException();
            }
            if (this._values == null) {
                return new String[0];
            }
            final String[] a = new String[this._values.size()];
            return (String[])this._values.keySet().toArray(a);
        }
        
        protected void access(final long time) {
            synchronized (this) {
                this._newSession = false;
                this._lastAccessed = this._accessed;
                this._accessed = time;
                ++this._requests;
            }
        }
        
        protected void complete() {
            synchronized (this) {
                --this._requests;
                if (this._doInvalidate && this._requests <= 0) {
                    this.doInvalidate();
                }
            }
        }
        
        protected void timeout() throws IllegalStateException {
            AbstractSessionManager.this.removeSession(this, true);
            synchronized (this) {
                if (!this._invalid) {
                    if (this._requests <= 0) {
                        this.doInvalidate();
                    }
                    else {
                        this._doInvalidate = true;
                    }
                }
            }
        }
        
        public void invalidate() throws IllegalStateException {
            AbstractSessionManager.this.removeSession(this, true);
            this.doInvalidate();
        }
        
        protected void doInvalidate() throws IllegalStateException {
            try {
                if (this._invalid) {
                    throw new IllegalStateException();
                }
                while (this._values != null && this._values.size() > 0) {
                    final ArrayList keys;
                    synchronized (this) {
                        keys = new ArrayList(this._values.keySet());
                    }
                    for (final String key : keys) {
                        final Object value;
                        synchronized (this) {
                            value = this._values.remove(key);
                        }
                        this.unbindValue(key, value);
                        if (AbstractSessionManager.this._sessionAttributeListeners != null) {
                            final HttpSessionBindingEvent event = new HttpSessionBindingEvent(this, key, value);
                            for (int i = 0; i < LazyList.size(AbstractSessionManager.this._sessionAttributeListeners); ++i) {
                                ((HttpSessionAttributeListener)LazyList.get(AbstractSessionManager.this._sessionAttributeListeners, i)).attributeRemoved(event);
                            }
                        }
                    }
                }
            }
            finally {
                this._invalid = true;
            }
        }
        
        public boolean isIdChanged() {
            return this._idChanged;
        }
        
        public boolean isNew() throws IllegalStateException {
            if (this._invalid) {
                throw new IllegalStateException();
            }
            return this._newSession;
        }
        
        public void putValue(final String name, final Object value) throws IllegalStateException {
            this.setAttribute(name, value);
        }
        
        public synchronized void removeAttribute(final String name) {
            if (this._invalid) {
                throw new IllegalStateException();
            }
            if (this._values == null) {
                return;
            }
            final Object old = this._values.remove(name);
            if (old != null) {
                this.unbindValue(name, old);
                if (AbstractSessionManager.this._sessionAttributeListeners != null) {
                    final HttpSessionBindingEvent event = new HttpSessionBindingEvent(this, name, old);
                    for (int i = 0; i < LazyList.size(AbstractSessionManager.this._sessionAttributeListeners); ++i) {
                        ((HttpSessionAttributeListener)LazyList.get(AbstractSessionManager.this._sessionAttributeListeners, i)).attributeRemoved(event);
                    }
                }
            }
        }
        
        public void removeValue(final String name) throws IllegalStateException {
            this.removeAttribute(name);
        }
        
        public synchronized void setAttribute(final String name, final Object value) {
            if (value == null) {
                this.removeAttribute(name);
                return;
            }
            if (this._invalid) {
                throw new IllegalStateException();
            }
            if (this._values == null) {
                this._values = this.newAttributeMap();
            }
            final Object oldValue = this._values.put(name, value);
            if (oldValue == null || !value.equals(oldValue)) {
                this.unbindValue(name, oldValue);
                this.bindValue(name, value);
                if (AbstractSessionManager.this._sessionAttributeListeners != null) {
                    final HttpSessionBindingEvent event = new HttpSessionBindingEvent(this, name, (oldValue == null) ? value : oldValue);
                    for (int i = 0; i < LazyList.size(AbstractSessionManager.this._sessionAttributeListeners); ++i) {
                        final HttpSessionAttributeListener l = (HttpSessionAttributeListener)LazyList.get(AbstractSessionManager.this._sessionAttributeListeners, i);
                        if (oldValue == null) {
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
        }
        
        public void setIdChanged(final boolean changed) {
            this._idChanged = changed;
        }
        
        public void setMaxInactiveInterval(final int secs) {
            this._maxIdleMs = secs * 1000L;
        }
        
        public String toString() {
            return this.getClass().getName() + ":" + this.getId() + "@" + this.hashCode();
        }
        
        protected void bindValue(final String name, final Object value) {
            if (value != null && value instanceof HttpSessionBindingListener) {
                ((HttpSessionBindingListener)value).valueBound(new HttpSessionBindingEvent(this, name));
            }
        }
        
        protected boolean isValid() {
            return !this._invalid;
        }
        
        protected abstract Map newAttributeMap();
        
        protected void cookieSet() {
            this._cookieSet = this._accessed;
        }
        
        protected void unbindValue(final String name, final Object value) {
            if (value != null && value instanceof HttpSessionBindingListener) {
                ((HttpSessionBindingListener)value).valueUnbound(new HttpSessionBindingEvent(this, name));
            }
        }
        
        protected synchronized void willPassivate() {
            final HttpSessionEvent event = new HttpSessionEvent(this);
            for (final Object value : this._values.values()) {
                if (value instanceof HttpSessionActivationListener) {
                    final HttpSessionActivationListener listener = (HttpSessionActivationListener)value;
                    listener.sessionWillPassivate(event);
                }
            }
        }
        
        protected synchronized void didActivate() {
            final HttpSessionEvent event = new HttpSessionEvent(this);
            for (final Object value : this._values.values()) {
                if (value instanceof HttpSessionActivationListener) {
                    final HttpSessionActivationListener listener = (HttpSessionActivationListener)value;
                    listener.sessionDidActivate(event);
                }
            }
        }
    }
    
    public interface SessionIf extends HttpSession
    {
        Session getSession();
    }
}
