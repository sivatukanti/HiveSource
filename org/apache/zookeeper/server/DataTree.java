// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.server;

import org.slf4j.LoggerFactory;
import org.apache.zookeeper.WatchedEvent;
import java.io.PrintWriter;
import org.apache.jute.InputArchive;
import org.apache.jute.OutputArchive;
import java.io.IOException;
import java.nio.ByteBuffer;
import org.apache.zookeeper.txn.Txn;
import org.apache.zookeeper.txn.MultiTxn;
import org.apache.zookeeper.txn.CheckVersionTxn;
import org.apache.zookeeper.txn.ErrorTxn;
import org.apache.zookeeper.txn.SetACLTxn;
import org.apache.zookeeper.txn.SetDataTxn;
import org.apache.zookeeper.txn.DeleteTxn;
import org.apache.zookeeper.txn.CreateTxn;
import org.apache.jute.Record;
import org.apache.zookeeper.txn.TxnHeader;
import org.apache.zookeeper.server.upgrade.DataNodeV1;
import java.util.ArrayList;
import java.util.Set;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.data.ACL;
import java.util.List;
import org.apache.zookeeper.StatsTrack;
import org.apache.zookeeper.Quotas;
import org.apache.zookeeper.data.Stat;
import org.apache.zookeeper.data.StatPersisted;
import java.util.Iterator;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import org.apache.zookeeper.common.PathTrie;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;

public class DataTree
{
    private static final Logger LOG;
    private final ConcurrentHashMap<String, DataNode> nodes;
    private final WatchManager dataWatches;
    private final WatchManager childWatches;
    private static final String rootZookeeper = "/";
    private static final String procZookeeper = "/zookeeper";
    private static final String procChildZookeeper;
    private static final String quotaZookeeper = "/zookeeper/quota";
    private static final String quotaChildZookeeper;
    private final PathTrie pTrie;
    private final Map<Long, HashSet<String>> ephemerals;
    private final ReferenceCountedACLCache aclCache;
    private DataNode root;
    private DataNode procDataNode;
    private DataNode quotaDataNode;
    public volatile long lastProcessedZxid;
    int scount;
    public boolean initialized;
    static final /* synthetic */ boolean $assertionsDisabled;
    
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
    
    public Map<Long, HashSet<String>> getEphemeralsMap() {
        return this.ephemerals;
    }
    
    public Collection<Long> getSessions() {
        return this.ephemerals.keySet();
    }
    
    public void addDataNode(final String path, final DataNode node) {
        this.nodes.put(path, node);
    }
    
    public DataNode getNode(final String path) {
        return this.nodes.get(path);
    }
    
    public int getNodeCount() {
        return this.nodes.size();
    }
    
    public int getWatchCount() {
        return this.dataWatches.size() + this.childWatches.size();
    }
    
    public int getEphemeralsCount() {
        final Map<Long, HashSet<String>> map = this.getEphemeralsMap();
        int result = 0;
        for (final HashSet<String> set : map.values()) {
            result += set.size();
        }
        return result;
    }
    
    public long approximateDataSize() {
        long result = 0L;
        for (final Map.Entry<String, DataNode> entry : this.nodes.entrySet()) {
            final DataNode value = entry.getValue();
            synchronized (value) {
                result += entry.getKey().length();
                result += ((value.data == null) ? 0 : value.data.length);
            }
        }
        return result;
    }
    
    public DataTree() {
        this.nodes = new ConcurrentHashMap<String, DataNode>();
        this.dataWatches = new WatchManager();
        this.childWatches = new WatchManager();
        this.pTrie = new PathTrie();
        this.ephemerals = new ConcurrentHashMap<Long, HashSet<String>>();
        this.aclCache = new ReferenceCountedACLCache();
        this.root = new DataNode(null, new byte[0], -1L, new StatPersisted());
        this.procDataNode = new DataNode(this.root, new byte[0], -1L, new StatPersisted());
        this.quotaDataNode = new DataNode(this.procDataNode, new byte[0], -1L, new StatPersisted());
        this.lastProcessedZxid = 0L;
        this.initialized = false;
        this.nodes.put("", this.root);
        this.nodes.put("/", this.root);
        this.root.addChild(DataTree.procChildZookeeper);
        this.nodes.put("/zookeeper", this.procDataNode);
        this.procDataNode.addChild(DataTree.quotaChildZookeeper);
        this.nodes.put("/zookeeper/quota", this.quotaDataNode);
    }
    
