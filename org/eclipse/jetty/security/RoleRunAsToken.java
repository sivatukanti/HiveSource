// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.security;

public class RoleRunAsToken implements RunAsToken
{
    private final String _runAsRole;
    
    public RoleRunAsToken(final String runAsRole) {
        this._runAsRole = runAsRole;
    }
    
    public String getRunAsRole() {
        return this._runAsRole;
    }
    
    @Override
    public String toString() {
        return "RoleRunAsToken(" + this._runAsRole + ")";
    }
}
