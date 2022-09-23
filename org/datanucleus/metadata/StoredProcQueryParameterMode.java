// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.metadata;

public enum StoredProcQueryParameterMode
{
    IN, 
    OUT, 
    INOUT, 
    REF_CURSOR;
    
    public static StoredProcQueryParameterMode getMode(final String value) {
        if (value == null) {
            return null;
        }
        if (StoredProcQueryParameterMode.IN.toString().equalsIgnoreCase(value)) {
            return StoredProcQueryParameterMode.IN;
        }
        if (StoredProcQueryParameterMode.OUT.toString().equalsIgnoreCase(value)) {
            return StoredProcQueryParameterMode.OUT;
        }
        if (StoredProcQueryParameterMode.INOUT.toString().equalsIgnoreCase(value)) {
            return StoredProcQueryParameterMode.INOUT;
        }
        if (StoredProcQueryParameterMode.REF_CURSOR.toString().equalsIgnoreCase(value)) {
            return StoredProcQueryParameterMode.REF_CURSOR;
        }
        return null;
    }
}
