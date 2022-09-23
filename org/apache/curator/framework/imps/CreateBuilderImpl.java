// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.imps;

import org.apache.curator.shaded.com.google.common.collect.Iterables;
import com.google.common.base.Predicate;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.data.Stat;
import org.apache.curator.framework.api.CuratorEventType;
import org.apache.curator.RetryLoop;
import org.apache.curator.utils.InternalACLProvider;
import org.apache.curator.drivers.OperationTrace;
import org.apache.zookeeper.AsyncCallback;
import java.util.UUID;
import org.apache.curator.utils.ZKPaths;
import org.apache.zookeeper.KeeperException;
import org.apache.curator.utils.ThreadUtils;
import org.apache.curator.framework.api.UnhandledErrorListener;
import org.apache.curator.framework.api.ACLCreateModeBackgroundPathAndBytesable;
import org.apache.curator.framework.api.ProtectACLCreateModePathAndBytesable;
import org.apache.curator.framework.api.ACLBackgroundPathAndBytesable;
import java.util.concurrent.Executor;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.BackgroundPathAndBytesable;
import org.apache.curator.framework.api.CreateBackgroundModeACLable;
import org.apache.curator.framework.api.transaction.OperationType;
import org.apache.zookeeper.Op;
import org.apache.curator.framework.api.ACLCreateModePathAndBytesable;
import org.apache.curator.framework.api.ACLPathAndBytesable;
import org.apache.curator.framework.api.transaction.CuratorTransactionBridge;
import org.apache.curator.framework.api.PathAndBytesable;
import org.apache.zookeeper.data.ACL;
import java.util.List;
import org.apache.curator.framework.api.transaction.TransactionCreateBuilder;
import org.apache.curator.shaded.com.google.common.annotations.VisibleForTesting;
import org.apache.zookeeper.CreateMode;
import org.apache.curator.framework.api.ErrorListenerPathAndBytesable;
import org.apache.curator.framework.api.CreateBuilder;

class CreateBuilderImpl implements CreateBuilder, BackgroundOperation<PathAndBytes>, ErrorListenerPathAndBytesable<String>
{
    private final CuratorFrameworkImpl client;
    private CreateMode createMode;
    private Backgrounding backgrounding;
    private boolean createParentsIfNeeded;
    private boolean createParentsAsContainers;
    private boolean doProtected;
    private boolean compress;
    private String protectedId;
    private ACLing acling;
    @VisibleForTesting
    boolean failNextCreateForTesting;
    @VisibleForTesting
    static final String PROTECTED_PREFIX = "_c_";
    @VisibleForTesting
    volatile boolean debugForceFindProtectedNode;
    
    CreateBuilderImpl(final CuratorFrameworkImpl client) {
        this.failNextCreateForTesting = false;
        this.debugForceFindProtectedNode = false;
        this.client = client;
        this.createMode = CreateMode.PERSISTENT;
        this.backgrounding = new Backgrounding();
        this.acling = new ACLing(client.getAclProvider());
        this.createParentsIfNeeded = false;
        this.createParentsAsContainers = false;
        this.compress = false;
        this.doProtected = false;
        this.protectedId = null;
    }
    
    TransactionCreateBuilder asTransactionCreateBuilder(final CuratorTransactionImpl curatorTransaction, final CuratorMultiTransactionRecord transaction) {
        return new TransactionCreateBuilder() {
            @Override
            public PathAndBytesable<CuratorTransactionBridge> withACL(final List<ACL> aclList) {
                CreateBuilderImpl.this.withACL(aclList);
                return this;
            }
            
            @Override
            public ACLPathAndBytesable<CuratorTransactionBridge> withMode(final CreateMode mode) {
                CreateBuilderImpl.this.withMode(mode);
                return this;
            }
            
            @Override
            public ACLCreateModePathAndBytesable<CuratorTransactionBridge> compressed() {
                CreateBuilderImpl.this.compressed();
                return this;
            }
            
            @Override
            public CuratorTransactionBridge forPath(final String path) throws Exception {
                return this.forPath(path, CreateBuilderImpl.this.client.getDefaultData());
            }
            
            @Override
            public CuratorTransactionBridge forPath(final String path, byte[] data) throws Exception {
                if (CreateBuilderImpl.this.compress) {
                    data = CreateBuilderImpl.this.client.getCompressionProvider().compress(path, data);
                }
                final String fixedPath = CreateBuilderImpl.this.client.fixForNamespace(path);
                transaction.add(Op.create(fixedPath, data, CreateBuilderImpl.this.acling.getAclList(path), CreateBuilderImpl.this.createMode), OperationType.CREATE, path);
                return curatorTransaction;
            }
        };
    }
    
