// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.recipes.locks;

import org.slf4j.LoggerFactory;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.CreateMode;
import org.apache.curator.framework.api.ACLBackgroundPathAndBytesable;
import java.util.List;
import org.apache.curator.framework.CuratorFramework;
import org.slf4j.Logger;

public class StandardLockInternalsDriver implements LockInternalsDriver
{
    private static final Logger log;
    
    @Override
    public PredicateResults getsTheLock(final CuratorFramework client, final List<String> children, final String sequenceNodeName, final int maxLeases) throws Exception {
        final int ourIndex = children.indexOf(sequenceNodeName);
        validateOurIndex(sequenceNodeName, ourIndex);
        final boolean getsTheLock = ourIndex < maxLeases;
        final String pathToWatch = getsTheLock ? null : children.get(ourIndex - maxLeases);
        return new PredicateResults(pathToWatch, getsTheLock);
    }
    
    @Override
    public String createsTheLock(final CuratorFramework client, final String path, final byte[] lockNodeBytes) throws Exception {
        String ourPath;
        if (lockNodeBytes != null) {
            ourPath = client.create().creatingParentContainersIfNeeded().withProtection().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath(path, lockNodeBytes);
        }
        else {
            ourPath = client.create().creatingParentContainersIfNeeded().withProtection().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath(path);
        }
        return ourPath;
    }
    
    @Override
    public String fixForSorting(final String str, final String lockName) {
        return standardFixForSorting(str, lockName);
    }
    
    public static String standardFixForSorting(final String str, final String lockName) {
        int index = str.lastIndexOf(lockName);
        if (index >= 0) {
            index += lockName.length();
            return (index <= str.length()) ? str.substring(index) : "";
        }
        return str;
    }
    
    static void validateOurIndex(final String sequenceNodeName, final int ourIndex) throws KeeperException {
        if (ourIndex < 0) {
            throw new KeeperException.NoNodeException("Sequential path not found: " + sequenceNodeName);
        }
    }
    
    static {
        log = LoggerFactory.getLogger(StandardLockInternalsDriver.class);
    }
}
