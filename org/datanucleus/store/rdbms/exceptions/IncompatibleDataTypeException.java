// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.exceptions;

import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.store.rdbms.JDBCUtils;
import org.datanucleus.store.rdbms.table.Column;
import org.datanucleus.util.Localiser;
import org.datanucleus.store.exceptions.DatastoreValidationException;

public class IncompatibleDataTypeException extends DatastoreValidationException
{
    private static final Localiser LOCALISER_RDBMS;
    
    public IncompatibleDataTypeException(final Column column, final int expectedType, final int actualType) {
        super(IncompatibleDataTypeException.LOCALISER_RDBMS.msg("020009", column, JDBCUtils.getNameForJDBCType(actualType), JDBCUtils.getNameForJDBCType(expectedType)));
    }
    
    static {
        LOCALISER_RDBMS = Localiser.getInstance("org.datanucleus.store.rdbms.Localisation", RDBMSStoreManager.class.getClassLoader());
    }
}
