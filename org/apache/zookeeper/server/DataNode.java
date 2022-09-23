// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.server;

import org.apache.jute.OutputArchive;
import java.io.IOException;
import org.apache.jute.InputArchive;
import org.apache.zookeeper.data.Stat;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.apache.zookeeper.data.StatPersisted;
import org.apache.jute.Record;

public class DataNode implements Record
{
    DataNode parent;
    byte[] data;
    Long acl;
    public StatPersisted stat;
    private Set<String> children;
    private static final Set<String> EMPTY_SET;
    
    DataNode() {
        this.children = null;
    }
    
    public DataNode(final DataNode parent, final byte[] data, final Long acl, final StatPersisted stat) {
        this.children = null;
        this.parent = parent;
        this.data = data;
        this.acl = acl;
        this.stat = stat;
    }
    
    public synchronized boolean addChild(final String child) {
        if (this.children == null) {
            this.children = new HashSet<String>(8);
        }
        return this.children.add(child);
    }
    
    public synchronized boolean removeChild(final String child) {
        return this.children != null && this.children.remove(child);
    }
    
    public synchronized void setChildren(final HashSet<String> children) {
        this.children = children;
    }
    
    public synchronized Set<String> getChildren() {
        if (this.children == null) {
            return DataNode.EMPTY_SET;
        }
        return Collections.unmodifiableSet((Set<? extends String>)this.children);
    }
    
    public synchronized void copyStat(final Stat to) {
        to.setAversion(this.stat.getAversion());
        to.setCtime(this.stat.getCtime());
        to.setCzxid(this.stat.getCzxid());
        to.setMtime(this.stat.getMtime());
        to.setMzxid(this.stat.getMzxid());
        to.setPzxid(this.stat.getPzxid());
        to.setVersion(this.stat.getVersion());
        to.setEphemeralOwner(this.stat.getEphemeralOwner());
        to.setDataLength((this.data == null) ? 0 : this.data.length);
        int numChildren = 0;
        if (this.children != null) {
            numChildren = this.children.size();
        }
        to.setCversion(this.stat.getCversion() * 2 - numChildren);
        to.setNumChildren(numChildren);
    }
    
    @Override
    public synchronized void deserialize(final InputArchive archive, final String tag) throws IOException {
        archive.startRecord("node");
        this.data = archive.readBuffer("data");
        this.acl = archive.readLong("acl");
        (this.stat = new StatPersisted()).deserialize(archive, "statpersisted");
        archive.endRecord("node");
    }
    
    @Override
    public synchronized void serialize(final OutputArchive archive, final String tag) throws IOException {
        archive.startRecord(this, "node");
        archive.writeBuffer(this.data, "data");
        archive.writeLong(this.acl, "acl");
        this.stat.serialize(archive, "statpersisted");
        archive.endRecord(this, "node");
    }
    
    static {
        EMPTY_SET = Collections.emptySet();
    }
}
