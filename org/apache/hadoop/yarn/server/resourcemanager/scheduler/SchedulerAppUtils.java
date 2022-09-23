// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.scheduler;

import org.apache.commons.logging.Log;

public class SchedulerAppUtils
{
    public static boolean isBlacklisted(final SchedulerApplicationAttempt application, final SchedulerNode node, final Log LOG) {
        if (application.isBlacklisted(node.getNodeName())) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Skipping 'host' " + node.getNodeName() + " for " + application.getApplicationId() + " since it has been blacklisted");
            }
            return true;
        }
        if (application.isBlacklisted(node.getRackName())) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Skipping 'rack' " + node.getRackName() + " for " + application.getApplicationId() + " since it has been blacklisted");
            }
            return true;
        }
        return false;
    }
}