    @Override
    public CreateBackgroundModeACLable compressed() {
        this.compress = true;
        return new CreateBackgroundModeACLable() {
            @Override
            public ACLCreateModePathAndBytesable<String> creatingParentsIfNeeded() {
                CreateBuilderImpl.this.createParentsIfNeeded = true;
                return CreateBuilderImpl.this.asACLCreateModePathAndBytesable();
            }
            
            @Override
            public ACLCreateModePathAndBytesable<String> creatingParentContainersIfNeeded() {
                CreateBuilderImpl.this.setCreateParentsAsContainers();
                return this.creatingParentsIfNeeded();
            }
            
            @Override
            public ACLPathAndBytesable<String> withProtectedEphemeralSequential() {
                return CreateBuilderImpl.this.withProtectedEphemeralSequential();
            }
            
            @Override
            public BackgroundPathAndBytesable<String> withACL(final List<ACL> aclList) {
                return CreateBuilderImpl.this.withACL(aclList);
            }
            
            @Override
            public ErrorListenerPathAndBytesable<String> inBackground(final BackgroundCallback callback, final Object context) {
                return CreateBuilderImpl.this.inBackground(callback, context);
            }
            
            @Override
            public ErrorListenerPathAndBytesable<String> inBackground(final BackgroundCallback callback, final Object context, final Executor executor) {
                return CreateBuilderImpl.this.inBackground(callback, context, executor);
            }
            
            @Override
            public ErrorListenerPathAndBytesable<String> inBackground() {
                return CreateBuilderImpl.this.inBackground();
            }
            
            @Override
            public ErrorListenerPathAndBytesable<String> inBackground(final Object context) {
                return CreateBuilderImpl.this.inBackground(context);
            }
            
            @Override
            public ErrorListenerPathAndBytesable<String> inBackground(final BackgroundCallback callback) {
                return CreateBuilderImpl.this.inBackground(callback);
            }
            
            @Override
            public ErrorListenerPathAndBytesable<String> inBackground(final BackgroundCallback callback, final Executor executor) {
                return CreateBuilderImpl.this.inBackground(callback, executor);
            }
            
            @Override
            public ACLBackgroundPathAndBytesable<String> withMode(final CreateMode mode) {
                return CreateBuilderImpl.this.withMode(mode);
            }
            
            @Override
            public String forPath(final String path, final byte[] data) throws Exception {
                return CreateBuilderImpl.this.forPath(path, data);
            }
            
            @Override
            public String forPath(final String path) throws Exception {
                return CreateBuilderImpl.this.forPath(path);
            }
        };
    }
    
    @Override
    public ACLBackgroundPathAndBytesable<String> withACL(final List<ACL> aclList) {
        this.acling = new ACLing(this.client.getAclProvider(), aclList);
        return new ACLBackgroundPathAndBytesable<String>() {
            @Override
            public BackgroundPathAndBytesable<String> withACL(final List<ACL> aclList) {
                return CreateBuilderImpl.this.withACL(aclList);
            }
            
            @Override
            public ErrorListenerPathAndBytesable<String> inBackground() {
                return CreateBuilderImpl.this.inBackground();
            }
            
            @Override
            public ErrorListenerPathAndBytesable<String> inBackground(final BackgroundCallback callback, final Object context) {
                return CreateBuilderImpl.this.inBackground(callback, context);
            }
            
            @Override
            public ErrorListenerPathAndBytesable<String> inBackground(final BackgroundCallback callback, final Object context, final Executor executor) {
                return CreateBuilderImpl.this.inBackground(callback, context, executor);
            }
            
            @Override
            public ErrorListenerPathAndBytesable<String> inBackground(final Object context) {
                return CreateBuilderImpl.this.inBackground(context);
            }
            
            @Override
            public ErrorListenerPathAndBytesable<String> inBackground(final BackgroundCallback callback) {
                return CreateBuilderImpl.this.inBackground(callback);
            }
            
            @Override
            public ErrorListenerPathAndBytesable<String> inBackground(final BackgroundCallback callback, final Executor executor) {
                return CreateBuilderImpl.this.inBackground(callback, executor);
            }
            
            @Override
            public String forPath(final String path, final byte[] data) throws Exception {
                return CreateBuilderImpl.this.forPath(path, data);
            }
            
            @Override
            public String forPath(final String path) throws Exception {
                return CreateBuilderImpl.this.forPath(path);
            }
        };
    }
    
