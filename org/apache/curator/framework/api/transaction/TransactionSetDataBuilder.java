// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.api.transaction;

import org.apache.curator.framework.api.Compressible;
import org.apache.curator.framework.api.VersionPathAndBytesable;
import org.apache.curator.framework.api.Versionable;
import org.apache.curator.framework.api.PathAndBytesable;

public interface TransactionSetDataBuilder extends PathAndBytesable<CuratorTransactionBridge>, Versionable<PathAndBytesable<CuratorTransactionBridge>>, VersionPathAndBytesable<CuratorTransactionBridge>, Compressible<VersionPathAndBytesable<CuratorTransactionBridge>>
{
}
