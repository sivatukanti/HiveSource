// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.imps;

import org.apache.curator.utils.ThreadUtils;
import org.apache.curator.RetryLoop;
import java.util.concurrent.Callable;
import org.apache.curator.utils.ZKPaths;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.WatchedEvent;
import java.util.List;
import org.apache.zookeeper.data.Stat;
import org.apache.curator.framework.api.CuratorEventType;
import org.apache.zookeeper.KeeperException;
import org.apache.curator.drivers.OperationTrace;
import org.apache.zookeeper.AsyncCallback;
import org.apache.curator.framework.api.UnhandledErrorListener;
import java.util.concurrent.Executor;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.BackgroundPathable;
import org.apache.curator.framework.api.BackgroundVersionable;
import org.apache.curator.framework.api.ChildrenDeletable;
import org.apache.curator.framework.api.Pathable;
import org.apache.curator.framework.api.transaction.OperationType;
import org.apache.zookeeper.Op;
import org.apache.curator.framework.api.transaction.CuratorTransactionBridge;
import org.apache.curator.framework.api.transaction.TransactionDeleteBuilder;
import org.apache.curator.framework.api.ErrorListenerPathable;
import org.apache.curator.framework.api.DeleteBuilder;

class DeleteBuilderImpl implements DeleteBuilder, BackgroundOperation<String>, ErrorListenerPathable<Void>
{
    private final CuratorFrameworkImpl client;
    private int version;
    private Backgrounding backgrounding;
    private boolean deletingChildrenIfNeeded;
    private boolean guaranteed;
    
    DeleteBuilderImpl(final CuratorFrameworkImpl client) {
        this.client = client;
        this.version = -1;
        this.backgrounding = new Backgrounding();
        this.deletingChildrenIfNeeded = false;
        this.guaranteed = false;
    }
    
    TransactionDeleteBuilder asTransactionDeleteBuilder(final CuratorTransactionImpl curatorTransaction, final CuratorMultiTransactionRecord transaction) {
        return new TransactionDeleteBuilder() {
            @Override
            public CuratorTransactionBridge forPath(final String path) throws Exception {
                final String fixedPath = DeleteBuilderImpl.this.client.fixForNamespace(path);
                transaction.add(Op.delete(fixedPath, DeleteBuilderImpl.this.version), OperationType.DELETE, path);
                return curatorTransaction;
            }
            
            @Override
            public Pathable<CuratorTransactionBridge> withVersion(final int version) {
                DeleteBuilderImpl.this.withVersion(version);
                return this;
            }
        };
    }
    
    @Override
    public ChildrenDeletable guaranteed() {
        this.guaranteed = true;
        return this;
    }
    
    @Override
    public BackgroundVersionable deletingChildrenIfNeeded() {
        this.deletingChildrenIfNeeded = true;
        return this;
    }
    
    @Override
    public BackgroundPathable<Void> withVersion(final int version) {
        this.version = version;
        return this;
    }
    
    @Override
    public ErrorListenerPathable<Void> inBackground(final BackgroundCallback callback, final Object context) {
        this.backgrounding = new Backgrounding(callback, context);
        return this;
    }
    
    @Override
    public ErrorListenerPathable<Void> inBackground(final BackgroundCallback callback, final Object context, final Executor executor) {
        this.backgrounding = new Backgrounding(this.client, callback, context, executor);
        return this;
    }
    
    @Override
    public ErrorListenerPathable<Void> inBackground(final BackgroundCallback callback) {
        this.backgrounding = new Backgrounding(callback);
        return this;
    }
    
    @Override
    public ErrorListenerPathable<Void> inBackground(final BackgroundCallback callback, final Executor executor) {
        this.backgrounding = new Backgrounding(this.client, callback, executor);
        return this;
    }
    
    @Override
    public ErrorListenerPathable<Void> inBackground() {
        this.backgrounding = new Backgrounding(true);
        return this;
    }
    
