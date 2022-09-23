// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.security;

import java.security.Principal;
import javax.security.auth.Subject;
import org.eclipse.jetty.server.UserIdentity;

public class DefaultUserIdentity implements UserIdentity
{
    private final Subject _subject;
    private final Principal _userPrincipal;
    private final String[] _roles;
    
    public DefaultUserIdentity(final Subject subject, final Principal userPrincipal, final String[] roles) {
        this._subject = subject;
        this._userPrincipal = userPrincipal;
        this._roles = roles;
    }
    
    @Override
    public Subject getSubject() {
        return this._subject;
    }
    
    @Override
    public Principal getUserPrincipal() {
        return this._userPrincipal;
    }
    
    @Override
    public boolean isUserInRole(final String role, final Scope scope) {
        if ("*".equals(role)) {
            return false;
        }
        String roleToTest = null;
        if (scope != null && scope.getRoleRefMap() != null) {
            roleToTest = scope.getRoleRefMap().get(role);
        }
        if (roleToTest == null) {
            roleToTest = role;
        }
        for (final String r : this._roles) {
            if (r.equals(roleToTest)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public String toString() {
        return DefaultUserIdentity.class.getSimpleName() + "('" + this._userPrincipal + "')";
    }
}
