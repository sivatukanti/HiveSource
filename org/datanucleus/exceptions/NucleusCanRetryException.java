// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.exceptions;

public class NucleusCanRetryException extends NucleusException
{
    public NucleusCanRetryException() {
    }
    
    public NucleusCanRetryException(final String msg) {
        super(msg);
    }
    
    public NucleusCanRetryException(final String msg, final Throwable[] nested) {
        super(msg, nested);
    }
    
    public NucleusCanRetryException(final String msg, final Throwable nested) {
        super(msg, nested);
    }
    
    public NucleusCanRetryException(final String msg, final Object failed) {
        super(msg, failed);
    }
    
    public NucleusCanRetryException(final String msg, final Throwable[] nested, final Object failed) {
        super(msg, nested, failed);
    }
    
    public NucleusCanRetryException(final String msg, final Throwable nested, final Object failed) {
        super(msg, nested, failed);
    }
}
