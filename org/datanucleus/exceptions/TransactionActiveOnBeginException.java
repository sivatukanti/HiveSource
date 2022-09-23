// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.exceptions;

import org.datanucleus.ClassConstants;
import org.datanucleus.util.Localiser;

public class TransactionActiveOnBeginException extends NucleusUserException
{
    private static final Localiser LOCALISER;
    
    public TransactionActiveOnBeginException(final Object failedObject) {
        super(TransactionActiveOnBeginException.LOCALISER.msg("015033"), failedObject);
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
    }
}
