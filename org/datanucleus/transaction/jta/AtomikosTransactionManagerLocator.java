// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.transaction.jta;

import org.datanucleus.util.NucleusLogger;
import javax.transaction.TransactionManager;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.NucleusContext;

public class AtomikosTransactionManagerLocator implements TransactionManagerLocator
{
    public AtomikosTransactionManagerLocator(final NucleusContext nucleusCtx) {
    }
    
    @Override
    public TransactionManager getTransactionManager(final ClassLoaderResolver clr) {
        final Class cls = clr.classForName("com.atomikos.icatch.jta.UserTransactionManager");
        try {
            return cls.newInstance();
        }
        catch (Exception e) {
            NucleusLogger.TRANSACTION.debug("Exception obtaining Atomikos transaction manager " + e.getMessage());
            return null;
        }
    }
}
