// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.metadata;

public enum IdentityType
{
    APPLICATION, 
    DATASTORE, 
    NONDURABLE;
    
    public static IdentityType getIdentityType(final String value) {
        if (value == null) {
            return null;
        }
        if (IdentityType.APPLICATION.toString().equalsIgnoreCase(value)) {
            return IdentityType.APPLICATION;
        }
        if (IdentityType.DATASTORE.toString().equalsIgnoreCase(value)) {
            return IdentityType.DATASTORE;
        }
        if (IdentityType.NONDURABLE.toString().equalsIgnoreCase(value)) {
            return IdentityType.NONDURABLE;
        }
        return null;
    }
}
