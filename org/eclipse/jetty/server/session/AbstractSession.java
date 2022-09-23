// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.server.session;

import java.util.Iterator;
import javax.servlet.http.HttpSessionActivationListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;
import java.util.ArrayList;
import java.util.Enumeration;
import org.eclipse.jetty.server.SessionManager;
import javax.servlet.http.HttpSessionContext;
import javax.servlet.ServletContext;
import java.util.Set;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.eclipse.jetty.util.log.Logger;

public abstract class AbstractSession implements AbstractSessionManager.SessionIf
{
    static final Logger LOG;
    public static final String SESSION_CREATED_SECURE = "org.eclipse.jetty.security.sessionCreatedSecure";
    private String _clusterId;
    private String _nodeId;
    private final AbstractSessionManager _manager;
    private boolean _idChanged;
    private final long _created;
    private long _cookieSet;
    private long _accessed;
    private long _lastAccessed;
    private boolean _invalid;
    private boolean _doInvalidate;
    private long _maxIdleMs;
    private boolean _newSession;
    private int _requests;
    
    protected AbstractSession(final AbstractSessionManager abstractSessionManager, final HttpServletRequest request) {
        this._manager = abstractSessionManager;
        this._newSession = true;
        this._created = System.currentTimeMillis();
        this._clusterId = this._manager._sessionIdManager.newSessionId(request, this._created);
        this._nodeId = this._manager._sessionIdManager.getNodeId(this._clusterId, request);
        this._accessed = this._created;
        this._lastAccessed = this._created;
        this._requests = 1;
        this._maxIdleMs = ((this._manager._dftMaxIdleSecs > 0) ? (this._manager._dftMaxIdleSecs * 1000L) : -1L);
        if (AbstractSession.LOG.isDebugEnabled()) {
            AbstractSession.LOG.debug("New session & id " + this._nodeId + " " + this._clusterId, new Object[0]);
        }
    }
    
    protected AbstractSession(final AbstractSessionManager abstractSessionManager, final long created, final long accessed, final String clusterId) {
        this._manager = abstractSessionManager;
        this._created = created;
        this._clusterId = clusterId;
        this._nodeId = this._manager._sessionIdManager.getNodeId(this._clusterId, null);
        this._accessed = accessed;
        this._lastAccessed = accessed;
        this._requests = 1;
        this._maxIdleMs = ((this._manager._dftMaxIdleSecs > 0) ? (this._manager._dftMaxIdleSecs * 1000L) : -1L);
        if (AbstractSession.LOG.isDebugEnabled()) {
            AbstractSession.LOG.debug("Restored session " + this._nodeId + " " + this._clusterId, new Object[0]);
        }
    }
    
    protected void checkValid() throws IllegalStateException {
        if (this._invalid) {
            throw new IllegalStateException("id=" + this._clusterId + " created=" + this._created + " accessed=" + this._accessed + " lastaccessed=" + this._lastAccessed + " maxInactiveMs=" + this._maxIdleMs);
        }
    }
    
    protected boolean checkExpiry(final long time) {
        return this._maxIdleMs > 0L && this._lastAccessed > 0L && this._lastAccessed + this._maxIdleMs < time;
    }
    
    @Override
    public AbstractSession getSession() {
        return this;
    }
    
    public long getAccessed() {
        synchronized (this) {
            return this._accessed;
        }
    }
    
    public abstract Map<String, Object> getAttributeMap();
    
    public abstract int getAttributes();
    
    public abstract Set<String> getNames();
    
    public long getCookieSetTime() {
        return this._cookieSet;
    }
    
    public void setCookieSetTime(final long time) {
        this._cookieSet = time;
    }
    
    @Override
    public long getCreationTime() throws IllegalStateException {
        this.checkValid();
        return this._created;
    }
    
    @Override
    public String getId() throws IllegalStateException {
        return this._manager._nodeIdInSessionId ? this._nodeId : this._clusterId;
    }
    
