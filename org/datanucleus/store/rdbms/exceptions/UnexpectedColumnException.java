// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.exceptions;

import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.util.Localiser;
import org.datanucleus.store.exceptions.DatastoreValidationException;

public class UnexpectedColumnException extends DatastoreValidationException
{
    private static final Localiser LOCALISER_RDBMS;
    
    public UnexpectedColumnException(final String table_name, final String column_name, final String schema_name, final String catalog_name) {
        super(UnexpectedColumnException.LOCALISER_RDBMS.msg("020024", column_name, table_name, schema_name, catalog_name));
    }
    
    static {
        LOCALISER_RDBMS = Localiser.getInstance("org.datanucleus.store.rdbms.Localisation", RDBMSStoreManager.class.getClassLoader());
    }
}
