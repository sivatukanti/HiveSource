// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.security.authentication.client;

import java.io.IOException;
import java.net.URL;

public interface Authenticator
{
    void setConnectionConfigurator(final ConnectionConfigurator p0);
    
    void authenticate(final URL p0, final AuthenticatedURL.Token p1) throws IOException, AuthenticationException;
}
