// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.transaction.jta;

import org.datanucleus.NucleusContext;

public class JBossTransactionManagerLocator extends JNDIBasedTransactionManagerLocator
{
    public JBossTransactionManagerLocator(final NucleusContext nucleusCtx) {
    }
    
    @Override
    public String getJNDIName() {
        return "java:/TransactionManager";
    }
}
