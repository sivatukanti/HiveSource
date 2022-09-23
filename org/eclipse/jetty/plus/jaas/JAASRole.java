// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.plus.jaas;

public class JAASRole extends JAASPrincipal
{
    private static final long serialVersionUID = 3465114254970134526L;
    
    public JAASRole(final String name) {
        super(name);
    }
    
    @Override
    public boolean equals(final Object o) {
        return o instanceof JAASRole && this.getName().equals(((JAASRole)o).getName());
    }
}
