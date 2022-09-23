// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.plus.jaas;

import java.io.Serializable;
import java.security.Principal;

public class JAASPrincipal implements Principal, Serializable
{
    private static final long serialVersionUID = -5538962177019315479L;
    private String _name;
    
    public JAASPrincipal(final String userName) {
        this._name = null;
        this._name = userName;
    }
    
    @Override
    public boolean equals(final Object p) {
        return p instanceof JAASPrincipal && this.getName().equals(((JAASPrincipal)p).getName());
    }
    
    @Override
    public int hashCode() {
        return this.getName().hashCode();
    }
    
    public String getName() {
        return this._name;
    }
    
    @Override
    public String toString() {
        return this.getName();
    }
}
