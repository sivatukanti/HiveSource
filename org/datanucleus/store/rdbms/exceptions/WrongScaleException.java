// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.exceptions;

import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.util.Localiser;
import org.datanucleus.store.exceptions.DatastoreValidationException;

public class WrongScaleException extends DatastoreValidationException
{
    private static final Localiser LOCALISER_RDBMS;
    
    public WrongScaleException(final String columnName, final int expectedScale, final int actualScale) {
        super(WrongScaleException.LOCALISER_RDBMS.msg("020021", columnName, "" + actualScale, "" + expectedScale));
    }
    
    public WrongScaleException(final String columnName, final int expectedScale, final int actualScale, final String fieldName) {
        super(WrongScaleException.LOCALISER_RDBMS.msg("020022", columnName, "" + actualScale, "" + expectedScale, fieldName));
    }
    
    static {
        LOCALISER_RDBMS = Localiser.getInstance("org.datanucleus.store.rdbms.Localisation", RDBMSStoreManager.class.getClassLoader());
    }
}
