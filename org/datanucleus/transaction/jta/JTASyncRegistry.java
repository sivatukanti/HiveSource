// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.transaction.jta;

import javax.transaction.Synchronization;
import javax.naming.InitialContext;
import javax.transaction.TransactionSynchronizationRegistry;

public class JTASyncRegistry
{
    TransactionSynchronizationRegistry registry;
    
    public JTASyncRegistry() throws JTASyncRegistryUnavailableException {
        try {
            final InitialContext ctx = new InitialContext();
            this.registry = (TransactionSynchronizationRegistry)ctx.lookup("java:comp/TransactionSynchronizationRegistry");
        }
        catch (Throwable thr) {
            throw new JTASyncRegistryUnavailableException();
        }
    }
    
    public void register(final Synchronization sync) {
        this.registry.registerInterposedSynchronization(sync);
    }
}
