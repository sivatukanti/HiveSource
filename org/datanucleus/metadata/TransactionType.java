// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.metadata;

public enum TransactionType
{
    JTA, 
    RESOURCE_LOCAL;
    
    public static TransactionType getValue(final String value) {
        if (value == null) {
            return null;
        }
        if (TransactionType.JTA.toString().equalsIgnoreCase(value)) {
            return TransactionType.JTA;
        }
        if (TransactionType.RESOURCE_LOCAL.toString().equalsIgnoreCase(value)) {
            return TransactionType.RESOURCE_LOCAL;
        }
        return null;
    }
}
