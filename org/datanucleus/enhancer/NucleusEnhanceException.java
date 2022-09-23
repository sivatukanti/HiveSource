// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.enhancer;

import org.datanucleus.exceptions.NucleusException;

public class NucleusEnhanceException extends NucleusException
{
    public NucleusEnhanceException(final String msg) {
        super(msg);
    }
    
    public NucleusEnhanceException(final String msg, final Throwable[] nested) {
        super(msg, nested);
    }
    
    public NucleusEnhanceException(final String msg, final Throwable nested) {
        super(msg, nested);
    }
}
