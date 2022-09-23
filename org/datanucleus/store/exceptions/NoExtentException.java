// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.exceptions;

import org.datanucleus.ClassConstants;
import org.datanucleus.util.Localiser;
import org.datanucleus.exceptions.NucleusUserException;

public class NoExtentException extends NucleusUserException
{
    protected static final Localiser LOCALISER;
    
    public NoExtentException(final String className) {
        super(NoExtentException.LOCALISER.msg("018007", className));
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
    }
}
