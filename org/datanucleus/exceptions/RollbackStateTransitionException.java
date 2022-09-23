// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.exceptions;

import org.datanucleus.ClassConstants;
import org.datanucleus.util.Localiser;

public class RollbackStateTransitionException extends NucleusException
{
    private static final Localiser LOCALISER;
    
    public RollbackStateTransitionException(final Exception[] nested) {
        super(RollbackStateTransitionException.LOCALISER.msg("015031"), nested);
        this.setFatal();
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
    }
}
