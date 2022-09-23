// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.security;

import java.util.Set;
import org.eclipse.jetty.server.UserIdentity;
import java.io.Serializable;
import org.eclipse.jetty.server.Authentication;

public abstract class AbstractUserAuthentication implements Authentication.User, Serializable
{
    private static final long serialVersionUID = -6290411814232723403L;
    protected String _method;
    protected transient UserIdentity _userIdentity;
    
    public AbstractUserAuthentication(final String method, final UserIdentity userIdentity) {
        this._method = method;
        this._userIdentity = userIdentity;
    }
    
    @Override
    public String getAuthMethod() {
        return this._method;
    }
    
    @Override
    public UserIdentity getUserIdentity() {
        return this._userIdentity;
    }
    
    @Override
    public boolean isUserInRole(final UserIdentity.Scope scope, final String role) {
        String roleToTest = null;
        if (scope != null && scope.getRoleRefMap() != null) {
            roleToTest = scope.getRoleRefMap().get(role);
        }
        if (roleToTest == null) {
            roleToTest = role;
        }
        if ("**".equals(roleToTest.trim())) {
            return !this.declaredRolesContains("**") || this._userIdentity.isUserInRole(role, scope);
        }
        return this._userIdentity.isUserInRole(role, scope);
    }
    
    public boolean declaredRolesContains(final String roleName) {
        final SecurityHandler security = SecurityHandler.getCurrentSecurityHandler();
        if (security == null) {
            return false;
        }
        if (security instanceof ConstraintAware) {
            final Set<String> declaredRoles = ((ConstraintAware)security).getRoles();
            return declaredRoles != null && declaredRoles.contains(roleName);
        }
        return false;
    }
}
