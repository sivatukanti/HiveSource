// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.security;

public enum UserDataConstraint
{
    None, 
    Integral, 
    Confidential;
    
    public static UserDataConstraint get(final int dataConstraint) {
        if (dataConstraint < -1 || dataConstraint > 2) {
            throw new IllegalArgumentException("Expected -1, 0, 1, or 2, not: " + dataConstraint);
        }
        if (dataConstraint == -1) {
            return UserDataConstraint.None;
        }
        return values()[dataConstraint];
    }
    
    public UserDataConstraint combine(final UserDataConstraint other) {
        if (this.compareTo(other) < 0) {
            return this;
        }
        return other;
    }
}