    @Override
    public ProtectACLCreateModePathAndBytesable<String> creatingParentContainersIfNeeded() {
        this.setCreateParentsAsContainers();
        return this.creatingParentsIfNeeded();
    }
    
    private void setCreateParentsAsContainers() {
        if (this.client.useContainerParentsIfAvailable()) {
            this.createParentsAsContainers = true;
        }
    }
    
    @Override
    public ProtectACLCreateModePathAndBytesable<String> creatingParentsIfNeeded() {
        this.createParentsIfNeeded = true;
        return new ProtectACLCreateModePathAndBytesable<String>() {
            @Override
            public ACLCreateModeBackgroundPathAndBytesable<String> withProtection() {
                return CreateBuilderImpl.this.withProtection();
            }
            
            @Override
            public BackgroundPathAndBytesable<String> withACL(final List<ACL> aclList) {
                return CreateBuilderImpl.this.withACL(aclList);
            }
            
            @Override
            public ErrorListenerPathAndBytesable<String> inBackground() {
                return CreateBuilderImpl.this.inBackground();
            }
            
            @Override
            public ErrorListenerPathAndBytesable<String> inBackground(final Object context) {
                return CreateBuilderImpl.this.inBackground(context);
            }
            
            @Override
            public ErrorListenerPathAndBytesable<String> inBackground(final BackgroundCallback callback) {
                return CreateBuilderImpl.this.inBackground(callback);
            }
            
            @Override
            public ErrorListenerPathAndBytesable<String> inBackground(final BackgroundCallback callback, final Object context) {
                return CreateBuilderImpl.this.inBackground(callback, context);
            }
            
            @Override
            public ErrorListenerPathAndBytesable<String> inBackground(final BackgroundCallback callback, final Executor executor) {
                return CreateBuilderImpl.this.inBackground(callback, executor);
            }
            
            @Override
            public ErrorListenerPathAndBytesable<String> inBackground(final BackgroundCallback callback, final Object context, final Executor executor) {
                return CreateBuilderImpl.this.inBackground(callback, context, executor);
            }
            
            @Override
            public ACLBackgroundPathAndBytesable<String> withMode(final CreateMode mode) {
                return CreateBuilderImpl.this.withMode(mode);
            }
            
            @Override
            public String forPath(final String path, final byte[] data) throws Exception {
                return CreateBuilderImpl.this.forPath(path, data);
            }
            
            @Override
            public String forPath(final String path) throws Exception {
                return CreateBuilderImpl.this.forPath(path);
            }
        };
    }
    
    @Override
    public ACLCreateModeBackgroundPathAndBytesable<String> withProtection() {
        this.setProtected();
        return this;
    }
    
    @Override
    public ACLPathAndBytesable<String> withProtectedEphemeralSequential() {
        this.setProtected();
        this.createMode = CreateMode.EPHEMERAL_SEQUENTIAL;
        return new ACLPathAndBytesable<String>() {
            @Override
            public PathAndBytesable<String> withACL(final List<ACL> aclList) {
                return CreateBuilderImpl.this.withACL(aclList);
            }
            
            @Override
            public String forPath(final String path, final byte[] data) throws Exception {
                return CreateBuilderImpl.this.forPath(path, data);
            }
            
            @Override
            public String forPath(final String path) throws Exception {
                return CreateBuilderImpl.this.forPath(path);
            }
        };
    }
    
    @Override
    public ACLBackgroundPathAndBytesable<String> withMode(final CreateMode mode) {
        this.createMode = mode;
        return this;
    }
    
    @Override
    public ErrorListenerPathAndBytesable<String> inBackground(final BackgroundCallback callback, final Object context) {
        this.backgrounding = new Backgrounding(callback, context);
        return this;
    }
    
    @Override
    public ErrorListenerPathAndBytesable<String> inBackground(final BackgroundCallback callback, final Object context, final Executor executor) {
        this.backgrounding = new Backgrounding(this.client, callback, context, executor);
        return this;
    }
    
