// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.recipes.atomic;

import org.apache.curator.framework.api.Versionable;
import org.apache.curator.RetryLoop;
import org.apache.curator.framework.api.WatchPathable;
import org.apache.curator.framework.api.BackgroundPathAndBytesable;
import java.util.Arrays;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.data.Stat;
import org.apache.curator.utils.PathUtils;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;

public class DistributedAtomicValue
{
    private final CuratorFramework client;
    private final String path;
    private final RetryPolicy retryPolicy;
    private final PromotedToLock promotedToLock;
    private final InterProcessMutex mutex;
    
    public DistributedAtomicValue(final CuratorFramework client, final String path, final RetryPolicy retryPolicy) {
        this(client, path, retryPolicy, null);
    }
    
    public DistributedAtomicValue(final CuratorFramework client, final String path, final RetryPolicy retryPolicy, final PromotedToLock promotedToLock) {
        this.client = client;
        this.path = PathUtils.validatePath(path);
        this.retryPolicy = retryPolicy;
        this.promotedToLock = promotedToLock;
        this.mutex = ((promotedToLock != null) ? new InterProcessMutex(client, promotedToLock.getPath()) : null);
    }
    
    public AtomicValue<byte[]> get() throws Exception {
        final MutableAtomicValue<byte[]> result = new MutableAtomicValue<byte[]>(null, null, false);
        this.getCurrentValue(result, new Stat());
        result.postValue = result.preValue;
        result.succeeded = true;
        return result;
    }
    
    public void forceSet(final byte[] newValue) throws Exception {
        try {
            this.client.setData().forPath(this.path, newValue);
        }
        catch (KeeperException.NoNodeException dummy) {
            try {
                this.client.create().creatingParentContainersIfNeeded().forPath(this.path, newValue);
            }
            catch (KeeperException.NodeExistsException dummy2) {
                this.client.setData().forPath(this.path, newValue);
            }
        }
    }
    
    public AtomicValue<byte[]> compareAndSet(final byte[] expectedValue, final byte[] newValue) throws Exception {
        final Stat stat = new Stat();
        final MutableAtomicValue<byte[]> result = new MutableAtomicValue<byte[]>(null, null, false);
        final boolean createIt = this.getCurrentValue(result, stat);
        if (!createIt && Arrays.equals(expectedValue, result.preValue)) {
            try {
                ((Versionable<BackgroundPathAndBytesable>)this.client.setData()).withVersion(stat.getVersion()).forPath(this.path, newValue);
                result.succeeded = true;
                result.postValue = newValue;
            }
            catch (KeeperException.BadVersionException dummy) {
                result.succeeded = false;
            }
            catch (KeeperException.NoNodeException dummy2) {
                result.succeeded = false;
            }
        }
        else {
            result.succeeded = false;
        }
        return result;
    }
    
    public AtomicValue<byte[]> trySet(final byte[] newValue) throws Exception {
        final MutableAtomicValue<byte[]> result = new MutableAtomicValue<byte[]>(null, null, false);
        final MakeValue makeValue = new MakeValue() {
            @Override
            public byte[] makeFrom(final byte[] previous) {
                return newValue;
            }
        };
        this.tryOptimistic(result, makeValue);
        if (!result.succeeded() && this.mutex != null) {
            this.tryWithMutex(result, makeValue);
        }
        return result;
    }
    
    public boolean initialize(final byte[] value) throws Exception {
        try {
            this.client.create().creatingParentContainersIfNeeded().forPath(this.path, value);
        }
        catch (KeeperException.NodeExistsException ignore) {
            return false;
        }
        return true;
    }
    
    AtomicValue<byte[]> trySet(final MakeValue makeValue) throws Exception {
        final MutableAtomicValue<byte[]> result = new MutableAtomicValue<byte[]>(null, null, false);
        this.tryOptimistic(result, makeValue);
        if (!result.succeeded() && this.mutex != null) {
            this.tryWithMutex(result, makeValue);
        }
        return result;
    }
    
    RuntimeException createCorruptionException(final byte[] bytes) {
        final StringBuilder str = new StringBuilder();
        str.append('[');
        boolean first = true;
        for (final byte b : bytes) {
            if (first) {
                first = false;
            }
            else {
                str.append(", ");
            }
            str.append("0x").append(Integer.toHexString(b & 0xFF));
        }
        str.append(']');
        return new RuntimeException(String.format("Corrupted data for node \"%s\": %s", this.path, str.toString()));
    }
    
    private boolean getCurrentValue(final MutableAtomicValue<byte[]> result, final Stat stat) throws Exception {
        boolean createIt = false;
        try {
            result.preValue = this.client.getData().storingStatIn(stat).forPath(this.path);
        }
        catch (KeeperException.NoNodeException e) {
            result.preValue = null;
            createIt = true;
        }
        return createIt;
    }
    
    private void tryWithMutex(final MutableAtomicValue<byte[]> result, final MakeValue makeValue) throws Exception {
        final long startMs = System.currentTimeMillis();
        int retryCount = 0;
        if (this.mutex.acquire(this.promotedToLock.getMaxLockTime(), this.promotedToLock.getMaxLockTimeUnit())) {
            try {
                boolean done = false;
                while (!done) {
                    result.stats.incrementPromotedTries();
                    if (this.tryOnce(result, makeValue)) {
                        result.succeeded = true;
                        done = true;
                    }
                    else {
                        if (this.promotedToLock.getRetryPolicy().allowRetry(retryCount++, System.currentTimeMillis() - startMs, RetryLoop.getDefaultRetrySleeper())) {
                            continue;
                        }
                        done = true;
                    }
                }
            }
            finally {
                this.mutex.release();
            }
        }
        result.stats.setPromotedTimeMs(System.currentTimeMillis() - startMs);
    }
    
    private void tryOptimistic(final MutableAtomicValue<byte[]> result, final MakeValue makeValue) throws Exception {
        final long startMs = System.currentTimeMillis();
        int retryCount = 0;
        boolean done = false;
        while (!done) {
            result.stats.incrementOptimisticTries();
            if (this.tryOnce(result, makeValue)) {
                result.succeeded = true;
                done = true;
            }
            else {
                if (this.retryPolicy.allowRetry(retryCount++, System.currentTimeMillis() - startMs, RetryLoop.getDefaultRetrySleeper())) {
                    continue;
                }
                done = true;
            }
        }
        result.stats.setOptimisticTimeMs(System.currentTimeMillis() - startMs);
    }
    
    private boolean tryOnce(final MutableAtomicValue<byte[]> result, final MakeValue makeValue) throws Exception {
        final Stat stat = new Stat();
        final boolean createIt = this.getCurrentValue(result, stat);
        boolean success = false;
        try {
            final byte[] newValue = makeValue.makeFrom(result.preValue);
            if (createIt) {
                this.client.create().creatingParentContainersIfNeeded().forPath(this.path, newValue);
            }
            else {
                ((Versionable<BackgroundPathAndBytesable>)this.client.setData()).withVersion(stat.getVersion()).forPath(this.path, newValue);
            }
            result.postValue = Arrays.copyOf(newValue, newValue.length);
            success = true;
        }
        catch (KeeperException.NodeExistsException ex) {}
        catch (KeeperException.BadVersionException ex2) {}
        catch (KeeperException.NoNodeException ex3) {}
        return success;
    }
}
