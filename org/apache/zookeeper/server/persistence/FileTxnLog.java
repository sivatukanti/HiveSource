// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.server.persistence;

import org.apache.zookeeper.server.util.SerializeUtils;
import java.io.EOFException;
import java.io.FilterInputStream;
import org.slf4j.LoggerFactory;
import java.nio.ByteBuffer;
import org.apache.jute.InputArchive;
import org.apache.jute.BinaryInputArchive;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.RandomAccessFile;
import java.util.concurrent.TimeUnit;
import java.util.List;
import java.util.ArrayList;
import org.apache.jute.BinaryOutputArchive;
import java.io.OutputStream;
import org.apache.jute.Record;
import org.apache.zookeeper.txn.TxnHeader;
import java.util.Iterator;
import java.io.IOException;
import java.util.zip.Adler32;
import java.util.zip.Checksum;
import org.apache.zookeeper.server.ServerStats;
import java.util.LinkedList;
import java.io.File;
import java.io.FileOutputStream;
import org.apache.jute.OutputArchive;
import java.io.BufferedOutputStream;
import org.slf4j.Logger;

public class FileTxnLog implements TxnLog
{
    private static final Logger LOG;
    public static final int TXNLOG_MAGIC;
    public static final int VERSION = 2;
    public static final String LOG_FILE_PREFIX = "log";
    private static final long fsyncWarningThresholdMS;
    long lastZxidSeen;
    volatile BufferedOutputStream logStream;
    volatile OutputArchive oa;
    volatile FileOutputStream fos;
    File logDir;
    private final boolean forceSync;
    long dbId;
    private LinkedList<FileOutputStream> streamsToFlush;
    File logFileWrite;
    private FilePadding filePadding;
    private ServerStats serverStats;
    
    public FileTxnLog(final File logDir) {
        this.logStream = null;
        this.fos = null;
        this.forceSync = !System.getProperty("zookeeper.forceSync", "yes").equals("no");
        this.streamsToFlush = new LinkedList<FileOutputStream>();
        this.logFileWrite = null;
        this.filePadding = new FilePadding();
        this.logDir = logDir;
    }
    
    public static void setPreallocSize(final long size) {
        FilePadding.setPreallocSize(size);
    }
    
    @Override
    public void setServerStats(final ServerStats serverStats) {
        this.serverStats = serverStats;
    }
    
    protected Checksum makeChecksumAlgorithm() {
        return new Adler32();
    }
    
    @Override
    public synchronized void rollLog() throws IOException {
        if (this.logStream != null) {
            this.logStream.flush();
            this.logStream = null;
            this.oa = null;
        }
    }
    
    @Override
    public synchronized void close() throws IOException {
        if (this.logStream != null) {
            this.logStream.close();
        }
        for (final FileOutputStream log : this.streamsToFlush) {
            log.close();
        }
    }
    
    @Override
    public synchronized boolean append(final TxnHeader hdr, final Record txn) throws IOException {
        if (hdr == null) {
            return false;
        }
        if (hdr.getZxid() <= this.lastZxidSeen) {
            FileTxnLog.LOG.warn("Current zxid " + hdr.getZxid() + " is <= " + this.lastZxidSeen + " for " + hdr.getType());
        }
        else {
            this.lastZxidSeen = hdr.getZxid();
        }
        if (this.logStream == null) {
            if (FileTxnLog.LOG.isInfoEnabled()) {
                FileTxnLog.LOG.info("Creating new log file: " + Util.makeLogName(hdr.getZxid()));
            }
            this.logFileWrite = new File(this.logDir, Util.makeLogName(hdr.getZxid()));
            this.fos = new FileOutputStream(this.logFileWrite);
            this.logStream = new BufferedOutputStream(this.fos);
            this.oa = BinaryOutputArchive.getArchive(this.logStream);
            final FileHeader fhdr = new FileHeader(FileTxnLog.TXNLOG_MAGIC, 2, this.dbId);
            fhdr.serialize(this.oa, "fileheader");
            this.logStream.flush();
            this.filePadding.setCurrentSize(this.fos.getChannel().position());
            this.streamsToFlush.add(this.fos);
        }
        this.filePadding.padFile(this.fos.getChannel());
        final byte[] buf = Util.marshallTxnEntry(hdr, txn);
        if (buf == null || buf.length == 0) {
            throw new IOException("Faulty serialization for header and txn");
        }
        final Checksum crc = this.makeChecksumAlgorithm();
        crc.update(buf, 0, buf.length);
        this.oa.writeLong(crc.getValue(), "txnEntryCRC");
        Util.writeTxnBytes(this.oa, buf);
        return true;
    }
    
