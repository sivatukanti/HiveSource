// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.recipes.locks;

import org.apache.curator.framework.listen.Listenable;
import org.apache.curator.utils.ZKPaths;
import java.io.IOException;
import org.apache.zookeeper.KeeperException;
import org.apache.curator.utils.ThreadUtils;
import org.apache.curator.shaded.com.google.common.collect.ImmutableList;
import org.apache.curator.shaded.com.google.common.base.Preconditions;
import java.util.concurrent.TimeUnit;
import java.util.Iterator;
import java.io.Closeable;
import org.apache.curator.utils.CloseableUtils;
import java.util.Collection;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.recipes.shared.SharedCountListener;
import org.slf4j.LoggerFactory;
import org.apache.curator.framework.recipes.shared.SharedCountReader;
import org.apache.curator.framework.CuratorFramework;
import org.slf4j.Logger;

@Deprecated
public class InterProcessSemaphore
{
    private final Logger log;
    private final LockInternals internals;
    private static final String LOCK_NAME = "lock-";
    
    public InterProcessSemaphore(final CuratorFramework client, final String path, final int maxLeases) {
        this(client, path, maxLeases, null);
    }
    
    public InterProcessSemaphore(final CuratorFramework client, final String path, final SharedCountReader count) {
        this(client, path, 0, count);
    }
    
    private InterProcessSemaphore(final CuratorFramework client, final String path, final int maxLeases, final SharedCountReader count) {
        this.log = LoggerFactory.getLogger(this.getClass());
        this.internals = new LockInternals(client, new StandardLockInternalsDriver(), path, "lock-", (count != null) ? count.getCount() : maxLeases);
        if (count != null) {
            ((Listenable<InterProcessSemaphore$1>)count).addListener(new SharedCountListener() {
                @Override
                public void countHasChanged(final SharedCountReader sharedCount, final int newCount) throws Exception {
                    InterProcessSemaphore.this.internals.setMaxLeases(newCount);
                }
                
                @Override
                public void stateChanged(final CuratorFramework client, final ConnectionState newState) {
                }
            });
        }
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
        final String path = this.internals.attemptLock(-1L, null, null);
        return this.makeLease(path);
    }
    
    public Collection<Lease> acquire(int qty) throws Exception {
        Preconditions.checkArgument(qty > 0, (Object)"qty cannot be 0");
        final ImmutableList.Builder<Lease> builder = ImmutableList.builder();
        try {
            while (qty-- > 0) {
                final String path = this.internals.attemptLock(-1L, null, null);
                builder.add(this.makeLease(path));
            }
        }
        catch (Exception e) {
            ThreadUtils.checkInterrupted(e);
            this.returnAll(builder.build());
            throw e;
        }
        return builder.build();
    }
    
    public Lease acquire(final long time, final TimeUnit unit) throws Exception {
        final String path = this.internals.attemptLock(time, unit, null);
        return (path != null) ? this.makeLease(path) : null;
    }
    
    public Collection<Lease> acquire(int qty, final long time, final TimeUnit unit) throws Exception {
        final long startMs = System.currentTimeMillis();
        final long waitMs = TimeUnit.MILLISECONDS.convert(time, unit);
        Preconditions.checkArgument(qty > 0, (Object)"qty cannot be 0");
        final ImmutableList.Builder<Lease> builder = ImmutableList.builder();
        try {
            while (qty-- > 0) {
                final long elapsedMs = System.currentTimeMillis() - startMs;
                final long thisWaitMs = waitMs - elapsedMs;
                final String path = (thisWaitMs > 0L) ? this.internals.attemptLock(thisWaitMs, TimeUnit.MILLISECONDS, null) : null;
                if (path == null) {
                    this.returnAll(builder.build());
                    return null;
                }
                builder.add(this.makeLease(path));
            }
        }
        catch (Exception e) {
            ThreadUtils.checkInterrupted(e);
            this.returnAll(builder.build());
            throw e;
        }
        return builder.build();
    }
    
    private Lease makeLease(final String path) {
        return new Lease() {
            @Override
            public void close() throws IOException {
                try {
                    InterProcessSemaphore.this.internals.releaseLock(path);
                }
                catch (KeeperException.NoNodeException e) {
                    InterProcessSemaphore.this.log.warn("Lease already released", e);
                }
                catch (Exception e2) {
                    ThreadUtils.checkInterrupted(e2);
                    throw new IOException(e2);
                }
            }
            
            @Override
            public byte[] getData() throws Exception {
                return InterProcessSemaphore.this.internals.getClient().getData().forPath(path);
            }
            
            @Override
            public String getNodeName() {
                return ZKPaths.getNodeFromPath(path);
            }
        };
    }
}