    public String getNodeId() {
        return this._nodeId;
    }
    
    public String getClusterId() {
        return this._clusterId;
    }
    
    @Override
    public long getLastAccessedTime() throws IllegalStateException {
        this.checkValid();
        return this._lastAccessed;
    }
    
    public void setLastAccessedTime(final long time) {
        this._lastAccessed = time;
    }
    
    @Override
    public int getMaxInactiveInterval() {
        return (int)(this._maxIdleMs / 1000L);
    }
    
    @Override
    public ServletContext getServletContext() {
        return this._manager._context;
    }
    
    @Deprecated
    @Override
    public HttpSessionContext getSessionContext() throws IllegalStateException {
        this.checkValid();
        return AbstractSessionManager.__nullSessionContext;
    }
    
    @Deprecated
    @Override
    public Object getValue(final String name) throws IllegalStateException {
        return this.getAttribute(name);
    }
    
    public void renewId(final HttpServletRequest request) {
        this._manager._sessionIdManager.renewSessionId(this.getClusterId(), this.getNodeId(), request);
        this.setIdChanged(true);
    }
    
    public SessionManager getSessionManager() {
        return this._manager;
    }
    
    protected void setClusterId(final String clusterId) {
        this._clusterId = clusterId;
    }
    
    protected void setNodeId(final String nodeId) {
        this._nodeId = nodeId;
    }
    
