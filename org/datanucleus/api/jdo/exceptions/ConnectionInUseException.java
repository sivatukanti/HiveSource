// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.api.jdo.exceptions;

import org.datanucleus.ClassConstants;
import org.datanucleus.util.Localiser;
import javax.jdo.JDOUserException;

public class ConnectionInUseException extends JDOUserException
{
    private static final Localiser LOCALISER;
    
    public ConnectionInUseException() {
        super(ConnectionInUseException.LOCALISER.msg("009003"));
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
    }
}
