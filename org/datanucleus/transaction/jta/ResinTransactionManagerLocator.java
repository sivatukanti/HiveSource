// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.transaction.jta;

import org.datanucleus.NucleusContext;

public class ResinTransactionManagerLocator extends JNDIBasedTransactionManagerLocator
{
    public ResinTransactionManagerLocator(final NucleusContext nucleusCtx) {
    }
    
    @Override
    public String getJNDIName() {
        return "java:comp/TransactionManager";
    }
}
