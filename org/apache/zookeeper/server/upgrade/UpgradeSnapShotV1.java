// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.server.upgrade;

import org.slf4j.LoggerFactory;
import java.util.HashSet;
import org.apache.zookeeper.server.DataNode;
import org.apache.zookeeper.server.DataTree;
import org.apache.zookeeper.data.StatPersisted;
import org.apache.zookeeper.data.StatPersistedV1;
import java.util.Iterator;
import org.apache.zookeeper.server.persistence.Util;
import org.apache.zookeeper.server.persistence.FileTxnLog;
import org.apache.jute.BinaryInputArchive;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import org.apache.jute.Record;
import org.apache.zookeeper.data.Id;
import java.util.List;
import java.nio.ByteBuffer;
import org.apache.zookeeper.server.ServerCnxn;
import org.apache.zookeeper.server.Request;
import org.apache.zookeeper.txn.CreateSessionTxn;
import org.apache.zookeeper.server.util.SerializeUtils;
import org.apache.zookeeper.txn.TxnHeader;
import java.io.EOFException;
import java.io.IOException;
import org.apache.zookeeper.server.ZooTrace;
import java.util.Map;
import org.apache.jute.InputArchive;
import java.io.File;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;

public class UpgradeSnapShotV1 implements UpgradeSnapShot
{
    private static final Logger LOG;
    ConcurrentHashMap<Long, Integer> sessionsWithTimeouts;
    File dataDir;
    File snapShotDir;
    DataTreeV1 oldDataTree;
    
    public UpgradeSnapShotV1(final File dataDir, final File snapShotDir) {
        this.sessionsWithTimeouts = new ConcurrentHashMap<Long, Integer>();
        this.dataDir = dataDir;
        this.snapShotDir = snapShotDir;
        this.oldDataTree = new DataTreeV1();
    }
    
    private void deserializeSnapshot(final DataTreeV1 oldTree, final InputArchive ia, final Map<Long, Integer> sessions) throws IOException {
        for (int count = ia.readInt("count"); count > 0; --count) {
            final long id = ia.readLong("id");
            final int to = ia.readInt("timeout");
            sessions.put(id, to);
            if (UpgradeSnapShotV1.LOG.isTraceEnabled()) {
                ZooTrace.logTraceMessage(UpgradeSnapShotV1.LOG, 32L, "loadData --- session in archive: " + id + " with timeout: " + to);
            }
        }
        oldTree.deserialize(ia, "tree");
    }
    
    public long playLog(final InputArchive logStream) throws IOException {
        long highestZxid = 0L;
        try {
            while (true) {
                final byte[] bytes = logStream.readBuffer("txnEntry");
                if (bytes.length == 0) {
                    throw new EOFException();
                }
                final TxnHeader hdr = new TxnHeader();
                final Record txn = SerializeUtils.deserializeTxn(bytes, hdr);
                if (logStream.readByte("EOR") != 66) {
                    UpgradeSnapShotV1.LOG.warn("Last transaction was partial.");
                    throw new EOFException("Last transaction was partial.");
                }
                if (hdr.getZxid() <= highestZxid && highestZxid != 0L) {
                    UpgradeSnapShotV1.LOG.error(highestZxid + "(higestZxid) >= " + hdr.getZxid() + "(next log) for type " + hdr.getType());
                }
                else {
                    highestZxid = hdr.getZxid();
                }
                switch (hdr.getType()) {
                    case -10: {
                        this.sessionsWithTimeouts.put(hdr.getClientId(), ((CreateSessionTxn)txn).getTimeOut());
                        if (UpgradeSnapShotV1.LOG.isTraceEnabled()) {
                            ZooTrace.logTraceMessage(UpgradeSnapShotV1.LOG, 32L, "playLog --- create session in log: 0x" + Long.toHexString(hdr.getClientId()) + " with timeout: " + ((CreateSessionTxn)txn).getTimeOut());
                        }
                        this.oldDataTree.processTxn(hdr, txn);
                        break;
                    }
                    case -11: {
                        this.sessionsWithTimeouts.remove(hdr.getClientId());
                        if (UpgradeSnapShotV1.LOG.isTraceEnabled()) {
                            ZooTrace.logTraceMessage(UpgradeSnapShotV1.LOG, 32L, "playLog --- close session in log: 0x" + Long.toHexString(hdr.getClientId()));
                        }
                        this.oldDataTree.processTxn(hdr, txn);
                        break;
                    }
                    default: {
                        this.oldDataTree.processTxn(hdr, txn);
                        break;
                    }
                }
                final Request r = new Request(null, 0L, hdr.getCxid(), hdr.getType(), null, null);
                r.txn = txn;
                r.hdr = hdr;
                r.zxid = hdr.getZxid();
            }
        }
        catch (EOFException ex) {
            return highestZxid;
        }
    }
    
