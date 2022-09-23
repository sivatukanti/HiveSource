// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.security.authentication;

import java.security.Principal;
import javax.security.auth.Subject;

public interface LoginCallback
{
    Subject getSubject();
    
    String getUserName();
    
    Object getCredential();
    
    boolean isSuccess();
    
    void setSuccess(final boolean p0);
    
    Principal getUserPrincipal();
    
    void setUserPrincipal(final Principal p0);
    
    String[] getRoles();
    
    void setRoles(final String[] p0);
    
    void clearPassword();
}
