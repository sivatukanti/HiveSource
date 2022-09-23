// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.security.authentication.server;

import org.apache.hadoop.security.authentication.client.AuthenticationException;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletException;
import java.util.Properties;

public interface AuthenticationHandler
{
    public static final String WWW_AUTHENTICATE = "WWW-Authenticate";
    
    String getType();
    
    void init(final Properties p0) throws ServletException;
    
    void destroy();
    
    boolean managementOperation(final AuthenticationToken p0, final HttpServletRequest p1, final HttpServletResponse p2) throws IOException, AuthenticationException;
    
    AuthenticationToken authenticate(final HttpServletRequest p0, final HttpServletResponse p1) throws IOException, AuthenticationException;
}
