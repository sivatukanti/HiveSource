// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.exceptions;

import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.util.Localiser;
import org.datanucleus.exceptions.NucleusUserException;

public class NoTableManagedException extends NucleusUserException
{
    protected static final Localiser LOCALISER;
    
    public NoTableManagedException(final String className) {
        super(NoTableManagedException.LOCALISER.msg("020000", className));
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.store.rdbms.Localisation", RDBMSStoreManager.class.getClassLoader());
    }
}
