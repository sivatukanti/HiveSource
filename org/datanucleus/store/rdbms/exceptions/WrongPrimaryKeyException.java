// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.exceptions;

import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.util.Localiser;
import org.datanucleus.store.exceptions.DatastoreValidationException;

public class WrongPrimaryKeyException extends DatastoreValidationException
{
    private static final Localiser LOCALISER_RDBMS;
    
    public WrongPrimaryKeyException(final String table_name, final String expected_pk, final String actual_pks) {
        super(WrongPrimaryKeyException.LOCALISER_RDBMS.msg("020020", table_name, expected_pk, actual_pks));
    }
    
    static {
        LOCALISER_RDBMS = Localiser.getInstance("org.datanucleus.store.rdbms.Localisation", RDBMSStoreManager.class.getClassLoader());
    }
}
