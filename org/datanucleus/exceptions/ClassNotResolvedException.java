// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.exceptions;

public class ClassNotResolvedException extends NucleusException
{
    public ClassNotResolvedException(final String msg, final Throwable nested) {
        super(msg, nested);
    }
    
    public ClassNotResolvedException(final String msg) {
        super(msg);
    }
}
