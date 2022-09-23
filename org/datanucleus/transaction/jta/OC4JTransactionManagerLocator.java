// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.transaction.jta;

import org.datanucleus.NucleusContext;

public class OC4JTransactionManagerLocator extends JNDIBasedTransactionManagerLocator
{
    public OC4JTransactionManagerLocator(final NucleusContext nucleusCtx) {
    }
    
    @Override
    public String getJNDIName() {
        return "java:comp/pm/TransactionManager";
    }
}
