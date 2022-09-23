// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.jetty;

import javax.servlet.http.Cookie;
import java.util.EventListener;
import org.mortbay.jetty.servlet.SessionHandler;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.mortbay.component.LifeCycle;

public interface SessionManager extends LifeCycle
{
    public static final String __SessionCookieProperty = "org.mortbay.jetty.servlet.SessionCookie";
    public static final String __DefaultSessionCookie = "JSESSIONID";
    public static final String __SessionURLProperty = "org.mortbay.jetty.servlet.SessionURL";
    public static final String __DefaultSessionURL = "jsessionid";
    public static final String __SessionDomainProperty = "org.mortbay.jetty.servlet.SessionDomain";
    public static final String __DefaultSessionDomain = null;
    public static final String __SessionPathProperty = "org.mortbay.jetty.servlet.SessionPath";
    public static final String __MaxAgeProperty = "org.mortbay.jetty.servlet.MaxAge";
    
    HttpSession getHttpSession(final String p0);
    
    HttpSession newHttpSession(final HttpServletRequest p0);
    
    boolean getSecureCookies();
    
    boolean getHttpOnly();
    
    int getMaxInactiveInterval();
    
    void setMaxInactiveInterval(final int p0);
    
    void setSessionHandler(final SessionHandler p0);
    
    void addEventListener(final EventListener p0);
    
    void removeEventListener(final EventListener p0);
    
    void clearEventListeners();
    
    Cookie getSessionCookie(final HttpSession p0, final String p1, final boolean p2);
    
    SessionIdManager getIdManager();
    
    SessionIdManager getMetaManager();
    
    void setIdManager(final SessionIdManager p0);
    
    boolean isValid(final HttpSession p0);
    
    String getNodeId(final HttpSession p0);
    
    String getClusterId(final HttpSession p0);
    
    Cookie access(final HttpSession p0, final boolean p1);
    
    void complete(final HttpSession p0);
    
    void setSessionCookie(final String p0);
    
    String getSessionCookie();
    
    void setSessionURL(final String p0);
    
    String getSessionURL();
    
    String getSessionURLPrefix();
    
    void setSessionDomain(final String p0);
    
    String getSessionDomain();
    
    void setSessionPath(final String p0);
    
    String getSessionPath();
    
    void setMaxCookieAge(final int p0);
    
    int getMaxCookieAge();
    
    boolean isUsingCookies();
}
