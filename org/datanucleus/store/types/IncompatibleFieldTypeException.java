// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.types;

import org.datanucleus.ClassConstants;
import org.datanucleus.util.Localiser;
import org.datanucleus.exceptions.NucleusUserException;

public class IncompatibleFieldTypeException extends NucleusUserException
{
    private static final Localiser LOCALISER;
    
    public IncompatibleFieldTypeException(final String classAndFieldName, final String requiredTypeName, final String requestedTypeName) {
        super(IncompatibleFieldTypeException.LOCALISER.msg("023000", classAndFieldName, requiredTypeName, requestedTypeName));
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
    }
}
