// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store;

import java.util.Iterator;
import org.datanucleus.FetchPlan;
import org.datanucleus.ExecutionContext;

public interface Extent
{
    Class getCandidateClass();
    
    boolean hasSubclasses();
    
    ExecutionContext getExecutionContext();
    
    FetchPlan getFetchPlan();
    
    Iterator iterator();
    
    void closeAll();
    
    void close(final Iterator p0);
}
