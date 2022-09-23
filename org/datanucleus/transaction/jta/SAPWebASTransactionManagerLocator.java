// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.transaction.jta;

import org.datanucleus.NucleusContext;

public class SAPWebASTransactionManagerLocator extends JNDIBasedTransactionManagerLocator
{
    public SAPWebASTransactionManagerLocator(final NucleusContext nucleusCtx) {
    }
    
    @Override
    public String getJNDIName() {
        return "TransactionManager";
    }
}
