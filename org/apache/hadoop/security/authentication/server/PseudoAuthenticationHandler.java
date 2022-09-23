// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.security.authentication.server;

import java.util.Iterator;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.hadoop.security.authentication.client.AuthenticationException;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletException;
import java.util.Properties;
import java.nio.charset.Charset;

public class PseudoAuthenticationHandler implements AuthenticationHandler
{
    public static final String TYPE = "simple";
    public static final String ANONYMOUS_ALLOWED = "simple.anonymous.allowed";
    private static final Charset UTF8_CHARSET;
    private static final String PSEUDO_AUTH = "PseudoAuth";
    private boolean acceptAnonymous;
    private String type;
    
    public PseudoAuthenticationHandler() {
        this("simple");
    }
    
    public PseudoAuthenticationHandler(final String type) {
        this.type = type;
    }
    
    @Override
    public void init(final Properties config) throws ServletException {
        this.acceptAnonymous = Boolean.parseBoolean(config.getProperty("simple.anonymous.allowed", "false"));
    }
    
    protected boolean getAcceptAnonymous() {
        return this.acceptAnonymous;
    }
    
    @Override
    public void destroy() {
    }
    
    @Override
    public String getType() {
        return this.type;
    }
    
    @Override
    public boolean managementOperation(final AuthenticationToken token, final HttpServletRequest request, final HttpServletResponse response) throws IOException, AuthenticationException {
        return true;
    }
    
    private String getUserName(final HttpServletRequest request) {
        final String queryString = request.getQueryString();
        if (queryString == null || queryString.length() == 0) {
            return null;
        }
        final List<NameValuePair> list = URLEncodedUtils.parse(queryString, PseudoAuthenticationHandler.UTF8_CHARSET);
        if (list != null) {
            for (final NameValuePair nv : list) {
                if ("user.name".equals(nv.getName())) {
                    return nv.getValue();
                }
            }
        }
        return null;
    }
    
    @Override
    public AuthenticationToken authenticate(final HttpServletRequest request, final HttpServletResponse response) throws IOException, AuthenticationException {
        final String userName = this.getUserName(request);
        AuthenticationToken token;
        if (userName == null) {
            if (this.getAcceptAnonymous()) {
                token = AuthenticationToken.ANONYMOUS;
            }
            else {
                response.setStatus(403);
                response.setHeader("WWW-Authenticate", "PseudoAuth");
                token = null;
            }
        }
        else {
            token = new AuthenticationToken(userName, userName, this.getType());
        }
        return token;
    }
    
    static {
        UTF8_CHARSET = Charset.forName("UTF-8");
    }
}
