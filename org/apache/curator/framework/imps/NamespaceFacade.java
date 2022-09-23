// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.imps;

import org.apache.curator.utils.EnsurePath;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.zookeeper.ZooKeeper;
import org.apache.curator.RetryLoop;
import org.apache.curator.CuratorZookeeperClient;
import org.apache.curator.framework.api.UnhandledErrorListener;
import org.apache.curator.framework.api.CuratorListener;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.framework.listen.Listenable;
import org.apache.curator.framework.CuratorFramework;

class NamespaceFacade extends CuratorFrameworkImpl
{
    private final CuratorFrameworkImpl client;
    private final NamespaceImpl namespace;
    private final FailedDeleteManager failedDeleteManager;
    
    NamespaceFacade(final CuratorFrameworkImpl client, final String namespace) {
        super(client);
        this.failedDeleteManager = new FailedDeleteManager(this);
        this.client = client;
        this.namespace = new NamespaceImpl(client, namespace);
    }
    
    @Override
    public CuratorFramework nonNamespaceView() {
        return this.usingNamespace(null);
    }
    
    @Override
    public CuratorFramework usingNamespace(final String newNamespace) {
        return this.client.getNamespaceFacadeCache().get(newNamespace);
    }
    
    @Override
    public String getNamespace() {
        return this.namespace.getNamespace();
    }
    
    @Override
    public void start() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void close() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public Listenable<ConnectionStateListener> getConnectionStateListenable() {
        return this.client.getConnectionStateListenable();
    }
    
    @Override
    public Listenable<CuratorListener> getCuratorListenable() {
        throw new UnsupportedOperationException("getCuratorListenable() is only available from a non-namespaced CuratorFramework instance");
    }
    
    @Override
    public Listenable<UnhandledErrorListener> getUnhandledErrorListenable() {
        return this.client.getUnhandledErrorListenable();
    }
    
    @Override
    public void sync(final String path, final Object context) {
        this.internalSync(this, path, context);
    }
    
    @Override
    public CuratorZookeeperClient getZookeeperClient() {
        return this.client.getZookeeperClient();
    }
    
    @Override
    RetryLoop newRetryLoop() {
        return this.client.newRetryLoop();
    }
    
    @Override
    ZooKeeper getZooKeeper() throws Exception {
        return this.client.getZooKeeper();
    }
    
    @Override
     <DATA_TYPE> void processBackgroundOperation(final OperationAndData<DATA_TYPE> operationAndData, final CuratorEvent event) {
        this.client.processBackgroundOperation(operationAndData, event);
    }
    
    @Override
    void logError(final String reason, final Throwable e) {
        this.client.logError(reason, e);
    }
    
    @Override
    String unfixForNamespace(final String path) {
        return this.namespace.unfixForNamespace(path);
    }
    
    @Override
    String fixForNamespace(final String path) {
        return this.namespace.fixForNamespace(path, false);
    }
    
    @Override
    String fixForNamespace(final String path, final boolean isSequential) {
        return this.namespace.fixForNamespace(path, isSequential);
    }
    
    @Override
    public EnsurePath newNamespaceAwareEnsurePath(final String path) {
        return this.namespace.newNamespaceAwareEnsurePath(path);
    }
    
    @Override
    FailedDeleteManager getFailedDeleteManager() {
        return this.failedDeleteManager;
    }
}
