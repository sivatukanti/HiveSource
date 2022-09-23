// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.common;

import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.commons.logging.Log;

public class ServerUtils
{
    public static final Log LOG;
    
    public static void cleanUpScratchDir(final HiveConf hiveConf) {
        if (hiveConf.getBoolVar(HiveConf.ConfVars.HIVE_START_CLEANUP_SCRATCHDIR)) {
            final String hiveScratchDir = hiveConf.get(HiveConf.ConfVars.SCRATCHDIR.varname);
            try {
                final Path jobScratchDir = new Path(hiveScratchDir);
                ServerUtils.LOG.info("Cleaning scratchDir : " + hiveScratchDir);
                final FileSystem fileSystem = jobScratchDir.getFileSystem(hiveConf);
                fileSystem.delete(jobScratchDir, true);
            }
            catch (Throwable e) {
                ServerUtils.LOG.warn("Unable to delete scratchDir : " + hiveScratchDir, e);
            }
        }
    }
    
    static {
        LOG = LogFactory.getLog(ServerUtils.class);
    }
}
