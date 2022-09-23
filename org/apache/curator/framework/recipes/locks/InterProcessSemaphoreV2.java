// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.recipes.locks;

import org.apache.curator.framework.api.Pathable;
import org.apache.curator.framework.listen.Listenable;
import org.apache.curator.shaded.com.google.common.collect.Sets;
import java.io.IOException;
import org.apache.curator.utils.ThreadUtils;
import org.apache.curator.framework.api.BackgroundPathable;
import java.util.List;
import org.apache.zookeeper.CreateMode;
import org.apache.curator.framework.api.PathAndBytesable;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.zookeeper.KeeperException;
import org.apache.curator.RetryLoop;
import org.apache.curator.shaded.com.google.common.collect.ImmutableList;
import org.apache.curator.shaded.com.google.common.base.Preconditions;
import java.util.concurrent.TimeUnit;
import java.util.Iterator;
import java.io.Closeable;
import org.apache.curator.utils.CloseableUtils;
import java.util.Collection;
import java.util.Arrays;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.recipes.shared.SharedCountListener;
import org.apache.curator.utils.ZKPaths;
import org.apache.curator.utils.PathUtils;
import org.apache.zookeeper.WatchedEvent;
import org.slf4j.LoggerFactory;
import org.apache.curator.framework.recipes.shared.SharedCountReader;
import java.util.concurrent.CountDownLatch;
import java.util.Set;
import org.apache.zookeeper.Watcher;
import org.apache.curator.framework.CuratorFramework;
import org.slf4j.Logger;

public class InterProcessSemaphoreV2
{
    private final Logger log;
    private final InterProcessMutex lock;
    private final CuratorFramework client;
    private final String leasesPath;
    private final Watcher watcher;
    private volatile byte[] nodeData;
    private volatile int maxLeases;
    private static final String LOCK_PARENT = "locks";
    private static final String LEASE_PARENT = "leases";
    private static final String LEASE_BASE_NAME = "lease-";
    public static final Set<String> LOCK_SCHEMA;
    static volatile CountDownLatch debugAcquireLatch;
    static volatile CountDownLatch debugFailedGetChildrenLatch;
    
    public InterProcessSemaphoreV2(final CuratorFramework client, final String path, final int maxLeases) {
        this(client, path, maxLeases, null);
    }
    
    public InterProcessSemaphoreV2(final CuratorFramework client, final String path, final SharedCountReader count) {
        this(client, path, 0, count);
    }
    
    private InterProcessSemaphoreV2(final CuratorFramework client, String path, final int maxLeases, final SharedCountReader count) {
        this.log = LoggerFactory.getLogger(this.getClass());
        this.watcher = new Watcher() {
            @Override
            public void process(final WatchedEvent event) {
                InterProcessSemaphoreV2.this.notifyFromWatcher();
            }
        };
        this.client = client;
        path = PathUtils.validatePath(path);
        this.lock = new InterProcessMutex(client, ZKPaths.makePath(path, "locks"));
        this.maxLeases = ((count != null) ? count.getCount() : maxLeases);
        this.leasesPath = ZKPaths.makePath(path, "leases");
        if (count != null) {
            ((Listenable<InterProcessSemaphoreV2$2>)count).addListener(new SharedCountListener() {
                @Override
                public void countHasChanged(final SharedCountReader sharedCount, final int newCount) throws Exception {
                    InterProcessSemaphoreV2.this.maxLeases = newCount;
                    InterProcessSemaphoreV2.this.notifyFromWatcher();
                }
                
                @Override
                public void stateChanged(final CuratorFramework client, final ConnectionState newState) {
                }
            });
        }
    }
    
    public void setNodeData(final byte[] nodeData) {
        this.nodeData = (byte[])((nodeData != null) ? Arrays.copyOf(nodeData, nodeData.length) : null);
    }
    
    public Collection<String> getParticipantNodes() throws Exception {
        return ((Pathable<Collection<String>>)this.client.getChildren()).forPath(this.leasesPath);
    }
    
    public void returnAll(final Collection<Lease> leases) {
        for (final Lease l : leases) {
            CloseableUtils.closeQuietly(l);
        }
    }
    
    public void returnLease(final Lease lease) {
        CloseableUtils.closeQuietly(lease);
    }
    
    public Lease acquire() throws Exception {
        final Collection<Lease> leases = this.acquire(1, 0L, null);
        return leases.iterator().next();
    }
    
    public Collection<Lease> acquire(final int qty) throws Exception {
        return this.acquire(qty, 0L, null);
    }
    
    public Lease acquire(final long time, final TimeUnit unit) throws Exception {
        final Collection<Lease> leases = this.acquire(1, time, unit);
        return (leases != null) ? leases.iterator().next() : null;
    }
    
