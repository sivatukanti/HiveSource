// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.exceptions;

public class NucleusOptimisticException extends NucleusException
{
    public NucleusOptimisticException() {
    }
    
    public NucleusOptimisticException(final String msg) {
        super(msg);
    }
    
    public NucleusOptimisticException(final String msg, final Throwable[] nested) {
        super(msg, nested);
    }
    
    public NucleusOptimisticException(final String msg, final Object failed) {
        super(msg, failed);
    }
}
