// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.exceptions;

import org.datanucleus.ClassConstants;
import org.datanucleus.util.Localiser;

public class ConnectionFactoryNotFoundException extends NucleusUserException
{
    private static final Localiser LOCALISER;
    
    public ConnectionFactoryNotFoundException(final String name, final Exception nested) {
        super(ConnectionFactoryNotFoundException.LOCALISER.msg("009002", name), nested);
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
    }
}
