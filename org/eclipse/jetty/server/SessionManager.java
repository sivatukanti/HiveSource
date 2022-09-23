// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.server;

import javax.servlet.SessionCookieConfig;
import javax.servlet.SessionTrackingMode;
import java.util.Set;
import org.eclipse.jetty.http.HttpCookie;
import java.util.EventListener;
import org.eclipse.jetty.server.session.SessionHandler;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.eclipse.jetty.util.component.LifeCycle;

public interface SessionManager extends LifeCycle
{
    public static final String __SessionCookieProperty = "org.eclipse.jetty.servlet.SessionCookie";
    public static final String __DefaultSessionCookie = "JSESSIONID";
    public static final String __SessionIdPathParameterNameProperty = "org.eclipse.jetty.servlet.SessionIdPathParameterName";
    public static final String __DefaultSessionIdPathParameterName = "jsessionid";
    public static final String __CheckRemoteSessionEncoding = "org.eclipse.jetty.servlet.CheckingRemoteSessionIdEncoding";
    public static final String __SessionDomainProperty = "org.eclipse.jetty.servlet.SessionDomain";
    public static final String __DefaultSessionDomain = null;
    public static final String __SessionPathProperty = "org.eclipse.jetty.servlet.SessionPath";
    public static final String __MaxAgeProperty = "org.eclipse.jetty.servlet.MaxAge";
    
    HttpSession getHttpSession(final String p0);
    
    HttpSession newHttpSession(final HttpServletRequest p0);
    
    boolean getHttpOnly();
    
    int getMaxInactiveInterval();
    
    void setMaxInactiveInterval(final int p0);
    
    void setSessionHandler(final SessionHandler p0);
    
    void addEventListener(final EventListener p0);
    
    void removeEventListener(final EventListener p0);
    
    void clearEventListeners();
    
    HttpCookie getSessionCookie(final HttpSession p0, final String p1, final boolean p2);
    
    SessionIdManager getSessionIdManager();
    
    @Deprecated
    SessionIdManager getMetaManager();
    
    void setSessionIdManager(final SessionIdManager p0);
    
    boolean isValid(final HttpSession p0);
    
    String getNodeId(final HttpSession p0);
    
    String getClusterId(final HttpSession p0);
    
    HttpCookie access(final HttpSession p0, final boolean p1);
    
    void complete(final HttpSession p0);
    
    void setSessionIdPathParameterName(final String p0);
    
    String getSessionIdPathParameterName();
    
    String getSessionIdPathParameterNamePrefix();
    
    boolean isUsingCookies();
    
    boolean isUsingURLs();
    
    Set<SessionTrackingMode> getDefaultSessionTrackingModes();
    
    Set<SessionTrackingMode> getEffectiveSessionTrackingModes();
    
    void setSessionTrackingModes(final Set<SessionTrackingMode> p0);
    
    SessionCookieConfig getSessionCookieConfig();
    
    boolean isCheckingRemoteSessionIdEncoding();
    
    void setCheckingRemoteSessionIdEncoding(final boolean p0);
    
    void renewSessionId(final String p0, final String p1, final String p2, final String p3);
}
