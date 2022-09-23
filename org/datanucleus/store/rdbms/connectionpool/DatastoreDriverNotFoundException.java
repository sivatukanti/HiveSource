// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.connectionpool;

import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.util.Localiser;
import org.datanucleus.exceptions.NucleusException;

public class DatastoreDriverNotFoundException extends NucleusException
{
    protected static final Localiser LOCALISER;
    
    public DatastoreDriverNotFoundException(final String driverClassName) {
        super(DatastoreDriverNotFoundException.LOCALISER.msg("047000", driverClassName));
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.store.rdbms.Localisation", RDBMSStoreManager.class.getClassLoader());
    }
}
