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
import org.apache.curator.drivers.OperationTrace;
import org.apache.zookeeper.AsyncCallback;
import org.apache.curator.framework.api.UnhandledErrorListener;
import org.apache.curator.framework.api.BackgroundPathAndBytesable;
import java.util.concurrent.Executor;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.SetDataBackgroundVersionable;
import org.apache.curator.framework.api.VersionPathAndBytesable;
import org.apache.curator.framework.api.PathAndBytesable;
import org.apache.curator.framework.api.transaction.OperationType;
import org.apache.zookeeper.Op;
import org.apache.curator.framework.api.transaction.CuratorTransactionBridge;
import org.apache.curator.framework.api.transaction.TransactionSetDataBuilder;
import org.apache.zookeeper.data.Stat;
import org.apache.curator.framework.api.ErrorListenerPathAndBytesable;
import org.apache.curator.framework.api.SetDataBuilder;

class SetDataBuilderImpl implements SetDataBuilder, BackgroundOperation<PathAndBytes>, ErrorListenerPathAndBytesable<Stat>
{
    private final CuratorFrameworkImpl client;
    private Backgrounding backgrounding;
    private int version;
    private boolean compress;
    
    SetDataBuilderImpl(final CuratorFrameworkImpl client) {
        this.client = client;
        this.backgrounding = new Backgrounding();
        this.version = -1;
        this.compress = false;
    }
    
    TransactionSetDataBuilder asTransactionSetDataBuilder(final CuratorTransactionImpl curatorTransaction, final CuratorMultiTransactionRecord transaction) {
        return new TransactionSetDataBuilder() {
            @Override
            public CuratorTransactionBridge forPath(final String path, byte[] data) throws Exception {
                if (SetDataBuilderImpl.this.compress) {
                    data = SetDataBuilderImpl.this.client.getCompressionProvider().compress(path, data);
                }
                final String fixedPath = SetDataBuilderImpl.this.client.fixForNamespace(path);
                transaction.add(Op.setData(fixedPath, data, SetDataBuilderImpl.this.version), OperationType.SET_DATA, path);
                return curatorTransaction;
            }
            
            @Override
            public CuratorTransactionBridge forPath(final String path) throws Exception {
                return this.forPath(path, SetDataBuilderImpl.this.client.getDefaultData());
            }
            
            @Override
            public PathAndBytesable<CuratorTransactionBridge> withVersion(final int version) {
                SetDataBuilderImpl.this.withVersion(version);
                return this;
            }
            
            @Override
            public VersionPathAndBytesable<CuratorTransactionBridge> compressed() {
                SetDataBuilderImpl.this.compress = true;
                return this;
            }
        };
    }
    
    @Override
    public SetDataBackgroundVersionable compressed() {
        this.compress = true;
        return new SetDataBackgroundVersionable() {
            @Override
            public ErrorListenerPathAndBytesable<Stat> inBackground() {
                return SetDataBuilderImpl.this.inBackground();
            }
            
            @Override
            public ErrorListenerPathAndBytesable<Stat> inBackground(final BackgroundCallback callback, final Object context) {
                return SetDataBuilderImpl.this.inBackground(callback, context);
            }
            
            @Override
            public ErrorListenerPathAndBytesable<Stat> inBackground(final BackgroundCallback callback, final Object context, final Executor executor) {
                return SetDataBuilderImpl.this.inBackground(callback, context, executor);
            }
            
            @Override
            public ErrorListenerPathAndBytesable<Stat> inBackground(final Object context) {
                return SetDataBuilderImpl.this.inBackground(context);
            }
            
            @Override
            public ErrorListenerPathAndBytesable<Stat> inBackground(final BackgroundCallback callback) {
                return SetDataBuilderImpl.this.inBackground(callback);
            }
            
            @Override
            public ErrorListenerPathAndBytesable<Stat> inBackground(final BackgroundCallback callback, final Executor executor) {
                return SetDataBuilderImpl.this.inBackground(callback, executor);
            }
            
            @Override
            public Stat forPath(final String path, final byte[] data) throws Exception {
                return SetDataBuilderImpl.this.forPath(path, data);
            }
            
            @Override
            public Stat forPath(final String path) throws Exception {
                return SetDataBuilderImpl.this.forPath(path);
            }
            
            @Override
            public BackgroundPathAndBytesable<Stat> withVersion(final int version) {
                return SetDataBuilderImpl.this.withVersion(version);
            }
        };
    }
    
