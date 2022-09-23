// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.query;

import org.datanucleus.exceptions.NucleusException;

public class QueryTimeoutException extends NucleusException
{
    public QueryTimeoutException() {
    }
    
    public QueryTimeoutException(final String msg) {
        super(msg);
    }
    
    public QueryTimeoutException(final String msg, final Throwable e) {
        super(msg, e);
    }
}
