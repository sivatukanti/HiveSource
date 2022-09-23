// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.server;

import org.slf4j.LoggerFactory;
import org.apache.jute.OutputArchive;
import org.apache.jute.InputArchive;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.data.Stat;
import java.util.HashSet;
import java.io.PrintWriter;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.server.quorum.QuorumPacket;
import org.apache.zookeeper.server.util.SerializeUtils;
import org.apache.zookeeper.data.Id;
import java.util.List;
import java.nio.ByteBuffer;
import java.io.IOException;
import java.util.Map;
import java.util.Collection;
import org.apache.jute.Record;
import org.apache.zookeeper.txn.TxnHeader;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.apache.zookeeper.server.quorum.Leader;
import java.util.LinkedList;
import org.apache.zookeeper.server.persistence.FileTxnSnapLog;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;

public class ZKDatabase
{
    private static final Logger LOG;
    protected DataTree dataTree;
    protected ConcurrentHashMap<Long, Integer> sessionsWithTimeouts;
    protected FileTxnSnapLog snapLog;
    protected long minCommittedLog;
    protected long maxCommittedLog;
    public static final int commitLogCount = 500;
    protected static int commitLogBuffer;
    protected LinkedList<Leader.Proposal> committedLog;
    protected ReentrantReadWriteLock logLock;
    private volatile boolean initialized;
    private final FileTxnSnapLog.PlayBackListener commitProposalPlaybackListener;
    
    public ZKDatabase(final FileTxnSnapLog snapLog) {
        this.committedLog = new LinkedList<Leader.Proposal>();
        this.logLock = new ReentrantReadWriteLock();
        this.initialized = false;
        this.commitProposalPlaybackListener = new FileTxnSnapLog.PlayBackListener() {
            @Override
            public void onTxnLoaded(final TxnHeader hdr, final Record txn) {
                ZKDatabase.this.addCommittedProposal(hdr, txn);
            }
        };
        this.dataTree = new DataTree();
        this.sessionsWithTimeouts = new ConcurrentHashMap<Long, Integer>();
        this.snapLog = snapLog;
    }
    
    public boolean isInitialized() {
        return this.initialized;
    }
    
    public void clear() {
        this.minCommittedLog = 0L;
        this.maxCommittedLog = 0L;
        this.dataTree = new DataTree();
        this.sessionsWithTimeouts.clear();
        final ReentrantReadWriteLock.WriteLock lock = this.logLock.writeLock();
        try {
            lock.lock();
            this.committedLog.clear();
        }
        finally {
            lock.unlock();
        }
        this.initialized = false;
    }
    
    public DataTree getDataTree() {
        return this.dataTree;
    }
    
    public long getmaxCommittedLog() {
        return this.maxCommittedLog;
    }
    
    public long getminCommittedLog() {
        return this.minCommittedLog;
    }
    
    public ReentrantReadWriteLock getLogLock() {
        return this.logLock;
    }
    
    public synchronized LinkedList<Leader.Proposal> getCommittedLog() {
        final ReentrantReadWriteLock.ReadLock rl = this.logLock.readLock();
        if (this.logLock.getReadHoldCount() <= 0) {
            try {
                rl.lock();
                return new LinkedList<Leader.Proposal>(this.committedLog);
            }
            finally {
                rl.unlock();
            }
        }
        return this.committedLog;
    }
    
    public long getDataTreeLastProcessedZxid() {
        return this.dataTree.lastProcessedZxid;
    }
    
    public void setDataTreeInit(final boolean b) {
        this.dataTree.initialized = b;
    }
    
    public Collection<Long> getSessions() {
        return this.dataTree.getSessions();
    }
    
    public ConcurrentHashMap<Long, Integer> getSessionWithTimeOuts() {
        return this.sessionsWithTimeouts;
    }
    
    public long loadDataBase() throws IOException {
        final long zxid = this.snapLog.restore(this.dataTree, this.sessionsWithTimeouts, this.commitProposalPlaybackListener);
        this.initialized = true;
        return zxid;
    }
    
    public long fastForwardDataBase() throws IOException {
        final long zxid = this.snapLog.fastForwardFromEdits(this.dataTree, this.sessionsWithTimeouts, this.commitProposalPlaybackListener);
        this.initialized = true;
        return zxid;
    }
    
    private void addCommittedProposal(final TxnHeader hdr, final Record txn) {
        final Request r = new Request(null, 0L, hdr.getCxid(), hdr.getType(), null, null);
        r.txn = txn;
        r.hdr = hdr;
        r.zxid = hdr.getZxid();
        this.addCommittedProposal(r);
    }
    
