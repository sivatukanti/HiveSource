// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.recipes.locks;

import org.apache.curator.framework.api.Watchable;
import org.apache.curator.utils.ThreadUtils;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import java.util.Arrays;
import org.apache.curator.framework.api.BackgroundPathable;
import org.apache.curator.RetryLoop;
import java.util.concurrent.TimeUnit;
import java.util.Collections;
import java.util.Comparator;
import org.apache.curator.shaded.com.google.common.collect.Lists;
import java.util.List;
import org.apache.curator.shaded.com.google.common.collect.ImmutableList;
import org.apache.curator.shaded.com.google.common.collect.Iterables;
import com.google.common.base.Function;
import java.util.Collection;
import org.apache.curator.utils.ZKPaths;
import org.apache.curator.utils.PathUtils;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher;
import org.apache.curator.framework.api.CuratorWatcher;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.curator.framework.CuratorFramework;

public class LockInternals
{
    private final CuratorFramework client;
    private final String path;
    private final String basePath;
    private final LockInternalsDriver driver;
    private final String lockName;
    private final AtomicReference<RevocationSpec> revocable;
    private final CuratorWatcher revocableWatcher;
    private final Watcher watcher;
    private volatile int maxLeases;
    static final byte[] REVOKE_MESSAGE;
    
    public void clean() throws Exception {
        try {
            this.client.delete().forPath(this.basePath);
        }
        catch (KeeperException.BadVersionException ex) {}
        catch (KeeperException.NotEmptyException ex2) {}
    }
    
    LockInternals(final CuratorFramework client, final LockInternalsDriver driver, final String path, final String lockName, final int maxLeases) {
        this.revocable = new AtomicReference<RevocationSpec>(null);
        this.revocableWatcher = new CuratorWatcher() {
            @Override
            public void process(final WatchedEvent event) throws Exception {
                if (event.getType() == Watcher.Event.EventType.NodeDataChanged) {
                    LockInternals.this.checkRevocableWatcher(event.getPath());
                }
            }
        };
        this.watcher = new Watcher() {
            @Override
            public void process(final WatchedEvent event) {
                LockInternals.this.notifyFromWatcher();
            }
        };
        this.driver = driver;
        this.lockName = lockName;
        this.maxLeases = maxLeases;
        this.client = client;
        this.basePath = PathUtils.validatePath(path);
        this.path = ZKPaths.makePath(path, lockName);
    }
    
    synchronized void setMaxLeases(final int maxLeases) {
        this.maxLeases = maxLeases;
        this.notifyAll();
    }
    
    void makeRevocable(final RevocationSpec entry) {
        this.revocable.set(entry);
    }
    
    void releaseLock(final String lockPath) throws Exception {
        this.revocable.set(null);
        this.deleteOurPath(lockPath);
    }
    
    CuratorFramework getClient() {
        return this.client;
    }
    
    public static Collection<String> getParticipantNodes(final CuratorFramework client, final String basePath, final String lockName, final LockInternalsSorter sorter) throws Exception {
        final List<String> names = getSortedChildren(client, basePath, lockName, sorter);
        final Iterable<String> transformed = Iterables.transform((Iterable<String>)names, (Function<? super String, ? extends String>)new Function<String, String>() {
            @Override
            public String apply(final String name) {
                return ZKPaths.makePath(basePath, name);
            }
        });
        return (Collection<String>)ImmutableList.copyOf((Iterable<?>)transformed);
    }
    
    public static List<String> getSortedChildren(final CuratorFramework client, final String basePath, final String lockName, final LockInternalsSorter sorter) throws Exception {
        final List<String> children = client.getChildren().forPath(basePath);
        final List<String> sortedList = (List<String>)Lists.newArrayList((Iterable<?>)children);
        Collections.sort(sortedList, new Comparator<String>() {
            @Override
            public int compare(final String lhs, final String rhs) {
                return sorter.fixForSorting(lhs, lockName).compareTo(sorter.fixForSorting(rhs, lockName));
            }
        });
        return sortedList;
    }
    
    public static List<String> getSortedChildren(final String lockName, final LockInternalsSorter sorter, final List<String> children) {
        final List<String> sortedList = (List<String>)Lists.newArrayList((Iterable<?>)children);
        Collections.sort(sortedList, new Comparator<String>() {
            @Override
            public int compare(final String lhs, final String rhs) {
                return sorter.fixForSorting(lhs, lockName).compareTo(sorter.fixForSorting(rhs, lockName));
            }
        });
        return sortedList;
    }
    