    private long processLogFiles(final DataTreeV1 oldTree, final File[] logFiles) throws IOException {
        long zxid = 0L;
        for (final File f : logFiles) {
            UpgradeSnapShotV1.LOG.info("Processing log file: " + f);
            final InputStream logIs = new BufferedInputStream(new FileInputStream(f));
            zxid = this.playLog(BinaryInputArchive.getArchive(logIs));
            logIs.close();
        }
        return zxid;
    }
    
    private void loadThisSnapShot() throws IOException {
        final File snapshot = this.findMostRecentSnapshot();
        if (snapshot == null) {
            throw new IOException("Invalid snapshots or not snapshots in " + this.snapShotDir);
        }
        final InputStream inputstream = new BufferedInputStream(new FileInputStream(snapshot));
        final InputArchive ia = BinaryInputArchive.getArchive(inputstream);
        this.deserializeSnapshot(this.oldDataTree, ia, this.sessionsWithTimeouts);
        final long snapshotZxid = this.oldDataTree.lastProcessedZxid;
        final File[] files = FileTxnLog.getLogFiles(this.dataDir.listFiles(), snapshotZxid);
        final long zxid = this.processLogFiles(this.oldDataTree, files);
        if (zxid != this.oldDataTree.lastProcessedZxid) {
            UpgradeSnapShotV1.LOG.error("Zxids not equal  log zxid " + zxid + " datatree processed " + this.oldDataTree.lastProcessedZxid);
        }
    }
    
    private File findMostRecentSnapshot() throws IOException {
        final List<File> files = Util.sortDataDir(this.snapShotDir.listFiles(), "snapshot", false);
        for (final File f : files) {
            try {
                if (Util.isValidSnapshot(f)) {
                    return f;
                }
                continue;
            }
            catch (IOException e) {
                UpgradeSnapShotV1.LOG.info("Invalid snapshot " + f, e);
            }
        }
        return null;
    }
    
    private StatPersisted convertStat(final StatPersistedV1 oldStat) {
        final StatPersisted stat = new StatPersisted();
        stat.setAversion(oldStat.getAversion());
        stat.setCtime(oldStat.getCtime());
        stat.setCversion(oldStat.getCversion());
        stat.setCzxid(oldStat.getCzxid());
        stat.setEphemeralOwner(oldStat.getEphemeralOwner());
        stat.setMtime(oldStat.getMtime());
        stat.setMzxid(oldStat.getMzxid());
        stat.setVersion(oldStat.getVersion());
        return stat;
    }
    
    private DataNode convertDataNode(final DataTree dt, final DataNode parent, final DataNodeV1 oldDataNode) {
        final StatPersisted stat = this.convertStat(oldDataNode.stat);
        final DataNode dataNode = new DataNode(parent, oldDataNode.data, dt.getACL(oldDataNode), stat);
        dataNode.setChildren(oldDataNode.children);
        return dataNode;
    }
    
    private void recurseThroughDataTree(final DataTree dataTree, final String path) {
        if (path == null) {
            return;
        }
        final DataNodeV1 oldDataNode = this.oldDataTree.getNode(path);
        final HashSet<String> children = oldDataNode.children;
        DataNode parent = null;
        if ("".equals(path)) {
            parent = null;
        }
        else {
            final int lastSlash = path.lastIndexOf(47);
            final String parentPath = path.substring(0, lastSlash);
            parent = dataTree.getNode(parentPath);
        }
        final DataNode thisDatNode = this.convertDataNode(dataTree, parent, oldDataNode);
        dataTree.addDataNode(path, thisDatNode);
        if (children == null || children.size() == 0) {
            return;
        }
        for (final String child : children) {
            this.recurseThroughDataTree(dataTree, path + "/" + child);
        }
    }
    
    private DataTree convertThisSnapShot() throws IOException {
        final DataTree dataTree = new DataTree();
        final DataNodeV1 oldDataNode = this.oldDataTree.getNode("");
        if (oldDataNode == null) {
            UpgradeSnapShotV1.LOG.error("Upgrading from an empty snapshot.");
        }
        this.recurseThroughDataTree(dataTree, "");
        dataTree.lastProcessedZxid = this.oldDataTree.lastProcessedZxid;
        return dataTree;
    }
    
    @Override
    public DataTree getNewDataTree() throws IOException {
        this.loadThisSnapShot();
        final DataTree dt = this.convertThisSnapShot();
        return dt;
    }
    
    @Override
    public ConcurrentHashMap<Long, Integer> getSessionWithTimeOuts() {
        return this.sessionsWithTimeouts;
    }
    
    static {
        LOG = LoggerFactory.getLogger(UpgradeSnapShotV1.class);
    }
}
