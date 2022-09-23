// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.query;

import org.datanucleus.exceptions.NucleusException;

public class QueryInterruptedException extends NucleusException
{
    public QueryInterruptedException() {
    }
    
    public QueryInterruptedException(final String msg) {
        super(msg);
    }
    
    public QueryInterruptedException(final String msg, final Throwable e) {
        super(msg, e);
    }
}
