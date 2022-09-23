// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.exceptions;

public class NucleusFatalUserException extends NucleusUserException
{
    public NucleusFatalUserException() {
        this.setFatal();
    }
    
    public NucleusFatalUserException(final String msg) {
        super(msg);
        this.setFatal();
    }
    
    public NucleusFatalUserException(final String msg, final Throwable[] nested) {
        super(msg, nested);
        this.setFatal();
    }
    
    public NucleusFatalUserException(final String msg, final Throwable nested) {
        super(msg, nested);
        this.setFatal();
    }
    
    public NucleusFatalUserException(final String msg, final Object failed) {
        super(msg, failed);
        this.setFatal();
    }
    
    public NucleusFatalUserException(final String msg, final Throwable[] nested, final Object failed) {
        super(msg, nested, failed);
        this.setFatal();
    }
    
    public NucleusFatalUserException(final String msg, final Throwable nested, final Object failed) {
        super(msg, nested, failed);
        this.setFatal();
    }
}
