// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.server.persistence;

import java.nio.ByteBuffer;
import org.slf4j.LoggerFactory;
import org.apache.jute.BinaryOutputArchive;
import java.util.zip.CheckedOutputStream;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import org.apache.jute.OutputArchive;
import java.util.Iterator;
import java.util.ArrayList;
import org.apache.zookeeper.server.util.SerializeUtils;
import org.apache.jute.InputArchive;
import java.util.List;
import java.io.IOException;
import org.apache.jute.BinaryInputArchive;
import java.util.zip.Checksum;
import java.util.zip.CheckedInputStream;
import java.util.zip.Adler32;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.util.Map;
import org.apache.zookeeper.server.DataTree;
import org.slf4j.Logger;
import java.io.File;

public class FileSnap implements SnapShot
{
    File snapDir;
    private volatile boolean close;
    private static final int VERSION = 2;
    private static final long dbId = -1L;
    private static final Logger LOG;
    public static final int SNAP_MAGIC;
    public static final String SNAPSHOT_FILE_PREFIX = "snapshot";
    
    public FileSnap(final File snapDir) {
        this.close = false;
        this.snapDir = snapDir;
    }
    
    @Override
    public long deserialize(final DataTree dt, final Map<Long, Integer> sessions) throws IOException {
        final List<File> snapList = this.findNValidSnapshots(100);
        if (snapList.size() == 0) {
            return -1L;
        }
        File snap = null;
        boolean foundValid = false;
        for (int i = 0; i < snapList.size(); ++i) {
            snap = snapList.get(i);
            InputStream snapIS = null;
            CheckedInputStream crcIn = null;
            try {
                FileSnap.LOG.info("Reading snapshot " + snap);
                snapIS = new BufferedInputStream(new FileInputStream(snap));
                crcIn = new CheckedInputStream(snapIS, new Adler32());
                final InputArchive ia = BinaryInputArchive.getArchive(crcIn);
                this.deserialize(dt, sessions, ia);
                final long checkSum = crcIn.getChecksum().getValue();
                final long val = ia.readLong("val");
                if (val != checkSum) {
                    throw new IOException("CRC corruption in snapshot :  " + snap);
                }
                foundValid = true;
                break;
            }
            catch (IOException e) {
                FileSnap.LOG.warn("problem reading snap file " + snap, e);
            }
            finally {
                if (snapIS != null) {
                    snapIS.close();
                }
                if (crcIn != null) {
                    crcIn.close();
                }
            }
        }
        if (!foundValid) {
            throw new IOException("Not able to find valid snapshots in " + this.snapDir);
        }
        return dt.lastProcessedZxid = Util.getZxidFromName(snap.getName(), "snapshot");
    }
    
    public void deserialize(final DataTree dt, final Map<Long, Integer> sessions, final InputArchive ia) throws IOException {
        final FileHeader header = new FileHeader();
        header.deserialize(ia, "fileheader");
        if (header.getMagic() != FileSnap.SNAP_MAGIC) {
            throw new IOException("mismatching magic headers " + header.getMagic() + " !=  " + FileSnap.SNAP_MAGIC);
        }
        SerializeUtils.deserializeSnapshot(dt, ia, sessions);
    }
    
    @Override
    public File findMostRecentSnapshot() throws IOException {
        final List<File> files = this.findNValidSnapshots(1);
        if (files.size() == 0) {
            return null;
        }
        return files.get(0);
    }
    
    private List<File> findNValidSnapshots(final int n) throws IOException {
        final List<File> files = Util.sortDataDir(this.snapDir.listFiles(), "snapshot", false);
        int count = 0;
        final List<File> list = new ArrayList<File>();
        for (final File f : files) {
            try {
                if (!Util.isValidSnapshot(f)) {
                    continue;
                }
                list.add(f);
                if (++count == n) {
                    break;
                }
                continue;
            }
            catch (IOException e) {
                FileSnap.LOG.info("invalid snapshot " + f, e);
            }
        }
        return list;
    }
    
    public List<File> findNRecentSnapshots(final int n) throws IOException {
        final List<File> files = Util.sortDataDir(this.snapDir.listFiles(), "snapshot", false);
        int count = 0;
        final List<File> list = new ArrayList<File>();
        for (final File f : files) {
            if (count == n) {
                break;
            }
            if (Util.getZxidFromName(f.getName(), "snapshot") == -1L) {
                continue;
            }
            ++count;
            list.add(f);
        }
        return list;
    }
    
    protected void serialize(final DataTree dt, final Map<Long, Integer> sessions, final OutputArchive oa, final FileHeader header) throws IOException {
        if (header == null) {
            throw new IllegalStateException("Snapshot's not open for writing: uninitialized header");
        }
        header.serialize(oa, "fileheader");
        SerializeUtils.serializeSnapshot(dt, oa, sessions);
    }
    
    @Override
    public synchronized void serialize(final DataTree dt, final Map<Long, Integer> sessions, final File snapShot) throws IOException {
        if (!this.close) {
            final OutputStream sessOS = new BufferedOutputStream(new FileOutputStream(snapShot));
            final CheckedOutputStream crcOut = new CheckedOutputStream(sessOS, new Adler32());
            final OutputArchive oa = BinaryOutputArchive.getArchive(crcOut);
            final FileHeader header = new FileHeader(FileSnap.SNAP_MAGIC, 2, -1L);
            this.serialize(dt, sessions, oa, header);
            final long val = crcOut.getChecksum().getValue();
            oa.writeLong(val, "val");
            oa.writeString("/", "path");
            sessOS.flush();
            crcOut.close();
            sessOS.close();
        }
    }
    
    @Override
    public synchronized void close() throws IOException {
        this.close = true;
    }
    
    static {
        LOG = LoggerFactory.getLogger(FileSnap.class);
        SNAP_MAGIC = ByteBuffer.wrap("ZKSN".getBytes()).getInt();
    }
}
