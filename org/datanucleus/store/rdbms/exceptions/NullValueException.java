// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.exceptions;

import org.datanucleus.exceptions.NucleusDataStoreException;

public class NullValueException extends NucleusDataStoreException
{
    public NullValueException() {
    }
    
    public NullValueException(final String msg) {
        super(msg);
    }
}
