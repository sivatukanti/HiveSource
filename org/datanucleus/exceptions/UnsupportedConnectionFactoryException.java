// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.exceptions;

import org.datanucleus.ClassConstants;
import org.datanucleus.util.Localiser;

public class UnsupportedConnectionFactoryException extends NucleusUserException
{
    private static final Localiser LOCALISER;
    
    public UnsupportedConnectionFactoryException(final Object factory) {
        super(UnsupportedConnectionFactoryException.LOCALISER.msg("009001", factory));
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
    }
}
