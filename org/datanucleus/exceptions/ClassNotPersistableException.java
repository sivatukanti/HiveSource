// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.exceptions;

import org.datanucleus.ClassConstants;
import org.datanucleus.util.Localiser;

public class ClassNotPersistableException extends NucleusUserException
{
    private static final Localiser LOCALISER;
    
    public ClassNotPersistableException(final String className) {
        super(ClassNotPersistableException.LOCALISER.msg("018000", className));
    }
    
    public ClassNotPersistableException(final String className, final Exception nested) {
        super(ClassNotPersistableException.LOCALISER.msg("018000", className), nested);
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
    }
}
