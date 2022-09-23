// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.transaction;

import org.datanucleus.exceptions.NucleusException;

public class NucleusTransactionException extends NucleusException
{
    public NucleusTransactionException() {
    }
    
    public NucleusTransactionException(final String message) {
        super(message);
    }
    
    public NucleusTransactionException(final String message, final Throwable exception) {
        super(message, exception);
    }
    
    public NucleusTransactionException(final String message, final Throwable[] exceptions) {
        super(message, exceptions);
    }
}
