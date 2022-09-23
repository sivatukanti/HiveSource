// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.jetty.security;

import java.security.Principal;
import org.mortbay.jetty.Response;
import org.mortbay.jetty.Request;

public interface SSORealm
{
    Credential getSingleSignOn(final Request p0, final Response p1);
    
    void setSingleSignOn(final Request p0, final Response p1, final Principal p2, final Credential p3);
    
    void clearSingleSignOn(final String p0);
}
