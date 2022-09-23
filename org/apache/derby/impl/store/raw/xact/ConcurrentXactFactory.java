// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.raw.xact;

public class ConcurrentXactFactory extends XactFactory
{
    @Override
    TransactionMapFactory createMapFactory() {
        return new ConcurrentTransactionMapFactory();
    }
}
