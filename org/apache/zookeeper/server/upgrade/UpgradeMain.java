// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.server.upgrade;

import org.slf4j.LoggerFactory;
import org.apache.zookeeper.server.DataTree;
import org.apache.zookeeper.server.persistence.FileTxnSnapLog;
import java.io.IOException;
import org.slf4j.Logger;
import java.io.File;
import org.apache.yetus.audience.InterfaceAudience;

@InterfaceAudience.Public
public class UpgradeMain
{
    File snapShotDir;
    File dataDir;
    File bkupsnapShotDir;
    File bkupdataDir;
    File currentdataDir;
    File currentsnapShotDir;
    private static final Logger LOG;
    private static final String USAGE = "Usage: UpgradeMain dataDir snapShotDir";
    private static final int LASTVERSION = 1;
    private static final int CURRENTVERSION = 2;
    private static final String dirName = "version-";
    private static final String manual = "Please take manual steps to sanitize your database.\n Please read the upgrade manual";
    
    public UpgradeMain(final File dataDir, final File snapShotDir) {
        this.snapShotDir = snapShotDir;
        this.dataDir = dataDir;
        this.bkupdataDir = new File(dataDir, "version-1");
        this.bkupsnapShotDir = new File(snapShotDir, "version-1");
        this.currentsnapShotDir = new File(snapShotDir, "version-2");
        this.currentdataDir = new File(dataDir, "version-2");
    }
    
    private void createAllDirs() throws IOException {
        String error = "backup directory " + this.bkupdataDir + " already exists";
        UpgradeMain.LOG.info("Creating previous version data dir " + this.bkupdataDir);
        if (!this.bkupdataDir.mkdirs()) {
            UpgradeMain.LOG.error(error);
            UpgradeMain.LOG.error("Please take manual steps to sanitize your database.\n Please read the upgrade manual");
            throw new IOException(error);
        }
        UpgradeMain.LOG.info("Creating previous version snapshot dir " + this.bkupdataDir);
        if (!this.bkupsnapShotDir.mkdirs() && !this.bkupsnapShotDir.exists()) {
            UpgradeMain.LOG.error(error);
            UpgradeMain.LOG.error("Please take manual steps to sanitize your database.\n Please read the upgrade manual");
            throw new IOException(error);
        }
        error = "current directory " + this.currentdataDir + " already exists";
        UpgradeMain.LOG.info("Creating current data dir " + this.currentdataDir);
        if (!this.currentdataDir.mkdirs()) {
            UpgradeMain.LOG.error(error);
            UpgradeMain.LOG.error("Please take manual steps to sanitize your database.\n Please read the upgrade manual");
            throw new IOException(error);
        }
        UpgradeMain.LOG.info("Creating current snapshot dir " + this.currentdataDir);
        if (!this.currentsnapShotDir.mkdirs() && !this.currentsnapShotDir.exists()) {
            UpgradeMain.LOG.error(error);
            UpgradeMain.LOG.error("Please take manual steps to sanitize your database.\n Please read the upgrade manual");
            throw new IOException(error);
        }
    }
    
    void copyFiles(final File srcDir, final File dstDir, final String filter) throws IOException {
        final File[] list = srcDir.listFiles();
        if (list != null) {
            for (final File file : list) {
                final String name = file.getName();
                if (name.startsWith(filter)) {
                    final File dest = new File(dstDir, name);
                    UpgradeMain.LOG.info("Renaming " + file + " to " + dest);
                    if (!file.renameTo(dest)) {
                        throw new IOException("Unable to rename " + file + " to " + dest);
                    }
                }
            }
        }
    }
    
    public void runUpgrade() throws IOException {
        if (!this.dataDir.exists()) {
            throw new IOException(this.dataDir + " does not exist");
        }
        if (!this.snapShotDir.exists()) {
            throw new IOException(this.snapShotDir + " does not exist");
        }
        this.createAllDirs();
        try {
            this.copyFiles(this.dataDir, this.bkupdataDir, "log");
            this.copyFiles(this.snapShotDir, this.bkupsnapShotDir, "snapshot");
        }
        catch (IOException io) {
            UpgradeMain.LOG.error("Failed in backing up.");
            throw io;
        }
        final UpgradeSnapShotV1 upgrade = new UpgradeSnapShotV1(this.bkupdataDir, this.bkupsnapShotDir);
        UpgradeMain.LOG.info("Creating new data tree");
        final DataTree dt = upgrade.getNewDataTree();
        final FileTxnSnapLog filesnapLog = new FileTxnSnapLog(this.dataDir, this.snapShotDir);
        UpgradeMain.LOG.info("snapshotting the new datatree");
        filesnapLog.save(dt, upgrade.getSessionWithTimeOuts());
        UpgradeMain.LOG.info("Upgrade is complete");
    }
    
    public static void main(final String[] argv) {
        if (argv.length < 2) {
            UpgradeMain.LOG.error("Usage: UpgradeMain dataDir snapShotDir");
            System.exit(-1);
        }
        try {
            final UpgradeMain upgrade = new UpgradeMain(new File(argv[0]), new File(argv[1]));
            upgrade.runUpgrade();
        }
        catch (Throwable th) {
            UpgradeMain.LOG.error("Upgrade Error: Please read the docs for manual failure recovery ", th);
        }
    }
    
    static {
        LOG = LoggerFactory.getLogger(UpgradeMain.class);
    }
}