    public Collection<Lease> acquire(int qty, final long time, final TimeUnit unit) throws Exception {
        final long startMs = System.currentTimeMillis();
        final boolean hasWait = unit != null;
        final long waitMs = hasWait ? TimeUnit.MILLISECONDS.convert(time, unit) : 0L;
        Preconditions.checkArgument(qty > 0, (Object)"qty cannot be 0");
        final ImmutableList.Builder<Lease> builder = ImmutableList.builder();
        boolean success = false;
        try {
            while (qty-- > 0) {
                int retryCount = 0;
                final long startMillis = System.currentTimeMillis();
                boolean isDone = false;
                while (!isDone) {
                    switch (this.internalAcquire1Lease(builder, startMs, hasWait, waitMs)) {
                        case CONTINUE: {
                            isDone = true;
                            continue;
                        }
                        case RETURN_NULL: {
                            return null;
                        }
                        case RETRY_DUE_TO_MISSING_NODE: {
                            if (!this.client.getZookeeperClient().getRetryPolicy().allowRetry(retryCount++, System.currentTimeMillis() - startMillis, RetryLoop.getDefaultRetrySleeper())) {
                                throw new KeeperException.NoNodeException("Sequential path not found - possible session loss");
                            }
                            continue;
                        }
                    }
                }
            }
            success = true;
        }
        finally {
            if (!success) {
                this.returnAll(builder.build());
            }
        }
        return builder.build();
    }
    
    private InternalAcquireResult internalAcquire1Lease(final ImmutableList.Builder<Lease> builder, final long startMs, final boolean hasWait, final long waitMs) throws Exception {
        if (this.client.getState() != CuratorFrameworkState.STARTED) {
            return InternalAcquireResult.RETURN_NULL;
        }
        if (hasWait) {
            final long thisWaitMs = this.getThisWaitMs(startMs, waitMs);
            if (!this.lock.acquire(thisWaitMs, TimeUnit.MILLISECONDS)) {
                return InternalAcquireResult.RETURN_NULL;
            }
        }
        else {
            this.lock.acquire();
        }
        Lease lease = null;
        try {
            final PathAndBytesable<String> createBuilder = this.client.create().creatingParentContainersIfNeeded().withProtection().withMode(CreateMode.EPHEMERAL_SEQUENTIAL);
            final String path = (this.nodeData != null) ? createBuilder.forPath(ZKPaths.makePath(this.leasesPath, "lease-"), this.nodeData) : createBuilder.forPath(ZKPaths.makePath(this.leasesPath, "lease-"));
            final String nodeName = ZKPaths.getNodeFromPath(path);
            lease = this.makeLease(path);
            if (InterProcessSemaphoreV2.debugAcquireLatch != null) {
                InterProcessSemaphoreV2.debugAcquireLatch.await();
            }
            synchronized (this) {
                while (true) {
                    List<String> children;
                    try {
                        children = this.client.getChildren().usingWatcher(this.watcher).forPath(this.leasesPath);
                    }
                    catch (Exception e) {
                        if (InterProcessSemaphoreV2.debugFailedGetChildrenLatch != null) {
                            InterProcessSemaphoreV2.debugFailedGetChildrenLatch.countDown();
                        }
                        this.returnLease(lease);
                        throw e;
                    }
                    if (!children.contains(nodeName)) {
                        this.log.error("Sequential path not found: " + path);
                        this.returnLease(lease);
                        return InternalAcquireResult.RETRY_DUE_TO_MISSING_NODE;
                    }
                    if (children.size() <= this.maxLeases) {
                        break;
                    }
                    if (hasWait) {
                        final long thisWaitMs2 = this.getThisWaitMs(startMs, waitMs);
                        if (thisWaitMs2 <= 0L) {
                            this.returnLease(lease);
                            return InternalAcquireResult.RETURN_NULL;
                        }
                        this.wait(thisWaitMs2);
                    }
                    else {
                        this.wait();
                    }
                }
            }
        }
        finally {
            this.lock.release();
        }
        builder.add(Preconditions.checkNotNull(lease));
        return InternalAcquireResult.CONTINUE;
    }
    
    private long getThisWaitMs(final long startMs, final long waitMs) {
        final long elapsedMs = System.currentTimeMillis() - startMs;
        return waitMs - elapsedMs;
    }
    
    private Lease makeLease(final String path) {
        return new Lease() {
            @Override
            public void close() throws IOException {
                try {
                    InterProcessSemaphoreV2.this.client.delete().guaranteed().forPath(path);
                }
                catch (KeeperException.NoNodeException e) {
                    InterProcessSemaphoreV2.this.log.warn("Lease already released", e);
                }
                catch (Exception e2) {
                    ThreadUtils.checkInterrupted(e2);
                    throw new IOException(e2);
                }
            }
            
            @Override
            public byte[] getData() throws Exception {
                return InterProcessSemaphoreV2.this.client.getData().forPath(path);
            }
            
            @Override
            public String getNodeName() {
                return ZKPaths.getNodeFromPath(path);
            }
        };
    }
    
    private synchronized void notifyFromWatcher() {
        this.notifyAll();
    }
    
    static {
        LOCK_SCHEMA = Sets.newHashSet("locks", "leases");
        InterProcessSemaphoreV2.debugAcquireLatch = null;
        InterProcessSemaphoreV2.debugFailedGetChildrenLatch = null;
    }
    
    private enum InternalAcquireResult
    {
        CONTINUE, 
        RETURN_NULL, 
        RETRY_DUE_TO_MISSING_NODE;
    }
}
