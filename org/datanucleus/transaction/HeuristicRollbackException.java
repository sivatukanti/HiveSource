// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.transaction;

public class HeuristicRollbackException extends NucleusTransactionException
{
    public HeuristicRollbackException() {
    }
    
    public HeuristicRollbackException(final String message, final Throwable exception) {
        super(message, exception);
    }
    
    public HeuristicRollbackException(final String message, final Throwable[] exceptions) {
        super(message, exceptions);
    }
    
    public HeuristicRollbackException(final String message) {
        super(message);
    }
}
