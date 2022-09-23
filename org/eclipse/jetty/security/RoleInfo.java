// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.security;

import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.Set;

public class RoleInfo
{
    private boolean _isAnyAuth;
    private boolean _isAnyRole;
    private boolean _checked;
    private boolean _forbidden;
    private UserDataConstraint _userDataConstraint;
    private final Set<String> _roles;
    
    public RoleInfo() {
        this._roles = new CopyOnWriteArraySet<String>();
    }
    
    public boolean isChecked() {
        return this._checked;
    }
    
    public void setChecked(final boolean checked) {
        if (!(this._checked = checked)) {
            this._forbidden = false;
            this._roles.clear();
            this._isAnyRole = false;
            this._isAnyAuth = false;
        }
    }
    
    public boolean isForbidden() {
        return this._forbidden;
    }
    
    public void setForbidden(final boolean forbidden) {
        this._forbidden = forbidden;
        if (forbidden) {
            this._checked = true;
            this._userDataConstraint = null;
            this._isAnyRole = false;
            this._isAnyAuth = false;
            this._roles.clear();
        }
    }
    
    public boolean isAnyRole() {
        return this._isAnyRole;
    }
    
    public void setAnyRole(final boolean anyRole) {
        this._isAnyRole = anyRole;
        if (anyRole) {
            this._checked = true;
        }
    }
    
    public boolean isAnyAuth() {
        return this._isAnyAuth;
    }
    
    public void setAnyAuth(final boolean anyAuth) {
        this._isAnyAuth = anyAuth;
        if (anyAuth) {
            this._checked = true;
        }
    }
    
    public UserDataConstraint getUserDataConstraint() {
        return this._userDataConstraint;
    }
    
    public void setUserDataConstraint(final UserDataConstraint userDataConstraint) {
        if (userDataConstraint == null) {
            throw new NullPointerException("Null UserDataConstraint");
        }
        if (this._userDataConstraint == null) {
            this._userDataConstraint = userDataConstraint;
        }
        else {
            this._userDataConstraint = this._userDataConstraint.combine(userDataConstraint);
        }
    }
    
    public Set<String> getRoles() {
        return this._roles;
    }
    
    public void addRole(final String role) {
        this._roles.add(role);
    }
    
    public void combine(final RoleInfo other) {
        if (other._forbidden) {
            this.setForbidden(true);
        }
        else if (!other._checked) {
            this.setChecked(true);
        }
        else if (other._isAnyRole) {
            this.setAnyRole(true);
        }
        else if (other._isAnyAuth) {
            this.setAnyAuth(true);
        }
        else if (!this._isAnyRole) {
            for (final String r : other._roles) {
                this._roles.add(r);
            }
        }
        this.setUserDataConstraint(other._userDataConstraint);
    }
    
    @Override
    public String toString() {
        return "{RoleInfo" + (this._forbidden ? ",F" : "") + (this._checked ? ",C" : "") + (this._isAnyRole ? ",*" : this._roles) + ((this._userDataConstraint != null) ? ("," + this._userDataConstraint) : "") + "}";
    }
}