    boolean isSpecialPath(final String path) {
        return "/".equals(path) || "/zookeeper".equals(path) || "/zookeeper/quota".equals(path);
    }
    
    public static void copyStatPersisted(final StatPersisted from, final StatPersisted to) {
        to.setAversion(from.getAversion());
        to.setCtime(from.getCtime());
        to.setCversion(from.getCversion());
        to.setCzxid(from.getCzxid());
        to.setMtime(from.getMtime());
        to.setMzxid(from.getMzxid());
        to.setPzxid(from.getPzxid());
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
        to.setPzxid(from.getPzxid());
        to.setVersion(from.getVersion());
        to.setEphemeralOwner(from.getEphemeralOwner());
        to.setDataLength(from.getDataLength());
        to.setNumChildren(from.getNumChildren());
    }
    
    public void updateCount(final String lastPrefix, final int diff) {
        final String statNode = Quotas.statPath(lastPrefix);
        DataNode node = this.nodes.get(statNode);
        StatsTrack updatedStat = null;
        if (node == null) {
            DataTree.LOG.error("Missing count node for stat " + statNode);
            return;
        }
        synchronized (node) {
            updatedStat = new StatsTrack(new String(node.data));
            updatedStat.setCount(updatedStat.getCount() + diff);
            node.data = updatedStat.toString().getBytes();
        }
        final String quotaNode = Quotas.quotaPath(lastPrefix);
        node = this.nodes.get(quotaNode);
        StatsTrack thisStats = null;
        if (node == null) {
            DataTree.LOG.error("Missing count node for quota " + quotaNode);
            return;
        }
        synchronized (node) {
            thisStats = new StatsTrack(new String(node.data));
        }
        if (thisStats.getCount() > -1 && thisStats.getCount() < updatedStat.getCount()) {
            DataTree.LOG.warn("Quota exceeded: " + lastPrefix + " count=" + updatedStat.getCount() + " limit=" + thisStats.getCount());
        }
    }
    
    public void updateBytes(final String lastPrefix, final long diff) {
        final String statNode = Quotas.statPath(lastPrefix);
        DataNode node = this.nodes.get(statNode);
        if (node == null) {
            DataTree.LOG.error("Missing stat node for bytes " + statNode);
            return;
        }
        StatsTrack updatedStat = null;
        synchronized (node) {
            updatedStat = new StatsTrack(new String(node.data));
            updatedStat.setBytes(updatedStat.getBytes() + diff);
            node.data = updatedStat.toString().getBytes();
        }
        final String quotaNode = Quotas.quotaPath(lastPrefix);
        node = this.nodes.get(quotaNode);
        if (node == null) {
            DataTree.LOG.error("Missing quota node for bytes " + quotaNode);
            return;
        }
        StatsTrack thisStats = null;
        synchronized (node) {
            thisStats = new StatsTrack(new String(node.data));
        }
        if (thisStats.getBytes() > -1L && thisStats.getBytes() < updatedStat.getBytes()) {
            DataTree.LOG.warn("Quota exceeded: " + lastPrefix + " bytes=" + updatedStat.getBytes() + " limit=" + thisStats.getBytes());
        }
    }
    
