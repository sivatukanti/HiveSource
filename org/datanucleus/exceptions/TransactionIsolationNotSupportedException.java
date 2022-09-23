// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.exceptions;

import org.datanucleus.ClassConstants;
import org.datanucleus.util.Localiser;

public class TransactionIsolationNotSupportedException extends NucleusUserException
{
    private static final Localiser LOCALISER;
    
    public TransactionIsolationNotSupportedException(final String level) {
        super(TransactionIsolationNotSupportedException.LOCALISER.msg("015043", level));
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
    }
}
