// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.types;

import org.datanucleus.state.FetchPlanState;

public interface SCO
{
    void initialise(final Object p0, final boolean p1, final boolean p2) throws ClassCastException;
    
    void initialise();
    
    String getFieldName();
    
    Object getOwner();
    
    void unsetOwner();
    
    Object getValue();
    
    Object clone();
    
    Object detachCopy(final FetchPlanState p0);
    
    void attachCopy(final Object p0);
}
