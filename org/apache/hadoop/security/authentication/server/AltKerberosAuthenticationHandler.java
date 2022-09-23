// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.security.authentication.server;

import org.apache.hadoop.security.authentication.client.AuthenticationException;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletException;
import java.util.Locale;
import java.util.Properties;

public abstract class AltKerberosAuthenticationHandler extends KerberosAuthenticationHandler
{
    public static final String TYPE = "alt-kerberos";
    public static final String NON_BROWSER_USER_AGENTS = "alt-kerberos.non-browser.user-agents";
    private static final String NON_BROWSER_USER_AGENTS_DEFAULT = "java,curl,wget,perl";
    private String[] nonBrowserUserAgents;
    
    @Override
    public String getType() {
        return "alt-kerberos";
    }
    
    @Override
    public void init(final Properties config) throws ServletException {
        super.init(config);
        this.nonBrowserUserAgents = config.getProperty("alt-kerberos.non-browser.user-agents", "java,curl,wget,perl").split("\\W*,\\W*");
        for (int i = 0; i < this.nonBrowserUserAgents.length; ++i) {
            this.nonBrowserUserAgents[i] = this.nonBrowserUserAgents[i].toLowerCase(Locale.ENGLISH);
        }
    }
    
    @Override
    public AuthenticationToken authenticate(final HttpServletRequest request, final HttpServletResponse response) throws IOException, AuthenticationException {
        AuthenticationToken token;
        if (this.isBrowser(request.getHeader("User-Agent"))) {
            token = this.alternateAuthenticate(request, response);
        }
        else {
            token = super.authenticate(request, response);
        }
        return token;
    }
    
    protected boolean isBrowser(String userAgent) {
        if (userAgent == null) {
            return false;
        }
        userAgent = userAgent.toLowerCase(Locale.ENGLISH);
        boolean isBrowser = true;
        for (final String nonBrowserUserAgent : this.nonBrowserUserAgents) {
            if (userAgent.contains(nonBrowserUserAgent)) {
                isBrowser = false;
                break;
            }
        }
        return isBrowser;
    }
    
    public abstract AuthenticationToken alternateAuthenticate(final HttpServletRequest p0, final HttpServletResponse p1) throws IOException, AuthenticationException;
}
