// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.connection;

public interface ManagedConnectionResourceListener
{
    void transactionFlushed();
    
    void transactionPreClose();
    
    void managedConnectionPreClose();
    
    void managedConnectionPostClose();
    
    void resourcePostClose();
}
