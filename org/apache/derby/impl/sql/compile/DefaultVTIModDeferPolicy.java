// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.vti.DeferModification;

class DefaultVTIModDeferPolicy implements DeferModification
{
    private final String targetVTIClassName;
    private final boolean VTIResultSetIsSensitive;
    
    DefaultVTIModDeferPolicy(final String targetVTIClassName, final boolean vtiResultSetIsSensitive) {
        this.targetVTIClassName = targetVTIClassName;
        this.VTIResultSetIsSensitive = vtiResultSetIsSensitive;
    }
    
    public boolean alwaysDefer(final int n) {
        return false;
    }
    
    public boolean columnRequiresDefer(final int n, final String s, final boolean b) {
        switch (n) {
            case 1: {
                return false;
            }
            case 2: {
                return this.VTIResultSetIsSensitive && b;
            }
            case 3: {
                return false;
            }
            default: {
                return false;
            }
        }
    }
    
    public boolean subselectRequiresDefer(final int n, final String s, final String s2) {
        return false;
    }
    
    public boolean subselectRequiresDefer(final int n, final String anObject) {
        return this.targetVTIClassName.equals(anObject);
    }
    
    public void modificationNotify(final int n, final boolean b) {
    }
}
