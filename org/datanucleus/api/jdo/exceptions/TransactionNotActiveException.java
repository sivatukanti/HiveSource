// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.api.jdo.exceptions;

import org.datanucleus.ClassConstants;
import org.datanucleus.util.Localiser;
import javax.jdo.JDOUserException;

public class TransactionNotActiveException extends JDOUserException
{
    private static final Localiser LOCALISER;
    
    public TransactionNotActiveException() {
        super(TransactionNotActiveException.LOCALISER.msg("015035"));
    }
    
    public TransactionNotActiveException(final String message, final Object failedObject) {
        super(message, failedObject);
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
    }
}
