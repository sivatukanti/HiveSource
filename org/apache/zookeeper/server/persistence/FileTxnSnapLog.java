// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.server.persistence;

import org.slf4j.LoggerFactory;
import org.apache.zookeeper.server.Request;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.zookeeper.server.ZooTrace;
import org.apache.zookeeper.txn.CreateSessionTxn;
import org.apache.jute.Record;
import org.apache.zookeeper.txn.TxnHeader;
import org.apache.zookeeper.KeeperException;
import java.util.Map;
import org.apache.zookeeper.server.DataTree;
import java.io.FilenameFilter;
import org.apache.zookeeper.server.ServerStats;
import java.io.IOException;
import org.slf4j.Logger;
import java.io.File;

public class FileTxnSnapLog
{
    private final File dataDir;
    private final File snapDir;
    private TxnLog txnLog;
    private SnapShot snapLog;
    public static final int VERSION = 2;
    public static final String version = "version-";
    private static final Logger LOG;
    
    public FileTxnSnapLog(final File dataDir, final File snapDir) throws IOException {
        FileTxnSnapLog.LOG.debug("Opening datadir:{} snapDir:{}", dataDir, snapDir);
        this.dataDir = new File(dataDir, "version-2");
        this.snapDir = new File(snapDir, "version-2");
        if (!this.dataDir.exists() && !this.dataDir.mkdirs()) {
            throw new IOException("Unable to create data directory " + this.dataDir);
        }
        if (!this.dataDir.canWrite()) {
            throw new IOException("Cannot write to data directory " + this.dataDir);
        }
        if (!this.snapDir.exists() && !this.snapDir.mkdirs()) {
            throw new IOException("Unable to create snap directory " + this.snapDir);
        }
        if (!this.snapDir.canWrite()) {
            throw new IOException("Cannot write to snap directory " + this.snapDir);
        }
        if (!this.dataDir.getPath().equals(this.snapDir.getPath())) {
            this.checkLogDir();
            this.checkSnapDir();
        }
        this.txnLog = new FileTxnLog(this.dataDir);
        this.snapLog = new FileSnap(this.snapDir);
    }
    
    public void setServerStats(final ServerStats serverStats) {
        this.txnLog.setServerStats(serverStats);
    }
    
