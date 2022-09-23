// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.jetty.servlet;

import java.util.EventListener;
import javax.servlet.ServletException;
import java.io.IOException;
import javax.servlet.http.Cookie;
import org.mortbay.jetty.RetryRequest;
import org.mortbay.log.Log;
import javax.servlet.http.HttpSession;
import org.mortbay.jetty.HttpConnection;
import org.mortbay.jetty.Request;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.SessionManager;
import org.mortbay.jetty.handler.HandlerWrapper;

public class SessionHandler extends HandlerWrapper
{
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
        final SessionManager old_session_manager = this._sessionManager;
        if (this.getServer() != null) {
            this.getServer().getContainer().update(this, old_session_manager, sessionManager, "sessionManager", true);
        }
        if (sessionManager != null) {
            sessionManager.setSessionHandler(this);
        }
        this._sessionManager = sessionManager;
        if (old_session_manager != null) {
            old_session_manager.setSessionHandler(null);
        }
    }
    
    public void setServer(final Server server) {
        final Server old_server = this.getServer();
        if (old_server != null && old_server != server) {
            old_server.getContainer().update(this, this._sessionManager, null, "sessionManager", true);
        }
        super.setServer(server);
        if (server != null && server != old_server) {
            server.getContainer().update(this, null, this._sessionManager, "sessionManager", true);
        }
    }
    
    protected void doStart() throws Exception {
        this._sessionManager.start();
        super.doStart();
    }
    
    protected void doStop() throws Exception {
        super.doStop();
        this._sessionManager.stop();
    }
    
    public void handle(final String target, final HttpServletRequest request, final HttpServletResponse response, final int dispatch) throws IOException, ServletException {
        this.setRequestedId(request, dispatch);
        final Request base_request = (Request)((request instanceof Request) ? request : HttpConnection.getCurrentConnection().getRequest());
        SessionManager old_session_manager = null;
        HttpSession old_session = null;
        try {
            old_session_manager = base_request.getSessionManager();
            old_session = base_request.getSession(false);
            if (old_session_manager != this._sessionManager) {
                base_request.setSessionManager(this._sessionManager);
                base_request.setSession(null);
            }
            HttpSession session = null;
            if (this._sessionManager != null) {
                session = base_request.getSession(false);
                if (session != null) {
                    if (session != old_session) {
                        final Cookie cookie = this._sessionManager.access(session, request.isSecure());
                        if (cookie != null) {
                            response.addCookie(cookie);
                        }
                    }
                }
                else {
                    session = base_request.recoverNewSession(this._sessionManager);
                    if (session != null) {
                        base_request.setSession(session);
                    }
                }
            }
            if (Log.isDebugEnabled()) {
                Log.debug("sessionManager=" + this._sessionManager);
                Log.debug("session=" + session);
            }
            this.getHandler().handle(target, request, response, dispatch);
        }
        catch (RetryRequest r) {
            final HttpSession session2 = base_request.getSession(false);
            if (session2 != null && session2.isNew()) {
                base_request.saveNewSession(this._sessionManager, session2);
            }
            throw r;
        }
        finally {
            final HttpSession session3 = request.getSession(false);
            if (old_session_manager != this._sessionManager) {
                if (session3 != null) {
                    this._sessionManager.complete(session3);
                }
                if (old_session_manager != null) {
                    base_request.setSessionManager(old_session_manager);
                    base_request.setSession(old_session);
                }
            }
        }
    }
    
    protected void setRequestedId(final HttpServletRequest request, final int dispatch) {
        final Request base_request = (Request)((request instanceof Request) ? request : HttpConnection.getCurrentConnection().getRequest());
        String requested_session_id = request.getRequestedSessionId();
        if (dispatch != 1 || requested_session_id != null) {
            return;
        }
        final SessionManager sessionManager = this.getSessionManager();
        boolean requested_session_id_from_cookie = false;
        HttpSession session = null;
        if (this._sessionManager.isUsingCookies()) {
            final Cookie[] cookies = request.getCookies();
            if (cookies != null && cookies.length > 0) {
                for (int i = 0; i < cookies.length; ++i) {
                    if (sessionManager.getSessionCookie().equalsIgnoreCase(cookies[i].getName())) {
                        if (requested_session_id != null && sessionManager.getHttpSession(requested_session_id) != null) {
                            break;
                        }
                        requested_session_id = cookies[i].getValue();
                        requested_session_id_from_cookie = true;
                        if (Log.isDebugEnabled()) {
                            Log.debug("Got Session ID " + requested_session_id + " from cookie");
                        }
                        session = sessionManager.getHttpSession(requested_session_id);
                        if (session != null) {
                            base_request.setSession(session);
                        }
                    }
                }
            }
        }
        if (requested_session_id == null || session == null) {
            final String uri = request.getRequestURI();
            final String prefix = sessionManager.getSessionURLPrefix();
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
                    if (Log.isDebugEnabled()) {
                        Log.debug("Got Session ID " + requested_session_id + " from URL");
                    }
                }
            }
        }
        base_request.setRequestedSessionId(requested_session_id);
        base_request.setRequestedSessionIdFromCookie(requested_session_id != null && requested_session_id_from_cookie);
    }
    
    public void addEventListener(final EventListener listener) {
        if (this._sessionManager != null) {
            this._sessionManager.addEventListener(listener);
        }
    }
    
    public void clearEventListeners() {
        if (this._sessionManager != null) {
            this._sessionManager.clearEventListeners();
        }
    }
}
