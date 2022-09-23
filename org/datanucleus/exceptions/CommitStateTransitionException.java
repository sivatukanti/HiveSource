// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.exceptions;

import org.datanucleus.ClassConstants;
import org.datanucleus.util.Localiser;

public class CommitStateTransitionException extends NucleusException
{
    private static final Localiser LOCALISER;
    
    public CommitStateTransitionException(final Exception[] nested) {
        super(CommitStateTransitionException.LOCALISER.msg("015037"), nested);
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
    }
}
