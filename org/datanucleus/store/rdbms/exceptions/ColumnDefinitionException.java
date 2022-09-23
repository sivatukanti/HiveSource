// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.exceptions;

import org.datanucleus.exceptions.NucleusUserException;

public class ColumnDefinitionException extends NucleusUserException
{
    public ColumnDefinitionException() {
        this.setFatal();
    }
    
    public ColumnDefinitionException(final String msg) {
        super(msg);
        this.setFatal();
    }
}
