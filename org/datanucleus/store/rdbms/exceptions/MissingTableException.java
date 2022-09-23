// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.exceptions;

import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.util.Localiser;
import org.datanucleus.store.exceptions.DatastoreValidationException;

public class MissingTableException extends DatastoreValidationException
{
    private static final Localiser LOCALISER_RDBMS;
    
    public MissingTableException(final String catalogName, final String schemaName, final String tableName) {
        super(MissingTableException.LOCALISER_RDBMS.msg("020011", catalogName, schemaName, tableName));
    }
    
    static {
        LOCALISER_RDBMS = Localiser.getInstance("org.datanucleus.store.rdbms.Localisation", RDBMSStoreManager.class.getClassLoader());
    }
}
