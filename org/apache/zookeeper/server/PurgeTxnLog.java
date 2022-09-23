// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.server;

import org.slf4j.LoggerFactory;
import java.util.Iterator;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Arrays;
import java.util.HashSet;
import org.apache.zookeeper.server.persistence.Util;
import java.util.Set;
import java.io.FileFilter;
import java.io.IOException;
import java.util.List;
import org.apache.zookeeper.server.persistence.FileTxnSnapLog;
import java.io.File;
import org.slf4j.Logger;
import org.apache.yetus.audience.InterfaceAudience;

@InterfaceAudience.Public
public class PurgeTxnLog
{
    private static final Logger LOG;
    private static final String COUNT_ERR_MSG = "count should be greater than or equal to 3";
    private static final String PREFIX_SNAPSHOT = "snapshot";
    private static final String PREFIX_LOG = "log";
    
    static void printUsage() {
        System.out.println("Usage:");
        System.out.println("PurgeTxnLog dataLogDir [snapDir] -n count");
        System.out.println("\tdataLogDir -- path to the txn log directory");
        System.out.println("\tsnapDir -- path to the snapshot directory");
        System.out.println("\tcount -- the number of old snaps/logs you want to keep, value should be greater than or equal to 3");
    }
    
    public static void purge(final File dataDir, final File snapDir, final int num) throws IOException {
        if (num < 3) {
            throw new IllegalArgumentException("count should be greater than or equal to 3");
        }
        final FileTxnSnapLog txnLog = new FileTxnSnapLog(dataDir, snapDir);
        final List<File> snaps = txnLog.findNRecentSnapshots(num);
        final int numSnaps = snaps.size();
        if (numSnaps > 0) {
            purgeOlderSnapshots(txnLog, snaps.get(numSnaps - 1));
        }
    }
    
    static void purgeOlderSnapshots(final FileTxnSnapLog txnLog, final File snapShot) {
        final long leastZxidToBeRetain = Util.getZxidFromName(snapShot.getName(), "snapshot");
        final Set<File> retainedTxnLogs = new HashSet<File>();
        retainedTxnLogs.addAll(Arrays.asList(txnLog.getSnapshotLogs(leastZxidToBeRetain)));
        final List<File> files = new ArrayList<File>();
        class MyFileFilter implements FileFilter
        {
            private final String prefix = "log";
            final /* synthetic */ long val$leastZxidToBeRetain;
            
            MyFileFilter(final String prefix, final String val$leastZxidToBeRetain) {
                this.val$leastZxidToBeRetain = (long)val$leastZxidToBeRetain;
            }
            
            @Override
            public boolean accept(final File f) {
                if (!f.getName().startsWith(this.prefix + ".")) {
                    return false;
                }
                if (retainedTxnLogs.contains(f)) {
                    return false;
                }
                final long fZxid = Util.getZxidFromName(f.getName(), this.prefix);
                return fZxid < this.val$leastZxidToBeRetain;
            }
        }
        File[] fileArray = txnLog.getDataDir().listFiles(new MyFileFilter(leastZxidToBeRetain));
        if (fileArray != null) {
            files.addAll(Arrays.asList(fileArray));
        }
        fileArray = txnLog.getSnapDir().listFiles(new MyFileFilter(leastZxidToBeRetain));
        if (fileArray != null) {
            files.addAll(Arrays.asList(fileArray));
        }
        for (final File f : files) {
            final String msg = "Removing file: " + DateFormat.getDateTimeInstance().format(f.lastModified()) + "\t" + f.getPath();
            PurgeTxnLog.LOG.info(msg);
            System.out.println(msg);
            if (!f.delete()) {
                System.err.println("Failed to remove " + f.getPath());
            }
        }
    }
    
    public static void main(final String[] args) throws IOException {
        if (args.length < 3 || args.length > 4) {
            printUsageThenExit();
        }
        File snapDir;
        final File dataDir = snapDir = validateAndGetFile(args[0]);
        int num = -1;
        String countOption = "";
        if (args.length == 3) {
            countOption = args[1];
            num = validateAndGetCount(args[2]);
        }
        else {
            snapDir = validateAndGetFile(args[1]);
            countOption = args[2];
            num = validateAndGetCount(args[3]);
        }
        if (!"-n".equals(countOption)) {
            printUsageThenExit();
        }
        purge(dataDir, snapDir, num);
    }
    
    private static File validateAndGetFile(final String path) {
        final File file = new File(path);
        if (!file.exists()) {
            System.err.println("Path '" + file.getAbsolutePath() + "' does not exist. ");
            printUsageThenExit();
        }
        return file;
    }
    
    private static int validateAndGetCount(final String number) {
        int result = 0;
        try {
            result = Integer.parseInt(number);
            if (result < 3) {
                System.err.println("count should be greater than or equal to 3");
                printUsageThenExit();
            }
        }
        catch (NumberFormatException e) {
            System.err.println("'" + number + "' can not be parsed to integer.");
            printUsageThenExit();
        }
        return result;
    }
    
    private static void printUsageThenExit() {
        printUsage();
        System.exit(1);
    }
    
    static {
        LOG = LoggerFactory.getLogger(PurgeTxnLog.class);
    }
}
