// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.server.upgrade;

import org.slf4j.LoggerFactory;
import org.apache.jute.InputArchive;
import java.io.IOException;
import org.apache.jute.OutputArchive;
import java.util.Iterator;
import org.apache.zookeeper.txn.ErrorTxn;
import org.apache.zookeeper.txn.SetACLTxn;
import org.apache.zookeeper.txn.SetDataTxn;
import org.apache.zookeeper.txn.DeleteTxn;
import org.apache.zookeeper.txn.CreateTxn;
import org.apache.jute.Record;
import org.apache.zookeeper.txn.TxnHeader;
import java.util.ArrayList;
import java.util.Set;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.data.Stat;
import org.apache.zookeeper.data.ACL;
import java.util.List;
import org.apache.zookeeper.data.StatPersistedV1;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import org.apache.zookeeper.server.WatchManager;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;

public class DataTreeV1
{
    private static final Logger LOG;
    private ConcurrentHashMap<String, DataNodeV1> nodes;
    private WatchManager dataWatches;
    private WatchManager childWatches;
    private Map<Long, HashSet<String>> ephemerals;
    private DataNodeV1 root;
    public volatile long lastProcessedZxid;
    int scount;
    public boolean initialized;
    
    public Map<Long, HashSet<String>> getEphemeralsMap() {
        return this.ephemerals;
    }
    
    public void setEphemeralsMap(final Map<Long, HashSet<String>> ephemerals) {
        this.ephemerals = ephemerals;
    }
    
    public HashSet<String> getEphemerals(final long sessionId) {
        final HashSet<String> retv = this.ephemerals.get(sessionId);
        if (retv == null) {
            return new HashSet<String>();
        }
        HashSet<String> cloned = null;
        synchronized (retv) {
            cloned = (HashSet<String>)retv.clone();
        }
        return cloned;
    }
    
    public Collection<Long> getSessions() {
        return this.ephemerals.keySet();
    }
    
    public DataNodeV1 getNode(final String path) {
        return this.nodes.get(path);
    }
    
    public DataTreeV1() {
        this.nodes = new ConcurrentHashMap<String, DataNodeV1>();
        this.dataWatches = new WatchManager();
        this.childWatches = new WatchManager();
        this.ephemerals = new ConcurrentHashMap<Long, HashSet<String>>();
        this.root = new DataNodeV1(null, new byte[0], null, new StatPersistedV1());
        this.lastProcessedZxid = 0L;
        this.initialized = false;
        this.nodes.put("", this.root);
        this.nodes.put("/", this.root);
    }
    
    public static void copyStatPersisted(final StatPersistedV1 from, final StatPersistedV1 to) {
        to.setAversion(from.getAversion());
        to.setCtime(from.getCtime());
        to.setCversion(from.getCversion());
        to.setCzxid(from.getCzxid());
        to.setMtime(from.getMtime());
        to.setMzxid(from.getMzxid());
        to.setVersion(from.getVersion());
        to.setEphemeralOwner(from.getEphemeralOwner());
    }
    
    public static void copyStat(final Stat from, final Stat to) {
        to.setAversion(from.getAversion());
        to.setCtime(from.getCtime());
        to.setCversion(from.getCversion());
        to.setCzxid(from.getCzxid());
        to.setMtime(from.getMtime());
        to.setMzxid(from.getMzxid());
        to.setVersion(from.getVersion());
        to.setEphemeralOwner(from.getEphemeralOwner());
        to.setDataLength(from.getDataLength());
        to.setNumChildren(from.getNumChildren());
    }
    
    public String createNode(final String path, final byte[] data, final List<ACL> acl, final long ephemeralOwner, final long zxid, final long time) throws KeeperException.NoNodeException, KeeperException.NodeExistsException {
        final int lastSlash = path.lastIndexOf(47);
        final String parentName = path.substring(0, lastSlash);
        final String childName = path.substring(lastSlash + 1);
        final StatPersistedV1 stat = new StatPersistedV1();
        stat.setCtime(time);
        stat.setMtime(time);
        stat.setCzxid(zxid);
        stat.setMzxid(zxid);
        stat.setVersion(0);
        stat.setAversion(0);
        stat.setEphemeralOwner(ephemeralOwner);
        final DataNodeV1 parent = this.nodes.get(parentName);
        if (parent == null) {
            throw new KeeperException.NoNodeException();
        }
        synchronized (parent) {
            if (parent.children.contains(childName)) {
                throw new KeeperException.NodeExistsException();
            }
            int cver = parent.stat.getCversion();
            ++cver;
            parent.stat.setCversion(cver);
            final DataNodeV1 child = new DataNodeV1(parent, data, acl, stat);
            parent.children.add(childName);
            this.nodes.put(path, child);
            if (ephemeralOwner != 0L) {
                HashSet<String> list = this.ephemerals.get(ephemeralOwner);
                if (list == null) {
                    list = new HashSet<String>();
                    this.ephemerals.put(ephemeralOwner, list);
                }
                synchronized (list) {
                    list.add(path);
                }
            }
        }
        this.dataWatches.triggerWatch(path, Watcher.Event.EventType.NodeCreated);
        this.childWatches.triggerWatch(parentName.equals("") ? "/" : parentName, Watcher.Event.EventType.NodeChildrenChanged);
        return path;
    }
    
