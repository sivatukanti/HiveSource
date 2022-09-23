// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.schshim;

import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.fair.QueuePlacementPolicy;
import org.apache.commons.lang.StringUtils;
import java.io.IOException;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.fair.AllocationFileLoaderService;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.fair.AllocationConfiguration;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.hadoop.conf.Configuration;
import org.apache.commons.logging.Log;
import org.apache.hadoop.hive.shims.SchedulerShim;

public class FairSchedulerShim implements SchedulerShim
{
    private static final Log LOG;
    private static final String MR2_JOB_QUEUE_PROPERTY = "mapreduce.job.queuename";
    
    @Override
    public void refreshDefaultQueue(final Configuration conf, final String userName) throws IOException {
        String requestedQueue = "default";
        final AtomicReference<AllocationConfiguration> allocConf = new AtomicReference<AllocationConfiguration>();
        final AllocationFileLoaderService allocsLoader = new AllocationFileLoaderService();
        allocsLoader.init(conf);
        allocsLoader.setReloadListener(new AllocationFileLoaderService.Listener() {
            @Override
            public void onReload(final AllocationConfiguration allocs) {
                allocConf.set(allocs);
            }
        });
        try {
            allocsLoader.reloadAllocations();
        }
        catch (Exception ex) {
            throw new IOException("Failed to load queue allocations", ex);
        }
        if (allocConf.get() == null) {
            allocConf.set(new AllocationConfiguration(conf));
        }
        final QueuePlacementPolicy queuePolicy = allocConf.get().getPlacementPolicy();
        if (queuePolicy != null) {
            requestedQueue = queuePolicy.assignAppToQueue(requestedQueue, userName);
            if (StringUtils.isNotBlank(requestedQueue)) {
                FairSchedulerShim.LOG.debug("Setting queue name to " + requestedQueue + " for user " + userName);
                conf.set("mapreduce.job.queuename", requestedQueue);
            }
        }
    }
    
    static {
        LOG = LogFactory.getLog(FairSchedulerShim.class);
    }
}
