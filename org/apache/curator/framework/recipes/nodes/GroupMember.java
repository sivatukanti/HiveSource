// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.recipes.nodes;

import org.apache.curator.utils.ZKPaths;
import java.util.Iterator;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.shaded.com.google.common.collect.ImmutableMap;
import java.util.Map;
import org.apache.curator.utils.CloseableUtils;
import org.apache.curator.shaded.com.google.common.base.Throwables;
import org.apache.curator.utils.ThreadUtils;
import org.apache.curator.shaded.com.google.common.base.Preconditions;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import java.io.Closeable;

public class GroupMember implements Closeable
{
    private final PersistentEphemeralNode pen;
    private final PathChildrenCache cache;
    private final String thisId;
    
    public GroupMember(final CuratorFramework client, final String membershipPath, final String thisId) {
        this(client, membershipPath, thisId, CuratorFrameworkFactory.getLocalAddress());
    }
    
    public GroupMember(final CuratorFramework client, final String membershipPath, final String thisId, final byte[] payload) {
        this.thisId = Preconditions.checkNotNull(thisId, (Object)"thisId cannot be null");
        this.cache = this.newPathChildrenCache(client, membershipPath);
        this.pen = this.newPersistentEphemeralNode(client, membershipPath, thisId, payload);
    }
    
    public void start() {
        this.pen.start();
        try {
            this.cache.start();
        }
        catch (Exception e) {
            ThreadUtils.checkInterrupted(e);
            Throwables.propagate(e);
        }
    }
    
    public void setThisData(final byte[] data) {
        try {
            this.pen.setData(data);
        }
        catch (Exception e) {
            ThreadUtils.checkInterrupted(e);
            Throwables.propagate(e);
        }
    }
    
    @Override
    public void close() {
        CloseableUtils.closeQuietly(this.cache);
        CloseableUtils.closeQuietly(this.pen);
    }
    
    public Map<String, byte[]> getCurrentMembers() {
        final ImmutableMap.Builder<String, byte[]> builder = ImmutableMap.builder();
        boolean thisIdAdded = false;
        for (final ChildData data : this.cache.getCurrentData()) {
            final String id = this.idFromPath(data.getPath());
            thisIdAdded = (thisIdAdded || id.equals(this.thisId));
            builder.put(id, data.getData());
        }
        if (!thisIdAdded) {
            builder.put(this.thisId, this.pen.getData());
        }
        return builder.build();
    }
    
    public String idFromPath(final String path) {
        return ZKPaths.getNodeFromPath(path);
    }
    
    protected PersistentEphemeralNode newPersistentEphemeralNode(final CuratorFramework client, final String membershipPath, final String thisId, final byte[] payload) {
        return new PersistentEphemeralNode(client, PersistentEphemeralNode.Mode.EPHEMERAL, ZKPaths.makePath(membershipPath, thisId), payload);
    }
    
    protected PathChildrenCache newPathChildrenCache(final CuratorFramework client, final String membershipPath) {
        return new PathChildrenCache(client, membershipPath, true);
    }
}
