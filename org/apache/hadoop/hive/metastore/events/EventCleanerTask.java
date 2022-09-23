// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.metastore.events;

import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hive.metastore.RawStore;
import org.apache.hadoop.hive.metastore.HiveMetaStore;
import org.apache.commons.logging.Log;
import java.util.TimerTask;

public class EventCleanerTask extends TimerTask
{
    public static final Log LOG;
    private final HiveMetaStore.HMSHandler handler;
    
    public EventCleanerTask(final HiveMetaStore.HMSHandler handler) {
        this.handler = handler;
    }
    
    @Override
    public void run() {
        try {
            final RawStore ms = this.handler.getMS();
            final long deleteCnt = ms.cleanupEvents();
            if (deleteCnt > 0L) {
                EventCleanerTask.LOG.info("Number of events deleted from event Table: " + deleteCnt);
            }
        }
        catch (Exception e) {
            EventCleanerTask.LOG.error(e);
        }
    }
    
    static {
        LOG = LogFactory.getLog(EventCleanerTask.class);
    }
}
