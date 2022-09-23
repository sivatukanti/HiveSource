// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.valuegenerator;

import org.datanucleus.exceptions.NucleusException;

public class ValueGenerationException extends NucleusException
{
    public ValueGenerationException(final String message) {
        super(message);
    }
    
    public ValueGenerationException(final String message, final Throwable nested) {
        super(message, nested);
    }
}
