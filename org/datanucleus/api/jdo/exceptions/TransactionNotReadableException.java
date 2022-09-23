// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.api.jdo.exceptions;

import org.datanucleus.ClassConstants;
import org.datanucleus.util.Localiser;

public class TransactionNotReadableException extends TransactionNotActiveException
{
    private static final Localiser LOCALISER;
    
    public TransactionNotReadableException() {
        super(TransactionNotReadableException.LOCALISER.msg("015041"), (Object)null);
    }
    
    public TransactionNotReadableException(final String message, final Object failedObject) {
        super(message, failedObject);
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
    }
}
