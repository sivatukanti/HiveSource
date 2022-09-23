// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.transaction.jta;

import org.datanucleus.ClassConstants;
import org.datanucleus.util.NucleusLogger;
import javax.transaction.TransactionManager;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.util.Localiser;

public abstract class FactoryBasedTransactionManagerLocator implements TransactionManagerLocator
{
    protected static final Localiser LOCALISER;
    
    protected abstract Class getFactoryClass(final ClassLoaderResolver p0);
    
    @Override
    public TransactionManager getTransactionManager(final ClassLoaderResolver clr) {
        final Class factoryClass = this.getFactoryClass(clr);
        if (factoryClass == null) {
            return null;
        }
        try {
            return (TransactionManager)factoryClass.getMethod("getTransactionManager", (Class[])new Class[0]).invoke(null, new Object[0]);
        }
        catch (Exception e) {
            if (NucleusLogger.TRANSACTION.isDebugEnabled()) {
                NucleusLogger.TRANSACTION.debug("Exception finding FactoryBased transaction manager " + e.getMessage());
            }
            return null;
        }
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
    }
}
