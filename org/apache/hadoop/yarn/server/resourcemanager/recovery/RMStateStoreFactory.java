// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.recovery;

import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.util.ReflectionUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.commons.logging.Log;

public class RMStateStoreFactory
{
    private static final Log LOG;
    
    public static RMStateStore getStore(final Configuration conf) {
        final Class<? extends RMStateStore> storeClass = conf.getClass("yarn.resourcemanager.store.class", MemoryRMStateStore.class, RMStateStore.class);
        RMStateStoreFactory.LOG.info("Using RMStateStore implementation - " + storeClass);
        return ReflectionUtils.newInstance(storeClass, conf);
    }
    
    static {
        LOG = LogFactory.getLog(RMStateStoreFactory.class);
    }
}
