// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.security;

import java.util.List;
import java.security.Principal;
import javax.security.auth.Subject;
import org.eclipse.jetty.server.UserIdentity;

public class SpnegoUserIdentity implements UserIdentity
{
    private Subject _subject;
    private Principal _principal;
    private List<String> _roles;
    
    public SpnegoUserIdentity(final Subject subject, final Principal principal, final List<String> roles) {
        this._subject = subject;
        this._principal = principal;
        this._roles = roles;
    }
    
    @Override
    public Subject getSubject() {
        return this._subject;
    }
    
    @Override
    public Principal getUserPrincipal() {
        return this._principal;
    }
    
    @Override
    public boolean isUserInRole(final String role, final Scope scope) {
        return this._roles.contains(role);
    }
}
