// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.exceptions;

import org.datanucleus.ClassConstants;
import org.datanucleus.util.Localiser;

public class NoPersistenceInformationException extends NucleusUserException
{
    private static final Localiser LOCALISER;
    
    public NoPersistenceInformationException(final String className) {
        super(NoPersistenceInformationException.LOCALISER.msg("018001", className));
    }
    
    public NoPersistenceInformationException(final String className, final Exception nested) {
        super(NoPersistenceInformationException.LOCALISER.msg("018001", className), nested);
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
    }
}
