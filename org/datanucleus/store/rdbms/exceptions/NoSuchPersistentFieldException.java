// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.exceptions;

import org.datanucleus.ClassConstants;
import org.datanucleus.util.Localiser;
import org.datanucleus.exceptions.NucleusUserException;

public class NoSuchPersistentFieldException extends NucleusUserException
{
    protected static final Localiser LOCALISER;
    
    public NoSuchPersistentFieldException(final String className, final String fieldName) {
        super(NoSuchPersistentFieldException.LOCALISER.msg("018009", fieldName, className));
    }
    
    public NoSuchPersistentFieldException(final String className, final int fieldNumber) {
        super(NoSuchPersistentFieldException.LOCALISER.msg("018010", "" + fieldNumber, className));
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
    }
}
