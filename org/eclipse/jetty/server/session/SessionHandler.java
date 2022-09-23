// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.server.session;

import javax.servlet.http.HttpSessionListener;
import javax.servlet.http.HttpSessionIdListener;
import javax.servlet.http.HttpSessionAttributeListener;
import org.eclipse.jetty.util.log.Log;
import javax.servlet.http.Cookie;
import javax.servlet.ServletException;
import java.io.IOException;
import org.eclipse.jetty.http.HttpCookie;
import javax.servlet.DispatcherType;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.SessionManager;
import java.util.EventListener;
import javax.servlet.SessionTrackingMode;
import java.util.EnumSet;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.server.handler.ScopedHandler;

public class SessionHandler extends ScopedHandler
{
    static final Logger LOG;
    public static final EnumSet<SessionTrackingMode> DEFAULT_TRACKING;
    public static final Class<? extends EventListener>[] SESSION_LISTENER_TYPES;
    private SessionManager _sessionManager;
    
    public SessionHandler() {
        this(new HashSessionManager());
    }
    
    public SessionHandler(final SessionManager manager) {
        this.setSessionManager(manager);
    }
    
    public SessionManager getSessionManager() {
        return this._sessionManager;
    }
    
    public void setSessionManager(final SessionManager sessionManager) {
        if (this.isStarted()) {
            throw new IllegalStateException();
        }
        if (sessionManager != null) {
            sessionManager.setSessionHandler(this);
        }
        this.updateBean(this._sessionManager, sessionManager);
        this._sessionManager = sessionManager;
    }
    
    @Override
    protected void doStart() throws Exception {
        if (this._sessionManager == null) {
            this.setSessionManager(new HashSessionManager());
        }
        super.doStart();
    }
    
    @Override
    protected void doStop() throws Exception {
        super.doStop();
    }
    
    @Override
    public void doScope(final String target, final Request baseRequest, final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
        SessionManager old_session_manager = null;
        HttpSession old_session = null;
        HttpSession access = null;
        try {
            old_session_manager = baseRequest.getSessionManager();
            old_session = baseRequest.getSession(false);
            if (old_session_manager != this._sessionManager) {
                baseRequest.setSessionManager(this._sessionManager);
                baseRequest.setSession(null);
                this.checkRequestedSessionId(baseRequest, request);
            }
            HttpSession session = null;
            if (this._sessionManager != null) {
                session = baseRequest.getSession(false);
                if (session != null) {
                    if (session != old_session && (request.getDispatcherType() == DispatcherType.ASYNC || request.getDispatcherType() == DispatcherType.REQUEST)) {
                        access = session;
                        final HttpCookie cookie = this._sessionManager.access(session, request.isSecure());
                        if (cookie != null) {
                            baseRequest.getResponse().addCookie(cookie);
                        }
                    }
                }
                else {
                    session = baseRequest.recoverNewSession(this._sessionManager);
                    if (session != null) {
                        baseRequest.setSession(session);
                    }
                }
            }
            if (SessionHandler.LOG.isDebugEnabled()) {
                SessionHandler.LOG.debug("sessionManager=" + this._sessionManager, new Object[0]);
                SessionHandler.LOG.debug("session=" + session, new Object[0]);
            }
            if (this._nextScope != null) {
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
            if (access != null) {
                this._sessionManager.complete(access);
            }
            final HttpSession session2 = baseRequest.getSession(false);
            if (session2 != null && old_session == null && session2 != access && (request.getDispatcherType() == DispatcherType.ASYNC || request.getDispatcherType() == DispatcherType.REQUEST)) {
                this._sessionManager.complete(session2);
            }
            if (old_session_manager != null && old_session_manager != this._sessionManager) {
                baseRequest.setSessionManager(old_session_manager);
                baseRequest.setSession(old_session);
            }
        }
    }
    
    @Override
    public void doHandle(final String target, final Request baseRequest, final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
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
    
    protected void checkRequestedSessionId(final Request baseRequest, final HttpServletRequest request) {
        String requested_session_id = request.getRequestedSessionId();
        final SessionManager sessionManager = this.getSessionManager();
        if (requested_session_id != null && sessionManager != null) {
            final HttpSession session = sessionManager.getHttpSession(requested_session_id);
            if (session != null && sessionManager.isValid(session)) {
                baseRequest.setSession(session);
            }
            return;
        }
        if (!DispatcherType.REQUEST.equals(baseRequest.getDispatcherType())) {
            return;
        }
        boolean requested_session_id_from_cookie = false;
        HttpSession session2 = null;
        if (this._sessionManager.isUsingCookies()) {
            final Cookie[] cookies = request.getCookies();
            if (cookies != null && cookies.length > 0) {
                final String sessionCookie = sessionManager.getSessionCookieConfig().getName();
                for (int i = 0; i < cookies.length; ++i) {
                    if (sessionCookie.equalsIgnoreCase(cookies[i].getName())) {
                        requested_session_id = cookies[i].getValue();
                        requested_session_id_from_cookie = true;
                        if (SessionHandler.LOG.isDebugEnabled()) {
                            SessionHandler.LOG.debug("Got Session ID {} from cookie", requested_session_id);
                        }
                        if (requested_session_id != null) {
                            session2 = sessionManager.getHttpSession(requested_session_id);
                            if (session2 != null && sessionManager.isValid(session2)) {
                                break;
                            }
                        }
                        else {
                            SessionHandler.LOG.warn("null session id from cookie", new Object[0]);
                        }
                    }
                }
            }
        }
        if (requested_session_id == null || session2 == null) {
            final String uri = request.getRequestURI();
            final String prefix = sessionManager.getSessionIdPathParameterNamePrefix();
            if (prefix != null) {
                int s = uri.indexOf(prefix);
                if (s >= 0) {
                    int j;
                    for (s = (j = s + prefix.length()); j < uri.length(); ++j) {
                        final char c = uri.charAt(j);
                        if (c == ';' || c == '#' || c == '?') {
                            break;
                        }
                        if (c == '/') {
                            break;
                        }
                    }
                    requested_session_id = uri.substring(s, j);
                    requested_session_id_from_cookie = false;
                    session2 = sessionManager.getHttpSession(requested_session_id);
                    if (SessionHandler.LOG.isDebugEnabled()) {
                        SessionHandler.LOG.debug("Got Session ID {} from URL", requested_session_id);
                    }
                }
            }
        }
        baseRequest.setRequestedSessionId(requested_session_id);
        baseRequest.setRequestedSessionIdFromCookie(requested_session_id != null && requested_session_id_from_cookie);
        if (session2 != null && sessionManager.isValid(session2)) {
            baseRequest.setSession(session2);
        }
    }
    
    public void addEventListener(final EventListener listener) {
        if (this._sessionManager != null) {
            this._sessionManager.addEventListener(listener);
        }
    }
    
    public void removeEventListener(final EventListener listener) {
        if (this._sessionManager != null) {
            this._sessionManager.removeEventListener(listener);
        }
    }
    
    public void clearEventListeners() {
        if (this._sessionManager != null) {
            this._sessionManager.clearEventListeners();
        }
    }
    
    static {
        LOG = Log.getLogger("org.eclipse.jetty.server.session");
        DEFAULT_TRACKING = EnumSet.of(SessionTrackingMode.COOKIE, SessionTrackingMode.URL);
        SESSION_LISTENER_TYPES = new Class[] { HttpSessionAttributeListener.class, HttpSessionIdListener.class, HttpSessionListener.class };
    }
}
