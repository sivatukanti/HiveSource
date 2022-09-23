// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.transaction.jta;

import org.datanucleus.util.NucleusLogger;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.NucleusContext;

public class BTMTransactionManagerLocator extends FactoryBasedTransactionManagerLocator
{
    Class factoryClass;
    
    public BTMTransactionManagerLocator(final NucleusContext nucleusCtx) {
        this.factoryClass = null;
    }
    
    @Override
    protected Class getFactoryClass(final ClassLoaderResolver clr) {
        if (this.factoryClass != null) {
            return this.factoryClass;
        }
        try {
            try {
                this.factoryClass = clr.classForName("bitronix.tm.TransactionManagerServices");
            }
            catch (Exception ex) {}
        }
        catch (Exception e) {
            if (NucleusLogger.TRANSACTION.isDebugEnabled()) {
                NucleusLogger.TRANSACTION.debug("Exception finding BTM transaction manager. Probably not in a BTM environment " + e.getMessage());
            }
        }
        return this.factoryClass;
    }
}
