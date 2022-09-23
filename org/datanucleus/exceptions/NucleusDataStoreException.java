// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.exceptions;

public class NucleusDataStoreException extends NucleusException
{
    public NucleusDataStoreException() {
    }
    
    public NucleusDataStoreException(final String msg) {
        super(msg);
    }
    
    public NucleusDataStoreException(final String msg, final Throwable[] nested) {
        super(msg, nested);
    }
    
    public NucleusDataStoreException(final String msg, final Throwable nested) {
        super(msg, nested);
    }
    
    public NucleusDataStoreException(final String msg, final Object failed) {
        super(msg, failed);
    }
    
    public NucleusDataStoreException(final String msg, final Throwable[] nested, final Object failed) {
        super(msg, nested, failed);
    }
    
    public NucleusDataStoreException(final String msg, final Throwable nested, final Object failed) {
        super(msg, nested, failed);
    }
}
