// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.imps;

import org.apache.curator.drivers.OperationTrace;
import org.apache.curator.CuratorConnectionLossException;
import org.apache.curator.RetrySleeper;
import org.apache.zookeeper.KeeperException;
import org.apache.curator.RetryLoop;
import org.apache.curator.utils.EnsurePath;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.SyncBuilder;
import org.apache.curator.framework.listen.Listenable;
import org.apache.curator.framework.api.transaction.CuratorTransaction;
import org.apache.curator.framework.api.SetACLBuilder;
import org.apache.curator.framework.api.GetACLBuilder;
import org.apache.curator.framework.api.GetChildrenBuilder;
import org.apache.curator.framework.api.SetDataBuilder;
import org.apache.curator.framework.api.GetDataBuilder;
import org.apache.curator.framework.api.ExistsBuilder;
import org.apache.curator.framework.api.DeleteBuilder;
import org.apache.curator.framework.api.CreateBuilder;
import org.apache.curator.shaded.com.google.common.base.Preconditions;
import com.google.common.base.Function;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import java.util.concurrent.TimeUnit;
import org.apache.curator.utils.ZKPaths;
import org.apache.curator.utils.ThreadUtils;
import java.util.Iterator;
import org.apache.zookeeper.ZooKeeper;
import org.apache.curator.shaded.com.google.common.collect.ImmutableList;
import org.apache.curator.utils.ZookeeperFactory;
import java.util.Arrays;
import java.util.concurrent.DelayQueue;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;
import org.apache.curator.framework.api.CuratorEventType;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.slf4j.LoggerFactory;
import org.apache.curator.framework.CuratorFrameworkFactory;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.curator.shaded.com.google.common.annotations.VisibleForTesting;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.ExecutorService;
import org.apache.curator.framework.api.ACLProvider;
import org.apache.curator.framework.api.CompressionProvider;
import org.apache.curator.framework.AuthInfo;
import java.util.List;
import org.apache.curator.framework.state.ConnectionStateManager;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadFactory;
import org.apache.curator.framework.api.UnhandledErrorListener;
import org.apache.curator.framework.api.CuratorListener;
import org.apache.curator.framework.listen.ListenerContainer;
import org.apache.curator.CuratorZookeeperClient;
import org.slf4j.Logger;
import org.apache.curator.framework.CuratorFramework;

public class CuratorFrameworkImpl implements CuratorFramework
{
    private final Logger log;
    private final CuratorZookeeperClient client;
    private final ListenerContainer<CuratorListener> listeners;
    private final ListenerContainer<UnhandledErrorListener> unhandledErrorListeners;
    private final ThreadFactory threadFactory;
    private final int maxCloseWaitMs;
    private final BlockingQueue<OperationAndData<?>> backgroundOperations;
    private final NamespaceImpl namespace;
    private final ConnectionStateManager connectionStateManager;
    private final List<AuthInfo> authInfos;
    private final byte[] defaultData;
    private final FailedDeleteManager failedDeleteManager;
    private final CompressionProvider compressionProvider;
    private final ACLProvider aclProvider;
    private final NamespaceFacadeCache namespaceFacadeCache;
    private final NamespaceWatcherMap namespaceWatcherMap;
    private final boolean useContainerParentsIfAvailable;
    private volatile ExecutorService executorService;
    private final AtomicBoolean logAsErrorConnectionErrors;
    private static final boolean LOG_ALL_CONNECTION_ISSUES_AS_ERROR_LEVEL;
    volatile DebugBackgroundListener debugListener;
    @VisibleForTesting
    public volatile UnhandledErrorListener debugUnhandledErrorListener;
    private final AtomicReference<CuratorFrameworkState> state;
    
