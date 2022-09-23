// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.flush;

import org.datanucleus.ClassConstants;
import org.datanucleus.exceptions.NucleusOptimisticException;
import org.datanucleus.state.ObjectProvider;
import java.util.List;
import org.datanucleus.ExecutionContext;
import org.datanucleus.util.Localiser;

public interface FlushProcess
{
    public static final Localiser LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
    
    List<NucleusOptimisticException> execute(final ExecutionContext p0, final List<ObjectProvider> p1, final List<ObjectProvider> p2, final OperationQueue p3);
}
