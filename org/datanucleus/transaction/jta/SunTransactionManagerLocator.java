// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.transaction.jta;

import org.datanucleus.NucleusContext;

public class SunTransactionManagerLocator extends JNDIBasedTransactionManagerLocator
{
    public SunTransactionManagerLocator(final NucleusContext nucleusCtx) {
    }
    
    @Override
    public String getJNDIName() {
        return "java:appserver/TransactionManager";
    }
}