    public static File[] getLogFiles(final File[] logDirList, final long snapshotZxid) {
        final List<File> files = Util.sortDataDir(logDirList, "log", true);
        long logZxid = 0L;
        for (final File f : files) {
            final long fzxid = Util.getZxidFromName(f.getName(), "log");
            if (fzxid > snapshotZxid) {
                continue;
            }
            if (fzxid <= logZxid) {
                continue;
            }
            logZxid = fzxid;
        }
        final List<File> v = new ArrayList<File>(5);
        for (final File f2 : files) {
            final long fzxid2 = Util.getZxidFromName(f2.getName(), "log");
            if (fzxid2 < logZxid) {
                continue;
            }
            v.add(f2);
        }
        return v.toArray(new File[0]);
    }
    
    @Override
    public long getLastLoggedZxid() {
        final File[] files = getLogFiles(this.logDir.listFiles(), 0L);
        long zxid;
        final long maxLog = zxid = ((files.length > 0) ? Util.getZxidFromName(files[files.length - 1].getName(), "log") : -1L);
        TxnIterator itr = null;
        try {
            final FileTxnLog txn = new FileTxnLog(this.logDir);
            itr = txn.read(maxLog);
            while (itr.next()) {
                final TxnHeader hdr = itr.getHeader();
                zxid = hdr.getZxid();
            }
        }
        catch (IOException e) {
            FileTxnLog.LOG.warn("Unexpected exception", e);
        }
        finally {
            this.close(itr);
        }
        return zxid;
    }
    
    private void close(final TxnIterator itr) {
        if (itr != null) {
            try {
                itr.close();
            }
            catch (IOException ioe) {
                FileTxnLog.LOG.warn("Error closing file iterator", ioe);
            }
        }
    }
    
