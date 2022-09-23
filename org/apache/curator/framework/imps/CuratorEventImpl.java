// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.imps;

import java.util.Collection;
import org.apache.curator.shaded.com.google.common.collect.ImmutableList;
import java.util.Arrays;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.data.Stat;
import java.util.List;
import org.apache.curator.framework.api.CuratorEventType;
import org.apache.curator.framework.api.CuratorEvent;

class CuratorEventImpl implements CuratorEvent
{
    private final CuratorEventType type;
    private final int resultCode;
    private final String path;
    private final String name;
    private final List<String> children;
    private final Object context;
    private final Stat stat;
    private final byte[] data;
    private final WatchedEvent watchedEvent;
    private final List<ACL> aclList;
    
    @Override
    public CuratorEventType getType() {
        return this.type;
    }
    
    @Override
    public int getResultCode() {
        return this.resultCode;
    }
    
    @Override
    public String getPath() {
        return this.path;
    }
    
    @Override
    public Object getContext() {
        return this.context;
    }
    
    @Override
    public Stat getStat() {
        return this.stat;
    }
    
    @Override
    public byte[] getData() {
        return this.data;
    }
    
    @Override
    public String getName() {
        return this.name;
    }
    
    @Override
    public List<String> getChildren() {
        return this.children;
    }
    
    @Override
    public WatchedEvent getWatchedEvent() {
        return this.watchedEvent;
    }
    
    @Override
    public List<ACL> getACLList() {
        return this.aclList;
    }
    
    @Override
    public String toString() {
        return "CuratorEventImpl{type=" + this.type + ", resultCode=" + this.resultCode + ", path='" + this.path + '\'' + ", name='" + this.name + '\'' + ", children=" + this.children + ", context=" + this.context + ", stat=" + this.stat + ", data=" + Arrays.toString(this.data) + ", watchedEvent=" + this.watchedEvent + ", aclList=" + this.aclList + '}';
    }
    
    CuratorEventImpl(final CuratorFrameworkImpl client, final CuratorEventType type, final int resultCode, final String path, final String name, final Object context, final Stat stat, final byte[] data, final List<String> children, final WatchedEvent watchedEvent, final List<ACL> aclList) {
        this.type = type;
        this.resultCode = resultCode;
        this.path = client.unfixForNamespace(path);
        this.name = name;
        this.context = context;
        this.stat = stat;
        this.data = data;
        this.children = children;
        this.watchedEvent = ((watchedEvent != null) ? new NamespaceWatchedEvent(client, watchedEvent) : watchedEvent);
        this.aclList = (List<ACL>)((aclList != null) ? ImmutableList.copyOf((Collection<?>)aclList) : null);
    }
}
