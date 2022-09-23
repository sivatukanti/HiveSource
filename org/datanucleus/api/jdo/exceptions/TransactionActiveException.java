// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.api.jdo.exceptions;

import org.datanucleus.ClassConstants;
import org.datanucleus.util.Localiser;
import javax.jdo.JDOUserException;

public class TransactionActiveException extends JDOUserException
{
    private static final Localiser LOCALISER;
    
    public TransactionActiveException(final Object failedObject) {
        super(TransactionActiveException.LOCALISER.msg("015032"), failedObject);
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
    }
}
