// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.exceptions;

import org.datanucleus.ClassConstants;
import org.datanucleus.util.Localiser;

public class TransactionActiveOnCloseException extends NucleusUserException
{
    private static final Localiser LOCALISER;
    
    public TransactionActiveOnCloseException(final Object failedObject) {
        super(TransactionActiveOnCloseException.LOCALISER.msg("015034"), failedObject);
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
    }
}
