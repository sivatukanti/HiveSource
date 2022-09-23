// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.exceptions;

import org.datanucleus.exceptions.NucleusException;

public class NotYetFlushedException extends NucleusException
{
    private final Object pc;
    
    public NotYetFlushedException(final Object pc) {
        super("not yet flushed");
        this.pc = pc;
    }
    
    public Object getPersistable() {
        return this.pc;
    }
}
