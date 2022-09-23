// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.transaction.jta;

import org.datanucleus.util.NucleusLogger;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.NucleusContext;

public class WebSphereTransactionManagerLocator extends FactoryBasedTransactionManagerLocator
{
    Class factoryClass;
    
    public WebSphereTransactionManagerLocator(final NucleusContext nucleusCtx) {
        this.factoryClass = null;
    }
    
    @Override
    protected Class getFactoryClass(final ClassLoaderResolver clr) {
        if (this.factoryClass != null) {
            return this.factoryClass;
        }
        try {
            try {
                this.factoryClass = clr.classForName("com.ibm.ws.Transaction.TransactionManagerFactory");
            }
            catch (Exception e) {
                try {
                    this.factoryClass = clr.classForName("com.ibm.ejs.jts.jta.TransactionManagerFactory");
                }
                catch (Exception e2) {
                    this.factoryClass = clr.classForName("com.ibm.ejs.jts.jta.JTSXA");
                }
            }
        }
        catch (Exception e) {
            if (NucleusLogger.TRANSACTION.isDebugEnabled()) {
                NucleusLogger.TRANSACTION.debug("Exception finding Websphere transaction manager. Probably not in a Websphere environment " + e.getMessage());
            }
        }
        return this.factoryClass;
    }
}