    @Override
    public ErrorListenerPathAndBytesable<String> inBackground(final BackgroundCallback callback) {
        this.backgrounding = new Backgrounding(callback);
        return this;
    }
    
    @Override
    public ErrorListenerPathAndBytesable<String> inBackground(final BackgroundCallback callback, final Executor executor) {
        this.backgrounding = new Backgrounding(this.client, callback, executor);
        return this;
    }
    
    @Override
    public ErrorListenerPathAndBytesable<String> inBackground() {
        this.backgrounding = new Backgrounding(true);
        return this;
    }
    
    @Override
    public ErrorListenerPathAndBytesable<String> inBackground(final Object context) {
        this.backgrounding = new Backgrounding(context);
        return this;
    }
    
    @Override
    public PathAndBytesable<String> withUnhandledErrorListener(final UnhandledErrorListener listener) {
        this.backgrounding = new Backgrounding(this.backgrounding, listener);
        return this;
    }
    
    @Override
    public String forPath(final String path) throws Exception {
        return this.forPath(path, this.client.getDefaultData());
    }
    
    @Override
    public String forPath(final String givenPath, byte[] data) throws Exception {
        if (this.compress) {
            data = this.client.getCompressionProvider().compress(givenPath, data);
        }
        final String adjustedPath = this.adjustPath(this.client.fixForNamespace(givenPath, this.createMode.isSequential()));
        String returnPath = null;
        if (this.backgrounding.inBackground()) {
            this.pathInBackground(adjustedPath, data, givenPath);
        }
        else {
            final String path = this.protectedPathInForeground(adjustedPath, data);
            returnPath = this.client.unfixForNamespace(path);
        }
        return returnPath;
    }
    
    private String protectedPathInForeground(final String adjustedPath, final byte[] data) throws Exception {
        try {
            return this.pathInForeground(adjustedPath, data);
        }
        catch (Exception e) {
            ThreadUtils.checkInterrupted(e);
            if ((e instanceof KeeperException.ConnectionLossException || !(e instanceof KeeperException)) && this.protectedId != null) {
                new FindAndDeleteProtectedNodeInBackground(this.client, ZKPaths.getPathAndNode(adjustedPath).getPath(), this.protectedId).execute();
                this.protectedId = UUID.randomUUID().toString();
            }
            throw e;
        }
    }
    
    @Override
    public void performBackgroundOperation(final OperationAndData<PathAndBytes> operationAndData) throws Exception {
        try {
            final OperationTrace trace = this.client.getZookeeperClient().startAdvancedTracer("CreateBuilderImpl-Background");
            final byte[] data = operationAndData.getData().getData();
            this.client.getZooKeeper().create(operationAndData.getData().getPath(), data, this.acling.getAclList(operationAndData.getData().getPath()), this.createMode, new AsyncCallback.StringCallback() {
                @Override
                public void processResult(final int rc, final String path, final Object ctx, final String name) {
                    trace.setReturnCode(rc).setRequestBytesLength(data).setPath(path).commit();
                    if (rc == KeeperException.Code.NONODE.intValue() && CreateBuilderImpl.this.createParentsIfNeeded) {
                        CreateBuilderImpl.backgroundCreateParentsThenNode(CreateBuilderImpl.this.client, (OperationAndData<Object>)operationAndData, operationAndData.getData().getPath(), CreateBuilderImpl.this.backgrounding, CreateBuilderImpl.this.createParentsAsContainers);
                    }
                    else {
                        CreateBuilderImpl.this.sendBackgroundResponse(rc, path, ctx, name, operationAndData);
                    }
                }
            }, this.backgrounding.getContext());
        }
        catch (Throwable e) {
            this.backgrounding.checkError(e);
        }
    }
    
    private static String getProtectedPrefix(final String protectedId) {
        return "_c_" + protectedId + "-";
    }
    
    static <T> void backgroundCreateParentsThenNode(final CuratorFrameworkImpl client, final OperationAndData<T> mainOperationAndData, final String path, final Backgrounding backgrounding, final boolean createParentsAsContainers) {
        final BackgroundOperation<T> operation = new BackgroundOperation<T>() {
            @Override
            public void performBackgroundOperation(final OperationAndData<T> dummy) throws Exception {
                try {
                    ZKPaths.mkdirs(client.getZooKeeper(), path, false, client.getAclProvider(), createParentsAsContainers);
                }
                catch (KeeperException e) {
                    if (!RetryLoop.isRetryException(e)) {
                        throw e;
                    }
                }
                client.queueOperation((OperationAndData<Object>)mainOperationAndData);
            }
        };
        final OperationAndData<T> parentOperation = new OperationAndData<T>(operation, mainOperationAndData.getData(), null, null, backgrounding.getContext());
        client.queueOperation(parentOperation);
    }
    
