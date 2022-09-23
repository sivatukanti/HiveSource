// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.exceptions;

import org.datanucleus.store.rdbms.RDBMSStoreManager;
import java.util.Iterator;
import org.datanucleus.store.rdbms.table.Column;
import java.util.Collection;
import org.datanucleus.store.rdbms.table.Table;
import org.datanucleus.util.Localiser;
import org.datanucleus.store.exceptions.DatastoreValidationException;

public class MissingColumnException extends DatastoreValidationException
{
    private static final Localiser LOCALISER_RDBMS;
    
    public MissingColumnException(final Table table, final Collection columns) {
        super(MissingColumnException.LOCALISER_RDBMS.msg("020010", table.toString(), getColumnNameList(columns)));
    }
    
    private static String getColumnNameList(final Collection columns) {
        final StringBuffer list = new StringBuffer();
        final Iterator<Column> i = columns.iterator();
        while (i.hasNext()) {
            if (list.length() > 0) {
                list.append(", ");
            }
            list.append(i.next().getIdentifier());
        }
        return list.toString();
    }
    
    static {
        LOCALISER_RDBMS = Localiser.getInstance("org.datanucleus.store.rdbms.Localisation", RDBMSStoreManager.class.getClassLoader());
    }
}
