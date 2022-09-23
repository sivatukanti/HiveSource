// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.jetty.security;

import java.io.IOException;
import java.security.Principal;
import org.mortbay.jetty.Response;
import org.mortbay.jetty.Request;
import java.io.Serializable;

public interface Authenticator extends Serializable
{
    Principal authenticate(final UserRealm p0, final String p1, final Request p2, final Response p3) throws IOException;
    
    String getAuthMethod();
}
