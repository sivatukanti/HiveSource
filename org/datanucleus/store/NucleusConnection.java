// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store;

public interface NucleusConnection
{
    Object getNativeConnection();
    
    void close();
    
    boolean isAvailable();
}