    private void sendBackgroundResponse(final int rc, String path, final Object ctx, String name, final OperationAndData<PathAndBytes> operationAndData) {
        path = this.client.unfixForNamespace(path);
        name = this.client.unfixForNamespace(name);
        final CuratorEvent event = new CuratorEventImpl(this.client, CuratorEventType.CREATE, rc, path, name, ctx, null, null, null, null, null);
        this.client.processBackgroundOperation(operationAndData, event);
    }
    
    private void setProtected() {
        this.doProtected = true;
        this.protectedId = UUID.randomUUID().toString();
    }
    
    private ACLCreateModePathAndBytesable<String> asACLCreateModePathAndBytesable() {
        return new ACLCreateModePathAndBytesable<String>() {
            @Override
            public PathAndBytesable<String> withACL(final List<ACL> aclList) {
                return CreateBuilderImpl.this.withACL(aclList);
            }
            
            @Override
            public ACLPathAndBytesable<String> withMode(final CreateMode mode) {
                CreateBuilderImpl.this.createMode = mode;
                return new ACLPathAndBytesable<String>() {
                    @Override
                    public PathAndBytesable<String> withACL(final List<ACL> aclList) {
                        return CreateBuilderImpl.this.withACL(aclList);
                    }
                    
                    @Override
                    public String forPath(final String path, final byte[] data) throws Exception {
                        return CreateBuilderImpl.this.forPath(path, data);
                    }
                    
                    @Override
                    public String forPath(final String path) throws Exception {
                        return CreateBuilderImpl.this.forPath(path);
                    }
                };
            }
            
            @Override
            public String forPath(final String path, final byte[] data) throws Exception {
                return CreateBuilderImpl.this.forPath(path, data);
            }
            
            @Override
            public String forPath(final String path) throws Exception {
                return CreateBuilderImpl.this.forPath(path);
            }
        };
    }
    
    private void pathInBackground(final String path, final byte[] data, final String givenPath) {
        final AtomicBoolean firstTime = new AtomicBoolean(true);
        final OperationAndData<PathAndBytes> operationAndData = new OperationAndData<PathAndBytes>(this, new PathAndBytes(path, data), this.backgrounding.getCallback(), new OperationAndData.ErrorCallback<PathAndBytes>() {
            @Override
            public void retriesExhausted(final OperationAndData<PathAndBytes> operationAndData) {
                if (CreateBuilderImpl.this.doProtected) {
                    new FindAndDeleteProtectedNodeInBackground(CreateBuilderImpl.this.client, ZKPaths.getPathAndNode(path).getPath(), CreateBuilderImpl.this.protectedId).execute();
                    CreateBuilderImpl.this.protectedId = UUID.randomUUID().toString();
                }
            }
        }, this.backgrounding.getContext()) {
            @Override
            void callPerformBackgroundOperation() throws Exception {
                boolean callSuper = true;
                final boolean localFirstTime = firstTime.getAndSet(false) && !CreateBuilderImpl.this.debugForceFindProtectedNode;
                if (!localFirstTime && CreateBuilderImpl.this.doProtected) {
                    CreateBuilderImpl.this.debugForceFindProtectedNode = false;
                    String createdPath = null;
                    try {
                        createdPath = CreateBuilderImpl.this.findProtectedNodeInForeground(path);
                    }
                    catch (KeeperException.ConnectionLossException e2) {
                        CreateBuilderImpl.this.sendBackgroundResponse(KeeperException.Code.CONNECTIONLOSS.intValue(), path, CreateBuilderImpl.this.backgrounding.getContext(), null, this);
                        callSuper = false;
                    }
                    if (createdPath != null) {
                        try {
                            CreateBuilderImpl.this.sendBackgroundResponse(KeeperException.Code.OK.intValue(), createdPath, CreateBuilderImpl.this.backgrounding.getContext(), createdPath, this);
                        }
                        catch (Exception e) {
                            ThreadUtils.checkInterrupted(e);
                            CreateBuilderImpl.this.client.logError("Processing protected create for path: " + givenPath, e);
                        }
                        callSuper = false;
                    }
                }
                if (CreateBuilderImpl.this.failNextCreateForTesting) {
                    CreateBuilderImpl.this.pathInForeground(path, data);
                    CreateBuilderImpl.this.failNextCreateForTesting = false;
                    throw new KeeperException.ConnectionLossException();
                }
                if (callSuper) {
                    super.callPerformBackgroundOperation();
                }
            }
        };
        this.client.processBackgroundOperation(operationAndData, null);
    }
    
