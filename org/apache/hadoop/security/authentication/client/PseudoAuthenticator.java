// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.security.authentication.client;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class PseudoAuthenticator implements Authenticator
{
    public static final String USER_NAME = "user.name";
    private static final String USER_NAME_EQ = "user.name=";
    private ConnectionConfigurator connConfigurator;
    
    @Override
    public void setConnectionConfigurator(final ConnectionConfigurator configurator) {
        this.connConfigurator = configurator;
    }
    
    @Override
    public void authenticate(URL url, final AuthenticatedURL.Token token) throws IOException, AuthenticationException {
        String strUrl = url.toString();
        final String paramSeparator = strUrl.contains("?") ? "&" : "?";
        strUrl = strUrl + paramSeparator + "user.name=" + this.getUserName();
        url = new URL(strUrl);
        final HttpURLConnection conn = token.openConnection(url, this.connConfigurator);
        conn.setRequestMethod("OPTIONS");
        conn.connect();
        AuthenticatedURL.extractToken(conn, token);
    }
    
    protected String getUserName() {
        return System.getProperty("user.name");
    }
}
