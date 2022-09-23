// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.sync;

public enum NoOpSynchronizer implements Synchronizer
{
    INSTANCE;
    
    @Override
    public void beginRead() {
    }
    
    @Override
    public void endRead() {
    }
    
    @Override
    public void beginWrite() {
    }
    
    @Override
    public void endWrite() {
    }
}
