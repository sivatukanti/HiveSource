// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.imps;

import org.apache.curator.RetryLoop;
import java.util.concurrent.Callable;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.zookeeper.WatchedEvent;
import org.apache.curator.framework.api.CuratorEventType;
import org.apache.curator.drivers.OperationTrace;
import org.apache.zookeeper.AsyncCallback;
import org.apache.curator.framework.api.Pathable;
import org.apache.curator.framework.api.UnhandledErrorListener;
import java.util.concurrent.Executor;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.zookeeper.data.Stat;
import org.apache.zookeeper.data.ACL;
import java.util.List;
import org.apache.curator.framework.api.ErrorListenerPathable;
import org.apache.curator.framework.api.GetACLBuilder;

class GetACLBuilderImpl implements GetACLBuilder, BackgroundOperation<String>, ErrorListenerPathable<List<ACL>>
{
    private final CuratorFrameworkImpl client;
    private Backgrounding backgrounding;
    private Stat responseStat;
    
    GetACLBuilderImpl(final CuratorFrameworkImpl client) {
        this.client = client;
        this.backgrounding = new Backgrounding();
        this.responseStat = new Stat();
    }
    
    @Override
    public ErrorListenerPathable<List<ACL>> inBackground(final BackgroundCallback callback, final Object context) {
        this.backgrounding = new Backgrounding(callback, context);
        return this;
    }
    
    @Override
    public ErrorListenerPathable<List<ACL>> inBackground(final BackgroundCallback callback, final Object context, final Executor executor) {
        this.backgrounding = new Backgrounding(this.client, callback, context, executor);
        return this;
    }
    
    @Override
    public ErrorListenerPathable<List<ACL>> inBackground() {
        this.backgrounding = new Backgrounding(true);
        return this;
    }
    
    @Override
    public ErrorListenerPathable<List<ACL>> inBackground(final Object context) {
        this.backgrounding = new Backgrounding(context);
        return this;
    }
    
    @Override
    public ErrorListenerPathable<List<ACL>> inBackground(final BackgroundCallback callback) {
        this.backgrounding = new Backgrounding(callback);
        return this;
    }
    
    @Override
    public ErrorListenerPathable<List<ACL>> inBackground(final BackgroundCallback callback, final Executor executor) {
        this.backgrounding = new Backgrounding(this.client, callback, executor);
        return this;
    }
    
    @Override
    public Pathable<List<ACL>> withUnhandledErrorListener(final UnhandledErrorListener listener) {
        this.backgrounding = new Backgrounding(this.backgrounding, listener);
        return this;
    }
    
    @Override
    public Pathable<List<ACL>> storingStatIn(final Stat stat) {
        this.responseStat = stat;
        return this;
    }
    
    @Override
    public void performBackgroundOperation(final OperationAndData<String> operationAndData) throws Exception {
        try {
            final OperationTrace trace = this.client.getZookeeperClient().startAdvancedTracer("GetACLBuilderImpl-Background");
            final AsyncCallback.ACLCallback callback = new AsyncCallback.ACLCallback() {
                @Override
                public void processResult(final int rc, final String path, final Object ctx, final List<ACL> acl, final Stat stat) {
                    trace.setReturnCode(rc).setPath(path).setStat(stat).commit();
                    final CuratorEventImpl event = new CuratorEventImpl(GetACLBuilderImpl.this.client, CuratorEventType.GET_ACL, rc, path, null, ctx, stat, null, null, null, acl);
                    GetACLBuilderImpl.this.client.processBackgroundOperation((OperationAndData<Object>)operationAndData, event);
                }
            };
            this.client.getZooKeeper().getACL(operationAndData.getData(), this.responseStat, callback, this.backgrounding.getContext());
        }
        catch (Throwable e) {
            this.backgrounding.checkError(e);
        }
    }
    
    @Override
    public List<ACL> forPath(String path) throws Exception {
        path = this.client.fixForNamespace(path);
        List<ACL> result = null;
        if (this.backgrounding.inBackground()) {
            this.client.processBackgroundOperation(new OperationAndData<Object>((BackgroundOperation<Object>)this, path, this.backgrounding.getCallback(), null, this.backgrounding.getContext()), null);
        }
        else {
            result = this.pathInForeground(path);
        }
        return result;
    }
    
    private List<ACL> pathInForeground(final String path) throws Exception {
        final OperationTrace trace = this.client.getZookeeperClient().startAdvancedTracer("GetACLBuilderImpl-Foreground");
        final List<ACL> result = RetryLoop.callWithRetry(this.client.getZookeeperClient(), (Callable<List<ACL>>)new Callable<List<ACL>>() {
            @Override
            public List<ACL> call() throws Exception {
                return GetACLBuilderImpl.this.client.getZooKeeper().getACL(path, GetACLBuilderImpl.this.responseStat);
            }
        });
        trace.setPath(path).setStat(this.responseStat).commit();
        return result;
    }
}
