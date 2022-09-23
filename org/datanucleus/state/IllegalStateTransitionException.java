// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.state;

import org.datanucleus.ClassConstants;
import org.datanucleus.util.Localiser;
import org.datanucleus.exceptions.NucleusException;

public class IllegalStateTransitionException extends NucleusException
{
    protected static final Localiser LOCALISER;
    
    public IllegalStateTransitionException(final LifeCycleState state, final String transition, final ObjectProvider op) {
        super(IllegalStateTransitionException.LOCALISER.msg("026027", transition, state, op));
        this.setFatal();
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
    }
}
