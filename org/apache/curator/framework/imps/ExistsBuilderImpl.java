// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.imps;

import org.apache.curator.RetryLoop;
import org.apache.zookeeper.KeeperException;
import org.apache.curator.utils.InternalACLProvider;
import java.util.concurrent.Callable;
import org.apache.curator.utils.ZKPaths;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.WatchedEvent;
import java.util.List;
import org.apache.curator.framework.api.CuratorEventType;
import org.apache.curator.drivers.OperationTrace;
import org.apache.zookeeper.AsyncCallback;
import org.apache.curator.framework.api.Pathable;
import org.apache.curator.framework.api.UnhandledErrorListener;
import java.util.concurrent.Executor;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.zookeeper.Watcher;
import org.apache.curator.framework.api.BackgroundPathable;
import org.apache.curator.framework.api.ExistsBuilderMain;
import org.apache.zookeeper.data.Stat;
import org.apache.curator.framework.api.ErrorListenerPathable;
import org.apache.curator.framework.api.ExistsBuilder;

class ExistsBuilderImpl implements ExistsBuilder, BackgroundOperation<String>, ErrorListenerPathable<Stat>
{
    private final CuratorFrameworkImpl client;
    private Backgrounding backgrounding;
    private Watching watching;
    private boolean createParentContainersIfNeeded;
    
    ExistsBuilderImpl(final CuratorFrameworkImpl client) {
        this.client = client;
        this.backgrounding = new Backgrounding();
        this.watching = new Watching();
        this.createParentContainersIfNeeded = false;
    }
    
    @Override
    public ExistsBuilderMain creatingParentContainersIfNeeded() {
        this.createParentContainersIfNeeded = true;
        return this;
    }
    
    @Override
    public BackgroundPathable<Stat> watched() {
        this.watching = new Watching(true);
        return this;
    }
    
    @Override
    public BackgroundPathable<Stat> usingWatcher(final Watcher watcher) {
        this.watching = new Watching(this.client, watcher);
        return this;
    }
    
    @Override
    public BackgroundPathable<Stat> usingWatcher(final CuratorWatcher watcher) {
        this.watching = new Watching(this.client, watcher);
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
    public ErrorListenerPathable<Stat> inBackground(final BackgroundCallback callback) {
        this.backgrounding = new Backgrounding(callback);
        return this;
    }
    
    @Override
    public ErrorListenerPathable<Stat> inBackground(final BackgroundCallback callback, final Executor executor) {
        this.backgrounding = new Backgrounding(this.client, callback, executor);
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
    public Pathable<Stat> withUnhandledErrorListener(final UnhandledErrorListener listener) {
        this.backgrounding = new Backgrounding(this.backgrounding, listener);
        return this;
    }
    
    @Override
    public void performBackgroundOperation(final OperationAndData<String> operationAndData) throws Exception {
        try {
            final OperationTrace trace = this.client.getZookeeperClient().startAdvancedTracer("ExistsBuilderImpl-Background");
            final AsyncCallback.StatCallback callback = new AsyncCallback.StatCallback() {
                @Override
                public void processResult(final int rc, final String path, final Object ctx, final Stat stat) {
                    trace.setReturnCode(rc).setPath(path).setWithWatcher(ExistsBuilderImpl.this.watching.getWatcher() != null).setStat(stat).commit();
                    final CuratorEvent event = new CuratorEventImpl(ExistsBuilderImpl.this.client, CuratorEventType.EXISTS, rc, path, null, ctx, stat, null, null, null, null);
                    ExistsBuilderImpl.this.client.processBackgroundOperation((OperationAndData<Object>)operationAndData, event);
                }
            };
            if (this.watching.isWatched()) {
                this.client.getZooKeeper().exists(operationAndData.getData(), true, callback, this.backgrounding.getContext());
            }
            else {
                this.client.getZooKeeper().exists(operationAndData.getData(), this.watching.getWatcher(), callback, this.backgrounding.getContext());
            }
        }
        catch (Throwable e) {
            this.backgrounding.checkError(e);
        }
    }
    
    @Override
    public Stat forPath(String path) throws Exception {
        path = this.client.fixForNamespace(path);
        Stat returnStat = null;
        if (this.backgrounding.inBackground()) {
            final OperationAndData<String> operationAndData = new OperationAndData<String>(this, path, this.backgrounding.getCallback(), null, this.backgrounding.getContext());
            if (this.createParentContainersIfNeeded) {
                CreateBuilderImpl.backgroundCreateParentsThenNode(this.client, operationAndData, operationAndData.getData(), this.backgrounding, true);
            }
            else {
                this.client.processBackgroundOperation(operationAndData, null);
            }
        }
        else {
            returnStat = this.pathInForeground(path);
        }
        return returnStat;
    }
    
    private Stat pathInForeground(final String path) throws Exception {
        if (this.createParentContainersIfNeeded) {
            final String parent = ZKPaths.getPathAndNode(path).getPath();
            if (!parent.equals("/")) {
                final OperationTrace trace = this.client.getZookeeperClient().startAdvancedTracer("ExistsBuilderImpl-Foreground-CreateParents");
                RetryLoop.callWithRetry(this.client.getZookeeperClient(), (Callable<Object>)new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        try {
                            ZKPaths.mkdirs(ExistsBuilderImpl.this.client.getZooKeeper(), parent, true, ExistsBuilderImpl.this.client.getAclProvider(), true);
                        }
                        catch (KeeperException.NodeExistsException ex) {}
                        catch (KeeperException.NoNodeException ex2) {}
                        return null;
                    }
                });
                trace.setPath(path).commit();
            }
        }
        return this.pathInForegroundStandard(path);
    }
    
    private Stat pathInForegroundStandard(final String path) throws Exception {
        final OperationTrace trace = this.client.getZookeeperClient().startAdvancedTracer("ExistsBuilderImpl-Foreground");
        final Stat returnStat = RetryLoop.callWithRetry(this.client.getZookeeperClient(), (Callable<Stat>)new Callable<Stat>() {
            @Override
            public Stat call() throws Exception {
                Stat returnStat;
                if (ExistsBuilderImpl.this.watching.isWatched()) {
                    returnStat = ExistsBuilderImpl.this.client.getZooKeeper().exists(path, true);
                }
                else {
                    returnStat = ExistsBuilderImpl.this.client.getZooKeeper().exists(path, ExistsBuilderImpl.this.watching.getWatcher());
                }
                return returnStat;
            }
        });
        trace.setPath(path).setWithWatcher(this.watching.getWatcher() != null).setStat(returnStat).commit();
        return returnStat;
    }
}
