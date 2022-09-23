// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.transaction.jta;

import javax.transaction.TransactionManager;
import org.datanucleus.ClassLoaderResolver;

public interface TransactionManagerLocator
{
    TransactionManager getTransactionManager(final ClassLoaderResolver p0);
}
