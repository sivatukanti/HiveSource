// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.imps;

import org.apache.curator.RetryLoop;
import java.util.concurrent.Callable;
import org.apache.zookeeper.WatchedEvent;
import org.apache.curator.framework.api.CuratorEventType;
import org.apache.curator.drivers.OperationTrace;
import org.apache.zookeeper.AsyncCallback;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.api.Pathable;
import org.apache.curator.framework.api.UnhandledErrorListener;
import java.util.concurrent.Executor;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.ACLable;
import org.apache.zookeeper.data.ACL;
import java.util.List;
import org.apache.curator.framework.api.ErrorListenerPathable;
import org.apache.zookeeper.data.Stat;
import org.apache.curator.framework.api.BackgroundPathable;
import org.apache.curator.framework.api.SetACLBuilder;

class SetACLBuilderImpl implements SetACLBuilder, BackgroundPathable<Stat>, BackgroundOperation<String>, ErrorListenerPathable<Stat>
{
    private final CuratorFrameworkImpl client;
    private ACLing acling;
    private Backgrounding backgrounding;
    private int version;
    
    SetACLBuilderImpl(final CuratorFrameworkImpl client) {
        this.client = client;
        this.backgrounding = new Backgrounding();
        this.acling = new ACLing(client.getAclProvider());
        this.version = -1;
    }
    
    @Override
    public BackgroundPathable<Stat> withACL(final List<ACL> aclList) {
        this.acling = new ACLing(this.client.getAclProvider(), aclList);
        return this;
    }
    
    @Override
    public ACLable<BackgroundPathable<Stat>> withVersion(final int version) {
        this.version = version;
        return this;
    }
    
    @Override
    public ErrorListenerPathable<Stat> inBackground() {
        this.backgrounding = new Backgrounding(true);
        return this;
    }
    
    @Override
    public ErrorListenerPathable<Stat> inBackground(final Object context) {
        this.backgrounding = new Backgrounding(context);
        return this;
    }
    
    @Override
    public ErrorListenerPathable<Stat> inBackground(final BackgroundCallback callback) {
        this.backgrounding = new Backgrounding(callback);
        return this;
    }
    
    @Override
    public ErrorListenerPathable<Stat> inBackground(final BackgroundCallback callback, final Object context) {
        this.backgrounding = new Backgrounding(callback, context);
        return this;
    }
    
    @Override
    public ErrorListenerPathable<Stat> inBackground(final BackgroundCallback callback, final Object context, final Executor executor) {
        this.backgrounding = new Backgrounding(this.client, callback, context, executor);
        return this;
    }
    
    @Override
    public ErrorListenerPathable<Stat> inBackground(final BackgroundCallback callback, final Executor executor) {
        this.backgrounding = new Backgrounding(this.client, callback, executor);
        return this;
    }
    
    @Override
    public Pathable<Stat> withUnhandledErrorListener(final UnhandledErrorListener listener) {
        this.backgrounding = new Backgrounding(this.backgrounding, listener);
        return this;
    }
    
    @Override
    public Stat forPath(String path) throws Exception {
        path = this.client.fixForNamespace(path);
        Stat resultStat = null;
        if (this.backgrounding.inBackground()) {
            this.client.processBackgroundOperation(new OperationAndData<Object>((BackgroundOperation<Object>)this, path, this.backgrounding.getCallback(), null, this.backgrounding.getContext()), null);
        }
        else {
            resultStat = this.pathInForeground(path);
        }
        return resultStat;
    }
    
    @Override
    public void performBackgroundOperation(final OperationAndData<String> operationAndData) throws Exception {
        try {
            final OperationTrace trace = this.client.getZookeeperClient().startAdvancedTracer("SetACLBuilderImpl-Background");
            final String path = operationAndData.getData();
            this.client.getZooKeeper().setACL(path, this.acling.getAclList(path), this.version, new AsyncCallback.StatCallback() {
                @Override
                public void processResult(final int rc, final String path, final Object ctx, final Stat stat) {
                    trace.setReturnCode(rc).setPath(path).setStat(stat).commit();
                    final CuratorEvent event = new CuratorEventImpl(SetACLBuilderImpl.this.client, CuratorEventType.SET_ACL, rc, path, null, ctx, stat, null, null, null, null);
                    SetACLBuilderImpl.this.client.processBackgroundOperation((OperationAndData<Object>)operationAndData, event);
                }
            }, this.backgrounding.getContext());
        }
        catch (Throwable e) {
            this.backgrounding.checkError(e);
        }
    }
    
    private Stat pathInForeground(final String path) throws Exception {
        final OperationTrace trace = this.client.getZookeeperClient().startAdvancedTracer("SetACLBuilderImpl-Foreground");
        final Stat resultStat = RetryLoop.callWithRetry(this.client.getZookeeperClient(), (Callable<Stat>)new Callable<Stat>() {
            @Override
            public Stat call() throws Exception {
                return SetACLBuilderImpl.this.client.getZooKeeper().setACL(path, SetACLBuilderImpl.this.acling.getAclList(path), SetACLBuilderImpl.this.version);
            }
        });
        trace.setPath(path).setStat(resultStat).commit();
        return resultStat;
    }
}
