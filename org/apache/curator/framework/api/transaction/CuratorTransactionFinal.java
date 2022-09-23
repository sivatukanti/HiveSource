// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.api.transaction;

import java.util.Collection;

public interface CuratorTransactionFinal extends CuratorTransaction
{
    Collection<CuratorTransactionResult> commit() throws Exception;
}
