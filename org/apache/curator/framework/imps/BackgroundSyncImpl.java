// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.imps;

import org.apache.curator.framework.api.CuratorEvent;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.WatchedEvent;
import java.util.List;
import org.apache.zookeeper.data.Stat;
import org.apache.curator.framework.api.CuratorEventType;
import org.apache.curator.drivers.OperationTrace;
import org.apache.zookeeper.AsyncCallback;

class BackgroundSyncImpl implements BackgroundOperation<String>
{
    private final CuratorFrameworkImpl client;
    private final Object context;
    
    BackgroundSyncImpl(final CuratorFrameworkImpl client, final Object context) {
        this.client = client;
        this.context = context;
    }
    
    @Override
    public void performBackgroundOperation(final OperationAndData<String> operationAndData) throws Exception {
        final OperationTrace trace = this.client.getZookeeperClient().startAdvancedTracer("BackgroundSyncImpl");
        final String data = operationAndData.getData();
        this.client.getZooKeeper().sync(data, new AsyncCallback.VoidCallback() {
            @Override
            public void processResult(final int rc, final String path, final Object ctx) {
                trace.setReturnCode(rc).setRequestBytesLength(data).commit();
                final CuratorEventImpl event = new CuratorEventImpl(BackgroundSyncImpl.this.client, CuratorEventType.SYNC, rc, path, null, ctx, null, null, null, null, null);
                BackgroundSyncImpl.this.client.processBackgroundOperation((OperationAndData<Object>)operationAndData, event);
            }
        }, this.context);
    }
}
