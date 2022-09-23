// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.imps;

import org.apache.curator.RetryLoop;
import java.util.concurrent.Callable;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.WatchedEvent;
import org.apache.curator.framework.api.CuratorEventType;
import org.apache.curator.shaded.com.google.common.collect.Lists;
import org.apache.curator.drivers.OperationTrace;
import org.apache.zookeeper.AsyncCallback;
import org.apache.curator.framework.api.BackgroundPathable;
import org.apache.curator.framework.api.UnhandledErrorListener;
import java.util.concurrent.Executor;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.zookeeper.Watcher;
import org.apache.curator.framework.api.Pathable;
import org.apache.curator.framework.api.WatchPathable;
import org.apache.zookeeper.data.Stat;
import java.util.List;
import org.apache.curator.framework.api.ErrorListenerPathable;
import org.apache.curator.framework.api.GetChildrenBuilder;

class GetChildrenBuilderImpl implements GetChildrenBuilder, BackgroundOperation<String>, ErrorListenerPathable<List<String>>
{
    private final CuratorFrameworkImpl client;
    private Watching watching;
    private Backgrounding backgrounding;
    private Stat responseStat;
    
    GetChildrenBuilderImpl(final CuratorFrameworkImpl client) {
        this.client = client;
        this.watching = new Watching();
        this.backgrounding = new Backgrounding();
        this.responseStat = null;
    }
    
    @Override
    public WatchPathable<List<String>> storingStatIn(final Stat stat) {
        this.responseStat = stat;
        return new WatchPathable<List<String>>() {
            @Override
            public List<String> forPath(final String path) throws Exception {
                return GetChildrenBuilderImpl.this.forPath(path);
            }
            
            @Override
            public Pathable<List<String>> watched() {
                GetChildrenBuilderImpl.this.watched();
                return GetChildrenBuilderImpl.this;
            }
            
            @Override
            public Pathable<List<String>> usingWatcher(final Watcher watcher) {
                GetChildrenBuilderImpl.this.usingWatcher(watcher);
                return GetChildrenBuilderImpl.this;
            }
            
            @Override
            public Pathable<List<String>> usingWatcher(final CuratorWatcher watcher) {
                GetChildrenBuilderImpl.this.usingWatcher(watcher);
                return GetChildrenBuilderImpl.this;
            }
        };
    }
    
    @Override
    public ErrorListenerPathable<List<String>> inBackground(final BackgroundCallback callback, final Object context) {
        this.backgrounding = new Backgrounding(callback, context);
        return this;
    }
    
    @Override
    public ErrorListenerPathable<List<String>> inBackground(final BackgroundCallback callback, final Object context, final Executor executor) {
        this.backgrounding = new Backgrounding(this.client, callback, context, executor);
        return this;
    }
    
    @Override
    public ErrorListenerPathable<List<String>> inBackground(final BackgroundCallback callback) {
        this.backgrounding = new Backgrounding(callback);
        return this;
    }
    
    @Override
    public ErrorListenerPathable<List<String>> inBackground(final BackgroundCallback callback, final Executor executor) {
        this.backgrounding = new Backgrounding(this.client, callback, executor);
        return this;
    }
    
    @Override
    public ErrorListenerPathable<List<String>> inBackground() {
        this.backgrounding = new Backgrounding(true);
        return this;
    }
    
    @Override
    public ErrorListenerPathable<List<String>> inBackground(final Object context) {
        this.backgrounding = new Backgrounding(context);
        return this;
    }
    
    @Override
    public Pathable<List<String>> withUnhandledErrorListener(final UnhandledErrorListener listener) {
        this.backgrounding = new Backgrounding(this.backgrounding, listener);
        return this;
    }
    
    @Override
    public BackgroundPathable<List<String>> watched() {
        this.watching = new Watching(true);
        return this;
    }
    
    @Override
    public BackgroundPathable<List<String>> usingWatcher(final Watcher watcher) {
        this.watching = new Watching(this.client, watcher);
        return this;
    }
    
    @Override
    public BackgroundPathable<List<String>> usingWatcher(final CuratorWatcher watcher) {
        this.watching = new Watching(this.client, watcher);
        return this;
    }
    
    @Override
    public void performBackgroundOperation(final OperationAndData<String> operationAndData) throws Exception {
        try {
            final OperationTrace trace = this.client.getZookeeperClient().startAdvancedTracer("GetChildrenBuilderImpl-Background");
            final AsyncCallback.Children2Callback callback = new AsyncCallback.Children2Callback() {
                @Override
                public void processResult(final int rc, final String path, final Object o, List<String> strings, final Stat stat) {
                    trace.setReturnCode(rc).setPath(path).setWithWatcher(GetChildrenBuilderImpl.this.watching.getWatcher() != null).setStat(stat).commit();
                    if (strings == null) {
                        strings = (List<String>)Lists.newArrayList();
                    }
                    final CuratorEventImpl event = new CuratorEventImpl(GetChildrenBuilderImpl.this.client, CuratorEventType.CHILDREN, rc, path, null, o, stat, null, strings, null, null);
                    GetChildrenBuilderImpl.this.client.processBackgroundOperation((OperationAndData<Object>)operationAndData, event);
                }
            };
            if (this.watching.isWatched()) {
                this.client.getZooKeeper().getChildren(operationAndData.getData(), true, callback, this.backgrounding.getContext());
            }
            else {
                this.client.getZooKeeper().getChildren(operationAndData.getData(), this.watching.getWatcher(), callback, this.backgrounding.getContext());
            }
        }
        catch (Throwable e) {
            this.backgrounding.checkError(e);
        }
    }
    
    @Override
    public List<String> forPath(String path) throws Exception {
        path = this.client.fixForNamespace(path);
        List<String> children = null;
        if (this.backgrounding.inBackground()) {
            this.client.processBackgroundOperation(new OperationAndData<Object>((BackgroundOperation<Object>)this, path, this.backgrounding.getCallback(), null, this.backgrounding.getContext()), null);
        }
        else {
            children = this.pathInForeground(path);
        }
        return children;
    }
    
    private List<String> pathInForeground(final String path) throws Exception {
        final OperationTrace trace = this.client.getZookeeperClient().startAdvancedTracer("GetChildrenBuilderImpl-Foreground");
        final List<String> children = RetryLoop.callWithRetry(this.client.getZookeeperClient(), (Callable<List<String>>)new Callable<List<String>>() {
            @Override
            public List<String> call() throws Exception {
                List<String> children;
                if (GetChildrenBuilderImpl.this.watching.isWatched()) {
                    children = GetChildrenBuilderImpl.this.client.getZooKeeper().getChildren(path, true, GetChildrenBuilderImpl.this.responseStat);
                }
                else {
                    children = GetChildrenBuilderImpl.this.client.getZooKeeper().getChildren(path, GetChildrenBuilderImpl.this.watching.getWatcher(), GetChildrenBuilderImpl.this.responseStat);
                }
                return children;
            }
        });
        trace.setPath(path).setWithWatcher(this.watching.getWatcher() != null).setStat(this.responseStat).commit();
        return children;
    }
}
