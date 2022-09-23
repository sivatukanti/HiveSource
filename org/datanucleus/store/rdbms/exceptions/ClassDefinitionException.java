// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.exceptions;

import org.datanucleus.ClassConstants;
import org.datanucleus.util.Localiser;
import org.datanucleus.exceptions.NucleusUserException;

public class ClassDefinitionException extends NucleusUserException
{
    protected static final Localiser LOCALISER;
    
    public ClassDefinitionException() {
        this.setFatal();
    }
    
    public ClassDefinitionException(final String msg) {
        super(msg);
        this.setFatal();
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
    }
}