    public void deleteNode(final String path) throws KeeperException.NoNodeException {
        final int lastSlash = path.lastIndexOf(47);
        final String parentName = path.substring(0, lastSlash);
        final String childName = path.substring(lastSlash + 1);
        final DataNodeV1 node = this.nodes.get(path);
        if (node == null) {
            throw new KeeperException.NoNodeException();
        }
        this.nodes.remove(path);
        final DataNodeV1 parent = this.nodes.get(parentName);
        if (parent == null) {
            throw new KeeperException.NoNodeException();
        }
        synchronized (parent) {
            parent.children.remove(childName);
            parent.stat.setCversion(parent.stat.getCversion() + 1);
            final long eowner = node.stat.getEphemeralOwner();
            if (eowner != 0L) {
                final HashSet<String> nodes = this.ephemerals.get(eowner);
                if (nodes != null) {
                    synchronized (nodes) {
                        nodes.remove(path);
                    }
                }
            }
            node.parent = null;
        }
        final Set<Watcher> processed = this.dataWatches.triggerWatch(path, Watcher.Event.EventType.NodeDeleted);
        this.childWatches.triggerWatch(path, Watcher.Event.EventType.NodeDeleted, processed);
        this.childWatches.triggerWatch(parentName.equals("") ? "/" : parentName, Watcher.Event.EventType.NodeChildrenChanged);
    }
    
    public Stat setData(final String path, final byte[] data, final int version, final long zxid, final long time) throws KeeperException.NoNodeException {
        final Stat s = new Stat();
        final DataNodeV1 n = this.nodes.get(path);
        if (n == null) {
            throw new KeeperException.NoNodeException();
        }
        synchronized (n) {
            n.data = data;
            n.stat.setMtime(time);
            n.stat.setMzxid(zxid);
            n.stat.setVersion(version);
            n.copyStat(s);
        }
        this.dataWatches.triggerWatch(path, Watcher.Event.EventType.NodeDataChanged);
        return s;
    }
    
    public byte[] getData(final String path, final Stat stat, final Watcher watcher) throws KeeperException.NoNodeException {
        final DataNodeV1 n = this.nodes.get(path);
        if (n == null) {
            throw new KeeperException.NoNodeException();
        }
        synchronized (n) {
            n.copyStat(stat);
            if (watcher != null) {
                this.dataWatches.addWatch(path, watcher);
            }
            return n.data;
        }
    }
    
    public Stat statNode(final String path, final Watcher watcher) throws KeeperException.NoNodeException {
        final Stat stat = new Stat();
        final DataNodeV1 n = this.nodes.get(path);
        if (watcher != null) {
            this.dataWatches.addWatch(path, watcher);
        }
        if (n == null) {
            throw new KeeperException.NoNodeException();
        }
        synchronized (n) {
            n.copyStat(stat);
            return stat;
        }
    }
    
    public ArrayList<String> getChildren(final String path, final Stat stat, final Watcher watcher) throws KeeperException.NoNodeException {
        final DataNodeV1 n = this.nodes.get(path);
        if (n == null) {
            throw new KeeperException.NoNodeException();
        }
        synchronized (n) {
            final ArrayList<String> children = new ArrayList<String>();
            children.addAll(n.children);
            if (watcher != null) {
                this.childWatches.addWatch(path, watcher);
            }
            return children;
        }
    }
    
    public Stat setACL(final String path, final List<ACL> acl, final int version) throws KeeperException.NoNodeException {
        final Stat stat = new Stat();
        final DataNodeV1 n = this.nodes.get(path);
        if (n == null) {
            throw new KeeperException.NoNodeException();
        }
        synchronized (n) {
            n.stat.setAversion(version);
            n.acl = acl;
            n.copyStat(stat);
            return stat;
        }
    }
    
    public List<ACL> getACL(final String path, final Stat stat) throws KeeperException.NoNodeException {
        final DataNodeV1 n = this.nodes.get(path);
        if (n == null) {
            throw new KeeperException.NoNodeException();
        }
        synchronized (n) {
            n.copyStat(stat);
            return new ArrayList<ACL>(n.acl);
        }
    }
    
