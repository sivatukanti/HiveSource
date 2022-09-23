// 
// Decompiled by Procyon v0.5.36
// 

package parquet.org.apache.thrift.transport;

import java.nio.channels.Selector;

public abstract class TNonblockingServerTransport extends TServerTransport
{
    public abstract void registerSelector(final Selector p0);
}
