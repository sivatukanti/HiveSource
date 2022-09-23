// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.api.transaction;

import org.apache.curator.framework.api.Compressible;
import org.apache.curator.framework.api.ACLCreateModePathAndBytesable;
import org.apache.curator.framework.api.ACLPathAndBytesable;
import org.apache.curator.framework.api.CreateModable;
import org.apache.curator.framework.api.PathAndBytesable;

public interface TransactionCreateBuilder extends PathAndBytesable<CuratorTransactionBridge>, CreateModable<ACLPathAndBytesable<CuratorTransactionBridge>>, ACLPathAndBytesable<CuratorTransactionBridge>, ACLCreateModePathAndBytesable<CuratorTransactionBridge>, Compressible<ACLCreateModePathAndBytesable<CuratorTransactionBridge>>
{
}
