// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.exceptions;

import org.datanucleus.exceptions.NucleusDataStoreException;

public class UnsupportedDataTypeException extends NucleusDataStoreException
{
    public UnsupportedDataTypeException() {
        this.setFatal();
    }
    
    public UnsupportedDataTypeException(final String msg) {
        super(msg);
        this.setFatal();
    }
    
    public UnsupportedDataTypeException(final String msg, final Exception nested) {
        super(msg, nested);
        this.setFatal();
    }
}
