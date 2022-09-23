// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.exceptions;

import org.datanucleus.ClassConstants;
import org.datanucleus.store.rdbms.adapter.DatastoreAdapter;
import org.datanucleus.util.Localiser;
import org.datanucleus.exceptions.NucleusDataStoreException;

public class TooManyIndicesException extends NucleusDataStoreException
{
    protected static final Localiser LOCALISER;
    
    public TooManyIndicesException(final DatastoreAdapter dba, final String tableName) {
        super(TooManyIndicesException.LOCALISER.msg("020016", "" + dba.getMaxIndexes(), tableName));
        this.setFatal();
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
    }
}
