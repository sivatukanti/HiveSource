// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.connectionpool;

import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.util.Localiser;
import org.datanucleus.exceptions.NucleusException;

public class DatastorePoolException extends NucleusException
{
    protected static final Localiser LOCALISER;
    
    public DatastorePoolException(final String poolName, final String driverName, final String url, final Exception nested) {
        super(DatastorePoolException.LOCALISER.msg("047002", poolName, driverName, url, nested.getMessage()), nested);
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.store.rdbms.Localisation", RDBMSStoreManager.class.getClassLoader());
    }
}
