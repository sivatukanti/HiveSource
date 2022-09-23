// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.imps;

import org.apache.curator.drivers.OperationTrace;
import org.apache.curator.RetryLoop;
import java.util.concurrent.Callable;
import org.apache.curator.framework.api.Pathable;
import org.apache.curator.framework.api.StatPathable;
import org.apache.zookeeper.data.Stat;
import org.apache.curator.framework.api.TempGetDataBuilder;

class TempGetDataBuilderImpl implements TempGetDataBuilder
{
    private final CuratorFrameworkImpl client;
    private Stat responseStat;
    private boolean decompress;
    
    TempGetDataBuilderImpl(final CuratorFrameworkImpl client) {
        this.client = client;
        this.responseStat = null;
        this.decompress = false;
    }
    
    @Override
    public StatPathable<byte[]> decompressed() {
        this.decompress = true;
        return this;
    }
    
    @Override
    public Pathable<byte[]> storingStatIn(final Stat stat) {
        this.responseStat = stat;
        return this;
    }
    
    @Override
    public byte[] forPath(final String path) throws Exception {
        final String localPath = this.client.fixForNamespace(path);
        final OperationTrace trace = this.client.getZookeeperClient().startAdvancedTracer("GetDataBuilderImpl-Foreground");
        final byte[] responseData = RetryLoop.callWithRetry(this.client.getZookeeperClient(), (Callable<byte[]>)new Callable<byte[]>() {
            @Override
            public byte[] call() throws Exception {
                return TempGetDataBuilderImpl.this.client.getZooKeeper().getData(localPath, false, TempGetDataBuilderImpl.this.responseStat);
            }
        });
        trace.setResponseBytesLength(responseData).setPath(path).setStat(this.responseStat).commit();
        return this.decompress ? this.client.getCompressionProvider().decompress(path, responseData) : responseData;
    }
}
