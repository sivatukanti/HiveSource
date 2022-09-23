// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.services.cache;

import org.apache.derby.iapi.error.StandardException;

interface ReplacementPolicy
{
    void insertEntry(final CacheEntry p0) throws StandardException;
    
    void doShrink();
    
    public interface Callback
    {
        void access();
        
        void free();
    }
}