    List<String> getSortedChildren() throws Exception {
        return getSortedChildren(this.client, this.basePath, this.lockName, this.driver);
    }
    
    String getLockName() {
        return this.lockName;
    }
    
    LockInternalsDriver getDriver() {
        return this.driver;
    }
    
    String attemptLock(final long time, final TimeUnit unit, final byte[] lockNodeBytes) throws Exception {
        final long startMillis = System.currentTimeMillis();
        final Long millisToWait = (unit != null) ? Long.valueOf(unit.toMillis(time)) : null;
        final byte[] localLockNodeBytes = (this.revocable.get() != null) ? new byte[0] : lockNodeBytes;
        int retryCount = 0;
        String ourPath = null;
        boolean hasTheLock = false;
        boolean isDone = false;
        while (!isDone) {
            isDone = true;
            try {
                ourPath = this.driver.createsTheLock(this.client, this.path, localLockNodeBytes);
                hasTheLock = this.internalLockLoop(startMillis, millisToWait, ourPath);
            }
            catch (KeeperException.NoNodeException e) {
                if (!this.client.getZookeeperClient().getRetryPolicy().allowRetry(retryCount++, System.currentTimeMillis() - startMillis, RetryLoop.getDefaultRetrySleeper())) {
                    throw e;
                }
                isDone = false;
            }
        }
        if (hasTheLock) {
            return ourPath;
        }
        return null;
    }
    
    private void checkRevocableWatcher(final String path) throws Exception {
        final RevocationSpec entry = this.revocable.get();
        if (entry != null) {
            try {
                final byte[] bytes = this.client.getData().usingWatcher(this.revocableWatcher).forPath(path);
                if (Arrays.equals(bytes, LockInternals.REVOKE_MESSAGE)) {
                    entry.getExecutor().execute(entry.getRunnable());
                }
            }
            catch (KeeperException.NoNodeException ex) {}
        }
    }
    
    private boolean internalLockLoop(long startMillis, Long millisToWait, final String ourPath) throws Exception {
        boolean haveTheLock = false;
        boolean doDelete = false;
        try {
            if (this.revocable.get() != null) {
                ((Watchable<BackgroundPathable>)this.client.getData()).usingWatcher(this.revocableWatcher).forPath(ourPath);
            }
            while (this.client.getState() == CuratorFrameworkState.STARTED && !haveTheLock) {
                final List<String> children = this.getSortedChildren();
                final String sequenceNodeName = ourPath.substring(this.basePath.length() + 1);
                final PredicateResults predicateResults = this.driver.getsTheLock(this.client, children, sequenceNodeName, this.maxLeases);
                if (predicateResults.getsTheLock()) {
                    haveTheLock = true;
                }
                else {
                    final String previousSequencePath = this.basePath + "/" + predicateResults.getPathToWatch();
                    synchronized (this) {
                        try {
                            ((Watchable<BackgroundPathable>)this.client.getData()).usingWatcher(this.watcher).forPath(previousSequencePath);
                            if (millisToWait != null) {
                                millisToWait -= System.currentTimeMillis() - startMillis;
                                startMillis = System.currentTimeMillis();
                                if (millisToWait <= 0L) {
                                    doDelete = true;
                                    break;
                                }
                                this.wait(millisToWait);
                            }
                            else {
                                this.wait();
                            }
                        }
                        catch (KeeperException.NoNodeException ex) {}
                    }
                }
            }
        }
        catch (Exception e) {
            ThreadUtils.checkInterrupted(e);
            doDelete = true;
            throw e;
        }
        finally {
            if (doDelete) {
                this.deleteOurPath(ourPath);
            }
        }
        return haveTheLock;
    }
    
    private void deleteOurPath(final String ourPath) throws Exception {
        try {
            this.client.delete().guaranteed().forPath(ourPath);
        }
        catch (KeeperException.NoNodeException ex) {}
    }
    
    private synchronized void notifyFromWatcher() {
        this.notifyAll();
    }
    
    static {
        REVOKE_MESSAGE = "__REVOKE__".getBytes();
    }
}
