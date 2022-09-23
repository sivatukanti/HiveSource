// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.exceptions;

import org.datanucleus.ClassConstants;
import org.datanucleus.util.Localiser;
import org.datanucleus.exceptions.NucleusDataStoreException;

public class DatastoreValidationException extends NucleusDataStoreException
{
    protected static final Localiser LOCALISER;
    
    public DatastoreValidationException(final String msg) {
        super(msg);
    }
    
    public DatastoreValidationException(final String msg, final Exception nested) {
        super(msg, nested);
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
    }
}
