// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.exceptions;

public class NucleusObjectNotFoundException extends NucleusException
{
    public NucleusObjectNotFoundException() {
    }
    
    public NucleusObjectNotFoundException(final String msg) {
        super(msg);
    }
    
    public NucleusObjectNotFoundException(final String msg, final Throwable[] nested) {
        super(msg, nested);
    }
    
    public NucleusObjectNotFoundException(final String msg, final Object failed) {
        super(msg, failed);
    }
}
