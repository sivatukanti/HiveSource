// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.transaction.jta;

import org.datanucleus.NucleusContext;

public class WebLogicTransactionManagerLocator extends JNDIBasedTransactionManagerLocator
{
    public WebLogicTransactionManagerLocator(final NucleusContext nucleusCtx) {
    }
    
    @Override
    public String getJNDIName() {
        return "javax.transaction.TransactionManager";
    }
}
