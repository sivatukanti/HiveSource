// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.server.upgrade;

import java.util.Iterator;
import org.apache.jute.OutputArchive;
import java.io.IOException;
import org.apache.jute.Index;
import java.util.ArrayList;
import org.apache.jute.InputArchive;
import org.apache.zookeeper.data.Stat;
import java.util.HashSet;
import org.apache.zookeeper.data.StatPersistedV1;
import org.apache.zookeeper.data.ACL;
import java.util.List;
import org.apache.jute.Record;

public class DataNodeV1 implements Record
{
    DataNodeV1 parent;
    byte[] data;
    public List<ACL> acl;
    public StatPersistedV1 stat;
    HashSet<String> children;
    
    DataNodeV1() {
        this.children = new HashSet<String>();
    }
    
    DataNodeV1(final DataNodeV1 parent, final byte[] data, final List<ACL> acl, final StatPersistedV1 stat) {
        this.children = new HashSet<String>();
        this.parent = parent;
        this.data = data;
        this.acl = acl;
        this.stat = stat;
        this.children = new HashSet<String>();
    }
    
    public void setChildren(final HashSet<String> children) {
        this.children = children;
    }
    
    public HashSet<String> getChildren() {
        return this.children;
    }
    
    public void copyStat(final Stat to) {
        to.setAversion(this.stat.getAversion());
        to.setCtime(this.stat.getCtime());
        to.setCversion(this.stat.getCversion());
        to.setCzxid(this.stat.getCzxid());
        to.setMtime(this.stat.getMtime());
        to.setMzxid(this.stat.getMzxid());
        to.setVersion(this.stat.getVersion());
        to.setEphemeralOwner(this.stat.getEphemeralOwner());
        to.setDataLength(this.data.length);
        to.setNumChildren(this.children.size());
    }
    
    @Override
    public void deserialize(final InputArchive archive, final String tag) throws IOException {
        archive.startRecord("node");
        this.data = archive.readBuffer("data");
        final Index i = archive.startVector("acl");
        if (i != null) {
            this.acl = new ArrayList<ACL>();
            while (!i.done()) {
                final ACL a = new ACL();
                a.deserialize(archive, "aclEntry");
                this.acl.add(a);
                i.incr();
            }
        }
        archive.endVector("acl");
        (this.stat = new StatPersistedV1()).deserialize(archive, "stat");
        archive.endRecord("node");
    }
    
    @Override
    public synchronized void serialize(final OutputArchive archive, final String tag) throws IOException {
        archive.startRecord(this, "node");
        archive.writeBuffer(this.data, "data");
        archive.startVector(this.acl, "acl");
        if (this.acl != null) {
            for (final ACL a : this.acl) {
                a.serialize(archive, "aclEntry");
            }
        }
        archive.endVector(this.acl, "acl");
        this.stat.serialize(archive, "stat");
        archive.endRecord(this, "node");
    }
}
