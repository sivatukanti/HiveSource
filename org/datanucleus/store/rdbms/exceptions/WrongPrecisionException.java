// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.exceptions;

import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.util.Localiser;
import org.datanucleus.store.exceptions.DatastoreValidationException;

public class WrongPrecisionException extends DatastoreValidationException
{
    private static final Localiser LOCALISER_RDBMS;
    
    public WrongPrecisionException(final String columnName, final int expectedPrecision, final int actualPrecision) {
        super(WrongPrecisionException.LOCALISER_RDBMS.msg("020018", columnName, "" + actualPrecision, "" + expectedPrecision));
    }
    
    public WrongPrecisionException(final String columnName, final int expectedPrecision, final int actualPrecision, final String fieldName) {
        super(WrongPrecisionException.LOCALISER_RDBMS.msg("020019", columnName, "" + actualPrecision, "" + expectedPrecision, fieldName));
    }
    
    static {
        LOCALISER_RDBMS = Localiser.getInstance("org.datanucleus.store.rdbms.Localisation", RDBMSStoreManager.class.getClassLoader());
    }
}
