// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.exceptions;

public class NucleusUserException extends NucleusException
{
    public NucleusUserException() {
    }
    
    public NucleusUserException(final String msg) {
        super(msg);
    }
    
    public NucleusUserException(final String msg, final Throwable[] nested) {
        super(msg, nested);
    }
    
    public NucleusUserException(final String msg, final Throwable nested) {
        super(msg, nested);
    }
    
    public NucleusUserException(final String msg, final Object failed) {
        super(msg, failed);
    }
    
    public NucleusUserException(final String msg, final Throwable[] nested, final Object failed) {
        super(msg, nested, failed);
    }
    
    public NucleusUserException(final String msg, final Throwable nested, final Object failed) {
        super(msg, nested, failed);
    }
}
