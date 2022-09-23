// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.services.cache;

import org.apache.derby.iapi.error.StandardException;

public interface Cacheable
{
    Cacheable setIdentity(final Object p0) throws StandardException;
    
    Cacheable createIdentity(final Object p0, final Object p1) throws StandardException;
    
    void clearIdentity();
    
    Object getIdentity();
    
    boolean isDirty();
    
    void clean(final boolean p0) throws StandardException;
}
