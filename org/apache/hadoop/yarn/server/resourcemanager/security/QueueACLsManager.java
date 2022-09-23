// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.security;

import org.apache.hadoop.yarn.api.records.QueueACL;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.ResourceScheduler;

public class QueueACLsManager
{
    private ResourceScheduler scheduler;
    private boolean isACLsEnable;
    
    public QueueACLsManager(final ResourceScheduler scheduler, final Configuration conf) {
        this.scheduler = scheduler;
        this.isACLsEnable = conf.getBoolean("yarn.acl.enable", false);
    }
    
    public boolean checkAccess(final UserGroupInformation callerUGI, final QueueACL acl, final String queueName) {
        return !this.isACLsEnable || this.scheduler.checkAccess(callerUGI, acl, queueName);
    }
}
