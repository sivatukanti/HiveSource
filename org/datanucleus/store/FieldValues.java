// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store;

import org.datanucleus.FetchPlan;
import org.datanucleus.state.ObjectProvider;

public interface FieldValues
{
    void fetchFields(final ObjectProvider p0);
    
    void fetchNonLoadedFields(final ObjectProvider p0);
    
    FetchPlan getFetchPlanForLoading();
}