    @Override
    public synchronized void commit() throws IOException {
        if (this.logStream != null) {
            this.logStream.flush();
        }
        for (final FileOutputStream log : this.streamsToFlush) {
            log.flush();
            if (this.forceSync) {
                final long startSyncNS = System.nanoTime();
                log.getChannel().force(false);
                final long syncElapsedMS = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startSyncNS);
                if (syncElapsedMS <= FileTxnLog.fsyncWarningThresholdMS) {
                    continue;
                }
                if (this.serverStats != null) {
                    this.serverStats.incrementFsyncThresholdExceedCount();
                }
                FileTxnLog.LOG.warn("fsync-ing the write ahead log in " + Thread.currentThread().getName() + " took " + syncElapsedMS + "ms which will adversely effect operation latency. See the ZooKeeper troubleshooting guide");
            }
        }
        while (this.streamsToFlush.size() > 1) {
            this.streamsToFlush.removeFirst().close();
        }
    }
    
    @Override
    public TxnIterator read(final long zxid) throws IOException {
        return new FileTxnIterator(this.logDir, zxid);
    }
    
    @Override
    public boolean truncate(final long zxid) throws IOException {
        FileTxnIterator itr = null;
        try {
            itr = new FileTxnIterator(this.logDir, zxid);
            final PositionInputStream input = itr.inputStream;
            if (input == null) {
                throw new IOException("No log files found to truncate! This could happen if you still have snapshots from an old setup or log files were deleted accidentally or dataLogDir was changed in zoo.cfg.");
            }
            final long pos = input.getPosition();
            final RandomAccessFile raf = new RandomAccessFile(itr.logFile, "rw");
            raf.setLength(pos);
            raf.close();
            while (itr.goToNextLog()) {
                if (!itr.logFile.delete()) {
                    FileTxnLog.LOG.warn("Unable to truncate {}", itr.logFile);
                }
            }
        }
        finally {
            this.close(itr);
        }
        return true;
    }
    
    private static FileHeader readHeader(final File file) throws IOException {
        InputStream is = null;
        try {
            is = new BufferedInputStream(new FileInputStream(file));
            final InputArchive ia = BinaryInputArchive.getArchive(is);
            final FileHeader hdr = new FileHeader();
            hdr.deserialize(ia, "fileheader");
            return hdr;
        }
        finally {
            try {
                if (is != null) {
                    is.close();
                }
            }
            catch (IOException e) {
                FileTxnLog.LOG.warn("Ignoring exception during close", e);
            }
        }
    }
    
    @Override
    public long getDbId() throws IOException {
        final FileTxnIterator itr = new FileTxnIterator(this.logDir, 0L);
        final FileHeader fh = readHeader(itr.logFile);
        itr.close();
        if (fh == null) {
            throw new IOException("Unsupported Format.");
        }
        return fh.getDbid();
    }
    
    public boolean isForceSync() {
        return this.forceSync;
    }
    
    static {
        TXNLOG_MAGIC = ByteBuffer.wrap("ZKLG".getBytes()).getInt();
        LOG = LoggerFactory.getLogger(FileTxnLog.class);
        Long fsyncWarningThreshold;
        if ((fsyncWarningThreshold = Long.getLong("zookeeper.fsync.warningthresholdms")) == null) {
            fsyncWarningThreshold = Long.getLong("fsync.warningthresholdms", 1000L);
        }
        fsyncWarningThresholdMS = fsyncWarningThreshold;
    }
    
    static class PositionInputStream extends FilterInputStream
    {
        long position;
        
        protected PositionInputStream(final InputStream in) {
            super(in);
            this.position = 0L;
        }
        
        @Override
        public int read() throws IOException {
            final int rc = super.read();
            if (rc > -1) {
                ++this.position;
            }
            return rc;
        }
        
        @Override
        public int read(final byte[] b) throws IOException {
            final int rc = super.read(b);
            if (rc > 0) {
                this.position += rc;
            }
            return rc;
        }
        
        @Override
        public int read(final byte[] b, final int off, final int len) throws IOException {
            final int rc = super.read(b, off, len);
            if (rc > 0) {
                this.position += rc;
            }
            return rc;
        }
        
        @Override
        public long skip(final long n) throws IOException {
            final long rc = super.skip(n);
            if (rc > 0L) {
                this.position += rc;
            }
            return rc;
        }
        
        public long getPosition() {
            return this.position;
        }
        
        @Override
        public boolean markSupported() {
            return false;
        }
        
        @Override
        public void mark(final int readLimit) {
            throw new UnsupportedOperationException("mark");
        }
        
        @Override
        public void reset() {
            throw new UnsupportedOperationException("reset");
        }
    }
    
    public static class FileTxnIterator implements TxnIterator
    {
        File logDir;
        long zxid;
        TxnHeader hdr;
        Record record;
        File logFile;
        InputArchive ia;
        static final String CRC_ERROR = "CRC check failed";
        PositionInputStream inputStream;
        private ArrayList<File> storedFiles;
        
        public FileTxnIterator(final File logDir, final long zxid) throws IOException {
            this.inputStream = null;
            this.logDir = logDir;
            this.zxid = zxid;
            this.init();
        }
        
        void init() throws IOException {
            this.storedFiles = new ArrayList<File>();
            final List<File> files = Util.sortDataDir(FileTxnLog.getLogFiles(this.logDir.listFiles(), 0L), "log", false);
            for (final File f : files) {
                if (Util.getZxidFromName(f.getName(), "log") >= this.zxid) {
                    this.storedFiles.add(f);
                }
                else {
                    if (Util.getZxidFromName(f.getName(), "log") < this.zxid) {
                        this.storedFiles.add(f);
                        break;
                    }
                    continue;
                }
            }
            this.goToNextLog();
            if (!this.next()) {
                return;
            }
            while (this.hdr.getZxid() < this.zxid) {
                if (!this.next()) {
                    return;
                }
            }
        }
        
        private boolean goToNextLog() throws IOException {
            if (this.storedFiles.size() > 0) {
                this.logFile = this.storedFiles.remove(this.storedFiles.size() - 1);
                this.ia = this.createInputArchive(this.logFile);
                return true;
            }
            return false;
        }
        
        protected void inStreamCreated(final InputArchive ia, final InputStream is) throws IOException {
            final FileHeader header = new FileHeader();
            header.deserialize(ia, "fileheader");
            if (header.getMagic() != FileTxnLog.TXNLOG_MAGIC) {
                throw new IOException("Transaction log: " + this.logFile + " has invalid magic number " + header.getMagic() + " != " + FileTxnLog.TXNLOG_MAGIC);
            }
        }
        
        protected InputArchive createInputArchive(final File logFile) throws IOException {
            if (this.inputStream == null) {
                this.inputStream = new PositionInputStream(new BufferedInputStream(new FileInputStream(logFile)));
                FileTxnLog.LOG.debug("Created new input stream " + logFile);
                this.inStreamCreated(this.ia = BinaryInputArchive.getArchive(this.inputStream), this.inputStream);
                FileTxnLog.LOG.debug("Created new input archive " + logFile);
            }
            return this.ia;
        }
        
        protected Checksum makeChecksumAlgorithm() {
            return new Adler32();
        }
        
        @Override
        public boolean next() throws IOException {
            if (this.ia == null) {
                return false;
            }
            try {
                final long crcValue = this.ia.readLong("crcvalue");
                final byte[] bytes = Util.readTxnBytes(this.ia);
                if (bytes == null || bytes.length == 0) {
                    throw new EOFException("Failed to read " + this.logFile);
                }
                final Checksum crc = this.makeChecksumAlgorithm();
                crc.update(bytes, 0, bytes.length);
                if (crcValue != crc.getValue()) {
                    throw new IOException("CRC check failed");
                }
                this.hdr = new TxnHeader();
                this.record = SerializeUtils.deserializeTxn(bytes, this.hdr);
            }
            catch (EOFException e) {
                FileTxnLog.LOG.debug("EOF excepton " + e);
                this.inputStream.close();
                this.inputStream = null;
                this.ia = null;
                this.hdr = null;
                return this.goToNextLog() && this.next();
            }
            catch (IOException e2) {
                this.inputStream.close();
                throw e2;
            }
            return true;
        }
        
        @Override
        public TxnHeader getHeader() {
            return this.hdr;
        }
        
        @Override
        public Record getTxn() {
            return this.record;
        }
        
        @Override
        public void close() throws IOException {
            if (this.inputStream != null) {
                this.inputStream.close();
            }
        }
    }
}
