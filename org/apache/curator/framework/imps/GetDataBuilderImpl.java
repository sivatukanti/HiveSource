// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.imps;

import org.apache.curator.RetryLoop;
import java.util.concurrent.Callable;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.WatchedEvent;
import java.util.List;
import org.apache.curator.framework.api.CuratorEventType;
import org.apache.zookeeper.KeeperException;
import org.apache.curator.utils.ThreadUtils;
import org.apache.curator.drivers.OperationTrace;
import org.apache.zookeeper.AsyncCallback;
import org.apache.curator.framework.api.UnhandledErrorListener;
import org.apache.curator.framework.api.Pathable;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.zookeeper.Watcher;
import org.apache.curator.framework.api.BackgroundPathable;
import org.apache.curator.framework.api.WatchPathable;
import java.util.concurrent.Executor;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.GetDataWatchBackgroundStatable;
import org.slf4j.LoggerFactory;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.apache.curator.framework.api.ErrorListenerPathable;
import org.apache.curator.framework.api.GetDataBuilder;

class GetDataBuilderImpl implements GetDataBuilder, BackgroundOperation<String>, ErrorListenerPathable<byte[]>
{
    private final Logger log;
    private final CuratorFrameworkImpl client;
    private Stat responseStat;
    private Watching watching;
    private Backgrounding backgrounding;
    private boolean decompress;
    
    GetDataBuilderImpl(final CuratorFrameworkImpl client) {
        this.log = LoggerFactory.getLogger(this.getClass());
        this.client = client;
        this.responseStat = null;
        this.watching = new Watching();
        this.backgrounding = new Backgrounding();
        this.decompress = false;
    }
    
    @Override
    public GetDataWatchBackgroundStatable decompressed() {
        this.decompress = true;
        return new GetDataWatchBackgroundStatable() {
            @Override
            public ErrorListenerPathable<byte[]> inBackground() {
                return GetDataBuilderImpl.this.inBackground();
            }
            
            @Override
            public ErrorListenerPathable<byte[]> inBackground(final BackgroundCallback callback, final Object context) {
                return GetDataBuilderImpl.this.inBackground(callback, context);
            }
            
            @Override
            public ErrorListenerPathable<byte[]> inBackground(final BackgroundCallback callback, final Object context, final Executor executor) {
                return GetDataBuilderImpl.this.inBackground(callback, context, executor);
            }
            
            @Override
            public ErrorListenerPathable<byte[]> inBackground(final Object context) {
                return GetDataBuilderImpl.this.inBackground(context);
            }
            
            @Override
            public ErrorListenerPathable<byte[]> inBackground(final BackgroundCallback callback) {
                return GetDataBuilderImpl.this.inBackground(callback);
            }
            
            @Override
            public ErrorListenerPathable<byte[]> inBackground(final BackgroundCallback callback, final Executor executor) {
                return GetDataBuilderImpl.this.inBackground(callback, executor);
            }
            
            @Override
            public byte[] forPath(final String path) throws Exception {
                return GetDataBuilderImpl.this.forPath(path);
            }
            
            @Override
            public WatchPathable<byte[]> storingStatIn(final Stat stat) {
                return GetDataBuilderImpl.this.storingStatIn(stat);
            }
            
            @Override
            public BackgroundPathable<byte[]> watched() {
                return GetDataBuilderImpl.this.watched();
            }
            
            @Override
            public BackgroundPathable<byte[]> usingWatcher(final Watcher watcher) {
                return GetDataBuilderImpl.this.usingWatcher(watcher);
            }
            
            @Override
            public BackgroundPathable<byte[]> usingWatcher(final CuratorWatcher watcher) {
                return GetDataBuilderImpl.this.usingWatcher(watcher);
            }
        };
    }
    
    @Override
    public WatchPathable<byte[]> storingStatIn(final Stat stat) {
        this.responseStat = stat;
        return new WatchPathable<byte[]>() {
            @Override
            public byte[] forPath(final String path) throws Exception {
                return GetDataBuilderImpl.this.forPath(path);
            }
            
            @Override
            public Pathable<byte[]> watched() {
                GetDataBuilderImpl.this.watched();
                return GetDataBuilderImpl.this;
            }
            
            @Override
            public Pathable<byte[]> usingWatcher(final Watcher watcher) {
                GetDataBuilderImpl.this.usingWatcher(watcher);
                return GetDataBuilderImpl.this;
            }
            
            @Override
            public Pathable<byte[]> usingWatcher(final CuratorWatcher watcher) {
                GetDataBuilderImpl.this.usingWatcher(watcher);
                return GetDataBuilderImpl.this;
            }
        };
    }
    
    @Override
    public ErrorListenerPathable<byte[]> inBackground(final BackgroundCallback callback, final Object context) {
        this.backgrounding = new Backgrounding(callback, context);
        return this;
    }
    
    @Override
    public ErrorListenerPathable<byte[]> inBackground(final BackgroundCallback callback, final Object context, final Executor executor) {
        this.backgrounding = new Backgrounding(this.client, callback, context, executor);
        return this;
    }
    
