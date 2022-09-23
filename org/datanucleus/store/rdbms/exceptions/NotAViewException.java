// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.exceptions;

import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.util.Localiser;
import org.datanucleus.store.exceptions.DatastoreValidationException;

public class NotAViewException extends DatastoreValidationException
{
    private static final Localiser LOCALISER_RDBMS;
    
    public NotAViewException(final String viewName, final String type) {
        super(NotAViewException.LOCALISER_RDBMS.msg("020013", viewName, type));
    }
    
    static {
        LOCALISER_RDBMS = Localiser.getInstance("org.datanucleus.store.rdbms.Localisation", RDBMSStoreManager.class.getClassLoader());
    }
}
