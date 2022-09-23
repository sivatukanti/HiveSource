// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.exceptions;

import org.datanucleus.ClassConstants;
import org.datanucleus.util.Localiser;

public class ClassNotDetachableException extends NucleusUserException
{
    private static final Localiser LOCALISER;
    
    public ClassNotDetachableException(final String class_name) {
        super(ClassNotDetachableException.LOCALISER.msg("018004", class_name));
    }
    
    public ClassNotDetachableException(final String class_name, final Exception nested) {
        super(ClassNotDetachableException.LOCALISER.msg("018004", class_name), nested);
    }
    
    public ClassNotDetachableException(final Throwable[] nested) {
        super(ClassNotDetachableException.LOCALISER.msg("018005"), nested);
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
    }
}
