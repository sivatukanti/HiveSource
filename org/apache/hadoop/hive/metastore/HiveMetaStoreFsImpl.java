// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.metastore;

import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hive.shims.HadoopShims;
import java.io.FileNotFoundException;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hadoop.hive.shims.ShimLoader;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.FileSystem;
import org.apache.commons.logging.Log;

public class HiveMetaStoreFsImpl implements MetaStoreFS
{
    public static final Log LOG;
    
    @Override
    public boolean deleteDir(final FileSystem fs, final Path f, final boolean recursive, final boolean ifPurge, final Configuration conf) throws MetaException {
        HiveMetaStoreFsImpl.LOG.info("deleting  " + f);
        final HadoopShims hadoopShim = ShimLoader.getHadoopShims();
        try {
            if (ifPurge) {
                HiveMetaStoreFsImpl.LOG.info("Not moving " + f + " to trash");
            }
            else if (hadoopShim.moveToAppropriateTrash(fs, f, conf)) {
                HiveMetaStoreFsImpl.LOG.info("Moved to trash: " + f);
                return true;
            }
            if (fs.delete(f, true)) {
                HiveMetaStoreFsImpl.LOG.info("Deleted the diretory " + f);
                return true;
            }
            if (fs.exists(f)) {
                throw new MetaException("Unable to delete directory: " + f);
            }
        }
        catch (FileNotFoundException e2) {
            return true;
        }
        catch (Exception e) {
            MetaStoreUtils.logAndThrowMetaException(e);
        }
        return false;
    }
    
    static {
        LOG = LogFactory.getLog("hive.metastore.hivemetastoressimpl");
    }
}
