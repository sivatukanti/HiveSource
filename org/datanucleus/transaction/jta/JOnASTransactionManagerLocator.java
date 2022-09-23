// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.transaction.jta;

import org.datanucleus.util.NucleusLogger;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.NucleusContext;

public class JOnASTransactionManagerLocator extends FactoryBasedTransactionManagerLocator
{
    Class factoryClass;
    
    public JOnASTransactionManagerLocator(final NucleusContext nucleusCtx) {
        this.factoryClass = null;
    }
    
    @Override
    protected Class getFactoryClass(final ClassLoaderResolver clr) {
        if (this.factoryClass != null) {
            return this.factoryClass;
        }
        try {
            try {
                this.factoryClass = clr.classForName("org.objectweb.jonas_tm.Current");
            }
            catch (Exception ex) {}
        }
        catch (Exception e) {
            if (NucleusLogger.TRANSACTION.isDebugEnabled()) {
                NucleusLogger.TRANSACTION.debug("Exception finding JOnAS transaction manager. Probably not in a JOnAS environment " + e.getMessage());
            }
        }
        return this.factoryClass;
    }
}
