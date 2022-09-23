// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.sync;

public interface Synchronizer
{
    void beginRead();
    
    void endRead();
    
    void beginWrite();
    
    void endWrite();
}
