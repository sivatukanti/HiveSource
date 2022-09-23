// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.exceptions;

import org.datanucleus.ClassConstants;
import org.datanucleus.store.rdbms.adapter.DatastoreAdapter;
import org.datanucleus.util.Localiser;
import org.datanucleus.exceptions.NucleusDataStoreException;

public class TooManyForeignKeysException extends NucleusDataStoreException
{
    protected static final Localiser LOCALISER;
    
    public TooManyForeignKeysException(final DatastoreAdapter dba, final String table_name) {
        super(TooManyForeignKeysException.LOCALISER.msg("020015", "" + dba.getMaxForeignKeys(), table_name));
        this.setFatal();
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
    }
}