    protected boolean access(final long time) {
        synchronized (this) {
            if (this._invalid) {
                return false;
            }
            this._newSession = false;
            this._lastAccessed = this._accessed;
            this._accessed = time;
            if (this.checkExpiry(time)) {
                this.invalidate();
                return false;
            }
            ++this._requests;
            return true;
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
        this._manager.removeSession(this, true);
        boolean do_invalidate = false;
        synchronized (this) {
            if (!this._invalid) {
                if (this._requests <= 0) {
                    do_invalidate = true;
                }
                else {
                    this._doInvalidate = true;
                }
            }
        }
        if (do_invalidate) {
            this.doInvalidate();
        }
    }
    
    @Override
    public void invalidate() throws IllegalStateException {
        this.checkValid();
        this._manager.removeSession(this, true);
        this.doInvalidate();
    }
    
    protected void doInvalidate() throws IllegalStateException {
        try {
            if (AbstractSession.LOG.isDebugEnabled()) {
                AbstractSession.LOG.debug("invalidate {}", this._clusterId);
            }
            if (this.isValid()) {
                this.clearAttributes();
            }
        }
        finally {
            synchronized (this) {
                this._invalid = true;
            }
        }
    }
    
    public abstract void clearAttributes();
    
    public boolean isIdChanged() {
        return this._idChanged;
    }
    
    @Override
    public boolean isNew() throws IllegalStateException {
        this.checkValid();
        return this._newSession;
    }
    
    @Deprecated
    @Override
    public void putValue(final String name, final Object value) throws IllegalStateException {
        this.changeAttribute(name, value);
    }
    
    @Override
    public void removeAttribute(final String name) {
        this.setAttribute(name, null);
    }
    
    @Deprecated
    @Override
    public void removeValue(final String name) throws IllegalStateException {
        this.removeAttribute(name);
    }
    
    @Override
    public Enumeration<String> getAttributeNames() {
        synchronized (this) {
            this.checkValid();
            return this.doGetAttributeNames();
        }
    }
    
    @Deprecated
    @Override
    public String[] getValueNames() throws IllegalStateException {
        synchronized (this) {
            this.checkValid();
            final Enumeration<String> anames = this.doGetAttributeNames();
            if (anames == null) {
                return new String[0];
            }
            final ArrayList<String> names = new ArrayList<String>();
            while (anames.hasMoreElements()) {
                names.add(anames.nextElement());
            }
            return names.toArray(new String[names.size()]);
        }
    }
    
    public abstract Object doPutOrRemove(final String p0, final Object p1);
    
    public abstract Object doGet(final String p0);
    
    public abstract Enumeration<String> doGetAttributeNames();
    
    @Override
    public Object getAttribute(final String name) {
        synchronized (this) {
            this.checkValid();
            return this.doGet(name);
        }
    }
    
    @Override
    public void setAttribute(final String name, final Object value) {
        this.changeAttribute(name, value);
    }
    
    @Deprecated
    protected boolean updateAttribute(final String name, final Object value) {
        Object old = null;
        synchronized (this) {
            this.checkValid();
            old = this.doPutOrRemove(name, value);
        }
        if (value == null || !value.equals(old)) {
            if (old != null) {
                this.unbindValue(name, old);
            }
            if (value != null) {
                this.bindValue(name, value);
            }
            this._manager.doSessionAttributeListeners(this, name, old, value);
            return true;
        }
        return false;
    }
    
    protected Object changeAttribute(final String name, final Object value) {
        Object old = null;
        synchronized (this) {
            this.checkValid();
            old = this.doPutOrRemove(name, value);
        }
        this.callSessionAttributeListeners(name, value, old);
        return old;
    }
    
    protected void callSessionAttributeListeners(final String name, final Object newValue, final Object oldValue) {
        if (newValue == null || !newValue.equals(oldValue)) {
            if (oldValue != null) {
                this.unbindValue(name, oldValue);
            }
            if (newValue != null) {
                this.bindValue(name, newValue);
            }
            this._manager.doSessionAttributeListeners(this, name, oldValue, newValue);
        }
    }
    
    public void setIdChanged(final boolean changed) {
        this._idChanged = changed;
    }
    
    @Override
    public void setMaxInactiveInterval(final int secs) {
        if (AbstractSession.LOG.isDebugEnabled()) {
            if (secs <= 0) {
                AbstractSession.LOG.debug("Session {} is now immortal (maxInactiveInterval={})", this._clusterId, secs);
            }
            else {
                AbstractSession.LOG.debug("Session {} maxInactiveInterval={}", this._clusterId, secs);
            }
        }
        this._maxIdleMs = secs * 1000L;
    }
    
    @Override
    public String toString() {
        return this.getClass().getName() + ":" + this.getId() + "@" + this.hashCode();
    }
    
    public void bindValue(final String name, final Object value) {
        if (value != null && value instanceof HttpSessionBindingListener) {
            ((HttpSessionBindingListener)value).valueBound(new HttpSessionBindingEvent(this, name));
        }
    }
    
    public boolean isValid() {
        return !this._invalid;
    }
    
    protected void cookieSet() {
        synchronized (this) {
            this._cookieSet = this._accessed;
        }
    }
    
    public int getRequests() {
        synchronized (this) {
            return this._requests;
        }
    }
    
    public void setRequests(final int requests) {
        synchronized (this) {
            this._requests = requests;
        }
    }
    
    public void unbindValue(final String name, final Object value) {
        if (value != null && value instanceof HttpSessionBindingListener) {
            ((HttpSessionBindingListener)value).valueUnbound(new HttpSessionBindingEvent(this, name));
        }
    }
    
    public void willPassivate() {
        synchronized (this) {
            final HttpSessionEvent event = new HttpSessionEvent(this);
            for (final Object value : this.getAttributeMap().values()) {
                if (value instanceof HttpSessionActivationListener) {
                    final HttpSessionActivationListener listener = (HttpSessionActivationListener)value;
                    listener.sessionWillPassivate(event);
                }
            }
        }
    }
    
    public void didActivate() {
        synchronized (this) {
            final HttpSessionEvent event = new HttpSessionEvent(this);
            for (final Object value : this.getAttributeMap().values()) {
                if (value instanceof HttpSessionActivationListener) {
                    final HttpSessionActivationListener listener = (HttpSessionActivationListener)value;
                    listener.sessionDidActivate(event);
                }
            }
        }
    }
    
    static {
        LOG = SessionHandler.LOG;
    }
}