    public void addCommittedProposal(final Request request) {
        final ReentrantReadWriteLock.WriteLock wl = this.logLock.writeLock();
        try {
            wl.lock();
            if (this.committedLog.size() > 500) {
                this.committedLog.removeFirst();
                this.minCommittedLog = this.committedLog.getFirst().packet.getZxid();
            }
            if (this.committedLog.size() == 0) {
                this.minCommittedLog = request.zxid;
                this.maxCommittedLog = request.zxid;
            }
            final byte[] data = SerializeUtils.serializeRequest(request);
            final QuorumPacket pp = new QuorumPacket(2, request.zxid, data, null);
            final Leader.Proposal p = new Leader.Proposal();
            p.packet = pp;
            p.request = request;
            this.committedLog.add(p);
            this.maxCommittedLog = p.packet.getZxid();
        }
        finally {
            wl.unlock();
        }
    }
    
    public List<ACL> aclForNode(final DataNode n) {
        return this.dataTree.getACL(n);
    }
    
    public void removeCnxn(final ServerCnxn cnxn) {
        this.dataTree.removeCnxn(cnxn);
    }
    
    public void killSession(final long sessionId, final long zxid) {
        this.dataTree.killSession(sessionId, zxid);
    }
    
    public void dumpEphemerals(final PrintWriter pwriter) {
        this.dataTree.dumpEphemerals(pwriter);
    }
    
    public int getNodeCount() {
        return this.dataTree.getNodeCount();
    }
    
    public HashSet<String> getEphemerals(final long sessionId) {
        return this.dataTree.getEphemerals(sessionId);
    }
    
    public void setlastProcessedZxid(final long zxid) {
        this.dataTree.lastProcessedZxid = zxid;
    }
    
    public DataTree.ProcessTxnResult processTxn(final TxnHeader hdr, final Record txn) {
        return this.dataTree.processTxn(hdr, txn);
    }
    
    public Stat statNode(final String path, final ServerCnxn serverCnxn) throws KeeperException.NoNodeException {
        return this.dataTree.statNode(path, serverCnxn);
    }
    
    public DataNode getNode(final String path) {
        return this.dataTree.getNode(path);
    }
    
    public byte[] getData(final String path, final Stat stat, final Watcher watcher) throws KeeperException.NoNodeException {
        return this.dataTree.getData(path, stat, watcher);
    }
    
    public void setWatches(final long relativeZxid, final List<String> dataWatches, final List<String> existWatches, final List<String> childWatches, final Watcher watcher) {
        this.dataTree.setWatches(relativeZxid, dataWatches, existWatches, childWatches, watcher);
    }
    
    public List<ACL> getACL(final String path, final Stat stat) throws KeeperException.NoNodeException {
        return this.dataTree.getACL(path, stat);
    }
    
    public List<String> getChildren(final String path, final Stat stat, final Watcher watcher) throws KeeperException.NoNodeException {
        return this.dataTree.getChildren(path, stat, watcher);
    }
    
    public boolean isSpecialPath(final String path) {
        return this.dataTree.isSpecialPath(path);
    }
    
    public int getAclSize() {
        return this.dataTree.aclCacheSize();
    }
    
    public boolean truncateLog(final long zxid) throws IOException {
        this.clear();
        final boolean truncated = this.snapLog.truncateLog(zxid);
        if (!truncated) {
            return false;
        }
        this.loadDataBase();
        return true;
    }
    
    public void deserializeSnapshot(final InputArchive ia) throws IOException {
        this.clear();
        SerializeUtils.deserializeSnapshot(this.getDataTree(), ia, this.getSessionWithTimeOuts());
        this.initialized = true;
    }
    
    public void serializeSnapshot(final OutputArchive oa) throws IOException, InterruptedException {
        SerializeUtils.serializeSnapshot(this.getDataTree(), oa, this.getSessionWithTimeOuts());
    }
    
    public boolean append(final Request si) throws IOException {
        return this.snapLog.append(si);
    }
    
    public void rollLog() throws IOException {
        this.snapLog.rollLog();
    }
    
    public void commit() throws IOException {
        this.snapLog.commit();
    }
    
    public void close() throws IOException {
        this.snapLog.close();
    }
    
    static {
        LOG = LoggerFactory.getLogger(ZKDatabase.class);
        ZKDatabase.commitLogBuffer = 700;
    }
}