    private String pathInForeground(final String path, final byte[] data) throws Exception {
        final OperationTrace trace = this.client.getZookeeperClient().startAdvancedTracer("CreateBuilderImpl-Foreground");
        final AtomicBoolean firstTime = new AtomicBoolean(true);
        final String returnPath = RetryLoop.callWithRetry(this.client.getZookeeperClient(), (Callable<String>)new Callable<String>() {
            @Override
            public String call() throws Exception {
                final boolean localFirstTime = firstTime.getAndSet(false) && !CreateBuilderImpl.this.debugForceFindProtectedNode;
                String createdPath = null;
                if (!localFirstTime && CreateBuilderImpl.this.doProtected) {
                    CreateBuilderImpl.this.debugForceFindProtectedNode = false;
                    createdPath = CreateBuilderImpl.this.findProtectedNodeInForeground(path);
                }
                if (createdPath == null) {
                    try {
                        createdPath = CreateBuilderImpl.this.client.getZooKeeper().create(path, data, CreateBuilderImpl.this.acling.getAclList(path), CreateBuilderImpl.this.createMode);
                    }
                    catch (KeeperException.NoNodeException e) {
                        if (!CreateBuilderImpl.this.createParentsIfNeeded) {
                            throw e;
                        }
                        ZKPaths.mkdirs(CreateBuilderImpl.this.client.getZooKeeper(), path, false, CreateBuilderImpl.this.client.getAclProvider(), CreateBuilderImpl.this.createParentsAsContainers);
                        createdPath = CreateBuilderImpl.this.client.getZooKeeper().create(path, data, CreateBuilderImpl.this.acling.getAclList(path), CreateBuilderImpl.this.createMode);
                    }
                }
                if (CreateBuilderImpl.this.failNextCreateForTesting) {
                    CreateBuilderImpl.this.failNextCreateForTesting = false;
                    throw new KeeperException.ConnectionLossException();
                }
                return createdPath;
            }
        });
        trace.setRequestBytesLength(data).setPath(path).commit();
        return returnPath;
    }
    
    private String findProtectedNodeInForeground(final String path) throws Exception {
        final OperationTrace trace = this.client.getZookeeperClient().startAdvancedTracer("CreateBuilderImpl-findProtectedNodeInForeground");
        final String returnPath = RetryLoop.callWithRetry(this.client.getZookeeperClient(), (Callable<String>)new Callable<String>() {
            @Override
            public String call() throws Exception {
                String foundNode = null;
                try {
                    final ZKPaths.PathAndNode pathAndNode = ZKPaths.getPathAndNode(path);
                    final List<String> children = CreateBuilderImpl.this.client.getZooKeeper().getChildren(pathAndNode.getPath(), false);
                    foundNode = CreateBuilderImpl.findNode(children, pathAndNode.getPath(), CreateBuilderImpl.this.protectedId);
                }
                catch (KeeperException.NoNodeException ex) {}
                return foundNode;
            }
        });
        trace.setPath(path).commit();
        return returnPath;
    }
    
    @VisibleForTesting
    String adjustPath(String path) throws Exception {
        if (this.doProtected) {
            final ZKPaths.PathAndNode pathAndNode = ZKPaths.getPathAndNode(path);
            final String name = getProtectedPrefix(this.protectedId) + pathAndNode.getNode();
            path = ZKPaths.makePath(pathAndNode.getPath(), name);
        }
        return path;
    }
    
    static String findNode(final List<String> children, final String path, final String protectedId) {
        final String protectedPrefix = getProtectedPrefix(protectedId);
        String foundNode = Iterables.find(children, new Predicate<String>() {
            @Override
            public boolean apply(final String node) {
                return node.startsWith(protectedPrefix);
            }
        }, (String)null);
        if (foundNode != null) {
            foundNode = ZKPaths.makePath(path, foundNode);
        }
        return foundNode;
    }
}
