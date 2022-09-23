// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.security.jaspi;

import javax.security.auth.message.MessageInfo;
import javax.security.auth.message.AuthException;
import java.util.Map;
import javax.security.auth.Subject;
import javax.security.auth.message.config.ServerAuthContext;
import javax.security.auth.message.config.ServerAuthConfig;

public class SimpleAuthConfig implements ServerAuthConfig
{
    public static final String HTTP_SERVLET = "HttpServlet";
    private final String _appContext;
    private final ServerAuthContext _serverAuthContext;
    
    public SimpleAuthConfig(final String appContext, final ServerAuthContext serverAuthContext) {
        this._appContext = appContext;
        this._serverAuthContext = serverAuthContext;
    }
    
    public ServerAuthContext getAuthContext(final String authContextID, final Subject serviceSubject, final Map properties) throws AuthException {
        return this._serverAuthContext;
    }
    
    public String getAppContext() {
        return this._appContext;
    }
    
    public String getAuthContextID(final MessageInfo messageInfo) throws IllegalArgumentException {
        return null;
    }
    
    public String getMessageLayer() {
        return "HttpServlet";
    }
    
    public boolean isProtected() {
        return true;
    }
    
    public void refresh() {
    }
}