    public String createNode(final String path, final byte[] data, final List<ACL> acl, final long ephemeralOwner, int parentCVersion, final long zxid, final long time) throws KeeperException.NoNodeException, KeeperException.NodeExistsException {
        final int lastSlash = path.lastIndexOf(47);
        final String parentName = path.substring(0, lastSlash);
        final String childName = path.substring(lastSlash + 1);
        final StatPersisted stat = new StatPersisted();
        stat.setCtime(time);
        stat.setMtime(time);
        stat.setCzxid(zxid);
        stat.setMzxid(zxid);
        stat.setPzxid(zxid);
        stat.setVersion(0);
        stat.setAversion(0);
        stat.setEphemeralOwner(ephemeralOwner);
        final DataNode parent = this.nodes.get(parentName);
        if (parent == null) {
            throw new KeeperException.NoNodeException();
        }
        synchronized (parent) {
            final Set<String> children = parent.getChildren();
            if (children.contains(childName)) {
                throw new KeeperException.NodeExistsException();
            }
            if (parentCVersion == -1) {
                parentCVersion = parent.stat.getCversion();
                ++parentCVersion;
            }
            parent.stat.setCversion(parentCVersion);
            parent.stat.setPzxid(zxid);
            final Long longval = this.aclCache.convertAcls(acl);
            final DataNode child = new DataNode(parent, data, longval, stat);
            parent.addChild(childName);
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
        if (parentName.startsWith("/zookeeper/quota")) {
            if ("zookeeper_limits".equals(childName)) {
                this.pTrie.addPath(parentName.substring("/zookeeper/quota".length()));
            }
            if ("zookeeper_stats".equals(childName)) {
                this.updateQuotaForPath(parentName.substring("/zookeeper/quota".length()));
            }
        }
        final String lastPrefix;
        if ((lastPrefix = this.getMaxPrefixWithQuota(path)) != null) {
            this.updateCount(lastPrefix, 1);
            this.updateBytes(lastPrefix, (data == null) ? 0L : ((long)data.length));
        }
        this.dataWatches.triggerWatch(path, Watcher.Event.EventType.NodeCreated);
        this.childWatches.triggerWatch(parentName.equals("") ? "/" : parentName, Watcher.Event.EventType.NodeChildrenChanged);
        return path;
    }
    
    public void deleteNode(final String path, final long zxid) throws KeeperException.NoNodeException {
        final int lastSlash = path.lastIndexOf(47);
        final String parentName = path.substring(0, lastSlash);
        final String childName = path.substring(lastSlash + 1);
        final DataNode node = this.nodes.get(path);
        if (node == null) {
            throw new KeeperException.NoNodeException();
        }
        this.nodes.remove(path);
        synchronized (node) {
            this.aclCache.removeUsage(node.acl);
        }
        final DataNode parent = this.nodes.get(parentName);
        if (parent == null) {
            throw new KeeperException.NoNodeException();
        }
        synchronized (parent) {
            parent.removeChild(childName);
            parent.stat.setPzxid(zxid);
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
        if (parentName.startsWith("/zookeeper") && "zookeeper_limits".equals(childName)) {
            this.pTrie.deletePath(parentName.substring("/zookeeper/quota".length()));
        }
        final String lastPrefix;
        if ((lastPrefix = this.getMaxPrefixWithQuota(path)) != null) {
            this.updateCount(lastPrefix, -1);
            int bytes = 0;
            synchronized (node) {
                bytes = ((node.data == null) ? 0 : (-node.data.length));
            }
            this.updateBytes(lastPrefix, bytes);
        }
        if (DataTree.LOG.isTraceEnabled()) {
            ZooTrace.logTraceMessage(DataTree.LOG, 64L, "dataWatches.triggerWatch " + path);
            ZooTrace.logTraceMessage(DataTree.LOG, 64L, "childWatches.triggerWatch " + parentName);
        }
        final Set<Watcher> processed = this.dataWatches.triggerWatch(path, Watcher.Event.EventType.NodeDeleted);
        this.childWatches.triggerWatch(path, Watcher.Event.EventType.NodeDeleted, processed);
        this.childWatches.triggerWatch(parentName.equals("") ? "/" : parentName, Watcher.Event.EventType.NodeChildrenChanged);
    }
    
    public Stat setData(final String path, final byte[] data, final int version, final long zxid, final long time) throws KeeperException.NoNodeException {
        final Stat s = new Stat();
        final DataNode n = this.nodes.get(path);
        if (n == null) {
            throw new KeeperException.NoNodeException();
        }
        byte[] lastdata = null;
        synchronized (n) {
            lastdata = n.data;
            n.data = data;
            n.stat.setMtime(time);
            n.stat.setMzxid(zxid);
            n.stat.setVersion(version);
            n.copyStat(s);
        }
        final String lastPrefix;
        if ((lastPrefix = this.getMaxPrefixWithQuota(path)) != null) {
            this.updateBytes(lastPrefix, ((data == null) ? 0 : data.length) - ((lastdata == null) ? 0 : lastdata.length));
        }
        this.dataWatches.triggerWatch(path, Watcher.Event.EventType.NodeDataChanged);
        return s;
    }
    
    public String getMaxPrefixWithQuota(final String path) {
        final String lastPrefix = this.pTrie.findMaxPrefix(path);
        if (!"/".equals(lastPrefix) && !"".equals(lastPrefix)) {
            return lastPrefix;
        }
        return null;
    }
    
    public byte[] getData(final String path, final Stat stat, final Watcher watcher) throws KeeperException.NoNodeException {
        final DataNode n = this.nodes.get(path);
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
        final DataNode n = this.nodes.get(path);
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
    
    public List<String> getChildren(final String path, final Stat stat, final Watcher watcher) throws KeeperException.NoNodeException {
        final DataNode n = this.nodes.get(path);
        if (n == null) {
            throw new KeeperException.NoNodeException();
        }
        synchronized (n) {
            if (stat != null) {
                n.copyStat(stat);
            }
            final List<String> children = new ArrayList<String>(n.getChildren());
            if (watcher != null) {
                this.childWatches.addWatch(path, watcher);
            }
            return children;
        }
    }
    
    public Stat setACL(final String path, final List<ACL> acl, final int version) throws KeeperException.NoNodeException {
        final Stat stat = new Stat();
        final DataNode n = this.nodes.get(path);
        if (n == null) {
            throw new KeeperException.NoNodeException();
        }
        synchronized (n) {
            this.aclCache.removeUsage(n.acl);
            n.stat.setAversion(version);
            n.acl = this.aclCache.convertAcls(acl);
            n.copyStat(stat);
            return stat;
        }
    }
    
    public List<ACL> getACL(final String path, final Stat stat) throws KeeperException.NoNodeException {
        final DataNode n = this.nodes.get(path);
        if (n == null) {
            throw new KeeperException.NoNodeException();
        }
        synchronized (n) {
            n.copyStat(stat);
            return new ArrayList<ACL>(this.aclCache.convertLong(n.acl));
        }
    }
    
    public List<ACL> getACL(final DataNode node) {
        synchronized (node) {
            return this.aclCache.convertLong(node.acl);
        }
    }
    
    public Long getACL(final DataNodeV1 oldDataNode) {
        synchronized (oldDataNode) {
            return this.aclCache.convertAcls(oldDataNode.acl);
        }
    }
    
    public int aclCacheSize() {
        return this.aclCache.size();
    }
    
    public ProcessTxnResult processTxn(final TxnHeader header, final Record txn) {
        final ProcessTxnResult rc = new ProcessTxnResult();
        try {
            rc.clientId = header.getClientId();
            rc.cxid = header.getCxid();
            rc.zxid = header.getZxid();
            rc.type = header.getType();
            rc.err = 0;
            rc.multiResult = null;
            switch (header.getType()) {
                case 1: {
                    final CreateTxn createTxn = (CreateTxn)txn;
                    rc.path = createTxn.getPath();
                    this.createNode(createTxn.getPath(), createTxn.getData(), createTxn.getAcl(), createTxn.getEphemeral() ? header.getClientId() : 0L, createTxn.getParentCVersion(), header.getZxid(), header.getTime());
                    break;
                }
                case 2: {
                    final DeleteTxn deleteTxn = (DeleteTxn)txn;
                    rc.path = deleteTxn.getPath();
                    this.deleteNode(deleteTxn.getPath(), header.getZxid());
                    break;
                }
                case 5: {
                    final SetDataTxn setDataTxn = (SetDataTxn)txn;
                    rc.path = setDataTxn.getPath();
                    rc.stat = this.setData(setDataTxn.getPath(), setDataTxn.getData(), setDataTxn.getVersion(), header.getZxid(), header.getTime());
                    break;
                }
                case 7: {
                    final SetACLTxn setACLTxn = (SetACLTxn)txn;
                    rc.path = setACLTxn.getPath();
                    rc.stat = this.setACL(setACLTxn.getPath(), setACLTxn.getAcl(), setACLTxn.getVersion());
                    break;
                }
                case -11: {
                    this.killSession(header.getClientId(), header.getZxid());
                    break;
                }
                case -1: {
                    final ErrorTxn errTxn = (ErrorTxn)txn;
                    rc.err = errTxn.getErr();
                    break;
                }
                case 13: {
                    final CheckVersionTxn checkTxn = (CheckVersionTxn)txn;
                    rc.path = checkTxn.getPath();
                    break;
                }
                case 14: {
                    final MultiTxn multiTxn = (MultiTxn)txn;
                    final List<Txn> txns = multiTxn.getTxns();
                    rc.multiResult = new ArrayList<ProcessTxnResult>();
                    boolean failed = false;
                    for (final Txn subtxn : txns) {
                        if (subtxn.getType() == -1) {
                            failed = true;
                            break;
                        }
                    }
                    boolean post_failed = false;
                    for (final Txn subtxn2 : txns) {
                        final ByteBuffer bb = ByteBuffer.wrap(subtxn2.getData());
                        Record record = null;
                        switch (subtxn2.getType()) {
                            case 1: {
                                record = new CreateTxn();
                                break;
                            }
                            case 2: {
                                record = new DeleteTxn();
                                break;
                            }
                            case 5: {
                                record = new SetDataTxn();
                                break;
                            }
                            case -1: {
                                record = new ErrorTxn();
                                post_failed = true;
                                break;
                            }
                            case 13: {
                                record = new CheckVersionTxn();
                                break;
                            }
                            default: {
                                throw new IOException("Invalid type of op: " + subtxn2.getType());
                            }
                        }
                        assert record != null;
                        ByteBufferInputStream.byteBuffer2Record(bb, record);
                        if (failed && subtxn2.getType() != -1) {
                            final int ec = post_failed ? KeeperException.Code.RUNTIMEINCONSISTENCY.intValue() : KeeperException.Code.OK.intValue();
                            subtxn2.setType(-1);
                            record = new ErrorTxn(ec);
                        }
                        if (failed && !DataTree.$assertionsDisabled && subtxn2.getType() != -1) {
                            throw new AssertionError();
                        }
                        final TxnHeader subHdr = new TxnHeader(header.getClientId(), header.getCxid(), header.getZxid(), header.getTime(), subtxn2.getType());
                        final ProcessTxnResult subRc = this.processTxn(subHdr, record);
                        rc.multiResult.add(subRc);
                        if (subRc.err == 0 || rc.err != 0) {
                            continue;
                        }
                        rc.err = subRc.err;
                    }
                    break;
                }
            }
        }
        catch (KeeperException e) {
            if (DataTree.LOG.isDebugEnabled()) {
                DataTree.LOG.debug("Failed: " + header + ":" + txn, e);
            }
            rc.err = e.code().intValue();
        }
        catch (IOException e2) {
            if (DataTree.LOG.isDebugEnabled()) {
                DataTree.LOG.debug("Failed: " + header + ":" + txn, e2);
            }
        }
        if (rc.zxid > this.lastProcessedZxid) {
            this.lastProcessedZxid = rc.zxid;
        }
        if (header.getType() == 1 && rc.err == KeeperException.Code.NODEEXISTS.intValue()) {
            DataTree.LOG.debug("Adjusting parent cversion for Txn: " + header.getType() + " path:" + rc.path + " err: " + rc.err);
            final int lastSlash = rc.path.lastIndexOf(47);
            final String parentName = rc.path.substring(0, lastSlash);
            final CreateTxn cTxn = (CreateTxn)txn;
            try {
                this.setCversionPzxid(parentName, cTxn.getParentCVersion(), header.getZxid());
            }
            catch (KeeperException.NoNodeException e3) {
                DataTree.LOG.error("Failed to set parent cversion for: " + parentName, e3);
                rc.err = e3.code().intValue();
            }
        }
        else if (rc.err != KeeperException.Code.OK.intValue()) {
            DataTree.LOG.debug("Ignoring processTxn failure hdr: " + header.getType() + " : error: " + rc.err);
        }
        return rc;
    }
    
    void killSession(final long session, final long zxid) {
        final HashSet<String> list = this.ephemerals.remove(session);
        if (list != null) {
            for (final String path : list) {
                try {
                    this.deleteNode(path, zxid);
                    if (!DataTree.LOG.isDebugEnabled()) {
                        continue;
                    }
                    DataTree.LOG.debug("Deleting ephemeral node " + path + " for session 0x" + Long.toHexString(session));
                }
                catch (KeeperException.NoNodeException e) {
                    DataTree.LOG.warn("Ignoring NoNodeException for path " + path + " while removing ephemeral for dead session 0x" + Long.toHexString(session));
                }
            }
        }
    }
    
    private void getCounts(final String path, final Counts counts) {
        final DataNode node = this.getNode(path);
        if (node == null) {
            return;
        }
        String[] children = null;
        int len = 0;
        synchronized (node) {
            final Set<String> childs = node.getChildren();
            children = childs.toArray(new String[childs.size()]);
            len = ((node.data == null) ? 0 : node.data.length);
        }
        ++counts.count;
        counts.bytes += len;
        for (final String child : children) {
            this.getCounts(path + "/" + child, counts);
        }
    }
    
    private void updateQuotaForPath(final String path) {
        final Counts c = new Counts();
        this.getCounts(path, c);
        final StatsTrack strack = new StatsTrack();
        strack.setBytes(c.bytes);
        strack.setCount(c.count);
        final String statPath = "/zookeeper/quota" + path + "/" + "zookeeper_stats";
        final DataNode node = this.getNode(statPath);
        if (node == null) {
            DataTree.LOG.warn("Missing quota stat node " + statPath);
            return;
        }
        synchronized (node) {
            node.data = strack.toString().getBytes();
        }
    }
    
    private void traverseNode(final String path) {
        final DataNode node = this.getNode(path);
        String[] children = null;
        synchronized (node) {
            final Set<String> childs = node.getChildren();
            children = childs.toArray(new String[childs.size()]);
        }
        if (children.length == 0) {
            final String endString = "/zookeeper_limits";
            if (path.endsWith(endString)) {
                final String realPath = path.substring("/zookeeper/quota".length(), path.indexOf(endString));
                this.updateQuotaForPath(realPath);
                this.pTrie.addPath(realPath);
            }
            return;
        }
        for (final String child : children) {
            this.traverseNode(path + "/" + child);
        }
    }
    
    private void setupQuota() {
        final String quotaPath = "/zookeeper/quota";
        final DataNode node = this.getNode(quotaPath);
        if (node == null) {
            return;
        }
        this.traverseNode(quotaPath);
    }
    
    void serializeNode(final OutputArchive oa, final StringBuilder path) throws IOException {
        final String pathString = path.toString();
        final DataNode node = this.getNode(pathString);
        if (node == null) {
            return;
        }
        String[] children = null;
        final DataNode nodeCopy;
        synchronized (node) {
            ++this.scount;
            final StatPersisted statCopy = new StatPersisted();
            copyStatPersisted(node.stat, statCopy);
            nodeCopy = new DataNode(node.parent, node.data, node.acl, statCopy);
            final Set<String> childs = node.getChildren();
            children = childs.toArray(new String[childs.size()]);
        }
        oa.writeString(pathString, "path");
        oa.writeRecord(nodeCopy, "node");
        path.append('/');
        final int off = path.length();
        for (final String child : children) {
            path.delete(off, Integer.MAX_VALUE);
            path.append(child);
            this.serializeNode(oa, path);
        }
    }
    
    public void serialize(final OutputArchive oa, final String tag) throws IOException {
        this.scount = 0;
        this.aclCache.serialize(oa);
        this.serializeNode(oa, new StringBuilder(""));
        if (this.root != null) {
            oa.writeString("/", "path");
        }
    }
    
    public void deserialize(final InputArchive ia, final String tag) throws IOException {
        this.aclCache.deserialize(ia);
        this.nodes.clear();
        this.pTrie.clear();
        for (String path = ia.readString("path"); !path.equals("/"); path = ia.readString("path")) {
            final DataNode node = new DataNode();
            ia.readRecord(node, "node");
            this.nodes.put(path, node);
            synchronized (node) {
                this.aclCache.addUsage(node.acl);
            }
            final int lastSlash = path.lastIndexOf(47);
            if (lastSlash == -1) {
                this.root = node;
            }
            else {
                final String parentPath = path.substring(0, lastSlash);
                node.parent = this.nodes.get(parentPath);
                if (node.parent == null) {
                    throw new IOException("Invalid Datatree, unable to find parent " + parentPath + " of path " + path);
                }
                node.parent.addChild(path.substring(lastSlash + 1));
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
        this.setupQuota();
        this.aclCache.purgeUnused();
    }
    
    public synchronized void dumpWatchesSummary(final PrintWriter pwriter) {
        pwriter.print(this.dataWatches.toString());
    }
    
    public synchronized void dumpWatches(final PrintWriter pwriter, final boolean byPath) {
        this.dataWatches.dumpWatches(pwriter, byPath);
    }
    
    public void dumpEphemerals(final PrintWriter pwriter) {
        final Set<Map.Entry<Long, HashSet<String>>> entrySet = this.ephemerals.entrySet();
        pwriter.println("Sessions with Ephemerals (" + entrySet.size() + "):");
        for (final Map.Entry<Long, HashSet<String>> entry : entrySet) {
            pwriter.print("0x" + Long.toHexString(entry.getKey()));
            pwriter.println(":");
            final HashSet<String> tmp = entry.getValue();
            if (tmp != null) {
                synchronized (tmp) {
                    for (final String path : tmp) {
                        pwriter.println("\t" + path);
                    }
                }
            }
        }
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
    
    public void setWatches(final long relativeZxid, final List<String> dataWatches, final List<String> existWatches, final List<String> childWatches, final Watcher watcher) {
        for (final String path : dataWatches) {
            final DataNode node = this.getNode(path);
            if (node == null) {
                watcher.process(new WatchedEvent(Watcher.Event.EventType.NodeDeleted, Watcher.Event.KeeperState.SyncConnected, path));
            }
            else if (node.stat.getMzxid() > relativeZxid) {
                watcher.process(new WatchedEvent(Watcher.Event.EventType.NodeDataChanged, Watcher.Event.KeeperState.SyncConnected, path));
            }
            else {
                this.dataWatches.addWatch(path, watcher);
            }
        }
        for (final String path : existWatches) {
            final DataNode node = this.getNode(path);
            if (node != null) {
                watcher.process(new WatchedEvent(Watcher.Event.EventType.NodeCreated, Watcher.Event.KeeperState.SyncConnected, path));
            }
            else {
                this.dataWatches.addWatch(path, watcher);
            }
        }
        for (final String path : childWatches) {
            final DataNode node = this.getNode(path);
            if (node == null) {
                watcher.process(new WatchedEvent(Watcher.Event.EventType.NodeDeleted, Watcher.Event.KeeperState.SyncConnected, path));
            }
            else if (node.stat.getPzxid() > relativeZxid) {
                watcher.process(new WatchedEvent(Watcher.Event.EventType.NodeChildrenChanged, Watcher.Event.KeeperState.SyncConnected, path));
            }
            else {
                this.childWatches.addWatch(path, watcher);
            }
        }
    }
    
    public void setCversionPzxid(String path, int newCversion, final long zxid) throws KeeperException.NoNodeException {
        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        final DataNode node = this.nodes.get(path);
        if (node == null) {
            throw new KeeperException.NoNodeException(path);
        }
        synchronized (node) {
            if (newCversion == -1) {
                newCversion = node.stat.getCversion() + 1;
            }
            if (newCversion > node.stat.getCversion()) {
                node.stat.setCversion(newCversion);
                node.stat.setPzxid(zxid);
            }
        }
    }
    
    static {
        LOG = LoggerFactory.getLogger(DataTree.class);
        procChildZookeeper = "/zookeeper".substring(1);
        quotaChildZookeeper = "/zookeeper/quota".substring("/zookeeper".length() + 1);
    }
    
    public static class ProcessTxnResult
    {
        public long clientId;
        public int cxid;
        public long zxid;
        public int err;
        public int type;
        public String path;
        public Stat stat;
        public List<ProcessTxnResult> multiResult;
        
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
    
    private static class Counts
    {
        long bytes;
        int count;
    }
}
