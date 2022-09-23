// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.jetty.security;

import org.mortbay.jetty.Request;
import java.security.Principal;

public interface UserRealm
{
    String getName();
    
    Principal getPrincipal(final String p0);
    
    Principal authenticate(final String p0, final Object p1, final Request p2);
    
    boolean reauthenticate(final Principal p0);
    
    boolean isUserInRole(final Principal p0, final String p1);
    
    void disassociate(final Principal p0);
    
    Principal pushRole(final Principal p0, final String p1);
    
    Principal popRole(final Principal p0);
    
    void logout(final Principal p0);
}
