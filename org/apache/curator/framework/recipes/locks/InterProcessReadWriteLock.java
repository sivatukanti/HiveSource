// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.recipes.locks;

import org.apache.curator.shaded.com.google.common.collect.ImmutableList;
import org.apache.curator.shaded.com.google.common.collect.Iterables;
import com.google.common.base.Predicate;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Arrays;
import org.apache.curator.framework.CuratorFramework;

public class InterProcessReadWriteLock
{
    private final InterProcessMutex readMutex;
    private final InterProcessMutex writeMutex;
    private static final String READ_LOCK_NAME = "__READ__";
    private static final String WRITE_LOCK_NAME = "__WRIT__";
    
    public InterProcessReadWriteLock(final CuratorFramework client, final String basePath) {
        this(client, basePath, null);
    }
    
    public InterProcessReadWriteLock(final CuratorFramework client, final String basePath, byte[] lockData) {
        lockData = (byte[])((lockData == null) ? null : Arrays.copyOf(lockData, lockData.length));
        this.writeMutex = new InternalInterProcessMutex(client, basePath, "__WRIT__", lockData, 1, new SortingLockInternalsDriver() {
            @Override
            public PredicateResults getsTheLock(final CuratorFramework client, final List<String> children, final String sequenceNodeName, final int maxLeases) throws Exception {
                return super.getsTheLock(client, children, sequenceNodeName, maxLeases);
            }
        });
        this.readMutex = new InternalInterProcessMutex(client, basePath, "__READ__", lockData, Integer.MAX_VALUE, new SortingLockInternalsDriver() {
            @Override
            public PredicateResults getsTheLock(final CuratorFramework client, final List<String> children, final String sequenceNodeName, final int maxLeases) throws Exception {
                return InterProcessReadWriteLock.this.readLockPredicate(children, sequenceNodeName);
            }
        });
    }
    
    public InterProcessMutex readLock() {
        return this.readMutex;
    }
    
    public InterProcessMutex writeLock() {
        return this.writeMutex;
    }
    
    private PredicateResults readLockPredicate(final List<String> children, final String sequenceNodeName) throws Exception {
        if (this.writeMutex.isOwnedByCurrentThread()) {
            return new PredicateResults(null, true);
        }
        int index = 0;
        int firstWriteIndex = Integer.MAX_VALUE;
        int ourIndex = -1;
        for (final String node : children) {
            if (node.contains("__WRIT__")) {
                firstWriteIndex = Math.min(index, firstWriteIndex);
            }
            else if (node.startsWith(sequenceNodeName)) {
                ourIndex = index;
                break;
            }
            ++index;
        }
        StandardLockInternalsDriver.validateOurIndex(sequenceNodeName, ourIndex);
        final boolean getsTheLock = ourIndex < firstWriteIndex;
        final String pathToWatch = getsTheLock ? null : children.get(firstWriteIndex);
        return new PredicateResults(pathToWatch, getsTheLock);
    }
    
    private static class SortingLockInternalsDriver extends StandardLockInternalsDriver
    {
        @Override
        public final String fixForSorting(String str, final String lockName) {
            str = super.fixForSorting(str, "__READ__");
            str = super.fixForSorting(str, "__WRIT__");
            return str;
        }
    }
    
    private static class InternalInterProcessMutex extends InterProcessMutex
    {
        private final String lockName;
        private final byte[] lockData;
        
        InternalInterProcessMutex(final CuratorFramework client, final String path, final String lockName, final byte[] lockData, final int maxLeases, final LockInternalsDriver driver) {
            super(client, path, lockName, maxLeases, driver);
            this.lockName = lockName;
            this.lockData = lockData;
        }
        
        @Override
        public Collection<String> getParticipantNodes() throws Exception {
            final Collection<String> nodes = super.getParticipantNodes();
            final Iterable<String> filtered = Iterables.filter(nodes, new Predicate<String>() {
                @Override
                public boolean apply(final String node) {
                    return node.contains(InternalInterProcessMutex.this.lockName);
                }
            });
            return (Collection<String>)ImmutableList.copyOf((Iterable<?>)filtered);
        }
        
        @Override
        protected byte[] getLockNodeBytes() {
            return this.lockData;
        }
    }
}
