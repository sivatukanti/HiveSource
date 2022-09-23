// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.server.persistence;

import java.io.Serializable;
import org.slf4j.LoggerFactory;
import java.util.Comparator;
import java.util.Collections;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import org.apache.jute.OutputArchive;
import java.io.OutputStream;
import org.apache.jute.BinaryOutputArchive;
import java.io.ByteArrayOutputStream;
import org.apache.jute.Record;
import org.apache.zookeeper.txn.TxnHeader;
import java.io.EOFException;
import org.apache.jute.InputArchive;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.io.RandomAccessFile;
import java.util.Properties;
import java.net.URI;
import java.io.File;
import org.slf4j.Logger;

public class Util
{
    private static final Logger LOG;
    private static final String SNAP_DIR = "snapDir";
    private static final String LOG_DIR = "logDir";
    private static final String DB_FORMAT_CONV = "dbFormatConversion";
    
    public static String makeURIString(final String dataDir, final String dataLogDir, final String convPolicy) {
        String uri = "file:snapDir=" + dataDir + ";" + "logDir" + "=" + dataLogDir;
        if (convPolicy != null) {
            uri = uri + ";dbFormatConversion=" + convPolicy;
        }
        return uri.replace('\\', '/');
    }
    
    public static URI makeFileLoggerURL(final File dataDir, final File dataLogDir) {
        return URI.create(makeURIString(dataDir.getPath(), dataLogDir.getPath(), null));
    }
    
    public static URI makeFileLoggerURL(final File dataDir, final File dataLogDir, final String convPolicy) {
        return URI.create(makeURIString(dataDir.getPath(), dataLogDir.getPath(), convPolicy));
    }
    
    public static String makeLogName(final long zxid) {
        return "log." + Long.toHexString(zxid);
    }
    
    public static String makeSnapshotName(final long zxid) {
        return "snapshot." + Long.toHexString(zxid);
    }
    
    public static File getSnapDir(final Properties props) {
        return new File(props.getProperty("snapDir"));
    }
    
    public static File getLogDir(final Properties props) {
        return new File(props.getProperty("logDir"));
    }
    
    public static String getFormatConversionPolicy(final Properties props) {
        return props.getProperty("dbFormatConversion");
    }
    
    public static long getZxidFromName(final String name, final String prefix) {
        long zxid = -1L;
        final String[] nameParts = name.split("\\.");
        if (nameParts.length == 2 && nameParts[0].equals(prefix)) {
            try {
                zxid = Long.parseLong(nameParts[1], 16);
            }
            catch (NumberFormatException ex) {}
        }
        return zxid;
    }
    
    public static boolean isValidSnapshot(final File f) throws IOException {
        if (f == null || getZxidFromName(f.getName(), "snapshot") == -1L) {
            return false;
        }
        final RandomAccessFile raf = new RandomAccessFile(f, "r");
        try {
            if (raf.length() < 10L) {
                return false;
            }
            raf.seek(raf.length() - 5L);
            byte[] bytes;
            int readlen;
            int l;
            for (bytes = new byte[5], readlen = 0; readlen < 5 && (l = raf.read(bytes, readlen, bytes.length - readlen)) >= 0; readlen += l) {}
            if (readlen != bytes.length) {
                Util.LOG.info("Invalid snapshot " + f + " too short, len = " + readlen);
                return false;
            }
            final ByteBuffer bb = ByteBuffer.wrap(bytes);
            final int len = bb.getInt();
            final byte b = bb.get();
            if (len != 1 || b != 47) {
                Util.LOG.info("Invalid snapshot " + f + " len = " + len + " byte = " + (b & 0xFF));
                return false;
            }
        }
        finally {
            raf.close();
        }
        return true;
    }
    
    public static byte[] readTxnBytes(final InputArchive ia) throws IOException {
        try {
            final byte[] bytes = ia.readBuffer("txtEntry");
            if (bytes.length == 0) {
                return bytes;
            }
            if (ia.readByte("EOF") != 66) {
                Util.LOG.error("Last transaction was partial.");
                return null;
            }
            return bytes;
        }
        catch (EOFException ex) {
            return null;
        }
    }
    
    public static byte[] marshallTxnEntry(final TxnHeader hdr, final Record txn) throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final OutputArchive boa = BinaryOutputArchive.getArchive(baos);
        hdr.serialize(boa, "hdr");
        if (txn != null) {
            txn.serialize(boa, "txn");
        }
        return baos.toByteArray();
    }
    
    public static void writeTxnBytes(final OutputArchive oa, final byte[] bytes) throws IOException {
        oa.writeBuffer(bytes, "txnEntry");
        oa.writeByte((byte)66, "EOR");
    }
    
    public static List<File> sortDataDir(final File[] files, final String prefix, final boolean ascending) {
        if (files == null) {
            return new ArrayList<File>(0);
        }
        final List<File> filelist = Arrays.asList(files);
        Collections.sort(filelist, new DataDirFileComparator(prefix, ascending));
        return filelist;
    }
    
    public static boolean isLogFileName(final String fileName) {
        return fileName.startsWith("log.");
    }
    
    public static boolean isSnapshotFileName(final String fileName) {
        return fileName.startsWith("snapshot.");
    }
    
    static {
        LOG = LoggerFactory.getLogger(Util.class);
    }
    
    private static class DataDirFileComparator implements Comparator<File>, Serializable
    {
        private static final long serialVersionUID = -2648639884525140318L;
        private String prefix;
        private boolean ascending;
        
        public DataDirFileComparator(final String prefix, final boolean ascending) {
            this.prefix = prefix;
            this.ascending = ascending;
        }
        
        @Override
        public int compare(final File o1, final File o2) {
            final long z1 = Util.getZxidFromName(o1.getName(), this.prefix);
            final long z2 = Util.getZxidFromName(o2.getName(), this.prefix);
            final int result = (z1 < z2) ? -1 : ((z1 > z2) ? 1 : 0);
            return this.ascending ? result : (-result);
        }
    }
}
