// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.identity;

public interface OID
{
    Object getKeyValue();
    
    String getPcClass();
    
    boolean equals(final Object p0);
    
    int hashCode();
    
    String toString();
}
