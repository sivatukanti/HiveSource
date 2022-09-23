// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.query;

import org.datanucleus.ClassConstants;
import org.datanucleus.util.Localiser;
import org.datanucleus.exceptions.NucleusUserException;

public class QueryNotUniqueException extends NucleusUserException
{
    protected static final Localiser LOCALISER;
    
    public QueryNotUniqueException() {
        super(QueryNotUniqueException.LOCALISER.msg("021001"));
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
    }
}