    @Override
    public ErrorListenerPathable<Void> inBackground(final Object context) {
        this.backgrounding = new Backgrounding(context);
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
            final OperationTrace trace = this.client.getZookeeperClient().startAdvancedTracer("DeleteBuilderImpl-Background");
            this.client.getZooKeeper().delete(operationAndData.getData(), this.version, new AsyncCallback.VoidCallback() {
                @Override
                public void processResult(final int rc, final String path, final Object ctx) {
                    trace.setReturnCode(rc).setPath(path).commit();
                    if (rc == KeeperException.Code.NOTEMPTY.intValue() && DeleteBuilderImpl.this.deletingChildrenIfNeeded) {
                        DeleteBuilderImpl.this.backgroundDeleteChildrenThenNode(operationAndData);
                    }
                    else {
                        final CuratorEvent event = new CuratorEventImpl(DeleteBuilderImpl.this.client, CuratorEventType.DELETE, rc, path, null, ctx, null, null, null, null, null);
                        DeleteBuilderImpl.this.client.processBackgroundOperation((OperationAndData<Object>)operationAndData, event);
                    }
                }
            }, this.backgrounding.getContext());
        }
        catch (Throwable e) {
            this.backgrounding.checkError(e);
        }
    }
    
    private void backgroundDeleteChildrenThenNode(final OperationAndData<String> mainOperationAndData) {
        final BackgroundOperation<String> operation = new BackgroundOperation<String>() {
            @Override
            public void performBackgroundOperation(final OperationAndData<String> dummy) throws Exception {
                try {
                    ZKPaths.deleteChildren(DeleteBuilderImpl.this.client.getZooKeeper(), mainOperationAndData.getData(), false);
                }
                catch (KeeperException ex) {}
                DeleteBuilderImpl.this.client.queueOperation((OperationAndData<Object>)mainOperationAndData);
            }
        };
        final OperationAndData<String> parentOperation = new OperationAndData<String>(operation, mainOperationAndData.getData(), null, null, this.backgrounding.getContext());
        this.client.queueOperation(parentOperation);
    }
    
    @Override
    public Void forPath(String path) throws Exception {
        final String unfixedPath = path;
        path = this.client.fixForNamespace(path);
        if (this.backgrounding.inBackground()) {
            OperationAndData.ErrorCallback<String> errorCallback = null;
            if (this.guaranteed) {
                errorCallback = new OperationAndData.ErrorCallback<String>() {
                    @Override
                    public void retriesExhausted(final OperationAndData<String> operationAndData) {
                        DeleteBuilderImpl.this.client.getFailedDeleteManager().addFailedDelete(unfixedPath);
                    }
                };
            }
            this.client.processBackgroundOperation(new OperationAndData<Object>((BackgroundOperation<Object>)this, path, this.backgrounding.getCallback(), (OperationAndData.ErrorCallback<Object>)errorCallback, this.backgrounding.getContext()), null);
        }
        else {
            this.pathInForeground(path, unfixedPath);
        }
        return null;
    }
    
    protected int getVersion() {
        return this.version;
    }
    
    private void pathInForeground(final String path, final String unfixedPath) throws Exception {
        final OperationTrace trace = this.client.getZookeeperClient().startAdvancedTracer("DeleteBuilderImpl-Foreground");
        try {
            RetryLoop.callWithRetry(this.client.getZookeeperClient(), (Callable<Object>)new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    try {
                        DeleteBuilderImpl.this.client.getZooKeeper().delete(path, DeleteBuilderImpl.this.version);
                    }
                    catch (KeeperException.NotEmptyException e) {
                        if (!DeleteBuilderImpl.this.deletingChildrenIfNeeded) {
                            throw e;
                        }
                        ZKPaths.deleteChildren(DeleteBuilderImpl.this.client.getZooKeeper(), path, true);
                    }
                    return null;
                }
            });
        }
        catch (Exception e) {
            ThreadUtils.checkInterrupted(e);
            if ((RetryLoop.isRetryException(e) || e instanceof InterruptedException) && this.guaranteed) {
                this.client.getFailedDeleteManager().addFailedDelete(unfixedPath);
            }
            throw e;
        }
        trace.setPath(path).commit();
    }
}
