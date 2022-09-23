// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.exceptions;

import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.util.Localiser;
import org.datanucleus.store.exceptions.DatastoreValidationException;

public class NotATableException extends DatastoreValidationException
{
    private static final Localiser LOCALISER_RDBMS;
    
    public NotATableException(final String tableName, final String type) {
        super(NotATableException.LOCALISER_RDBMS.msg("020012", tableName, type));
    }
    
    static {
        LOCALISER_RDBMS = Localiser.getInstance("org.datanucleus.store.rdbms.Localisation", RDBMSStoreManager.class.getClassLoader());
    }
}
