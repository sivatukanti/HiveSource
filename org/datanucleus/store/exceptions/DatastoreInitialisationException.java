// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.exceptions;

import org.datanucleus.exceptions.NucleusUserException;

public class DatastoreInitialisationException extends NucleusUserException
{
    public DatastoreInitialisationException(final String msg) {
        super(msg);
        this.setFatal();
    }
    
    public DatastoreInitialisationException(final String msg, final Throwable ex) {
        super(msg, ex);
        this.setFatal();
    }
}
