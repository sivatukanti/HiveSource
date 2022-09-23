// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.security.authentication;

import org.eclipse.jetty.util.log.Log;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionEvent;
import org.eclipse.jetty.server.Authentication;
import java.io.IOException;
import org.eclipse.jetty.security.LoginService;
import javax.servlet.ServletRequest;
import org.eclipse.jetty.security.SecurityHandler;
import java.io.ObjectInputStream;
import org.eclipse.jetty.server.UserIdentity;
import javax.servlet.http.HttpSession;
import org.eclipse.jetty.util.log.Logger;
import javax.servlet.http.HttpSessionBindingListener;
import javax.servlet.http.HttpSessionActivationListener;
import java.io.Serializable;
import org.eclipse.jetty.security.AbstractUserAuthentication;

public class SessionAuthentication extends AbstractUserAuthentication implements Serializable, HttpSessionActivationListener, HttpSessionBindingListener
{
    private static final Logger LOG;
    private static final long serialVersionUID = -4643200685888258706L;
    public static final String __J_AUTHENTICATED = "org.eclipse.jetty.security.UserIdentity";
    private final String _name;
    private final Object _credentials;
    private transient HttpSession _session;
    
    public SessionAuthentication(final String method, final UserIdentity userIdentity, final Object credentials) {
        super(method, userIdentity);
        this._name = userIdentity.getUserPrincipal().getName();
        this._credentials = credentials;
    }
    
    private void readObject(final ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        final SecurityHandler security = SecurityHandler.getCurrentSecurityHandler();
        if (security == null) {
            throw new IllegalStateException("!SecurityHandler");
        }
        final LoginService login_service = security.getLoginService();
        if (login_service == null) {
            throw new IllegalStateException("!LoginService");
        }
        this._userIdentity = login_service.login(this._name, this._credentials, null);
        SessionAuthentication.LOG.debug("Deserialized and relogged in {}", this);
    }
    
    @Override
    public void logout() {
        if (this._session != null && this._session.getAttribute("org.eclipse.jetty.security.UserIdentity") != null) {
            this._session.removeAttribute("org.eclipse.jetty.security.UserIdentity");
        }
        this.doLogout();
    }
    
    private void doLogout() {
        final SecurityHandler security = SecurityHandler.getCurrentSecurityHandler();
        if (security != null) {
            security.logout(this);
        }
        if (this._session != null) {
            this._session.removeAttribute("org.eclipse.jetty.security.sessionCreatedSecure");
        }
    }
    
    @Override
    public String toString() {
        return String.format("%s@%x{%s,%s}", this.getClass().getSimpleName(), this.hashCode(), (this._session == null) ? "-" : this._session.getId(), this._userIdentity);
    }
    
    @Override
    public void sessionWillPassivate(final HttpSessionEvent se) {
    }
    
    @Override
    public void sessionDidActivate(final HttpSessionEvent se) {
        if (this._session == null) {
            this._session = se.getSession();
        }
    }
    
    @Override
    public void valueBound(final HttpSessionBindingEvent event) {
        if (this._session == null) {
            this._session = event.getSession();
        }
    }
    
    @Override
    public void valueUnbound(final HttpSessionBindingEvent event) {
        this.doLogout();
    }
    
    static {
        LOG = Log.getLogger(SessionAuthentication.class);
    }
}
