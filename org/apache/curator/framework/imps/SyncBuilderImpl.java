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
import org.apache.curator.framework.api.Pathable;
import org.apache.curator.framework.api.UnhandledErrorListener;
import java.util.concurrent.Executor;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.ErrorListenerPathable;
import org.apache.curator.framework.api.SyncBuilder;

public class SyncBuilderImpl implements SyncBuilder, BackgroundOperation<String>, ErrorListenerPathable<Void>
{
    private final CuratorFrameworkImpl client;
    private Backgrounding backgrounding;
    
    public SyncBuilderImpl(final CuratorFrameworkImpl client) {
        this.backgrounding = new Backgrounding();
        this.client = client;
    }
    
    @Override
    public ErrorListenerPathable<Void> inBackground() {
        return this;
    }
    
    @Override
    public ErrorListenerPathable<Void> inBackground(final Object context) {
        this.backgrounding = new Backgrounding(context);
        return this;
    }
    
    @Override
    public ErrorListenerPathable<Void> inBackground(final BackgroundCallback callback) {
        this.backgrounding = new Backgrounding(callback);
        return this;
    }
    
    @Override
    public ErrorListenerPathable<Void> inBackground(final BackgroundCallback callback, final Object context) {
        this.backgrounding = new Backgrounding(callback, context);
        return this;
    }
    
    @Override
    public ErrorListenerPathable<Void> inBackground(final BackgroundCallback callback, final Executor executor) {
        this.backgrounding = new Backgrounding(this.client, callback, executor);
        return this;
    }
    
    @Override
    public ErrorListenerPathable<Void> inBackground(final BackgroundCallback callback, final Object context, final Executor executor) {
        this.backgrounding = new Backgrounding(this.client, callback, context, executor);
        return this;
    }
    
    @Override
    public Pathable<Void> withUnhandledErrorListener(final UnhandledErrorListener listener) {
        this.backgrounding = new Backgrounding(this.backgrounding, listener);
        return this;
    }
    
    @Override
    public void performBackgroundOperation(final OperationAndData<String> operationAndData) throws Exception {
        try {
            final OperationTrace trace = this.client.getZookeeperClient().startAdvancedTracer("SyncBuilderImpl-Background");
            final String path = operationAndData.getData();
            final String adjustedPath = this.client.fixForNamespace(path);
            final AsyncCallback.VoidCallback voidCallback = new AsyncCallback.VoidCallback() {
                @Override
                public void processResult(final int rc, final String path, final Object ctx) {
                    trace.setReturnCode(rc).setPath(path).commit();
                    final CuratorEvent event = new CuratorEventImpl(SyncBuilderImpl.this.client, CuratorEventType.SYNC, rc, path, path, ctx, null, null, null, null, null);
                    SyncBuilderImpl.this.client.processBackgroundOperation((OperationAndData<Object>)operationAndData, event);
                }
            };
            this.client.getZooKeeper().sync(adjustedPath, voidCallback, this.backgrounding.getContext());
        }
        catch (Throwable e) {
            this.backgrounding.checkError(e);
        }
    }
    
    @Override
    public Void forPath(final String path) throws Exception {
        final OperationAndData<String> operationAndData = new OperationAndData<String>(this, path, this.backgrounding.getCallback(), null, this.backgrounding.getContext());
        this.client.processBackgroundOperation(operationAndData, null);
        return null;
    }
}
