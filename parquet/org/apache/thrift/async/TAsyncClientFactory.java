// 
// Decompiled by Procyon v0.5.36
// 

package parquet.org.apache.thrift.async;

import parquet.org.apache.thrift.transport.TNonblockingTransport;

public interface TAsyncClientFactory<T extends TAsyncClient>
{
    T getAsyncClient(final TNonblockingTransport p0);
}
