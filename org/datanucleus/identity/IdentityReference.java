// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.identity;

import java.io.Serializable;

public class IdentityReference implements Serializable
{
    private static final long serialVersionUID = 2472281096825989665L;
    protected Object client;
    
    public IdentityReference(final Object client) {
        this.client = client;
    }
    
    @Override
    public int hashCode() {
        return System.identityHashCode(this.client);
    }
    
    @Override
    public boolean equals(final Object o) {
        return o instanceof IdentityReference && this.equals((IdentityReference)o);
    }
    
    public boolean equals(final IdentityReference o) {
        return this.client == o.client;
    }
}
