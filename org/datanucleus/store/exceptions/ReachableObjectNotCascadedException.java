// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.exceptions;

import org.datanucleus.ClassConstants;
import org.datanucleus.util.Localiser;
import org.datanucleus.exceptions.NucleusUserException;

public class ReachableObjectNotCascadedException extends NucleusUserException
{
    protected static final Localiser LOCALISER;
    
    public ReachableObjectNotCascadedException(final String fieldName, final Object pc) {
        super(ReachableObjectNotCascadedException.LOCALISER.msg("018008", fieldName, pc));
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
    }
}
