// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.authentication;

import java.io.Serializable;
import java.security.Principal;

public final class SystemPrincipal implements Principal, Serializable
{
    static final long serialVersionUID = 925380094921530190L;
    private final String name;
    
    public SystemPrincipal(final String name) {
        if (name == null) {
            throw new NullPointerException("name can't be null");
        }
        if (name.length() == 0) {
            throw new IllegalArgumentException("name can't be empty");
        }
        this.name = name;
    }
    
    public boolean equals(final Object o) {
        return o != null && o instanceof SystemPrincipal && this.name.equals(((SystemPrincipal)o).name);
    }
    
    public String getName() {
        return this.name;
    }
    
    public int hashCode() {
        return this.name.hashCode();
    }
    
    public String toString() {
        return this.getClass().getName() + "(" + this.name + ")";
    }
}