    public ProcessTxnResult processTxn(final TxnHeader header, final Record txn) {
        final ProcessTxnResult rc = new ProcessTxnResult();
        String debug = "";
        try {
            rc.clientId = header.getClientId();
            rc.cxid = header.getCxid();
            rc.zxid = header.getZxid();
            if (rc.zxid > this.lastProcessedZxid) {
                this.lastProcessedZxid = rc.zxid;
            }
            switch (header.getType()) {
                case 1: {
                    final CreateTxn createTxn = (CreateTxn)txn;
                    debug = "Create transaction for " + createTxn.getPath();
                    this.createNode(createTxn.getPath(), createTxn.getData(), createTxn.getAcl(), createTxn.getEphemeral() ? header.getClientId() : 0L, header.getZxid(), header.getTime());
                    break;
                }
                case 2: {
                    final DeleteTxn deleteTxn = (DeleteTxn)txn;
                    debug = "Delete transaction for " + deleteTxn.getPath();
                    this.deleteNode(deleteTxn.getPath());
                    break;
                }
                case 5: {
                    final SetDataTxn setDataTxn = (SetDataTxn)txn;
                    debug = "Set data for  transaction for " + setDataTxn.getPath();
                    break;
                }
                case 7: {
                    final SetACLTxn setACLTxn = (SetACLTxn)txn;
                    debug = "Set ACL for  transaction for " + setACLTxn.getPath();
                    break;
                }
                case -11: {
                    this.killSession(header.getClientId());
                    break;
                }
                case -1: {
                    final ErrorTxn errorTxn = (ErrorTxn)txn;
                    break;
                }
            }
        }
        catch (KeeperException e) {
            if (this.initialized || (e.code() != KeeperException.Code.NONODE && e.code() != KeeperException.Code.NODEEXISTS)) {
                DataTreeV1.LOG.warn("Failed:" + debug, e);
            }
        }
        return rc;
    }
    
    void killSession(final long session) {
        final HashSet<String> list = this.ephemerals.remove(session);
        if (list != null) {
            for (final String path : list) {
                try {
                    this.deleteNode(path);
                    if (!DataTreeV1.LOG.isDebugEnabled()) {
                        continue;
                    }
                    DataTreeV1.LOG.debug("Deleting ephemeral node " + path + " for session 0x" + Long.toHexString(session));
                }
                catch (KeeperException.NoNodeException e) {
                    DataTreeV1.LOG.warn("Ignoring NoNodeException for path " + path + " while removing ephemeral for dead session 0x" + Long.toHexString(session));
                }
            }
        }
    }
    
    void serializeNode(final OutputArchive oa, final StringBuilder path) throws IOException, InterruptedException {
        final String pathString = path.toString();
        final DataNodeV1 node = this.getNode(pathString);
        if (node == null) {
            return;
        }
        String[] children = null;
        synchronized (node) {
            ++this.scount;
            oa.writeString(pathString, "path");
            oa.writeRecord(node, "node");
            children = node.children.toArray(new String[node.children.size()]);
        }
        path.append('/');
        final int off = path.length();
        if (children != null) {
            for (final String child : children) {
                path.delete(off, Integer.MAX_VALUE);
                path.append(child);
                this.serializeNode(oa, path);
            }
        }
    }
    
    public void serialize(final OutputArchive oa, final String tag) throws IOException, InterruptedException {
        this.scount = 0;
        this.serializeNode(oa, new StringBuilder(""));
        if (this.root != null) {
            oa.writeString("/", "path");
        }
    }
    
    public void deserialize(final InputArchive ia, final String tag) throws IOException {
        this.nodes.clear();
        for (String path = ia.readString("path"); !path.equals("/"); path = ia.readString("path")) {
            final DataNodeV1 node = new DataNodeV1();
            ia.readRecord(node, "node");
            this.nodes.put(path, node);
            final int lastSlash = path.lastIndexOf(47);
            if (lastSlash == -1) {
                this.root = node;
            }
            else {
                final String parentPath = path.substring(0, lastSlash);
                node.parent = this.nodes.get(parentPath);
                node.parent.children.add(path.substring(lastSlash + 1));
                final long eowner = node.stat.getEphemeralOwner();
                if (eowner != 0L) {
                    HashSet<String> list = this.ephemerals.get(eowner);
                    if (list == null) {
                        list = new HashSet<String>();
                        this.ephemerals.put(eowner, list);
                    }
                    list.add(path);
                }
            }
        }
        this.nodes.put("/", this.root);
    }
    
    public String dumpEphemerals() {
        final Set<Long> keys = this.ephemerals.keySet();
        final StringBuilder sb = new StringBuilder("Sessions with Ephemerals (" + keys.size() + "):\n");
        for (final long k : keys) {
            sb.append("0x" + Long.toHexString(k));
            sb.append(":\n");
            final HashSet<String> tmp = this.ephemerals.get(k);
            synchronized (tmp) {
                for (final String path : tmp) {
                    sb.append("\t" + path + "\n");
                }
            }
        }
        return sb.toString();
    }
    
    public void removeCnxn(final Watcher watcher) {
        this.dataWatches.removeWatcher(watcher);
        this.childWatches.removeWatcher(watcher);
    }
    
    public void clear() {
        this.root = null;
        this.nodes.clear();
        this.ephemerals.clear();
    }
    
    static {
        LOG = LoggerFactory.getLogger(DataTreeV1.class);
    }
    
    public static class ProcessTxnResult
    {
        public long clientId;
        public int cxid;
        public long zxid;
        
        @Override
        public boolean equals(final Object o) {
            if (o instanceof ProcessTxnResult) {
                final ProcessTxnResult other = (ProcessTxnResult)o;
                return other.clientId == this.clientId && other.cxid == this.cxid;
            }
            return false;
        }
        
        @Override
        public int hashCode() {
            return (int)((this.clientId ^ (long)this.cxid) % 2147483647L);
        }
    }
}