    private void checkLogDir() throws LogDirContentCheckException {
        final File[] files = this.dataDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(final File dir, final String name) {
                return Util.isSnapshotFileName(name);
            }
        });
        if (files != null && files.length > 0) {
            throw new LogDirContentCheckException("Log directory has snapshot files. Check if dataLogDir and dataDir configuration is correct.");
        }
    }
    
    private void checkSnapDir() throws SnapDirContentCheckException {
        final File[] files = this.snapDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(final File dir, final String name) {
                return Util.isLogFileName(name);
            }
        });
        if (files != null && files.length > 0) {
            throw new SnapDirContentCheckException("Snapshot directory has log files. Check if dataLogDir and dataDir configuration is correct.");
        }
    }
    
    public File getDataDir() {
        return this.dataDir;
    }
    
    public File getSnapDir() {
        return this.snapDir;
    }
    
    public long restore(final DataTree dt, final Map<Long, Integer> sessions, final PlayBackListener listener) throws IOException {
        this.snapLog.deserialize(dt, sessions);
        return this.fastForwardFromEdits(dt, sessions, listener);
    }
    
    public long fastForwardFromEdits(final DataTree dt, final Map<Long, Integer> sessions, final PlayBackListener listener) throws IOException {
        final FileTxnLog txnLog = new FileTxnLog(this.dataDir);
        final TxnLog.TxnIterator itr = txnLog.read(dt.lastProcessedZxid + 1L);
        long highestZxid = dt.lastProcessedZxid;
        try {
            do {
                final TxnHeader hdr = itr.getHeader();
                if (hdr == null) {
                    return dt.lastProcessedZxid;
                }
                if (hdr.getZxid() < highestZxid && highestZxid != 0L) {
                    FileTxnSnapLog.LOG.error("{}(higestZxid) > {}(next log) for type {}", highestZxid, hdr.getZxid(), hdr.getType());
                }
                else {
                    highestZxid = hdr.getZxid();
                }
                try {
                    this.processTransaction(hdr, dt, sessions, itr.getTxn());
                }
                catch (KeeperException.NoNodeException e) {
                    throw new IOException("Failed to process transaction type: " + hdr.getType() + " error: " + e.getMessage(), e);
                }
                listener.onTxnLoaded(hdr, itr.getTxn());
            } while (itr.next());
        }
        finally {
            if (itr != null) {
                itr.close();
            }
        }
        return highestZxid;
    }
    
    public void processTransaction(final TxnHeader hdr, final DataTree dt, final Map<Long, Integer> sessions, final Record txn) throws KeeperException.NoNodeException {
        DataTree.ProcessTxnResult rc = null;
        switch (hdr.getType()) {
            case -10: {
                sessions.put(hdr.getClientId(), ((CreateSessionTxn)txn).getTimeOut());
                if (FileTxnSnapLog.LOG.isTraceEnabled()) {
                    ZooTrace.logTraceMessage(FileTxnSnapLog.LOG, 32L, "playLog --- create session in log: 0x" + Long.toHexString(hdr.getClientId()) + " with timeout: " + ((CreateSessionTxn)txn).getTimeOut());
                }
                rc = dt.processTxn(hdr, txn);
                break;
            }
            case -11: {
                sessions.remove(hdr.getClientId());
                if (FileTxnSnapLog.LOG.isTraceEnabled()) {
                    ZooTrace.logTraceMessage(FileTxnSnapLog.LOG, 32L, "playLog --- close session in log: 0x" + Long.toHexString(hdr.getClientId()));
                }
                rc = dt.processTxn(hdr, txn);
                break;
            }
            default: {
                rc = dt.processTxn(hdr, txn);
                break;
            }
        }
        if (rc.err != KeeperException.Code.OK.intValue()) {
            FileTxnSnapLog.LOG.debug("Ignoring processTxn failure hdr:" + hdr.getType() + ", error: " + rc.err + ", path: " + rc.path);
        }
    }
    
    public long getLastLoggedZxid() {
        final FileTxnLog txnLog = new FileTxnLog(this.dataDir);
        return txnLog.getLastLoggedZxid();
    }
    
    public void save(final DataTree dataTree, final ConcurrentHashMap<Long, Integer> sessionsWithTimeouts) throws IOException {
        final long lastZxid = dataTree.lastProcessedZxid;
        final File snapshotFile = new File(this.snapDir, Util.makeSnapshotName(lastZxid));
        FileTxnSnapLog.LOG.info("Snapshotting: 0x{} to {}", Long.toHexString(lastZxid), snapshotFile);
        this.snapLog.serialize(dataTree, sessionsWithTimeouts, snapshotFile);
    }
    
    public boolean truncateLog(final long zxid) throws IOException {
        this.close();
        final FileTxnLog truncLog = new FileTxnLog(this.dataDir);
        final boolean truncated = truncLog.truncate(zxid);
        truncLog.close();
        this.txnLog = new FileTxnLog(this.dataDir);
        this.snapLog = new FileSnap(this.snapDir);
        return truncated;
    }
    
    public File findMostRecentSnapshot() throws IOException {
        final FileSnap snaplog = new FileSnap(this.snapDir);
        return snaplog.findMostRecentSnapshot();
    }
    
    public List<File> findNRecentSnapshots(final int n) throws IOException {
        final FileSnap snaplog = new FileSnap(this.snapDir);
        return snaplog.findNRecentSnapshots(n);
    }
    
    public File[] getSnapshotLogs(final long zxid) {
        return FileTxnLog.getLogFiles(this.dataDir.listFiles(), zxid);
    }
    
    public boolean append(final Request si) throws IOException {
        return this.txnLog.append(si.hdr, si.txn);
    }
    
    public void commit() throws IOException {
        this.txnLog.commit();
    }
    
    public void rollLog() throws IOException {
        this.txnLog.rollLog();
    }
    
    public void close() throws IOException {
        this.txnLog.close();
        this.snapLog.close();
    }
    
    static {
        LOG = LoggerFactory.getLogger(FileTxnSnapLog.class);
    }
    
    public static class DatadirException extends IOException
    {
        public DatadirException(final String msg) {
            super(msg);
        }
        
        public DatadirException(final String msg, final Exception e) {
            super(msg, e);
        }
    }
    
    public static class LogDirContentCheckException extends DatadirException
    {
        public LogDirContentCheckException(final String msg) {
            super(msg);
        }
    }
    
    public static class SnapDirContentCheckException extends DatadirException
    {
        public SnapDirContentCheckException(final String msg) {
            super(msg);
        }
    }
    
    public interface PlayBackListener
    {
        void onTxnLoaded(final TxnHeader p0, final Record p1);
    }
}
