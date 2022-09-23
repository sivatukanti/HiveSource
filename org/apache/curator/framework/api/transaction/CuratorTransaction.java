// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.api.transaction;

public interface CuratorTransaction
{
    TransactionCreateBuilder create();
    
    TransactionDeleteBuilder delete();
    
    TransactionSetDataBuilder setData();
    
    TransactionCheckBuilder check();
}
