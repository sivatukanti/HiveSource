// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.imps;

import org.apache.curator.framework.api.Backgroundable;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.WatchedEvent;
import org.apache.curator.framework.api.CuratorEventType;
import org.apache.curator.utils.ThreadUtils;
import org.apache.curator.framework.api.ErrorListenerPathable;
import org.apache.curator.utils.ZKPaths;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.data.Stat;
import java.util.List;
import org.apache.curator.drivers.OperationTrace;
import org.apache.zookeeper.AsyncCallback;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.CuratorEvent;
import org.slf4j.LoggerFactory;
import org.apache.curator.shaded.com.google.common.annotations.VisibleForTesting;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;

class FindAndDeleteProtectedNodeInBackground implements BackgroundOperation<Void>
{
    private final Logger log;
    private final CuratorFrameworkImpl client;
    private final String namespaceAdjustedParentPath;
    private final String protectedId;
    @VisibleForTesting
    static final AtomicBoolean debugInsertError;
    
    FindAndDeleteProtectedNodeInBackground(final CuratorFrameworkImpl client, final String namespaceAdjustedParentPath, final String protectedId) {
        this.log = LoggerFactory.getLogger(this.getClass());
        this.client = client;
        this.namespaceAdjustedParentPath = namespaceAdjustedParentPath;
        this.protectedId = protectedId;
    }
    
    void execute() {
        final OperationAndData.ErrorCallback<Void> errorCallback = new OperationAndData.ErrorCallback<Void>() {
            @Override
            public void retriesExhausted(final OperationAndData<Void> operationAndData) {
                operationAndData.reset();
                FindAndDeleteProtectedNodeInBackground.this.client.processBackgroundOperation(operationAndData, null);
            }
        };
        final OperationAndData<Void> operationAndData = new OperationAndData<Void>(this, null, null, errorCallback, null);
        this.client.processBackgroundOperation(operationAndData, null);
    }
    
    @Override
    public void performBackgroundOperation(final OperationAndData<Void> operationAndData) throws Exception {
        final OperationTrace trace = this.client.getZookeeperClient().startAdvancedTracer("FindAndDeleteProtectedNodeInBackground");
        final AsyncCallback.Children2Callback callback = new AsyncCallback.Children2Callback() {
            @Override
            public void processResult(int rc, final String path, final Object o, final List<String> strings, final Stat stat) {
                trace.setReturnCode(rc).setPath(path).setStat(stat).commit();
                if (FindAndDeleteProtectedNodeInBackground.debugInsertError.compareAndSet(true, false)) {
                    rc = KeeperException.Code.CONNECTIONLOSS.intValue();
                }
                if (rc == KeeperException.Code.OK.intValue()) {
                    final String node = CreateBuilderImpl.findNode(strings, "/", FindAndDeleteProtectedNodeInBackground.this.protectedId);
                    if (node != null) {
                        try {
                            final String deletePath = FindAndDeleteProtectedNodeInBackground.this.client.unfixForNamespace(ZKPaths.makePath(FindAndDeleteProtectedNodeInBackground.this.namespaceAdjustedParentPath, node));
                            ((Backgroundable<ErrorListenerPathable>)FindAndDeleteProtectedNodeInBackground.this.client.delete().guaranteed()).inBackground().forPath(deletePath);
                        }
                        catch (Exception e) {
                            ThreadUtils.checkInterrupted(e);
                            FindAndDeleteProtectedNodeInBackground.this.log.error("Could not start guaranteed delete for node: " + node);
                            rc = KeeperException.Code.CONNECTIONLOSS.intValue();
                        }
                    }
                }
                if (rc != KeeperException.Code.OK.intValue()) {
                    final CuratorEventImpl event = new CuratorEventImpl(FindAndDeleteProtectedNodeInBackground.this.client, CuratorEventType.CHILDREN, rc, path, null, o, stat, null, strings, null, null);
                    FindAndDeleteProtectedNodeInBackground.this.client.processBackgroundOperation((OperationAndData<Object>)operationAndData, event);
                }
            }
        };
        this.client.getZooKeeper().getChildren(this.namespaceAdjustedParentPath, false, callback, null);
    }
    
    static {
        debugInsertError = new AtomicBoolean(false);
    }
}
