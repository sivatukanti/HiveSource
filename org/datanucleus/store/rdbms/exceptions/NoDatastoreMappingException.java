// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.exceptions;

import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.util.Localiser;
import org.datanucleus.exceptions.NucleusUserException;

public class NoDatastoreMappingException extends NucleusUserException
{
    protected static final Localiser LOCALISER;
    
    public NoDatastoreMappingException(final String fieldName) {
        super(NoDatastoreMappingException.LOCALISER.msg("020001", fieldName));
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.store.rdbms.Localisation", RDBMSStoreManager.class.getClassLoader());
    }
}