    public CuratorFrameworkImpl(final CuratorFrameworkFactory.Builder builder) {
        this.log = LoggerFactory.getLogger(this.getClass());
        this.namespaceWatcherMap = new NamespaceWatcherMap(this);
        this.logAsErrorConnectionErrors = new AtomicBoolean(false);
        this.debugListener = null;
        this.debugUnhandledErrorListener = null;
        final ZookeeperFactory localZookeeperFactory = this.makeZookeeperFactory(builder.getZookeeperFactory());
        this.client = new CuratorZookeeperClient(localZookeeperFactory, builder.getEnsembleProvider(), builder.getSessionTimeoutMs(), builder.getConnectionTimeoutMs(), new Watcher() {
            @Override
            public void process(final WatchedEvent watchedEvent) {
                final CuratorEvent event = new CuratorEventImpl(CuratorFrameworkImpl.this, CuratorEventType.WATCHED, watchedEvent.getState().getIntValue(), CuratorFrameworkImpl.this.unfixForNamespace(watchedEvent.getPath()), null, null, null, null, null, watchedEvent, null);
                CuratorFrameworkImpl.this.processEvent(event);
            }
        }, builder.getRetryPolicy(), builder.canBeReadOnly());
        this.listeners = new ListenerContainer<CuratorListener>();
        this.unhandledErrorListeners = new ListenerContainer<UnhandledErrorListener>();
        this.backgroundOperations = new DelayQueue<OperationAndData<?>>();
        this.namespace = new NamespaceImpl(this, builder.getNamespace());
        this.threadFactory = this.getThreadFactory(builder);
        this.maxCloseWaitMs = builder.getMaxCloseWaitMs();
        this.connectionStateManager = new ConnectionStateManager(this, builder.getThreadFactory());
        this.compressionProvider = builder.getCompressionProvider();
        this.aclProvider = builder.getAclProvider();
        this.state = new AtomicReference<CuratorFrameworkState>(CuratorFrameworkState.LATENT);
        this.useContainerParentsIfAvailable = builder.useContainerParentsIfAvailable();
        final byte[] builderDefaultData = builder.getDefaultData();
        this.defaultData = ((builderDefaultData != null) ? Arrays.copyOf(builderDefaultData, builderDefaultData.length) : new byte[0]);
        this.authInfos = this.buildAuths(builder);
        this.failedDeleteManager = new FailedDeleteManager(this);
        this.namespaceFacadeCache = new NamespaceFacadeCache(this);
    }
    
    private List<AuthInfo> buildAuths(final CuratorFrameworkFactory.Builder builder) {
        final ImmutableList.Builder<AuthInfo> builder2 = ImmutableList.builder();
        if (builder.getAuthInfos() != null) {
            builder2.addAll(builder.getAuthInfos());
        }
        return builder2.build();
    }
    
    private ZookeeperFactory makeZookeeperFactory(final ZookeeperFactory actualZookeeperFactory) {
        return new ZookeeperFactory() {
            @Override
            public ZooKeeper newZooKeeper(final String connectString, final int sessionTimeout, final Watcher watcher, final boolean canBeReadOnly) throws Exception {
                final ZooKeeper zooKeeper = actualZookeeperFactory.newZooKeeper(connectString, sessionTimeout, watcher, canBeReadOnly);
                for (final AuthInfo auth : CuratorFrameworkImpl.this.authInfos) {
                    zooKeeper.addAuthInfo(auth.getScheme(), auth.getAuth());
                }
                return zooKeeper;
            }
        };
    }
    
    private ThreadFactory getThreadFactory(final CuratorFrameworkFactory.Builder builder) {
        ThreadFactory threadFactory = builder.getThreadFactory();
        if (threadFactory == null) {
            threadFactory = ThreadUtils.newThreadFactory("Framework");
        }
        return threadFactory;
    }
    
