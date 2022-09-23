// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.sync;

public interface SynchronizerSupport
{
    Synchronizer getSynchronizer();
    
    void setSynchronizer(final Synchronizer p0);
    
    void lock(final LockMode p0);
    
    void unlock(final LockMode p0);
}
