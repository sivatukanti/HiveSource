// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.api.jdo.exceptions;

import org.datanucleus.ClassConstants;
import org.datanucleus.util.Localiser;

public class TransactionNotWritableException extends TransactionNotActiveException
{
    private static final Localiser LOCALISER;
    
    public TransactionNotWritableException() {
        super(TransactionNotWritableException.LOCALISER.msg("015041"), (Object)null);
    }
    
    public TransactionNotWritableException(final String message, final Object failedObject) {
        super(message, failedObject);
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
    }
}