    protected CuratorFrameworkImpl(final CuratorFrameworkImpl parent) {
        this.log = LoggerFactory.getLogger(this.getClass());
        this.namespaceWatcherMap = new NamespaceWatcherMap(this);
        this.logAsErrorConnectionErrors = new AtomicBoolean(false);
        this.debugListener = null;
        this.debugUnhandledErrorListener = null;
        this.client = parent.client;
        this.listeners = parent.listeners;
        this.unhandledErrorListeners = parent.unhandledErrorListeners;
        this.threadFactory = parent.threadFactory;
        this.maxCloseWaitMs = parent.maxCloseWaitMs;
        this.backgroundOperations = parent.backgroundOperations;
        this.connectionStateManager = parent.connectionStateManager;
        this.defaultData = parent.defaultData;
        this.failedDeleteManager = parent.failedDeleteManager;
        this.compressionProvider = parent.compressionProvider;
        this.aclProvider = parent.aclProvider;
        this.namespaceFacadeCache = parent.namespaceFacadeCache;
        this.namespace = new NamespaceImpl(this, null);
        this.state = parent.state;
        this.authInfos = parent.authInfos;
        this.useContainerParentsIfAvailable = parent.useContainerParentsIfAvailable;
    }
    
    @Override
    public void createContainers(final String path) throws Exception {
        this.checkExists().creatingParentContainersIfNeeded().forPath(ZKPaths.makePath(path, "foo"));
    }
    
    @Override
    public void clearWatcherReferences(final Watcher watcher) {
        final NamespaceWatcher namespaceWatcher = this.namespaceWatcherMap.remove(watcher);
        if (namespaceWatcher != null) {
            namespaceWatcher.close();
        }
    }
    
    @Override
    public CuratorFrameworkState getState() {
        return this.state.get();
    }
    
    @Deprecated
    @Override
    public boolean isStarted() {
        return this.state.get() == CuratorFrameworkState.STARTED;
    }
    
    @Override
    public boolean blockUntilConnected(final int maxWaitTime, final TimeUnit units) throws InterruptedException {
        return this.connectionStateManager.blockUntilConnected(maxWaitTime, units);
    }
    
    @Override
    public void blockUntilConnected() throws InterruptedException {
        this.blockUntilConnected(0, null);
    }
    