    @Override
    public ErrorListenerPathable<byte[]> inBackground(final BackgroundCallback callback) {
        this.backgrounding = new Backgrounding(callback);
        return this;
    }
    
    @Override
    public ErrorListenerPathable<byte[]> inBackground(final BackgroundCallback callback, final Executor executor) {
        this.backgrounding = new Backgrounding(this.client, callback, executor);
        return this;
    }
    
    @Override
    public ErrorListenerPathable<byte[]> inBackground() {
        this.backgrounding = new Backgrounding(true);
        return this;
    }
    
    @Override
    public ErrorListenerPathable<byte[]> inBackground(final Object context) {
        this.backgrounding = new Backgrounding(context);
        return this;
    }
    
    @Override
    public Pathable<byte[]> withUnhandledErrorListener(final UnhandledErrorListener listener) {
        this.backgrounding = new Backgrounding(this.backgrounding, listener);
        return this;
    }
    
    @Override
    public BackgroundPathable<byte[]> watched() {
        this.watching = new Watching(true);
        return this;
    }
    
    @Override
    public BackgroundPathable<byte[]> usingWatcher(final Watcher watcher) {
        this.watching = new Watching(this.client, watcher);
        return this;
    }
    
    @Override
    public BackgroundPathable<byte[]> usingWatcher(final CuratorWatcher watcher) {
        this.watching = new Watching(this.client, watcher);
        return this;
    }
    
    @Override
    public void performBackgroundOperation(final OperationAndData<String> operationAndData) throws Exception {
        try {
            final OperationTrace trace = this.client.getZookeeperClient().startAdvancedTracer("GetDataBuilderImpl-Background");
            final AsyncCallback.DataCallback callback = new AsyncCallback.DataCallback() {
                @Override
                public void processResult(int rc, final String path, final Object ctx, byte[] data, final Stat stat) {
                    trace.setReturnCode(rc).setResponseBytesLength(data).setPath(path).setWithWatcher(GetDataBuilderImpl.this.watching.getWatcher() != null).setStat(stat).commit();
                    if (GetDataBuilderImpl.this.decompress && data != null) {
                        try {
                            data = GetDataBuilderImpl.this.client.getCompressionProvider().decompress(path, data);
                        }
                        catch (Exception e) {
                            ThreadUtils.checkInterrupted(e);
                            GetDataBuilderImpl.this.log.error("Decompressing for path: " + path, e);
                            rc = KeeperException.Code.DATAINCONSISTENCY.intValue();
                        }
                    }
                    final CuratorEvent event = new CuratorEventImpl(GetDataBuilderImpl.this.client, CuratorEventType.GET_DATA, rc, path, null, ctx, stat, data, null, null, null);
                    GetDataBuilderImpl.this.client.processBackgroundOperation((OperationAndData<Object>)operationAndData, event);
                }
            };
            if (this.watching.isWatched()) {
                this.client.getZooKeeper().getData(operationAndData.getData(), true, callback, this.backgrounding.getContext());
            }
            else {
                this.client.getZooKeeper().getData(operationAndData.getData(), this.watching.getWatcher(), callback, this.backgrounding.getContext());
            }
        }
        catch (Throwable e) {
            this.backgrounding.checkError(e);
        }
    }
    
    @Override
    public byte[] forPath(String path) throws Exception {
        path = this.client.fixForNamespace(path);
        byte[] responseData = null;
        if (this.backgrounding.inBackground()) {
            this.client.processBackgroundOperation(new OperationAndData<Object>((BackgroundOperation<Object>)this, path, this.backgrounding.getCallback(), null, this.backgrounding.getContext()), null);
        }
        else {
            responseData = this.pathInForeground(path);
        }
        return responseData;
    }
    
    private byte[] pathInForeground(final String path) throws Exception {
        final OperationTrace trace = this.client.getZookeeperClient().startAdvancedTracer("GetDataBuilderImpl-Foreground");
        final byte[] responseData = RetryLoop.callWithRetry(this.client.getZookeeperClient(), (Callable<byte[]>)new Callable<byte[]>() {
            @Override
            public byte[] call() throws Exception {
                byte[] responseData;
                if (GetDataBuilderImpl.this.watching.isWatched()) {
                    responseData = GetDataBuilderImpl.this.client.getZooKeeper().getData(path, true, GetDataBuilderImpl.this.responseStat);
                }
                else {
                    responseData = GetDataBuilderImpl.this.client.getZooKeeper().getData(path, GetDataBuilderImpl.this.watching.getWatcher(), GetDataBuilderImpl.this.responseStat);
                }
                return responseData;
            }
        });
        trace.setResponseBytesLength(responseData).setPath(path).setWithWatcher(this.watching.getWatcher() != null).setStat(this.responseStat).commit();
        return this.decompress ? this.client.getCompressionProvider().decompress(path, responseData) : responseData;
    }
}
