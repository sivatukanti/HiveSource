// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.exceptions;

import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.util.Localiser;
import org.datanucleus.exceptions.NucleusException;

public class PrimaryKeyColumnNotAllowedException extends NucleusException
{
    protected static final Localiser LOCALISER;
    
    public PrimaryKeyColumnNotAllowedException(final String viewName, final String columnName) {
        super(PrimaryKeyColumnNotAllowedException.LOCALISER.msg("020014", viewName, columnName));
        this.setFatal();
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.store.rdbms.Localisation", RDBMSStoreManager.class.getClassLoader());
    }
}