    @Override
    public void start() {
        this.log.info("Starting");
        if (!this.state.compareAndSet(CuratorFrameworkState.LATENT, CuratorFrameworkState.STARTED)) {
            throw new IllegalStateException("Cannot be started more than once");
        }
        try {
            this.connectionStateManager.start();
            final ConnectionStateListener listener = new ConnectionStateListener() {
                @Override
                public void stateChanged(final CuratorFramework client, final ConnectionState newState) {
                    if (ConnectionState.CONNECTED == newState || ConnectionState.RECONNECTED == newState) {
                        CuratorFrameworkImpl.this.logAsErrorConnectionErrors.set(true);
                    }
                }
            };
            this.getConnectionStateListenable().addListener(listener);
            this.client.start();
            (this.executorService = Executors.newSingleThreadScheduledExecutor(this.threadFactory)).submit((Callable<Object>)new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    CuratorFrameworkImpl.this.backgroundOperationsLoop();
                    return null;
                }
            });
        }
        catch (Exception e) {
            ThreadUtils.checkInterrupted(e);
            this.handleBackgroundOperationException((OperationAndData<Object>)null, e);
        }
    }
    
    @Override
    public void close() {
        this.log.debug("Closing");
        if (this.state.compareAndSet(CuratorFrameworkState.STARTED, CuratorFrameworkState.STOPPED)) {
            this.listeners.forEach(new Function<CuratorListener, Void>() {
                @Override
                public Void apply(final CuratorListener listener) {
                    final CuratorEvent event = new CuratorEventImpl(CuratorFrameworkImpl.this, CuratorEventType.CLOSING, 0, null, null, null, null, null, null, null, null);
                    try {
                        listener.eventReceived(CuratorFrameworkImpl.this, event);
                    }
                    catch (Exception e) {
                        ThreadUtils.checkInterrupted(e);
                        CuratorFrameworkImpl.this.log.error("Exception while sending Closing event", e);
                    }
                    return null;
                }
            });
            if (this.executorService != null) {
                this.executorService.shutdownNow();
                try {
                    this.executorService.awaitTermination(this.maxCloseWaitMs, TimeUnit.MILLISECONDS);
                }
                catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            this.listeners.clear();
            this.unhandledErrorListeners.clear();
            this.connectionStateManager.close();
            this.client.close();
            this.namespaceWatcherMap.close();
        }
    }
    
    @Deprecated
    @Override
    public CuratorFramework nonNamespaceView() {
        return this.usingNamespace(null);
    }
    
    @Override
    public String getNamespace() {
        final String str = this.namespace.getNamespace();
        return (str != null) ? str : "";
    }
    
    @Override
    public CuratorFramework usingNamespace(final String newNamespace) {
        Preconditions.checkState(this.getState() == CuratorFrameworkState.STARTED, (Object)"instance must be started before calling this method");
        return this.namespaceFacadeCache.get(newNamespace);
    }
    
    @Override
    public CreateBuilder create() {
        Preconditions.checkState(this.getState() == CuratorFrameworkState.STARTED, (Object)"instance must be started before calling this method");
        return new CreateBuilderImpl(this);
    }
    
    @Override
    public DeleteBuilder delete() {
        Preconditions.checkState(this.getState() == CuratorFrameworkState.STARTED, (Object)"instance must be started before calling this method");
        return new DeleteBuilderImpl(this);
    }
    
    @Override
    public ExistsBuilder checkExists() {
        Preconditions.checkState(this.getState() == CuratorFrameworkState.STARTED, (Object)"instance must be started before calling this method");
        return new ExistsBuilderImpl(this);
    }
    
    @Override
    public GetDataBuilder getData() {
        Preconditions.checkState(this.getState() == CuratorFrameworkState.STARTED, (Object)"instance must be started before calling this method");
        return new GetDataBuilderImpl(this);
    }
    
    @Override
    public SetDataBuilder setData() {
        Preconditions.checkState(this.getState() == CuratorFrameworkState.STARTED, (Object)"instance must be started before calling this method");
        return new SetDataBuilderImpl(this);
    }
    
    @Override
    public GetChildrenBuilder getChildren() {
        Preconditions.checkState(this.getState() == CuratorFrameworkState.STARTED, (Object)"instance must be started before calling this method");
        return new GetChildrenBuilderImpl(this);
    }
    
    @Override
    public GetACLBuilder getACL() {
        Preconditions.checkState(this.getState() == CuratorFrameworkState.STARTED, (Object)"instance must be started before calling this method");
        return new GetACLBuilderImpl(this);
    }
    
    @Override
    public SetACLBuilder setACL() {
        Preconditions.checkState(this.getState() == CuratorFrameworkState.STARTED, (Object)"instance must be started before calling this method");
        return new SetACLBuilderImpl(this);
    }
    
    @Override
    public CuratorTransaction inTransaction() {
        Preconditions.checkState(this.getState() == CuratorFrameworkState.STARTED, (Object)"instance must be started before calling this method");
        return new CuratorTransactionImpl(this);
    }
    
    @Override
    public Listenable<ConnectionStateListener> getConnectionStateListenable() {
        return this.connectionStateManager.getListenable();
    }
    
    @Override
    public Listenable<CuratorListener> getCuratorListenable() {
        return this.listeners;
    }
    
    @Override
    public Listenable<UnhandledErrorListener> getUnhandledErrorListenable() {
        return this.unhandledErrorListeners;
    }
    
    @Override
    public void sync(String path, final Object context) {
        Preconditions.checkState(this.getState() == CuratorFrameworkState.STARTED, (Object)"instance must be started before calling this method");
        path = this.fixForNamespace(path);
        this.internalSync(this, path, context);
    }
    
    @Override
    public SyncBuilder sync() {
        return new SyncBuilderImpl(this);
    }
    
    protected void internalSync(final CuratorFrameworkImpl impl, final String path, final Object context) {
        final BackgroundOperation<String> operation = new BackgroundSyncImpl(impl, context);
        this.performBackgroundOperation(new OperationAndData<Object>(operation, path, null, null, context));
    }
    
    @Override
    public CuratorZookeeperClient getZookeeperClient() {
        return this.client;
    }
    
    @Override
    public EnsurePath newNamespaceAwareEnsurePath(final String path) {
        return this.namespace.newNamespaceAwareEnsurePath(path);
    }
    
    ACLProvider getAclProvider() {
        return this.aclProvider;
    }
    
    FailedDeleteManager getFailedDeleteManager() {
        return this.failedDeleteManager;
    }
    
    RetryLoop newRetryLoop() {
        return this.client.newRetryLoop();
    }
    
    ZooKeeper getZooKeeper() throws Exception {
        return this.client.getZooKeeper();
    }
    
    CompressionProvider getCompressionProvider() {
        return this.compressionProvider;
    }
    
    boolean useContainerParentsIfAvailable() {
        return this.useContainerParentsIfAvailable;
    }
    
     <DATA_TYPE> void processBackgroundOperation(final OperationAndData<DATA_TYPE> operationAndData, final CuratorEvent event) {
        final boolean isInitialExecution = event == null;
        if (isInitialExecution) {
            this.performBackgroundOperation(operationAndData);
            return;
        }
        boolean doQueueOperation = false;
        if (RetryLoop.shouldRetry(event.getResultCode())) {
            doQueueOperation = this.checkBackgroundRetry(operationAndData, event);
        }
        else if (operationAndData.getCallback() != null) {
            this.sendToBackgroundCallback((OperationAndData<Object>)operationAndData, event);
        }
        else {
            this.processEvent(event);
        }
        if (doQueueOperation) {
            this.queueOperation(operationAndData);
        }
    }
    
     <DATA_TYPE> void queueOperation(final OperationAndData<DATA_TYPE> operationAndData) {
        if (this.getState() == CuratorFrameworkState.STARTED) {
            this.backgroundOperations.offer(operationAndData);
        }
    }
    
    void logError(String reason, final Throwable e) {
        if (reason == null || reason.length() == 0) {
            reason = "n/a";
        }
        if (!Boolean.getBoolean("curator-dont-log-connection-problems") || !(e instanceof KeeperException)) {
            if (e instanceof KeeperException.ConnectionLossException) {
                if (CuratorFrameworkImpl.LOG_ALL_CONNECTION_ISSUES_AS_ERROR_LEVEL || this.logAsErrorConnectionErrors.compareAndSet(true, false)) {
                    this.log.error(reason, e);
                }
                else {
                    this.log.debug(reason, e);
                }
            }
            else {
                this.log.error(reason, e);
            }
        }
        final String localReason = reason;
        this.unhandledErrorListeners.forEach(new Function<UnhandledErrorListener, Void>() {
            @Override
            public Void apply(final UnhandledErrorListener listener) {
                listener.unhandledError(localReason, e);
                return null;
            }
        });
        if (this.debugUnhandledErrorListener != null) {
            this.debugUnhandledErrorListener.unhandledError(reason, e);
        }
    }
    
    String unfixForNamespace(final String path) {
        return this.namespace.unfixForNamespace(path);
    }
    
    String fixForNamespace(final String path) {
        return this.namespace.fixForNamespace(path, false);
    }
    
    String fixForNamespace(final String path, final boolean isSequential) {
        return this.namespace.fixForNamespace(path, isSequential);
    }
    
    byte[] getDefaultData() {
        return this.defaultData;
    }
    
    NamespaceFacadeCache getNamespaceFacadeCache() {
        return this.namespaceFacadeCache;
    }
    
    NamespaceWatcherMap getNamespaceWatcherMap() {
        return this.namespaceWatcherMap;
    }
    
    void validateConnection(final Watcher.Event.KeeperState state) {
        if (state == Watcher.Event.KeeperState.Disconnected) {
            this.suspendConnection();
        }
        else if (state == Watcher.Event.KeeperState.Expired) {
            this.connectionStateManager.addStateChange(ConnectionState.LOST);
        }
        else if (state == Watcher.Event.KeeperState.SyncConnected) {
            this.connectionStateManager.addStateChange(ConnectionState.RECONNECTED);
        }
        else if (state == Watcher.Event.KeeperState.ConnectedReadOnly) {
            this.connectionStateManager.addStateChange(ConnectionState.READ_ONLY);
        }
    }
    
    Watcher.Event.KeeperState codeToState(final KeeperException.Code code) {
        switch (code) {
            case AUTHFAILED:
            case NOAUTH: {
                return Watcher.Event.KeeperState.AuthFailed;
            }
            case CONNECTIONLOSS:
            case OPERATIONTIMEOUT: {
                return Watcher.Event.KeeperState.Disconnected;
            }
            case SESSIONEXPIRED: {
                return Watcher.Event.KeeperState.Expired;
            }
            case OK:
            case SESSIONMOVED: {
                return Watcher.Event.KeeperState.SyncConnected;
            }
            default: {
                return Watcher.Event.KeeperState.fromInt(-1);
            }
        }
    }
    
    private void suspendConnection() {
        if (!this.connectionStateManager.setToSuspended()) {
            return;
        }
        this.doSyncForSuspendedConnection(this.client.getInstanceIndex());
    }
    
    private void doSyncForSuspendedConnection(final long instanceIndex) {
        final BackgroundOperation<String> operation = new BackgroundSyncImpl(this, null);
        final OperationAndData.ErrorCallback<String> errorCallback = new OperationAndData.ErrorCallback<String>() {
            @Override
            public void retriesExhausted(final OperationAndData<String> operationAndData) {
                if (instanceIndex < 0L || instanceIndex == CuratorFrameworkImpl.this.client.getInstanceIndex()) {
                    CuratorFrameworkImpl.this.connectionStateManager.addStateChange(ConnectionState.LOST);
                }
                else {
                    CuratorFrameworkImpl.this.log.debug("suspendConnection() failure ignored as the ZooKeeper instance was reset. Retrying.");
                    CuratorFrameworkImpl.this.doSyncForSuspendedConnection(-1L);
                }
            }
        };
        this.performBackgroundOperation(new OperationAndData<Object>(operation, "/", null, errorCallback, null));
    }
    
    private <DATA_TYPE> boolean checkBackgroundRetry(final OperationAndData<DATA_TYPE> operationAndData, final CuratorEvent event) {
        boolean doRetry = false;
        if (this.client.getRetryPolicy().allowRetry(operationAndData.getThenIncrementRetryCount(), operationAndData.getElapsedTimeMs(), operationAndData)) {
            doRetry = true;
        }
        else {
            if (operationAndData.getErrorCallback() != null) {
                operationAndData.getErrorCallback().retriesExhausted(operationAndData);
            }
            if (operationAndData.getCallback() != null) {
                this.sendToBackgroundCallback(operationAndData, event);
            }
            final KeeperException.Code code = KeeperException.Code.get(event.getResultCode());
            Exception e = null;
            try {
                e = ((code != null) ? KeeperException.create(code) : null);
            }
            catch (Throwable t) {
                ThreadUtils.checkInterrupted(t);
            }
            if (e == null) {
                e = new Exception("Unknown result codegetResultCode()");
            }
            this.validateConnection(this.codeToState(code));
            this.logError("Background operation retry gave up", e);
        }
        return doRetry;
    }
    
    private <DATA_TYPE> void sendToBackgroundCallback(final OperationAndData<DATA_TYPE> operationAndData, final CuratorEvent event) {
        try {
            operationAndData.getCallback().processResult(this, event);
        }
        catch (Exception e) {
            ThreadUtils.checkInterrupted(e);
            this.handleBackgroundOperationException(operationAndData, e);
        }
    }
    
    private <DATA_TYPE> void handleBackgroundOperationException(final OperationAndData<DATA_TYPE> operationAndData, final Throwable e) {
        if (operationAndData != null && RetryLoop.isRetryException(e)) {
            if (!Boolean.getBoolean("curator-dont-log-connection-problems")) {
                this.log.debug("Retry-able exception received", e);
            }
            if (this.client.getRetryPolicy().allowRetry(operationAndData.getThenIncrementRetryCount(), operationAndData.getElapsedTimeMs(), operationAndData)) {
                if (!Boolean.getBoolean("curator-dont-log-connection-problems")) {
                    this.log.debug("Retrying operation");
                }
                this.backgroundOperations.offer(operationAndData);
                return;
            }
            if (!Boolean.getBoolean("curator-dont-log-connection-problems")) {
                this.log.debug("Retry policy did not allow retry");
            }
            if (operationAndData.getErrorCallback() != null) {
                operationAndData.getErrorCallback().retriesExhausted(operationAndData);
            }
        }
        this.logError("Background exception was not retry-able or retry gave up", e);
    }
    
    private void backgroundOperationsLoop() {
        try {
            while (this.state.get() == CuratorFrameworkState.STARTED) {
                try {
                    final OperationAndData<?> operationAndData = this.backgroundOperations.take();
                    if (this.debugListener != null) {
                        this.debugListener.listen(operationAndData);
                    }
                    this.performBackgroundOperation(operationAndData);
                }
                catch (InterruptedException ex) {}
            }
        }
        finally {
            this.log.info("backgroundOperationsLoop exiting");
        }
    }
    
    private void performBackgroundOperation(final OperationAndData<?> operationAndData) {
        try {
            if (this.client.isConnected()) {
                operationAndData.callPerformBackgroundOperation();
            }
            else {
                this.client.getZooKeeper();
                if (operationAndData.getElapsedTimeMs() >= this.client.getConnectionTimeoutMs()) {
                    throw new CuratorConnectionLossException();
                }
                operationAndData.sleepFor(1L, TimeUnit.SECONDS);
                this.queueOperation(operationAndData);
            }
        }
        catch (Throwable e) {
            ThreadUtils.checkInterrupted(e);
            if (e instanceof CuratorConnectionLossException) {
                final WatchedEvent watchedEvent = new WatchedEvent(Watcher.Event.EventType.None, Watcher.Event.KeeperState.Disconnected, null);
                final CuratorEvent event = new CuratorEventImpl(this, CuratorEventType.WATCHED, KeeperException.Code.CONNECTIONLOSS.intValue(), null, null, operationAndData.getContext(), null, null, null, watchedEvent, null);
                if (this.checkBackgroundRetry(operationAndData, event)) {
                    this.queueOperation(operationAndData);
                }
                else {
                    this.logError("Background retry gave up", e);
                }
            }
            else {
                this.handleBackgroundOperationException(operationAndData, e);
            }
        }
    }
    
    private void processEvent(final CuratorEvent curatorEvent) {
        if (curatorEvent.getType() == CuratorEventType.WATCHED) {
            this.validateConnection(curatorEvent.getWatchedEvent().getState());
        }
        this.listeners.forEach(new Function<CuratorListener, Void>() {
            @Override
            public Void apply(final CuratorListener listener) {
                try {
                    final OperationTrace trace = CuratorFrameworkImpl.this.client.startAdvancedTracer("EventListener");
                    listener.eventReceived(CuratorFrameworkImpl.this, curatorEvent);
                    trace.commit();
                }
                catch (Exception e) {
                    ThreadUtils.checkInterrupted(e);
                    CuratorFrameworkImpl.this.logError("Event listener threw exception", e);
                }
                return null;
            }
        });
    }
    
    static {
        LOG_ALL_CONNECTION_ISSUES_AS_ERROR_LEVEL = !Boolean.getBoolean("curator-log-only-first-connection-issue-as-error-level");
    }
    
    interface DebugBackgroundListener
    {
        void listen(final OperationAndData<?> p0);
    }
}