    @Override
    public BackgroundPathAndBytesable<Stat> withVersion(final int version) {
        this.version = version;
        return this;
    }
    
    @Override
    public ErrorListenerPathAndBytesable<Stat> inBackground(final BackgroundCallback callback, final Object context) {
        this.backgrounding = new Backgrounding(callback, context);
        return this;
    }
    
    @Override
    public ErrorListenerPathAndBytesable<Stat> inBackground(final BackgroundCallback callback, final Object context, final Executor executor) {
        this.backgrounding = new Backgrounding(this.client, callback, context, executor);
        return this;
    }
    
    @Override
    public ErrorListenerPathAndBytesable<Stat> inBackground(final BackgroundCallback callback) {
        this.backgrounding = new Backgrounding(callback);
        return this;
    }
    
    @Override
    public ErrorListenerPathAndBytesable<Stat> inBackground() {
        this.backgrounding = new Backgrounding(true);
        return this;
    }
    
    @Override
    public ErrorListenerPathAndBytesable<Stat> inBackground(final Object context) {
        this.backgrounding = new Backgrounding(context);
        return this;
    }
    
    @Override
    public ErrorListenerPathAndBytesable<Stat> inBackground(final BackgroundCallback callback, final Executor executor) {
        this.backgrounding = new Backgrounding(this.client, callback, executor);
        return this;
    }
    
    @Override
    public PathAndBytesable<Stat> withUnhandledErrorListener(final UnhandledErrorListener listener) {
        this.backgrounding = new Backgrounding(this.backgrounding, listener);
        return this;
    }
    
    @Override
    public void performBackgroundOperation(final OperationAndData<PathAndBytes> operationAndData) throws Exception {
        try {
            final OperationTrace trace = this.client.getZookeeperClient().startAdvancedTracer("SetDataBuilderImpl-Background");
            final byte[] data = operationAndData.getData().getData();
            this.client.getZooKeeper().setData(operationAndData.getData().getPath(), data, this.version, new AsyncCallback.StatCallback() {
                @Override
                public void processResult(final int rc, final String path, final Object ctx, final Stat stat) {
                    trace.setReturnCode(rc).setRequestBytesLength(data).setPath(path).setStat(stat).commit();
                    final CuratorEvent event = new CuratorEventImpl(SetDataBuilderImpl.this.client, CuratorEventType.SET_DATA, rc, path, null, ctx, stat, null, null, null, null);
                    SetDataBuilderImpl.this.client.processBackgroundOperation((OperationAndData<Object>)operationAndData, event);
                }
            }, this.backgrounding.getContext());
        }
        catch (Throwable e) {
            this.backgrounding.checkError(e);
        }
    }
    
    @Override
    public Stat forPath(final String path) throws Exception {
        return this.forPath(path, this.client.getDefaultData());
    }
    
    @Override
    public Stat forPath(String path, byte[] data) throws Exception {
        if (this.compress) {
            data = this.client.getCompressionProvider().compress(path, data);
        }
        path = this.client.fixForNamespace(path);
        Stat resultStat = null;
        if (this.backgrounding.inBackground()) {
            this.client.processBackgroundOperation(new OperationAndData<Object>((BackgroundOperation<Object>)this, new PathAndBytes(path, data), this.backgrounding.getCallback(), null, this.backgrounding.getContext()), null);
        }
        else {
            resultStat = this.pathInForeground(path, data);
        }
        return resultStat;
    }
    
    int getVersion() {
        return this.version;
    }
    
    private Stat pathInForeground(final String path, final byte[] data) throws Exception {
        final OperationTrace trace = this.client.getZookeeperClient().startAdvancedTracer("SetDataBuilderImpl-Foreground");
        final Stat resultStat = RetryLoop.callWithRetry(this.client.getZookeeperClient(), (Callable<Stat>)new Callable<Stat>() {
            @Override
            public Stat call() throws Exception {
                return SetDataBuilderImpl.this.client.getZooKeeper().setData(path, data, SetDataBuilderImpl.this.version);
            }
        });
        trace.setRequestBytesLength(data).setPath(path).setStat(resultStat).commit();
        return resultStat;
    }
}
