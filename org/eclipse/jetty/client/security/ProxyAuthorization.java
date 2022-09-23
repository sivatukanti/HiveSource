// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.client.security;

import org.eclipse.jetty.http.HttpHeaders;
import org.eclipse.jetty.client.HttpExchange;
import java.io.IOException;
import org.eclipse.jetty.io.ByteArrayBuffer;
import org.eclipse.jetty.util.B64Code;
import org.eclipse.jetty.io.Buffer;

public class ProxyAuthorization implements Authentication
{
    private Buffer _authorization;
    
    public ProxyAuthorization(final String username, final String password) throws IOException {
        final String authenticationString = "Basic " + B64Code.encode(username + ":" + password, "ISO-8859-1");
        this._authorization = new ByteArrayBuffer(authenticationString);
    }
    
    public void setCredentials(final HttpExchange exchange) throws IOException {
        exchange.setRequestHeader(HttpHeaders.PROXY_AUTHORIZATION_BUFFER, this._authorization);
    }
}
